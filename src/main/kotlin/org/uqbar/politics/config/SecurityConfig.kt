package org.uqbar.politics.config

import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter

@EnableWebSecurity
class SecurityConfig : WebSecurityConfigurerAdapter() {

    override fun configure(http: HttpSecurity) {
        http.csrf().disable(); // no queremos dejar desactivado csrf

        http.authorizeRequests()
            .antMatchers("/**")
            .permitAll() // acá tengo permitAll porque keycloak se encarga de validar el acceso a la vista de admin
                // después habría que cambiarlo para permitir paginas según roles
            .and()
            .headers {
                it.frameOptions {
                    it.sameOrigin()
                }
            }

    }
}