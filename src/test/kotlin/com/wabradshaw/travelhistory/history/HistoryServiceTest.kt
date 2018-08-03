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

    /**
     * Tests getCurrentLocation when the repository doesn't contain any history will return null.
     */
    @Test
    fun testGetCurrentLocation_NoHistory() {

        val history = emptyList<LocationHistory>()

        val mockRepository = Mockito.mock(HistoryRepository::class.java)
        Mockito.`when`(mockRepository.getAllHistory()).thenReturn(history)

        val service = HistoryService()
        service.repository = mockRepository;

        val result = service.getCurrentLocation()

        Assert.assertEquals(null, result)
    }

    /**
     * Tests getCurrentLocation when the repository only contains entries in the future will return null.
     */
    @Test
    fun testGetCurrentLocation_TooEarly() {

        val history = listOf(LocationHistory(DateTime.now().plusDays(1), null, "a","a", 1),
                                                LocationHistory(DateTime.now().plusDays(5), null, "b","b", 1))

        val mockRepository = Mockito.mock(HistoryRepository::class.java)
        Mockito.`when`(mockRepository.getAllHistory()).thenReturn(history)

        val service = HistoryService()
        service.repository = mockRepository;

        val result = service.getCurrentLocation()

        Assert.assertEquals(null, result)
    }

    /**
     * Tests getCurrentLocation when the repository only contains entries which have already passed will return null.
     */
    @Test
    fun testGetCurrentLocation_AllComplete() {

        val history = listOf(LocationHistory(DateTime.now().plusDays(-10), DateTime.now().plusDays(-5), "a","a", 1),
                                                LocationHistory(DateTime.now().plusDays(-5), DateTime.now().plusDays(-1), "b","b", 1))

        val mockRepository = Mockito.mock(HistoryRepository::class.java)
        Mockito.`when`(mockRepository.getAllHistory()).thenReturn(history)

        val service = HistoryService()
        service.repository = mockRepository;

        val result = service.getCurrentLocation()

        Assert.assertEquals(null, result)
    }

    /**
     * Tests getCurrentLocation when the repository only contains one entry which is ongoing (but has no known end date)
     */
    @Test
    fun testGetCurrentLocation_OneOngoing_UnknownEnd() {

        val history = listOf(LocationHistory(DateTime.now().plusDays(-10), null, "a","a", 1))

        val mockRepository = Mockito.mock(HistoryRepository::class.java)
        Mockito.`when`(mockRepository.getAllHistory()).thenReturn(history)

        val service = HistoryService()
        service.repository = mockRepository;

        val result = service.getCurrentLocation()

        Assert.assertEquals("a", result?.name)
    }

    /**
     * Tests getCurrentLocation when the repository only contains one entry which is ongoing (including a known end
     * date).
     */
    @Test
    fun testGetCurrentLocation_OneOngoing_KnownEnd() {

        val history = listOf(LocationHistory(DateTime.now().plusDays(-10), DateTime.now().plusDays(10), "a","a", 1))

        val mockRepository = Mockito.mock(HistoryRepository::class.java)
        Mockito.`when`(mockRepository.getAllHistory()).thenReturn(history)

        val service = HistoryService()
        service.repository = mockRepository;

        val result = service.getCurrentLocation()

        Assert.assertEquals("a", result?.name)
    }

    /**
     * Tests getCurrentLocation when the repository contains multiple ongoing entries will choose the one with the
     * latest start date.
     */
    @Test
    fun testGetCurrentLocation_MultipleOngoing() {

        val history = listOf(LocationHistory(DateTime.now().plusDays(-10), DateTime.now().plusDays(10), "a","a", 1),
                LocationHistory(DateTime.now().plusDays(-4), null, "b","b", 1),
                LocationHistory(DateTime.now().plusDays(-7), null, "c","c", 1))

        val mockRepository = Mockito.mock(HistoryRepository::class.java)
        Mockito.`when`(mockRepository.getAllHistory()).thenReturn(history)

        val service = HistoryService()
        service.repository = mockRepository;

        val result = service.getCurrentLocation()

        Assert.assertEquals("b", result?.name)
    }
}