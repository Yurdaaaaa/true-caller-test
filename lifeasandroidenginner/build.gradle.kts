plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("com.google.devtools.ksp")
}

android {
    namespace = "com.zj.hometest.lifeasandroidengineer"
    compileSdk = libs.versions.compileSdk.get().toInt()


    defaultConfig {
        minSdk = libs.versions.minSdk.get().toInt()
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

dependencies {

    implementation (libs.appCompat)
    implementation (libs.recyclerView)
    implementation (libs.materialDesign)
    implementation (libs.swipeRefreshLayout)

    implementation (libs.daggerRuntime)
    ksp (libs.daggerCompiler)

    implementation (libs.rxJava)
    implementation (libs.rxAndroid)
    implementation (libs.rxKotlin)
    implementation (libs.rxRelay)

    implementation (libs.okhttpClient)
    implementation (libs.okhttpLoggingInterceptor)
    implementation (libs.retrofitClient)
    implementation (libs.retrofitRxjavaAdapter)

    implementation (libs.conductorRuntime)

    implementation(project(":core"))

    testImplementation (libs.testJunit)
}