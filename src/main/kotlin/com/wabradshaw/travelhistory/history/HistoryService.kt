package com.wabradshaw.travelhistory.history

import org.joda.time.DateTime
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.GetMapping

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

    fun updateLocation(uuid: Int, startDate: DateTime? = null,
                       endDate: DateTime? = null,
                       name: String? = null,
                       country: String? = null,
                       timezone: Int? = null): Boolean {
        return false
    }
}