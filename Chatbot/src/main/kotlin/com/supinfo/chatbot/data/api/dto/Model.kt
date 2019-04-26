package com.supinfo.chatbot.data.api.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.supinfo.chatbot.Utils.TerminalItem
import com.supinfo.chatbot.containIgnoreCase

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
) : TerminalItem {
    override fun getTerminalString() = "Nom : $modelName, marque : $makeName, id : $id"

    override fun equalsTerminalInput(input: String, strict: Boolean): Boolean {
        return if (!strict) {
            (input.length >= 3 && modelName?.containIgnoreCase(input.trim()) ?: false
                    || id == input.toLongOrNull())
        } else {
            id == input.toLongOrNull()
        }
    }
}
