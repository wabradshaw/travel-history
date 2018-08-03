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

        val target = listOf(LocationHistory(1,
                DateTime(0),
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

        val history = listOf(LocationHistory(1, DateTime.now().plusDays(1), null, "a","a", 1),
                                                LocationHistory(2, DateTime.now().plusDays(5), null, "b","b", 1))

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

        val history = listOf(LocationHistory(1, DateTime.now().plusDays(-10), DateTime.now().plusDays(-5), "a","a", 1),
                                                LocationHistory(2, DateTime.now().plusDays(-5), DateTime.now().plusDays(-1), "b","b", 1))

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

        val history = listOf(LocationHistory(1, DateTime.now().plusDays(-10), null, "a","a", 1))

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

        val history = listOf(LocationHistory(1, DateTime.now().plusDays(-10), DateTime.now().plusDays(10), "a","a", 1))

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

        val history = listOf(LocationHistory(1, DateTime.now().plusDays(-10), DateTime.now().plusDays(10), "a","a", 1),
                LocationHistory(2, DateTime.now().plusDays(-4), null, "b","b", 1),
                LocationHistory(3, DateTime.now().plusDays(-7), null, "c","c", 1))

        val mockRepository = Mockito.mock(HistoryRepository::class.java)
        Mockito.`when`(mockRepository.getAllHistory()).thenReturn(history)

        val service = HistoryService()
        service.repository = mockRepository;

        val result = service.getCurrentLocation()

        Assert.assertEquals("b", result?.name)
    }

    /**
     * Tests getNextLocation when the repository doesn't contain any history will return null.
     */
    @Test
    fun testGetNextLocation_NoHistory() {

        val history = emptyList<LocationHistory>()

        val mockRepository = Mockito.mock(HistoryRepository::class.java)
        Mockito.`when`(mockRepository.getAllHistory()).thenReturn(history)

        val service = HistoryService()
        service.repository = mockRepository;

        val result = service.getNextLocation()

        Assert.assertEquals(null, result)
    }

    /**
     * Tests getNextLocation when the repository only contains entries in the past/present will return null.
     */
    @Test
    fun testGetNextLocation_TooLate() {

        val history = listOf(LocationHistory(1, DateTime.now().plusDays(-1), null, "a","a", 1),
                                                LocationHistory(2, DateTime.now().plusDays(-5), null, "b","b", 1))

        val mockRepository = Mockito.mock(HistoryRepository::class.java)
        Mockito.`when`(mockRepository.getAllHistory()).thenReturn(history)

        val service = HistoryService()
        service.repository = mockRepository;

        val result = service.getNextLocation()

        Assert.assertEquals(null, result)
    }

    /**
     * Tests getNextLocation when the repository contains one more location will return that.
     */
    @Test
    fun testGetNextLocation_OneNext() {

        val history = listOf(LocationHistory(1, DateTime.now().plusDays(1), null, "a","a", 1))

        val mockRepository = Mockito.mock(HistoryRepository::class.java)
        Mockito.`when`(mockRepository.getAllHistory()).thenReturn(history)

        val service = HistoryService()
        service.repository = mockRepository;

        val result = service.getNextLocation()

        Assert.assertEquals("a", result?.name)
    }


    /**
     * Tests getNextLocation when the repository contains several more locations will return the earliest.
     */
    @Test
    fun testGetNextLocation_MultipleNext() {

        val history = listOf(LocationHistory(1, DateTime.now().plusDays(3), null, "a","a", 1),
                                                LocationHistory(2, DateTime.now().plusDays(1), null, "b","b", 1),
                                                LocationHistory(3, DateTime.now().plusDays(5), null, "c","c", 1))

        val mockRepository = Mockito.mock(HistoryRepository::class.java)
        Mockito.`when`(mockRepository.getAllHistory()).thenReturn(history)

        val service = HistoryService()
        service.repository = mockRepository;

        val result = service.getNextLocation()

        Assert.assertEquals("b", result?.name)
    }

    /**
     * Tests getLatestBlogPost when the repository doesn't contain any history will return null.
     */
    @Test
    fun testGetLatestBlogPost_noHistory() {

        val history = emptyList<LocationHistory>()

        val mockRepository = Mockito.mock(HistoryRepository::class.java)
        Mockito.`when`(mockRepository.getAllHistory()).thenReturn(history)

        val service = HistoryService()
        service.repository = mockRepository;

        val result = service.getLatestBlogPost()

        Assert.assertEquals(null, result)
    }

    /**
     * Tests getLatestBlogPost when the repository contains history, but no blog posts will return null.
     */
    @Test
    fun testGetLatestBlogPost_noBlogPosts() {

        val history = listOf(LocationHistory(1, DateTime.now().plusDays(-10), DateTime.now().plusDays(-5), "a","a", 1),
                                                LocationHistory(2, DateTime.now().plusDays(-5), DateTime.now().plusDays(-1), "b","b", 1))

        val mockRepository = Mockito.mock(HistoryRepository::class.java)
        Mockito.`when`(mockRepository.getAllHistory()).thenReturn(history)

        val service = HistoryService()
        service.repository = mockRepository;

        val result = service.getLatestBlogPost()

        Assert.assertEquals(null, result)
    }

    /**
     * Tests getLatestBlogPost when the repository contains a single blog post will return it.
     */
    @Test
    fun testGetLatestBlogPost_oneBlogPost() {

        val history = listOf(LocationHistory(1, DateTime.now().plusDays(-10), DateTime.now().plusDays(-5), "a","a", 1, BlogPost("https://example.com","blog1")))

        val mockRepository = Mockito.mock(HistoryRepository::class.java)
        Mockito.`when`(mockRepository.getAllHistory()).thenReturn(history)

        val service = HistoryService()
        service.repository = mockRepository;

        val result = service.getLatestBlogPost()

        Assert.assertEquals("blog1", result?.name)
    }

    /**
     * Tests getLatestBlogPost when the repository contains multiple blog posts will return the latest one.
     */
    @Test
    fun testGetLatestBlogPost_multipleBlogPost() {

        val history = listOf(LocationHistory(1, DateTime.now().plusDays(-10), DateTime.now().plusDays(-5), "a","a", 1, BlogPost("https://example.com","blog1")),
                                                LocationHistory(2, DateTime.now().plusDays(-3), DateTime.now().plusDays(-5), "b","b", 1, BlogPost("https://example.com","blog2")),
                                                LocationHistory(3, DateTime.now().plusDays(-6), DateTime.now().plusDays(-5), "c","c", 1, BlogPost("https://example.com","blog3")))

        val mockRepository = Mockito.mock(HistoryRepository::class.java)
        Mockito.`when`(mockRepository.getAllHistory()).thenReturn(history)

        val service = HistoryService()
        service.repository = mockRepository;

        val result = service.getLatestBlogPost()

        Assert.assertEquals("blog2", result?.name)
    }

}