plugins {
    id("java")
    id("application")
}

group = "com.durable"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

dependencies {
    // Application dependencies
    implementation("com.typesafe:config:1.4.3")
    implementation("com.zaxxer:HikariCP:7.1.0")
    implementation("org.postgresql:postgresql:42.7.7")
    implementation("org.flywaydb:flyway-core:11.13.1")
    implementation("org.flywaydb:flyway-database-postgresql:11.13.1")
    implementation("io.javalin:javalin:6.7.0")

    // JUnit 5: runs your tests
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")

    // Testcontainers: starts PostgreSQL for integration tests
    testImplementation("org.testcontainers:postgresql:1.20.6")
    testImplementation("org.testcontainers:junit-jupiter:1.20.6")

    // Allows Gradle to launch the JUnit test platform
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}


application {
    mainClass.set("com.durable.Main")
}

tasks.named<JavaExec>("run") {
    systemProperty("config.resource", System.getProperty("config.resource"))
}

tasks.test {
    useJUnitPlatform()
}