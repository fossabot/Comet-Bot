/*
 * Copyright (c) 2019-2022 StarWishsama.
 *
 * 此源代码的使用受 MIT 许可证约束, 欲阅读此许可证, 可在以下链接查看.
 * Use of this source code is governed by the MIT License which can be found through the following link.
 *
 * https://github.com/StarWishsama/Comet-Bot/blob/dev/LICENSE
 */

package ren.natsuyuk1.comet.utils.message

import kotlinx.serialization.Serializable
import ren.natsuyuk1.comet.utils.file.absPath
import java.io.File

@Serializable
sealed class WrapperElement {
    abstract fun parseToString(): String
}

/**
 * [Text]
 *
 * 纯文本消息
 *
 * @param text 文本
 */
@Serializable
data class Text(val text: String) : WrapperElement() {
    override fun parseToString(): String = text
}

/**
 * [Image]
 *
 * 图片消息
 *
 * 必须提供图片下载链接, 图片本地路径及 Base64 之中其一.
 *
 * @param url 图片下载链接
 * @param filePath 图片本地路径
 */
@Serializable
data class Image(
    val url: String = "",
    val filePath: String = "",
    val base64: String = ""
) : WrapperElement() {

    init {
        if (url.isEmpty() && filePath.isEmpty() && base64.isEmpty()) {
            throw IllegalArgumentException("url/filePath/base64 can't be null or empty!")
        }
    }

    override fun parseToString(): String = "[图片]"
}

fun String.asURLImage(): Image = Image(url = this)

fun String.asBase64Image(): Image = Image(base64 = this)

fun File.asImage(): Image = Image(filePath = absPath)

/**
 * [AtElement]
 *
 * At 消息
 *
 * @param target At 目标
 */
@Serializable
data class AtElement(
    val target: Long = 0,
    // For Telegram
    val userName: String = ""
) : WrapperElement() {
    override fun parseToString(): String = "@$target"
}

/**
 * [XmlElement]
 *
 * XML 消息
 *
 * @param content XML 消息
 */
@Serializable
data class XmlElement(val content: String) : WrapperElement() {
    override fun parseToString(): String = "[XML 消息]"
}

/**
 * [Voice]
 *
 * 语音消息
 */
@Serializable
data class Voice(val filePath: String) : WrapperElement() {
    override fun parseToString(): String = "[语音消息]"
}

@Serializable
data class Nudge(val target: Long) : WrapperElement() {
    override fun parseToString(): String = "[戳一戳]"
}
