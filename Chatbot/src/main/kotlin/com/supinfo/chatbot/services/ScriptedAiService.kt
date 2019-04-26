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
                askUserBoolQuestion("Voulez vous selectionner une marque directement ? Sinon vous pourrez selectionner un constructeur, puis une marque associé", true)

        val make: Make = if (directSelectMake) {
            makeService.pickMake()
        } else {
            // Select a manufacturer
            manufacturerService.pickManufacturer()
                    ?.let { makeService.pickMake(it) }
                    ?: run {
                        textIoService.displayChatbotMessage("Aucun constructeur selectionnez, abandon")
                        textIoService.stop()
                        return
                    }
        } ?: run {
            textIoService.displayChatbotMessage("Aucune marque selectionnez, abandon")
            textIoService.stop()
            return
        }

        val directSelectModel = textIoService.askUserBoolQuestion("Voulez vous selectionner directement un model ? Sinn vous pourrez selectionner un type de vehicule puis un model associer.", true)
        val model: Model = if (directSelectModel) {
            modelService.pickModel(make)
        } else {
            // Select a type
            vehiculeTypeService.pickVehicleType(make)
                    ?.let { modelService.pickModel(make, it) }
                    ?: run {
                        textIoService.displayChatbotMessage("Vous n'avez selectionnez aucun type, abandon")
                        textIoService.stop()
                        return
                    }
        } ?: run {
            textIoService.displayChatbotMessage("Vous n'avez selectionnez aucun model, abandon")
            textIoService.stop()
            return
        }

        textIoService.displayChatbotMessage("Vous avez selectionnez le model ${model.getTerminalString()}")
        textIoService.stop()
    }
}
