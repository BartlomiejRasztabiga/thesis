import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "3.0.6"
    id("io.spring.dependency-management") version "1.1.0"
    id("org.jetbrains.kotlin.jvm") version "1.8.20"
    id("org.jetbrains.kotlin.plugin.spring") version "1.8.20"
    id("io.gitlab.arturbosch.detekt") version "1.23.0-RC2"
}

group = "me.rasztabiga.thesis"

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

repositories {
    mavenCentral()
    maven {
        url = uri("https://maven.pkg.github.com/bartlomiejrasztabiga/thesis")
        credentials {
            username =
                System.getenv("GITHUB_ACTOR") ?: project.findProperty("GITHUB_ACTOR").toString()
            password =
                System.getenv("GITHUB_TOKEN") ?: project.findProperty("GITHUB_TOKEN").toString()
        }
    }
}

val axonVersion = "4.7.3"
val testcontainersVersion = "1.18.0"
val kotestVersion = "5.6.1"

dependencies {
    implementation("me.rasztabiga.thesis:shared:0.1.15")

    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")
    implementation("org.springframework.boot:spring-boot-configuration-processor")
    implementation("org.springframework.boot:spring-boot-starter-data-mongodb-reactive")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.axonframework:axon-spring-boot-starter")
    implementation("org.axonframework.extensions.kotlin:axon-kotlin")
    implementation("org.axonframework.extensions.reactor:axon-reactor-spring-boot-starter")
    implementation("org.axonframework:axon-micrometer")
    implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
    implementation("org.dom4j:dom4j:2.1.4")
    implementation(kotlin("reflect"))
    implementation(kotlin("stdlib"))
    runtimeOnly("org.springframework.boot:spring-boot-devtools")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("io.projectreactor:reactor-test")
    testImplementation("org.axonframework:axon-test")
    testImplementation("org.testcontainers:junit-jupiter")
    testImplementation("io.kotest:kotest-runner-junit5:$kotestVersion")
    testImplementation("io.kotest:kotest-assertions-core:$kotestVersion")
    testImplementation("io.mockk:mockk:1.13.5")
    testImplementation("com.ninja-squad:springmockk:4.0.2")
}

dependencyManagement {
    imports {
        mavenBom("org.axonframework:axon-bom:${axonVersion}")
        mavenBom("org.testcontainers:testcontainers-bom:${testcontainersVersion}")
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
