package com.supinfo.chatbot.data.db.dao

import com.supinfo.chatbot.data.db.entities.Extractor
import com.supinfo.chatbot.data.db.entities.Keyword
import org.springframework.data.repository.CrudRepository

interface ExtractorDao: CrudRepository<Extractor, Long> {

    fun findAllByKeywordEquals(keyword: Keyword) : List<Extractor>

}
