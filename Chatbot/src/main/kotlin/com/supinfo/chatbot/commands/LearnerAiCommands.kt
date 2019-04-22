package com.supinfo.chatbot.commands

import com.supinfo.chatbot.services.LearnerAiService
import org.springframework.shell.standard.ShellComponent
import org.springframework.shell.standard.ShellMethod

@ShellComponent
class LearnerAiCommands(private val learnerAiService: LearnerAiService) {

    @ShellMethod("Launch the leaner AI", key = ["learner-ai", "learner", "start", "launch"])
    fun start() {
        learnerAiService.launch()
    }

}
