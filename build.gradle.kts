plugins {
    // Plugin para aplicaciones Android
    alias(libs.plugins.android.application) apply false
    // Plugin de Google Services (Firebase)
    id("org.jetbrains.kotlin.android") version "2.1.0" apply false // Match the Kotlin version
    id("com.google.gms.google-services") version "4.3.15" apply false
    id("org.jetbrains.kotlin.kapt") version "2.1.0" apply false
}
