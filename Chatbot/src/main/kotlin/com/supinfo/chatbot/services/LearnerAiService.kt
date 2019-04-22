package com.supinfo.chatbot.services

import com.supinfo.chatbot.api.VpicService
import com.supinfo.chatbot.api.dto.Manufacturer
import io.reactivex.Single
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

    fun allManufacturers() = vpicService.allManufecturers()

}
