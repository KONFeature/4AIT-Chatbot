package com.supinfo.chatbot.api.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * Class represnting a manufacturer from vpic
 */
@JsonIgnoreProperties(ignoreUnknown = true)
data class Manufacturer(

        @JsonProperty("Mfr_ID")
        val id: Long,

        @JsonProperty("Country")
        val country: String?,

        @JsonProperty("Mfr_CommonName")
        val commonName: String?,

        @JsonProperty("Mfr_Name")
        val name: String?,

        @JsonProperty("VehicleTypes")
        val vehicleTypes: Collection<VehicleType>?
) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    data class VehicleType(

            @JsonProperty("IsPrimary")
            val isPrimary: Boolean,

            @JsonProperty("Name")
            val name: String?
    )
}
