plugins {
    kotlin("jvm") version "2.0.20"
}

group = "com.musinsa.jinow"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.cloudinary:cloudinary-http5:2.0.0")
    implementation("com.cloudinary:cloudinary-taglib:2.0.0")
    implementation("io.github.cdimascio:dotenv-java:2.2.4")
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(17)
}
