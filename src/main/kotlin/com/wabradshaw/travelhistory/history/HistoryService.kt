package com.wabradshaw.travelhistory.history

import org.joda.time.DateTime
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

/**
 * The HistoryService serves as the intermediary between the database layer and the frontend when getting information
 * about the locations a person has been at.
 */
@Service
class HistoryService() {

    @Autowired
    lateinit var repository: HistoryRepository;

    /**
     * Gets the list of all of the locations that a person has visited.
     */
    fun getCompleteHistory(): List<LocationHistory> {
        return repository.getAllHistory()
    }

    /**
     * Gets the location the person is currently in.
     */
    fun getCurrentLocation(): LocationHistory? {
        val allHistory = repository.getAllHistory()

        return allHistory.filter { it.startTime < DateTime.now() }
                         .filter { it.endTime == null || DateTime.now().isBefore(it.endTime) }
                         .sortedBy { it.startTime }
                         .lastOrNull()
    }

    /**
     * Gets the location the person is planning to be in next, if they've planned that far ahead.
     */
    fun getNextLocation(): LocationHistory? {
        val allHistory = repository.getAllHistory()

        return allHistory.filter { it.startTime > DateTime.now() }
                         .sortedBy { it.startTime }
                         .firstOrNull()

    }


    /**
     * Gets the location the person was in on a particular date, if that information is available.
     */
    fun getHistoricalLocation(targetDate: DateTime): LocationHistory? {
        val allHistory = repository.getAllHistory()

        return allHistory.filter { it.startTime < targetDate }
                         .filter { val endTime = it.endTime; endTime == null || endTime >= targetDate}
                         .sortedBy { it.startTime }
                         .lastOrNull()
    }

    /**
     * Gets the list of all of the locations the person was in between two dates, sorted in chronological order.
     */
    fun getHistoricalPeriod(startDate: DateTime, endDate: DateTime): List<LocationHistory> {
        val allHistory = repository.getAllHistory()

        return allHistory.filter{it.startTime < endDate}
                         .filter{val endTime = it.endTime; endTime == null || startDate < endTime }
                         .sortedBy{it.startTime}
    }

    /**
     * Gets the location the person was in, or is currently in, before the target date.
     */
    fun getPreviousLocation(targetDate: DateTime): LocationHistory? {
        val allHistory = repository.getAllHistory()

        return allHistory.filter { it.startTime < targetDate }
                         .sortedBy { it.startTime }
                         .lastOrNull()
    }

    /**
     * Gets the blog post for the most recent location the person has visited and written about. Null if the person
     * hasn't written any blog posts.
     */
    fun getLatestBlogPost(): BlogPost? {
        val allHistory = repository.getAllHistory()

        return allHistory.filter { it.blog != null }
                         .sortedBy { it.startTime }
                         .lastOrNull()
                         ?.blog
    }

    /**
     * Updates some or all of the information about a particular location.
     */
    fun updateLocation(uuid: Int,
                       startDate: DateTime? = null,
                       endDate: DateTime? = null,
                       group: String? = null,
                       name: String? = null,
                       country: String? = null,
                       timezone: Int? = null): Boolean {
        val current = repository.getSpecificHistory(uuid);
        if(current == null){
            return false
        } else {
            if (startDate != null) repository.updateStartTime(uuid, startDate);
            if (endDate != null) repository.updateEndTime(uuid, endDate);
            if (group != null) repository.updateGroup(uuid, group);
            if (name != null) repository.updateName(uuid, name);
            if (country != null) repository.updateCountry(uuid, country);
            if (timezone != null) repository.updateTimezone(uuid, timezone);

            return true
        }
    }

    /**
     * Adds a new trip to the location history. If this trip is after the current trip, this will also make sure that
     * the current trip is complete.
     *
     * @param startDate The dateTime when the trip started.
     * @param endDate The dateTime when the user will move on from the trip. Null if that isn't known yet.
     * @param group The name of the overall group of trips the trip is part of. If this is null, the name of the latest
     *              trip group will be used.
     * @param name The name of the location for the trip.
     * @param country The name of the country the trip is in. If this is null, the country of the latest trip will be
     *                used.
     * @param timezone The timezone offset for the location. If this is null, the timezone of the latest trip will be
     *                used.
     */
    fun addTrip(startDate: DateTime, endDate: DateTime?, group: String?, name: String, country: String?, timezone: Int?) {
        val existingTrips = getCompleteHistory();

        finishEarlierTrips(startDate, existingTrips);

        val previousLocation = getPreviousLocation(startDate);

        repository.addTrip(startDate,
                           endDate,
                           group ?: previousLocation?.group ?: "unknown",
                           name,
                           country ?: previousLocation?.country ?: "unknown",
                           timezone ?:  previousLocation?.timezone ?: 0)
    }

    /**
     * Adds a new blog post to the chosen trip.
     */
    fun addBlogPost(uuid: Int, url: String, name: String): Boolean {
        val current = repository.getSpecificHistory(uuid);
        if(current == null){
            return false
        } else {
            repository.addBlogPost(uuid, url, name)
            return true;
        }
    }

    /**
     * Adds a new map to the chosen trip.
     */
    fun addMap(uuid: Int, url: String): Boolean {
        val current = repository.getSpecificHistory(uuid);
        if(current == null){
            return false
        } else {
            repository.addMap(uuid, url)
            return true;
        }
    }

    /**
     * Removes the blog post from the supplied trip.
     */
    fun deleteBlogPost(uuid: Int): Boolean {
        val current = repository.getSpecificHistory(uuid);
        if(current == null){
            return false
        } else {
            repository.deleteBlogPost(uuid);
            return true
        }
    }

    /**
     * Finishes any trips which were unfinished that started before the given startDate.
     */
    private fun finishEarlierTrips(startDate: DateTime, existingTrips: List<LocationHistory>) {
        existingTrips.filter{it.endTime == null}
                     .filter{it.startTime < startDate}
                     .forEach{
                        repository.updateEndTime(it.uuid, startDate)
                     }
    }

}