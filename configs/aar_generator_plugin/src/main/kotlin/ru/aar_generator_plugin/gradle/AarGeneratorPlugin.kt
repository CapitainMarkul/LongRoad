package ru.aar_generator_plugin.gradle

import com.android.build.gradle.AppExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.publish.maven.plugins.MavenPublishPlugin

class AarGeneratorPlugin : Plugin<Project> {
    private companion object {
        const val AAR_GENERATOR_PLUGIN_TAG = "AAR_GENERATOR_PLUGIN"
        const val AAR_GENERATOR_PLUGIN_NAME = "aarGeneratorPlugin"
    }

    override fun apply(project: Project) {
        logAarPlugin("Starting for $project")

        // Переменная, которую укажем в root.build.gradle
        val extension = project.extensions.create<AarGeneratorPluginExtension>(
            AAR_GENERATOR_PLUGIN_NAME, AarGeneratorPluginExtension::class.java
        )
        project.afterEvaluate {
            logAarPlugin("AfterEvaluate : ${extension.packageNamePlugin}")

            project.allprojects {
                val mavenPlugin = MavenPublishPlugin::class.java
                logAarPlugin("Run for $it")

                logAarPlugin("Apply ${mavenPlugin.canonicalName} plugin for $it")
                it.pluginManager.apply(mavenPlugin)
                logAarPlugin("Plugin ${mavenPlugin.canonicalName} applied for $it || ${it.project.plugins.hasPlugin(mavenPlugin)}")
            }

            logAarPlugin("Finished $project")
        }
    }

    private fun logAarPlugin(message: String) {
        println("|$AAR_GENERATOR_PLUGIN_TAG| >> $message")
    }

    open class AarGeneratorPluginExtension {
        var packageNamePlugin: String? = null
    }
}