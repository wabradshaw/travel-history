package com.wabradshaw.travelhistory.vcs.meta

import org.springframework.beans.factory.annotation.Value
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

/**
 * A simple REST controller for debugging purposes that returns the version of travel-history this is using from
 * the application properties. The version is used in application properties to ensure that they are being loaded.
 */
@RestController
class VersionController {

    @Value("\${version}")
    lateinit var currentVersion: String

    /**
     * Gets the version of the system as defined in application properties. Used to check that the application is using
     * the correct version of the application properties.
     */
    @GetMapping("/version")
    fun getVersion() = currentVersion

}