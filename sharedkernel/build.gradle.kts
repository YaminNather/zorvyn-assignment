plugins {
    kotlin("jvm")

    alias(libs.plugins.ktor)
    alias(libs.plugins.kotlin.plugin.serialization)
}

group = "com.example"
version = "0.0.1"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))

    implementation(libs.ktor.server.core)
    implementation(libs.ktor.serialization.kotlinx.json)
    implementation(libs.koin.core)
    implementation(libs.ktor.server.auth)
    implementation(libs.ktor.server.auth.jwt)
}

kotlin {
    jvmToolchain(25)
}

tasks.test {
    useJUnitPlatform()
}