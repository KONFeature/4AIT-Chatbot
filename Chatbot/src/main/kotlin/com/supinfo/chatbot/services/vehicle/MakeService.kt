package com.supinfo.chatbot.services.vehicle

import antlr.StringUtils
import com.supinfo.chatbot.data.api.VpicService
import com.supinfo.chatbot.data.api.dto.Make
import com.supinfo.chatbot.data.api.dto.Manufacturer
import com.supinfo.chatbot.services.TextIoService
import org.beryx.textio.TextIO
import org.springframework.stereotype.Service

/**
 * Service handeling all the vehicule make logic
 */
@Service
class MakeService(private val textIoService: TextIoService,
                  private val vpicService: VpicService) {

    /**
     * Pick a make from terminal with no information
     */
    fun pickMake(): Make? {
        // Load and display makes list
        textIoService.displayChatbotMessage("I'm searching all the available make for you, this can take sometime ...")
        val makesResonse = vpicService.allMakes().blockingGet()
        if (makesResonse.count <= 0) {
            makesResonse.message?.let { textIoService.displayMessage(it) }
            textIoService.displayChatbotMessage("I failed to fetch the makes, refreshing")
            return pickMake()
        }

        // Pick multiple make
        val makes = pickMultipleFromList(makesResonse.results?:ArrayList())

        // Let the user between make if we have more than once
        return if (makes.size > 1) {
            pickFromList(makes)
        } else {
            makes.firstOrNull()
        }
    }

    /**
     * Pick a make from terminal with manufacturer
     */
    fun pickMake(manufacturer: Manufacturer): Make? {
        // Load and display makes list
        textIoService.displayChatbotMessage("I'm searching all the available make for you, this can take sometime ...")
        val makesResonse = vpicService.makeForManufacturer(manufacturer.commonName
                ?: manufacturer.name?: "").blockingGet()
        if (makesResonse.count <= 0) {
            makesResonse.message?.let { textIoService.displayMessage(it) }
            textIoService.displayChatbotMessage("I failed to fetch the makes, refreshing")
            return pickMake(manufacturer)
        }

        // Pick multiple make
        val makes = pickMultipleFromList(makesResonse.results?:ArrayList())

        // Let the user between make if we have more than once
        return if (makes.size > 1) {
            pickFromList(makes)
        } else {
            makes.firstOrNull()
        }
    }

    /**
     * Function used to pick a make from a list
     */
    private fun pickMultipleFromList(makesInput: List<Make>) : List<Make> {
        val makes = ArrayList<Make>()
        while (makes.isEmpty()) {
            textIoService.displayMessage("Make, column : (ID : Name)")
            makesInput?.forEach {
                textIoService.displayMessage("${it.id} : ${it.name}")
            }

            val makeAnswer = textIoService.askUserQuestion("Please enter the desired make name or id, enter exit to abort")
            if (makeAnswer == "exit") return makes

            // Find if the id or name entered is known
            makesInput.forEach { makeTmp ->
                if ((makeAnswer.length > 3 && containsIgnoreCase(makeAnswer.trim(), makeTmp.name))
                        || makeTmp.id == makeAnswer.toLongOrNull()) {
                    makes.add(makeTmp)
                }
            }
        }
        return makes
    }

    /**
     * Function used to pick a single make from a list
     */
    private fun pickFromList(makesInput: List<Make>) : Make? {
        var make: Make? = null
        while (make == null) {

            textIoService.displayChatbotMessage("Multiple makes correspond to your answer, please refine your answer")
            makesInput.forEach { makeTmp ->
                textIoService.displayMessage("name : ${makeTmp.name}, id : ${makeTmp.id}")
            }

            val makeAnswer = textIoService.askUserQuestion("Please enter the desired make id, enter exit to abort", TextIoService.PATTERN_NUMBER_OR_EXIT)
            if (makeAnswer == "exit") return null

            // Find if the id or name entered is known
            make = makesInput.firstOrNull { makeTmp ->
                makeTmp.id == makeAnswer.toLongOrNull()
            }
        }
        return make
    }

    /**
     * Check if a string is contained in another string ignore the case
     */
    fun containsIgnoreCase(str: String?, searchStr: String?): Boolean {
        if (str == null || searchStr == null) return false

        val length = searchStr.length
        if (length == 0)
            return true

        for (i in str.length - length downTo 0) {
            if (str.regionMatches(i, searchStr, 0, length, ignoreCase = true))
                return true
        }
        return false
    }
}
