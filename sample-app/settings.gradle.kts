import java.net.URI
import java.util.Properties

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

@Suppress("UnstableApiUsage") dependencyResolutionManagement {
    repositoriesMode = RepositoriesMode.FAIL_ON_PROJECT_REPOS
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
        maven("https://jitpack.io")
        mavenLocal()

        /**
         * This enables the sample-app to be used to test unpublished versions of the SDK.
         * It is only needed for the sample-app and should not be included in your project.
         */
        if (gradle.parent == null) {
            val mavenCentralDeploymentVersion =
                findOptionalLocalProperty("maven.central.deployment.version")
            if (mavenCentralDeploymentVersion != null) {
                val mavenCentralBearerToken =
                    findOptionalLocalProperty("maven.central.deployment.bearer.token")!!
                maven {
                    name = "mavenCentralDeploymentTesting"
                    url = URI("https://central.sonatype.com/api/v1/publisher/deployments/download/")
                    credentials(HttpHeaderCredentials::class.java) {
                        name = "Authorization"
                        value = "Bearer $mavenCentralBearerToken"
                    }
                    authentication {
                        create<HttpHeaderAuthentication>("header")
                    }
                    content {
                        @Suppress("UnstableApiUsage") includeGroupAndSubgroups(
                            providers.gradleProperty("project.group").get()
                        )
                    }
                }
            }
        }
    }
}

rootProject.name = "cheq-sst-kotlin-sample-app"
include(":app")

private fun findOptionalLocalProperty(propertyName: String): String? =
    properties(rootDir, "local.properties").getProperty(propertyName)

private fun properties(rootDir: File?, name: String): Properties {
    return rootDir?.resolve(name)?.let {
        when {
            it.exists() -> Properties().apply { it.reader().use(::load) }
            else -> properties(rootDir.parentFile, name)
        }
    } ?: Properties()
}
