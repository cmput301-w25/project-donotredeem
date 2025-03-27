plugins {
    id("com.android.application")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.donotredeem"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.donotredeem"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    implementation ("com.google.maps.android:android-maps-utils:2.2.3")
    implementation("com.github.st235:swipetoactionlayout:1.1.4")
    //firebase
    implementation(platform("com.google.firebase:firebase-bom:33.9.0"))
    implementation("com.google.firebase:firebase-auth:23.2.0")
    implementation("com.google.firebase:firebase-firestore")
    //implementation ("com.google.firebase:firebase-firestore:24.9.1")
    implementation ("com.google.firebase:firebase-storage:21.0.1")
    implementation ("com.google.android.gms:play-services-auth:20.5.0")
    //implementation(libs.protobuf.javalite)
    implementation("com.google.protobuf:protobuf-javalite:3.25.1")
    implementation ("com.google.android.material:material:1.6.1")
    //switch button
    implementation ("com.google.android.material:material:1.11.0")


    //image
    implementation ("com.github.bumptech.glide:glide:4.12.0")
    implementation(libs.espresso.core)
    implementation(libs.fragment)
    testImplementation(libs.ext.junit) // or the latest version
    annotationProcessor ("com.github.bumptech.glide:compiler:4.12.0")


    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    androidTestImplementation("androidx.test:runner:1.5.2")

    androidTestImplementation("androidx.fragment:fragment-testing:1.3.6")
    androidTestImplementation(libs.espresso.contrib){
        exclude(group = "com.google.protobuf", module = "protobuf-lite")
    }
    androidTestImplementation(libs.google.protobuf.javalite.v32112)

    testImplementation("org.mockito:mockito-core:5.7.0")
    testImplementation("org.mockito:mockito-inline:5.2.0")
    implementation("net.bytebuddy:byte-buddy:1.17.1")
    androidTestImplementation("org.mockito:mockito-android:5.7.0")

    implementation("com.google.android.gms:play-services-maps:19.1.0")
    implementation("com.google.android.gms:play-services-location:21.3.0")
    implementation("com.google.android.libraries.places:places:4.1.0")
    implementation("de.hdodenhof:circleimageview:3.1.0")

    implementation(libs.circleimageview)

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.play.services.location)
    implementation(libs.firebase.storage)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)


    // QR Code Generation
    implementation ("com.journeyapps:zxing-android-embedded:4.3.0")


}