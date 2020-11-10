package ru.aar_generator_plugin.gradle

import org.gradle.api.Project
import ru.aar_generator_plugin.gradle.util.findMandatoryProperty
import ru.aar_generator_plugin.gradle.util.findOptionalProperty

internal data class MavenPublishPom(
  val groupId: String,
  val artifactId: String,
  val version: String,

  val name: String?,
  val packaging: String?,
  val description: String?,
  val url: String?,
  val inceptionYear: String?,

  val scmUrl: String?,
  val scmConnection: String?,
  val scmDeveloperConnection: String?,

  val licenseName: String?,
  val licenseUrl: String?,
  val licenseDistribution: String?,

  val developerId: String?,
  val developerName: String?,
  val developerUrl: String?
) {

  internal companion object {
    @JvmStatic
    fun fromProject(project: Project) = MavenPublishPom(
        "GROUP",
        "POM_ARTIFACT_ID",
        "VERSION_NAME",
        "POM_NAME",
        "POM_PACKAGING",
        "POM_DESCRIPTION",
        "POM_URL",
        "POM_INCEPTION_YEAR",
        "POM_SCM_URL",
        "POM_SCM_CONNECTION",
        "POM_SCM_DEV_CONNECTION",
        "POM_LICENCE_NAME",
        "POM_LICENCE_URL",
        "POM_LICENCE_DIST",
        "POM_DEVELOPER_ID",
        "POM_DEVELOPER_NAME",
        "POM_DEVELOPER_URL"
//        project.findMandatoryProperty("GROUP"),
//        project.findMandatoryProperty("POM_ARTIFACT_ID"),
//        project.findMandatoryProperty("VERSION_NAME"),
//        project.findOptionalProperty("POM_NAME"),
//        project.findOptionalProperty("POM_PACKAGING"),
//        project.findOptionalProperty("POM_DESCRIPTION"),
//        project.findOptionalProperty("POM_URL"),
//        project.findOptionalProperty("POM_INCEPTION_YEAR"),
//        project.findOptionalProperty("POM_SCM_URL"),
//        project.findOptionalProperty("POM_SCM_CONNECTION"),
//        project.findOptionalProperty("POM_SCM_DEV_CONNECTION"),
//        project.findOptionalProperty("POM_LICENCE_NAME"),
//        project.findOptionalProperty("POM_LICENCE_URL"),
//        project.findOptionalProperty("POM_LICENCE_DIST"),
//        project.findOptionalProperty("POM_DEVELOPER_ID"),
//        project.findOptionalProperty("POM_DEVELOPER_NAME"),
//        project.findOptionalProperty("POM_DEVELOPER_URL")
    )
  }
}
