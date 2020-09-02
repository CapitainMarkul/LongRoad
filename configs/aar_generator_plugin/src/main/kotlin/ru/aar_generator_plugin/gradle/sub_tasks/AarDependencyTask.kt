package ru.aar_generator_plugin.gradle.sub_tasks

import com.android.build.gradle.internal.tasks.factory.TaskCreationAction
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import ru.aar_generator_plugin.gradle.log.PluginLogger
import ru.aar_generator_plugin.gradle.sub_tasks.base.BaseTaskCreator

/*** Task */
open class AarDependencyTask : DefaultTask(), PluginLogger {
    override val tag: String
        get() = TASK_NAME

    @TaskAction
    fun runTask() {
        logSimple("$TASK_NAME running!")
    }

    companion object {
        const val TASK_NAME = "aar_dependency_task"

        /*** Task Creator */
        fun taskCreator(): TaskCreationAction<AarDependencyTask> =
            object : BaseTaskCreator<AarDependencyTask>(TASK_NAME, AarDependencyTask::class.java) {
                override fun configure(task: AarDependencyTask): Unit = task.run {
                    super.configure(task)

                    // Действие ДО выполнения задачи
                    doFirst {
                        logSimple("aar_test_dependency_doFirst")
                    }

                    // Действие ПОСЛЕ выполнения задачи
                    doLast {
                        logSimple("aar_test_dependency_doLast")
                    }
                }
            }
    }
}