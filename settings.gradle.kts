pluginManagement {
    repositories {
        google()
        jcenter()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        maven { url = uri("https://jitpack.io") } // Use uri() to specify the repository URL
        google()
        jcenter()
    }
}

rootProject.name = "Weather Forecast"
include(":app")
 