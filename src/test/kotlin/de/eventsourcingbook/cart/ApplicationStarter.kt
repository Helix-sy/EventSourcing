package de.eventsourcingbook.cart

import org.springframework.boot.SpringApplication
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import java.sql.Connection
import java.sql.DriverManager

object ApplicationStarter {
  @JvmStatic
  fun main(args: Array<String>) {
    SpringApplication.from(SpringApp::main).run(*args)
  }
}

@TestConfiguration(proxyBeanMethods = false)
internal class ContainerConfiguration {

  @Bean
  fun databaseConnection(): Connection {
    val url = "jdbc:postgresql://localhost:5432/test"
    val username = "postgres"
    val password = "postgres"
    return DriverManager.getConnection(url, username, password)
  }
}
