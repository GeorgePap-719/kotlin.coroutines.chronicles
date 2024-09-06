plugins {
  id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}

rootProject.name = "kotlin.coroutines.chronicles"

fun module(name: String, path: String) {
  include(name)
  val projectDir = rootDir.resolve(path).normalize().absoluteFile
  if (!projectDir.exists()) {
    throw AssertionError("file $projectDir does not exist")
  }
  project(name).projectDir = projectDir
}

module(":kotlin-coroutines-chronicles-codegen", "kotlin-coroutines-chronicles-codegen")
