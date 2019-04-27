package com.supinfo.chatbot.data.db.entities

import com.supinfo.chatbot.Utils.TerminalItem
import javax.persistence.*

@Entity
data class Selector(

        @Id
        @GeneratedValue(strategy = GenerationType.AUTO)
        val id: Long = 0,

        /**
         * The number of word to select after / before the keyword
         */
        @Column
        val nbrWord: Int = 0,

        /**
         * Is the number of word to select is after the keyword ? else we choose before
         */
        @Column
        val after: Boolean = true

) : TerminalItem {
    override fun getTerminalString() = "$nbrWord mots ${if (after) "apres" else "avant"} le mot clé"

    override fun equalsTerminalInput(input: String, strict: Boolean): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}
