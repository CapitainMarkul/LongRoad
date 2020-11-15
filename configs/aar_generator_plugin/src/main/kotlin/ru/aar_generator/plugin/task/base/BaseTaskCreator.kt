package ru.aar_generator.plugin.task.base

import com.android.build.gradle.internal.tasks.factory.TaskCreationAction
import org.gradle.api.Project
import org.gradle.api.Task
import ru.aar_generator.plugin.AarGeneratorPlugin

abstract class BaseTaskCreator<T : Task>(taskName: String, taskClass: Class<T>) :
    TaskCreationAction<T>() {
    override val name: String = taskName
    override val type: Class<T> = taskClass

    override fun configure(task: T) {
        with(task) {
            // 0. Базовая конфигурация Task'и
            baseTaskConfigure()

            // 1. Установка зависимостей от сторонних Task'ок
            setupTaskDependency().invoke(task)
        }
    }

    open fun setupTaskDependency(): (task: Task) -> Task = { it }
}

private fun Task.baseTaskConfigure() {
    // Устанавливаем группу для задачи
    group = AarGeneratorPlugin.AAR_GENERATOR_PLUGIN_TASK_GROUP
}