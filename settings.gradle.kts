plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.5.0"
}
rootProject.name = "datahub"
include("datahub-app-external-api")
include("domain:domain-login")
include("domain:domain-post")
include("domain:domain-system")
include("domain:domain-qa")
include("security")
include("domain:domain-notice")
