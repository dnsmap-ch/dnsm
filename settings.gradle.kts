rootProject.name = "dnsm"

include(
    "client",
    "client-app",
    "core"
)

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version("0.4.0")
}
