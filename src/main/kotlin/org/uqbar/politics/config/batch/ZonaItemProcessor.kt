package org.uqbar.politics.config.batch

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.batch.item.ItemProcessor
import org.uqbar.politics.domain.Zona

class ZonaItemProcessor : ItemProcessor<Zona, Zona> {

    @Throws(Exception::class)
    override fun process(zona: Zona): Zona {
        val descripcion: String = zona.descripcion.toUpperCase()
        val transformedZona = Zona(descripcion)
        LOGGER.info("Converting ( {} ) into ( {} )", zona, transformedZona)
        return transformedZona
    }

    companion object {
        private val LOGGER: Logger = LoggerFactory.getLogger(ZonaItemProcessor::class.java)
    }
}