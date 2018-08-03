package com.wabradshaw.travelhistory.history

/**
 * A BlogPost contains the information pointing to the blog post about a place.
 * @property url The URL where the blog post is hosted.
 * @property name The name for the blog post (not necessarily it's actual name, the name that should appear in the
 *                front end.
 */
data class BlogPost (val url: String,
                     val name: String)