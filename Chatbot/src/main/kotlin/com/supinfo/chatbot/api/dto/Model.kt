package com.supinfo.chatbot.api.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class Model(

        @JsonProperty("Model_ID")
        val id: Long,

        @JsonProperty("Make_ID")
        val makeId: Long,

        @JsonProperty("Make_Name")
        val makeName: String?,

        @JsonProperty("Model_Name")
        val modelName: String?
)
