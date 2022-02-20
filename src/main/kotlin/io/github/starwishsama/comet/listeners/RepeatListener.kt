/*
 * Copyright (c) 2019-2021 StarWishsama.
 *
 * 此源代码的使用受 GNU General Affero Public License v3.0 许可证约束, 欲阅读此许可证, 可在以下链接查看.
 *  Use of this source code is governed by the GNU AGPLv3 license which can be found through the following link.
 *
 * https://github.com/StarWishsama/Comet-Bot/blob/master/LICENSE
 *
 */

package io.github.starwishsama.comet.listeners

import io.github.starwishsama.comet.api.command.CommandManager
import io.github.starwishsama.comet.managers.GroupConfigManager
import kotlinx.coroutines.runBlocking
import net.mamoe.mirai.contact.Group
import net.mamoe.mirai.contact.isBotMuted
import net.mamoe.mirai.event.events.GroupMessageEvent
import net.mamoe.mirai.message.data.*

object RepeatListener : INListener {
    override val name: String
        get() = "复读机"

    private val repeatCachePool = mutableMapOf<Long, RepeatInfo>()

    @EventHandler
    fun listen(event: GroupMessageEvent) {
        if (GroupConfigManager.getConfig(event.group.id)?.canRepeat != true
            || event.group.isBotMuted
            || CommandManager.getCommandPrefix(event.message.contentToString()).isNotEmpty()
        ) {
            return
        }

        val groupId = event.group.id
        val repeatInfo = repeatCachePool[groupId]

        if (repeatInfo == null) {
            repeatCachePool[groupId] = RepeatInfo(mutableListOf(event.message))
            return
        }

        repeatInfo.handleRepeat(event.group, event.message)
    }
}

data class RepeatInfo(
    val messageCache: MutableList<MessageChain> = mutableListOf(),
    var lastRepeat: MessageChain = EmptyMessageChain
) {
    fun handleRepeat(group: Group, message: MessageChain) {
        if (messageCache.isEmpty()) {
            messageCache.add(message)
            return
        }

        val previousMessage = messageCache.last()
        val previousSenderId = previousMessage.source.fromId

        if (previousSenderId != message.sourceOrNull?.fromId && previousMessage.contentEquals(
                message,
                ignoreCase = false,
                strict = true
            )
        ) {
            messageCache.add(message)
        } else {
            // No one repeat now, clear it
            lastRepeat = EmptyMessageChain
            messageCache.clear()
            return
        }

        if (messageCache.size > 1 && !previousMessage.contentEquals(lastRepeat, ignoreCase = false, strict = true)) {
            runBlocking {
                group.sendMessage(processRepeatMessage(previousMessage))
            }

            // Set last repeat message
            lastRepeat = previousMessage

            messageCache.clear()
        }
    }

    private fun processRepeatMessage(message: MessageChain): MessageChain {
        // 避免复读过多图片刷屏
        val count = message.filterIsInstance<Image>().count()

        if (count <= 2L) {
            val revampChain = ArrayList<Message>()

            message.forEach {
                val msg: SingleMessage = if (it is PlainText) {
                    PlainText(
                        it.content.replace("你".toRegex(), "他")
                            .replace("我".toRegex(), "你")
                    )
                } else {
                    it
                }
                revampChain.add(msg)
            }

            return revampChain.toMessageChain()
        } else {
            return EmptyMessageChain
        }
    }
}