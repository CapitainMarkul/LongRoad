package ru.aar_generator_plugin.gradle.sub_plugins

import org.gradle.api.Plugin
import org.gradle.api.Project

class DataBindingPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        println("DataBindingCfgPlugin enabled for $project")

    }
}