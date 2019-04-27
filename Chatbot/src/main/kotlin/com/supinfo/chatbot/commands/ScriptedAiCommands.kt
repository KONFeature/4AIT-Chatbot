package com.supinfo.chatbot.commands

import com.supinfo.chatbot.services.ScriptedAiService
import org.beryx.textio.TextIO
import org.beryx.textio.TextIoFactory
import org.springframework.shell.standard.ShellComponent
import org.springframework.shell.standard.ShellMethod

@ShellComponent
class ScriptedAiCommands(private val scriptedAiService: ScriptedAiService) {


    @ShellMethod("Lancement de la version scripter de l'IA", key = ["scripted-ai", "scripted", "slaunch", "sstart"])
    fun start() {
        scriptedAiService.launch()
    }
}
