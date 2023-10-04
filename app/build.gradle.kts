plugins {
    id("com.android.application")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.celes.imgreco"
    compileSdk = 33

    defaultConfig {
        applicationId = "com.celes.imgreco"
        minSdk = 24
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {

    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.8.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")

    //image labeling
    implementation ("com.google.mlkit:image-labeling:17.0.7")
    //custom image labeling
    implementation ("com.google.mlkit:image-labeling-custom:17.0.1")
    //object detection
    implementation("com.google.mlkit:object-detection:17.0.0")

    //Firebase dependencies
    implementation(platform("com.google.firebase:firebase-bom:32.2.3"))
    implementation("com.google.firebase:firebase-analytics")
    implementation ("com.google.mlkit:linkfirebase:17.0.0")

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}