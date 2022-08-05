package ren.natsuyuk1.comet.network.thirdparty.bangumi

import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ren.natsuyuk1.comet.consts.cometClient
import ren.natsuyuk1.comet.network.thirdparty.bangumi.const.MAIN_DOMAIN
import ren.natsuyuk1.comet.network.thirdparty.bangumi.const.buildSubjectUrl
import ren.natsuyuk1.comet.network.thirdparty.bangumi.data.common.SearchType
import ren.natsuyuk1.comet.network.thirdparty.bangumi.parser.Subject
import ren.natsuyuk1.comet.network.thirdparty.bangumi.parser.SubjectSearchResults
import java.io.IOException

internal val bgmCrawler by lazy {
    BangumiCrawler()
}

class BangumiCrawler {
    suspend fun fetchSubject(url: String) = withContext(Dispatchers.IO) {
        Subject(cometClient.client.get(url) ?: throw IOException("Unable to fetch bangumi page: $url"))
    }

    suspend fun fetchSubject(id: Long) = withContext(Dispatchers.IO) {
        fetchSubject(buildSubjectUrl(id))
    }

    private suspend fun rawSearch(type: SearchType, keyword: String, exactMode: Boolean = false): String {
        val path = when (type) {
            is SearchType.Subject -> "/subject_search/"
            is SearchType.Person -> "/mono_search/"
        }
        val url = MAIN_DOMAIN + path + keyword.encodeURLPath()
        return cometClient.client.get("$url?cat=${type.category}${if (exactMode) "&legacy=1" else ""}") ?: ""
    }

    suspend fun searchSubject(
        type: SearchType.Subject,
        keyword: String,
        exactMode: Boolean = false
    ): SubjectSearchResults = SubjectSearchResults(rawSearch(type, keyword, exactMode))
}
