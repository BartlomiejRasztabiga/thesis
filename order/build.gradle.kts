import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	id("org.springframework.boot") version "3.0.6"
	id("io.spring.dependency-management") version "1.1.0"
	id("org.jetbrains.kotlin.jvm") version "1.8.20"
	id("org.jetbrains.kotlin.plugin.spring") version "1.8.20"
	id("io.gitlab.arturbosch.detekt") version "1.23.0-RC1"
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
			username = System.getenv("GITHUB_ACTOR") ?: project.findProperty("GITHUB_ACTOR").toString()
			password = System.getenv("GITHUB_TOKEN") ?: project.findProperty("GITHUB_TOKEN").toString()
		}
	}
}

val axonVersion = "4.7.3"
val testcontainersVersion = "1.18.0"

dependencies {
	implementation("me.rasztabiga.thesis:shared:0.1.15")

	implementation("org.springframework.boot:spring-boot-starter-actuator")
	implementation("org.springframework.boot:spring-boot-starter-webflux")
	implementation("org.springframework.boot:spring-boot-starter-security")
	implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")
	implementation("org.springframework.boot:spring-boot-configuration-processor")
	implementation("org.springframework.boot:spring-boot-starter-data-mongodb")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("org.axonframework:axon-spring-boot-starter")
	implementation("org.axonframework.extensions.kotlin:axon-kotlin")
	implementation("org.axonframework.extensions.reactor:axon-reactor-spring-boot-starter")
	implementation("org.axonframework:axon-micrometer")
	implementation(kotlin("reflect"))
	implementation(kotlin("stdlib"))
	runtimeOnly("org.springframework.boot:spring-boot-devtools")

	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.axonframework:axon-test")
	testImplementation("org.testcontainers:junit-jupiter")
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
