package com.wabradshaw.travelhistory.history

import org.joda.time.DateTime
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

/**
 * The HistoryController is the class which provides a RESTful endpoints for viewing the saved location history.
 */
@RestController
class HistoryController(val service: HistoryService = HistoryService()){

    /**
     * Gets the list of all locations that have been visited.
     */
    @GetMapping("/history")
    fun getCompleteHistory(): ResponseEntity<List<LocationHistory>> {
        return ResponseEntity.ok(service.getCompleteHistory())
    }
}