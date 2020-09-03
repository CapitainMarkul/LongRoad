package ru.aar_generator_plugin.gradle

/*** Регистрация task'и в проекте. Регистрация происходит в момент Sync'а проекта */
/* override fun apply(project: Project) {
    logAarPlugin("====> Starting apply for $project")
        project.afterEvaluate {
            with(it.tasks) {
                registerTask(AarMainTask.taskCreator())
                registerTask(AarDependencyTask.taskCreator())
            }
        }
} */

/*** Task в dsl стиле */
/* project.task("aar_test_dependency") {
    task ->
    // Устанавливаем группу для задачи
    task.group = AAR_GENERATOR_TASK_GROUP

    // Действие ДО выполнения задачи
    task.doFirst {
        logAarPlugin("aar_test_dependency_doFirst")
    }

    // Действие ПОСЛЕ выполнения задачи
    task.doLast {
        logAarPlugin("aar_test_dependency_doLast")
    }
}

project.task("aar_test") {
    task ->
    // Устанавливаем группу для задачи
    task.group = AAR_GENERATOR_TASK_GROUP
    // Действие ДО выполнения задачи
    task.doFirst {
        logAarPlugin("aar_test_doFirst")
    }

    // Зависимость от других задач. Основная задача будет выполнена только после
    // выполнения указанных в зависимости
    task.dependsOn.add("aar_test_dependency").apply {
        logAarPlugin("Dependency activating")
    }

    // Действие ПОСЛЕ выполнения задачи
    task.doLast {
        logAarPlugin("aar_test_doLast")
    }
} */

/*** Task в формате class'a */
/* open class AarDependencyTask : DefaultTask() {
    companion object {
        const val TASK_NAME = "AAR_DEPENDENCY_TASK"

        fun taskCreator(): TaskCreationAction<AarDependencyTask> =
            object : TaskCreationAction<AarDependencyTask>() {
                override val name: String
                    get() = TASK_NAME

                override val type: Class<AarDependencyTask>
                    get() = AarDependencyTask::class.java

                override fun configure(task: AarDependencyTask): Unit = task.run {
                    // Устанавливаем группу для задачи
                    group = AarGeneratorPlugin.AAR_GENERATOR_TASK_GROUP

                    // Действие ДО выполнения задачи
                    doFirst {
                        AarGeneratorPlugin.logAarPlugin("aar_test_dependency_doFirst")
                    }

                    // Действие ПОСЛЕ выполнения задачи
                    doLast {
                        AarGeneratorPlugin.logAarPlugin("aar_test_dependency_doLast")
                    }

                    // Зависимости
                    // dependsOn("clean")
                }
            }
    }

    @TaskAction
    fun runTask() {
        AarGeneratorPlugin.logAarPlugin("$TASK_NAME running!")
    }
} */

/*** Этапы сборки ( https://docs.gradle.org/current/userguide/build_lifecycle.html#build_lifecycle ) */
/* 1. Initialization
'settings.gradle'
 println("This is executed during the initialization phase.")
*/

/* 2. Configuration
 'build.gradle'
  println("This is executed during the configuration phase.")
*/

/* 3. Tasks
 'task'

tasks.register("configured") {
    println("This is also executed during the configuration phase.")
}

tasks.register("test") {
    doLast {
        println("This is executed during the execution phase.")
    }
}
*/

/*** Группы task'ок */
/* const val AAR_GENERATOR_PLUGIN_TASK_GROUP = "aar generator task group"
   Необходимо, чтобы они отображались в общем списке тасок проекта
 */

/*** Бывают разные типы плагинов */
/*
class GradleScript : Plugin<Gradle> {
    override fun apply(target: Gradle) {
        TODO()
    }
}

class GradleSettings : Plugin<Settings> {
    override fun apply(target: Settings) {
        TODO()
    }
}
*/

/*** Register Base Tasks For Project */
/* extension.targetPlatform?.platformName?.let {
    val bundleAarTask = BUNDLE_DEBUG_AAR_FOR_TEST.format(it)
    register(bundleAarTask)
    getByName(bundleAarTask)
        .mustRunAfter(getByName(CLEAN_PROJECT))
        .logExecuteBaseTask()
} */

/*** Check plugin available */
/* project.subprojects {
    foreachItem ->
    foreachItem.afterEvaluate {
        val isAndroidProject =
            (it.pluginManager.hasPlugin("com.android.application") ||
                    it.pluginManager.hasPlugin("com.android.library"))

        if (isAndroidProject) {
            //TODO()
        }
    }
} */
