import io.ktor.plugin.features.DockerPortMapping

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.ktor)
    alias(libs.plugins.kotlin.plugin.serialization)
}

group = "com.example"
version = "0.0.1"

application {
    mainClass = "com.example.ApplicationKt"
}

val javaVersion = JavaVersion.VERSION_21

kotlin {
    jvmToolchain(javaVersion.majorVersion.toInt())
}

dependencies {
    // Default
    implementation(libs.koin.ktor)
    implementation(libs.koin.logger.slf4j)
    implementation(libs.ktor.server.core)
    implementation(libs.ktor.serialization.kotlinx.json)
    implementation(libs.ktor.server.content.negotiation)
    implementation(libs.ktor.call.logging)
    implementation(libs.ktor.rate.limit)
    implementation(libs.exposed.core)
    implementation(libs.exposed.r2dbc)
    implementation(libs.r2dbc.postgresql)
    implementation(libs.ktor.server.request.validation)
    implementation(libs.ktor.server.auth)
    implementation(libs.ktor.server.auth.jwt)
    implementation(libs.ktor.server.cors)
    implementation(libs.ktor.server.netty)
    implementation(libs.logback.classic)
    implementation(libs.ktor.server.config.yaml)
    testImplementation(libs.ktor.server.test.host)
    testImplementation(libs.kotlin.test.junit)


    // Added
    implementation(libs.ktor.status.pages)
    implementation(project(":sharedkernel"))
    implementation(project(":finance"))
    implementation(project(":iam"))
}

ktor {
    docker {
        jreVersion.set(javaVersion)
        localImageName.set("zorvyn-assignment")
    }
}