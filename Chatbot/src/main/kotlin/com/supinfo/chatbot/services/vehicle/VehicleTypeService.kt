package com.supinfo.chatbot.services.vehicle

import com.supinfo.chatbot.Utils.TerminalAction
import com.supinfo.chatbot.data.api.VpicService
import com.supinfo.chatbot.data.api.dto.Make
import com.supinfo.chatbot.data.api.dto.Model
import com.supinfo.chatbot.data.api.dto.VehicleType
import com.supinfo.chatbot.services.TextIoService
import org.springframework.stereotype.Service

/**
 * Service handeling all the vehicule type logic
 */
@Service
class VehicleTypeService(private val textIoService: TextIoService,
                         private val vpicService: VpicService) {

    /**
     * Pick a vehicle type from terminal with knwon make
     */
    fun pickVehicleType(make: Make): VehicleType? {
        // Load and display type list
        textIoService.displayChatbotMessage("Chargement des types disponnible pour la marque ${make.name}, ca peut prendre un peut de temp...")
        val typesResponse = vpicService.vehicleTypeForMake(make.id).blockingGet()
        if (typesResponse.count <= 0) {
            typesResponse.message?.let { textIoService.displayChatbotMessage(it) }
            textIoService.displayChatbotMessage("Impossible de trouver des type pour la marque ${make.name}, abandon")
            return null
        }

        // Pick a type
        val response = textIoService.selectItemFromList(typesResponse.results ?: ArrayList(), false, false)
        return if (response.action == TerminalAction.DONE) {
            response.result as VehicleType
        } else {
            return null
        }
    }

}
