package org.uqbar.politics

import org.springframework.batch.core.Job
import org.springframework.batch.core.JobParameter
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.batch.core.JobParameters
import org.springframework.batch.core.JobParametersBuilder
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.batch.core.launch.JobLauncher
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.stereotype.Component
import org.uqbar.politics.config.batch.JobCompletionNotificationListener
import java.lang.Exception

@SpringBootApplication
@EnableBatchProcessing
class PoliticsApplication

fun main(args: Array<String>) {
    runApplication<PoliticsApplication>(*args)
}

@Component
@EnableScheduling
internal class ScheduledTasks {
    @Autowired
    lateinit var jobLauncher: JobLauncher

    @Autowired
    lateinit var job: Job

    @Scheduled(fixedDelay = 10000, initialDelay = 10000)
    @Throws(Exception::class)
    fun runJob() {
        jobLauncher.run(job, JobParameters(hashMapOf(Pair("file.input", JobParameter("src/main/resources/zonas.csv")), Pair("time", JobParameter(System.currentTimeMillis())))))
    }
}