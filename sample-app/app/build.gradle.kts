import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.kotlin.parcelize)
}

dependencies {
    sdkImplementation(this, libs.cheq.sst.kotlin.core)
    sdkImplementation(this, libs.cheq.sst.kotlin.advertising)
    implementation(libs.androidx.appcompat)
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

    compileSdk = 34

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
            targetSdk = 34
            minSdk = 34
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

val mavenCentralDeploymentVersion =
    findOptionalLocalProperty("maven.central.deployment.version")
private fun Project.sdkImplementation(scope: DependencyHandlerScope, dependency: Provider<MinimalExternalModuleDependency>) {
    scope.implementation(dependency.get().let {
        var resolvedVersion = it.version
        if (this.gradle.parent == null && !mavenCentralDeploymentVersion.isNullOrEmpty()) {
            resolvedVersion = mavenCentralDeploymentVersion
            println("=====> Substituting ${it.group}:${it.name}:${it.version} with ${it.group}:${it.name}:$resolvedVersion")
        }
        "${it.group}:${it.name}:$resolvedVersion"
    })
}

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
