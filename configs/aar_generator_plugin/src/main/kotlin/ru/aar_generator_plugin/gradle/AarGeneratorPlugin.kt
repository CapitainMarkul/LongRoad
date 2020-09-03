package ru.aar_generator_plugin.gradle

import com.android.build.gradle.internal.tasks.factory.registerTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import ru.aar_generator_plugin.gradle.PluginConfigurator.AarGeneratorPluginConfig
import ru.aar_generator_plugin.gradle.log.PluginLogger
import ru.aar_generator_plugin.gradle.sub_tasks.AarDependencyTask
import ru.aar_generator_plugin.gradle.sub_tasks.AarMainTask
import ru.aar_generator_plugin.gradle.sub_tasks.base.BUNDLE_DEBUG_AAR_FOR_TEST
import ru.aar_generator_plugin.gradle.sub_tasks.base.CLEAN_PROJECT

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

        project.subprojects { foreachItem ->
            foreachItem.afterEvaluate {
                val isAndroidProject =
                    (it.pluginManager.hasPlugin("com.android.application") ||
                            it.pluginManager.hasPlugin("com.android.library"))

                if (isAndroidProject) {
                    //TODO()
                }
            }
        }


        project.afterEvaluate {
//            logAarPlugin("AfterEvaluate : ${extension.packageNamePlugin}")
//
//            project.allprojects {
//                val mavenPlugin = MavenPublishPlugin::class.java
//                logAarPlugin("Run for $it")
//
//                logAarPlugin("Apply ${mavenPlugin.canonicalName} plugin for $it")
//                it.pluginManager.apply(mavenPlugin)
//                logAarPlugin("Plugin ${mavenPlugin.canonicalName} applied for $it || ${it.project.plugins.hasPlugin(mavenPlugin)}")
//            }
//
//            logAarPlugin("Finished $project")
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
                    if (evaluateSubProject.pluginManager.hasPlugin("com.android.library")) {
                        logSimple("Configure $evaluateSubProject")

                        with(evaluateSubProject.tasks) {
                            /* Register Custom Task'и */
                            registerTask(AarMainTask.taskCreator(extension))
                            registerTask(AarDependencyTask.taskCreator())
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