package com.wabradshaw.travelhistory.history

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

        return allHistory.get(0);
    }
}