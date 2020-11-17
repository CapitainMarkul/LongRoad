//package ru.aar_generator.plugin.config.option.logging
//
///** Интерфейс для объявления доступных настроек для конфигурации уровня логирования. */
//interface LoggingLevelOptionApi {
//
//    /*** Логирование плагина отключено */
//    fun configureNoneLoggingLevel()
//
//    /*** Логирование только важных сообщений плагина */
//    fun configureImportantLoggingLevel()
//
//    /*** Логирование всех сообщений плагина */
//    fun configureAllLoggingLevel()
//
//    /*** Уровни логирования */
//    enum class LoggingLevel { NONE, IMPORTANT, ALL }
//
//    /*** Переменная для изменения */
//    interface Variable {
//        var loggingLevel: LoggingLevel?
//    }
//}