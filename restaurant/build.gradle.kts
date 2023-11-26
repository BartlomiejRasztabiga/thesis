import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "3.1.5"
    id("io.spring.dependency-management") version "1.1.4"
    id("org.jetbrains.kotlin.jvm") version "1.9.21"
    id("org.jetbrains.kotlin.plugin.spring") version "1.9.21"
    id("io.gitlab.arturbosch.detekt") version "1.23.4"
    id("io.sentry.jvm.gradle") version "3.14.0"
}

group = "me.rasztabiga.thesis"

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

repositories {
    mavenCentral()
    maven {
        name = "mymavenrepo.com"
        url = uri("https://mymavenrepo.com/repo/xi9nf7WcfPC8Wl1EAs9n")
    }
    maven {
        url = uri("https://maven.pkg.github.com/bartlomiejrasztabiga/thesis")
        credentials {
            username = System.getenv("GITHUB_ACTOR") ?: project.findProperty("GITHUB_ACTOR").toString()
            password = System.getenv("GITHUB_TOKEN") ?: project.findProperty("GITHUB_TOKEN").toString()
        }
    }
}

val axonVersion = "4.9.1"
val kotestVersion = "5.8.0"

dependencies {
    implementation("me.rasztabiga.thesis:shared:0.20.12")

    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")
    implementation("org.springframework.boot:spring-boot-configuration-processor")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.axonframework:axon-spring-boot-starter")
    implementation("org.axonframework.extensions.kotlin:axon-kotlin")
    implementation("org.axonframework.extensions.reactor:axon-reactor-spring-boot-starter")
    implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
    implementation("org.dom4j:dom4j:2.1.4")
    implementation(kotlin("reflect"))
    implementation(kotlin("stdlib"))

    implementation("io.axoniq.console:console-framework-client-spring-boot-starter:1.2.0")

    testImplementation(testFixtures("me.rasztabiga.thesis:shared:0.20.12"))
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("io.projectreactor:reactor-test")
    testImplementation("org.axonframework:axon-test")
    testImplementation("io.kotest:kotest-runner-junit5:$kotestVersion")
    testImplementation("io.kotest:kotest-assertions-core:$kotestVersion")
    testImplementation("io.mockk:mockk:1.13.8")
    testImplementation("com.ninja-squad:springmockk:4.0.2")
}

dependencyManagement {
    imports {
        mavenBom("org.axonframework:axon-bom:${axonVersion}")
    }
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "17"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.getByName<Jar>("jar") {
    enabled = false
}
