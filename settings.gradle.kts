pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
    }

    repositoriesMode = RepositoriesMode.PREFER_SETTINGS
}

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

rootProject.name = "coroutines-tutorial-project"
include(":desktop-client")
include(":server")
include("shared")
