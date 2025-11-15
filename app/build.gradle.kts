plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.devtools.ksp")
    id("com.google.dagger.hilt.android")
    id("org.sonarqube") version "7.0.1.6134"
    id("org.jetbrains.kotlin.plugin.compose") version "2.2.21" // this version matches your Kotlin version
}

android {
    compileSdk = 36
    namespace = "org.liamjd.amber"
    defaultConfig {
        applicationId = "org.liamjd.amber"
        minSdk = 31
        targetSdk = 36
        versionCode = 1
        versionName = "1.2"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.3"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

    implementation(platform("androidx.compose:compose-bom:2025.11.00"))
    androidTestImplementation(platform("androidx.compose:compose-bom:2025.11.00"))

    implementation("androidx.core:core-ktx:1.17.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.9.4")
    implementation("androidx.activity:activity-compose:1.11.0")
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3:1.4.0")
    implementation("androidx.navigation:navigation-compose:2.9.6")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.9.4")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.9.4")
    implementation("androidx.compose.runtime:runtime-livedata")
    implementation("androidx.compose.material:material-icons-extended")

    // images
    implementation("androidx.activity:activity-ktx:1.11.0")
    implementation("io.coil-kt:coil-compose:2.7.0")

    // Hilt DI
    implementation("com.google.dagger:hilt-android:2.57.2")
    ksp("com.google.dagger:hilt-compiler:2.57.2")

    // room DB
    implementation("androidx.room:room-ktx:2.8.3")
    implementation("androidx.room:room-common:2.8.3")
    ksp("androidx.room:room-compiler:2.8.3")

    //testing
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.3.0")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.7.0")
//    androidTestImplementation "androidx.compose.ui:ui-test-junit4:$compose_version"
    testImplementation("androidx.room:room-testing:2.8.3")
    debugImplementation("androidx.compose.ui:ui-tooling")
//    debugImplementation "androidx.compose.ui:ui-test-manifest:$compose_version"
    testImplementation("org.mockito:mockito-core:5.20.0")
    testImplementation("org.mockito.kotlin:mockito-kotlin:6.1.0")

}

// Allow references to generated code
