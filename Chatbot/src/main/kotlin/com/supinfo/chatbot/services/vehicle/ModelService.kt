package com.supinfo.chatbot.services.vehicle

import com.supinfo.chatbot.Utils.TerminalAction
import com.supinfo.chatbot.data.api.VpicService
import com.supinfo.chatbot.data.api.dto.Make
import com.supinfo.chatbot.data.api.dto.Model
import com.supinfo.chatbot.data.api.dto.VehicleType
import com.supinfo.chatbot.services.TextIoService
import org.beryx.textio.TextIO
import org.springframework.stereotype.Service

/**
 * Service handeling all the vehicule model logic
 */
@Service
class ModelService(private val textIoService: TextIoService,
                   private val vpicService: VpicService) {

    /**
     * Pick a model from terminal from make
     */
    fun pickModel(make: Make): Model? {
        // Load and display makes list
        textIoService.displayChatbotMessage("Chargement des model disponnible pour la marque ${make.name}, ca peut prendre un peut de temp...")
        val modelsResponse = vpicService.modelsForMake(make.id).blockingGet()
        if (modelsResponse.count <= 0) {
            modelsResponse.message?.let { textIoService.displayChatbotMessage(it) }
            textIoService.displayChatbotMessage("Impossible de trouver des models pour la marque ${make.name}, abandon")
            return null
        }

        // Pick a model
        val response = textIoService.selectItemFromList(modelsResponse.results ?: ArrayList(), false, false)
        return if (response.action == TerminalAction.DONE) {
            response.result as Model
        } else {
            return null
        }
    }

    /**
     * Pick a model from terminal from make and vehicule type
     */
    fun pickModel(make: Make, vType: VehicleType): Model? {
        // Load and display makes list
        textIoService.displayChatbotMessage("Chargement des model disponnible pour la marque ${make.name} du type ${vType.name}, ca peut prendre un peut de temp...")
        val modelsResponse = vpicService.modelsForMakeAndType(make.id, vType.name ?: run { "" }).blockingGet()
        if (modelsResponse.count <= 0) {
            modelsResponse.message?.let { textIoService.displayChatbotMessage(it) }
            textIoService.displayChatbotMessage("Impossible de trouver des models pour la marque ${make.name} de type ${vType.name}, abandon")
            return null
        }

        // Pick a model
        val response = textIoService.selectItemFromList(modelsResponse.results ?: ArrayList(), false, false)
        return if (response.action == TerminalAction.DONE) {
            response.result as Model
        } else {
            return null
        }
    }
}
