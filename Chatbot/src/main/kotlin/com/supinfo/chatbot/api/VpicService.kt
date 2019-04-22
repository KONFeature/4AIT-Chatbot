package com.supinfo.chatbot.api

import com.supinfo.chatbot.api.dto.Make
import com.supinfo.chatbot.api.dto.Manufacturer
import com.supinfo.chatbot.api.dto.Model
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Service helping us the vpic API
 */
interface VpicService {

    @GET("/api/vehicles/getallmanufacturers")
    fun allManufacturers() : Single<VpicResponse<List<Manufacturer>>>

    @GET("/api/vehicles/getallmanufacturers")
    fun allManufacturersPerPage(@Query("page") page: Long) : Single<VpicResponse<List<Manufacturer>>>

    @GET("/api/vehicles/GetAllMakes")
    fun allMakes() : Single<VpicResponse<List<Make>>>

    @GET("/api/vehicles/GetMakeForManufacturer/{manufacturerName}")
    fun makeForManufacturer(@Path("manufacturerName") manufacturer: String) : Single<VpicResponse<List<Make>>>

    @GET("/api/vehicles/GetModelsForMakeId/{makeId}")
    fun modelsForMake(@Path("makeId") makeId: Long) : Single<VpicResponse<List<Model>>>

}
