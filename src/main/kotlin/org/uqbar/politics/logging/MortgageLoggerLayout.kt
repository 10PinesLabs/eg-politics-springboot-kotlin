package org.uqbar.politics.logging

import org.apache.logging.log4j.core.LogEvent
import org.apache.logging.log4j.core.config.plugins.Plugin

import org.apache.logging.log4j.core.config.plugins.PluginAttribute

import org.apache.logging.log4j.core.config.plugins.PluginFactory

import org.apache.logging.log4j.core.layout.AbstractStringLayout
import java.nio.charset.Charset


@Plugin(name = "CustomLayout", category = "Core", elementType = "layout", printObject = true)
class MortgageLoggerLayout protected constructor(charset: Charset?) : AbstractStringLayout(charset) {
    override fun toSerializable(logEvent: LogEvent): String {
        return logEvent.message.formattedMessage + DEFAULT_EOL
    }

    companion object {
        private const val DEFAULT_EOL = "\r\n"
        @PluginFactory
        fun createLayout(@PluginAttribute(value = "charset", defaultString = "UTF-8") charset: Charset?): MortgageLoggerLayout {
            return MortgageLoggerLayout(charset)
        }
    }
}