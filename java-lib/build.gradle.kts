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
}

tasks.test {
    useJUnitPlatform()
}

// Print the main runtime classpath to a file so the Makefile can use it
val classpathFile = layout.buildDirectory.file("classpath.txt")

tasks.register("printClasspath") {
    group = "help"
    description = "Print the main runtime classpath to build/classpath.txt"
    doLast {
        val cp = sourceSets["main"].runtimeClasspath.asPath
        classpathFile.get().asFile.writeText(cp)
        println(cp)
    }
}

// Compile main sources (needed before we can resolve the classpath)
tasks.named("printClasspath") {
    dependsOn("classes")
}
