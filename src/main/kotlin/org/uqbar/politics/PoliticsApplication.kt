package org.uqbar.politics

import org.slf4j.LoggerFactory
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.liquibase.LiquibaseAutoConfiguration
import org.springframework.boot.autoconfigure.web.ServerProperties
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.ApplicationListener
import org.springframework.context.annotation.Bean
import org.uqbar.politics.config.KeycloakServerProperties
import java.lang.Exception

@SpringBootApplication(exclude = [LiquibaseAutoConfiguration::class])
@EnableConfigurationProperties(KeycloakServerProperties::class)
class PoliticsApplication {
    @Bean
    fun onApplicationReadyEventListener(serverProperties: ServerProperties, keycloakServerProperties: KeycloakServerProperties): ApplicationListener<ApplicationReadyEvent> {
        return ApplicationListener { evt: ApplicationReadyEvent? ->
            val port = serverProperties.port
            val keycloakContextPath = keycloakServerProperties.contextPath
            LOG.info("Embedded Keycloak started: http://localhost:{}{} to use keycloak", port, keycloakContextPath)
        }
    }

    companion object {
        private val LOG = LoggerFactory.getLogger(PoliticsApplication::class.java)
        @Throws(Exception::class)
        @JvmStatic
        fun main(args: Array<String>) {
            SpringApplication.run(PoliticsApplication::class.java, *args)
        }
    }
}