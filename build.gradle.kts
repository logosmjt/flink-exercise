plugins {
    id("java")
    id("application")
}

group = "exercise.flink"

dependencies {
    implementation(libs.bundles.flinks)
    implementation(libs.google.gson)
}
