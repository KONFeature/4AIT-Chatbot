package com.supinfo.chatbot.commands

import com.supinfo.chatbot.api.VpicResponse
import com.supinfo.chatbot.api.dto.Manufacturer
import com.supinfo.chatbot.services.LearnerAiService
import io.reactivex.schedulers.Schedulers
import org.beryx.textio.TextIoFactory
import org.jline.reader.LineReader
import org.springframework.shell.standard.ShellComponent
import org.springframework.shell.standard.ShellMethod

@ShellComponent
class LearnerAiCommands(private val learnerAiService: LearnerAiService) {

    @ShellMethod("Launch the leaner AI", key = ["learner-ai", "learner", "start", "launch"])
    fun start() {
        learnerAiService.launch()
    }

}
