package ru.aar_generator_plugin.gradle.util

import org.gradle.api.Project

/*** Проверка подключенного плагина */
fun Project.hasPlugin(pluginName: String) =
    pluginManager.hasPlugin(pluginName)