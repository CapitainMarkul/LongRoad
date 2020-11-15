package ru.aar_generator.gradle

import com.android.build.gradle.LibraryExtension
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.jvm.tasks.Jar
import ru.aar_generator.log.PluginLogger
import ru.aar_generator.plugin.utils.publishing
import java.net.URI

internal class AarPublishConfigurer(
    private val project: Project/*,
    private val publishPom: MavenPublishPom*/
) {

    private fun configurePom(
        publication: MavenPublication,
        groupId: String = project.group as String, // The plugin initially sets project.group to publishPom.groupId
        artifactId: String = project.name,
        version: String = project.version as String
    ) {
        publication.groupId = groupId
        publication.artifactId = artifactId
        publication.version = version

        @Suppress("UnstableApiUsage")
        publication.pom { pom ->

            pom.name.set("publishPom.name")
            pom.description.set("publishPom.description")
            pom.url.set("publishPom.url")
            pom.inceptionYear.set("publishPom.inceptionYear")

            pom.scm {
                it.url.set("publishPom.scmUrl")
                it.connection.set("publishPom.scmConnection")
                it.developerConnection.set("publishPom.scmDeveloperConnection")
            }

            pom.licenses { licenses ->
                licenses.license {
                    it.name.set("publishPom.licenseName")
                    it.url.set("publishPom.licenseUrl")
                    it.distribution.set("publishPom.licenseDistribution")
                }
            }

            pom.developers { developers ->
                developers.developer {
                    it.id.set("publishPom.developerId")
                    it.name.set("publishPom.developerName")
                    it.url.set("publishPom.developerUrl")
                }
            }
        }
    }

    fun configureTarget(target: MavenPublishTarget) {
        project.publishing.repositories.maven { repo ->
            repo.name = "local"
            repo.url = target.repositoryUrl(project.version.toString())
            if (target.repositoryUsername != null) {
                repo.credentials {
                    it.username = target.repositoryUsername
                    it.password = target.repositoryPassword
                }
            }
        }

        // create task that depends on new publishing task for compatibility and easier switching
        project.tasks.register(target.name) { task ->
            project.publishing.publications.all { publication ->
                val publishTaskName = "publish${publication.name.capitalize()}Publication" +
                        "To${target.repositoryName.capitalize()}Repository"
                task.dependsOn(project.tasks.named(publishTaskName))
            }
        }
    }

    private val MavenPublishTarget.repositoryName get(): String {
        return "local" /*when (name) {
            DEFAULT_TARGET -> "maven"
            LOCAL_TARGET -> "local"
            else -> name
        }*/
    }

    private fun MavenPublishTarget.repositoryUrl(version: String): URI {
        val url = if (version.endsWith("SNAPSHOT")) {
            snapshotRepositoryUrl ?: releaseRepositoryUrl
        } else {
            releaseRepositoryUrl
        }
        return URI.create(requireNotNull(url))
    }

    fun configureAndroidArtifacts() {
        val publications = project.publishing.publications
        publications.create(PUBLICATION_NAME, MavenPublication::class.java) { publication ->
            configurePom(publication)
        }

        val publication = project.publishing.publications.getByName(PUBLICATION_NAME) as MavenPublication

        publication.from(project.components.getByName("debug"/*project.publishExtension.androidVariantToPublish*/))

        val androidSourcesJar = project.tasks.register("androidSourcesJar", AndroidSourcesJarTask::class.java)
        publication.artifact(androidSourcesJar)
    }

    companion object {
        const val PUBLICATION_NAME = "maven"
    }
}

@Suppress("UnstableApiUsage")
open class AndroidSourcesJarTask : Jar() {

    init {
        archiveClassifier.set("sources")

        val androidExtension = project.extensions.getByType(LibraryExtension::class.java)
        from(androidExtension.sourceSets.getByName("main").java.srcDirs)
    }
}