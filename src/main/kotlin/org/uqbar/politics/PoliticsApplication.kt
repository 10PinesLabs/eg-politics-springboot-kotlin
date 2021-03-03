package org.uqbar.politics

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.liquibase.LiquibaseAutoConfiguration
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.uqbar.politics.config.KeycloakServerProperties

import org.springframework.boot.autoconfigure.web.ServerProperties

import org.springframework.boot.context.event.ApplicationReadyEvent

import org.springframework.context.ApplicationListener
import org.springframework.context.annotation.Bean


@SpringBootApplication(exclude = [LiquibaseAutoConfiguration::class])
@EnableConfigurationProperties(KeycloakServerProperties::class)
class PoliticsApplication {
    private val LOG: Logger = LoggerFactory.getLogger(PoliticsApplication::class.java)

    @Bean
    fun onApplicationReadyEventListener(
        serverProperties: ServerProperties, keycloakServerProperties: KeycloakServerProperties
    ): ApplicationListener<ApplicationReadyEvent?>? {
        return ApplicationListener { evt: ApplicationReadyEvent? ->
            val port = serverProperties.port
            val keycloakContextPath = keycloakServerProperties.contextPath
            LOG.info(
                "Embedded Keycloak started: http://localhost:{}{} to use keycloak",
                port, keycloakContextPath
            )
        }
    }
}

fun main(args: Array<String>) {
    runApplication<PoliticsApplication>(*args)
}