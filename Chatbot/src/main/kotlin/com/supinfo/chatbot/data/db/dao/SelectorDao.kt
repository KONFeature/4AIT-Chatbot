package com.supinfo.chatbot.data.db.dao

import com.supinfo.chatbot.data.db.entities.Extractor
import com.supinfo.chatbot.data.db.entities.Keyword
import com.supinfo.chatbot.data.db.entities.Selector
import org.springframework.data.repository.CrudRepository

interface SelectorDao: CrudRepository<Selector, Long> {
}
