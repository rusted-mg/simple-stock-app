// Top-level build file where you can add configuration options common to all subprojects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
}

allprojects {
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
    }
}

buildscript {
    repositories {
        google()
    }
    dependencies {
        classpath(libs.navigation.safeArgs)
    }
}