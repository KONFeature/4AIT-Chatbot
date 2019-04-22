package com.supinfo.chatbot.api.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class VehicleType(

        @JsonProperty("VehicleTypeId")
        val id: Long,

        @JsonProperty("VehicleTypeName")
        val name: String?,

        @JsonProperty("MakeId")
        val makeId: Long,

        @JsonProperty("MakeName")
        val makeName: String?
)
