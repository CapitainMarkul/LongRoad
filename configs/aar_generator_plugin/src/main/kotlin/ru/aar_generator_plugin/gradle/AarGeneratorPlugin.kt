package ru.aar_generator_plugin.gradle

import com.android.build.gradle.internal.tasks.factory.registerTask
import org.gradle.api.DefaultTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import ru.aar_generator_plugin.gradle.log.PluginLogger
import ru.aar_generator_plugin.gradle.sub_tasks.AarDependencyTask
import ru.aar_generator_plugin.gradle.sub_tasks.AarMainTask

//class GradleScript : Plugin<Gradle> {
//    override fun apply(target: Gradle) {
//        target.settingsEvaluated {
////            it.settings.a
//        }
//    }
//}
//
//class GradleSettings : Plugin<Settings> {
//    override fun apply(target: Settings) {
//        target.include("aar_generator_plugin")
//        target.
//        target.gradle.includedBuild("aar_generator_plugin")
//    }
//}

class AarGeneratorPlugin : Plugin<Project>, PluginLogger {
    override val tag: String
        get() = AAR_GENERATOR_PLUGIN_TAG

    override fun apply(project: Project) {
        logStartRegion("Starting apply for $project")

        project.afterEvaluate {
            // Регистрируем созданные Task'и. Регистрация происходит в момент Sync'а проекта
            with(it.tasks) {
                /* Base Task'и */
                getByName("clean").logExecuteBaseTask()

                /* Custom Task'и */
                registerTask(AarMainTask.taskCreator())
                registerTask(AarDependencyTask.taskCreator())
            }
        }

        // Переменная, которую укажем в root.build.gradle
        val extension = project.extensions.create<AarGeneratorPluginExtension>(
            AAR_GENERATOR_PLUGIN_NAME, AarGeneratorPluginExtension::class.java
        )
        project.afterEvaluate {
//            logAarPlugin("AfterEvaluate : ${extension.packageNamePlugin}")
//
//            project.allprojects {
//                val mavenPlugin = MavenPublishPlugin::class.java
//                logAarPlugin("Run for $it")
//
//                logAarPlugin("Apply ${mavenPlugin.canonicalName} plugin for $it")
//                it.pluginManager.apply(mavenPlugin)
//                logAarPlugin("Plugin ${mavenPlugin.canonicalName} applied for $it || ${it.project.plugins.hasPlugin(mavenPlugin)}")
//            }
//
//            logAarPlugin("Finished $project")
        }

        logEndRegion("End apply for $project")
    }

    open class AarGeneratorPluginExtension {
        var packageNamePlugin: String? = null
        var testPlugin: Test? = null
    }

    enum class Test {
        FIRST, SECOND
    }

    private class AarTestDependencyTask : DefaultTask() {

    }

    companion object {
        private const val AAR_GENERATOR_PLUGIN_TAG = "AAR_GENERATOR_PLUGIN"
        private const val AAR_GENERATOR_PLUGIN_NAME = "aarGeneratorPlugin"

        const val AAR_GENERATOR_PLUGIN_TASK_GROUP = "aar generator task group"
    }
}