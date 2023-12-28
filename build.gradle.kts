import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.springframework.boot.gradle.tasks.bundling.BootJar

plugins {
	id("org.springframework.boot") version "2.7.17"
	id("io.spring.dependency-management") version "1.0.15.RELEASE"
	kotlin("jvm") version "1.6.21"
	kotlin("plugin.spring") version "1.6.21" apply false
	kotlin("plugin.jpa") version "1.6.21" apply false
}

java.sourceCompatibility = JavaVersion.VERSION_17

allprojects {
	group = "com.knusolution"
	version = "1.0.0"

	repositories {
		mavenCentral()
	}
}

subprojects {
	apply(plugin = "java")

	apply(plugin = "io.spring.dependency-management")
	apply(plugin = "org.springframework.boot")
	apply(plugin = "org.jetbrains.kotlin.plugin.spring")

	apply(plugin = "kotlin")
	apply(plugin = "kotlin-spring") //all-open
	apply(plugin = "kotlin-jpa")

	dependencies {
		// springboot
		implementation("org.springframework.boot:spring-boot-starter-web")
		implementation("org.springframework.boot:spring-boot-starter-data-jpa")
		implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
		developmentOnly("org.springframework.boot:spring-boot-devtools")

		// kotlin
		implementation("org.jetbrains.kotlin:kotlin-reflect")
		implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

		//s3
		implementation("com.amazonaws:aws-java-sdk-s3:1.12.281")

		// DB
		runtimeOnly("mysql:mysql-connector-java:8.0.25")

		//swagger
		implementation("io.springfox:springfox-swagger-ui:3.0.0")
		implementation("io.springfox:springfox-boot-starter:3.0.0")

		// test
		testImplementation("org.springframework.boot:spring-boot-starter-test")
	}

	dependencyManagement {
		imports {
			mavenBom(org.springframework.boot.gradle.plugin.SpringBootPlugin.BOM_COORDINATES)
		}

		dependencies {
			dependency("net.logstash.logback:logstash-logback-encoder:6.6")
		}
	}

	tasks.withType<KotlinCompile> {
		kotlinOptions {
			freeCompilerArgs = listOf("-Xjsr305=strict")
			jvmTarget = "11"
		}
	}

	tasks.withType<Test> {
		useJUnitPlatform()
	}

	configurations {
		compileOnly {
			extendsFrom(configurations.annotationProcessor.get())
		}
	}
}

// datahub-app-external-api가 domain-login, domain-post에 의존
project(":datahub-app-external-api") {
	dependencies {
		implementation(project(":domain:domain-login"))
		implementation(project(":domain:domain-post"))
	}
}

//login 설정
project(":domain:domain-login") {
	val jar: Jar by tasks
	val bootJar: BootJar by tasks

	bootJar.enabled = false
	jar.enabled = true

}

// post 설정
project(":domain:domain-post") {
	val jar: Jar by tasks
	val bootJar: BootJar by tasks

	bootJar.enabled = false
	jar.enabled = true
}

//system 설정
project(":domain:domain-system") {
	val jar: Jar by tasks
	val bootJar: BootJar by tasks

	bootJar.enabled = false
	jar.enabled = true
}
project(":domain")
{
	val jar:Jar by tasks
	val bootJar:BootJar by tasks
	bootJar.enabled = false
	jar.enabled = true
}
project(":security")
{
        val jar:Jar by tasks
        val bootJar:BootJar by tasks
        bootJar.enabled = false
        jar.enabled = true
}
val bootJar : BootJar by tasks
bootJar.enabled = false
