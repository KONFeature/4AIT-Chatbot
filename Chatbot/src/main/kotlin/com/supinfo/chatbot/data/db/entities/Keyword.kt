package com.supinfo.chatbot.data.db.entities

import com.supinfo.chatbot.data.api.dto.Make
import com.supinfo.chatbot.data.api.dto.Model
import com.supinfo.chatbot.data.api.dto.VehicleType
import javax.persistence.*

@Entity
data class Keyword(
        @Id
        @GeneratedValue(strategy = GenerationType.AUTO)
        var id: Long = 0,

        @Column
        val word: String = "",

        @Enumerated(EnumType.STRING)
        val target: KeywordTarget = KeywordTarget.MODEL
)

/**
 * Class representing the different target possible for a keyword
 */
enum class KeywordTarget {
        MODEL,
        MAKE,
        TYPE,
        MANUFACTURER
}
