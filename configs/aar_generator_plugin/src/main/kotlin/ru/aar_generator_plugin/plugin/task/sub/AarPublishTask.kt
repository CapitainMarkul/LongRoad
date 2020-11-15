package ru.aar_generator_plugin.plugin.task.sub

import com.android.build.gradle.internal.tasks.factory.TaskCreationAction
import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.artifacts.dsl.RepositoryHandler
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.tasks.TaskAction
import ru.aar_generator_plugin.plugin.config.PluginConfigurator.AarGeneratorPluginConfig
import ru.aar_generator_plugin.log.PluginLogger
import ru.aar_generator_plugin.plugin.task.base.BaseTaskCreator

/*** Task */
open class AarPublishTask : DefaultTask(),
    PluginLogger {
    override val tag: String
        get() = TASK_NAME

    @TaskAction
    fun runTask() {
        logSimple("$TASK_NAME running!")
    }

    companion object {
        const val TASK_NAME = "aar_publish_task"
        const val PUBLISH_EXTENSIONS = "publishing"

        /*** Task Creator */
        fun taskCreator(
            afterEvaluatedProject: Project,
            pluginCfg: AarGeneratorPluginConfig?
        ): TaskCreationAction<AarPublishTask> {
            // ВАЖНО: конфигурировать плагин необходимо ДО конфигурирования Task'и
            // 0. Установка зависимостей от сторонних плагинов

//            logStartRegion("Apply MavenPublish for project: ${evaluateSubProject.name}")
//            afterEvaluatedProject.plugins.apply(PLUGIN_MAVEN_PUBLISH)

            return object : BaseTaskCreator<AarPublishTask>(
                TASK_NAME, AarPublishTask::class.java) {
                override fun configure(task: AarPublishTask): Unit = task.run {
                    // 1. Конфигурирование Task'и
                    super.configure(task)

                    // Действие ДО выполнения задачи
                    doFirst { task ->
                        logSimple("aar_publish_doFirst")

                        val publishing =
                            afterEvaluatedProject.properties[PUBLISH_EXTENSIONS] as PublishingExtension

                        /* Setup Repositories */
//                        publishing.repositories.mavenLocal {
//                            it.name = "LongRoad"
////                            it.metadataSources { it.artifact() }
//                        }
//
//                        /* Setup Publication options */
//                        publishing.publications.create("LongRoadPublish", MavenPublication::class.java) { publication ->
//                            publication.configurePomDependencies(afterEvaluatedProject, this@run)
//                            publication.configurePublishArtifact(afterEvaluatedProject, this@run)
//                        }

                        /* Setup Repositories */
                        publishing.repositories { repositoryHandler ->
                            logStartRegion("configurePublishRepositories Start")
                            repositoryHandler.configurePublishRepositories(this@run)
                            logEndRegion("configurePublishRepositories End")
                        }

                        /* Setup Publication options */
                        publishing.publications.create(
                            "CUSTOM_PUBLICATION_NAME",
                            MavenPublication::class.java
                        ) {
                            logStartRegion("configurePublishArtifact Start")
                            it.configurePomDependencies(afterEvaluatedProject, this@run)
                            it.configurePublishArtifact(afterEvaluatedProject, this@run)
                            logEndRegion("configurePublishArtifact End")
                        }
                    }

                    // Действие ПОСЛЕ выполнения задачи
                    doLast { task ->
                        //TODO() Сообщение с содержанием "Куда и что опубликовали"
                        logSimple("aar_publish_doLast")
                        task.didWork = true
                    }
                }

                /*override fun setupTaskDependency(): (T: Task) -> Task = { task ->
                    pluginCfg?.targetPlatform?.platformName?.let {
                        task.dependsOn(TASK_BUNDLE_DEBUG_AAR_FOR_TEST.format(it))
                    } ?: task
                }*/


                private fun RepositoryHandler.configurePublishRepositories(logger: PluginLogger) {
                    logger.logSimple("${this.size}")

//                    this.gradlePluginPortal {
//                        logger.logSimple("gradlePluginPortal")
//                    }
//                    this.ivy {
//                        logger.logSimple("ivy")
//                    }
//                    this.maven {
//                        logger.logSimple("maven")
//                    }

                    this.mavenLocal {
                        it.name = "LongRoad__222"
                        it.artifactUrls("/home/local/TENSOR-CORP/da.pavlov1/PavlovDoc/HomeProject/LongRoad/module/engine/build/outputs/aar/engine-debug.aar")
                    }
//                    this.flatDir {
//                        logger.logSimple("flatDir")
//                    }

                    logger.logSimple("${this.size}")

                }

                private fun MavenPublication.configurePomDependencies(
                    project: Project,
                    logger: PluginLogger
                ) {
                    pom { pom ->
                        pom.name.set("publishPom.name")
                        pom.description.set("publishPom.description")
                        pom.url.set("publishPom.url")
                        pom.inceptionYear.set("publishPom.inceptionYear")

                        pom.scm {
                            it.url.set("publishPom.scmUrl")
                            it.connection.set("publishPom.scmConnection")
                            it.developerConnection.set("publishPom.scmDeveloperConnection")
                        }

                        pom.licenses { licenses ->
                            licenses.license {
                                it.name.set("publishPom.licenseName")
                                it.url.set("publishPom.licenseUrl")
                                it.distribution.set("publishPom.licenseDistribution")
                            }
                        }

                        pom.developers { developers ->
                            developers.developer {
                                it.id.set("publishPom.developerId")
                                it.name.set("publishPom.developerName")
                                it.url.set("publishPom.developerUrl")
                            }
                        }
                    }
                }

                private fun MavenPublication.configurePublishArtifact(
                    project: Project,
                    logger: PluginLogger
                ) {
/*                    val publication = project.publishing.publications.getByName(PUBLICATION_NAME) as MavenPublication

                    from(afterEvaluatedProject.components.findByName("debug"))

                    // TODO() можно добовлять выбранный BuildType (вместо '.debug')
                    artifactId =
                        "${project.properties["applicationId"].toString()}.${project.name}.debug"
                    groupId = "${project.name}.aar_group"
                    version = "0.0.1"

                    val androidSourcesJar = project.tasks.register("androidSourcesJar", AndroidSourcesJar::class.java)
                    artifact(androidSourcesJar)*/
                }
            }
        }
    }
}