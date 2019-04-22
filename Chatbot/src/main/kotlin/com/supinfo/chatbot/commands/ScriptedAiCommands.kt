package com.supinfo.chatbot.commands

import com.supinfo.chatbot.services.ScriptedAiService
import org.springframework.shell.standard.ShellComponent

@ShellComponent
class ScriptedAiCommands(private val scriptedAiService: ScriptedAiService) {
}
