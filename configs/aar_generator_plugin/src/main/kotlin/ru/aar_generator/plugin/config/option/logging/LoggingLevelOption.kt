//package ru.aar_generator.plugin.config.option.logging
//
//import ru.aar_generator.plugin.config.option.logging.LoggingLevelOptionApi.LoggingLevel
//
//class LoggingLevelOption(
//    private var configPart: LoggingLevelOptionApi.Variable
//) : LoggingLevelOptionApi {
//
//    override fun configureNoneLoggingLevel() {
//        configPart.loggingLevel = LoggingLevel.NONE
//    }
//
//    override fun configureImportantLoggingLevel() {
//        configPart.loggingLevel = LoggingLevel.IMPORTANT
//    }
//
//    override fun configureAllLoggingLevel() {
//        configPart.loggingLevel = LoggingLevel.ALL
//    }
//}