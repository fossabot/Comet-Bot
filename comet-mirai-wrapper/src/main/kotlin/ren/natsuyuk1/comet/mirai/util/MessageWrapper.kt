package ren.natsuyuk1.comet.mirai.util

import io.ktor.client.call.*
import io.ktor.client.request.*
import net.mamoe.mirai.contact.AudioSupported
import net.mamoe.mirai.contact.Contact
import net.mamoe.mirai.message.data.*
import net.mamoe.mirai.message.data.Image.Key.queryUrl
import net.mamoe.mirai.message.data.MessageSource
import net.mamoe.mirai.utils.ExternalResource.Companion.toExternalResource
import net.mamoe.mirai.utils.ExternalResource.Companion.uploadAsImage
import net.mamoe.mirai.utils.MiraiExperimentalApi
import ren.natsuyuk1.comet.api.message.*
import ren.natsuyuk1.comet.api.message.Image
import ren.natsuyuk1.comet.consts.cometClient
import ren.natsuyuk1.comet.mirai.MiraiComet
import ren.natsuyuk1.comet.mirai.MiraiMessageSource
import ren.natsuyuk1.comet.utils.file.messageWrapperDirectory
import ren.natsuyuk1.comet.utils.file.touch
import ren.natsuyuk1.comet.utils.file.writeToFile
import java.io.File
import java.io.FileNotFoundException
import java.io.InputStream

private val logger = mu.KotlinLogging.logger("MessageWrapperConverter")

@OptIn(MiraiExperimentalApi::class)
suspend fun WrapperElement.toMessageContent(subject: Contact): MessageContent? {
    return when (this) {
        is Text -> return PlainText(this.text)

        is Image -> {
            try {
                if (url?.isNotBlank() == true) {
                    cometClient.client.get(url!!).body<InputStream>().use {
                        it.uploadAsImage(subject)
                    }
                } else if (filePath?.isNotBlank() == true) {
                    if (File(filePath!!).exists()) {
                        return File(filePath!!).uploadAsImage(subject)
                    } else {
                        throw FileNotFoundException(filePath)
                    }
                } else if (base64?.isNotEmpty() == true) {
                    return base64!!.toByteArray().toExternalResource().use {
                        it.uploadAsImage(subject)
                    }
                } else {
                    throw IllegalArgumentException("图片消息元素必须存在一个参数!")
                }
            } catch (e: Exception) {
                logger.warn { "转换图片失败, 原始内容: ${toString()}" }
                return PlainText("[图片]")
            }
        }

        is AtElement -> return At(target)

        is XmlElement -> return SimpleServiceMessage(serviceId = 60, content = content)

        is Voice -> {
            if (subject !is AudioSupported) {
                throw UnsupportedOperationException(
                    "发送语音消息失败: 发送对象必须是好友或群聊!"
                )
            }

            if (filePath.isNotEmpty() && File(filePath).exists()) {
                return subject.uploadAudio(File(filePath).toExternalResource())
            } else {
                throw FileNotFoundException("转换语音失败, 对应的语音文件不存在: $filePath")
            }
        }

        else -> {
            logger.debug { "Mirai Wrapper 不支持该消息元素: ${this::class.simpleName}" }
            null
        }
    }
}

/**
 * [toMessageChain]
 *
 * 将一个 [MessageWrapper] 转换为 [MessageChain]
 *
 * @param subject Mirai 的 [Contact], 为空时一些需要 [Contact] 的元素会转为文字
 */
suspend fun MessageWrapper.toMessageChain(subject: Contact): MessageChain {
    return MessageChainBuilder().apply {
        getMessageContent().forEach { elem ->
            kotlin.runCatching {
                elem.toMessageContent(subject)?.let { add(it) }
            }.onFailure {
                if (it !is UnsupportedOperationException) {
                    logger.warn(it) { "在转换 Mirai 消息时出现问题" }
                } else {
                    logger.debug { "在转换 Mirai 消息时出现问题, 不受支持的消息元素: ${elem::class.simpleName}" }
                }
            }
        }
    }.build()
}

suspend fun MessageChain.toMessageWrapper(comet: MiraiComet): MessageWrapper {
    val wrapper = MessageWrapper(MessageReceipt(comet, source.toMessageSource()))

    for (message in this) {
        when (message) {
            is PlainText -> {
                wrapper.appendText(message.content)
            }

            is net.mamoe.mirai.message.data.Image -> {
                wrapper.appendElement(Image(url = message.queryUrl()))
            }
            is At -> {
                wrapper.appendElement(AtElement(message.target))
            }
            is ServiceMessage -> {
                wrapper.appendElement(XmlElement(message.content))
            }
            is OnlineAudio -> {
                val fileName = message.filename
                val downloadedAudio =
                    cometClient.client.get(message.urlForDownload).body<InputStream>()

                downloadedAudio.use {
                    val location = File(messageWrapperDirectory, fileName)
                    location.touch()

                    writeToFile(it, location)
                }
            }
            is Face -> {
                wrapper.appendText(message.content)
            }
            else -> {
                continue
            }
        }
    }

    return wrapper
}

internal fun MessageSource.toMessageSource(): MiraiMessageSource =
    MiraiMessageSource(
        this,
        ren.natsuyuk1.comet.api.message.MessageSource.MessageSourceType.values()[kind.ordinal],
        time,
        fromId,
        targetId
    )
