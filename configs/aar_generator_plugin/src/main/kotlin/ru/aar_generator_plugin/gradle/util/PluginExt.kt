package ru.aar_generator_plugin.gradle.util

import org.gradle.api.Project
import org.gradle.api.publish.PublishingExtension
import ru.aar_generator_plugin.gradle.PluginConfigurator

/*** Проверка подключенного плагина */
fun Project.hasPlugin(pluginName: String) =
    pluginManager.hasPlugin(pluginName)

/*** Быстрый доступ к PublishingExtension */
internal inline val Project.publishing: PublishingExtension
    get() = extensions.getByType(PublishingExtension::class.java)

/*** Быстрый доступ к AarGeneratorPluginConfig */
internal inline val Project.publishExtension: PluginConfigurator.AarGeneratorPluginConfig
    get() = project.extensions.getByType(PluginConfigurator.AarGeneratorPluginConfig::class.java)