plugins {
    kotlin("jvm")
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

    implementation(project(":sharedkernel"))
}

kotlin {
    jvmToolchain(25)
}

tasks.test {
    useJUnitPlatform()
}