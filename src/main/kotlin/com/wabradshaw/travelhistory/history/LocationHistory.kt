package com.wabradshaw.travelhistory.history

import org.joda.time.DateTime

/**
 * The history of a visit to a particular location.
 * @property uuid The unique id for this particular location, used when updating a location.
 * @property startTime The point when you started visiting this location.
 * @property endTime The point when you left this location. Can be null if you haven't left or don't know when you are
 *                   leaving.
 * @property name The name of the place. Typically the name of a city.
 * @property country The country the place is in.
 * @property timezone The timezone offset from UTC.
 * @property blog The blog post describing your experiences in this place, or null if not written yet.
 * @property mapUrl The URL for an image of a map of where you were. Can be null.
 */
data class LocationHistory(val uuid: Int,
                           val startTime: DateTime,
                           var endTime: DateTime?,
                           val name: String,
                           val country: String,
                           val timezone: Int,
                           val blog: BlogPost? = null,
                           val mapUrl: String? = null)