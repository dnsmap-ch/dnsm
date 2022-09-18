plugins {
    java
    pmd
    checkstyle
}

description = "DNS core components and wire format generation and parsing."
group = "ch.dnsmap.dnsm"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

dependencies {
    testImplementation(libs.assertj.core)
    testImplementation(libs.junit.jupiter.api)
    testRuntimeOnly(libs.junit.jupiter.engine)
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}

pmd {
    isConsoleOutput = true
    toolVersion = "6.48.0"
    rulesMinimumPriority.set(5)
    ruleSets = emptyList()
    ruleSetFiles = files("$rootDir/config/pmd/ruleset.xml")
}

checkstyle {
    toolVersion = "9.0"
}
