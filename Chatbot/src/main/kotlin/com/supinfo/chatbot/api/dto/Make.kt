package com.supinfo.chatbot.api.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class Make (
        @JsonProperty("Make_ID")
        val id: Long,

        @JsonProperty("Make_Name")
        val name: String?,

        @JsonProperty("Mfr_Name")
        val mfrName: String?
)
