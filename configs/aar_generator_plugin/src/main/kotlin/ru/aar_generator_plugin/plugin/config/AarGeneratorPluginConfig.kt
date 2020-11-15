package ru.aar_generator_plugin.plugin.config

/*** Файл конфигурации плагина */
open class PluginConfigurator {

    class AarGeneratorPluginConfig {
        var targetPlatform: PlatformVariants? = null
    }

    private val currentPluginConfig =
        AarGeneratorPluginConfig()

    fun getCurrentConfiguration() = currentPluginConfig

    /* ===== Configure methods ===== */
    fun configureForDebug() {
        currentPluginConfig.targetPlatform =
            PlatformVariants.DEBUG
    }

    fun configureForX86() {
        currentPluginConfig.targetPlatform =
            PlatformVariants.X86_64
    }

    fun configureForArm64() {
        currentPluginConfig.targetPlatform =
            PlatformVariants.ARM_64
    }

    fun configureForArm7A() {
        currentPluginConfig.targetPlatform =
            PlatformVariants.ARM_7A
    }

    fun configureForMulti() {
        currentPluginConfig.targetPlatform =
            PlatformVariants.MULTI
    }

}

enum class PlatformVariants(val platformName: String) {
    DEBUG("Debug"), // TODO() <==== Эту потом удалить
    X86_64("X86_64"),
    ARM_64("Arm64"),
    ARM_7A("Armv7a"),
    MULTI("Mult")
}