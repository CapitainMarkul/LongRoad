package ru.aar_generator.plugin

import org.gradle.api.JavaVersion
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.publish.maven.plugins.MavenPublishPlugin
import org.gradle.api.tasks.javadoc.Javadoc
import org.gradle.external.javadoc.StandardJavadocDocletOptions
import org.gradle.util.VersionNumber
import ru.aar_generator.gradle.AarPublishConfigurer
import ru.aar_generator.gradle.MavenPublishPom
import ru.aar_generator.gradle.MavenPublishTarget
import ru.aar_generator.logger.PluginLogger
import ru.aar_generator.plugin.config.PluginConfigurator

class AarGeneratorPlugin : Plugin<Project>, PluginLogger {

    companion object {
        private const val AAR_GENERATOR_PLUGIN_LOG_TAG = "AAR_LOG"
        private const val AAR_GENERATOR_PLUGIN_NAME = "aarGeneratorPlugin"
        private const val AAR_GENERATOR_PLUGIN_CONFIG_NAME = "aarGeneratorPluginConfig"

        private const val MAVEN_LOCAL_TARGET = "installArchives"

        private val SUPPORT_GRADLE_VERSION = VersionNumber(6, 6, 0, null)

        const val AAR_GENERATOR_PLUGIN_TASK_GROUP = "aar generator task group"
    }

    private val pluginClassName: String = AarGeneratorPlugin::class.java.simpleName

    override val logTag: String
        get() = AAR_GENERATOR_PLUGIN_LOG_TAG

    override fun apply(project: Project) {
        logStartRegion("Starting apply $pluginClassName for $project")

        // 0. Проверка версии Gradle
        val gradleVersion = VersionNumber.parse(project.gradle.gradleVersion)
        if (gradleVersion < SUPPORT_GRADLE_VERSION)
            throw IllegalArgumentException("You need gradle version 6.6.0 or higher")

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
            if (subProject.name != "app" && subProject.name != "LongRoad") {
                subProject.afterEvaluate { afterEvaluateProject ->
                    afterEvaluateProject.plugins.apply(AarGeneratorPlugin::class.java)
                }
            }
        }






        logSimple("Apply MavenPublishPlugin for: " + project.name)
        project.plugins.apply(MavenPublishPlugin::class.java)

        val pom =
            MavenPublishPom.fromProject(
                project
            )
        project.group = "com.long_road"
        project.version = "0.0.1"

        configureJavadoc(project)
//        configureDokka(project)

        project.afterEvaluate { projectAfterEvaluate ->
            val configurer =
                AarPublishConfigurer(project)

            val localTarget = MavenPublishTarget(
                MAVEN_LOCAL_TARGET,
                releaseRepositoryUrl = project.repositories.mavenLocal().url.toASCIIString(),
                signing = false
            )

            val targets: NamedDomainObjectContainer<MavenPublishTarget> =
                project.container(MavenPublishTarget::class.java) {
                    MavenPublishTarget(it)
                }.apply {
                    add(localTarget)
                }

            targets.all {
                checkNotNull(it.releaseRepositoryUrl) {
                    "releaseRepositoryUrl of ${it.name} is required to be set"
                }
                configurer.configureTarget(it)
            }

            if (projectAfterEvaluate.plugins.hasPlugin("com.android.library")) {
                configurer.configureAndroidArtifacts()
            }
        }

        // Регистрируем созданные Task'и. Регистрация происходит в момент Sync'а проекта
//            taskConfigureAndRegister(it, extension.getCurrentConfiguration())
//        }

        logEndRegion("End apply $pluginClassName for $project")
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