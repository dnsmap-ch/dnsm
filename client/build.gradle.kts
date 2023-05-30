plugins {
    alias(libs.plugins.detekt)
    alias(libs.plugins.kotlinjvm)
}

description = "DNS client role functionalities."
group = "ch.dnsmap.dnsm"
version = project.properties["dnsm.client.version"]!!

repositories {
    mavenCentral()
}

dependencies {
    api(project(":core"))

    implementation(libs.clikt)
    implementation(libs.kotlin.stdlib.jdk8)
    implementation(libs.kotlinx)
    implementation(libs.okhttp)
    implementation(libs.rxjava)
    implementation(platform(libs.kotlin.bom))

    detektPlugins(libs.klint)

    testImplementation(kotlin("test"))
    testImplementation(libs.assertj.core)
    testImplementation(libs.jupiter)
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(18))
        vendor.set(JvmVendorSpec.ADOPTIUM)
    }
}

tasks.test {
    useJUnitPlatform()
}

detekt {
    config.setFrom(file("../config/detekt/detekt.yml"))
    buildUponDefaultConfig = true
}
