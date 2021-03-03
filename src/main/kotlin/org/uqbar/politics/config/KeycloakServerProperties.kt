package org.uqbar.politics.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "keycloak.server")
class KeycloakServerProperties {
    var contextPath = "/auth"
    var realmImportFile = "baeldung-realm.json"
    var adminUser = AdminUser()

    // getters and setters
    class AdminUser {
        var username = "admin"
        var password = "admin" // getters and setters
    }
}
