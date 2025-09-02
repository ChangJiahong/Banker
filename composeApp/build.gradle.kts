import org.gradle.kotlin.dsl.implementation
import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompilationTask
import kotlin.jvm.java

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.composeHotReload)
    alias(libs.plugins.sqldelight)
    alias(libs.plugins.ksp)
    alias(libs.plugins.serialization)
}

kotlin {
    androidTarget {
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }
    
    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
        }
    }
    
    jvm("desktop")
    
    sourceSets {
        val desktopMain by getting
        
        androidMain.dependencies {
            implementation(compose.preview)
            implementation(libs.androidx.activity.compose)

            implementation("app.cash.sqldelight:android-driver:2.1.0")

        }
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
//            implementation(compose.material)
            implementation(compose.materialIconsExtended)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodel)
            implementation(libs.androidx.lifecycle.runtimeCompose)

            implementation(libs.composeIcons.feather)

            // Navigator
            implementation(libs.bundles.vovager)

            implementation("app.cash.sqldelight:coroutines-extensions:2.1.0")

            implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.5.0") // 最新版

            implementation("com.github.alorma.compose-settings:ui-tiles:2.10.0")
            implementation("com.github.alorma.compose-settings:ui-tiles-extended:2.10.0")

            implementation("io.github.vinceglb:filekit-core:0.10.0")
//            implementation("io.github.vinceglb:filekit-dialogs:0.10.0")
            implementation("io.github.vinceglb:filekit-dialogs-compose:0.10.0")
//            implementation("io.github.vinceglb:filekit-coil:0.10.0")

            implementation(project.dependencies.platform(libs.koin.bom))
            implementation(libs.koin.core)
            implementation(libs.koin.compose)
            // Koin Annotations
            api(libs.koin.annotations)

            implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.9.0")
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
        iosMain.dependencies {
            implementation("app.cash.sqldelight:native-driver:2.1.0")
        }
        desktopMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(libs.kotlinx.coroutinesSwing)

            implementation("com.itextpdf:itext-core:9.2.0")
            implementation("com.itextpdf:bouncy-castle-adapter:9.2.0")
            implementation("org.apache.pdfbox:pdfbox:3.0.5")

            implementation("org.apache.poi:poi-ooxml:5.2.5")
            implementation("com.deepoove:poi-tl:1.12.0")

            implementation("com.alibaba:easyexcel:4.0.3")


            implementation("app.cash.sqldelight:sqlite-driver:2.1.0")
        }
    }
    // KSP Common sourceSet
    sourceSets.named("commonMain").configure {
        kotlin.srcDir("build/generated/ksp/metadata/commonMain/kotlin")
    }
}

// KSP Tasks
dependencies {
    add("kspCommonMainMetadata", libs.koin.ksp.compiler)
    add("kspAndroid", libs.koin.ksp.compiler)
    add("kspIosX64", libs.koin.ksp.compiler)
    add("kspIosArm64", libs.koin.ksp.compiler)
    add("kspIosSimulatorArm64", libs.koin.ksp.compiler)
}

// Trigger Common Metadata Generation from Native tasks
project.tasks.withType(KotlinCompilationTask::class.java).configureEach {
    if(name != "kspCommonMainKotlinMetadata") {
        dependsOn("kspCommonMainKotlinMetadata")
    }
}

android {
    namespace = "cn.changjiahong.banker"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "cn.changjiahong.banker"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    debugImplementation(compose.uiTooling)
}

compose.desktop {
    application {
        mainClass = "cn.changjiahong.banker.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Exe, TargetFormat.Deb)
            packageName = "Banker"
            packageVersion = "1.0.0"

            includeAllModules=true
            jvmArgs += listOf("-Xmx512m")

//            windows{
//                iconFile = project.file("src/commonMain/composeResources/drawable/icon.ico")
//            }
        }
    }
}


sqldelight {
    databases {
        create("BankerDb") {
            packageName.set("cn.changjiahong.banker")
        }
    }
}