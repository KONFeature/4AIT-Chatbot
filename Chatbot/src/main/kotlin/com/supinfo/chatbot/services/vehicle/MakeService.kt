package com.supinfo.chatbot.services.vehicle

import antlr.StringUtils
import com.supinfo.chatbot.Utils.TerminalAction
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
        textIoService.displayChatbotMessage("Je recherche les marques disponnible, ca peut prendre un peut de temp ...")
        val makesResonse = vpicService.allMakes().blockingGet()
        if (makesResonse.count <= 0) {
            makesResonse.message?.let { textIoService.displayMessage(it) }
            textIoService.displayChatbotMessage("Echec du chargement des marques, rafraichissement ...")
            return pickMake()
        }

        // Pick make
        val terminalResponse = textIoService.selectItemFromList(makesResonse.results?:ArrayList(), false, false)

        // Let the user between make if we have more than once
        return if(terminalResponse.action == TerminalAction.DONE) {
            terminalResponse.result as Make
        } else {
            return null
        }
    }

    /**
     * Pick a make from terminal with manufacturer
     */
    fun pickMake(manufacturer: Manufacturer): Make? {
        // Load and display makes list
        textIoService.displayChatbotMessage("Je recherche les marques disponnible, ca peut prendre un peut de temp ...")
        val makesResonse = vpicService
                .makeForManufacturer(manufacturer.commonName ?: manufacturer.name?: "")
                .blockingGet()
        if (makesResonse.count <= 0) {
            makesResonse.message?.let { textIoService.displayMessage(it) }
            textIoService.displayChatbotMessage("Impossible de trouver des marques pour le constructeur ${manufacturer.commonName?:manufacturer.name?:""}, abandon")
            return null
        }

        // Pick multiple make
        val terminalResponse = textIoService.selectItemFromList(makesResonse.results?:ArrayList(), false, false)

        // Let the user between make if we have more than once
        return if(terminalResponse.action == TerminalAction.DONE) {
            terminalResponse.result as Make
        } else {
            return null
        }
    }
}
