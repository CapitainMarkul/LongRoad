package ru.aar_generator_plugin.gradle.task.sub

import com.android.build.gradle.internal.tasks.factory.TaskCreationAction
import org.gradle.api.DefaultTask
import org.gradle.api.Task
import org.gradle.api.tasks.TaskAction
import ru.aar_generator_plugin.gradle.PluginConfigurator.AarGeneratorPluginConfig
import ru.aar_generator_plugin.gradle.log.PluginLogger
import ru.aar_generator_plugin.gradle.task.base.TASK_BUNDLE_DEBUG_AAR_FOR_TEST
import ru.aar_generator_plugin.gradle.task.base.BaseTaskCreator

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
                    doFirst {
                        logSimple("aar_publish_doFirst")
                    }

                    // Действие ПОСЛЕ выполнения задачи
                    doLast {
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