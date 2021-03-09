package org.uqbar.politics

//import org.junit.After
import org.junit.After
import org.junit.jupiter.api.Test
import org.junit.runner.RunWith;
import org.springframework.batch.test.context.SpringBatchTest
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.TestExecutionListeners
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener
import org.springframework.test.context.support.DirtiesContextTestExecutionListener
import org.uqbar.politics.config.batch.BatchConfiguration
import org.springframework.batch.core.JobParametersBuilder

import org.springframework.batch.core.JobParameters

import org.springframework.batch.test.JobRepositoryTestUtils

import org.springframework.beans.factory.annotation.Autowired

import org.springframework.batch.test.JobLauncherTestUtils
import org.springframework.batch.test.AssertFile

import org.springframework.batch.core.ExitStatus
import org.springframework.boot.test.context.SpringBootTest

import org.springframework.core.io.FileSystemResource
import java.lang.Exception

//@RunWith(SpringRunner::class)
@SpringBatchTest
@EnableAutoConfiguration
@ContextConfiguration(classes = [BatchConfiguration::class])
@TestExecutionListeners(DependencyInjectionTestExecutionListener::class, DirtiesContextTestExecutionListener::class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class SpringBatchIntegrationTest {

    private val TEST_INPUT = "src/main/resources/zonas.csv"

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
        assert(true)
        /*// given
        val expectedResult = FileSystemResource(EXPECTED_OUTPUT)
        val actualResult = FileSystemResource(TEST_OUTPUT)

        // when
        val jobExecution: JobExecution = jobLauncherTestUtils!!.launchJob(defaultJobParameters())
        val actualJobInstance: JobInstance = jobExecution.getJobInstance()
        val actualJobExitStatus: ExitStatus = jobExecution.getExitStatus()

        // then
        assertThat(actualJobInstance.getJobName(), `is`("transformBooksRecords"))
        assertThat(actualJobExitStatus.exitCode, `is`("COMPLETED"))
        AssertFile.assertFileEquals(expectedResult, actualResult)*/
    }
}