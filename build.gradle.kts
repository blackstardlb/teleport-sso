import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "3.0.4"
    id("io.spring.dependency-management") version "1.1.0"
    id("com.google.cloud.tools.jib") version "3.3.1"
    kotlin("jvm") version "1.7.22"
    kotlin("plugin.spring") version "1.7.22"
}

group = "nl.blackstardlb"
version = "0.0.1"
java.sourceCompatibility = JavaVersion.VERSION_17

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("com.google.guava:guava:31.1-jre")
    api("com.google.guava:guava:31.1-jre")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-csv")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jboss.aerogear:aerogear-otp-java:1.0.0")
    implementation("io.github.microutils:kotlin-logging-jvm:2.0.11")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.security:spring-security-test")
    testImplementation("io.rest-assured:rest-assured:5.3.0")
    testImplementation("org.hamcrest:hamcrest-all:1.3")
}

jib {
    container {
        jvmFlags = listOf("-Xms512m", "-Xdebug", "-Dspring.profiles.active=prod")
        mainClass = "nl.blackstardlb.sso.SsoApplicationKt"
        args = listOf()
        ports = listOf("8080/tcp")
    }
    to {
        image = "ghcr.io/blackstardlb/teleport-sso"
        tags = setOf("latest", "${project.version}")
    }
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "17"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}
