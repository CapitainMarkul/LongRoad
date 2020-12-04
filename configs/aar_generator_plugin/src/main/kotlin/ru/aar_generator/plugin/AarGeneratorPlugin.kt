package ru.aar_generator.plugin

import com.android.build.gradle.internal.tasks.factory.registerTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.publish.maven.plugins.MavenPublishPlugin
import org.gradle.util.VersionNumber
import ru.aar_generator.logger.PluginLogger
import ru.aar_generator.plugin.config.PluginConfigurator
import ru.aar_generator.plugin.publish.PublishConfigurator
import ru.aar_generator.plugin.task.AarPublishTask
import ru.aar_generator.plugin.task.AarScriptTask
import ru.aar_generator.plugin.utils.hasPlugin

class AarGeneratorPlugin : Plugin<Project>, PluginLogger {

    //TODO: 0. Добавить в конфигурацию возможность передать список модулей, для игнорирования
    //TODO: 1. Скрипты для изменения build.gradle Linux/Windows
    //https://stackoverflow.com/questions/25562207/execute-shell-script-in-gradle
    //TODO: 2. MavenPublishPom -> заполнение информации из gradle.properties
    //PublishConfigurator.configureFinalPom -> можно начать заполнять
    //TODO: 3. .aar version - вынести в option, чтобы версия задавалась в конфигурации
    //TODO: 4. .aar для разных приложений в разные папки

    companion object {
        private const val AAR_GENERATOR_PLUGIN_LOG_TAG = "AAR_LOG"
        private const val AAR_GENERATOR_PLUGIN_NAME = "aarGeneratorPlugin"
        private const val AAR_GENERATOR_PLUGIN_CONFIG_NAME = "aarGeneratorPluginConfig"


        private const val MAVEN_AAR_GROUP = "ru.aar_generator"
        private const val MAVEN_AAR_VERSION = "0.0.1"

        private val SUPPORT_GRADLE_VERSION = VersionNumber(6, 6, 0, null)

        const val AAR_GENERATOR_PLUGIN_TASK_GROUP = "aar generator tasks"

        /* Gradle Plugin - android library */
        private const val PLUGIN_ANDROID_LIBRARY = "com.android.library"
    }

    private val pluginClassName: String = AarGeneratorPlugin::class.java.simpleName

    override val logTag: String
        get() = AAR_GENERATOR_PLUGIN_LOG_TAG

    override fun apply(project: Project) {
        logStartRegion("Starting apply $pluginClassName for $project")

        // 0. Проверка версии Gradle
        val gradleVersion = VersionNumber.parse(project.gradle.gradleVersion)
        if (gradleVersion < SUPPORT_GRADLE_VERSION)
            throw IllegalArgumentException(
                "Must be gradle version ${gradleVersion.major}.${gradleVersion.minor}.${gradleVersion.micro} or higher!"
            )

        // 1. Создаем конфигурацию, которую объявили в root.build.gradle
        val pluginConfigurator = project.extensions.create<PluginConfigurator>(
            AAR_GENERATOR_PLUGIN_CONFIG_NAME, PluginConfigurator::class.java
        )

        // 2. Устанавливаем конфигурацию для подпроектов (конфигурацию забираем из rootProject'a)
        project.extensions.configure(PluginConfigurator::class.java) {
            val extensionInner =
                project.rootProject.extensions.getByType(PluginConfigurator::class.java)
            pluginConfigurator.setCurrentConfiguration(extensionInner.getCurrentConfiguration())
        }

        // 3. Логирование конфигурации для проекта
        project.showProjectConfiguration(pluginConfigurator.getCurrentConfiguration())

        // 4. Логирование списка подпроектов для текущего проекта
        project.showAllSubProjects()

        // 5. Применение AarGeneratorPlugin для подпроектов текущего проекта
        project.subprojects.forEach { subProject ->
            subProject.afterEvaluate { afterEvaluateProject ->
                logSimple(
                    "SUBPROJECT: ${afterEvaluateProject.name} hasPlugin ${
                        afterEvaluateProject.hasPlugin(
                            PLUGIN_ANDROID_LIBRARY
                        )
                    }"
                )
                if (subProject.hasPlugin(PLUGIN_ANDROID_LIBRARY)) {
                    afterEvaluateProject.plugins.apply(AarGeneratorPlugin::class.java)
                }
            }
        }

        // 6. Применение MavenPublishPlugin для текущего проекта
        logSimple("Apply MavenPublishPlugin for ${project.name}")
        project.plugins.apply(MavenPublishPlugin::class.java)

        project.afterEvaluate { projectAfterEvaluate ->
            /* Доступ к переменным конфигурации можно получить только после конфигурирования проекта */

            // 7. Формирование базовых параметров проекта, на основе которых будет создан итоговый .aar
            val rootProjectName = project.rootProject.name
            val variantOptionName =
                pluginConfigurator.getCurrentConfiguration().targetPlatform.platformName
            val milestonesVersion = pluginConfigurator.getCurrentConfiguration()
                .milestonesVersion.replace('.', '_')

            project.group =
                if (rootProjectName.isEmpty()) "$MAVEN_AAR_GROUP.${milestonesVersion}_$variantOptionName"
                else "$MAVEN_AAR_GROUP.${rootProjectName}_${milestonesVersion}_$variantOptionName."
            project.version = MAVEN_AAR_VERSION

            // 8. После конфигурирования проекта, запускаем конфигурирование публикации
            with(PublishConfigurator(project)) {
                configureTarget(PublishConfigurator.getDefaultLocalMavenPublishTarget(project))

                if (projectAfterEvaluate.hasPlugin(PLUGIN_ANDROID_LIBRARY)) {
                    configureAndroidArtifacts(pluginConfigurator.getCurrentConfiguration().targetPlatform)
                }
            }

            val isRootProject = project.parent == null
            val needAutoRunScriptTask =
                pluginConfigurator.getCurrentConfiguration().needRunReplaceProjectToAarScript

            // 9. Регистрация AarPublishTask'и
            project.tasks.registerTask(
                AarPublishTask.taskCreator(
                    isRootProject,
                    needAutoRunScriptTask
                )
            )

            // 10. Регистрация AarScriptTask'и (пока только для Root gradle файла)
            if (isRootProject) {
                logSimple("Register ${AarScriptTask.TASK_NAME} for ${project.name} project")
                logSimple("with params: $variantOptionName | $milestonesVersion")
                val taskParams = AarScriptTask.Companion.ScriptParams(
                    variantOptionName, milestonesVersion
                )

                project.tasks.registerTask(AarScriptTask.taskCreator(project, taskParams))
            }
        }

        logEndRegion("End apply $pluginClassName for $project")
    }
}