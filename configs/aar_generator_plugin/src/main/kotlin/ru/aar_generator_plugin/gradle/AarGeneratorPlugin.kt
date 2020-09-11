package ru.aar_generator_plugin.gradle

import com.android.build.gradle.internal.tasks.factory.registerTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import ru.aar_generator_plugin.gradle.PluginConfigurator.AarGeneratorPluginConfig
import ru.aar_generator_plugin.gradle.log.PluginLogger
import ru.aar_generator_plugin.gradle.task.AarMainTask
import ru.aar_generator_plugin.gradle.task.sub.AarDependencyTask
import ru.aar_generator_plugin.gradle.task.sub.AarPublishTask
import ru.aar_generator_plugin.gradle.task.base.PLUGIN_ANDROID_LIBRARY
import ru.aar_generator_plugin.gradle.util.hasPlugin

class AarGeneratorPlugin : Plugin<Project>, PluginLogger {
    override val tag: String
        get() = AAR_GENERATOR_PLUGIN_TAG

    override fun apply(project: Project) {
        logStartRegion("Starting apply for $project")

        // Читаем конфигурацию, которую настроили в root.build.gradle
        val extension = project.extensions.create<PluginConfigurator>(
            AAR_GENERATOR_PLUGIN_NAME, PluginConfigurator::class.java
        )

        project.afterEvaluate {
            // Первоначальная конфигурация плагина
            setupConfiguration(it, extension.getCurrentConfiguration())

            // Регистрируем созданные Task'и. Регистрация происходит в момент Sync'а проекта
            taskConfigureAndRegister(it, extension.getCurrentConfiguration())
        }

        logEndRegion("End apply for $project")
    }


    private fun setupConfiguration(project: Project, extension: AarGeneratorPluginConfig) {
        logSimple("Selected config: ${extension.targetPlatform}")
    }

    private fun taskConfigureAndRegister(project: Project, extension: AarGeneratorPluginConfig) {
//        project.showAllSubProjects()
//        project.showAllTasks(false)

        project.afterEvaluate { rootProject ->
            rootProject.subprojects { subProject ->
                subProject.afterEvaluate { evaluateSubProject ->
                    if (evaluateSubProject.hasPlugin(PLUGIN_ANDROID_LIBRARY)) {
                        logSimple("Configure $evaluateSubProject")

                        /* Register Custom Task'и */
                        with(evaluateSubProject.tasks) {
                            registerTask(AarMainTask.taskCreator(extension))
                            registerTask(AarDependencyTask.taskCreator())
                            registerTask(AarPublishTask.taskCreator(evaluateSubProject, extension))
                        }
                    }
                }
            }
        }
    }

    companion object {
        private const val AAR_GENERATOR_PLUGIN_TAG = "AAR_GENERATOR_PLUGIN"
        private const val AAR_GENERATOR_PLUGIN_NAME = "aarGeneratorPlugin"

        const val AAR_GENERATOR_PLUGIN_TASK_GROUP = "aar generator task group"
    }
}