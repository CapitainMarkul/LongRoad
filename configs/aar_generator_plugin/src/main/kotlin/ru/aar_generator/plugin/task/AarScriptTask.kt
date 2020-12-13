package ru.aar_generator.plugin.task

import com.android.build.gradle.internal.tasks.factory.TaskCreationAction
import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.tasks.TaskAction
import ru.aar_generator.logger.PluginLogger
import ru.aar_generator.plugin.AarGeneratorPlugin
import java.util.*

/*** Task */
open class AarScriptTask : DefaultTask(), PluginLogger {
    override val logTag: String
        get() = TASK_NAME

    @TaskAction
    fun runTask() {
        logSimple("$TASK_NAME running!")
    }

    companion object {
        const val TASK_NAME = "aar_script_task_experimental"

        private const val LINUX_SCRIPT_NAME = "convert_project_to_aar_script.sh"
        private const val WINDOWS_SCRIPT_NAME = "convert_project_to_aar_script.ps1"

        private const val SCRIPT_PROJECT_DIR_OPTION = "-p"
        private const val SCRIPT_PROJECT_NAME_OPTION = "-n"
        private const val SCRIPT_PROJECT_FLAVOUR_OPTION = "-f"
        private const val SCRIPT_PROJECT_MILESTONES_OPTION = "-m"

        /*** Параметры запуска скрипта */
        class ScriptParams(
            val platformName: String,
            val milestonesVersion: String
        )

        /*** Task Creator */
        fun taskCreator(
            rootProject: Project,
            scriptParams: ScriptParams
        ): TaskCreationAction<AarScriptTask> =
            object : TaskCreationAction<AarScriptTask>() {

                override val name: String = TASK_NAME
                override val type: Class<AarScriptTask>
                    get() = AarScriptTask::class.java

                override fun configure(task: AarScriptTask): Unit = task.run {
                    // Устанавливаем группу для задачи
                    group = AarGeneratorPlugin.AAR_GENERATOR_PLUGIN_TASK_GROUP

                    doLast {
                        val systemName = System.getProperty("os.name")
                        logSimple("Your OS is $systemName")

                        val isWindows = systemName
                            .toLowerCase(Locale.ROOT)
                            .contains("windows")

                        val scriptNameForCurrentOs =
                            if (isWindows) WINDOWS_SCRIPT_NAME else LINUX_SCRIPT_NAME

                        val scriptPath = rootProject
                            .fileTree(rootProject.projectDir)
                            .filter { it.isFile && it.name == scriptNameForCurrentOs }
                            .singleFile

                        rootProject.exec {
                            if (isWindows) {
                                logSimple("Run '$WINDOWS_SCRIPT_NAME' script.")

                                it.commandLine(
                                    "cmd", "/c", "powershell.exe -ExecutionPolicy Bypass -File \"${scriptPath}\" " +
                                            "\"${rootProject.projectDir}\" " +
                                            "\"${rootProject.name}\" " +
                                            "\"${scriptParams.platformName}\" " +
                                            "\"${scriptParams.milestonesVersion}\""
                                )
                            } else {
                                logSimple("Run '$LINUX_SCRIPT_NAME' script.")

                                it.commandLine(
                                    "bash", scriptPath,
                                    SCRIPT_PROJECT_DIR_OPTION, rootProject.projectDir,
                                    SCRIPT_PROJECT_NAME_OPTION, rootProject.name,
                                    SCRIPT_PROJECT_FLAVOUR_OPTION, scriptParams.platformName,
                                    SCRIPT_PROJECT_MILESTONES_OPTION, scriptParams.milestonesVersion
                                )
                            }
                        }
                    }
                }
            }
    }
}