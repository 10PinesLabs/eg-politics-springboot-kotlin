package org.uqbar.politics.config.batch

import org.springframework.batch.core.Job
import org.springframework.batch.core.Step
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper
import org.springframework.core.io.ClassPathResource
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder
import org.springframework.batch.item.file.FlatFileItemReader
import org.springframework.context.annotation.Bean
import org.uqbar.politics.domain.Zona
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder
import org.springframework.batch.item.database.JdbcBatchItemWriter
import javax.sql.DataSource
import org.springframework.batch.core.launch.support.RunIdIncrementer
import org.springframework.scheduling.annotation.Scheduled

@Configuration
@EnableBatchProcessing
class BatchConfiguration {

    @Autowired
    lateinit var jobBuilderFactory: JobBuilderFactory;

    @Autowired
    lateinit var stepBuilderFactory: StepBuilderFactory;

    @Value("\${file.input}")
    lateinit var fileInput: String;

    @Bean
    fun reader(): FlatFileItemReader<Zona> {
        return FlatFileItemReaderBuilder<Zona>().name("zonasItemReader")
            .resource(ClassPathResource(fileInput))
            .delimited()
            .names("descripcion")
            .fieldSetMapper(object : BeanWrapperFieldSetMapper<Zona>() {
                init {
                    setTargetType(Zona::class.java)
                }
            })
            .build()
    }

    @Bean
    fun writer(dataSource: DataSource): JdbcBatchItemWriter<Zona> {
        return JdbcBatchItemWriterBuilder<Zona>()
            .itemSqlParameterSourceProvider(BeanPropertyItemSqlParameterSourceProvider())
            .sql("INSERT INTO zona (descripcion) VALUES (:descripcion)")
            .dataSource(dataSource)
            .build()
    }

    @Bean
    fun importUserJob(listener: JobCompletionNotificationListener, step1: Step): Job {
        return jobBuilderFactory["importUserJob"]
            .incrementer(RunIdIncrementer())
            .listener(listener)
            .flow(step1)
            .end()
            .build()
    }

    @Bean
    fun step1(writer: JdbcBatchItemWriter<Zona>): Step {
        return stepBuilderFactory["step1"]
            .chunk<Zona, Zona>(10)
            .reader(reader())
            .processor(processor())
            .writer(writer)
            .build()
    }

    @Bean
    fun processor(): ZonaItemProcessor {
        return ZonaItemProcessor()
    }
}