package com.supinfo.chatbot.services

import com.supinfo.chatbot.api.VpicService
import com.supinfo.chatbot.api.dto.Make
import com.supinfo.chatbot.api.dto.Manufacturer
import com.supinfo.chatbot.api.dto.Model
import org.beryx.textio.TextIO
import org.beryx.textio.TextIoFactory
import org.springframework.stereotype.Service

/**
 * Service helping us with the Scripted AI
 */
@Service
class ScriptedAiService(private val vpicService: VpicService) {

    // Variable representing ur terminal
    private var textIo: TextIO? = null

    fun launch() {
        textIo = TextIoFactory.getTextIO()

        textIo?.let { terminal ->
            // Launch the script to get a specific vehicle model from scratch

            val directSelectMake = terminal.newBooleanInputReader()
                    .withDefaultValue(true)
                    .read("Do you want to directly select a make ? Else you will be able to select a manufacturer and then a make associated to this manufacturer")

            val make = if(directSelectMake) {
                 directSelectMake(terminal)
            } else {
                // Select a manufacturer
                val manufacturer: Manufacturer = selectManufacturer(terminal)
                selectMakeFromManufacturer(terminal, manufacturer)
            }

            val directSelectModel = terminal.newBooleanInputReader()
                    .withDefaultValue(true)
                    .read("Do you Want to directly select a model ? Else you will be able to select a vehicle type and then a model associated to this type and make")
            if(directSelectModel) {
                directSelectModel(terminal, make)
            }

        }
    }

    /**
     * Function used to directly select a make
     */
    private fun directSelectMake(textIO: TextIO): Make {
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
                    .withMinVal(0)
                    .read("Please enter the desired make id")
            make = makesResonse.results?.firstOrNull { it.id == makeId }
            if(make == null) textIO.textTerminal.println("Null make selected, refreshing")
        }
        return make
    }

    /**
     * Function used to select a manufacturer
     */
    private fun selectManufacturer(textIO: TextIO): Manufacturer {
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
                    .read("Please enter the desired manufacturer id or -2 for the previous page and -1 for the next page")

            if(action == -2L && currentPage <= 1) {
                textIO.textTerminal.println("You are on first page, can't go behind, refreshing")
            } else if (action == -1L && manufacturersResonse.count <= 0) {
                textIO.textTerminal.println("You are on last page, can't go further, refreshing")
            } else {
                when(action) {
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

    /**
     * Function used to directly select a make
     */
    private fun selectMakeFromManufacturer(textIO: TextIO, manufacturer: Manufacturer): Make {
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
                    .withMinVal(0)
                    .read("Please enter the desired make id")
            make = makesResonse.results?.firstOrNull { it.id == makeId }
            if(make == null) textIO.textTerminal.println("Null make selected, refreshing")
        }
        return make
    }

    /**
     * Function used to dirctly select a mode from a make
     */
    private fun directSelectModel(textIO: TextIO, make: Make) : Model {
        var model: Model? = null
        while (model == null) {
            // Load and display makes list
            textIO.textTerminal.println("Loading the available models for the make ${make.name}, this can take sometime ...")
            val modelsResponse = vpicService.modelsForMake(make.id).blockingGet()
            if (modelsResponse.count <= 0) {
                modelsResponse.message?.let { textIO.textTerminal.println(it) }
                textIO.textTerminal.println("Error during the models fetching, refreshing")
            } else {
                textIO.textTerminal.println("Model, column : (ID : Name)")
                modelsResponse.results?.forEach {
                    textIO.textTerminal.println("${it.id} : ${it.modelName}")
                }
            }

            // Pick a model
            val modelId = textIO.newLongInputReader()
                    .withMinVal(0)
                    .read("Please enter the desired model id")
            model = modelsResponse.results?.firstOrNull { it.id == modelId }
            if(model == null) textIO.textTerminal.println("Null model selected, refreshing")
        }
        return model

    }
}
