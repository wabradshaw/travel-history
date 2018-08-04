package com.wabradshaw.travelhistory.history

import com.wabradshaw.travelhistory.database.History.uuid
import org.joda.time.DateTime
import org.springframework.beans.factory.annotation.Value
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

/**
 * The HistoryController is the class which provides a RESTful endpoints for viewing the saved location history.
 */
@RestController
class HistoryController(val service: HistoryService) {

    @Value("\${key}")
    lateinit var targetKey: String

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

    /**
     * Adds a new trip to the location history. If this trip is after the current trip, this will also make sure that
     * the current trip is complete.
     *
     * @param key The unique key required to make any write operations on this database.
     * @param startDate The dateTime when the trip started.
     * @param endDate The dateTime when the user will move on from the trip. Null if that isn't known yet.
     * @param name The name of the location for the trip.
     * @param country The name of the country the trip is in. If this is null, the name of the latest trip will be
     *                used.
     * @param timezone The timezone offset for the location. If this is null, the name of the latest trip will be
     *                used.
     * TODO: Change this to use a request body, rather than individual request params.
     */
    @PostMapping("/history")
    fun addTrip(@RequestParam(value="key") key: String,
                @RequestParam(value="startDate")
                    @DateTimeFormat(pattern="yyyy-MM-dd'T'HH:mm:ss.SSSZ") startDate: DateTime,
                @RequestParam(value="endDate", required = false)
                    @DateTimeFormat(pattern="yyyy-MM-dd'T'HH:mm:ss.SSSZ") endDate: DateTime?,
                @RequestParam(value="name") name: String,
                @RequestParam(value="country", required = false) country: String?,
                @RequestParam(value="timezone", required = false) timezone: Int?): ResponseEntity<String>{

        if(invalidKey(key)) return ResponseEntity("Invalid authentication key for this request.", HttpStatus.FORBIDDEN)

        service.addTrip(startDate, endDate, name, country, timezone);

        return ResponseEntity.noContent().build();
    }

    /**
     * Updates any or all of the information about a visit to a particular location. This does not include additional
     * resources tied to the location like blog posts or maps.
     *
     * To perform the update, the user must have supplied the correct authentication key.
     */
    @PatchMapping("/history/{uuid}")
    fun updateLocation(@PathVariable(value = "uuid") uuid: Int,
                       @RequestParam(value = "key") key: String,
                       @RequestParam(value = "startDate", required = false)
                        @DateTimeFormat(pattern="yyyy-MM-dd'T'HH:mm:ss.SSSZ") startDate: DateTime?  = null,
                       @RequestParam(value = "endDate", required = false)
                        @DateTimeFormat(pattern="yyyy-MM-dd'T'HH:mm:ss.SSSZ") endDate: DateTime? = null,
                       @RequestParam(value = "name", required = false) name: String? = null,
                       @RequestParam(value = "country", required = false) country: String? = null,
                       @RequestParam(value = "timezone", required = false) timezone: Int? = null): ResponseEntity<String> {

        if(invalidKey(key)) return ResponseEntity("Invalid authentication key for this request.", HttpStatus.FORBIDDEN)

        val exists = service.updateLocation(uuid, startDate, endDate, name, country, timezone)
        if (exists) {
            return ResponseEntity.noContent().build()
        } else {
            return ResponseEntity.unprocessableEntity().body("No historical location with that id exists, so nothing was updated.");
        }
    }

    @PutMapping("/history/{uuid}/blog")
    fun addBlogPost(@PathVariable(value = "uuid") uuid: Int,
                    @RequestParam(value = "key") key: String,
                    @RequestParam(value = "url") url: String,
                    @RequestParam(value = "name") name: String): ResponseEntity<String>{

        if(invalidKey(key)) return ResponseEntity("Invalid authentication key for this request.", HttpStatus.FORBIDDEN)

        val exists = service.addBlogPost(uuid, url, name);
        if (exists) {
            return ResponseEntity.noContent().build()
        } else {
            return ResponseEntity.unprocessableEntity().body("No historical location with that id exists, so nothing was updated.");
        }
    }

    @PutMapping("/history/{uuid}/map")
    fun addMap(@PathVariable(value = "uuid") uuid: Int,
                    @RequestParam(value = "key") key: String,
                    @RequestParam(value = "url") url: String): ResponseEntity<String>{

        if(invalidKey(key)) return ResponseEntity("Invalid authentication key for this request.", HttpStatus.FORBIDDEN)

        val exists = service.addMap(uuid, url);
        if (exists) {
            return ResponseEntity.noContent().build()
        } else {
            return ResponseEntity.unprocessableEntity().body("No historical location with that id exists, so nothing was updated.");
        }
    }

    /**
     * Checks to see if the user supplied authentication key matches the server's authentication key.
     * @param key The user supplied authentication key.
     * @return Returns true if the user is not authenticated.
     */
    private fun invalidKey(key: String): Boolean {
        return !targetKey.equals(key);
    }

}