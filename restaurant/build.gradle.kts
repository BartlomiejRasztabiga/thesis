import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	id("org.springframework.boot") version "2.7.9"
	id("io.spring.dependency-management") version "1.1.0"
	id("org.jetbrains.kotlin.jvm") version "1.8.10"
	id("org.jetbrains.kotlin.plugin.spring") version "1.8.10"
}

group = "me.rasztabiga.thesis"

java {
	sourceCompatibility = JavaVersion.VERSION_17
	targetCompatibility = JavaVersion.VERSION_17
}

repositories {
	mavenCentral()
}

val axonVersion = "4.7.2"
val testcontainersVersion = "1.17.6"

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-actuator")
//	implementation("org.springframework.boot:spring-boot-starter-rsocket")
	implementation("org.springframework.boot:spring-boot-starter-web")
//	implementation("org.springframework.boot:spring-boot-starter-webflux")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("org.axonframework:axon-spring-boot-starter")
	implementation("org.axonframework.extensions.kotlin:axon-kotlin")
//	implementation("org.axonframework.extensions.reactor:axon-reactor-spring-boot-starter")
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
