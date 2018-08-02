package com.wabradshaw.travelhistory.meta

import com.wabradshaw.travelhistory.vcs.meta.VersionController
import org.junit.Assert
import org.junit.Test

/**
 * A set of tests for the VersionController
 */
class VersionControllerTest {

    /**
     * A basic test for the VersionController ensuring that it's getVersion method will get the injected property.
     */
    @Test
    fun testVersionController(){
        val controller = VersionController()
        controller.currentVersion = "testVersion"

        Assert.assertEquals("testVersion", controller.getVersion());
    }
}