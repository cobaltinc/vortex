plugins {
  kotlin("jvm") version "1.6.0"
  id("io.spring.dependency-management") version "1.0.11.RELEASE"
  `maven-publish`
  signing
}

group = "run.cobalt"
version = "0.0.1"
java.sourceCompatibility = JavaVersion.VERSION_11

repositories {
  maven(url = "https://plugins.gradle.org/m2/")
  mavenCentral()
}

tasks {
  compileKotlin {
    kotlinOptions {
      jvmTarget = "11"
    }
  }

  compileTestKotlin {
    kotlinOptions {
      jvmTarget = "11"
    }
  }

  val sourcesJar by creating(Jar::class) {
    archiveClassifier.set("sources")
    from(sourceSets.main.get().allSource)
  }

  val javadocJar by creating(Jar::class) {
    dependsOn.add(javadoc)
    archiveClassifier.set("javadoc")
    from(javadoc)
  }

  artifacts {
    archives(sourcesJar)
    archives(javadocJar)
    archives(jar)
  }
}

publishing {
  repositories {
    maven {
      credentials {
        username = project.property("ossrhUsername").toString()
        password = project.property("ossrhPassword").toString()
      }
      val releasesRepoUrl = uri("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/")
      val snapshotsRepoUrl = uri("https://s01.oss.sonatype.org/content/repositories/snapshots/")
      url = if (version.toString().endsWith("SNAPSHOT")) snapshotsRepoUrl else releasesRepoUrl
    }
  }

  publications {
    create<MavenPublication>("vortex") {
      groupId = "$group"
      artifactId = rootProject.name
      version = version

      from(components["java"])

      artifact(tasks["sourcesJar"])
      artifact(tasks["javadocJar"])

      pom {
        name.set("vortex")
        packaging = "jar"
        description.set("Kotlin functional extensions for Spring Webflux")
        url.set("https://github.com/cobaltinc/vortex")

        licenses {
          license {
            name.set("The MIT License")
            url.set("https://opensource.org/licenses/MIT")
          }
        }

        developers {
          developer {
            id.set("kciter")
            name.set("Lee Sun-Hyoup")
            email.set("kciter@naver.com")
            url.set("https://github.com/kciter")
            roles.addAll("developer")
            timezone.set("Asia/Seoul")
          }
        }

        scm {
          connection.set("scm:git:git://github.com/cobaltinc/vortex.git")
          developerConnection.set("scm:git:ssh://github.com:cobaltinc/vortex.git")
          url.set("https://github.com/cobaltinc/vortex")
        }
      }
    }
  }
}

signing {
  sign(configurations.archives.get())
  sign(publishing.publications["vortex"])
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
