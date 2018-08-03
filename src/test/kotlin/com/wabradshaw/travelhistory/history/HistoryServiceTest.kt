package com.wabradshaw.travelhistory.history

import org.joda.time.DateTime
import org.junit.Assert
import org.junit.Test
import org.mockito.Mockito

/**
 * A set of tests for the HistoryService
 */
class HistoryServiceTest {
    /**
     * Tests getCompleteHistory when everything works correctly.
     */
    @Test
    fun testGetCompleteHistory() {

        val target = listOf(LocationHistory(DateTime(0),
                DateTime(1),
                "Tirana",
                "Albania",
                2,
                BlogPost("https://example.com", "Tirana"),
                "https://example2.com"))

        val mockRepository = Mockito.mock(HistoryRepository::class.java)
        Mockito.`when`(mockRepository.getAllHistory()).thenReturn(target)

        val service = HistoryService()
        service.repository = mockRepository;

        val result = service.getCompleteHistory()

        Assert.assertEquals(target, result)
    }
}