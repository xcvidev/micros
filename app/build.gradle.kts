import org.jetbrains.kotlin.konan.properties.Properties
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)

    alias(libs.plugins.ksp)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.xcvi.micros"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.xcvi.micros"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        val localProperties = Properties().apply {
            val localFile = rootProject.file("local.properties")
            if (localFile.exists()) {
                load(localFile.inputStream())
            }
        }

        val openAiKey = localProperties.getProperty("OPENAI_API_KEY")
            ?: throw GradleException("OPENAI_API_KEY not found in local.properties")

        buildConfigField("String", "OPENAI_API_KEY", "\"$openAiKey\"")
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        buildConfig = true
        compose = true
    }
}

dependencies {
    //scanner
    implementation("androidx.camera:camera-camera2:1.3.3")
    implementation("androidx.camera:camera-lifecycle:1.3.3")
    implementation("androidx.camera:camera-view:1.3.3")
    implementation("com.google.mlkit:barcode-scanning:17.2.0")

    //serialization
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.0")

    //room
    implementation("androidx.room:room-runtime:2.7.1")
    implementation("androidx.room:room-ktx:2.7.1")
    ksp("androidx.room:room-compiler:2.7.1")

    // Koin
    implementation("io.insert-koin:koin-android:3.5.3")
    implementation("io.insert-koin:koin-androidx-compose:3.5.3")

    //dateTime
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.6.0")

    //dateTime
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.6.0")

    //ktor
    implementation ("io.ktor:ktor-client-core:1.6.3")
    implementation ("io.ktor:ktor-client-android:1.6.3")
    implementation ("io.ktor:ktor-client-serialization:1.6.3")
    implementation("io.ktor:ktor-utils-jvm:1.6.3")

    // Kotlin stdlib
    implementation ("org.jetbrains.kotlin:kotlin-stdlib:2.1.21")

    //compose
    implementation("androidx.compose.material:material-icons-extended:1.7.8")
    implementation("androidx.compose.material3:material3:1.3.2")
    implementation("androidx.navigation:navigation-compose:2.9.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.9.0")


    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}