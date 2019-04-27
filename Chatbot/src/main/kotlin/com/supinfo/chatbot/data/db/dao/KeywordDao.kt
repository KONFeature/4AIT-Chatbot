package com.supinfo.chatbot.data.db.dao

import com.supinfo.chatbot.data.db.entities.Extractor
import com.supinfo.chatbot.data.db.entities.Keyword
import org.springframework.data.repository.CrudRepository

interface KeywordDao: CrudRepository<Keyword, Long> {

    fun findAllByWordIsLike(word: String): List<Keyword>

}
