package com.supinfo.chatbot.services

import com.supinfo.chatbot.api.VpicService
import io.reactivex.schedulers.Schedulers
import org.beryx.textio.TextIO
import org.beryx.textio.TextIoFactory
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

/**
 * Service helping us with the Learner AI
 */
@Service
class LearnerAiService(private val vpicService: VpicService) {

    companion object {
        val log = LoggerFactory.getLogger(LearnerAiService::class.java)
    }

    /**
     * TODO : Use Database to store and find keyword in the sentence, if we don't find any in the question we ask the user which one he want and them store that in the database
     */

    // Variable representing ur terminal
    private var textIo : TextIO? = null

    // Variable that tell us if the learner chatbot is currently running
    private var running: Boolean = false

    /**
     * Base method used to launch ther leaner ai service
     */
    fun launch() {
        textIo = TextIoFactory.getTextIO()
        running = true

        textIo?.let {terminal ->
            do {
                val userInput = terminal.newStringInputReader()
                        .withDefaultValue("help")
                        .read("Ask ur question")

                when(userInput) {
                    "stop" -> running = false
                    "help" -> displayHelp(terminal)
                    else -> parseInput(userInput, terminal)
                }
            } while (running)
        }
    }

    /**
     * show the help page concerning the learning chatbot AI
     */
    fun displayHelp(textIO: TextIO) {
        textIO.textTerminal.println("You are in the learning AI, to exit type 'stop', to show help type 'help', or just type the question u have.")
    }

    /**
     * try to parse the user input, all the logic are in there
     */
    fun parseInput(input: String, textIO: TextIO) {
        if(input.contains("manufacturers", true)) {
            // If the question contains manufacturers, we display a list of all the manufacturers
            val manufacturers = vpicService.allManufacturers()
                    .subscribeOn(Schedulers.io())
                    .blockingGet()

            manufacturers.results?.forEach { manufacturer ->
                textIO.textTerminal.println(manufacturer.name?:manufacturer.commonName)
            }

        } else if(input.contains("makes", true)) {
            // If the qestion contains make, we ask the user the manufacturer (or let im select one) and the search the makes associated
            val manufacturerName = textIO.newStringInputReader()
                    .withDefaultValue("honda")
                    .read("Manufacturer name")

            val makes = vpicService.makeForManufacturer(manufacturerName)
                    .subscribeOn(Schedulers.io())
                    .blockingGet()

            makes.results?.forEach { make ->
                textIO.textTerminal.println("${make.name} from ${make.mfrName} with id ${make.id}")
            }
        }
    }

}
