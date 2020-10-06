package ru.aar_generator_plugin.gradle.task.sub

import com.android.build.gradle.internal.tasks.factory.TaskCreationAction
import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.artifacts.dsl.RepositoryHandler
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.tasks.TaskAction
import ru.aar_generator_plugin.gradle.PluginConfigurator.AarGeneratorPluginConfig
import ru.aar_generator_plugin.gradle.log.PluginLogger
import ru.aar_generator_plugin.gradle.task.base.BaseTaskCreator
import ru.aar_generator_plugin.gradle.task.base.PLUGIN_MAVEN_PUBLISH
import ru.aar_generator_plugin.gradle.task.base.TASK_BUNDLE_DEBUG_AAR_FOR_TEST

/*** Task */
open class AarPublishTask : DefaultTask(), PluginLogger {
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

            return object : BaseTaskCreator<AarPublishTask>(TASK_NAME, AarPublishTask::class.java) {
                override fun configure(task: AarPublishTask): Unit = task.run {
                    // 1. Конфигурирование Task'и
                    super.configure(task)

                    // Действие ДО выполнения задачи
                    doFirst { task ->
                        logSimple("aar_publish_doFirst")

                        val publishing =
                            afterEvaluatedProject.properties[PUBLISH_EXTENSIONS] as PublishingExtension

                        /* Setup Repositories */
                        publishing.repositories {
                            logStartRegion("configurePublishRepositories Start")
                            it.configurePublishRepositories(this@run)
                            logEndRegion("configurePublishRepositories End")
                        }

                        /* Setup Publication options */
                        publishing.publications { publicationContainer ->
                            publicationContainer.create(
                                "CUSTOM_PUBLICATION_NAME",
                                MavenPublication::class.java
                            ) {
                                logStartRegion("configurePublishArtifact Start")
                                it.configurePublishArtifact(afterEvaluatedProject, this@run)
                                it.configurePomDependencies()
                                logEndRegion("configurePublishArtifact End")
                            }
                        }

                        publishing.publications
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
                    // Enable 'mavenLocal'
                    mavenLocal()
/*                    mavenLocal { mavenLocal ->
//                      mavenLocal.artifactUrls("$buildDir/outputs/aar")
                        mavenLocal.artifactUrls.forEach {
                            logger.logSimple("Repository $it")
                        }
                    }*/
                }

                private fun MavenPublication.configurePublishArtifact(
                    project: Project,
                    logger: PluginLogger
                ) {
                    from(afterEvaluatedProject.components.findByName("debug"))

                    logger.logSimple("Publication Name: ${this.name}")

                    // TODO() можно добовлять выбранный BuildType (вместо '.debug')
                    artifactId =
                        "${project.properties["applicationId"].toString()}.${project.name}.debug"
                    groupId = "${project.name}.aar_group"
                    version = "0.0.1"

                    //FIXME: нужно заставить работать artifact(task)
                    val task = project.getTasksByName("bundleDebugAar", true).first()
                    logger.logSimple("Task: ${task}")
                    val art = artifact(task)
                        logger.logSimple("Artifact: ${art.extension}")
                    logger.logSimple("File: ${art.file}")

//                    val aarDirectory = "$buildDir/outputs/aar/engine-debug.aar"
//                    logSimple(aarDirectory)

//                    val artifactLocal = it.artifact(aarDirectory)
//                    logSimple(artifactLocal.toString())

//                    configurations.maybeCreate("default")
//
//                    val fileAar = file(aarDirectory)
//                    logStartRegion(fileAar.toString())
//                    val publishArtifact = artifacts.add("default", fileAar)
//
//                    it.artifact(publishArtifact)
                }

                private fun MavenPublication.configurePomDependencies() {
                    /*it.pom?.let { mavenPom ->
                        mavenPom.developers {  }
                        mavenPom.packaging = "TestPackage"

                        //The publication doesn't know about our dependencies, so we have to manually add them to the pom
                        mavenPom.withXml { xmlProvider ->
                            val dependenciesNode = xmlProvider.asNode().appendNode("dependencies")

                            project.configurations.names.forEach {
                                logSimple("Config: $it")
                            }
//                                            configurationNames.each { configurationName ->
//                                                configurations[configurationName].allDependencies.each {
//                                                    if (it.group != null && it.name != null) {
//                                                        def dependencyNode = dependenciesNode.appendNode('dependency')
//                                                        dependencyNode.appendNode('groupId', it.group)
//                                                        dependencyNode.appendNode('artifactId', it.name)
//                                                        dependencyNode.appendNode('version', it.version)
//
//                                                        //If there are any exclusions in dependency
//                                                        if (it.excludeRules.size() > 0) {
//                                                            def exclusionsNode = dependencyNode.appendNode('exclusions')
//                                                            it.excludeRules.each { rule ->
//                                                                def exclusionNode = exclusionsNode.appendNode('exclusion')
//                                                                exclusionNode.appendNode('groupId', rule.group)
//                                                                exclusionNode.appendNode('artifactId', rule.module)
//                                                            }
//                                                        }
//                                                    }
//                                                }
//                                            }
                        }
                    }*/
                }
            }
        }
    }
}