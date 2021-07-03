/*
 * Copyright (c) 2019-2021 StarWishsama.
 *
 * 此源代码的使用受 GNU General Affero Public License v3.0 许可证约束, 欲阅读此许可证, 可在以下链接查看.
 *  Use of this source code is governed by the GNU AGPLv3 license which can be found through the following link.
 *
 * https://github.com/StarWishsama/Comet-Bot/blob/master/LICENSE
 *
 */

package io.github.starwishsama.comet.exceptions

class TwitterApiException(val code: Int, val reason: String) : ApiException(reason)

class ApiKeyIsEmptyException(apiName: String) : ApiException("$apiName API 的 APIKey 不能为空!")

class EmptyTweetException(val msg: String = "该用户没有发送过推文") : ApiException(msg)
