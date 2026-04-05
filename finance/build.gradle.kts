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
    testImplementation(kotlin("test"))

    implementation(libs.ktor.server.core)
    implementation(libs.koin.ktor)
    implementation(libs.ktor.serialization.kotlinx.json)
    implementation(libs.ktor.status.pages)
    implementation(libs.ktor.server.auth)
    implementation(libs.ktor.server.auth.jwt)
    implementation(libs.exposed.core)
    implementation(libs.exposed.r2dbc)
    implementation(libs.ktor.server.request.validation)
    implementation(libs.kotlinx.datetime)
    implementation(libs.exposed.datetime)


    implementation(project(":sharedkernel"))
}

kotlin {
    jvmToolchain(25)
}

tasks.test {
    useJUnitPlatform()
}