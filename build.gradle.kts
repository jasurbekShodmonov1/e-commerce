plugins {
	java
	id("org.springframework.boot") version "4.0.6"
	id("io.spring.dependency-management") version "1.1.7"
}

group = "com.example"
version = "0.0.1-SNAPSHOT"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(21)
	}
}

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-actuator")
	implementation("org.springframework.boot:spring-boot-starter-webmvc")
	implementation("org.springframework.boot:spring-boot-starter-security")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.security:spring-security-oauth2-jose:6.4.4")
	implementation("com.nimbusds:nimbus-jose-jwt:9.40")
	implementation ("org.springframework.boot:spring-boot-starter-validation")
	implementation ("org.springframework.boot:spring-boot-starter-data-redis")
	compileOnly("org.projectlombok:lombok")
	runtimeOnly("org.postgresql:postgresql")
	annotationProcessor("org.projectlombok:lombok")
	testImplementation("org.springframework.boot:spring-boot-starter-data-jpa-test")
	testImplementation("org.springframework.boot:spring-boot-starter-webmvc-test")
	testCompileOnly("org.projectlombok:lombok")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
	testAnnotationProcessor("org.projectlombok:lombok")

	testImplementation ("com.h2database:h2")
	implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:${rootProject.extra.get("openApiVersion")}")
	implementation("org.mapstruct:mapstruct:${rootProject.extra.get("mapstructVersion")}")
	annotationProcessor("org.mapstruct:mapstruct-processor:${rootProject.extra.get("mapstructVersion")}")

	implementation("io.jsonwebtoken:jjwt-api:0.12.5")
	runtimeOnly("io.jsonwebtoken:jjwt-impl:0.12.5")
	runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.12.5")

	testImplementation("org.springframework.security:spring-security-test")

	// 1. RestAssured
	testImplementation("io.rest-assured:rest-assured:6.0.0")

	// 2. Testcontainers
	testImplementation("org.testcontainers:postgresql:1.19.0")
	testImplementation("org.testcontainers:junit-jupiter:1.19.0")

	// 3. RestDocs
	testImplementation("org.springframework.restdocs:spring-restdocs-restassured:3.0.0")
}


tasks.withType<Test> {
	useJUnitPlatform()
}
