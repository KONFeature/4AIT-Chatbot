package com.supinfo.chatbot.services.vehicle

import com.supinfo.chatbot.api.VpicService
import com.supinfo.chatbot.api.dto.Manufacturer
import org.beryx.textio.TextIO
import org.springframework.stereotype.Service

/**
 * Service handeling all the vehicle manufacturer logic
 */
@Service
class ManufacturerService(private val vpicService: VpicService) {

    /**
     * Function used to pick a manufacturer from terminal
     */
    fun pickManufacturer(textIO: TextIO): Manufacturer? {
        var manufacturer: Manufacturer? = null
        var currentPage: Long = 1
        while (manufacturer == null) {
            textIO.textTerminal.println("Manufacturer of page $currentPage")

            // Load and display manufacturers list
            textIO.textTerminal.println("Loading the available manufacturers, this can take sometime ...")
            val manufacturersResonse = vpicService.allManufacturersPerPage(currentPage).blockingGet()
            if (manufacturersResonse.count <= 0) {
                manufacturersResonse.message?.let { textIO.textTerminal.println(it) }
                textIO.textTerminal.println("Try on another page before")
            } else {
                textIO.textTerminal.println("Manufacturer, column : (ID : Name : Common Name)")
                manufacturersResonse.results?.forEach {
                    textIO.textTerminal.println("${it.id} : ${it.name} ! ${it.commonName}")
                }
            }

            // Pick a manufacturer or another page
            val action = textIO.newLongInputReader()
                    .withMinVal(-2)
                    .read("Please enter the desired manufacturer id or -2 for the previous page and -1 for the next page, or -3 to abort")

            if(action == -2L && currentPage <= 1) {
                textIO.textTerminal.println("You are on first page, can't go behind, refreshing")
            } else if (action == -1L && manufacturersResonse.count <= 0) {
                textIO.textTerminal.println("You are on last page, can't go further, refreshing")
            } else {
                when(action) {
                    -3L -> return null
                    -2L -> currentPage--
                    -1L -> currentPage++
                    else -> {
                        manufacturer = manufacturersResonse.results?.firstOrNull { it.id == action }
                        if(manufacturer == null) textIO.textTerminal.println("Null manufacturer selected, refreshing")
                    }
                }
            }
        }
        return manufacturer
    }

}
