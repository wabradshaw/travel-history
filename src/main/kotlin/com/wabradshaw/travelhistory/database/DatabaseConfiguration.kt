package com.wabradshaw.travelhistory.database

import org.springframework.boot.context.properties.ConfigurationProperties

/**
 * A set of configuration parameters managing the connection to the database.
 *
 * @property password The password for the user in the database
 * @property user The username of the database user
 * @property url The connection URL for the database
 * @property driver The name of the driver to use to connect to this type of database
 */
@ConfigurationProperties("db")
class DatabaseConfiguration {
    lateinit var password: String
    lateinit var user: String
    lateinit var url: String
    lateinit var driver: String
}