package com.supinfo.chatbot.config

import org.jline.utils.AttributedString
import org.jline.utils.AttributedStyle
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.shell.jline.PromptProvider
import org.springframework.stereotype.Component


/**
 * Class to set the CLI Prompt.
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
class SpringShellCustomPrompt : PromptProvider {

    /**
     * Custom prompt
     */
    override fun getPrompt(): AttributedString
    = AttributedString("\$chatbot>",
            AttributedStyle.DEFAULT.foreground(AttributedStyle.GREEN))

}
