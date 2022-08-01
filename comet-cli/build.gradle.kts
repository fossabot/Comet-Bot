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
    maven("https://s01.oss.sonatype.org/content/repositories/snapshots/")
}

dependencies {
    implementation(KotlinX.serialization.json)

    implementation("moe.sdl.yac:core:_")

    implementation("org.jline:jline:_")

    api(project(":comet-api"))
    api(project(":comet-core"))
    api(project(":comet-utils"))

    implementation(project(":comet-mirai-wrapper"))
    implementation(project(":comet-telegram-wrapper"))
}

tasks.jar {
    manifest {
        attributes["Main-Class"] = "ren.natsuyuk1.comet.cli.CometTerminalKt"
        attributes["Author"] = "StarWishsama"
    }
}
