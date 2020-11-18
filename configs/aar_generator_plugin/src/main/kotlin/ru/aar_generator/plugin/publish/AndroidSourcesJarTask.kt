package ru.aar_generator.plugin.publish

import com.android.build.gradle.LibraryExtension
import org.gradle.jvm.tasks.Jar

@Suppress("UnstableApiUsage")
open class AndroidSourcesJarTask : Jar() {

    companion object {
        private const val SOURCE_CLASSIFIER = "sources"
        private const val SOURCE_FOLDER = "main"
    }

    init {
        archiveClassifier.set(SOURCE_CLASSIFIER)

        val androidExtension = project.extensions.getByType(LibraryExtension::class.java)
        from(androidExtension.sourceSets.getByName(SOURCE_FOLDER).java.srcDirs)
    }
}