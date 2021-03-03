package org.uqbar.politics.config

import org.keycloak.common.ClientConnection
import org.keycloak.models.KeycloakSession
import javax.servlet.http.HttpServletRequest
import java.io.UnsupportedEncodingException
import javax.servlet.FilterChain
import javax.servlet.ServletResponse
import javax.servlet.ServletRequest
import org.keycloak.services.filters.AbstractRequestFilter
import java.lang.Exception
import javax.servlet.Filter

class EmbeddedKeycloakRequestFilter : AbstractRequestFilter(), Filter {
    @Throws(UnsupportedEncodingException::class)
    override fun doFilter(servletRequest: ServletRequest, servletResponse: ServletResponse?, filterChain: FilterChain) {
        servletRequest.characterEncoding = "UTF-8"
        val clientConnection = createConnection(servletRequest as HttpServletRequest)
        filter(clientConnection) { session: KeycloakSession? ->
            try {
                filterChain.doFilter(servletRequest, servletResponse)
            } catch (e: Exception) {
                throw RuntimeException(e)
            }
        }
    }

    private fun createConnection(request: HttpServletRequest): ClientConnection {
        return object : ClientConnection {
            override fun getRemoteAddr(): String {
                return request.remoteAddr
            }

            override fun getRemoteHost(): String {
                return request.remoteHost
            }

            override fun getRemotePort(): Int {
                return request.remotePort
            }

            override fun getLocalAddr(): String {
                return request.localAddr
            }

            override fun getLocalPort(): Int {
                return request.localPort
            }
        }
    }
}