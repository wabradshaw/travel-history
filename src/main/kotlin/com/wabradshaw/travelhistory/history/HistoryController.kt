package com.wabradshaw.travelhistory.history

import org.joda.time.DateTime
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

/**
 * The HistoryController is the class which provides a RESTful endpoints for viewing the saved location history.
 */
@RestController
class HistoryController(val service: HistoryService) {

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
        if (currentLocation != null) {
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
        if (nextLocation != null) {
            return ResponseEntity.ok(nextLocation);
        } else {
            return ResponseEntity.noContent().build();
        }
    }

    /**
     * Gets the latest blog post the person has written. Null if they have not written any blog posts.
     */
    @GetMapping("/blog/latest")
    fun getLatestBlogPost(): ResponseEntity<BlogPost?> {
        val blogPost = service.getLatestBlogPost()
        if (blogPost != null) {
            return ResponseEntity.ok(blogPost);
        } else {
            return ResponseEntity.noContent().build();
        }
    }

    @PatchMapping("/history/{uuid}")
    fun updateLocation(@PathVariable(value = "uuid") uuid: Int,
                       @RequestParam(value = "startDate", required = false) startDate: DateTime?  = null,
                       @RequestParam(value = "endDate", required = false) endDate: DateTime? = null,
                       @RequestParam(value = "name", required = false) name: String? = null,
                       @RequestParam(value = "country", required = false) country: String? = null,
                       @RequestParam(value = "timezone", required = false) timezone: Int? = null): ResponseEntity<String> {

        val exists = service.updateLocation(uuid, startDate, endDate, name, country, timezone)
        if (exists) {
            return ResponseEntity.noContent().build()
        } else {
            return ResponseEntity.unprocessableEntity().body("No historical location with that id exists, so nothing was updated.");
        }

    }
}