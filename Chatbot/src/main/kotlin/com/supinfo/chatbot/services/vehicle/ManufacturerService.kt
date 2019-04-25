package com.supinfo.chatbot.services.vehicle

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
            textIoService.displayChatbotMessage(("Manufacturer of page $currentPage"))

            // Load and display manufacturers list
            textIoService.displayChatbotMessage("Loading the available manufacturers, this can take sometime ...")
            val manufacturersResonse = vpicService.allManufacturersPerPage(currentPage).blockingGet()
            if (manufacturersResonse.count <= 0) {
                manufacturersResonse.message?.let { textIoService.displayChatbotMessage(it) }
                textIoService.displayChatbotMessage("Try on another page")
            } else {
                textIoService.displayMessage("Manufacturer, column : (ID : Name : Common Name)")
                manufacturersResonse.results?.forEach {
                    textIoService.displayMessage("${it.id} : ${it.name} ! ${it.commonName}")
                }
            }

            // Pick a manufacturer or another page
            val answer = textIoService.askUserQuestion("Please enter the desired manufacturer id, prev for previous page or next for next page, exit to abort, ", TextIoService.PATTERN_NUMBER_NAV_OR_EXIT)

            if(answer.trim() == "prev" && currentPage <= 1) {
                textIoService.displayChatbotMessage("You are on first page, can't go behind, refreshing")
            } else {
                when(answer.trim()) {
                    "exit" -> return null
                    "next" -> currentPage++
                    "prev" -> currentPage--
                    else -> {
                        manufacturer = manufacturersResonse.results?.firstOrNull { it.id == answer.toLongOrNull() }
                        if(manufacturer == null) textIoService.displayChatbotMessage("Null manufacturer selected, refreshing")
                    }
                }
            }
        }
        return manufacturer
    }

}
