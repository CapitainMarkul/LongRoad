package ru.aar_generator_plugin.gradle.task.base

import com.android.build.gradle.internal.tasks.factory.TaskCreationAction
import org.gradle.api.Task
import ru.aar_generator_plugin.gradle.AarGeneratorPlugin

abstract class BaseTaskCreator<T : Task>(taskName: String, taskClass: Class<T>) :
    TaskCreationAction<T>() {
    override val name: String = taskName
    override val type: Class<T> = taskClass

    override fun configure(task: T) {
        with(task) {
            // Базовая конфигурация Task'и
            baseTaskConfigure()

            // Установка зависимостей Task'и
            setupDependency().invoke(task)
        }
    }

    open fun setupDependency(): (T: Task) -> Task = { it }
}

private fun Task.baseTaskConfigure() {
    // Устанавливаем группу для задачи
    group = AarGeneratorPlugin.AAR_GENERATOR_PLUGIN_TASK_GROUP
}