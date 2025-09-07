plugins {
    java
}

repositories {
    mavenCentral()
}

dependencies {
    // JUnit 5 BOM для управления версиями
    testImplementation(platform("org.junit:junit-bom:5.10.0"))

    // JUnit 5
    testImplementation("org.junit.jupiter:junit-jupiter")

    // RestAssured
    testImplementation("io.rest-assured:rest-assured:5.4.0")
    testImplementation("io.rest-assured:json-path:5.4.0")
    testImplementation("io.rest-assured:xml-path:5.4.0")

    // AssertJ
    testImplementation("org.assertj:assertj-core:3.24.2")

    // Jackson для работы с JSON
    testImplementation("com.fasterxml.jackson.core:jackson-databind:2.15.0")
    testImplementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.15.0")

    //Allure для аннотации "Step"
    testImplementation("io.qameta.allure:allure-junit5:2.24.0")
    testImplementation("io.qameta.allure:allure-rest-assured:2.24.0")

    //Hamcrest для соответствия версий, чтобы сравнивать статус код ответа
    testImplementation ("org.hamcrest:hamcrest:2.2")

    implementation("com.googlecode.json-simple:json-simple:1.1.1")
}

tasks.test {
    useJUnitPlatform()

    testLogging {
        events = setOf(
            org.gradle.api.tasks.testing.logging.TestLogEvent.PASSED,
            org.gradle.api.tasks.testing.logging.TestLogEvent.SKIPPED,
            org.gradle.api.tasks.testing.logging.TestLogEvent.FAILED
        )
        showStandardStreams = true
        showCauses = true
        showExceptions = true
        showStackTraces = true
        exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
    }

    // Параллельное выполнение тестов
    systemProperties = mapOf(
        "junit.jupiter.execution.parallel.enabled" to "true",
        "junit.jupiter.execution.parallel.mode.default" to "concurrent",
        "junit.jupiter.execution.parallel.mode.classes.default" to "concurrent"
    )

    maxParallelForks = Runtime.getRuntime().availableProcessors()
    failFast = false // Не останавливаться при первой ошибке
}