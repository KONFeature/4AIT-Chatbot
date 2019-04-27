package com.supinfo.chatbot.services

import com.supinfo.chatbot.Utils.TerminalAction
import com.supinfo.chatbot.data.api.VpicService
import com.supinfo.chatbot.data.db.dao.ExtractorDao
import com.supinfo.chatbot.data.db.dao.KeywordDao
import com.supinfo.chatbot.data.db.dao.SelectorDao
import com.supinfo.chatbot.data.db.entities.Extractor
import com.supinfo.chatbot.data.db.entities.Keyword
import com.supinfo.chatbot.data.db.entities.Selector
import org.springframework.stereotype.Service

/**
 * Service helping us with the Learner AI
 */
@Service
class LearnerAiService(private val textIoService: TextIoService,
                       private val extractorDao: ExtractorDao,
                       private val keywordDao: KeywordDao,
                       private val selectorDao: SelectorDao,
                       private val vpicService: VpicService) {

    /**
     * TODO : Use Database to store and find keyword in the sentence, if we don't find any in the question we ask the user which one he want and them store that in the database
     */

    // Variable that tell us if the learner chatbot is currently running
    private var running: Boolean = false

    // Variable that tell us if the learner AI is currently learning
    private var learning: Boolean = false

    /**
     * Base method used to launch ther leaner ai service
     */
    fun launch() {
        textIoService.launch()
        textIoService.displayChatbotMessage("Bienvenue sur la version apprenante de l'IA, je connais ${extractorDao.count()} formules, pour le moment (si vous voulez en ajouter passer en version apprenante)")

        running = true
        learning = textIoService.askUserBoolQuestion("Voulez vous passer en mode apprentissage ?", false)
        textIoService.displayChatbotMessage("Vous pouvez a tout moment quitter en entrant 'exit', et passer / quitter le mode apprentissage en tappant 'learn' ")
        while (running) {
            run()
        }

        textIoService.stop()
    }

    /**
     * The run method (executed while running = true
     */
    fun run() {
        val question = textIoService.askUserQuestion("En quoi puis-je vous aider ?")

        // Check if it's exit or learn command
        if (question.trim().equals("exit", true)) {
            running = false
            return
        } else if (question.trim().equals("learn", true)) {
            learning = !learning
            if (learning)
                textIoService.displayChatbotMessage("Vous ete passer en mode apprentissage")
            else
                textIoService.displayChatbotMessage("Vous avez quitter le mode apprentissage")
            return
        }

        // Try to find keyword in the question
        var keywords = extractKeywordFromQuetion(question)
        if (keywords.isEmpty() && !learning) {
            textIoService.displayChatbotMessage("Impossible de trouver un mot clé dans votre phrase, et vous n'ete pas en mode apprentissage, merci de réessayer.")
        } else if (keywords.isEmpty()) {
            // Try to learn new keyword and then extract them again
            learnKeyword(question)
            keywords = extractKeywordFromQuetion(question)
        }

        // Find selector, if we havn't then select all for target of keyword
        keywords.forEach {keyword ->
            var extractors = extractorDao.findAllByKeywordEquals(keyword)
            if(extractors.isEmpty() && !learning) {
                textIoService.displayChatbotMessage("Impossible de trouver un model de selection pour le mot clé ${keyword.word}, et vous n'ete pas en mode apprentissage, merci de réessayer.")
                return
            } else if(extractors.isEmpty()) {
                // try to learn new selector
                val selector = learnSelectorForKeyword(keyword)
                selector?.let { selectorDao.save(it) }
                extractorDao.save(Extractor(keyword = keyword, selector = selector, score = 1))
                extractors = extractorDao.findAllByKeywordEquals(keyword)
            }

            // Learning the word extraction
            if(learning) {
                textIoService.displayChatbotMessage("Je vais vous presenter tout les models de selections disponnible pour ce mot clés")
                extractors.forEach { extractor -> textIoService.displayMessage(extractor.getTerminalString()) }

                // Adding a new extractor model
                while(textIoService.askUserBoolQuestion("Voulez vous ajouter un nouveau model de selection ?", false)) {
                    // learn new selector
                    val selector = learnSelectorForKeyword(keyword)
                    selector?.let { selectorDao.save(it) }
                    extractorDao.save(Extractor(keyword = keyword, selector = selector, score = 1))
                    extractors = extractorDao.findAllByKeywordEquals(keyword)
                }

                // Ask if he want to prioritize a model of another
                while(textIoService.askUserBoolQuestion("Voulez vous priorisez un model de selection ?", false)) {
                    val extractorResponse = textIoService.selectItemFromList(extractors, false, false)
                    if(extractorResponse.action == TerminalAction.EXIT)
                        return

                    extractorResponse.result?.let { extractor ->
                        if(extractor is Extractor) {
                            extractor.score++
                            extractorDao.save(extractor)
                        }
                    }
                }
            }

            // TODO : Extract data or select all
        }
    }

    /**
     * Function used to extract keyword from a sentence
     */
    private fun extractKeywordFromQuetion(question: String): List<Keyword> {
        val res = ArrayList<Keyword>()
        question.split(' ').forEach { questionWord ->
            res.addAll(keywordDao.findAllByWordIsLike(questionWord))
        }
        return res
    }

    /**
     * try to learn some keyword
     */
    private fun learnKeyword(question: String) {
        val words = question.split(' ')
        // Show the user the available keywords
        textIoService.displayChatbotMessage("Je vais vous montrez les mots clés de votre question que j'ai trouver")
        words.withIndex().forEach { wordIndex ->
            textIoService.displayMessage("id: ${wordIndex.index}, mot-clé : ${wordIndex.value}")
        }

        // ask him to select one, or multiple
        val keywords = ArrayList<String>()
        while (keywords.isEmpty()) {
            val selectedId = textIoService.askUserQuestion("Qu'elles sont les mots clés de votre questions ? (vous pouvez en entrez plusieur en les separant par ';'", TextIoService.PATTERN_NUMBER_SEPERATED)
            selectedId.split(';').forEach { keywordId ->
                words.withIndex().forEach { wordIndex ->
                    if (wordIndex.index == keywordId.toIntOrNull()) keywords.add(wordIndex.value)
                }
            }
            if (keywords.isEmpty()) textIoService.displayChatbotMessage("Vous n'avez selectionner aucun mot clé valide, merci de réessayer")
        }

        // Ask him to associate the keyword with a target
        keywords.forEach { keyword ->
            textIoService.displayChatbotMessage("Vous allez maintenant devoir choisir la cible du mot clé : $keyword")
            textIoService.askUserTarget()?.let {target ->
                keywordDao.save(Keyword(word = keyword, target = target))
                textIoService.displayChatbotMessage("J'ai bien sauvegarder le mot clé $keyword ayant pour cible $target")
            }?:run {
                textIoService.displayChatbotMessage("Vous n'avez pas selectionner de cible valide pour ce mot clé, nous l'oublions donc")
            }
        }
    }

    /**
     * Try to learn some selector
     */
    private fun learnSelectorForKeyword(keyword: Keyword)  : Selector? {
        textIoService.displayChatbotMessage("Vous allez maintenant devoir me guider pour comprendre votre mot clé : ${keyword.word}")
        val all = textIoService.askUserBoolQuestion("Quand vous tapper ce mot clé, voulez vous selectionner tout les ${keyword.target} ?", false)
        if(all) return null

        val nbrWordRaw = textIoService.askUserQuestion("Ok, alors combien de mot sont ils a prendre en compte apres ou avant votre mot clé ?", TextIoService.PATTERN_NUMBER)
        val after = textIoService.askUserBoolQuestion("Et c'est mots sont ils apres votre mot clés ? (Si vous repondez non, nous irons chercher ce nombre de mot avant votre mot clé)", true)
        return Selector(nbrWord = nbrWordRaw.toInt(), after = after)
    }
}
