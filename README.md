# Travel History
Travel History is a simple RESTful service that manages a history of where someone has been (or will be) on their travels. The
server stores the data for a single Traveller, which can be accessed by multiple Readers. The history includes when and
where the Traveller was/is/will be, as well as optionally linking each location with a map and/or a blog post.

# Installation
1. Create a database (the project is bundled with drivers for [MariaDB](https://mariadb.org/))
2. Run the command stored in the [db_create_script](../blob/master/db_create_script) using your database
3. Copy the (sample.application.properties)[../blob/master/src/main/resources/sample.application.properties) file and call it application.properties
4. Fill in the db credentials in the application.properties file so that they point to your database
5. Create a key for the Traveller to use when they update their travel history and add that to application.properties.
6. Run `gradle war` from the root folder to build a .war file
7. Deploy your war file to a server such as Apache Tomcat.

# API
## Read Access

## Write Access
The Traveller is able to change their travel history using an access key. All write functions require this
authentication key before anything can happen.


