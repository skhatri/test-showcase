plugins {
    java
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.2"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("com.intuit.karate:karate-junit5:1.4.1")
}

tasks.test {
    useJUnitPlatform()
    systemProperty("karate.options", System.getProperty("karate.options") ?: "")
}

