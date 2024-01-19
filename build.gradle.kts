import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "2.7.18"
    id("io.spring.dependency-management") version "1.1.4"
    kotlin("jvm") version "1.9.22"
    kotlin("plugin.spring") version "1.9.22"
    kotlin("plugin.jpa") version "1.9.22"
    application
}

repositories {
    mavenCentral()
}

application {
    mainClass.set("com.tr.candlestick.CandlestickApplicationKt")
}

group = "org.tr.candlesticks"
version = "1.1.4"

object DependencyVersions {
    const val coroutines = "1.7.3"
    const val http4k = "4.34.0.3"
    const val jackson = "2.14.0"
    const val mockk = "1.13.2"
    const val logback = "1.2.11"
    const val kotlinLogging = "3.0.5"
    const val springDocOpenApi = "1.7.0"
    const val shedlock = "4.46.0"
}

dependencies {
    implementation(kotlin("stdlib"))
    testImplementation(kotlin("test"))

    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-data-redis")
    implementation("org.springframework.boot:spring-boot-starter-cache")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-configuration-processor")
    implementation("org.springdoc:springdoc-openapi-ui:${DependencyVersions.springDocOpenApi}")
    implementation("org.postgresql:postgresql")
    implementation("org.flywaydb:flyway-core")

    implementation("net.javacrumbs.shedlock:shedlock-spring:${DependencyVersions.shedlock}")
    implementation("net.javacrumbs.shedlock:shedlock-provider-jdbc-template:${DependencyVersions.shedlock}")

    implementation(platform("org.http4k:http4k-bom:${DependencyVersions.http4k}"))
    implementation("org.http4k:http4k-core")
    implementation("org.http4k:http4k-server-netty")
    implementation("org.http4k:http4k-client-websocket")
    implementation("org.http4k:http4k-format-jackson")

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:${DependencyVersions.coroutines}")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:${DependencyVersions.jackson}")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:${DependencyVersions.jackson}")
    implementation("io.github.microutils:kotlin-logging-jvm:${DependencyVersions.kotlinLogging}")

    testImplementation("io.mockk:mockk:${DependencyVersions.mockk}")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.restdocs:spring-restdocs-restassured")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs += "-Xjsr305=strict"
        jvmTarget = "11"
    }
}

tasks.test {
    useJUnitPlatform()
}
