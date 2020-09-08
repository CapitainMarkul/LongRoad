package ru.aar_generator_plugin.gradle.task.sub

import com.android.build.gradle.internal.scope.publishArtifactToConfiguration
import com.android.build.gradle.internal.tasks.factory.TaskCreationAction
import org.gradle.api.Action
import org.gradle.api.DefaultTask
import org.gradle.api.Task
import org.gradle.api.artifacts.dsl.RepositoryHandler
import org.gradle.api.artifacts.repositories.MavenArtifactRepository
import org.gradle.api.publish.PublicationContainer
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.publish.maven.internal.publication.DefaultMavenPublication
import org.gradle.api.publish.maven.plugins.MavenPublishPlugin
import org.gradle.api.tasks.TaskAction
import ru.aar_generator_plugin.gradle.PluginConfigurator
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

        /*** Task Creator */
        fun taskCreator(pluginCfg: AarGeneratorPluginConfig?): TaskCreationAction<AarPublishTask> =
            object : BaseTaskCreator<AarPublishTask>(TASK_NAME, AarPublishTask::class.java) {
                override fun configure(task: AarPublishTask): Unit = task.run {
                    super.configure(task)

                    // Действие ДО выполнения задачи
                    doFirst { task ->
                        logSimple("aar_publish_doFirst")

                        with(task.project) {
                            plugins.apply(PLUGIN_MAVEN_PUBLISH)

                            val publishing = properties["publishing"] as PublishingExtension
publishing.toString()
                            /* Setup Repositories */
                            publishing.repositories { repository ->
                                logStartRegion("Setup Repository")

                                repository.mavenLocal { mavenLocal ->
                                    mavenLocal.artifactUrls("$buildDir/outputs/aar")

                                    mavenLocal.artifactUrls.forEach {
                                        logSimple("Repository $it")
                                    }
                                }

                                logEndRegion("Setup Repository")
                            }

                            /* Setup Publication options */
                            publishing.publications { publicationContainer ->
                                //TODO()
                                val mavenPublication = DefaultMavenPublication(

                                )

                                publicationContainer.add(mavenPublication)
                                publicationContainer.asMap.forEach { name, publication ->
                                    logSimple("Available: $name $publication")
                                }
//                                with( as MavenPublication) {
//                                    from(components.findByName("debug"))
//
//                                    groupId = "ru.aar_generator_plugin.gradle.library"
//                                    artifactId = "library.debug"
//                                    version = "1.0"
//                                }
                            }
                        }
                    }
//                            version = "0.0.1" //TODO() Взять это из rootProject

                    // Действие ПОСЛЕ выполнения задачи
                    doLast {
                        //TODO() Сообщение с содержанием "Куда и что опубликовали"
                        logSimple("aar_publish_doLast")
                        it.didWork = true
                    }
                }

                override fun setupDependency(): (T: Task) -> Task = { task ->
                    pluginCfg?.targetPlatform?.platformName?.let {
                        task.dependsOn(TASK_BUNDLE_DEBUG_AAR_FOR_TEST.format(it))
                    } ?: task
                }
            }
    }
}