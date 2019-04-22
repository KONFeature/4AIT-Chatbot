package com.supinfo.chatbot.api

import com.supinfo.chatbot.api.dto.Make
import com.supinfo.chatbot.api.dto.Manufacturer
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Path

/**
 * Service helping us the vpic API
 */
interface VpicService {

    @GET("/api/vehicles/getallmanufacturers")
    fun allManufacturers() : Single<VpicResponse<List<Manufacturer>>>

    @GET("/api/vehicles/GetMakeForManufacturer/{manufacturerName}")
    fun makeForManufacturer(@Path("manufacturerName") manufacturer: String) : Single<VpicResponse<List<Make>>>

}
