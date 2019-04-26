package com.supinfo.chatbot

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class ChatbotApplication

fun main(args: Array<String>) {
	runApplication<ChatbotApplication>(*args)
}

// Extension function to simplify ur life
fun String.containIgnoreCase(str: String?): Boolean {
	if (str.isNullOrEmpty()) return false

	val length = str.length
	for (i in this.length - length downTo 0) {
		if (this.regionMatches(i, str, 0, length, ignoreCase = true))
			return true
	}
	return false
}
