package ren.natsuyuk1.comet.commands

import ren.natsuyuk1.comet.api.Comet
import ren.natsuyuk1.comet.api.command.CometCommand
import ren.natsuyuk1.comet.api.command.CommandProperty
import ren.natsuyuk1.comet.api.command.PlatformCommandSender
import ren.natsuyuk1.comet.api.message.MessageWrapper
import ren.natsuyuk1.comet.api.user.CometUser
import ren.natsuyuk1.comet.api.wrapper.WrapperLoader
import ren.natsuyuk1.comet.config.version
import ren.natsuyuk1.comet.consts.coreUpTimer
import ren.natsuyuk1.comet.util.toMessageWrapper
import ren.natsuyuk1.comet.utils.datetime.toFriendly

val VERSION by lazy {
    CommandProperty(
        "version",
        listOf("v", "comet"),
        "查看 Comet 的相关信息",
        "/version - 查看 Comet 的相关信息"
    )
}

class VersionCommand(
    comet: Comet,
    override val sender: PlatformCommandSender,
    override val subject: PlatformCommandSender,
    val message: MessageWrapper,
    user: CometUser
) : CometCommand(comet, sender, subject, message, user, VERSION) {

    override suspend fun run() {
        subject.sendMessage(
            buildString {
                append(
                    """
                    ☄ Comet Bot - $version
                    已运行了 ${coreUpTimer.measureDuration().toFriendly()}
                    Made with ❤
                    """.trimIndent()
                )
                appendLine()
                appendLine()
                append("已加载的服务 >")
                appendLine()
                append(WrapperLoader.getServicesInfo())
            }.toMessageWrapper()
        )
    }
}
