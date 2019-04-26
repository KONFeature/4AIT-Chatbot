package com.supinfo.chatbot.services.vehicle

import com.supinfo.chatbot.Utils.TerminalAction
import com.supinfo.chatbot.data.api.VpicService
import com.supinfo.chatbot.data.api.dto.Manufacturer
import com.supinfo.chatbot.services.TextIoService
import org.beryx.textio.TextIO
import org.springframework.stereotype.Service

/**
 * Service handeling all the vehicle manufacturer logic
 */
@Service
class ManufacturerService(private val textIoService: TextIoService,
                          private val vpicService: VpicService) {

    /**
     * Function used to pick a manufacturer from terminal
     */
    fun pickManufacturer(): Manufacturer? {
        var manufacturer: Manufacturer? = null
        var currentPage: Long = 1
        while (manufacturer == null) {
            textIoService.displayChatbotMessage(("Constructeur de la page $currentPage"))

            // Load and display manufacturers list
            textIoService.displayChatbotMessage("Chargement des constructeurs disponnible, ca peut prendre un peut de temp...")
            val manufacturersResonse = vpicService.allManufacturersPerPage(currentPage).blockingGet()
            if (manufacturersResonse.count <= 0) {
                manufacturersResonse.message?.let { textIoService.displayChatbotMessage(it) }
                textIoService.displayChatbotMessage("Essayez une autre page")
            }

            // Ask the user a question and check the result
            val response = textIoService.selectItemFromList(manufacturersResonse.results?:ArrayList(), currentPage > 1, true)
            when(response.action) {
                TerminalAction.EXIT -> return null
                TerminalAction.PREV -> currentPage--
                TerminalAction.NEXT -> currentPage++
                else -> response.result?.let { it ->
                    if(it is Manufacturer) {
                        manufacturer = it
                    }
                }
            }
        }
        return manufacturer
    }

}
