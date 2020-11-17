package ru.aar_generator.plugin.config.option.variant

import ru.aar_generator.plugin.config.option.PluginConfigOption

/** Интерфейс для объявления доступных настроек для конфигурации Variable-option. */
interface VariantOptionApi : PluginConfigOption {
    /*** Debug Variable */
    fun configureForDebugVariable()

    /*** X86_64 Variable */
    fun configureForX86Variable()

    /*** Arm64 Variable */
    fun configureForArm64Variable()

    /*** Arm7 Variable */
    fun configureForArm7AVariable()

    /*** Multi Variable */
    fun configureForMultiVariable()

    /*** Возможные конфигурации проекта */
    enum class Platform(val platformName: String) {
        DEBUG("Debug"), // TODO() <==== Эту потом удалить
        X86_64("X86_64"),
        ARM_64("Arm64"),
        ARM_7A("Armv7a"),
        MULTI("Multi")
    }

    /*** Переменная для изменения */
    interface Variable {
        var targetPlatform: Platform?
    }
}