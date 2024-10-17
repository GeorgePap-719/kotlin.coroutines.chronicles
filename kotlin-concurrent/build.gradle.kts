plugins {
    kotlin("jvm") version "2.0.10"
    id("org.jetbrains.kotlinx.atomicfu") version "0.25.0"
}

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(17)
}