package com.supinfo.chatbot.services.vehicle

import com.supinfo.chatbot.data.api.VpicService
import com.supinfo.chatbot.data.api.dto.Make
import com.supinfo.chatbot.data.api.dto.Manufacturer
import org.beryx.textio.TextIO
import org.springframework.stereotype.Service

/**
 * Service handeling all the vehicule make logic
 */
@Service
class MakeService(private val vpicService: VpicService) {

    /**
     * Pick a make from terminal with no information
     */
    fun pickMake(textIO: TextIO) : Make? {
        var make: Make? = null
        while (make == null) {
            // Load and display makes list
            textIO.textTerminal.println("Loading the available makes, this can take sometime ...")
            val makesResonse = vpicService.allMakes().blockingGet()
            if (makesResonse.count <= 0) {
                makesResonse.message?.let { textIO.textTerminal.println(it) }
                textIO.textTerminal.println("Error during the makes fetching, refreshing")
            } else {
                textIO.textTerminal.println("Make, column : (ID : Name)")
                makesResonse.results?.forEach {
                    textIO.textTerminal.println("${it.id} : ${it.name}")
                }
            }

            // Pick a make
            val makeId = textIO.newLongInputReader()
                    .withMinVal(-1)
                    .read("Please enter the desired make id, or -1 to abort")
            if(makeId == -1L) return null
            make = makesResonse.results?.firstOrNull { it.id == makeId }
            if(make == null) textIO.textTerminal.println("Null make selected, refreshing")
        }
        return make
    }

    /**
     * Pick a make from terminal with manufacturer
     */
    fun pickMake(textIO: TextIO, manufacturer: Manufacturer) : Make? {
        var make: Make? = null
        while (make == null) {
            // Load and display makes list
            textIO.textTerminal.println("Loading the available makes, this can take sometime ...")
            val makesResonse = vpicService.makeForManufacturer(manufacturer.commonName?:manufacturer.name!!).blockingGet()
            if (makesResonse.count <= 0) {
                makesResonse.message?.let { textIO.textTerminal.println(it) }
                textIO.textTerminal.println("Error during the makes fetching, refreshing")
            } else {
                textIO.textTerminal.println("Make, column : (ID : Name : Manufacturer name)")
                makesResonse.results?.forEach {
                    textIO.textTerminal.println("${it.id} : ${it.name} : ${it.mfrName}")
                }
            }

            // Pick a make
            val makeId = textIO.newLongInputReader()
                    .withMinVal(-1)
                    .read("Please enter the desired make id, or -1 to abort")
            if(makeId == -1L) return null
            make = makesResonse.results?.firstOrNull { it.id == makeId }
            if(make == null) textIO.textTerminal.println("Null make selected, refreshing")
        }
        return make
    }

}
