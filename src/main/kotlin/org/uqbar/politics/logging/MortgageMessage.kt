package org.uqbar.politics.logging

import org.apache.logging.log4j.message.Message
import org.codehaus.jettison.json.JSONObject
import java.util.HashMap

class MortgageMessage(private val requestBody: Map<String, Any>) : Message {
    override fun getFormattedMessage(): String {
        val jsonBody = JSONObject(requestBody)
        val jsonToLog = JSONObject(object : HashMap<String?, Any?>() {
            init {
                put(TYPE, "custom")
                put(BODY, jsonBody)
            }
        })
        return jsonToLog.toString()
    }

    override fun getFormat(): String = requestBody.toString()
    override fun getParameters(): Array<Any?> = arrayOfNulls(0)
    override fun getThrowable(): Throwable? = null

    companion object {
        private const val TYPE = "type"
        private const val BODY = "body"
    }
}