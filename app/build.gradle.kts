import com.android.build.api.dsl.AaptOptions

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.example.mldetectliveobjects"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.mldetectliveobjects"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {

            androidResources {
                ignoreAssetsPattern="tflite"
            }

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

   /* aaptOptions {
        noCompress.add( "tflite")
    }*/
    dataBinding{
        enable=true
    }
    sourceSets {
        getByName("main") {
            assets {
                srcDirs("src/main/assets", "src/main/assets/2")
            }
        }
    }
}

dependencies {

    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")




    // Object detection feature with bundled default classifier
   // implementation ("com.google.mlkit:object-detection:17.0.0")

    // Object detection feature with custom classifier support
    implementation ("com.google.mlkit:object-detection-custom:16.3.0")

    // CameraX
    implementation ("androidx.camera:camera-camera2:1.4.0-alpha03")
    implementation ("androidx.camera:camera-lifecycle:1.4.0-alpha03")
    implementation ("androidx.camera:camera-view:1.4.0-alpha03")


    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}