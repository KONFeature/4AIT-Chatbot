package com.supinfo.chatbot.commands

import com.supinfo.chatbot.services.ScriptedAiService
import org.beryx.textio.TextIO
import org.beryx.textio.TextIoFactory
import org.springframework.shell.standard.ShellComponent
import org.springframework.shell.standard.ShellMethod

@ShellComponent
class ScriptedAiCommands(private val scriptedAiService: ScriptedAiService) {


    @ShellMethod("Launch the scripted AI", key = ["scripted-ai", "scripted", "start-scripted", "launch-scripted", "slaunch"])
    fun start() {
        scriptedAiService.launch()
    }
}
