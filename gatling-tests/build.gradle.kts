plugins {
    java
    id("io.gatling.gradle") version "3.11.5.2"
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

dependencies {
    implementation("io.gatling.highcharts:gatling-charts-highcharts:3.11.5")
    implementation("io.gatling:gatling-app:3.11.5")
}

tasks.register<JavaExec>("runServer") {
    group = "application"
    description = "Run the HTTP server"
    classpath = sourceSets["main"].runtimeClasspath
    mainClass.set("com.example.server.HttpServer")
    standardInput = System.`in`
}
