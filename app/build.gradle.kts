plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.zj.hometest"
    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "com.zj.hometest"
        minSdk = libs.versions.minSdk.get().toInt()
        targetSdk = libs.versions.targetSdk.get().toInt()
        versionCode = libs.versions.appVersionCode.get().toInt()
        versionName = libs.versions.appVersionName.get()
        buildToolsVersion = libs.versions.buildTools.get()
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        debug {

        }
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

dependencies {

    implementation (libs.appCompat)
    implementation (libs.materialDesign)

    implementation (libs.daggerRuntime)
    ksp (libs.daggerCompiler)

    implementation (libs.rxJava)
    implementation (libs.rxAndroid)
    implementation (libs.rxKotlin)

    implementation (libs.okhttpClient)
    implementation (libs.okhttpLoggingInterceptor)
    implementation (libs.retrofitClient)
    implementation (libs.retrofitRxjavaAdapter)

    implementation (libs.conductorRuntime)

    implementation(project(":core"))
    implementation(project(":lifeasandroidenginner"))

    testImplementation (libs.testJunit)
}