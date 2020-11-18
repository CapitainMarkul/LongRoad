package ru.aar_generator.plugin.utils

import org.gradle.api.Project
import org.gradle.api.publish.PublicationContainer
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import ru.aar_generator.plugin.config.PluginConfigurator

/*** Проверка подключенного плагина */
fun Project.hasPlugin(pluginName: String) =
    pluginManager.hasPlugin(pluginName)

internal fun Project.findMandatoryProperty(propertyName: String): String {
    val value = this.findOptionalProperty(propertyName)
    return requireNotNull(value) { "Please define \"$propertyName\" in your gradle.properties file" }
}

internal fun Project.findOptionalProperty(propertyName: String) = findProperty(propertyName)?.toString()

/*** ================== Extensions для быстрого доступа к полям ================== */

/*** Быстрый доступ к PublishingExtension */
internal inline val Project.publishing: PublishingExtension
    get() = extensions.getByType(PublishingExtension::class.java)

/*** Быстрый доступ к PublicationContainer */
internal inline val Project.publications: PublicationContainer
    get() = publishing.publications

/*** Быстрый доступ к созданию объекта MavenPublication */
fun Project.createMavenPublication(publicationName: String, configuration: (MavenPublication) -> Unit) =
    publications.create(publicationName, MavenPublication::class.java, configuration)

/*** Быстрый доступ к чтению объекта MavenPublication */
fun Project.getMavenPublication(publicationName: String): MavenPublication =
    publications.getByName(publicationName) as? MavenPublication
        ?: throw Exception("Publication '$publicationName' is not MavenPublish")

/*** Быстрый доступ к MavenLocal url */
fun Project.mavenLocalUrl() =
    repositories.mavenLocal().url.toASCIIString()

/*** Быстрый доступ к PluginConfig */
internal inline val Project.publishExtension: PluginConfigurator.Config
    get() = project.extensions.getByType(PluginConfigurator.Config::class.java)