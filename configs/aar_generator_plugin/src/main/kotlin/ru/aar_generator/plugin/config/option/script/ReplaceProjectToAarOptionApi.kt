package ru.aar_generator.plugin.config.option.script

/**
 *  Интерфейс для объявления доступных настроек для конфигурации
 *  необходимости запуска скрипта для замены всех вхождений:
 *
 *  # implementation project(':project')
 *  # implementation project( ':project')
 *  # implementation project(":project")
 *  # implementation project(path: ':project')
 *
 *  # api project(':project')
 *  # api project( ':project')
 *  # api project(":project")
 *  # api project(path: ':project')
 *
 *  # testImplementation project(':project')
 *  # testImplementation project( ':project')
 *  # testImplementation project(":project")
 *  # testImplementation project(path: ':project')
 *
 *  ---> на implementation 'ru.aar_generator:$library_name:0.0.1'
 *
 *  Скрипт запускается после публикации всех проектов в Maven.
 **/
interface ReplaceProjectToAarOptionApi {

    /**
     * Скрипт будет запущен автоматически после публикации всех проектов в Maven.
     * */
    fun enable()

    /**
     * Скрипт НЕ будет запущен автоматически после публикации всех проектов в Maven.
     * */
    fun disable()

    /*** Переменная для изменения */
    interface Variable {
        var needRunReplaceProjectToAarScript: Boolean
    }
}