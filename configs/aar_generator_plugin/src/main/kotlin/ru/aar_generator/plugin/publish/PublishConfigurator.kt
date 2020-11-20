package ru.aar_generator.plugin.publish

import org.gradle.api.Project
import org.gradle.api.publish.maven.MavenPublication
import ru.aar_generator.plugin.utils.*
import ru.aar_generator.plugin.utils.publications
import ru.aar_generator.plugin.utils.publishing
import java.net.URI

class PublishConfigurator(
    private val project: Project
) {

    companion object {
        private const val PUBLICATION_NAME = "maven"

        private const val TYPE_BUILD = "debug"
        private const val GENERATE_ANDROID_SOURCE_JAR_TASK = "androidSourcesJar"

        private const val MAVEN_LOCAL_REPOSITORY_NAME = "local"
        private const val MAVEN_INSTALL_TASK_NAME = "installArchives"
        fun getDefaultLocalMavenPublishTarget(project: Project) =
            LocalMavenPublishTarget(MAVEN_INSTALL_TASK_NAME, project.mavenLocalUrl())
    }

    /*** Класс-обертка с информацией о конечном хранилище */
    data class LocalMavenPublishTarget constructor(
        val taskName: String,
        var repositoryUrl: String? = null
    )

    /*** Конфигурирование локального Maven репозитория */
    fun configureTarget(target: LocalMavenPublishTarget) {
        /* Объявляем локальный Maven репозиторий */
        project.publishing.repositories.maven { repository ->
            repository.name = MAVEN_LOCAL_REPOSITORY_NAME
            repository.url = URI.create(requireNotNull(target.repositoryUrl))
        }

        /* Объявляем task'у для публикации проекта в MavenLocal */
        project.tasks.register(target.taskName) { task ->
            project.publications.all { publication ->
                val publishTaskName = "publish${publication.name.capitalize()}Publication" +
                        "To${MAVEN_LOCAL_REPOSITORY_NAME.capitalize()}Repository"

                /* Зависимость указанной task'и от publishTaskName */
                task.dependsOn(project.tasks.named(publishTaskName))
            }
        }
    }

    /*** Конфигурирование Android Artifact */
    fun configureAndroidArtifacts() {
        project.createMavenPublication(PUBLICATION_NAME) { publication ->
            configureFinalPom(publication)
        }

        with(project.getMavenPublication(PUBLICATION_NAME)) {
            from(project.components.getByName(TYPE_BUILD))

            val androidSourcesJar = project.tasks.register(
                GENERATE_ANDROID_SOURCE_JAR_TASK,
                AndroidSourcesJarTask::class.java
            )
            artifact(androidSourcesJar)
        }
    }

    private fun configureFinalPom(
        publication: MavenPublication,
        groupId: String = project.group as String,
        artifactId: String = project.name,
        version: String = project.version as String
    ) {
        publication.groupId = groupId
        publication.artifactId = artifactId
        publication.version = version

        //TODO: Можно заполнять из gradle.properties
//        publication.pom { pom ->
//            pom.name.set("publishPom.name")
//            pom.description.set("publishPom.description")
//            pom.url.set("publishPom.url")
//            pom.inceptionYear.set("publishPom.inceptionYear")
//            pom.developers { developers ->
//                developers.developer {
//                    it.id.set("publishPom.developerId")
//                    it.name.set("publishPom.developerName")
//                    it.url.set("publishPom.developerUrl")
//                }
//            }
//            pom.scm {
//                it.url.set("publishPom.scmUrl")
//                it.connection.set("publishPom.scmConnection")
//                it.developerConnection.set("publishPom.scmDeveloperConnection")
//            }
//            pom.licenses { licenses ->
//                licenses.license {
//                    it.name.set("publishPom.licenseName")
//                    it.url.set("publishPom.licenseUrl")
//                    it.distribution.set("publishPom.licenseDistribution")
//                }
//            }
//        }
    }
}