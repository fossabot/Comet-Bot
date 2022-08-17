package ren.natsuyuk1.comet.telegram.util

import dev.inmo.tgbotapi.extensions.api.bot.getMe
import dev.inmo.tgbotapi.extensions.api.files.downloadFile
import dev.inmo.tgbotapi.extensions.api.send.media.sendAudio
import dev.inmo.tgbotapi.extensions.api.send.media.sendPhoto
import dev.inmo.tgbotapi.extensions.api.send.sendMessage
import dev.inmo.tgbotapi.requests.abstracts.FileId
import dev.inmo.tgbotapi.requests.abstracts.InputFile
import dev.inmo.tgbotapi.types.ChatId
import dev.inmo.tgbotapi.types.message.content.MessageContent
import dev.inmo.tgbotapi.types.message.content.PhotoContent
import dev.inmo.tgbotapi.types.message.content.TextContent
import ren.natsuyuk1.comet.telegram.TelegramComet
import ren.natsuyuk1.comet.utils.file.absPath
import ren.natsuyuk1.comet.utils.file.cacheDirectory
import ren.natsuyuk1.comet.utils.file.touch
import ren.natsuyuk1.comet.utils.message.*
import java.io.File

private val logger = mu.KotlinLogging.logger {}

suspend fun MessageWrapper.send(comet: TelegramComet, target: ChatId) {
    val textBuffer = StringBuffer()

    getMessageContent().forEach {
        if (it is Text) {
            textBuffer.append(it.parseToString())
        } else if (it is AtElement) {
            textBuffer.append("@${it.userName}")
        }
    }

    if (find<Image>() != null) {
        find<Image>()?.toInputFile()?.let { comet.bot.sendPhoto(target, it, text = textBuffer.toString()) }
    } else if (find<Voice>() != null) {
        find<Voice>()?.toInputFile()?.let { comet.bot.sendAudio(target, it) }
    } else {
        comet.bot.sendMessage(target, textBuffer.toString())
    }
}

suspend fun MessageContent.toMessageWrapper(comet: TelegramComet, isCommand: Boolean): MessageWrapper {
    return when (val content = this) {
        is PhotoContent -> {
            buildMessageWrapper {
                val photoIDs = mutableSetOf<FileId>()

                content.mediaCollection.forEach { photoIDs.add(it.fileId) }

                photoIDs.forEach {
                    val tempFile = File(cacheDirectory, it.fileId)
                    tempFile.touch()
                    tempFile.deleteOnExit()

                    kotlin.runCatching {
                        comet.bot.downloadFile(it)
                    }.onSuccess { resp ->
                        try {
                            tempFile.writeBytes(resp)
                            appendElement(Image(filePath = tempFile.absPath))
                            appendText(content.text ?: "")
                        } catch (e: Exception) {
                            logger.warn(e) { "在转换 Telegram 图片为 Message Wrapper 时出现问题" }
                        }
                    }.onFailure { t ->
                        logger.warn(t) { "在转换 Telegram 图片为 Message Wrapper 时出现问题" }
                        return@forEach
                    }
                }
            }
        }

        is TextContent -> {
            buildMessageWrapper {
                if (isCommand)
                    appendText(content.text.replace(comet.bot.getMe().username.username, ""))
                else
                    appendText(content.text)
            }
        }

        else -> MessageWrapper().setUsable(false)
    }
}

fun Image.toInputFile(): InputFile? {
    return when {
        url.isNotBlank() -> InputFile.fromUrl(url)
        filePath.isNotBlank() -> InputFile.fromFile(File(filePath))
        else -> null
    }
}

fun Voice.toInputFile(): InputFile? = if (filePath.isNotBlank()) InputFile.fromFile(File(filePath)) else null
