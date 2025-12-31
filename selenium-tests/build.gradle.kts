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
    testImplementation("org.seleniumhq.selenium:selenium-java:4.13.0")
    testImplementation("org.seleniumhq.selenium:htmlunit-driver:4.13.0")
}

tasks.test {
    useJUnitPlatform()
}

