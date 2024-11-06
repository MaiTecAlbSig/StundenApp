
plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.ktor)
    kotlin("plugin.serialization") version "1.5.31" // oder deine aktuelle Kotlin-Version

}

group = "com.example"
version = "0.0.1"

application {
    mainClass.set("com.example.ApplicationKt")

    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

repositories {
    mavenCentral()

}

dependencies {
    implementation(libs.ktor.server.auth)
    implementation(libs.ktor.server.core)
    implementation(libs.ktor.server.content.negotiation)
    implementation(libs.ktor.serialization.jackson)
    implementation(libs.ktor.server.netty)
    implementation(libs.logback.classic)
    implementation(libs.ktor.server.config.yaml)
    testImplementation(libs.ktor.server.test.host)
    testImplementation(libs.kotlin.test.junit)
    implementation("io.ktor:ktor-server-core:2.0.0") // Deine Ktor-Abhängigkeiten
    implementation("io.ktor:ktor-server-netty:2.0.0")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.13.1")

    implementation("org.postgresql:postgresql:42.7.2") // PostgreSQL-Treiber
    implementation("org.jetbrains.exposed:exposed-core:0.41.1")
    implementation("org.jetbrains.exposed:exposed-dao:0.41.1")
    implementation("org.jetbrains.exposed:exposed-jdbc:0.41.1")

    implementation("io.ktor:ktor-server-host-common:2.0.0")
    implementation("io.ktor:ktor-server-call-logging:2.0.0")
    implementation("io.ktor:ktor-server-status-pages:2.0.0")
    implementation("io.ktor:ktor-server-content-negotiation:2.0.0")
    implementation("ch.qos.logback:logback-classic:1.4.12") // Für Logging


    testImplementation("io.ktor:ktor-server-tests:2.0.0") // Abhängigkeit für Ktor-Tests
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:1.5.31") // Abhängigkeit für JUnit-Tests

    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.2") // Aktuelle JSON-Serializer-Version


    implementation("io.ktor:ktor-server-content-negotiation:2.0.0") // Passe die Version an
    implementation("io.ktor:ktor-serialization-kotlinx-json:2.0.0") // Passe die Version an

    // JWT-Bibliothek hinzufügen
    implementation("com.auth0:java-jwt:4.0.0") // Aktuelle Version prüfen und ggf. anpassen

    implementation("org.mindrot:jbcrypt:0.4")

    implementation("io.ktor:ktor-server-auth:2.0.0") // JWT-Authentifizierung für Ktor
    implementation("io.ktor:ktor-server-auth-jwt:2.0.0") // JWT-Spezifische Klassen
    implementation("com.auth0:java-jwt:3.18.2") // JWT-Bibliothek von Auth0 für die Token-Erstellung

    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.4.0") // Version anpassen

}



