plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt")
    id("com.google.dagger.hilt.android")
    id("org.sonarqube") version "3.5.0.2730"
}

android {
    compileSdk = 34
    namespace = "org.liamjd.amber"
    defaultConfig {
        applicationId = "org.liamjd.amber"
        minSdk = 31
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
        kapt {
            arguments {
                arg("room.schemaLocation","$projectDir/schemas")
            }
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
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.3"
    }
    packagingOptions {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

    val composeBom = platform("androidx.compose:compose-bom:2022.10.00")
    implementation("androidx.compose:compose-bom:2023.10.01")
    androidTestImplementation("androidx.compose:compose-bom:2023.10.01")

    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.2")
    implementation("androidx.activity:activity-compose:1.8.0")
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3:1.2.0-alpha10")
    implementation("androidx.navigation:navigation-compose:2.7.5")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.6.2")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.6.2")
    implementation("androidx.compose.runtime:runtime-livedata")
    implementation("androidx.compose.material:material-icons-extended")

    // images
    implementation("androidx.activity:activity:1.8.0")
    implementation("io.coil-kt:coil-compose:2.5.0")

    // Hilt DI
    implementation("com.google.dagger:hilt-android:2.48.1")
    kapt("com.google.dagger:hilt-compiler:2.48.1")

    // room DB
    implementation("androidx.room:room-ktx:2.6.0")
    implementation("androidx.room:room-common:2.6.0")
    kapt("androidx.room:room-compiler:2.6.0")

    //testing
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
//    androidTestImplementation "androidx.compose.ui:ui-test-junit4:$compose_version"
    testImplementation("androidx.room:room-testing:2.6.0")
    debugImplementation("androidx.compose.ui:ui-tooling")
//    debugImplementation "androidx.compose.ui:ui-test-manifest:$compose_version"


}

// Allow references to generated code
kapt {
    correctErrorTypes = true
}
