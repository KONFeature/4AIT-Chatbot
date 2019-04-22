package com.supinfo.chatbot.api.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class VehicleType(

        @JsonProperty("MakeId")
        val makeId: Long,

        @JsonProperty("MakeName")
        val makeName: String?,

        @JsonProperty("VehicleTypeId")
        val vehicleTypeId: Long,

        @JsonProperty("VehicleTypeName")
        val vehicleTypeName: String?
)
