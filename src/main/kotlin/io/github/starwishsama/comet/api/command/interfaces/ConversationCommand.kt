/*
 * Copyright (c) 2019-2021 StarWishsama.
 *
 * 此源代码的使用受 GNU General Affero Public License v3.0 许可证约束, 欲阅读此许可证, 可在以下链接查看.
 *  Use of this source code is governed by the GNU AGPLv3 license which can be found through the following link.
 *
 * https://github.com/StarWishsama/Comet-Bot/blob/master/LICENSE
 *
 */

package io.github.starwishsama.comet.api.command.interfaces

import io.github.starwishsama.comet.objects.CometUser
import io.github.starwishsama.comet.sessions.Session
import net.mamoe.mirai.event.events.MessageEvent

/**
 * 交互式命令
 *
 * 支持接受输入内容并处理.
 *
 * 需要创建一个 [Session] 以触发监听
 */
interface ConversationCommand {
    suspend fun handle(event: MessageEvent, user: CometUser, session: Session)
}