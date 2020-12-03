package ru.aar_generator.plugin.config

import ru.aar_generator.plugin.config.option.milestones.MilestonesVersionOptionApi
import ru.aar_generator.plugin.config.option.script.ReplaceProjectToAarOptionApi
import ru.aar_generator.plugin.config.option.variant.VariantOptionApi

/*** Интерфейс для объявления доступных настроек для конфигурации плагина */
interface PluginConfiguratorApi {
    val variantOptionApi: VariantOptionApi
    val replaceProjectToAarOptionApi: ReplaceProjectToAarOptionApi
    val milestonesVersionOptionApi: MilestonesVersionOptionApi
}