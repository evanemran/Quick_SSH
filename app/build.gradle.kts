plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id ("kotlin-kapt")
}

android {
    namespace = "com.evanemran.quickssh"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.evanemran.quickssh"
        minSdk = 28
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildFeatures {
        viewBinding = true
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

    android {
        packagingOptions {
            resources.excludes.add("META-INF/DEPENDENCIES")
        }
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {

    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    // Adding implementations required for apache mina library

    implementation ("org.apache.mina:mina-core:3.0.0-M2") {
        exclude(group = "META-INF", module = "DEPENDENCIES")
    }

    implementation ("org.apache.sshd:sshd-core:2.1.0") {
        exclude(group = "META-INF", module = "DEPENDENCIES")
    }

    implementation ("org.apache.sshd:sshd-putty:2.1.0") {
        exclude(group = "META-INF", module = "DEPENDENCIES")
    }

    implementation ("org.apache.sshd:sshd-common:2.1.0") {
        exclude(group = "META-INF", module = "DEPENDENCIES")
    }

    implementation ("androidx.room:room-runtime:2.4.3")
    kapt ("androidx.room:room-compiler:2.4.3")

    implementation ("org.slf4j:slf4j-api:1.7.5")

    implementation ("org.slf4j:slf4j-simple:1.6.4")
}