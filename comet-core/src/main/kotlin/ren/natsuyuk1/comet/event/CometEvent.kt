package ren.natsuyuk1.comet.event

import ren.natsuyuk1.comet.api.Comet
import ren.natsuyuk1.comet.api.command.PlatformCommandSender
import ren.natsuyuk1.comet.api.event.AbstractEvent
import ren.natsuyuk1.comet.api.user.Group

/**
 * 一个基础的 Comet 事件
 *
 * @param comet 触发这一事件的 Comet
 */
class CometEvent(
    val comet: Comet
) : AbstractEvent()

/**
 * 一个基础的非 Comet 驱动事件
 *
 * 例如: 推送消息
 *
 */
abstract class CometBroadcastEvent : AbstractEvent() {
    /**
     * 推送对象
     */
    val broadcastTargets: List<BroadcastTarget> = mutableListOf()
}

data class BroadcastTarget(
    val type: BroadcastType,
    val id: Long
) {
    enum class BroadcastType {
        PRIVATE, GROUP
    }
}

fun PlatformCommandSender.toBroadcastTarget(): BroadcastTarget {
    return when (this) {
        is Group -> BroadcastTarget(BroadcastTarget.BroadcastType.GROUP, id)
        else -> BroadcastTarget(BroadcastTarget.BroadcastType.PRIVATE, id)
    }
}
