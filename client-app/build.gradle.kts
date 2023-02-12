plugins {
    alias(libs.plugins.detekt)
    alias(libs.plugins.kotlinjvm)
    application
    jacoco
}

description = "CLI client application."
group = "ch.dnsmap.dnsm"
version = project.properties["dnsm.client-app.version"]!!

repositories {
    mavenCentral()
}

dependencies {
    implementation(libs.clikt)
    implementation(libs.koin)
    implementation(libs.kotlin.stdlib.jdk8)
    implementation(platform(libs.kotlin.bom))
    implementation(project(":client"))

    detektPlugins(libs.klint)

    testImplementation(libs.assertj.core)
    testImplementation(libs.jupiter)
    testImplementation(libs.koin.test)
    testImplementation(libs.koin.test.junit5)
}

application {
    mainClass.set("ch.dnsmap.dnsm.application.DnsmClientAppKt")
}

kotlin {
    jvmToolchain {
        languageVersion.set(JavaLanguageVersion.of(18))
    }
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<Jar> {
    manifest {
        attributes["Main-Class"] = "ch.dnsmap.dnsm.application.DnsmClientAppKt"
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

tasks.named("check") {
    dependsOn("jacocoTestCoverageVerification")
}

tasks.named("jacocoTestReport") {
    dependsOn(testing.suites.named("test"))
}

tasks.jacocoTestCoverageVerification {
    violationRules {
        rule {
            limit {
                minimum = "0.7".toBigDecimal()
            }
        }
    }
}
