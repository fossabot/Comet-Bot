package ren.natsuyuk1.comet.commands

import io.ktor.client.plugins.*
import io.ktor.http.*
import moe.sdl.yac.parameters.arguments.argument
import mu.KotlinLogging
import ren.natsuyuk1.comet.api.Comet
import ren.natsuyuk1.comet.api.command.CometCommand
import ren.natsuyuk1.comet.api.command.CommandProperty
import ren.natsuyuk1.comet.api.command.PlatformCommandSender
import ren.natsuyuk1.comet.api.message.MessageWrapper
import ren.natsuyuk1.comet.api.user.CometUser
import ren.natsuyuk1.comet.network.thirdparty.jikipedia.JikiPediaAPI
import ren.natsuyuk1.comet.util.toMessageWrapper

private val logger = KotlinLogging.logger {}

val JIKI = CommandProperty(
    "jiki",
    listOf("小鸡百科", "jikipedia"),
    "小鸡词典 - 网络梗百科全书",
    "/jiki [关键词] 搜索对应词可能的意思" +
        "\n\n数据源来自小鸡词典 \uD83D\uDC25 ",
    executeConsumePoint = 8
)

class JikiPediaCommand(
    comet: Comet,
    override val sender: PlatformCommandSender,
    override val subject: PlatformCommandSender,
    message: MessageWrapper,
    user: CometUser
) : CometCommand(comet, sender, subject, message, user, JIKI) {

    private val keyword by argument(help = "搜索关键词")

    override suspend fun run() {
        try {
            if (keyword.length > 60) {
                subject.sendMessage("❌ 请缩短搜索内容至 60 个字符以内".toMessageWrapper())
            } else {
                subject.sendMessage(JikiPediaAPI.search(keyword).toMessageWrapper())
            }
        } catch (e: Exception) {
            if (e is ClientRequestException) {
                when (e.response.status) {
                    HttpStatusCode.Unauthorized -> subject.sendMessage(
                        "\uD83D\uDEA7 已到达小鸡百科搜索上限, 等一会再试吧~".toMessageWrapper()
                    )
                    else -> subject.sendMessage("❓ 搜索内容遁入了黑洞之中, 等一会再试试吧".toMessageWrapper())
                }
            } else {
                subject.sendMessage("❌ 在搜索时出现了问题".toMessageWrapper())
                logger.error(e) { "在搜索小鸡百科内容时出现问题" }
            }
        }
    }
}
