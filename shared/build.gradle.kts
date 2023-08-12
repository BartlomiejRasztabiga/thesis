import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.springframework.boot.gradle.tasks.bundling.BootJar

plugins {
    id("org.springframework.boot") version "3.1.2"
    id("io.spring.dependency-management") version "1.1.2"
    id("org.jetbrains.kotlin.jvm") version "1.9.0"
    id("org.jetbrains.kotlin.plugin.spring") version "1.9.0"
    id("java-library")
    id("maven-publish")
    id("java-test-fixtures")
    id("io.gitlab.arturbosch.detekt") version "1.23.1"
}

group = "me.rasztabiga.thesis"

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
    withSourcesJar()
}

repositories {
    mavenCentral()
}

val axonVersion = "4.8.1"
val kotestVersion = "5.6.2"

dependencies {
    api("org.springframework.boot:spring-boot-starter-webflux")
    api("org.springframework.boot:spring-boot-starter-security")
    api("org.springframework.boot:spring-boot-starter-oauth2-resource-server")
    api("org.axonframework:axon-spring-boot-starter")

    testFixturesApi(kotlin("reflect"))
    testFixturesApi("org.springframework.boot:spring-boot-starter-test")
    testFixturesApi("org.axonframework.extensions.reactor:axon-reactor-spring-boot-starter")
    testFixturesApi("io.projectreactor:reactor-test")
    testFixturesApi("org.axonframework:axon-test")
    testFixturesApi("org.testcontainers:junit-jupiter")
    testFixturesApi("io.kotest:kotest-runner-junit5:$kotestVersion")
    testFixturesApi("io.kotest:kotest-assertions-core:$kotestVersion")
    testFixturesApi("io.mockk:mockk:1.13.5")
    testFixturesApi("com.ninja-squad:springmockk:4.0.2")
}

dependencyManagement {
    imports {
        mavenBom("org.axonframework:axon-bom:${axonVersion}")
    }
}

val bootJar: BootJar by tasks
bootJar.enabled = false

publishing {
    publications {
        create<MavenPublication>("library") {
            from(components["java"])
        }
    }
    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/bartlomiejrasztabiga/thesis")
            credentials {
                username = System.getenv("GITHUB_ACTOR")
                password = System.getenv("GITHUB_TOKEN")
            }
        }
    }
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "17"
    }
}
