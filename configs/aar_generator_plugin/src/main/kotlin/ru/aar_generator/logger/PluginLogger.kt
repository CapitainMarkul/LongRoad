package ru.aar_generator.logger

import org.gradle.api.Project
import ru.aar_generator.plugin.config.PluginConfigurator

interface PluginLogger {

    val logTag: String
        get() = this::class.java.simpleName

    fun logStartRegion(message: String) =
        println("|$logTag| >>>> $message")

    fun logEndRegion(message: String) =
        println("|$logTag| $message <<<<")

    fun logSimple(message: String) =
        println("|$logTag| >> $message")

    fun Project.showAllSubProjects() = printWithDividerLine {
        print("SubProjects for ${this.name} project")
        if (subprojects.isEmpty()) println(" not found!")
        subprojects.forEach { println("SubProject $it") }
    }

    fun Project.showAllTasks(recursive: Boolean) = printWithDividerLine {
        logStartRegion("Tasks for ${this.name} project")
        getAllTasks(recursive).forEach { map ->
            map.value.forEach { task ->
                logSimple("${map.key} $task")
            }
        }
    }

    fun Project.showProjectConfiguration(currentConfig: PluginConfigurator.Config) = afterEvaluate {
        printWithDividerLine {
            println("Final configuration for '${it.name}' project")
            println(currentConfig.createConfigurationLog())
        }
    }

    private inline fun printWithDividerLine(block: () -> Unit) {
//        println("")
        block()
    }
}