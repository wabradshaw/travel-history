package com.wabradshaw.travelhistory.history

import org.joda.time.DateTime
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

/**
 * The HistoryController is the class which provides a RESTful endpoints for viewing the saved location history.
 */
@RestController
class HistoryController(val service: HistoryService){

    /**
     * Gets the list of all locations that have been visited.
     */
    @GetMapping("/history")
    fun getCompleteHistory(): ResponseEntity<List<LocationHistory>> {
        return ResponseEntity.ok(service.getCompleteHistory())
    }

    /**
     * Gets the location the person is currently in. If there isn't an entry for the current location, this will return
     * null.
     */
    @GetMapping("/history/current")
    fun getCurrentLocation(): ResponseEntity<LocationHistory?> {
        val currentLocation = service.getCurrentLocation();
        if(currentLocation != null) {
            return ResponseEntity.ok(currentLocation);
        } else {
            return ResponseEntity.noContent().build();
        }
    }

    /**
     * Gets the location the person is planning to be in next. Null if the plan is not yet known.
     */
    @GetMapping("/history/next")
    fun getNextLocation(): ResponseEntity<LocationHistory?> {
        val nextLocation = service.getNextLocation();
        if(nextLocation != null) {
            return ResponseEntity.ok(nextLocation);
        } else {
            return ResponseEntity.noContent().build();
        }
    }
}