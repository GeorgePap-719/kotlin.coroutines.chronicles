plugins {
    kotlin("jvm") version "2.0.10"
    id("org.jetbrains.kotlinx.atomicfu") version "0.25.0"
}

group = "github.io"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

allprojects {
    tasks.withType<Test> {
        useJUnitPlatform()
        afterSuite(KotlinClosure2({ desc: TestDescriptor, result: TestResult ->
            // Only execute on the outermost suite.
            if (desc.parent == null) {
                println("Tests: ${result.testCount}")
                println("Passed: ${result.successfulTestCount}")
                println("Failed: ${result.failedTestCount}")
                println("Skipped: ${result.skippedTestCount}")
            }
        }))
    }
}