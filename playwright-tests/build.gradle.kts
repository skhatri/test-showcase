import org.gradle.api.tasks.Exec

plugins {
    base
}

val npmInstall = tasks.register<Exec>("npmInstall") {
    workingDir = project.projectDir
    commandLine("npm", "ci", "--no-audit", "--no-fund")
}

val npmClean = tasks.register<Exec>("npmClean") {
    workingDir = project.projectDir
    commandLine("npm", "run", "clean")
}

tasks.register<Exec>("npmTest") {
    dependsOn(npmInstall)
    workingDir = project.projectDir
    commandLine("npm", "test")
}

tasks.register<Exec>("npmTestFast") {
    dependsOn(npmInstall)
    workingDir = project.projectDir
    commandLine("npm", "run", "test:fast")
}

tasks.register("test") {
    dependsOn("npmTest")
}

tasks.register("testFast") {
    dependsOn("npmTestFast")
}

tasks.named("clean") {
    dependsOn(npmClean)
}

