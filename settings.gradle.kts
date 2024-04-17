include(":eventmesh")
project(":eventmesh").projectDir = File("./submodules/event-mesh-protocol")

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

sourceControl {
    gitRepository(uri("https://github.com/Rasteplads/event-mesh-protocol.git")) {
        producesModule("org.rasteplads:api")
    }
}

rootProject.name = "FestFriend"
include(":app")
