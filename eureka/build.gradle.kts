import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	id("org.springframework.boot") version "3.0.3"
	id("io.spring.dependency-management") version "1.1.0"
	kotlin("jvm") version "1.7.22"
	kotlin("plugin.spring") version "1.7.22"
}

group = "me.rasztabiga.thesis"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_17

repositories {
	mavenCentral()
	maven { url = uri("https://artifactory-oss.prod.netflix.net/artifactory/maven-oss-candidates") }
}

extra["springCloudVersion"] = "2022.0.1"

dependencies {
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
	implementation("org.springframework.cloud:spring-cloud-starter-netflix-eureka-server")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
}

dependencyManagement {
	imports {
		mavenBom("org.springframework.cloud:spring-cloud-dependencies:${property("springCloudVersion")}")
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
