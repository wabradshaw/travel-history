package com.wabradshaw.travelhistory.history

import org.joda.time.DateTime
import org.junit.Test
import org.junit.Assert.assertEquals
import org.mockito.Mockito
import org.mockito.Mockito.verify
import org.mockito.Mockito.verifyZeroInteractions

/**
 * A set of tests for the HistoryController
 */
class HistoryControllerTest {

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

        val mockService = Mockito.mock(HistoryService::class.java)
        Mockito.`when`(mockService.getCompleteHistory()).thenReturn(target)

        val controller = HistoryController(mockService)
        val result = controller.getCompleteHistory()

        assertEquals(200, result.statusCodeValue)
        assertEquals(target, result.body)
    }

    /**
     * Tests the getCurrentLocation method if that information is known.
     */
    @Test
    fun testGetCurrentLocation_hasOne(){
        val target = LocationHistory(1,
                                     DateTime(0),
                                     DateTime(1),
                                     "Tirana",
                                     "Albania",
                                     2,
                                     BlogPost("https://example.com", "Tirana"),
                                     "https://example2.com")

        val mockService = Mockito.mock(HistoryService::class.java)
        Mockito.`when`(mockService.getCurrentLocation()).thenReturn(target)

        val controller = HistoryController(mockService)
        val result = controller.getCurrentLocation()

        assertEquals(200, result.statusCodeValue)
        assertEquals(target, result.body)
    }

    /**
     * Tests the getCurrentLocation method if that information is missing.
     */
    @Test
    fun testGetCurrentLocation_unknown(){

        val mockService = Mockito.mock(HistoryService::class.java)
        Mockito.`when`(mockService.getCurrentLocation()).thenReturn(null)

        val controller = HistoryController(mockService)
        val result = controller.getCurrentLocation()

        assertEquals(204, result.statusCodeValue)
        assertEquals(null, result.body)
    }

    /**
     * Tests the getNextLocation method if that information is known.
     */
    @Test
    fun testGetNextLocation_hasOne(){
        val target = LocationHistory(1,
                                     DateTime(0),
                                     DateTime(1),
                                     "Sarande",
                                     "Albania",
                                     2)

        val mockService = Mockito.mock(HistoryService::class.java)
        Mockito.`when`(mockService.getNextLocation()).thenReturn(target)

        val controller = HistoryController(mockService)
        val result = controller.getNextLocation()

        assertEquals(200, result.statusCodeValue)
        assertEquals(target, result.body)
    }

    /**
     * Tests the getNextLocation method if that information is missing.
     */
    @Test
    fun testGetNextLocation_unknown(){

        val mockService = Mockito.mock(HistoryService::class.java)
        Mockito.`when`(mockService.getNextLocation()).thenReturn(null)

        val controller = HistoryController(mockService)
        val result = controller.getNextLocation()

        assertEquals(204, result.statusCodeValue)
        assertEquals(null, result.body)
    }

    /**
     * Tests the getNextLocation method if that information is known.
     */
    @Test
    fun testGetLatestBlogPost_hasOne(){
        val target = BlogPost("https://example.com", "example")

        val mockService = Mockito.mock(HistoryService::class.java)
        Mockito.`when`(mockService.getLatestBlogPost()).thenReturn(target)

        val controller = HistoryController(mockService)
        val result = controller.getLatestBlogPost()

        assertEquals(200, result.statusCodeValue)
        assertEquals(target, result.body)
    }

    /**
     * Tests the getNextLocation method if that information is missing.
     */
    @Test
    fun testGetLatestBlogPost_unknown(){

        val mockService = Mockito.mock(HistoryService::class.java)
        Mockito.`when`(mockService.getLatestBlogPost()).thenReturn(null)

        val controller = HistoryController(mockService)
        val result = controller.getLatestBlogPost()

        assertEquals(204, result.statusCodeValue)
        assertEquals(null, result.body)
    }

    /**
     * Tests the addTrip method if the user has supplied an incorrect authentication key
     */
    @Test
    fun testAddTrip_invalid(){
        val mockService = Mockito.mock(HistoryService::class.java)

        val controller = HistoryController(mockService)
        controller.targetKey = "correct"
        val result = controller.addTrip("wrong", DateTime(0), DateTime(1), "a", "b", 5)

        verifyZeroInteractions(mockService);

        assertEquals(403, result.statusCodeValue);
    }

    /**
     * Tests the addTrip method if the user has supplied a correct authentication key
     */
    @Test
    fun testAddTrip_valid(){
        val mockService = Mockito.mock(HistoryService::class.java)

        val controller = HistoryController(mockService)
        controller.targetKey = "correct"
        val result = controller.addTrip("correct", DateTime(0), DateTime(1), "a", "b", 5)

        verify(mockService).addTrip(DateTime(0), DateTime(1), "a", "b", 5)

        assertEquals(204, result.statusCodeValue);
    }

    /**
     * Tests the addBlogPost method if the user has supplied an incorrect authentication key
     */
    @Test
    fun testAddBlogPost_invalid(){
        val mockService = Mockito.mock(HistoryService::class.java)

        val controller = HistoryController(mockService)
        controller.targetKey = "correct"
        val result = controller.addBlogPost(23, "wrong", "https://example.com", "bob")

        verifyZeroInteractions(mockService);

        assertEquals(403, result.statusCodeValue);
    }

    /**
     * Tests the addBlogPost method if the user has supplied a correct authentication key, and the location exists.
     */
    @Test
    fun testAddBlogPost_valid(){
        val mockService = Mockito.mock(HistoryService::class.java)
        Mockito.`when`(mockService.addBlogPost(23, "https://example.com", "bob")).thenReturn(true)

        val controller = HistoryController(mockService)
        controller.targetKey = "correct"
        val result = controller.addBlogPost(23, "correct", "https://example.com", "bob")

        verify(mockService).addBlogPost(23, "https://example.com", "bob")

        assertEquals(204, result.statusCodeValue);
    }

    /**
     * Tests the addBlogPost method if the user has supplied a correct authentication key, and the location exists.
     */
    @Test
    fun testAddBlogPost_doesntExist(){
        val mockService = Mockito.mock(HistoryService::class.java)
        Mockito.`when`(mockService.addBlogPost(23, "https://example.com", "bob")).thenReturn(false)

        val controller = HistoryController(mockService)
        controller.targetKey = "correct"
        val result = controller.addBlogPost(23, "correct", "https://example.com", "bob")

        verify(mockService).addBlogPost(23, "https://example.com", "bob")

        assertEquals(422, result.statusCodeValue)
        assertEquals("No historical location with that id exists, so nothing was updated.", result.body)
    }

    /**
     * Tests the updateLocation method if the user has supplied an incorrect authentication key
     */
    @Test
    fun testUpdateLocation_invalid(){
        val mockService = Mockito.mock(HistoryService::class.java)
        Mockito.`when`(mockService.updateLocation(23)).thenReturn(true)

        val controller = HistoryController(mockService)
        controller.targetKey = "correct"
        val result = controller.updateLocation(23, "wrong")

        assertEquals(403, result.statusCodeValue);
    }

    /**
     * Tests the updateLocation method if the service believes the entry exists.
     */
    @Test
    fun testUpdateLocation_exists(){
        val mockService = Mockito.mock(HistoryService::class.java)
        Mockito.`when`(mockService.updateLocation(23)).thenReturn(true)

        val controller = HistoryController(mockService)
        controller.targetKey = "correct"
        val result = controller.updateLocation(23, "correct")

        assertEquals(204, result.statusCodeValue);
    }

    /**
     * Tests the updateLocation method if the service believes the entry exists.
     */
    @Test
    fun testUpdateLocation_doesntExist(){
        val mockService = Mockito.mock(HistoryService::class.java)
        Mockito.`when`(mockService.updateLocation(23)).thenReturn(false)

        val controller = HistoryController(mockService)
        controller.targetKey = "correct"
        val result = controller.updateLocation(23, "correct")

        assertEquals(422, result.statusCodeValue)
        assertEquals("No historical location with that id exists, so nothing was updated.", result.body)
    }
}