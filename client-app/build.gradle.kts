plugins {
    alias(libs.plugins.detekt)
    alias(libs.plugins.kotlinjvm)
    application
}

description = "CLI client application."
group = "ch.dnsmap.dnsm"
version = project.properties["dnsm.client-app.version"]!!

repositories {
    mavenCentral()
}

dependencies {
    implementation(libs.clikt)
    implementation(libs.kotlin.stdlib.jdk8)
    implementation(platform(libs.kotlin.bom))
    implementation(project(":client"))

    testImplementation(kotlin("test"))
    testImplementation(libs.assertj.core)
    testImplementation(libs.jupiter)
}

application {
    mainClass.set("ch.dnsmap.dnsm.infrastructure.AppKt")
}

tasks.withType<Jar> {
    manifest {
        attributes["Main-Class"] = "ch.dnsmap.dnsm.infrastructure.AppKt"
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
