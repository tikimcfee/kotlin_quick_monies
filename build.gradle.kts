import org.gradle.jvm.tasks.Jar


plugins {
    // Apply the Kotlin JVM plugin to add support for Kotlin on the JVM.
    kotlin("jvm") version "1.4.10"
    kotlin("plugin.serialization") version "1.4.10"
    
    // Apply the application plugin to add support for building a CLI application.
    application
}

repositories {
    jcenter()
    mavenCentral()
    maven { setUrl("https://kotlin.bintray.com/kotlin-js-wrappers/") }
}

dependencies {
    // Use the Kotlin JDK 8 standard library.
    implementation(kotlin("stdlib-jdk8"))
    implementation(kotlin("reflect"))
    
    // --- PoC Libs ---
    
    // - Server -
    implementation("io.javalin:javalin:2.7.0")
    implementation("org.slf4j:slf4j-simple:1.7.25")
    
    // - Data Serialization (because 'Dataz' was lame) -
    implementation("com.squareup.moshi:moshi:1.8.0")
    implementation("com.squareup.moshi:moshi-kotlin:1.8.0")
    
    // - Ser-..client... serlcients.
    implementation("com.squareup.retrofit2:retrofit:2.5.0")
    implementation("com.squareup.retrofit2:converter-moshi:2.5.0")
    
    // - Time -
    implementation("joda-time:joda-time:2.10.1")
    
    // - Web Tools : CSS -
    implementation("org.jetbrains:kotlin-css:1.0.0-pre.141-kotlin-1.4.21")
}

val mainClassPathJavalin = "kotlin_quick_monies.visual_interfaces.web.JavalinWebFrameworkWrapperKt"

val fatJar = task("fatJar", type = Jar::class) {
    archiveBaseName.set("${project.name}-fat")
    
    // Used to provide default main class for executable jar
    manifest {
        attributes["Main-Class"] = mainClassPathJavalin
    }
    
    dependsOn(configurations.runtimeClasspath)
    from(sourceSets.main.get().output)
    from(configurations.runtimeClasspath.get()
        .filter { it.name.endsWith("jar") }
        .map { zipTree(it) }
    )
    
    exclude(
        "**/*.kotlin_metadata"
//        "**/*.kotlin_module",
//        "**/*.kotlin_builtins"
    )
}

application {
    // Define the main class for the CLI application.
    mainClassName = mainClassPathJavalin
}
