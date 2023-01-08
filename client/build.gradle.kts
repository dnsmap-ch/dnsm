plugins {
    id("org.jetbrains.kotlin.jvm") version "1.8.0"
    id("org.graalvm.buildtools.native") version "0.9.17"
    application
}

description = "Dnsmap client application to query domain servers."
group = "ch.dnsmap.dnsm"
version = project.properties["dnsm.version"]!!

repositories {
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    implementation("com.github.ajalt.clikt:clikt:3.5.1")
    implementation("io.reactivex.rxjava3:rxjava:3.1.6")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation(platform("org.jetbrains.kotlin:kotlin-bom"))
    implementation(project(":core"))

    testImplementation("org.junit.jupiter:junit-jupiter:5.8.1")
    testImplementation(kotlin("test"))
    testImplementation(libs.assertj.core)
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

application {
    mainClass.set("ch.dnsmap.dnsm.infrastructure.ClientKt")
}

tasks.withType<Jar> {
    manifest {
        attributes["Main-Class"] = "ch.dnsmap.dnsm.infrastructure.ClientKt"
    }

    // To avoid the duplicate handling strategy error
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE

    // To add all the dependencies
    from(sourceSets.main.get().output)

    dependsOn(configurations.runtimeClasspath)
    from({
        configurations.runtimeClasspath.get().filter { it.name.endsWith("jar") }.map { zipTree(it) }
    })
}
