package com.supinfo.chatbot.data.db.entities

import com.supinfo.chatbot.Utils.TerminalItem
import javax.persistence.*

/**
 * Classe representing an extractor of question in ur db
 */
@Entity
data class Extractor (

        @Id
        @GeneratedValue(strategy = GenerationType.AUTO)
        val id: Long = 0,

        @ManyToOne
        val keyword: Keyword = Keyword(),

        /**
         * If we don't have eany selector we select all of the keyword target
         */
        @ManyToOne(optional = true)
        val selector: Selector? = null,

        @Column
        var score: Long = 0
) : TerminalItem, Comparable<Extractor> {
        // Sort the extractor by score in a list
        override fun compareTo(other: Extractor) = score.compareTo(other.score)

        override fun getTerminalString() = "Pour le mot clé ${keyword.word} on selectionne des données ${keyword.target} qui corresponde a ${selector?.getTerminalString() ?:"toute"}, avec une priorité de $score"

        override fun equalsTerminalInput(input: String, strict: Boolean): Boolean {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }
}
