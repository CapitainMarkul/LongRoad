package ru.aar_generator.plugin.config.option.subproject

interface ApplyForAllSubProjectOptionApi {

    /**
     * Плагин применяется ко всем модулям в проекте
     * ВАЖНО: кроме 'root', 'app'
     * */
    fun enable()

    /**
     * Плагин НЕ применяется ко всем модулям в проекте
     * ВАЖНО: плагин должен быть в ручную подключен к необходимым модулям
     * */
    fun disable()

    /*** Переменная для изменения */
    interface Variable {
        var applyForAllSubProjects: Boolean
    }
}