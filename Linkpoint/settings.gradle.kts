pluginManagement {
    repositories {
        maven { url = uri("https://maven-central.storage-download.googleapis.com/maven2") }
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        mavenLocal()
        maven {
            url = uri("https://jitpack.io")
            content {
                includeGroupByRegex("com\\.github\\..*")
            }
        }
        maven { url = uri("https://maven-central.storage-download.googleapis.com/maven2") }
        google()
        mavenCentral()
        maven {
            name = "Clojars"
            url = uri("https://repo.clojars.org/")
        }
    }
}

rootProject.name = "Linkpoint"

include(":llsd-core")
project(":llsd-core").projectDir = file("../LLSD-KOTLIN")

/**
 * UI feature-module boundaries for the refactor plan.
 *
 * These are package-level boundaries inside the current single-module Android app.
 * They are consumed by build logic and docs to keep dependency direction explicit.
 */
val uiFeatureBoundaries = mapOf(
    "ui-theme" to listOf("com.linkpoint.ui.theme"),
    "ui-navigation" to listOf("com.linkpoint.ui.navigation"),
    "ui-common-components" to listOf(
        "com.linkpoint.ui.components",
        "com.linkpoint.ui.common",
        "com.linkpoint.ui.dialogs"
    ),
    "ui/chat" to listOf("com.linkpoint.ui.chat"),
    "ui/inventory" to listOf("com.linkpoint.ui.inventory"),
    "ui/world" to listOf("com.linkpoint.ui.world")
)

val uiRuntimeBoundaries = mapOf(
    "forbiddenRuntimePackages" to listOf(
        "com.linkpoint.protocol",
        "com.linkpoint.network",
        "com.linkpoint.render"
    ),
    "allowedInterfaceSegments" to listOf(
        ".api.",
        ".interfaces.",
        ".adapter.",
        ".adapters.",
        ".contract."
    )
)
