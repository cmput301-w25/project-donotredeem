// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.google.gms.google.services) apply false
}
buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath(libs.google.services)
        classpath ("com.google.gms:google-services:4.4.2")
    }
}
configurations.all {
    resolutionStrategy {
        force ("com.google.protobuf:protobuf-javalite:3.25.1")
    }
}
