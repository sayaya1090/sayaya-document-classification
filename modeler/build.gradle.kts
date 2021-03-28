import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "2.4.4"
    id("io.spring.dependency-management") version "1.0.11.RELEASE"
    kotlin("jvm") version "1.4.32"
    kotlin("kapt") version "1.4.32"
    kotlin("plugin.spring") version "1.4.32"
}
extra["springCloudVersion"] = "2020.0.2"

group = "net.sayaya"
version = "1.0"
java.sourceCompatibility = JavaVersion.VERSION_11

configurations {
    all {
        exclude(group = "org.springframework.boot", module = "spring-boot-starter-logging")
    }
}
repositories {
    mavenCentral()
    mavenLocal()
}
dependencies {
    implementation(project(":data"))
    implementation(kotlin("reflect"))
    implementation(kotlin("stdlib-jdk8"))
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")

    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("org.springframework.boot:spring-boot-starter-log4j2")
    implementation("org.springframework.boot:spring-boot-starter-data-cassandra-reactive")
    implementation("org.springframework.cloud:spring-cloud-starter-zookeeper-discovery")
    implementation("org.springframework.cloud:spring-cloud-starter-stream-kafka")

    implementation("io.springfox:springfox-boot-starter:3.+")
    implementation("io.springfox:springfox-swagger-ui")
    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.kafka:spring-kafka-test")
    testImplementation("org.apache.kafka:kafka-streams-test-utils")
    testImplementation("io.projectreactor:reactor-test")
}
dependencyManagement {
    imports {
        mavenBom("org.springframework.cloud:spring-cloud-dependencies:${property("springCloudVersion")}")
    }
}
tasks.withType<KotlinCompile> {
    kotlinOptions {
        useIR = true
        jvmTarget = "11"
        freeCompilerArgs = listOf("-Xjsr305=strict")
    }
}
tasks.withType<Test> {
    useJUnitPlatform()
}
/*task copyWebResources(type: Copy) {
    dependsOn ":modeler-ui:build"
    from(zipTree ('../modeler-ui/build/libs/sayaya-document-classification-modeler-ui.war')) {

        includeEmptyDirs = false
    }
    into "src/main/resources/static"
}
processResources {
   dependsOn copyWebResources
}
bootJar {
    archiveName "sayaya-document-classification-modeler.jar"
}*/
tasks.withType<Delete> {
    doFirst{
        delete("src/main/resources/static")
        delete("build/")
    }
}