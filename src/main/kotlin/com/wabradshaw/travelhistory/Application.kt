package com.wabradshaw.travelhistory

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.builder.SpringApplicationBuilder
import org.springframework.boot.web.support.SpringBootServletInitializer

/**
 * A top level Application class that allows SpringBoot to initialise this as a servlet.
 */
@SpringBootApplication
class Application : SpringBootServletInitializer() {

    override fun configure(application: SpringApplicationBuilder): SpringApplicationBuilder {
        return application.sources(Application::class.java)
    }
}

/**
 * Main function which creates the new Application and gives it any command line arguments.
 */
fun main(args: Array<String>){
    SpringApplication.run(Application::class.java, *args)
}