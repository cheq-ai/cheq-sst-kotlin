import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.kotlin.parcelize)
}

dependencies {
    implementation(libs.cheq.sst.kotlin.core)
    implementation(libs.cheq.sst.kotlin.advertising)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.core)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.extensions)
    implementation(libs.androidx.lifecycle.runtime.android)
    implementation(libs.json.viewer)
    implementation(libs.jackson.kotlin)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
    coreLibraryDesugaring(libs.android.desugar)
}

android {
    findProperty("project.group")?.toString()?.let { group = it }
    findProperty("project.version")?.toString()?.let { version = it }

    namespace = "${group}.${name.replace("-", ".")}"

    compileSdk = 35

    defaultConfig {
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    compileOptions {
        // Up to Java 11 APIs are available through desugaring
        // https://developer.android.com/studio/write/java11-minimal-support-table
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
        isCoreLibraryDesugaringEnabled = true
    }

    buildFeatures {
        buildConfig = true
        viewBinding = true
    }

    buildTypes {
        defaultConfig {
            applicationId = namespace
            targetSdk = 35
            minSdk = 29
            versionCode = 1
            versionName = "$version"
        }

        debug {
            enableAndroidTestCoverage = true
            enableUnitTestCoverage = true
        }

        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro"
            )
        }
    }

    kotlin {
        compilerOptions {
            jvmTarget = JvmTarget.JVM_11
            allWarningsAsErrors = true
            freeCompilerArgs.add(
                // Enable experimental coroutines APIs, including Flow
                "-opt-in=kotlinx.coroutines.ExperimentalCoroutinesApi",
            )
        }
    }
}

/**
 * This extension method enables the sample-app to be used to test unpublished versions of the SDK.
 * It is only needed for the sample-app and should not be included in your project.
 */
fun DependencyHandlerScope.implementation(dependency: Provider<MinimalExternalModuleDependency>) {
    fun properties(rootDir: File?, name: String): Properties {
        return rootDir?.resolve(name)?.let {
            when {
                it.exists() -> Properties().apply { it.reader().use(::load) }
                else -> properties(rootDir.parentFile, name)
            }
        } ?: Properties()
    }

    fun mavenCentralDeploymentVersion(): String? =
        properties(rootDir, "local.properties").getProperty("maven.central.deployment.version")

    if (project.gradle.parent == null) {
        val mavenCentralDeploymentVersion = mavenCentralDeploymentVersion()
        if (!mavenCentralDeploymentVersion.isNullOrEmpty()) {
            val thisGroup = findProperty("project.group")?.toString()
            with(dependency.get()) {
                if (group == thisGroup) {
                    val substitution = "${group}:${name}:$mavenCentralDeploymentVersion"
                    println("=====> Substituting ${group}:${name}:${version} with $substitution")
                    implementation(substitution)
                    return
                }
            }
        }
    }
    implementation(dependency as Any)
}
