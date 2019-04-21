package com.supinfo.chatbot.commands

import org.springframework.shell.standard.ShellComponent
import org.springframework.shell.standard.ShellMethod
import org.springframework.shell.standard.ShellOption

@ShellComponent
class TestCommands {

    @ShellMethod("Simple test command accepting one arg", key = ["test", "simple-arg-test"] )
    fun simpleArgTest(
            @ShellOption(defaultValue = "none",
                    help = "Simple argument with basic arity for testing purpose") args : String
    ): String {
        return "Args entered: $args"
    }
}
