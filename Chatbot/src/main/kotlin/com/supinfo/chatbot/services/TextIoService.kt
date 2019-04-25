package com.supinfo.chatbot.services

import org.beryx.textio.TextIO
import org.beryx.textio.TextIoFactory
import org.springframework.stereotype.Service

@Service
class TextIoService {

    companion object {
        const val PATTERN_NUMBER_OR_EXIT = "[0-9]+|(?i)(exit)"
        const val PATTERN_NUMBER_NAV_OR_EXIT = "[0-9]+|(?i)(exit)|(?i)(prev)|(?i)(next)"
    }

    // Variable representing ur terminal
    private var textIo: TextIO? = null

    /**
     * Launch the text IO instance
     */
    fun launch() {
        textIo?:run {
            textIo = TextIoFactory.getTextIO()
        }
    }

    /**
     * Stop the text IO instance
     */
    fun stop(){
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
    fun askUserQuestion(msg: String) : String {
        textIo?.textTerminal?.println("chatbot: $msg")
        return textIo?.newStringInputReader()?.read("you")?:""
    }

    /**
     * Function used to ask a chatbot question to the user with a specific pattern for the answer
     */
    fun askUserQuestion(msg: String, pattern: String) : String {
        textIo?.textTerminal?.println("chatbot: $msg")
        return textIo?.newStringInputReader()?.withPattern(pattern)?.read("you")?:""
    }

    /**
     * Function used to ask a chatbot boolean question to the user
     */
    fun askUserBoolQuestion(msg: String, default: Boolean) : Boolean {
        textIo?.textTerminal?.println("chatbot: $msg")
        return textIo?.newBooleanInputReader()?.withDefaultValue(default)?.read("you")?:false
    }

}
