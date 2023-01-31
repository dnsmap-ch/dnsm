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
    implementation(libs.clikt)
    implementation(libs.kotlin.stdlib.jdk8)
    implementation(libs.rxjava)
    implementation(platform(libs.kotlin.bom))
    api(project(":core"))

    detektPlugins(libs.klint)

    testImplementation(kotlin("test"))
    testImplementation(libs.assertj.core)
    testImplementation(libs.jupiter)
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
        vendor.set(JvmVendorSpec.ADOPTIUM)
    }
}

tasks.test {
    useJUnitPlatform()
}
