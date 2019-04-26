package com.supinfo.chatbot.Utils


object TerminalHelper {

}

interface TerminalItem {

    /**
     * Return the terminal representation of th eitem
     */
    fun getTerminalString() : String

    /**
     * Return if a terminal input is equals to this object
     * @param strict is to known if we need a strict equals, or a "like" equal
     */
    fun equalsTerminalInput(input: String, strict: Boolean) : Boolean

}

/**
 * Terminal requested action
 */
enum class TerminalAction {
    PREV,
    NEXT,
    EXIT,
    DONE
}

/**
 * Class representing a response from the terminal service
 */
data class TerminalResponse<T>(
        val action: TerminalAction,
        val result: T?
) {
    companion object {
        public fun <T> exit(data: T?) = TerminalResponse(TerminalAction.EXIT, data)
        fun <T> prev(data: T?) = TerminalResponse(TerminalAction.PREV, data)
        fun <T> next(data: T?) = TerminalResponse(TerminalAction.NEXT, data)
        fun <T> done(data: T) = TerminalResponse(TerminalAction.DONE, data)
    }
}
