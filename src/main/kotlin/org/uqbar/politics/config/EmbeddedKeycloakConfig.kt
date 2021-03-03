package org.uqbar.politics.config

import javax.naming.spi.NamingManager
import org.springframework.boot.web.servlet.FilterRegistrationBean
import org.jboss.resteasy.plugins.server.servlet.ResteasyContextParameters
import org.jboss.resteasy.plugins.server.servlet.HttpServlet30Dispatcher
import org.springframework.boot.web.servlet.ServletRegistrationBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.lang.Exception
import java.util.*
import javax.naming.*
import javax.naming.spi.InitialContextFactory
import javax.naming.spi.InitialContextFactoryBuilder
import javax.servlet.Filter
import javax.sql.DataSource

@Configuration
class EmbeddedKeycloakConfig {
    @Bean
    @Throws(Exception::class)
    fun keycloakJaxRsApplication(keycloakServerProperties: KeycloakServerProperties, dataSource: DataSource): ServletRegistrationBean<*> {
        mockJndiEnvironment(dataSource)
        EmbeddedKeycloakApplication.keycloakServerProperties = keycloakServerProperties
        val servlet: ServletRegistrationBean<*> = ServletRegistrationBean(HttpServlet30Dispatcher())
        servlet.addInitParameter("javax.ws.rs.Application", EmbeddedKeycloakApplication::class.java.name)
        servlet.addInitParameter(ResteasyContextParameters.RESTEASY_SERVLET_MAPPING_PREFIX, keycloakServerProperties.contextPath)
        servlet.addInitParameter(ResteasyContextParameters.RESTEASY_USE_CONTAINER_FORM_PARAMS, "true")
        servlet.addUrlMappings(keycloakServerProperties.contextPath + "/*")
        servlet.setLoadOnStartup(1)
        servlet.isAsyncSupported = true
        return servlet
    }

    @Bean
    fun keycloakSessionManagement(keycloakServerProperties: KeycloakServerProperties): FilterRegistrationBean<*> {
        val filter: FilterRegistrationBean<*> = FilterRegistrationBean<Filter>()
        filter.setName("Keycloak Session Management")
        filter.filter = EmbeddedKeycloakRequestFilter()
        filter.addUrlPatterns(keycloakServerProperties.contextPath + "/*")
        return filter
    }

    @Throws(NamingException::class)
    private fun mockJndiEnvironment(dataSource: DataSource) {
        NamingManager.setInitialContextFactoryBuilder { env: Hashtable<*, *>? ->
            InitialContextFactory { environment: Hashtable<*, *>? ->
                object : InitialContext() {
                    override fun lookup(name: Name): Any? = lookup(name.toString())

                    override fun lookup(name: String): Any? = if ("spring/datasource" == name) dataSource else null

                    override fun getNameParser(name: String): NameParser = NameParser { n: String? -> CompositeName(n) }

                    override fun close() {}
                }
            }
        }
    }
}