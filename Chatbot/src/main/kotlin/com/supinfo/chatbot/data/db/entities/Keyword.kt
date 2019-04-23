package com.supinfo.chatbot.data.db.entities

import javax.persistence.*

@Entity
data class Keyword(

        @Id
        @GeneratedValue(strategy = GenerationType.AUTO)
        val id: Long,

        @Column
        val word: String

)
