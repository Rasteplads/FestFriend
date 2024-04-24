include(":eventmesh")
project(":eventmesh").projectDir = File("./submodules/event-mesh-protocol")
include(":eventmeshandroid")
project(":eventmeshandroid").projectDir = File("./submodules/event-mesh-android")

pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    //repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}


rootProject.name = "FestFriend"
include(":app")
