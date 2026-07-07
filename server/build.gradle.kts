plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.ktor)
    alias(libs.plugins.kover)
}

group = "org.example"
version = "0.0.1"
application {
    mainClass.set("org.example.ApplicationKt")

    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

kotlin.jvmToolchain(11)

dependencies {
    implementation(projects.shared)
    implementation("io.ktor:ktor-server-core")
    implementation("io.ktor:ktor-server-content-negotiation")
    implementation("io.ktor:ktor-serialization-kotlinx-json")
    implementation("io.ktor:ktor-server-call-logging")
    implementation("io.ktor:ktor-server-netty")
    implementation(libs.logback.classic)
    implementation(libs.kotlinx.datetime)
    implementation(libs.kotlinx.collections.immutable)
    testImplementation("io.ktor:ktor-server-test-host")
    testImplementation(libs.kotlin.test.junit)
}
