package com.wabradshaw.travelhistory.history

import com.wabradshaw.travelhistory.database.DatabaseConfiguration
import com.wabradshaw.travelhistory.database.History
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.update
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