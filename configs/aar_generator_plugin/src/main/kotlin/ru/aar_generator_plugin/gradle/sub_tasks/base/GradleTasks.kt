package ru.aar_generator_plugin.gradle.sub_tasks.base

private const val CAPITALIZE_PARAM = "%s"

/*** Gradle Task'a - сборка Debug .aar файлов для проекта | TODO() УДАЛИТЬ! */
const val BUNDLE_DEBUG_AAR_FOR_TEST = "bundle${CAPITALIZE_PARAM}Aar"

// ============================================== //

/*** Gradle Task'a - чистка проекта */
const val CLEAN_PROJECT = "clean"

/*** Gradle Task'a - сборка Debug .aar файлов для проекта */
const val BUNDLE_DEBUG_AAR = "bundle${CAPITALIZE_PARAM}DebugAar"

/*** Gradle Task'a - сборка Release .aar файлов для проекта */
const val BUNDLE_RELEASE_AAR = "bundle${CAPITALIZE_PARAM}ReleaseAar"