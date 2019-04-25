package com.supinfo.chatbot.services

import com.supinfo.chatbot.data.api.dto.Make
import com.supinfo.chatbot.data.api.dto.Model
import com.supinfo.chatbot.services.vehicle.MakeService
import com.supinfo.chatbot.services.vehicle.ManufacturerService
import com.supinfo.chatbot.services.vehicle.ModelService
import com.supinfo.chatbot.services.vehicle.VehicleTypeService
import org.beryx.textio.TextIO
import org.beryx.textio.TextIoFactory
import org.springframework.stereotype.Service

/**
 * Service helping us with the Scripted AI
 */
@Service
class ScriptedAiService(private val textIoService: TextIoService,
                        private val manufacturerService: ManufacturerService,
                        private val makeService: MakeService,
                        private val modelService: ModelService,
                        private val vehiculeTypeService: VehicleTypeService) {

    fun launch() {
        textIoService.launch()

        // Launch the script to get a specific vehicle model from scratch
        val directSelectMake = textIoService.
                askUserBoolQuestion("Do you want to directly select a make ? Else you will be able to select a manufacturer and then a make associated to this manufacturer", true)

        val make: Make = if (directSelectMake) {
            makeService.pickMake()
        } else {
            // Select a manufacturer
            manufacturerService.pickManufacturer()
                    ?.let { makeService.pickMake(it) }
                    ?: run {
                        textIoService.displayChatbotMessage("No manufacturer selected, aborting")
                        textIoService.stop()
                        return
                    }
        } ?: run {
            textIoService.displayChatbotMessage("No make selected aborting")
            textIoService.stop()
            return
        }
//
//        val directSelectModel = terminal.newBooleanInputReader()
//                .withDefaultValue(true)
//                .read("Do you Want to directly select a model ? Else you will be able to select a vehicle type and then a model associated to this type and make")
//        val model: Model = if (directSelectModel) {
//            modelService.pickModel(terminal, make)
//        } else {
//            // Select a type
//            vehiculeTypeService.pickVehicleType(terminal, make)
//                    ?.let { modelService.pickModel(terminal, make, it) }
//                    ?: run {
//                        terminal.textTerminal.println("No vehicule type selected, aborting")
//                        return
//                    }
//        } ?: run {
//            terminal.textTerminal.println("No model selected aborting")
//            return
//        }
//
//        terminal.textTerminal.println("Model selected : ${model.modelName} from ${model.makeName}")
        textIoService.stop()
    }
}
