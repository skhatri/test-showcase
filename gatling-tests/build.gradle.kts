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
    implementation(project(":java-lib"))
    implementation("io.gatling.highcharts:gatling-charts-highcharts:3.11.5")
    implementation("io.gatling:gatling-app:3.11.5")
}

// Remove the old server source directory (server is now in java-lib)
sourceSets {
    main {
        java {
            srcDirs.removeIf { it.toString().contains("com/example/server") }
        }
    }
}
