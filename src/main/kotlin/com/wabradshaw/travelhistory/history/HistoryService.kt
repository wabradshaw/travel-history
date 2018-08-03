package com.wabradshaw.travelhistory.history

import org.joda.time.DateTime
import org.springframework.web.bind.annotation.GetMapping

class HistoryService {

    @GetMapping("/history")
    fun getCompleteHistory(): List<LocationHistory> {
        return listOf(LocationHistory(DateTime.now(), null, "Tirana","Albania",2, null, null))
    }
}