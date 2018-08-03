package com.wabradshaw.travelhistory.history

import com.wabradshaw.travelhistory.database.DatabaseConfiguration
import com.wabradshaw.travelhistory.database.History
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.SchemaUtils
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
            SchemaUtils.create(History)
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