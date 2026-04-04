plugins {
    kotlin("jvm")
    alias(libs.plugins.kotlin.plugin.serialization)
}

group = "com.example"
version = "0.0.1"

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":sharedkernel"))

    // Ktor
    implementation(libs.ktor.server.core)
    implementation(libs.ktor.server.content.negotiation)
    implementation(libs.ktor.serialization.kotlinx.json)
    
    // Koin
    implementation(libs.koin.core)

    // Tests
    testImplementation(kotlin("test"))
    testImplementation(libs.ktor.server.test.host)
}

kotlin {
    jvmToolchain(25)
}

tasks.test {
    useJUnitPlatform()
}