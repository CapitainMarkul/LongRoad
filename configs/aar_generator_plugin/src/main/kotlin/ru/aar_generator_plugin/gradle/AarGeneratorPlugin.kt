package ru.aar_generator_plugin.gradle

import com.android.build.gradle.internal.tasks.factory.registerTask
import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.publish.maven.plugins.MavenPublishPlugin
import org.gradle.api.tasks.javadoc.Javadoc
import org.gradle.external.javadoc.StandardJavadocDocletOptions
import ru.aar_generator_plugin.gradle.PluginConfigurator.AarGeneratorPluginConfig
import ru.aar_generator_plugin.gradle.log.PluginLogger
import ru.aar_generator_plugin.gradle.task.AarMainTask
import ru.aar_generator_plugin.gradle.task.base.PLUGIN_ANDROID_LIBRARY
import ru.aar_generator_plugin.gradle.task.base.PLUGIN_MAVEN_PUBLISH
import ru.aar_generator_plugin.gradle.task.base.TASK_BUNDLE_DEBUG_AAR_FOR_TEST
import ru.aar_generator_plugin.gradle.task.base.TASK_CLEAN_PROJECT
import ru.aar_generator_plugin.gradle.task.sub.AarDependencyTask
import ru.aar_generator_plugin.gradle.task.sub.AarPublishTask
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

//        project.afterEvaluate {
            // Первоначальная конфигурация плагина
//            setupConfiguration(it, extension.getCurrentConfiguration())

            project.plugins.apply(MavenPublishPlugin::class.java)

            project.group = "LongRoadGroup"
            project.version = "0.0.1"

//        configureSigning(project)
        configureJavadoc(project)
//        configureDokka(project)

        project.afterEvaluate { project ->
            val configurer = MavenPublishConfigurer(p, pom)

            extension.targets.all {
                checkNotNull(it.releaseRepositoryUrl) {
                    "releaseRepositoryUrl of ${it.name} is required to be set"
                }
                configurer.configureTarget(it)
            }

            configurePublishing(project, configurer)
        }

            // Регистрируем созданные Task'и. Регистрация происходит в момент Sync'а проекта
//            taskConfigureAndRegister(it, extension.getCurrentConfiguration())
//        }

        logEndRegion("End apply for $project")
    }

    private fun configureJavadoc(project: Project) {
        project.tasks.withType(Javadoc::class.java).configureEach {
            val options = it.options as StandardJavadocDocletOptions
            if (JavaVersion.current().isJava9Compatible) {
                options.addBooleanOption("html5", true)
            }
            if (JavaVersion.current().isJava8Compatible) {
                options.addStringOption("Xdoclint:none", "-quiet")
            }
        }
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

                        *//* Register Custom Task'и *//*
                        with(evaluateSubProject.tasks) {
                            registerTask(AarMainTask.taskCreator(extension))
//                            registerTask(AarDependencyTask.taskCreator())
                            registerTask(AarPublishTask.taskCreator(evaluateSubProject, extension))

                            extension.targetPlatform?.platformName?.let {

                                val clearTask = getByName(TASK_CLEAN_PROJECT)
                                    .doFirst { logSimple("clearTaskDoFirst") }
                                    .doLast { logSimple("clearTaskDoLast") }

*//*
                                val buildAarTask =
                                    getByName(TASK_BUNDLE_DEBUG_AAR_FOR_TEST.format(it))
                                        .dependsOn(clearTask)
                                        .mustRunAfter(clearTask)
*//*

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

    companion object {
        private const val AAR_GENERATOR_PLUGIN_TAG = "AAR_GENERATOR_PLUGIN"
        private const val AAR_GENERATOR_PLUGIN_NAME = "aarGeneratorPlugin"

        const val AAR_GENERATOR_PLUGIN_TASK_GROUP = "aar generator task group"
    }
}