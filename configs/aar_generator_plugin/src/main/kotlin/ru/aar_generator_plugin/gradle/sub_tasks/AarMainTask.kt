package ru.aar_generator_plugin.gradle.sub_tasks

import com.android.build.gradle.internal.tasks.factory.TaskCreationAction
import org.gradle.api.DefaultTask
import org.gradle.api.Task
import org.gradle.api.tasks.TaskAction
import ru.aar_generator_plugin.gradle.PluginConfigurator.AarGeneratorPluginConfig
import ru.aar_generator_plugin.gradle.log.PluginLogger
import ru.aar_generator_plugin.gradle.sub_tasks.base.BUNDLE_DEBUG_AAR_FOR_TEST
import ru.aar_generator_plugin.gradle.sub_tasks.base.BaseTaskCreator
import ru.aar_generator_plugin.gradle.sub_tasks.base.CLEAN_PROJECT

/*** Task */
open class AarMainTask : DefaultTask(), PluginLogger {
    override val tag: String
        get() = TASK_NAME

    @TaskAction
    fun runTask() {
        logSimple("$TASK_NAME running!")
    }

    companion object {
        const val TASK_NAME = "aar_main_task"

        /*** Task Creator */
        fun taskCreator(pluginCfg: AarGeneratorPluginConfig?): TaskCreationAction<AarMainTask> =
            object : BaseTaskCreator<AarMainTask>(TASK_NAME, AarMainTask::class.java) {
                override fun configure(task: AarMainTask): Unit = task.run {
                    super.configure(task)

                    // Действие ДО выполнения задачи
                    doFirst {
                        logSimple("aar_main_doFirst")
                    }

                    // Действие ПОСЛЕ выполнения задачи
                    doLast {
                        logSimple("aar_main_doLast")
                        it.didWork = true
                    }
                }

                override fun setupDependency(): (T: Task) -> Task = { task ->
                    task.dependsOn(CLEAN_PROJECT)

                    pluginCfg?.targetPlatform?.platformName?.let {
                        task.dependsOn(BUNDLE_DEBUG_AAR_FOR_TEST.format(it))

                        /* Собирать нужно именно поле того, как почистили папку с Build'ом.
                         * По-умолчанию Gradle не гарантирует очередность выполнения Task'ок */
                        task.mustRunAfter(CLEAN_PROJECT)
                    } ?: task
                }
            }
    }
}