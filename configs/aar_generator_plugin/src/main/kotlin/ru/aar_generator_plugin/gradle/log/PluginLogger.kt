package ru.aar_generator_plugin.gradle.log

import org.gradle.api.Project
import org.gradle.api.Task

interface PluginLogger {

    val tag: String
        get() = this::class.java.simpleName

    fun logStartRegion(message: String) =
        println("|$tag| =====> $message")

    fun logEndRegion(message: String) =
        println("|$tag| $message <=====")

    fun logSimple(message: String) =
        println("|$tag| >> $message")

    fun Task.logExecuteBaseTask(): Task {
        doFirst { logStartRegion("${it.name} doFirst") }

        doLast { logEndRegion("${it.name} doLast") }

        return this
    }

    fun Project.showAllSubProjects() {
        logSimple("")

        logStartRegion("SubProjects for ${this.name} project")
        if (subprojects.isEmpty()) logSimple("SubProjects not found!")
        subprojects.forEach { logSimple("SubProject $it") }
    }

    fun Project.showAllTasks(recursive: Boolean) {
        logSimple("")

        logStartRegion("Tasks for ${this.name} project")
        getAllTasks(recursive).forEach { map ->
            map.value.forEach { task ->
                logSimple("${map.key} $task")
            }
        }
    }
}