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