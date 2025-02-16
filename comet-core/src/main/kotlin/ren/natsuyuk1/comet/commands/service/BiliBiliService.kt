package ren.natsuyuk1.comet.commands.service

import io.ktor.client.call.*
import io.ktor.client.plugins.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.coroutines.launch
import moe.sdl.yabapi.data.search.results.UserResult
import moe.sdl.yabapi.data.video.VideoInfo
import moe.sdl.yabapi.util.encoding.bv
import ren.natsuyuk1.comet.api.command.PlatformCommandSender
import ren.natsuyuk1.comet.api.message.MessageWrapper
import ren.natsuyuk1.comet.api.message.buildMessageWrapper
import ren.natsuyuk1.comet.api.session.Session
import ren.natsuyuk1.comet.api.session.expire
import ren.natsuyuk1.comet.api.session.registerTimeout
import ren.natsuyuk1.comet.api.user.CometUser
import ren.natsuyuk1.comet.consts.cometClient
import ren.natsuyuk1.comet.consts.json
import ren.natsuyuk1.comet.network.thirdparty.bilibili.DynamicApi
import ren.natsuyuk1.comet.network.thirdparty.bilibili.SearchApi
import ren.natsuyuk1.comet.network.thirdparty.bilibili.UserApi
import ren.natsuyuk1.comet.network.thirdparty.bilibili.VideoApi
import ren.natsuyuk1.comet.network.thirdparty.bilibili.feed.toMessageWrapper
import ren.natsuyuk1.comet.network.thirdparty.bilibili.user.asReadable
import ren.natsuyuk1.comet.network.thirdparty.bilibili.video.toMessageWrapper
import ren.natsuyuk1.comet.util.toMessageWrapper
import ren.natsuyuk1.comet.utils.coroutine.ModuleScope
import ren.natsuyuk1.comet.utils.json.serializeTo
import ren.natsuyuk1.comet.utils.math.NumberUtil.getBetterNumber
import ren.natsuyuk1.comet.utils.string.StringUtil.isNumeric
import kotlin.time.Duration.Companion.seconds

typealias PendingSearchResult = List<UserResult>

object BiliBiliService {
    val scope = ModuleScope("comet-bili-service")

    class BiliBiliUserQuerySession(
        contact: PlatformCommandSender,
        cometUser: CometUser?,
        private val pendingSearchResult: PendingSearchResult
    ) : Session(contact, cometUser) {
        override suspend fun process(message: MessageWrapper) {
            val index = message.parseToString().toIntOrNull()

            if (index == null) {
                contact.sendMessage("请输入正确的编号!".toMessageWrapper())
            } else {
                val result = pendingSearchResult.getOrNull(index - 1)

                if (result == null) {
                    contact.sendMessage("请输入正确的编号!".toMessageWrapper())
                } else {
                    contact.sendMessage("🔍 正在查询用户 ${result.uname} 的信息...".toMessageWrapper())

                    expire()

                    scope.launch { queryUser(contact, result.mid!!) }
                }
            }
        }
    }

    suspend fun processUserSearch(
        subject: PlatformCommandSender,
        sender: PlatformCommandSender,
        id: Long = 0,
        keyword: String = ""
    ) = scope.launch {
        if (id != 0L) {
            queryUser(subject, id)
        } else {
            val searchResult = SearchApi.searchUser(keyword)?.data

            if (searchResult == null || searchResult.firstOrNull() !is UserResult) {
                subject.sendMessage("找不到你想要搜索的用户, 可能不存在哦".toMessageWrapper())
            } else {
                @Suppress("UNCHECKED_CAST")
                searchResult as PendingSearchResult

                if (searchResult.size == 1) {
                    queryUser(subject, searchResult.first().mid!!)
                } else {
                    val user: CometUser? = CometUser.getUser(sender.id, subject.platform)
                    BiliBiliUserQuerySession(
                        subject,
                        user,
                        searchResult
                    ).registerTimeout(15.seconds)

                    val request = buildMessageWrapper {
                        appendTextln("请选择你欲搜索的 UP 主 >")

                        appendLine()

                        searchResult.take(5).forEachIndexed { index, userResult ->
                            appendTextln("${index + 1} >> ${userResult.uname} (${userResult.mid})")
                        }

                        appendLine()

                        appendText("请在 15 秒内回复指定 UP 主编号")
                    }

                    subject.sendMessage(request)
                }
            }
        }
    }

    suspend fun queryUser(subject: PlatformCommandSender, id: Long = 0) = scope.launch {
        val space = UserApi.getUserSpace(id)
        val card = UserApi.getUserCard(id)

        if (space == null || card == null) {
            subject.sendMessage("❌ 找不到对应 UID 的用户信息, 可能是 B 站问题?".toMessageWrapper())
            return@launch
        }

        subject.sendMessage(
            buildMessageWrapper {
                appendText("${space.name}")

                if (space.vip?.asReadable()?.isNotBlank() == true) {
                    appendText(" | ${space.vip?.asReadable()}")
                }

                appendLine()

                if (space.official?.asReadable()?.isNotBlank() == true) {
                    appendText("${space.official?.asReadable()}")
                }

                appendLine()
                appendLine()

                appendTextln("签名 >> ${space.bio}")
                appendLine()
                appendTextln("粉丝 ${card.follower?.getBetterNumber()} | 获赞 ${card.like?.getBetterNumber()}")
                appendLine()
                appendText("\uD83D\uDD17 https://space.bilibili.com/${space.mid}")
            }
        )
    }

    private val pureNumberRegex by lazy { Regex("""^([aA][vV]\d+|[bB][vV]\w+|[eE][pP]\d+|[mM][dD]\d+|[sS]{2}\d+)$""") }
    private val shortLinkRegex by lazy { Regex("""^(https?://)?(www\.)?b23\.tv/(\w+)$""") }
    private val bvAvUrlRegex by lazy { Regex("""^(https?://)?(www\.)?bilibili\.com/video/([bB][vV]\w+|[aA][vV]\d+)""") }

    private suspend fun parseVideoNumber(input: String): String? {
        var s = input.filterNot { it.isWhitespace() }
        if (s.matches(pureNumberRegex)) return s
        if (shortLinkRegex.matches(s)) {
            try {
                cometClient.client.config { followRedirects = false }.get(s).bodyAsText().serializeTo(json)
            } catch (e: RedirectResponseException) {
                s = e.response.headers["Location"] ?: run {
                    return null
                }
            }
        }

        bvAvUrlRegex.find(s)?.groupValues?.getOrNull(3)?.let { return it }

        return null
    }

    suspend fun processVideoSearch(subject: PlatformCommandSender, input: String) {
        val video = parseVideoNumber(input)

        if (video == null) {
            subject.sendMessage("请输入有效的 av/bv/视频链接!".toMessageWrapper())
            return
        }

        val videoInfo = if (video.startsWith("av")) {
            VideoApi.getVideoInfo(video.bv)
        } else if (video.startsWith("BV") || video.startsWith("bv")) {
            VideoApi.getVideoInfo(video)
        } else {
            null
        }

        if (videoInfo == null) {
            subject.sendMessage("找不到你想要搜索的视频".toMessageWrapper())
        } else {
            videoInfo.onSuccess {
                if (it !is VideoInfo) {
                    subject.sendMessage("找不到你想要搜索的视频".toMessageWrapper())
                } else {
                    it.toMessageWrapper().let { mw -> subject.sendMessage(mw) }
                }
            }.onFailure {
                subject.sendMessage("获取视频信息失败, 等一会再试试吧".toMessageWrapper())
            }
        }
    }

    private val dynamicPattern by lazy { Regex("""https://t.bilibili.com/(\d+)""") }

    suspend fun processDynamicSearch(subject: PlatformCommandSender, dynamicID: String) {
        val dynamic =
            if (dynamicID.isNumeric()) {
                dynamicID.toLongOrNull()
            } else {
                dynamicPattern.find(dynamicID)?.groupValues?.getOrNull(
                    1
                )?.toLongOrNull()
            }

        if (dynamic == null) {
            subject.sendMessage("请输入有效的动态 ID 或链接!".toMessageWrapper())
        } else {
            DynamicApi.getDynamic(dynamic)
                .onSuccess { fcn ->
                    fcn?.toMessageWrapper()?.let { mw -> subject.sendMessage(mw) }
                }.onFailure {
                    subject.sendMessage("获取动态失败, 等一会再试试吧".toMessageWrapper())
                }
        }
    }
}
