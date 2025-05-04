package de.eventsourcingbook.cart.common.support

import java.sql.Connection
import java.sql.DriverManager
import java.time.Duration
import org.awaitility.Awaitility
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext
abstract class BaseIntegrationTest {

  companion object {
    private const val DATABASE_URL = "jdbc:postgresql://localhost:5432/test"
    private const val DATABASE_USERNAME = "postgres"
    private const val DATABASE_PASSWORD = "postgres"

    @JvmStatic
    @DynamicPropertySource
    fun properties(registry: DynamicPropertyRegistry) {
      registry.add("spring.datasource.url") { DATABASE_URL }
      registry.add("spring.flyway.url") { DATABASE_URL }
      registry.add("spring.datasource.username") { DATABASE_USERNAME }
      registry.add("spring.datasource.password") { DATABASE_PASSWORD }
      registry.add("spring.flyway.user") { DATABASE_USERNAME }
      registry.add("spring.flyway.password") { DATABASE_PASSWORD }
    }

    fun getDatabaseConnection(): Connection {
      return DriverManager.getConnection(DATABASE_URL, DATABASE_USERNAME, DATABASE_PASSWORD)
    }
  }
}

fun awaitUntilAssserted(fn: () -> Unit) {
  Awaitility.await().pollInSameThread().atMost(Duration.ofSeconds(15)).untilAsserted { fn() }
}
