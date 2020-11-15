package ru.aar_generator_plugin.plugin.task.base

private const val CAPITALIZE_PARAM = "%s"

/*** Gradle Task'a - сборка Debug .aar файлов для проекта | TODO() УДАЛИТЬ! */
const val TASK_BUNDLE_DEBUG_AAR_FOR_TEST = "bundle${CAPITALIZE_PARAM}Aar"

// ================= Plugins ================= //
/*** Gradle Plugin - android library */
const val PLUGIN_ANDROID_LIBRARY = "com.android.library"

/*** Gradle Plugin - maven publish */
const val PLUGIN_MAVEN_PUBLISH = "maven-publish"

// ================= Tasks =================== //

/*** Gradle Task'a - чистка проекта */
const val TASK_CLEAN_PROJECT = "clean"

/*** Gradle Task'a - сборка Debug .aar файлов для проекта */
const val TASK_BUNDLE_DEBUG_AAR = "bundle${CAPITALIZE_PARAM}DebugAar"

/*** Gradle Task'a - сборка Release .aar файлов для проекта */
const val TASK_BUNDLE_RELEASE_AAR = "bundle${CAPITALIZE_PARAM}ReleaseAar"