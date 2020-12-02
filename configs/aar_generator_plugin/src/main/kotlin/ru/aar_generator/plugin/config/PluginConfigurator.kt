package ru.aar_generator.plugin.config

import ru.aar_generator.plugin.config.option.script.ReplaceProjectToAarOption
import ru.aar_generator.plugin.config.option.script.ReplaceProjectToAarOptionApi
import ru.aar_generator.plugin.config.option.variant.VariantOption
import ru.aar_generator.plugin.config.option.variant.VariantOptionApi

/*** Класс отвечает за работу с конфигурацией плагина */
open class PluginConfigurator : PluginConfiguratorApi {

    /*** Файл конфигурации плагина */
    class Config :
        VariantOptionApi.Variable,
        ReplaceProjectToAarOptionApi.Variable {

        override var targetPlatform: VariantOptionApi.Platform = VariantOptionApi.Platform.MULTI
        override var needRunReplaceProjectToAarScript: Boolean = false

        fun createConfigurationLog(): String {
            return StringBuilder().apply {
                append(createConfigurationLog("targetPlatform", targetPlatform))
                append(createConfigurationLog(
                    "needRunReplaceProjectToAarScript", needRunReplaceProjectToAarScript
                ))
            }.toString()
        }

        private fun createConfigurationLog(name: String, value: Any?) = "* $name -> $value\n"
    }

    private var currentPluginConfig = Config()

    /*** Метод для получения текущей конфигурации */
    fun setCurrentConfiguration(newConfig: Config) {
        currentPluginConfig = newConfig
    }

    /*** Метод для получения текущей конфигурации */
    fun getCurrentConfiguration() = currentPluginConfig

    /*** ====== Api Options ====== */
    override val variantOptionApi: VariantOptionApi =
        VariantOption(currentPluginConfig)

    override val replaceProjectToAarOptionApi: ReplaceProjectToAarOptionApi =
        ReplaceProjectToAarOption(currentPluginConfig)
}