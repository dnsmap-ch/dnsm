plugins {
    checkstyle
    jacoco
    java
    pmd
}

description = "DNS core components and wire format generation and parsing."
group = "ch.dnsmap.dnsm"
version = project.properties["dnsm.core.version"]!!

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

testing {
    suites {
        val test by getting(JvmTestSuite::class) {
            useJUnitJupiter()
        }
    }
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
                minimum = "0.9".toBigDecimal()
            }
        }
    }
}
