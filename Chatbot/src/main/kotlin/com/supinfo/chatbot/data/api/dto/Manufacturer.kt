package com.supinfo.chatbot.data.api.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.supinfo.chatbot.Utils.TerminalItem
import com.supinfo.chatbot.containIgnoreCase

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
) : TerminalItem {

    @JsonIgnoreProperties(ignoreUnknown = true)
    data class VehicleType(

            @JsonProperty("IsPrimary")
            val isPrimary: Boolean,

            @JsonProperty("Name")
            val name: String?
    )

    override fun getTerminalString() = "Nom commun = $commonName, nom = $name, id = $id, types $vehicleTypes"

    override fun equalsTerminalInput(input: String, strict: Boolean): Boolean {
        return if (!strict) {
            (input.length >= 3 &&
                    (name?.containIgnoreCase(input.trim()) ?: false || commonName?.containIgnoreCase(input.trim()) ?: false)
                    || id == input.toLongOrNull())
        } else {
            id == input.toLongOrNull()
        }
    }
}
