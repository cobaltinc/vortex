plugins {
  kotlin("jvm") version "1.6.0"
  id("io.spring.dependency-management") version "1.0.11.RELEASE"
  id("org.jlleitschuh.gradle.ktlint") version "10.0.0"
  `maven-publish`
}

group = "run.cobalt"
version = "0.0.1"
java.sourceCompatibility = JavaVersion.VERSION_11

repositories {
  maven(url = "https://plugins.gradle.org/m2/")
  mavenCentral()
}

ktlint { 
  debug.set(true)
}

tasks.compileKotlin {
  kotlinOptions {
    jvmTarget = "11"
  }
}

tasks.compileTestKotlin {
  kotlinOptions {
    jvmTarget = "11"
  }
}

configure<PublishingExtension> {
  publications {
    register<MavenPublication>("maven") {
      groupId = "$group"
      artifactId = rootProject.name
      version = version

      from(components["java"])
    }
  }
}

dependencyManagement {
  imports {
    mavenBom("org.springframework.boot:spring-boot-dependencies:2.5.6")
  }
}

dependencies {
  compileOnly(kotlin("stdlib-jdk8"))
  compileOnly(kotlin("reflect"))

  compileOnly("org.springframework.boot:spring-boot-starter-webflux")

  testImplementation("io.kotest:kotest-runner-junit5:4.6.3")
  testImplementation("io.kotest:kotest-framework-engine:4.6.3")
  testImplementation("io.projectreactor:reactor-test")
}
