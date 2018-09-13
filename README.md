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
## Response Objects
API calls designed to access information from the travel history return json objects which can be in one of the following forms.
 
### LocationHistory
A LocationHistory object contains all of the information tied to a single visit in a particular location.

| Data | Nullable | Type | Description | 
| ---------| -------- | --- | ----------- |
| uuid | No | Int | The unique id for this particular location. Primarily used when updating a location. |
| startTime | No | DateTime | The point when the Traveller started visiting this location. |
| endTime | Yes | DateTime | The point when the Traveller left this location. Null if the Traveller hasn't left and hasn't decided when they are leaving. |
| name | No | String | The name of the location. Typically the name of a city or region. |
| country | No | String | The name of the country the location is in. |
| timezone | No | Int | The timezone offset from UTC. |
| blog | Yes | BlogPost | The BlogPost object describing the Traveller's experience in this location. Null if the Traveller hasn't written one yet. |
| mapUrl | Yes | String | The URL for an image of a map of the location. Null if the Traveller hasn't created one yet. |

### BlogPost
A BlogPost object contains information about an entry in the Traveller's blog.

| Data | Nullable | Type | Description | 
| ---------| -------- | --- | ----------- |
| url | No | String | The URL containing the blog post. |
| name | No | String | The human readable name for the blog post. |

## Read Access

### Get Server Version

Gets the version of the deployed Travel History server. The value is supplied in application.properties when the
system is deployed. This value should be updated whenever the server is updated and serves as an easy way to confirm
an update.

To change the version number, it needs to be edited in the application.properties file under src/main/resources.

**Example:** http://54.191.146.40:8080/travel-history/version

**Type:** GET

**URL :** http://54.191.146.40:8080/vcs/version

**Data:** None

**Response:** The plain text version number

### Get Complete Travel History

Gets the list of all locations the Traveller has visited, intends to visit, or will visit.

**Example:** http://54.191.146.40:8080/travel-history/history

**Type:** GET

**URL :** http://54.191.146.40:8080/travel-history/history

**Data:** None

**Response:** A json list of [LocationHistory](#locationhistory) objects. 

### Get Current Location

Gets the location the Traveller is currently. No content if the Traveller hasn't supplied that information. 

If the Traveller has claimed to be in multiple locations at the current time, the most recent one will be returned. 

**Example:** http://54.191.146.40:8080/travel-history/history/current

**Type:** GET

**URL :** http://54.191.146.40:8080/travel-history/history/current

**Data:** None

**Response:** A json [LocationHistory](#locationhistory) object, or empty if no location has been supplied.

### Get Next Location

Gets the location the Traveller is planning to be in next. No content if the Traveller hasn't supplied that information.  

**Example:** http://54.191.146.40:8080/travel-history/history/next

**Type:** GET

**URL :** http://54.191.146.40:8080/travel-history/history/next

**Data:** None

**Response:** A json [LocationHistory](#locationhistory) object, or empty if no location has been supplied.
 
### Get Location at a Specific Time

Gets the location the Traveller is/was/will be in at a particular time. Null if the Traveller hasn't supplied that 
information. 

If the Traveller has claimed to be in multiple locations at the specified time, the oldest one will be returned. 

**Example:** http://54.191.146.40:8080/travel-history/history/at?date=2018-09-13T11:20:00.000%2b01:00

**Type:** GET

**URL :** http://54.191.146.40:8080/travel-history/history/at

**Data:** 

| Argument | Optional | Description | 
| ---------| -------- | ----------- |
| date | false | The timestamp for the request. In encoded IS08601 format (yyyy-MM-dd'T'HH:mm:ss.SSSZ). |

**Response:** A json [LocationHistory](#locationhistory) object, or empty if no location has been supplied at that time.

## Write Access
The Traveller is able to change their travel history using an access key. All write functions require this
authentication key before anything can happen.


