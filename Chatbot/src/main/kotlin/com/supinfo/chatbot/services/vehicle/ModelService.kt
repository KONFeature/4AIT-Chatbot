package com.supinfo.chatbot.services.vehicle

import com.supinfo.chatbot.data.api.VpicService
import com.supinfo.chatbot.data.api.dto.Make
import com.supinfo.chatbot.data.api.dto.Model
import com.supinfo.chatbot.data.api.dto.VehicleType
import org.beryx.textio.TextIO
import org.springframework.stereotype.Service

/**
 * Service handeling all the vehicule model logic
 */
@Service
class ModelService(private val vpicService: VpicService) {

    /**
     * Pick a model from terminal from make
     */
    fun pickModel(textIO: TextIO, make: Make) : Model? {
        var model: Model? = null
        while (model == null) {
            // Load and display makes list
            textIO.textTerminal.println("Loading the available entities for the make ${make.name}, this can take sometime ...")
            val modelsResponse = vpicService.modelsForMake(make.id).blockingGet()
            if (modelsResponse.count <= 0) {
                modelsResponse.message?.let { textIO.textTerminal.println(it) }
                textIO.textTerminal.println("Error during the entities fetching, refreshing")
            } else {
                textIO.textTerminal.println("Model, column : (ID : Name)")
                modelsResponse.results?.forEach {
                    textIO.textTerminal.println("${it.id} : ${it.modelName}")
                }
            }

            // Pick a model
            val modelId = textIO.newLongInputReader()
                    .withMinVal(-1)
                    .read("Please enter the desired model id, or -1 to abort")
            if(modelId == -1L) return null
            model = modelsResponse.results?.firstOrNull { it.id == modelId }
            if(model == null) textIO.textTerminal.println("Null model selected, refreshing")
        }
        return model
    }

    /**
     * Pick a model from terminal from make and vehicule type
     */
    fun pickModel(textIO: TextIO, make: Make, vType: VehicleType) : Model? {
        var model: Model? = null
        while (model == null) {
            // Load and display makes list
            textIO.textTerminal.println("Loading the available entities for the make ${make.name} and type ${vType.name}, this can take sometime ...")
            val modelsResponse = vpicService.modelsForMakeAndType(make.id, vType.name?:run { "" }).blockingGet()
            if (modelsResponse.count <= 0) {
                modelsResponse.message?.let { textIO.textTerminal.println(it) }
                textIO.textTerminal.println("Error during the entities fetching, refreshing")
            } else {
                textIO.textTerminal.println("Model, column : (ID : Name)")
                modelsResponse.results?.forEach {
                    textIO.textTerminal.println("${it.id} : ${it.modelName}")
                }
            }

            // Pick a model
            val modelId = textIO.newLongInputReader()
                    .withMinVal(-1)
                    .read("Please enter the desired model id, or -1 to abort")
            if(modelId == -1L) return null
            model = modelsResponse.results?.firstOrNull { it.id == modelId }
            if(model == null) textIO.textTerminal.println("Null model selected, refreshing")
        }
        return model
    }
}
