plugins {
    checkstyle
    jacoco
    java
    pmd
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

        val integrationTest by registering(JvmTestSuite::class) {
            dependencies {
                implementation(project)
                // add testImplementation dependencies
                configurations.testImplementation {
                    dependencies.forEach(::implementation)
                }
            }

            targets {
                all {
                    testTask.configure {
                        shouldRunAfter(test)
                    }
                }
            }
        }
    }
}

tasks.named("check") {
    dependsOn(testing.suites.named("integrationTest"))
    dependsOn("jacocoTestCoverageVerification")
}

tasks.named("jacocoTestReport") {
    dependsOn(testing.suites.named("test"))
}

tasks.jacocoTestCoverageVerification {
    violationRules {
        rule {
            limit {
                minimum = "0.38".toBigDecimal()
            }
        }
    }
}
