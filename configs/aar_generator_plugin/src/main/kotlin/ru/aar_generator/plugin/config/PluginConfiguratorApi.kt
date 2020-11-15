package ru.aar_generator.plugin.config

import ru.aar_generator.plugin.config.option.subproject.ApplyForAllSubProjectOptionApi
import ru.aar_generator.plugin.config.option.variant.VariantOptionApi

/*** Интерфейс для объявления доступных настроек для конфигурации плагина */
interface PluginConfiguratorApi {
    val variantOptionApi: VariantOptionApi
    val applyForAllSubProjectOptionApi: ApplyForAllSubProjectOptionApi
}