/*
 * Copyright (c) 2019-2022 StarWishsama.
 *
 * 此源代码的使用受 MIT 许可证约束, 欲阅读此许可证, 可在以下链接查看.
 * Use of this source code is governed by the MIT License which can be found through the following link.
 *
 * https://github.com/StarWishsama/Comet-Bot/blob/dev/LICENSE
 */

@file:Suppress("GradlePackageUpdate")

plugins {
    `comet-conventions`
    kotlin("plugin.serialization")
}

repositories {
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
}

dependencies {
    compileOnly("org.jline:jline:3.21.0")
    testCompileOnly("org.jline:jline:3.21.0")

    api("org.jetbrains.exposed:exposed-core:0.41.1")
    api("org.jetbrains.exposed:exposed-dao:0.41.1")
    api("org.jetbrains.exposed:exposed-jdbc:0.41.1")
    api("org.jetbrains.exposed:exposed-kotlin-datetime:0.41.1")
    api("com.zaxxer:HikariCP:5.0.1")
    api("org.xerial:sqlite-jdbc:3.40.0.0")
    api("org.postgresql:postgresql:42.5.1")
    api("moe.sdl.yac:core:1.0.1")
    api("com.cronutils:cron-utils:9.2.0")

    implementation(project(":comet-utils"))
}

tasks.withType(AbstractTestTask::class.java).configureEach {
    testLogging {
        showExceptions = true
        showStackTraces = true
        exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
    }
}
