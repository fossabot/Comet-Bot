package ren.natsuyuk1.comet.telegram.event

import com.github.kotlintelegrambot.dispatcher.handlers.MessageHandlerEnvironment
import ren.natsuyuk1.comet.api.event.impl.message.GroupMessageEvent
import ren.natsuyuk1.comet.api.event.impl.message.MessageEvent
import ren.natsuyuk1.comet.api.event.impl.message.PrivateMessageEvent
import ren.natsuyuk1.comet.telegram.TelegramComet
import ren.natsuyuk1.comet.telegram.contact.toCometGroup
import ren.natsuyuk1.comet.telegram.contact.toCometGroupMember
import ren.natsuyuk1.comet.telegram.contact.toCometUser
import ren.natsuyuk1.comet.telegram.util.toMessageWrapper
import ren.natsuyuk1.comet.utils.string.blankIfNull

suspend fun MessageHandlerEnvironment.toCometEvent(comet: TelegramComet, isCommand: Boolean = false): MessageEvent? {
    return when (message.chat.type) {
        "group", "supergroup" -> this.toCometGroupEvent(comet, isCommand)
        "private" -> this.toCometPrivateEvent(comet, isCommand)
        else -> null
    }
}

suspend fun MessageHandlerEnvironment.toCometGroupEvent(comet: TelegramComet, isCommand: Boolean): GroupMessageEvent {
    return GroupMessageEvent(
        comet = comet,
        subject = this.message.chat.toCometGroup(comet),
        sender = this.message.from!!.toCometGroupMember(comet, this.message.chat.id),
        senderName = this.message.from?.username
            ?: (this.message.from!!.firstName.blankIfNull() + " " + this.message.from!!.lastName.blankIfNull()).trim(),
        message = this.message.toMessageWrapper(comet, isCommand),
        time = this.message.date,
        messageID = this.message.messageId
    )
}

suspend fun MessageHandlerEnvironment.toCometPrivateEvent(
    comet: TelegramComet,
    isCommand: Boolean
): PrivateMessageEvent {
    return PrivateMessageEvent(
        comet = comet,
        subject = this.message.chat.toCometUser(comet),
        sender = this.message.chat.toCometUser(comet),
        senderName = this.message.from?.username
            ?: (this.message.from!!.firstName.blankIfNull() + " " + this.message.from!!.lastName.blankIfNull()).trim(),
        message = this.message.toMessageWrapper(comet, isCommand),
        time = this.message.date,
        messageID = this.message.messageId
    )
}
