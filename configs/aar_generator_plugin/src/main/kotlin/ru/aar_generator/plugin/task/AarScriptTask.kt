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

        private const val SCRIPT_NAME = "convert_project_to_aar_script.sh"

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
                        val scriptPath = rootProject
                            .fileTree(rootProject.projectDir)
                            .filter { it.isFile && it.name == SCRIPT_NAME}
                            .singleFile
                        logSimple(scriptPath.absolutePath)

                        rootProject.exec {
                            if (System.getProperty("os.name")
                                    .toLowerCase(Locale.ROOT).contains("windows")
                            ) {
                                //TODO()
                            } else {
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



class PowerShellUtil {

    fun exec(command: String, args: String) : String {

        val powerShellCommand = ".\\${command} ${args}"
        val shellCommand = "powershell.exe -ExecutionPolicy Bypass -NoLogo -NonInteractive -NoProfile  -Command \"${powerShellCommand}\""

        println(powerShellCommand)

        return shellCommand
//        val process = shellCommand.execute()
//        def out = new StringBuffer()
//        def err = new StringBuffer()
//        process.consumeProcessOutput(out, err)
//        process.waitFor()
//        if(out.size() > 0 && debug) log.debug out
//                if(err.size() > 0) log.error err
    }

//    PowerShellUtil psUtil = new PowerShellUtil()
//    psUtil.exec(true, 'script.ps1','script-args')
}