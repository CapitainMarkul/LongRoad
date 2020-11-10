package ru.aar_generator_plugin.gradle

data class MavenPublishTarget(
  internal val name: String,
  /**
   * The release repository url this should be published to.
   * @since 0.7.0
   */
  var releaseRepositoryUrl: String? = null,

  /**
   * The snapshot repository url this should be published to.
   * @since 0.7.0
   */
  var snapshotRepositoryUrl: String? = null,

  /**
   * The username that should be used for publishing.
   * @since 0.7.0
   */
  var repositoryUsername: String? = null,

  /**
   * The password that should be used for publishing.
   * @since 0.7.0
   */
  var repositoryPassword: String? = null,

  /**
   * Whether release artifacts should be signed before uploading to this target.
   * @since 0.7.0
   */
  @Deprecated("Disabling signing on a target level is not supported anymore. See releaseSigningEnabled for a replacement")
  var signing: Boolean = true
) {
}
