package com.supinfo.chatbot.commands

import com.supinfo.chatbot.api.VpicResponse
import com.supinfo.chatbot.api.dto.Manufacturer
import com.supinfo.chatbot.services.LearnerAiService
import io.reactivex.schedulers.Schedulers
import org.springframework.shell.standard.ShellComponent
import org.springframework.shell.standard.ShellMethod

@ShellComponent
class LearnerAiCommands(private val learnerAiService: LearnerAiService) {

    @ShellMethod("List all the manufacturers known", key = ["manufacturers", "mfrs"] )
    fun allManufacturers() : VpicResponse<List<Manufacturer>> {
        return learnerAiService.allManufacturers()
                .subscribeOn(Schedulers.io())
                .blockingGet()
    }

}
