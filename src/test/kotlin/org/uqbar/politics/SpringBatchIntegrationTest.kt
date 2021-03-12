package org.uqbar.politics

import org.junit.After
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.batch.core.*
import org.springframework.batch.test.JobLauncherTestUtils
import org.springframework.batch.test.JobRepositoryTestUtils
import org.springframework.batch.test.context.SpringBatchTest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.TestExecutionListeners
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener
import org.springframework.test.context.support.DirtiesContextTestExecutionListener
import org.uqbar.politics.config.batch.BatchConfiguration
import org.uqbar.politics.config.batch.JobCompletionNotificationListener
import org.uqbar.politics.repository.ZonaRepository

@SpringBatchTest
@EnableAutoConfiguration
@ContextConfiguration(classes = [BatchConfiguration::class, JobCompletionNotificationListener::class])
@TestExecutionListeners(DependencyInjectionTestExecutionListener::class, DirtiesContextTestExecutionListener::class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class SpringBatchIntegrationTest {

    private val TEST_INPUT = "./src/main/resources/zonas.csv"

    @Autowired
    private val zonaRepository: ZonaRepository? = null

    @Autowired
    private val jobLauncherTestUtils: JobLauncherTestUtils? = null

    @Autowired
    private val jobRepositoryTestUtils: JobRepositoryTestUtils? = null

    @After
    fun cleanUp() {
        jobRepositoryTestUtils!!.removeJobExecutions()
    }

    private fun defaultJobParameters(): JobParameters {
        val paramsBuilder = JobParametersBuilder()
        paramsBuilder.addString("file.input", TEST_INPUT)
        return paramsBuilder.toJobParameters()
    }

    @Test
    @Throws(Exception::class)
    fun givenReferenceOutput_whenJobExecuted_thenSuccess() {
        // when
        val jobExecution: JobExecution = jobLauncherTestUtils!!.launchJob(defaultJobParameters())
        val actualJobInstance: JobInstance = jobExecution.getJobInstance()
        val actualJobExitStatus: ExitStatus = jobExecution.getExitStatus()

        // then
        assertEquals("Job Importación Zonas", actualJobInstance.getJobName())
        assertEquals("COMPLETED", actualJobExitStatus.exitCode)

        val nombresDeZonasEsperadas = listOf("LOMA HERMOSA", "SAN MARTIN", "VILLA BALLESTER", "VILLA DEL PARQUE")
        val nombresDeZonasActuales = zonaRepository!!.findAllByOrderByDescripcionAsc().map { it.descripcion }
        assertEquals(nombresDeZonasEsperadas, nombresDeZonasActuales)
    }
}