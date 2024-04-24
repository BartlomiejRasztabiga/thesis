import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.springframework.boot.gradle.tasks.bundling.BootJar

plugins {
    id("org.springframework.boot") version "3.2.5"
    id("io.spring.dependency-management") version "1.1.4"
    id("org.jetbrains.kotlin.jvm") version "1.9.23"
    id("org.jetbrains.kotlin.plugin.spring") version "1.9.23"
    id("java-library")
    id("maven-publish")
    id("java-test-fixtures")
    id("io.gitlab.arturbosch.detekt") version "1.23.6"
    id("io.sentry.jvm.gradle") version "4.5.0"
}

group = "me.rasztabiga.thesis"

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
    withSourcesJar()
}

repositories {
    mavenCentral()
}

val axonVersion = "4.9.4"
val kotestVersion = "5.8.1"

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")
    implementation("org.axonframework:axon-spring-boot-starter")

    implementation("com.google.maps:google-maps-services:2.2.0")

    testFixturesImplementation(kotlin("reflect"))
    testFixturesImplementation("org.springframework.boot:spring-boot-starter-test")
    testFixturesImplementation("org.axonframework.extensions.reactor:axon-reactor-spring-boot-starter")
    testFixturesImplementation("io.projectreactor:reactor-test")
    testFixturesImplementation("org.axonframework:axon-test")
    testFixturesImplementation("io.kotest:kotest-runner-junit5:$kotestVersion")
    testFixturesImplementation("io.kotest:kotest-assertions-core:$kotestVersion")
    testFixturesImplementation("io.mockk:mockk:1.13.10")
    testFixturesImplementation("com.ninja-squad:springmockk:4.0.2")
}

dependencyManagement {
    imports {
        mavenBom("org.axonframework:axon-bom:${axonVersion}")
    }
}

sentry {
    includeSourceContext = true
    org = "bartomiej-rasztabiga"
    projectName = "thesis"
    authToken = "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"
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
            name = "mymavenrepo.com"
            url = uri("https://mymavenrepo.com/repo/KAXC3J7fRzj0fcOWKXr0")
        }
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
        jvmTarget = "21"
    }
}

tasks.named("sourcesJar") {
    dependsOn("collectExternalDependenciesForSentry", "generateSentryDebugMetaPropertiesjava")
}
