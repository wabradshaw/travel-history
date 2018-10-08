package com.wabradshaw.travelhistory.history

import org.joda.time.DateTime
import org.junit.Assert
import org.junit.Test
import org.mockito.Mockito
import org.mockito.Mockito.*

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
                "Balkans",
                "Tirana",
                "Albania",
                2,
                BlogPost("https://example.com", "Tirana"),
                "https://example2.com"))

        val mockRepository = Mockito.mock(HistoryRepository::class.java)
        Mockito.`when`(mockRepository.getAllHistory()).thenReturn(target)

        val service = HistoryService()
        service.repository = mockRepository

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
        service.repository = mockRepository

        val result = service.getCurrentLocation()

        Assert.assertEquals(null, result)
    }

    /**
     * Tests getCurrentLocation when the repository only contains entries in the future will return null.
     */
    @Test
    fun testGetCurrentLocation_TooEarly() {

        val history = listOf(LocationHistory(1, DateTime.now().plusDays(1), null, "z", "a","a", 1),
                                                LocationHistory(2, DateTime.now().plusDays(5), null, "z", "b","b", 1))

        val mockRepository = Mockito.mock(HistoryRepository::class.java)
        Mockito.`when`(mockRepository.getAllHistory()).thenReturn(history)

        val service = HistoryService()
        service.repository = mockRepository

        val result = service.getCurrentLocation()

        Assert.assertEquals(null, result)
    }

    /**
     * Tests getCurrentLocation when the repository only contains entries which have already passed will return null.
     */
    @Test
    fun testGetCurrentLocation_AllComplete() {

        val history = listOf(LocationHistory(1, DateTime.now().plusDays(-10), DateTime.now().plusDays(-5), "z", "a","a", 1),
                                                LocationHistory(2, DateTime.now().plusDays(-5), DateTime.now().plusDays(-1), "z", "b","b", 1))

        val mockRepository = Mockito.mock(HistoryRepository::class.java)
        Mockito.`when`(mockRepository.getAllHistory()).thenReturn(history)

        val service = HistoryService()
        service.repository = mockRepository

        val result = service.getCurrentLocation()

        Assert.assertEquals(null, result)
    }

    /**
     * Tests getCurrentLocation when the repository only contains one entry which is ongoing (but has no known end date)
     */
    @Test
    fun testGetCurrentLocation_OneOngoing_UnknownEnd() {

        val history = listOf(LocationHistory(1, DateTime.now().plusDays(-10), null, "z", "a","a", 1))

        val mockRepository = Mockito.mock(HistoryRepository::class.java)
        Mockito.`when`(mockRepository.getAllHistory()).thenReturn(history)

        val service = HistoryService()
        service.repository = mockRepository

        val result = service.getCurrentLocation()

        Assert.assertEquals("a", result?.name)
    }

    /**
     * Tests getCurrentLocation when the repository only contains one entry which is ongoing (including a known end
     * date).
     */
    @Test
    fun testGetCurrentLocation_OneOngoing_KnownEnd() {

        val history = listOf(LocationHistory(1, DateTime.now().plusDays(-10), DateTime.now().plusDays(10), "z", "a","a", 1))

        val mockRepository = Mockito.mock(HistoryRepository::class.java)
        Mockito.`when`(mockRepository.getAllHistory()).thenReturn(history)

        val service = HistoryService()
        service.repository = mockRepository

        val result = service.getCurrentLocation()

        Assert.assertEquals("a", result?.name)
    }

    /**
     * Tests getCurrentLocation when the repository contains multiple ongoing entries will choose the one with the
     * latest start date.
     */
    @Test
    fun testGetCurrentLocation_MultipleOngoing() {

        val history = listOf(LocationHistory(1, DateTime.now().plusDays(-10), DateTime.now().plusDays(10), "z", "a","a", 1),
                LocationHistory(2, DateTime.now().plusDays(-4), null, "z", "b","b", 1),
                LocationHistory(3, DateTime.now().plusDays(-7), null, "z", "c","c", 1))

        val mockRepository = Mockito.mock(HistoryRepository::class.java)
        Mockito.`when`(mockRepository.getAllHistory()).thenReturn(history)

        val service = HistoryService()
        service.repository = mockRepository

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
        service.repository = mockRepository

        val result = service.getNextLocation()

        Assert.assertEquals(null, result)
    }

    /**
     * Tests getNextLocation when the repository only contains entries in the past/present will return null.
     */
    @Test
    fun testGetNextLocation_TooLate() {

        val history = listOf(LocationHistory(1, DateTime.now().plusDays(-1), null, "z", "a","a", 1),
                                                LocationHistory(2, DateTime.now().plusDays(-5), null, "z", "b","b", 1))

        val mockRepository = Mockito.mock(HistoryRepository::class.java)
        Mockito.`when`(mockRepository.getAllHistory()).thenReturn(history)

        val service = HistoryService()
        service.repository = mockRepository

        val result = service.getNextLocation()

        Assert.assertEquals(null, result)
    }

    /**
     * Tests getNextLocation when the repository contains one more location will return that.
     */
    @Test
    fun testGetNextLocation_OneNext() {

        val history = listOf(LocationHistory(1, DateTime.now().plusDays(1), null, "z", "a","a", 1))

        val mockRepository = Mockito.mock(HistoryRepository::class.java)
        Mockito.`when`(mockRepository.getAllHistory()).thenReturn(history)

        val service = HistoryService()
        service.repository = mockRepository

        val result = service.getNextLocation()

        Assert.assertEquals("a", result?.name)
    }


    /**
     * Tests getNextLocation when the repository contains several more locations will return the earliest.
     */
    @Test
    fun testGetNextLocation_MultipleNext() {

        val history = listOf(LocationHistory(1, DateTime.now().plusDays(3), null, "z", "a","a", 1),
                                                LocationHistory(2, DateTime.now().plusDays(1), null, "z", "b","b", 1),
                                                LocationHistory(3, DateTime.now().plusDays(5), null, "z", "c","c", 1))

        val mockRepository = Mockito.mock(HistoryRepository::class.java)
        Mockito.`when`(mockRepository.getAllHistory()).thenReturn(history)

        val service = HistoryService()
        service.repository = mockRepository

        val result = service.getNextLocation()

        Assert.assertEquals("b", result?.name)
    }

    /**
     * Tests getHistoricalLocation when the repository doesn't contain any history will return null.
     */
    @Test
    fun testGetHistoricalLocation_NoHistory() {

        val mockRepository = Mockito.mock(HistoryRepository::class.java)
        Mockito.`when`(mockRepository.getAllHistory()).thenReturn(emptyList())

        val service = HistoryService()
        service.repository = mockRepository

        val result = service.getHistoricalLocation(DateTime.now())

        Assert.assertEquals(null, result)
    }

    /**
     * Tests getHistoricalLocation when the repository only contains entries in the future will return null.
     */
    @Test
    fun testGetHistoricalLocation_OnlyFuture() {

        val history = listOf(LocationHistory(1, DateTime(100), null, "z", "a","a", 1),
                LocationHistory(2, DateTime(200), null, "z", "b","b", 1))

        val mockRepository = Mockito.mock(HistoryRepository::class.java)
        Mockito.`when`(mockRepository.getAllHistory()).thenReturn(history)

        val service = HistoryService()
        service.repository = mockRepository

        val result = service.getHistoricalLocation(DateTime(0))

        Assert.assertEquals(null, result)
    }

    /**
     * Tests getHistoricalLocation when the repository only contains entries in the past will return null.
     */
    @Test
    fun testGetHistoricalLocation_OnlyPast() {

        val history = listOf(LocationHistory(1, DateTime(100), DateTime(500), "z", "a","a", 1),
                LocationHistory(2, DateTime(500), DateTime(700), "z", "b","b", 1))

        val mockRepository = Mockito.mock(HistoryRepository::class.java)
        Mockito.`when`(mockRepository.getAllHistory()).thenReturn(history)

        val service = HistoryService()
        service.repository = mockRepository

        val result = service.getHistoricalLocation(DateTime(1000))

        Assert.assertEquals(null, result)
    }

    /**
     * Tests getHistoricalLocation when the repository contains one location will return that.
     */
    @Test
    fun testGetHistoricalLocation_OneValid() {

        val history = listOf(LocationHistory(1, DateTime(0), DateTime(200), "z", "a","a", 1))

        val mockRepository = Mockito.mock(HistoryRepository::class.java)
        Mockito.`when`(mockRepository.getAllHistory()).thenReturn(history)

        val service = HistoryService()
        service.repository = mockRepository

        val result = service.getHistoricalLocation(DateTime(100))

        Assert.assertEquals("a", result?.name)
    }

    /**
     * Tests getHistoricalLocation is able to return ongoing times.
     */
    @Test
    fun testGetHistoricalLocation_Ongoing() {

        val history = listOf(LocationHistory(1, DateTime(0), DateTime(200), "z", "a","a", 1))

        val mockRepository = Mockito.mock(HistoryRepository::class.java)
        Mockito.`when`(mockRepository.getAllHistory()).thenReturn(history)

        val service = HistoryService()
        service.repository = mockRepository

        val result = service.getHistoricalLocation(DateTime(100))

        Assert.assertEquals("a", result?.name)
    }

    /**
     * Tests getHistoricalLocation when the repository contains several more locations will return the latest.
     */
    @Test
    fun testGetHistoricalLocation_MultipleValid() {

        val history = listOf(LocationHistory(1, DateTime(300), DateTime(800), "z", "a","a", 1),
                LocationHistory(2, DateTime(500), DateTime(900), "z", "b","b", 1),
                LocationHistory(3, DateTime(400), DateTime(1000), "z", "c","c", 1))

        val mockRepository = Mockito.mock(HistoryRepository::class.java)
        Mockito.`when`(mockRepository.getAllHistory()).thenReturn(history)

        val service = HistoryService()
        service.repository = mockRepository

        val result = service.getHistoricalLocation(DateTime(700))

        Assert.assertEquals("b", result?.name)
    }

    /**
     * Tests getHistoricalPeriod will return the empty list if there's no history.
     */
    @Test
    fun testGetHistoricalPeriod_NoHistory() {

        val history = emptyList<LocationHistory>()

        val mockRepository = Mockito.mock(HistoryRepository::class.java)
        Mockito.`when`(mockRepository.getAllHistory()).thenReturn(history)

        val service = HistoryService()
        service.repository = mockRepository

        val result = service.getHistoricalPeriod(DateTime(10), DateTime(20))

        Assert.assertEquals(emptyList<LocationHistory>(), result)
    }

    /**
     * Tests getHistoricalPeriod will exclude an event which started and finished after the time period.
     */
    @Test
    fun testGetHistoricalPeriod_After() {

        val history = listOf(LocationHistory(1, DateTime(30),DateTime(40), "z", "a","b",1))

        val mockRepository = Mockito.mock(HistoryRepository::class.java)
        Mockito.`when`(mockRepository.getAllHistory()).thenReturn(history)

        val service = HistoryService()
        service.repository = mockRepository

        val result = service.getHistoricalPeriod(DateTime(10), DateTime(20))

        Assert.assertEquals(emptyList<LocationHistory>(), result)
    }

    /**
     * Tests getHistoricalPeriod will exclude an event which started at the exact end of the time period.
     */
    @Test
    fun testGetHistoricalPeriod_ImmediatelyAfter() {

        val history = listOf(LocationHistory(1, DateTime(20),DateTime(30), "z", "a","b",1))

        val mockRepository = Mockito.mock(HistoryRepository::class.java)
        Mockito.`when`(mockRepository.getAllHistory()).thenReturn(history)

        val service = HistoryService()
        service.repository = mockRepository

        val result = service.getHistoricalPeriod(DateTime(10), DateTime(20))

        Assert.assertEquals(emptyList<LocationHistory>(), result)
    }

    /**
     * Tests getHistoricalPeriod will exclude an event which started at the exact end of the time period, but doesn't
     * have an end time.
     */
    @Test
    fun testGetHistoricalPeriod_OngoingAfter() {

        val history = listOf(LocationHistory(1, DateTime(20),null, "z", "a","b",1))

        val mockRepository = Mockito.mock(HistoryRepository::class.java)
        Mockito.`when`(mockRepository.getAllHistory()).thenReturn(history)

        val service = HistoryService()
        service.repository = mockRepository

        val result = service.getHistoricalPeriod(DateTime(10), DateTime(20))

        Assert.assertEquals(emptyList<LocationHistory>(), result)
    }

    /**
     * Tests getHistoricalPeriod will include an event which started during the time period but continued after.
     */
    @Test
    fun testGetHistoricalPeriod_StartsDuring() {

        val history = listOf(LocationHistory(1, DateTime(10),DateTime(30), "z", "a","b",1))

        val mockRepository = Mockito.mock(HistoryRepository::class.java)
        Mockito.`when`(mockRepository.getAllHistory()).thenReturn(history)

        val service = HistoryService()
        service.repository = mockRepository

        val result = service.getHistoricalPeriod(DateTime(10), DateTime(20))

        Assert.assertEquals(history, result)
    }

    /**
     * Tests getHistoricalPeriod will include an event which is completely within the time period.
     */
    @Test
    fun testGetHistoricalPeriod_Within() {

        val history = listOf(LocationHistory(1, DateTime(15),DateTime(18), "z", "a","b",1))

        val mockRepository = Mockito.mock(HistoryRepository::class.java)
        Mockito.`when`(mockRepository.getAllHistory()).thenReturn(history)

        val service = HistoryService()
        service.repository = mockRepository

        val result = service.getHistoricalPeriod(DateTime(10), DateTime(20))

        Assert.assertEquals(history, result)
    }

    /**
     * Tests getHistoricalPeriod will include an event which started before the time period and finished after it.
     */
    @Test
    fun testGetHistoricalPeriod_Throughout() {

        val history = listOf(LocationHistory(1, DateTime(5),DateTime(28), "z", "a","b",1))

        val mockRepository = Mockito.mock(HistoryRepository::class.java)
        Mockito.`when`(mockRepository.getAllHistory()).thenReturn(history)

        val service = HistoryService()
        service.repository = mockRepository

        val result = service.getHistoricalPeriod(DateTime(10), DateTime(20))

        Assert.assertEquals(history, result)
    }

    /**
     * Tests getHistoricalPeriod will include an event which started and finished exactly when the time period did.
     */
    @Test
    fun testGetHistoricalPeriod_Exact() {

        val history = listOf(LocationHistory(1, DateTime(10),DateTime(20), "z", "a","b",1))

        val mockRepository = Mockito.mock(HistoryRepository::class.java)
        Mockito.`when`(mockRepository.getAllHistory()).thenReturn(history)

        val service = HistoryService()
        service.repository = mockRepository

        val result = service.getHistoricalPeriod(DateTime(10), DateTime(20))

        Assert.assertEquals(history, result)
    }

    /**
     * Tests getHistoricalPeriod will include an event which started during the time period and has no end date.
     */
    @Test
    fun testGetHistoricalPeriod_OngoingDuring() {

        val history = listOf(LocationHistory(1, DateTime(15), null, "z", "a","b",1))

        val mockRepository = Mockito.mock(HistoryRepository::class.java)
        Mockito.`when`(mockRepository.getAllHistory()).thenReturn(history)

        val service = HistoryService()
        service.repository = mockRepository

        val result = service.getHistoricalPeriod(DateTime(10), DateTime(20))

        Assert.assertEquals(history, result)
    }



    /**
     * Tests getHistoricalPeriod will include an event which started before the time period but ended during it.
     */
    @Test
    fun testGetHistoricalPeriod_EndsDuring() {

        val history = listOf(LocationHistory(1, DateTime(5),DateTime(15), "z", "a","b",1))

        val mockRepository = Mockito.mock(HistoryRepository::class.java)
        Mockito.`when`(mockRepository.getAllHistory()).thenReturn(history)

        val service = HistoryService()
        service.repository = mockRepository

        val result = service.getHistoricalPeriod(DateTime(10), DateTime(20))

        Assert.assertEquals(history, result)
    }

    /**
     * Tests getHistoricalPeriod will exclude an event which finished before the time period.
     */
    @Test
    fun testGetHistoricalPeriod_EndsBefore() {

        val history = listOf(LocationHistory(1, DateTime(0),DateTime(5), "z", "a","b",1))

        val mockRepository = Mockito.mock(HistoryRepository::class.java)
        Mockito.`when`(mockRepository.getAllHistory()).thenReturn(history)

        val service = HistoryService()
        service.repository = mockRepository

        val result = service.getHistoricalPeriod(DateTime(10), DateTime(20))

        Assert.assertEquals(emptyList<LocationHistory>(), result)
    }

    /**
     * Tests getHistoricalPeriod will exclude an event which finished immediately before the time period.
     */
    @Test
    fun testGetHistoricalPeriod_EndsImmediatelyBefore() {

        val history = listOf(LocationHistory(1, DateTime(0),DateTime(10), "z", "a","b",1))

        val mockRepository = Mockito.mock(HistoryRepository::class.java)
        Mockito.`when`(mockRepository.getAllHistory()).thenReturn(history)

        val service = HistoryService()
        service.repository = mockRepository

        val result = service.getHistoricalPeriod(DateTime(10), DateTime(20))

        Assert.assertEquals(emptyList<LocationHistory>(), result)
    }

    /**
     * Tests getHistoricalPeriod will include an event which started before the time period but is ongoing.
     */
    @Test
    fun testGetHistoricalPeriod_BeforeOngoing() {

        val history = listOf(LocationHistory(1, DateTime(0), null, "z", "a","b",1))

        val mockRepository = Mockito.mock(HistoryRepository::class.java)
        Mockito.`when`(mockRepository.getAllHistory()).thenReturn(history)

        val service = HistoryService()
        service.repository = mockRepository

        val result = service.getHistoricalPeriod(DateTime(10), DateTime(20))

        Assert.assertEquals(history, result)
    }

    /**
     * Tests getHistoricalPeriod can return multiple events, and will put them in order
     */
    @Test
    fun testGetHistoricalPeriod_Multiple() {

        val history = listOf(LocationHistory(1, DateTime(18),null, "z", "a","A",1),
                                                LocationHistory(1, DateTime(5),DateTime(15), "z", "b","B",1),
                                                LocationHistory(1, DateTime(15),DateTime(18),"z",  "c","C",1))

        val mockRepository = Mockito.mock(HistoryRepository::class.java)
        Mockito.`when`(mockRepository.getAllHistory()).thenReturn(history)

        val service = HistoryService()
        service.repository = mockRepository

        val result = service.getHistoricalPeriod(DateTime(10), DateTime(20))

        Assert.assertEquals(3, result.size)
        Assert.assertEquals("b", result[0].name)
        Assert.assertEquals("c", result[1].name)
        Assert.assertEquals("a", result[2].name)
    }

    /**
     * Tests getPreviousLocation when the repository doesn't contain any history will return null.
     */
    @Test
    fun testGetPreviousLocation_NoHistory() {

        val mockRepository = Mockito.mock(HistoryRepository::class.java)
        Mockito.`when`(mockRepository.getAllHistory()).thenReturn(emptyList())

        val service = HistoryService()
        service.repository = mockRepository

        val result = service.getPreviousLocation(DateTime.now())

        Assert.assertEquals(null, result)
    }

    /**
     * Tests getPreviousLocation when the repository only contains entries in the future will return null.
     */
    @Test
    fun testGetPreviousLocation_OnlyFuture() {

        val history = listOf(LocationHistory(1, DateTime(100), null, "z", "a","a", 1),
                                                LocationHistory(2, DateTime(200), null, "z", "b","b", 1))

        val mockRepository = Mockito.mock(HistoryRepository::class.java)
        Mockito.`when`(mockRepository.getAllHistory()).thenReturn(history)

        val service = HistoryService()
        service.repository = mockRepository

        val result = service.getPreviousLocation(DateTime(0))

        Assert.assertEquals(null, result)
    }

    /**
     * Tests getPreviousLocation when the repository contains one more location will return that.
     */
    @Test
    fun testGetPreviousLocation_OnePrevious() {

        val history = listOf(LocationHistory(1, DateTime(0), null, "z", "a","a", 1))

        val mockRepository = Mockito.mock(HistoryRepository::class.java)
        Mockito.`when`(mockRepository.getAllHistory()).thenReturn(history)

        val service = HistoryService()
        service.repository = mockRepository

        val result = service.getPreviousLocation(DateTime(100))

        Assert.assertEquals("a", result?.name)
    }


    /**
     * Tests getPreviousLocation when the repository contains several more locations will return the latest.
     */
    @Test
    fun testGetPreviousLocation_MultiplePrevious() {

        val history = listOf(LocationHistory(1, DateTime(300), null, "z", "a","a", 1),
                                                LocationHistory(2, DateTime(500), null, "z", "b","b", 1),
                                                LocationHistory(3, DateTime(400), null, "z", "c","c", 1))

        val mockRepository = Mockito.mock(HistoryRepository::class.java)
        Mockito.`when`(mockRepository.getAllHistory()).thenReturn(history)

        val service = HistoryService()
        service.repository = mockRepository

        val result = service.getPreviousLocation(DateTime(700))

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
        service.repository = mockRepository

        val result = service.getLatestBlogPost()

        Assert.assertEquals(null, result)
    }

    /**
     * Tests getLatestBlogPost when the repository contains history, but no blog posts will return null.
     */
    @Test
    fun testGetLatestBlogPost_noBlogPosts() {

        val history = listOf(LocationHistory(1, DateTime.now().plusDays(-10), DateTime.now().plusDays(-5), "z", "a","a", 1),
                                                LocationHistory(2, DateTime.now().plusDays(-5), DateTime.now().plusDays(-1), "z", "b","b", 1))

        val mockRepository = Mockito.mock(HistoryRepository::class.java)
        Mockito.`when`(mockRepository.getAllHistory()).thenReturn(history)

        val service = HistoryService()
        service.repository = mockRepository

        val result = service.getLatestBlogPost()

        Assert.assertEquals(null, result)
    }

    /**
     * Tests getLatestBlogPost when the repository contains a single blog post will return it.
     */
    @Test
    fun testGetLatestBlogPost_oneBlogPost() {

        val history = listOf(LocationHistory(1, DateTime.now().plusDays(-10), DateTime.now().plusDays(-5), "z", "a","a", 1, BlogPost("https://example.com","blog1")))

        val mockRepository = Mockito.mock(HistoryRepository::class.java)
        Mockito.`when`(mockRepository.getAllHistory()).thenReturn(history)

        val service = HistoryService()
        service.repository = mockRepository

        val result = service.getLatestBlogPost()

        Assert.assertEquals("blog1", result?.name)
    }

    /**
     * Tests getLatestBlogPost when the repository contains multiple blog posts will return the latest one.
     */
    @Test
    fun testGetLatestBlogPost_multipleBlogPost() {

        val history = listOf(LocationHistory(1, DateTime.now().plusDays(-10), DateTime.now().plusDays(-5), "z", "a","a", 1, BlogPost("https://example.com","blog1")),
                                                LocationHistory(2, DateTime.now().plusDays(-3), DateTime.now().plusDays(-5), "z", "b","b", 1, BlogPost("https://example.com","blog2")),
                                                LocationHistory(3, DateTime.now().plusDays(-6), DateTime.now().plusDays(-5), "z", "c","c", 1, BlogPost("https://example.com","blog3")))

        val mockRepository = Mockito.mock(HistoryRepository::class.java)
        Mockito.`when`(mockRepository.getAllHistory()).thenReturn(history)

        val service = HistoryService()
        service.repository = mockRepository

        val result = service.getLatestBlogPost()

        Assert.assertEquals("blog2", result?.name)
    }

    /**
     * Tests calling addTrip with all possible information.
     */
    @Test
    fun testAddTrip_allKnown(){

        val mockRepository = Mockito.mock(HistoryRepository::class.java)
        Mockito.`when`(mockRepository.getAllHistory()).thenReturn(emptyList())

        val service = HistoryService()
        service.repository = mockRepository

        service.addTrip(DateTime(100), DateTime(200), "z", "a", "b", 2)

        verify(mockRepository).addTrip(DateTime(100), DateTime(200), "z", "a", "b", 2)
    }


    /**
     * Tests calling addTrip missing a group name, but without a history, will use unknown.
     */
    @Test
    fun testAddTrip_unknownGroup_noHistory(){

        val mockRepository = Mockito.mock(HistoryRepository::class.java)
        Mockito.`when`(mockRepository.getAllHistory()).thenReturn(emptyList())

        val service = HistoryService()
        service.repository = mockRepository

        service.addTrip(DateTime(100), DateTime(200), null, "a", "b", 2)

        verify(mockRepository).addTrip(DateTime(100), DateTime(200), "unknown", "a", "b", 2)
    }

    /**
     * Tests calling addTrip missing a group name, but without anything in the past of the history, will use unknown.
     */
    @Test
    fun testAddTrip_unknownGroup_futureHistory(){

        val history = listOf(LocationHistory(1, DateTime(500), null, "holiday", "x", "future", 1))

        val mockRepository = Mockito.mock(HistoryRepository::class.java)
        Mockito.`when`(mockRepository.getAllHistory()).thenReturn(history)

        val service = HistoryService()
        service.repository = mockRepository

        service.addTrip(DateTime(100), DateTime(200), null, "a", "b", 2)

        verify(mockRepository).addTrip(DateTime(100), DateTime(200), "unknown", "a", "b", 2)
    }

    /**
     * Tests calling addTrip missing a group name, with a previous location will use its country.
     */
    @Test
    fun testAddTrip_unknownGroup_onePastHistory(){

        val history = listOf(LocationHistory(1, DateTime(50), null, "q", "x", "past", 1))

        val mockRepository = Mockito.mock(HistoryRepository::class.java)
        Mockito.`when`(mockRepository.getAllHistory()).thenReturn(history)

        val service = HistoryService()
        service.repository = mockRepository

        service.addTrip(DateTime(100), DateTime(200), "z", "a", null, 2)

        verify(mockRepository).addTrip(DateTime(100), DateTime(200), "z", "a", "past", 2)
    }

    /**
     * Tests calling addTrip missing a group name, with multiple previous locations will use the country of the most recent
     */
    @Test
    fun testAddTrip_unknownGroup_multiplePastHistory(){

        val history = listOf(LocationHistory(1, DateTime(50), null, "paster", "y", "c", 1),
                LocationHistory(2, DateTime(75), null, "past", "x", "d", 1),
                LocationHistory(3, DateTime(25), null, "pastest", "w", "e", 1))

        val mockRepository = Mockito.mock(HistoryRepository::class.java)
        Mockito.`when`(mockRepository.getAllHistory()).thenReturn(history)

        val service = HistoryService()
        service.repository = mockRepository

        service.addTrip(DateTime(100), DateTime(200), null, "a", "b", 2)

        verify(mockRepository).addTrip(DateTime(100), DateTime(200), "past","a", "b", 2)
    }
    
    /**
     * Tests calling addTrip missing a country name, but without a history, will use unknown.
     */
    @Test
    fun testAddTrip_unknownCountry_noHistory(){

        val mockRepository = Mockito.mock(HistoryRepository::class.java)
        Mockito.`when`(mockRepository.getAllHistory()).thenReturn(emptyList())

        val service = HistoryService()
        service.repository = mockRepository

        service.addTrip(DateTime(100), DateTime(200), "z", "a", null, 2)

        verify(mockRepository).addTrip(DateTime(100), DateTime(200), "z", "a", "unknown", 2)
    }

    /**
     * Tests calling addTrip missing a country name, but without anything in the past of the history, will use unknown.
     */
    @Test
    fun testAddTrip_unknownCountry_futureHistory(){

        val history = listOf(LocationHistory(1, DateTime(500), null, "q", "x", "future", 1))

        val mockRepository = Mockito.mock(HistoryRepository::class.java)
        Mockito.`when`(mockRepository.getAllHistory()).thenReturn(history)

        val service = HistoryService()
        service.repository = mockRepository

        service.addTrip(DateTime(100), DateTime(200), "z", "a", null, 2)

        verify(mockRepository).addTrip(DateTime(100), DateTime(200), "z", "a", "unknown", 2)
    }

    /**
     * Tests calling addTrip missing a country name, with a previous location will use its country.
     */
    @Test
    fun testAddTrip_unknownCountry_onePastHistory(){

        val history = listOf(LocationHistory(1, DateTime(50), null, "q", "x", "past", 1))

        val mockRepository = Mockito.mock(HistoryRepository::class.java)
        Mockito.`when`(mockRepository.getAllHistory()).thenReturn(history)

        val service = HistoryService()
        service.repository = mockRepository

        service.addTrip(DateTime(100), DateTime(200), "z", "a", null, 2)

        verify(mockRepository).addTrip(DateTime(100), DateTime(200), "z", "a", "past", 2)
    }

    /**
     * Tests calling addTrip missing a country name, with multiple previous locations will use the country of the most recent
     */
    @Test
    fun testAddTrip_unknownCountry_multiplePastHistory(){

        val history = listOf(LocationHistory(1, DateTime(50), null, "q", "y", "paster", 1),
                                                LocationHistory(2, DateTime(75), null, "q", "x", "past", 1),
                                                LocationHistory(3, DateTime(25), null, "q", "w", "pastest", 1))

        val mockRepository = Mockito.mock(HistoryRepository::class.java)
        Mockito.`when`(mockRepository.getAllHistory()).thenReturn(history)

        val service = HistoryService()
        service.repository = mockRepository

        service.addTrip(DateTime(100), DateTime(200), "z", "a", null, 2)

        verify(mockRepository).addTrip(DateTime(100), DateTime(200), "z","a", "past", 2)
    }

    /**
     * Tests calling addTrip missing a timezone, but without a history, will use 0.
     */
    @Test
    fun testAddTrip_unknownTimezone_noHistory(){

        val mockRepository = Mockito.mock(HistoryRepository::class.java)
        Mockito.`when`(mockRepository.getAllHistory()).thenReturn(emptyList())

        val service = HistoryService()
        service.repository = mockRepository

        service.addTrip(DateTime(100), DateTime(200), "z", "a", "b", null)

        verify(mockRepository).addTrip(DateTime(100), DateTime(200), "z", "a", "b", 0)
    }

    /**
     * Tests calling addTrip missing a timezone, but without anything in the past of the history, will use 0.
     */
    @Test
    fun testAddTrip_unknownTimezone_futureHistory(){

        val history = listOf(LocationHistory(1, DateTime(500), null, "q", "y", "future", 1))

        val mockRepository = Mockito.mock(HistoryRepository::class.java)
        Mockito.`when`(mockRepository.getAllHistory()).thenReturn(history)

        val service = HistoryService()
        service.repository = mockRepository

        service.addTrip(DateTime(100), DateTime(200), "z", "a", "b", null)

        verify(mockRepository).addTrip(DateTime(100), DateTime(200), "z", "a", "b", 0)
    }

    /**
     * Tests calling addTrip missing a timezone, with a previous location will use its timezone.
     */
    @Test
    fun testAddTrip_unknownTimezone_onePastHistory(){

        val history = listOf(LocationHistory(1, DateTime(50), null, "q", "y", "past", 1))

        val mockRepository = Mockito.mock(HistoryRepository::class.java)
        Mockito.`when`(mockRepository.getAllHistory()).thenReturn(history)

        val service = HistoryService()
        service.repository = mockRepository

        service.addTrip(DateTime(100), DateTime(200), "z", "a", "b", null)

        verify(mockRepository).addTrip(DateTime(100), DateTime(200), "z", "a", "b", 1)
    }

    /**
     * Tests calling addTrip missing a timezone, with multiple previous locations will use the country of the most recent
     */
    @Test
    fun testAddTrip_unknownTimezone_multiplePastHistory(){

        val history = listOf(LocationHistory(1, DateTime(50), null, "q", "y", "paster", 1),
                                                LocationHistory(2, DateTime(75), null, "q", "x", "past", 2),
                                                LocationHistory(3, DateTime(25), null, "q", "w", "pastest", 3))

        val mockRepository = Mockito.mock(HistoryRepository::class.java)
        Mockito.`when`(mockRepository.getAllHistory()).thenReturn(history)

        val service = HistoryService()
        service.repository = mockRepository

        service.addTrip(DateTime(100), DateTime(200), "z", "a", "b", null)

        verify(mockRepository).addTrip(DateTime(100), DateTime(200), "z", "a", "b", 2)
    }

    /**
     * Tests calling addTrip will finish a historical event missing a start date (but not other events)
     */
    @Test
    fun testAddTrip_finishPast(){

        val history = listOf(LocationHistory(1, DateTime(500), null, "q", "y", "future", 1),
                                                LocationHistory(2, DateTime(75), null, "q", "x", "past", 2),
                                                LocationHistory(3, DateTime(25), DateTime(75), "q", "w", "pastComplete", 3))

        val mockRepository = Mockito.mock(HistoryRepository::class.java)
        Mockito.`when`(mockRepository.getAllHistory()).thenReturn(history)

        val service = HistoryService()
        service.repository = mockRepository

        service.addTrip(DateTime(100), DateTime(200), "z", "a", "b", 2)

        verify(mockRepository, times(2)).getAllHistory()
        verify(mockRepository).addTrip(DateTime(100), DateTime(200), "z", "a", "b", 2)
        verify(mockRepository).updateEndTime(2, DateTime(100))
        verifyNoMoreInteractions(mockRepository)
    }

    /**
     * Tests calling addBlogPost if the location exists.
     */
    @Test
    fun testAddBlogPost_exists(){
        val history = LocationHistory(2, DateTime(75), null, "z", "a", "b", 2)

        val mockRepository = Mockito.mock(HistoryRepository::class.java)
        Mockito.`when`(mockRepository.getSpecificHistory(2)).thenReturn(history)

        val service = HistoryService()
        service.repository = mockRepository

        service.addBlogPost(2, "https://example.com", "example")

        verify(mockRepository).addBlogPost(2, "https://example.com", "example")
    }

    /**
     * Tests calling addBlogPost if the location doesntExist.
     */
    @Test
    fun testAddBlogPost_doesntExist(){

        val mockRepository = Mockito.mock(HistoryRepository::class.java)
        Mockito.`when`(mockRepository.getSpecificHistory(2)).thenReturn(null)

        val service = HistoryService()
        service.repository = mockRepository

        service.addBlogPost(2, "https://example.com", "example")

        verify(mockRepository).getSpecificHistory(2)
        verifyNoMoreInteractions(mockRepository)
    }


    /**
     * Tests calling addBlogPost if the location exists.
     */
    @Test
    fun testAddMap_exists(){
        val history = LocationHistory(2, DateTime(75), null, "z", "a", "b", 2)

        val mockRepository = Mockito.mock(HistoryRepository::class.java)
        Mockito.`when`(mockRepository.getSpecificHistory(2)).thenReturn(history)

        val service = HistoryService()
        service.repository = mockRepository

        service.addMap(2, "https://example.com")

        verify(mockRepository).addMap(2, "https://example.com")
    }

    /**
     * Tests calling addMap if the location doesntExist.
     */
    @Test
    fun testAddMap_doesntExist(){

        val mockRepository = Mockito.mock(HistoryRepository::class.java)
        Mockito.`when`(mockRepository.getSpecificHistory(2)).thenReturn(null)

        val service = HistoryService()
        service.repository = mockRepository

        service.addMap(2, "https://example.com")

        verify(mockRepository).getSpecificHistory(2)
        verifyNoMoreInteractions(mockRepository)
    }

    /**
     * Tests the updateLocation function when the chosen location doesn't exist will return false.
     */
    @Test
    fun testUpdateLocation_doesntExist(){

        val mockRepository = Mockito.mock(HistoryRepository::class.java)
        Mockito.`when`(mockRepository.getSpecificHistory(23)).thenReturn(null)

        val service = HistoryService()
        service.repository = mockRepository

        val result = service.updateLocation(23)

        Assert.assertEquals(false, result)
    }

    /**
     * Tests the updateLocation function when no changes were requested will return true, but not do anything.
     */
    @Test
    fun testUpdateLocation_noChanges(){

        val history = LocationHistory(23, DateTime.now(), null, "z", "a", "a", 2)
        val mockRepository = Mockito.mock(HistoryRepository::class.java)
        Mockito.`when`(mockRepository.getSpecificHistory(23)).thenReturn(history)

        val service = HistoryService()
        service.repository = mockRepository

        val result = service.updateLocation(23)

        verify(mockRepository).getSpecificHistory(23)
        verifyNoMoreInteractions(mockRepository)

        Assert.assertEquals(true, result)
    }

    /**
     * Tests the updateLocation function when trying to change the start date.
     */
    @Test
    fun testUpdateLocation_changeStart(){

        val history = LocationHistory(23, DateTime.now(), null, "z", "a", "a", 2)
        val mockRepository = Mockito.mock(HistoryRepository::class.java)
        Mockito.`when`(mockRepository.getSpecificHistory(23)).thenReturn(history)

        val service = HistoryService()
        service.repository = mockRepository

        val startDate = DateTime(0)
        val result = service.updateLocation(23, startDate = startDate)

        verify(mockRepository).getSpecificHistory(23)
        verify(mockRepository).updateStartTime(23, startDate)
        verifyNoMoreInteractions(mockRepository)

        Assert.assertEquals(true, result)
    }

    /**
     * Tests the updateLocation function when trying to change the end date.
     */
    @Test
    fun testUpdateLocation_changeEnd(){

        val history = LocationHistory(23, DateTime.now(), null, "z", "a", "a", 2)
        val mockRepository = Mockito.mock(HistoryRepository::class.java)
        Mockito.`when`(mockRepository.getSpecificHistory(23)).thenReturn(history)

        val service = HistoryService()
        service.repository = mockRepository

        val endDate = DateTime(0)
        val result = service.updateLocation(23, endDate = endDate)

        verify(mockRepository).getSpecificHistory(23)
        verify(mockRepository).updateEndTime(23, endDate)
        verifyNoMoreInteractions(mockRepository)

        Assert.assertEquals(true, result)
    }

    /**
     * Tests the updateLocation function when trying to change the group name.
     */
    @Test
    fun testUpdateLocation_changeGroup(){

        val history = LocationHistory(23, DateTime.now(), null, "z", "a", "a", 2)
        val mockRepository = Mockito.mock(HistoryRepository::class.java)
        Mockito.`when`(mockRepository.getSpecificHistory(23)).thenReturn(history)

        val service = HistoryService()
        service.repository = mockRepository

        val result = service.updateLocation(23, group = "holiday")

        verify(mockRepository).getSpecificHistory(23)
        verify(mockRepository).updateGroup(23, "holiday")
        verifyNoMoreInteractions(mockRepository)

        Assert.assertEquals(true, result)
    }

    /**
     * Tests the updateLocation function when trying to change the location name.
     */
    @Test
    fun testUpdateLocation_changeName(){

        val history = LocationHistory(23, DateTime.now(), null, "z", "a", "a", 2)
        val mockRepository = Mockito.mock(HistoryRepository::class.java)
        Mockito.`when`(mockRepository.getSpecificHistory(23)).thenReturn(history)

        val service = HistoryService()
        service.repository = mockRepository

        val result = service.updateLocation(23, name = "bob")

        verify(mockRepository).getSpecificHistory(23)
        verify(mockRepository).updateName(23, "bob")
        verifyNoMoreInteractions(mockRepository)

        Assert.assertEquals(true, result)
    }



    /**
     * Tests the updateLocation function when trying to change the country name.
     */
    @Test
    fun testUpdateLocation_changeCountry(){

        val history = LocationHistory(23, DateTime.now(), null, "z", "a", "a", 2)
        val mockRepository = Mockito.mock(HistoryRepository::class.java)
        Mockito.`when`(mockRepository.getSpecificHistory(23)).thenReturn(history)

        val service = HistoryService()
        service.repository = mockRepository

        val result = service.updateLocation(23, country = "dave")

        verify(mockRepository).getSpecificHistory(23)
        verify(mockRepository).updateCountry(23, "dave")
        verifyNoMoreInteractions(mockRepository)

        Assert.assertEquals(true, result)
    }

    /**
     * Tests the updateLocation function when trying to change the timezone.
     */
    @Test
    fun testUpdateLocation_changeTimezone(){

        val history = LocationHistory(23, DateTime.now(), null, "z", "a", "a", 2)
        val mockRepository = Mockito.mock(HistoryRepository::class.java)
        Mockito.`when`(mockRepository.getSpecificHistory(23)).thenReturn(history)

        val service = HistoryService()
        service.repository = mockRepository

        val result = service.updateLocation(23, timezone = 5)

        verify(mockRepository).getSpecificHistory(23)
        verify(mockRepository).updateTimezone(23, 5)
        verifyNoMoreInteractions(mockRepository)

        Assert.assertEquals(true, result)
    }

    /**
     * Tests the updateLocation function when trying to change multiple aspects of the location.
     */
    @Test
    fun testUpdateLocation_changeMultiple(){

        val history = LocationHistory(23, DateTime.now(), null, "z", "a", "a", 2)
        val mockRepository = Mockito.mock(HistoryRepository::class.java)
        Mockito.`when`(mockRepository.getSpecificHistory(23)).thenReturn(history)

        val service = HistoryService()
        service.repository = mockRepository

        val date = DateTime(0)
        val result = service.updateLocation(23, startDate = date, endDate = date, group="holiday", name="bob", country = "dave", timezone = 5)

        verify(mockRepository).getSpecificHistory(23)
        verify(mockRepository).updateStartTime(23, date)
        verify(mockRepository).updateEndTime(23, date)
        verify(mockRepository).updateGroup(23, "holiday")
        verify(mockRepository).updateName(23, "bob")
        verify(mockRepository).updateCountry(23, "dave")
        verify(mockRepository).updateTimezone(23, 5)
        verifyNoMoreInteractions(mockRepository)

        Assert.assertEquals(true, result)
    }

    /**
     * Tests calling deleteBlogPost if the location exists.
     */
    @Test
    fun testDeleteBlogPost_exists(){
        val history = LocationHistory(2, DateTime(75), null, "z", "a", "b", 2)

        val mockRepository = Mockito.mock(HistoryRepository::class.java)
        Mockito.`when`(mockRepository.getSpecificHistory(2)).thenReturn(history)

        val service = HistoryService()
        service.repository = mockRepository

        service.deleteBlogPost(2)

        verify(mockRepository).deleteBlogPost(2)
    }

    /**
     * Tests calling deleteBlogPost if the location doesntExist.
     */
    @Test
    fun testDeleteBlogPost_doesntExist(){

        val mockRepository = Mockito.mock(HistoryRepository::class.java)
        Mockito.`when`(mockRepository.getSpecificHistory(2)).thenReturn(null)

        val service = HistoryService()
        service.repository = mockRepository

        service.deleteBlogPost(2)

        verify(mockRepository).getSpecificHistory(2)
        verifyNoMoreInteractions(mockRepository)
    }
}