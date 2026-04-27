pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
        maven { 
            name = "Clojars"
            url = uri("https://repo.clojars.org/") 
        }
    }
}

rootProject.name = "Linkpoint"

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
