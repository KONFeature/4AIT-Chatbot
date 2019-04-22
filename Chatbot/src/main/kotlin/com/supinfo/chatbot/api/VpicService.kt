package com.supinfo.chatbot.api

import com.supinfo.chatbot.api.dto.Manufacturer
import io.reactivex.Single
import retrofit2.http.GET

/**
 * Service helping us the vpic API
 */
interface VpicService {

    @GET("/api/vehicles/getallmanufacturers")
    fun allManufecturers() : Single<VpicResponse<List<Manufacturer>>>

}
