package ru.aar_generator_plugin.gradle.task.sub

import com.android.build.gradle.internal.tasks.factory.TaskCreationAction
import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.Task
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

        /*** Task Creator */
        fun taskCreator(afterEvaluateProject: Project, pluginCfg: AarGeneratorPluginConfig?): TaskCreationAction<AarPublishTask> =
            object : BaseTaskCreator<AarPublishTask>(TASK_NAME, AarPublishTask::class.java) {
                override fun configure(task: AarPublishTask): Unit = task.run {
                    super.configure(task)

                    // Действие ДО выполнения задачи
                    doFirst { task ->
                        logSimple("aar_publish_doFirst")

                        with(afterEvaluateProject) {
                            plugins.apply(PLUGIN_MAVEN_PUBLISH)

                            val publishing = properties["publishing"] as PublishingExtension

                            /* Setup Repositories */
                            publishing.repositories { repository ->
                                logStartRegion("Setup Repository")

                                repository.mavenLocal()

                                repository.mavenLocal { mavenLocal ->
//                                    mavenLocal.artifactUrls("$buildDir/outputs/aar")

                                    mavenLocal.artifactUrls.forEach {
                                        logSimple("Repository $it")
                                    }
                                }

                                logEndRegion("Setup Repository")
                            }

                            /* Setup Publication options */
                            publishing.publications { publicationContainer ->
                                publicationContainer.create("PUBLICATION_NAME", MavenPublication::class.java) {
                                    logSimple("CreatePublish")
                                    it.from(afterEvaluateProject.components.findByName("debug"))

                                    it.artifactId = "${this.name}.debug"
                                    it.groupId = "ru.aar_generator_plugin.gradle.library.${this.name}.debug"
                                    it.version = "0.0.1"

                                    val aarDirectory = "$buildDir/outputs/aar/engine-debug.aar"
                                    logSimple(aarDirectory)

//                                    it.artifact("bundleDebugAar")
                                    val artifactLocal = it.artifact(aarDirectory)
                                    logSimple(artifactLocal.toString())

                                    configurations.maybeCreate("default")

                                    val fileAar = file(aarDirectory)
                                    logStartRegion(fileAar.toString())
                                    val publishArtifact = artifacts.add("default", fileAar)

                                    it.artifact(publishArtifact)

                                    it.pom.withXml { xmlProvider ->
                                        val dependenciesNode = xmlProvider.asNode().appendNode("dependencies")
                                    }


                                    logSimple("CreatePublishEnd")
                                }
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