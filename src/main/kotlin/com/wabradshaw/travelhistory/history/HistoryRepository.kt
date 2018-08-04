package com.wabradshaw.travelhistory.history

import com.wabradshaw.travelhistory.database.DatabaseConfiguration
import com.wabradshaw.travelhistory.database.History
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.joda.time.DateTime
import org.springframework.stereotype.Repository

/**
 * The HistoryRepository is a database connection class that directly accesses the database containing any and all
 * location history.
 */
@Repository
class HistoryRepository(val db: DatabaseConfiguration) {

    /**
     * Connect to the database. Called by every method here.
     */
    private fun connect() = Database.connect(url=db.url, user=db.user, password=db.password, driver=db.driver)

    /**
     * Gets the list of all LocationHistory rows stored within the database.
     */
    fun getAllHistory() : List<LocationHistory>{
        connect();

        return transaction{
            return@transaction History.selectAll()
                                      .map{row -> LocationHistory(row[History.uuid],
                                                                  row[History.startTime],
                                                                  row[History.endTime],
                                                                  row[History.name],
                                                                  row[History.country],
                                                                  row[History.timezone],
                                                                  getBlogPost(row[History.blogPostUrl], row[History.blogPostName]),
                                                                  row[History.mapUrl])

                                      }
        }
    }

    /**
     * Gets the LocationHistory for the row with the specific uuid stored within the database. Null if no such row
     * exists.
     */
    fun getSpecificHistory(uuid: Int) : LocationHistory?{
        connect();

        return transaction{
            return@transaction History.select({History.uuid.eq(uuid)})
                    .map{row -> LocationHistory(row[History.uuid],
                            row[History.startTime],
                            row[History.endTime],
                            row[History.name],
                            row[History.country],
                            row[History.timezone],
                            getBlogPost(row[History.blogPostUrl], row[History.blogPostName]),
                            row[History.mapUrl])

                    }
                    .firstOrNull()
        }
    }

    /**
     * Inserts the specified trip into the database.
     * @param startDate The dateTime when the trip started.
     * @param endDate The dateTime when the user will move on from the trip. Null if that isn't known yet.
     * @param name The name of the location for the trip.
     * @param country The name of the country the trip is in.
     * @param timezone The timezone offset for the location.
     */
    fun addTrip(startTimeVal: DateTime, endTimeVal: DateTime?, nameVal: String, countryVal: String, timezoneVal: Int){
        connect();

        transaction{
            History.insert{
                it[startTime] = startTimeVal
                it[endTime] = endTimeVal
                it[name] = nameVal
                it[country] = countryVal
                it[timezone] = timezoneVal
            }
        }
    }

    /**
     * Updates the start time of a location in the database
     * @param uuid The unique id of the location history to update
     * @param startTimeVal The new startTime describing when the user visited the location
     */
    fun updateStartTime(uuid: Int, startTimeVal: DateTime){
        connect();
        transaction{
            History.update({History.uuid.eq(uuid)}){
                it[startTime] = startTimeVal
            }
        }
    }

    /**
     * Updates the end time of a location in the database
     * @param uuid The unique id of the location history to update
     * @param endTimeVal The new startTime describing when the user left the location
     */
    fun updateEndTime(uuid: Int, endTimeVal: DateTime){
        connect();
        transaction{
            History.update({History.uuid.eq(uuid)}){
                it[endTime] = endTimeVal
            }
        }
    }

    /**
     * Updates the place name of a location in the database
     * @param uuid The unique id of the location history to update
     * @param nameVal The new name for the location
     */
    fun updateName(uuid: Int, nameVal: String){
        connect();
        transaction{
            History.update({History.uuid.eq(uuid)}){
                it[name] = nameVal
            }
        }
    }

    /**
     * Updates the country name of a location in the database
     * @param uuid The unique id of the location history to update
     * @param countryVal The new name for the location's country
     */
    fun updateCountry(uuid: Int, countryVal: String){
        connect();
        transaction{
            History.update({History.uuid.eq(uuid)}){
                it[country] = countryVal
            }
        }
    }

    /**
     * Updates the timezone offset of a location in the database
     * @param uuid The unique id of the location history to update
     * @param timezoneVal The new timezone offset for the location
     */
    fun updateTimezone(uuid: Int, timezoneVal: Int){
        connect();
        transaction{
            History.update({History.uuid.eq(uuid)}){
                it[timezone] = timezoneVal
            }
        }
    }

    /**
     * Adds a blog post to a location in the database
     * @param uuid The unique id of the location history to update
     * @param urlVal The URL for the blog post
     * @param nameVal The name of the blog post
     */
    fun addBlogPost(uuid: Int, urlVal: String, nameVal: String) {
        connect()
        transaction{
            History.update({History.uuid.eq(uuid)}){
                it[blogPostUrl] = urlVal
                it[blogPostName] = nameVal
            }
        }
    }

    /**
     * Adds a map to a location in the database
     * @param uuid The unique id of the location history to update
     * @param urlVal The URL for the map image
     */
    fun addMap(uuid: Int, urlVal: String) {
        connect()
        transaction{
            History.update({History.uuid.eq(uuid)}){
                it[mapUrl] = urlVal
            }
        }
    }

    /**
     * A helper method that creates a BlogPost from a name and a url, provided they are both non-null. Null if either
     * was null.
     * @param url The URL pointing to the hosted blog post
     * @param name The display name for this blog post
     * @return A blog post with the desired credentials, or null if either was null
     */
    private fun getBlogPost(url:String?, name:String?): BlogPost? {
        if(name == null || url == null){
            return null;
        } else {
            return BlogPost(url, name)
        }
    }

}