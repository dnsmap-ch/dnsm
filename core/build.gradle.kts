plugins {
    java
    pmd
    checkstyle
}

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
    testImplementation("org.assertj:assertj-core:3.23.1")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.1")
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
