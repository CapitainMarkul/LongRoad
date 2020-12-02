package ru.aar_generator.plugin.publish

import org.gradle.api.Project
import org.gradle.api.publish.maven.MavenPublication
import ru.aar_generator.plugin.config.option.variant.VariantOptionApi
import ru.aar_generator.plugin.utils.*
import java.net.URI

class PublishConfigurator(
    private val project: Project
) {

    companion object {
        private const val PUBLICATION_NAME = "maven"

        private const val GENERATE_ANDROID_SOURCE_JAR_TASK = "androidSourcesJar"

        private const val DEBUG_BUILD_TYPE_PREFFIX = "debug"
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
    fun configureAndroidArtifacts(buildVariant: VariantOptionApi.Platform?) {
        if (buildVariant == null) throw Exception("Need configure 'variantOptionApi' params!")

        project.createMavenPublication(PUBLICATION_NAME) { publication ->
            configureFinalPom(publication)
        }

        with(project.getMavenPublication(PUBLICATION_NAME)) {
            /* Формируем строку типа 'x86_64Debug' */
            val buildTypeName =
                if (buildVariant == VariantOptionApi.Platform.DEBUG) buildVariant.platformName
                else "${buildVariant.platformName}${DEBUG_BUILD_TYPE_PREFFIX.capitalize()}"

            try {
                from(project.components.getByName(buildTypeName))
            } catch (e: Exception) {
                /* Некоторые модули имеют только Debug/Release build variants,
                 * для них формируем строку 'debug' */
                from(project.components.getByName("debug"))
            }

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