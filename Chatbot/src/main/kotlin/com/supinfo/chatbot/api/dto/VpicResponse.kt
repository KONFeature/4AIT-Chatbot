package com.supinfo.chatbot.api.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class VpicResponse<out T> (

        @JsonProperty("Count")
        val count: Long,

        @JsonProperty("Message")
        val message: String?,

        @JsonProperty("SearchCriteria")
        val searchCriteria: String?,

        @JsonProperty("Results")
        val results: T?
)
