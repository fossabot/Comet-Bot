package io.github.starwishsama.comet.api.thirdparty.penguinstats

import io.github.starwishsama.comet.BotVariables
import io.github.starwishsama.comet.api.thirdparty.penguinstats.data.MatrixResponse
import retrofit2.Retrofit
import retrofit2.converter.jackson.JacksonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

object PenguinStats {
    val api: PenguinStatsAPI

    init {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://penguin-stats.io/PenguinStats/api/v2/")
            .addConverterFactory(JacksonConverterFactory.create(BotVariables.mapper))
            .client(BotVariables.client)
            .build()

        api = retrofit.create(PenguinStatsAPI::class.java)
    }
}

enum class ArkNightServer {
    CN, US, JP, KR
}

interface PenguinStatsAPI {
    @GET("result/matrix")
    fun getMatrix(
        @Query("stageFilter")
        stageFilter: List<Long> = listOf(),
        @Query("itemFilter")
        itemFilter: List<String> = listOf(),
        @Query("server")
        serverName: String = ArkNightServer.CN.toString(),
        @Query("show_closed_zones")
        showClosedZone: Boolean = false,
        @Query("is_personal")
        isPersonal: Boolean = false
    ): MatrixResponse
}