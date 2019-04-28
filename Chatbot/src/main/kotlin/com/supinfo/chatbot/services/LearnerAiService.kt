package com.supinfo.chatbot.services

import com.supinfo.chatbot.Utils.TerminalAction
import com.supinfo.chatbot.containIgnoreCase
import com.supinfo.chatbot.data.api.VpicService
import com.supinfo.chatbot.data.api.dto.Make
import com.supinfo.chatbot.data.api.dto.Manufacturer
import com.supinfo.chatbot.data.api.dto.Model
import com.supinfo.chatbot.data.db.dao.ExtractorDao
import com.supinfo.chatbot.data.db.dao.KeywordDao
import com.supinfo.chatbot.data.db.dao.SelectorDao
import com.supinfo.chatbot.data.db.entities.Extractor
import com.supinfo.chatbot.data.db.entities.Keyword
import com.supinfo.chatbot.data.db.entities.KeywordTarget
import com.supinfo.chatbot.data.db.entities.Selector
import com.supinfo.chatbot.services.vehicle.MakeService
import com.supinfo.chatbot.services.vehicle.ManufacturerService
import com.supinfo.chatbot.services.vehicle.ModelService
import com.supinfo.chatbot.services.vehicle.VehicleTypeService
import org.springframework.stereotype.Service

/**
 * Service helping us with the Learner AI
 */
@Service
class LearnerAiService(private val textIoService: TextIoService,
                       private val extractorDao: ExtractorDao,
                       private val keywordDao: KeywordDao,
                       private val selectorDao: SelectorDao,
                       private val vpicService: VpicService,
                       private val manufacturerService: ManufacturerService,
                       private val makeService: MakeService,
                       private val modelService: ModelService,
                       private val vehiculeTypeService: VehicleTypeService) {

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
        val extractors = ArrayList<Extractor>()
        keywords.forEach {keyword ->
            var keywordExtractor = extractorDao.findAllByKeywordEquals(keyword)
            if(keywordExtractor.isEmpty() && !learning) {
                textIoService.displayChatbotMessage("Impossible de trouver un model de selection pour le mot clé ${keyword.word}, et vous n'ete pas en mode apprentissage, merci de réessayer.")
                return
            } else if(keywordExtractor.isEmpty()) {
                // try to learn new selector
                val selector = learnSelectorForKeyword(keyword)
                selector?.let { selectorDao.save(it) }
                extractorDao.save(Extractor(keyword = keyword, selector = selector, score = 1))
                keywordExtractor = extractorDao.findAllByKeywordEquals(keyword)
            }

            // Learning the word extraction
            if(learning) {
                textIoService.displayChatbotMessage("Je vais vous presenter tout les models de selections disponnible pour ce mot clés")
                keywordExtractor.forEach { extractor -> textIoService.displayMessage(extractor.getTerminalString()) }

                // Adding a new extractor model
                while(textIoService.askUserBoolQuestion("Voulez vous ajouter un nouveau model de selection ?", false)) {
                    // learn new selector
                    val selector = learnSelectorForKeyword(keyword)
                    selector?.let { selectorDao.save(it) }
                    extractorDao.save(Extractor(keyword = keyword, selector = selector, score = 1))
                    keywordExtractor = extractorDao.findAllByKeywordEquals(keyword)
                }

                // Ask if he want to prioritize a model of another
                while(textIoService.askUserBoolQuestion("Voulez vous priorisez un model de selection ?", false)) {
                    val extractorResponse = textIoService.selectItemFromList(keywordExtractor, false, false)
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

            // Add the finded keyword extractors to the list of extractor
            extractors.addAll(keywordExtractor)
        }

        // Find the desired data target from extractor
        val targets = ArrayList<KeywordTarget>()
        extractors.forEach { extractor ->
            if(!targets.contains(extractor.keyword.target)) {
                targets.add(extractor.keyword.target)
            }
        }

        // Exception (if we choose a model but havn't specified a make for exemple
        if(targets.contains(KeywordTarget.MODEL) && !targets.contains(KeywordTarget.MAKE)) {
            textIoService.displayChatbotMessage("Vous ne pouvez pas cherchez de model si vous ne precisez pas de marque, merci de réessayer")
        } else if(targets.contains(KeywordTarget.MODEL)) {
            // Recherche d'un model, recuperation des marques en premier
            var makesInfo: String = ""
            if(targets.contains(KeywordTarget.MAKE)) {
                // Find the data for the make
                val extractor = getMostRelevantExtractor(extractors, KeywordTarget.MAKE)
                makesInfo = extractorWordWithSelector(extractor, question).joinToString()
            }

            // Find the models and sort them with the selector
            vpicService.modelsForMake(makesInfo)
                    .doOnError{ error -> textIoService.displayChatbotMessage("Erreur lors de la recuperation des model ${error.message}")}
                    .doOnSuccess { response ->
                        response.results?.let {models ->
                            if(models.isEmpty()) {
                                textIoService.displayChatbotMessage("Aucun model trouver pour la marque $makesInfo")
                            } else {
                                val extractor = getMostRelevantExtractor(extractors, KeywordTarget.MODEL)
                                val filter = extractorWordWithSelector(extractor, question)
                                val refinedModelList = ArrayList<Model>()
                                filter.forEach { filterWord ->
                                    refinedModelList.addAll(models.filter { model -> model.modelName?.containIgnoreCase(filterWord)?:false })
                                }
                                if(filter.isEmpty()) refinedModelList.addAll(models)
                                textIoService.displayChatbotMessage("J'ai trouvez ${refinedModelList.size} model correspondant a votre recherche.")
                                val item = textIoService.selectItemFromList(refinedModelList, false, false)
                                textIoService.displayChatbotMessage("Vous avez selectionnez le model ${item.result?.getTerminalString()}")
                            }
                        }
                    }.subscribe()

        } else if(targets.contains(KeywordTarget.MANUFACTURER)) {
            // Selection d'un constructeur
            val extractor = getMostRelevantExtractor(extractors, KeywordTarget.MANUFACTURER)
            vpicService.allManufacturers()
                    .doOnError {error -> textIoService.displayChatbotMessage("Erreur lors de la recuperation des constrcuteur ${error.message}") }
                    .doOnSuccess { response ->
                        response.results?.let { manufacturers ->
                            val filter = extractorWordWithSelector(extractor, question)
                            val refinedManufacturerList = ArrayList<Manufacturer>()
                            filter.forEach { filterWord ->
                                refinedManufacturerList.addAll(manufacturers.filter { manufacturer -> manufacturer.name?.containIgnoreCase(filterWord)?:false })
                            }
                            if(filter.isEmpty()) refinedManufacturerList.addAll(manufacturers)
                            textIoService.displayChatbotMessage("J'ai trouvez ${refinedManufacturerList.size} constructeur correspondant a votre recherche.")
                            val item = textIoService.selectItemFromList(refinedManufacturerList, false, false)
                            textIoService.displayChatbotMessage("Vous avez selectionnez le constructeur ${item.result?.getTerminalString()}")
                        }
                    }.subscribe()
        } else if(targets.contains(KeywordTarget.MAKE)) {
            // Selection d'une marque
            val extractor = getMostRelevantExtractor(extractors, KeywordTarget.MAKE)
            vpicService.allMakes()
                    .doOnError {error -> textIoService.displayChatbotMessage("Erreur lors de la recuperation des marques ${error.message}") }
                    .doOnSuccess { response ->
                        response.results?.let { makes ->
                            val filter = extractorWordWithSelector(extractor, question)
                            val refinedMakeList = ArrayList<Make>()
                            filter.forEach { filterWord ->
                                refinedMakeList.addAll(makes.filter { make -> make.name?.containIgnoreCase(filterWord)?:false })
                            }
                            if(filter.isEmpty()) refinedMakeList.addAll(makes)
                            textIoService.displayChatbotMessage("J'ai trouvez ${refinedMakeList.size} marques correspondant a votre recherche.")
                            val item = textIoService.selectItemFromList(refinedMakeList, false, false)
                            textIoService.displayChatbotMessage("Vous avez selectionnez la marque ${item.result?.getTerminalString()}")
                        }
                    }.subscribe()
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

    /**
     * Function helping us to get the most relevent extractor for a type
     */
    private fun getMostRelevantExtractor(extractors: List<Extractor>, target: KeywordTarget) : Extractor {
        return extractors.filter { extractor -> extractor.keyword.target == target}
                .sorted()
                .first()
    }

    /**
     * function used to extract word of the question by the extractor
     */
    private fun extractorWordWithSelector(extractor: Extractor, question: String) : List<String> {
        return extractor.selector?.let {selector ->
            val words = question.split(' ')
            val keywordIndex = words.indexOf(extractor.keyword.word)
            return if(selector.after)
                words.subList(keywordIndex + 1, keywordIndex + 1 + selector.nbrWord)
            else
                words.subList(keywordIndex - selector.nbrWord, keywordIndex)
        }?:ArrayList<String>()
    }
}
