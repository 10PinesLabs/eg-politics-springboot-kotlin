package org.uqbar.politics.config

import org.jboss.resteasy.core.Dispatcher
import org.jboss.resteasy.spi.ResteasyProviderFactory

import org.keycloak.common.util.ResteasyProvider


class Resteasy3Provider : ResteasyProvider {
    override fun <R> getContextData(type: Class<R>): R {
        ResteasyProviderFactory.getInstance()
        return ResteasyProviderFactory.getContextData(type)
    }

    override fun pushDefaultContextObject(type: Class<*>?, instance: Any) {
        ResteasyProviderFactory.getInstance()
        ResteasyProviderFactory.getContextData(Dispatcher::class.java)
            .defaultContextObjects[type] = instance
    }

    @SuppressWarnings("rawtypes")
    override fun pushContext(type: Class<*>?, instance: Any) {
        ResteasyProviderFactory.getInstance()
//        ResteasyProviderFactory.pushContext(type, instance)
    }

    override fun clearContextData() {
        ResteasyProviderFactory.getInstance()
        ResteasyProviderFactory.clearContextData()
    }
}
