package org.uqbar.politics.config.batch

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.batch.core.BatchStatus
import org.springframework.batch.core.JobExecution
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.batch.core.listener.JobExecutionListenerSupport
import org.springframework.stereotype.Component
import org.uqbar.politics.domain.Zona
import java.sql.ResultSet
import java.util.function.Consumer

@Component
class JobCompletionNotificationListener @Autowired constructor(private val jdbcTemplate: JdbcTemplate) : JobExecutionListenerSupport() {
    override fun afterJob(jobExecution: JobExecution) {
        if (jobExecution.status === BatchStatus.COMPLETED) {
            LOGGER.info("!!! JOB FINISHED! Time to verify the results")
            val query = "SELECT descripcion FROM zona"

            jdbcTemplate.query<Any>(query) { rs: ResultSet, row: Int -> Zona(rs.getString(1)) }
                .forEach(Consumer { zona: Any? -> LOGGER.info("Found < {} > in the database.", zona) })
        }
    }

    companion object {
        private val LOGGER: Logger = LoggerFactory.getLogger(JobCompletionNotificationListener::class.java)
    }
}