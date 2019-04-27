package com.supinfo.chatbot.commands

import com.supinfo.chatbot.services.LearnerAiService
import org.springframework.shell.standard.ShellComponent
import org.springframework.shell.standard.ShellMethod

@ShellComponent
class LearnerAiCommands(private val learnerAiService: LearnerAiService) {

    @ShellMethod("Lancement de la version apprenante de l'IA", key = ["learner-ai", "learner", "lstart", "llaunch"])
    fun start() {
        learnerAiService.launch()
    }

}
