package ren.natsuyuk1.comet.network.thirdparty.bangumi

import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.TimeZone
import ren.natsuyuk1.comet.api.task.TaskManager
import ren.natsuyuk1.comet.consts.cometClient
import ren.natsuyuk1.comet.consts.json
import ren.natsuyuk1.comet.network.thirdparty.bangumi.data.BangumiOnlineScheduleData
import ren.natsuyuk1.comet.utils.json.serializeTo
import kotlin.time.Duration.Companion.days

object BangumiOnlineApi {
    private const val API = "https://bangumi.online"
    private var cache: BangumiOnlineScheduleData

    init {
        runBlocking {
            cache = fetchBangumiSchedule()
        }

        TaskManager.registerTask(7.days) {
            cache = fetchBangumiSchedule()
        }
    }

    fun getCache(): BangumiOnlineScheduleData = cache

    suspend fun fetchBangumiSchedule(): BangumiOnlineScheduleData =
        cometClient.client.post("$API/api/serve/schedule") {
            setBody(
                FormDataContent(
                    Parameters.build {
                        append("tz", TimeZone.currentSystemDefault().id)
                    }
                )
            )
        }.bodyAsText().serializeTo(json)
}
