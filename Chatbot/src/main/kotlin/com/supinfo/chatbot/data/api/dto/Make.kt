package com.supinfo.chatbot.data.api.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.supinfo.chatbot.Utils.TerminalItem
import com.supinfo.chatbot.containIgnoreCase

@JsonIgnoreProperties(ignoreUnknown = true)
data class Make(
        @JsonProperty("Make_ID")
        val id: Long,

        @JsonProperty("Make_Name")
        val name: String?,

        @JsonProperty("Mfr_Name")
        val mfrName: String?
) : TerminalItem {

    override fun getTerminalString(): String {
        return "Nom = $name, id = $id"
    }

    override fun equalsTerminalInput(input: String, strict: Boolean): Boolean {
        return if (!strict) {
            (input.length >= 3 && name?.containIgnoreCase(input.trim()) ?: false
                    || id == input.toLongOrNull())
        } else {
            id == input.toLongOrNull()
        }
    }
}
