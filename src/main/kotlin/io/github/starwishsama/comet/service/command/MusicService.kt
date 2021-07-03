/*
 * Copyright (c) 2019-2021 StarWishsama.
 *
 * 此源代码的使用受 GNU General Affero Public License v3.0 许可证约束, 欲阅读此许可证, 可在以下链接查看.
 *  Use of this source code is governed by the GNU AGPLv3 license which can be found through the following link.
 *
 * https://github.com/StarWishsama/Comet-Bot/blob/master/LICENSE
 *
 */

package io.github.starwishsama.comet.service.command

import io.github.starwishsama.comet.CometVariables
import io.github.starwishsama.comet.api.thirdparty.music.ThirdPartyMusicApi
import io.github.starwishsama.comet.enums.MusicApiType
import io.github.starwishsama.comet.utils.CometUtil.toChain
import net.mamoe.mirai.contact.Contact
import net.mamoe.mirai.message.data.MessageChain
import net.mamoe.mirai.message.data.MusicKind

object MusicService {
    var plainText: Boolean = false

    fun setTextMode(): MessageChain {
        plainText = !plainText
        return "纯文字模式: $plainText".toChain()
    }

    fun setMusicApi(args: List<String>): MessageChain {
        return if (args.size > 1) {
            when (args[1].uppercase()) {
                "QQ", "TX", "腾讯" -> CometVariables.cfg.musicApi = MusicApiType.QQ
                "NETEASE", "网易", "WY" -> CometVariables.cfg.musicApi = MusicApiType.NETEASE
            }

            toChain("音乐API已修改为 ${CometVariables.cfg.musicApi}")
        } else {
            "/music api [API名称] (QQ/WY)".toChain()
        }
    }

    fun handleMusicSearch(name: String, subject: Contact?): MessageChain {
        return when (CometVariables.cfg.musicApi) {
            MusicApiType.NETEASE -> handleNetEaseMusic(name, subject)
            MusicApiType.QQ -> handleQQMusic(name, subject)
        }
    }

    fun handleNetEaseMusic(name: String, subject: Contact?): MessageChain {
        try {
            val result = ThirdPartyMusicApi.searchNetEaseMusic(name)

            if (result.isEmpty()) {
                return "❌ 找不到你想搜索的音乐".toChain()
            }

            return if (plainText) {
                result[0].toMessageWrapper().toMessageChain(subject)
            } else {
                result[0].toMusicShare(MusicKind.NeteaseCloudMusic)
            }
        } catch (e: Exception) {
            CometVariables.daemonLogger.warning("点歌时出现了意外", e)
            return "❌ 点歌系统开小差了, 稍后再试试吧".toChain()
        }
    }

    fun handleQQMusic(name: String, subject: Contact?): MessageChain {
        try {
            val result = ThirdPartyMusicApi.searchQQMusic(name)

            if (result.isEmpty()) {
                return "❌ 找不到你想搜索的音乐".toChain()
            }

            return if (plainText) {
                result[0].toMessageWrapper().toMessageChain(subject)
            } else {
                result[0].toMusicShare(MusicKind.QQMusic)
            }
        } catch (e: Exception) {
            CometVariables.daemonLogger.warning("点歌时出现了意外", e)
            return "❌ 点歌系统开小差了, 稍后再试试吧".toChain()
        }
    }
}