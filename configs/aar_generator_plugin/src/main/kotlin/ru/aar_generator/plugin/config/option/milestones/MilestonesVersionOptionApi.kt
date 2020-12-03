package ru.aar_generator.plugin.config.option.milestones

/**
 *  Интерфейс для объявления доступных настроек для конфигурации версии вехи.
 **/
interface MilestonesVersionOptionApi {
    /**
     * Метод для установки версии вехи.
     * */
    fun setMilestonesVersion(version: String)

    /*** Переменная для изменения */
    interface Variable {
        var milestonesVersion: String
    }
}