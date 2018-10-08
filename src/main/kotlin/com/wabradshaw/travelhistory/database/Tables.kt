package com.wabradshaw.travelhistory.database

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table
import org.joda.time.DateTime

/**
 * The definition of the main History table which contains all of the details about each historical event.
 */
object History : Table() {
    val uuid: Column<Int> = integer("uuid").primaryKey().autoIncrement()
    val startTime: Column<DateTime> = datetime("start_time")
    val endTime: Column<DateTime?> = datetime("end_time").nullable()
    val group: Column<String> = varchar("group", length=600)
    val name: Column<String> = varchar("name", length=600)
    val country: Column<String> = varchar("country", length=600)
    val timezone: Column<Int> = integer("timezone_offset")
    val blogPostUrl: Column<String?> = varchar("blog_post_url", length=6000).nullable()
    val blogPostName: Column<String?> = varchar("blog_post_name", length=6000).nullable()
    val mapUrl: Column<String?> = varchar("map_url", length=6000).nullable()
}