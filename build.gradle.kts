// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
}

buildscript {
    repositories {
        google()  // Google repository (for Firebase and other Google services)
        mavenCentral()  // Maven Central repository
    }

    dependencies {
        // Add Google Services plugin with the older working version
        classpath("com.google.gms:google-services:4.3.15")
    }
}
