package ru.aar_generator.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.publish.maven.plugins.MavenPublishPlugin
import org.gradle.util.VersionNumber
import ru.aar_generator.logger.PluginLogger
import ru.aar_generator.plugin.config.PluginConfigurator
import ru.aar_generator.plugin.publish.PublishConfigurator
import ru.aar_generator.plugin.utils.hasPlugin

class AarGeneratorPlugin : Plugin<Project>, PluginLogger {

    //TODO: 0. Добавить в конфигурацию возможность передать список модулей, для игнорирования
    //TODO: 1. Скрипты для изменения build.gradle Linux/Windows
    //https://stackoverflow.com/questions/25562207/execute-shell-script-in-gradle
    //TODO: 2. MavenPublishPom -> заполнение информации из gradle.properties
    //PublishConfigurator.configureFinalPom -> можно начать заполнять
    //TODO: 3. .aar version - вынести в option, чтобы версия задавалась в конфигурации

    companion object {
        private const val AAR_GENERATOR_PLUGIN_LOG_TAG = "AAR_LOG"
        private const val AAR_GENERATOR_PLUGIN_NAME = "aarGeneratorPlugin"
        private const val AAR_GENERATOR_PLUGIN_CONFIG_NAME = "aarGeneratorPluginConfig"


        private const val MAVEN_AAR_GROUP = "ru.aar_generator"
        private const val MAVEN_AAR_VERSION = "0.0.1"

        private val SUPPORT_GRADLE_VERSION = VersionNumber(6, 6, 0, null)

        const val AAR_GENERATOR_PLUGIN_TASK_GROUP = "aar generator task group"

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
            throw IllegalArgumentException("Must be gradle version 6.6.0 or higher!")

        // 1. Читаем конфигурацию, которую настроили в root.build.gradle
        val currentConfig = project.extensions.create<PluginConfigurator>(
            AAR_GENERATOR_PLUGIN_CONFIG_NAME, PluginConfigurator::class.java
        )

        // 2. Устанавливаем конфигурацию для подпроектов
        project.extensions.configure(PluginConfigurator::class.java) {
            val extensionInner =
                project.rootProject.extensions.getByType(PluginConfigurator::class.java)
            currentConfig.setCurrentConfiguration(extensionInner.getCurrentConfiguration())
        }

        // 3. Логирование конфигурации для проекта
        project.showProjectConfiguration(currentConfig.getCurrentConfiguration())

        // 4. Логирование списка подпроектов для текущего проекта
        project.showAllSubProjects()

        // 5. Применение AarGeneratorPlugin для подпроектов текущего проекта
        project.subprojects.forEach { subProject ->
            //FIXME: Вот это нужно поправить, сделать проверку на hasPlugin ("com.android.library")
            if (subProject.name != "app" && subProject.name != "LongRoad") {
                subProject.afterEvaluate { afterEvaluateProject ->
                    afterEvaluateProject.plugins.apply(AarGeneratorPlugin::class.java)
                }
            }
        }

        // 6. Применение MavenPublishPlugin для текущего проекта
        logSimple("Apply MavenPublishPlugin for ${project.name}")
        project.plugins.apply(MavenPublishPlugin::class.java)

        // 7. Формирование базовых параметров проекта, на основе которых будет создан итоговый .aar
        project.group = MAVEN_AAR_GROUP
        project.version = MAVEN_AAR_VERSION

//        val pom = MavenPublishPom.fromProject(project)
//        configureJavadocTask(project)

        // 8. После конфигурирования проекта, запускаем конфигурирование публикации
        project.afterEvaluate { projectAfterEvaluate ->
            with(PublishConfigurator(project)) {
                configureTarget(
                    PublishConfigurator.getDefaultLocalMavenPublishTarget(project)
                )

                if (projectAfterEvaluate.hasPlugin(PLUGIN_ANDROID_LIBRARY)) {
                    logSimple("SIZE: ${project.components.size}")

                    project.components.forEach {
                        logSimple("${it.name}")
                    }

                    configureAndroidArtifacts()
                }
            }
        }

        logEndRegion("End apply $pluginClassName for $project")
    }

/*    private fun setupConfiguration(project: Project, extension: AarGeneratorPluginConfig) {
        logSimple("Selected config: ${extension.targetPlatform}")
    }*/

    /*    private fun taskConfigureAndRegister(project: Project, extension: AarGeneratorPluginConfig) {
//        project.showAllSubProjects()
//        project.showAllTasks(false)

        project.afterEvaluate { rootProject ->
            rootProject.subprojects { subProject ->
                subProject.afterEvaluate { evaluateSubProject ->
                    evaluateSubProject.plugins.apply(PLUGIN_MAVEN_PUBLISH)

                    if (evaluateSubProject.hasPlugin(PLUGIN_ANDROID_LIBRARY)) {
                        logSimple("Configure $evaluateSubProject")

                         Register Custom Task'и
                        with(evaluateSubProject.tasks) {
                            registerTask(AarMainTask.taskCreator(extension))
//                            registerTask(AarDependencyTask.taskCreator())
                            registerTask(AarPublishTask.taskCreator(evaluateSubProject, extension))

                            extension.targetPlatform?.platformName?.let {

                                val clearTask = getByName(TASK_CLEAN_PROJECT)
                                    .doFirst { logSimple("clearTaskDoFirst") }
                                    .doLast { logSimple("clearTaskDoLast") }


                                val buildAarTask =
                                    getByName(TASK_BUNDLE_DEBUG_AAR_FOR_TEST.format(it))
                                        .dependsOn(clearTask)
                                        .mustRunAfter(clearTask)


//                                val buildAarTask =
//                                    getByName(AarDependencyTask.TASK_NAME)
////                                        .dependsOn(clearTask)
//                                        .mustRunAfter(clearTask)
//                                        .doFirst { logSimple("buildAarTaskDoFirst") }
//                                        .doLast { logSimple("buildAarTaskDoLast") }

                                val publishTask = getByName(AarPublishTask.TASK_NAME)
                                    .mustRunAfter(TASK_BUNDLE_DEBUG_AAR_FOR_TEST.format(it))
                                    .doFirst { logSimple("publishTaskDoFirst") }
                                    .doLast { logSimple("publishTaskDoLast") }

                                val publishToMavenTask = getByName(MavenPublishPlugin.PUBLISH_LOCAL_LIFECYCLE_TASK_NAME)
                                    .mustRunAfter(publishTask)
                                    .doFirst { logSimple("publishToMavenTaskDoFirst") }
                                    .doLast { logSimple("publishToMavenTaskDoLast") }

                                val mainTask = getByPath(AarMainTask.TASK_NAME)
                                    .doFirst { logSimple("mainTaskDoFirst") }
                                    .doLast { logSimple("mainTaskDoLast") }

                                mainTask
                                    .dependsOn(clearTask)
                                    .dependsOn(TASK_BUNDLE_DEBUG_AAR_FOR_TEST.format(it))
                                    .mustRunAfter(clearTask)
//                                    .dependsOn(buildAarTask)
                                    .dependsOn(publishTask)
                                    .dependsOn(publishToMavenTask)

                            }
                        }
                    }
                }
            }
        }
    }*/
}