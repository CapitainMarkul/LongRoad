package ru.aar_generator_plugin.gradle.log

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

    fun Task.logExecuteBaseTask() {
        doFirst { logStartRegion("${it.name} doFirst") }

        doLast { logEndRegion("${it.name} doLast") }
    }
}