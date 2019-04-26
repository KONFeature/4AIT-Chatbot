package com.supinfo.chatbot.services

import com.supinfo.chatbot.Utils.TerminalItem
import com.supinfo.chatbot.Utils.TerminalResponse
import org.beryx.textio.TextIO
import org.beryx.textio.TextIoFactory
import org.springframework.stereotype.Service

@Service
class TextIoService {

    companion object {
        const val PATTERN_NUMBER_OR_EXIT = "[0-9]+|(?i)(exit)"
        const val PATTERN_NUMBER_NAV_OR_EXIT = "[0-9]+|(?i)(exit)|(?i)(prev)|(?i)(next)"

        const val ITEMS_PER_PAGE = 15
    }

    // Variable representing ur terminal
    private var textIo: TextIO? = null

    /**
     * Launch the text IO instance
     */
    fun launch() {
        textIo ?: run {
            textIo = TextIoFactory.getTextIO()
        }
    }

    /**
     * Stop the text IO instance
     */
    fun stop() {
        textIo?.dispose()
        textIo = null
    }

    /**
     * Function used to display a simple message
     */
    fun displayMessage(msg: String) {
        textIo?.textTerminal?.println(msg)
    }

    /**
     * Function used to display a chatbot message
     */
    fun displayChatbotMessage(msg: String) {
        displayMessage("chatbot: $msg")
    }

    /**
     * Function used to ask a chatbot question to the user
     */
    fun askUserQuestion(msg: String): String {
        textIo?.textTerminal?.println("chatbot: $msg")
        return textIo?.newStringInputReader()?.read("vous") ?: ""
    }

    /**
     * Function used to ask a chatbot question to the user with a specific pattern for the answer
     */
    fun askUserQuestion(msg: String, pattern: String): String {
        textIo?.textTerminal?.println("chatbot: $msg")
        return textIo?.newStringInputReader()?.withPattern(pattern)?.read("vous") ?: ""
    }

    /**
     * Function used to ask a chatbot boolean question to the user
     */
    fun askUserBoolQuestion(msg: String, default: Boolean): Boolean {
        textIo?.textTerminal?.println("chatbot: $msg")
        return textIo?.newBooleanInputReader()?.
                withDefaultValue(default)?.
                withTrueInput("O")?.
                withFalseInput("N")?.
                withInputTrimming(true)?.
                read("vous") ?: false
    }

    /**
     * Select an item from a list
     */
    fun selectItemFromList(list: List<TerminalItem>, prevPage: Boolean, nextPage: Boolean): TerminalResponse<TerminalItem> {
        val choices = ArrayList<TerminalItem>()
        var currentPage = 0
        while (choices.isEmpty()) {
            // Cut the list for a better prompt
            val searchingEnd = if ((ITEMS_PER_PAGE * currentPage) + ITEMS_PER_PAGE > list.lastIndex) list.lastIndex else ((ITEMS_PER_PAGE * currentPage) + ITEMS_PER_PAGE)
            val searchingList = list.subList(
                    (currentPage * ITEMS_PER_PAGE),
                    searchingEnd)

            // Show it to the user
            displayChatbotMessage("Il y a ${searchingList.size} items sur cette page, page ${currentPage + 1}/${list.size.div(ITEMS_PER_PAGE) + 1} ${if (nextPage || prevPage) "d'autre page sont chargeable." else "."}")
            searchingList.forEach { displayMessage(it.getTerminalString()) }

            // prepare the local navigation
            val localPrevPossible = currentPage > 0
            val localNextPossible = searchingEnd + 1 < list.lastIndex

            val actionAnswer = askUserQuestion("Merci d'entrer le nom ou l'id de l'item que vous voulez, " +
                    (if (localPrevPossible || prevPage) "prec pour la page precedente, " else "") +
                    (if (localNextPossible || nextPage) "suiv pour la page suivante, " else "") +
                    "exit pour quitter"
            )
            // Exit action
            if (actionAnswer.equals("exit", true)) {
                return TerminalResponse.exit(choices.firstOrNull())
            } else if (actionAnswer.equals("prec", true)) {
                if (!localPrevPossible && prevPage) {
                    return TerminalResponse.prev(choices.firstOrNull())
                } else if (localPrevPossible) {
                    currentPage--
                    continue
                }
            } else if (actionAnswer.equals("suiv", true)) {
                if (!localNextPossible && nextPage) {
                    return TerminalResponse.next(choices.firstOrNull())
                } else if (localNextPossible) {
                    currentPage++
                    continue
                }
            } else {
                // Find some answer correspond to a choice
                list.forEach { if (it.equalsTerminalInput(actionAnswer, false)) choices.add(it) }
            }
        }

        // If we have a single item comming out of the list we return it
        if(choices.size == 1) return TerminalResponse.done(choices.first())

        // else we ask the user to pick one by it's ID
        var selectedItem: TerminalItem? = null
        while(selectedItem == null) {
            // Show it to the user
            displayMessage("Il y a ${choices.size} items qui corresponde a votre demande")
            choices.forEach { displayMessage(it.getTerminalString()) }

            val actionAnswer = askUserQuestion("Merci d'entrer l'id de l'item que vous souhaitez, ou exit pour quitter")

            // Exit action
            if (actionAnswer.equals("exit", true)) {
                return TerminalResponse.exit(choices.firstOrNull())
            } else {
                // Find some answer correspond to a choice
                selectedItem = list.firstOrNull { it.equalsTerminalInput(actionAnswer.trim(), true) }
            }
        }

        // Return the result
        displayChatbotMessage("Vous avez selectionnez ${selectedItem.getTerminalString()}")
        return TerminalResponse.done(selectedItem)
    }

}
