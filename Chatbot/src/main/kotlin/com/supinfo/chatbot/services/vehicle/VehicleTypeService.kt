package com.supinfo.chatbot.services.vehicle

import com.supinfo.chatbot.api.VpicService
import com.supinfo.chatbot.api.dto.Make
import com.supinfo.chatbot.api.dto.VehicleType
import org.beryx.textio.TextIO
import org.springframework.stereotype.Service

/**
 * Service handeling all the vehicule type logic
 */
@Service
class VehicleTypeService(private val vpicService: VpicService) {

    /**
     * Pick a vehicle type from terminal with knwon make
     */
    fun pickVehicleType(textIO: TextIO, make: Make) : VehicleType? {
        var vType: VehicleType? = null
        while (vType == null) {
            // Load and display type list
            textIO.textTerminal.println("Loading the available types for the make ${make.name}, this can take sometime ...")
            val modelsResponse = vpicService.vehicleTypeForMake(make.id).blockingGet()
            if (modelsResponse.count <= 0) {
                modelsResponse.message?.let { textIO.textTerminal.println(it) }
                textIO.textTerminal.println("Error during the models fetching, refreshing")
            } else {
                textIO.textTerminal.println("Model, column : (ID : Name)")
                modelsResponse.results?.forEach {
                    textIO.textTerminal.println("${it.id} : ${it.name}")
                }
            }

            // Pick a type
            val vTypeId = textIO.newLongInputReader()
                    .withMinVal(-1)
                    .read("Please enter the desired type id, or -1 to abort")
            if(vTypeId == -1L) return null
            vType = modelsResponse.results?.firstOrNull { it.id == vTypeId }
            if(vType == null) textIO.textTerminal.println("Null type selected, refreshing")
        }
        return vType
    }

}
