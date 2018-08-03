package com.wabradshaw.travelhistory.history

import org.joda.time.DateTime
import org.junit.Test
import org.junit.Assert.assertEquals
import org.mockito.Mockito

/**
 * A set of tests for the HistoryController
 */
class HistoryControllerTest {

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

        val mockService = Mockito.mock(HistoryService::class.java)
        Mockito.`when`(mockService.getCompleteHistory()).thenReturn(target)

        val controller = HistoryController(mockService)
        val result = controller.getCompleteHistory()

        assertEquals(200, result.statusCodeValue)
        assertEquals(target, result.body)
    }
}