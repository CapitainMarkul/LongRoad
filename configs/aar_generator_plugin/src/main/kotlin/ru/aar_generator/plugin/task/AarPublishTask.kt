package ru.aar_generator.plugin.task

import com.android.build.gradle.internal.tasks.factory.TaskCreationAction
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import ru.aar_generator.logger.PluginLogger
import ru.aar_generator.plugin.AarGeneratorPlugin

/*** Task */
open class AarPublishTask : DefaultTask(), PluginLogger {
    override val logTag: String
        get() = TASK_NAME

    @TaskAction
    fun runTask() {
        logSimple("$TASK_NAME running!")
    }

    companion object {
        const val TASK_NAME = "aar_publish_task"

        private const val PUBLISH_TO_MAVEN_LOCAL_TASK_NAME = "publishToMavenLocal"

        /*** Task Creator */
        fun taskCreator(
            isRootProject: Boolean,
            needAutoRunScript: Boolean
        ): TaskCreationAction<AarPublishTask> =
            object : TaskCreationAction<AarPublishTask>() {

                override val name: String = TASK_NAME
                override val type: Class<AarPublishTask>
                    get() = AarPublishTask::class.java

                override fun configure(task: AarPublishTask): Unit = task.run {

                    // Устанавливаем группу для задачи
                    group = AarGeneratorPlugin.AAR_GENERATOR_PLUGIN_TASK_GROUP

                    dependsOn(PUBLISH_TO_MAVEN_LOCAL_TASK_NAME)
                    if (isRootProject && needAutoRunScript) dependsOn(AarScriptTask.TASK_NAME)
                }
            }
    }
}