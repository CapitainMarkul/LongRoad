package ru.aar_generator.plugin.config

import ru.aar_generator.plugin.config.option.subproject.ApplyForAllSubProjectOption
import ru.aar_generator.plugin.config.option.subproject.ApplyForAllSubProjectOptionApi
import ru.aar_generator.plugin.config.option.variant.VariantOption
import ru.aar_generator.plugin.config.option.variant.VariantOptionApi

/*** Класс отвечает за работу с конфигурацией плагина */
open class PluginConfigurator : PluginConfiguratorApi {

    /*** Файл конфигурации плагина */
    class Config :
        VariantOptionApi.Variable,
        ApplyForAllSubProjectOptionApi.Variable {
        override var targetPlatform: VariantOption.Platform? = null
        override var applyForAllSubProjects: Boolean = true
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

    override val applyForAllSubProjectOptionApi: ApplyForAllSubProjectOptionApi =
        ApplyForAllSubProjectOption(currentPluginConfig)
}