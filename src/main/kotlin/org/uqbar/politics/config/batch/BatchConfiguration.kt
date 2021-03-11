package org.uqbar.politics.config.batch

import org.springframework.batch.core.Job
import org.springframework.batch.core.Step
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory
import org.springframework.batch.core.configuration.annotation.StepScope
import org.springframework.batch.core.launch.support.RunIdIncrementer
import org.springframework.batch.item.data.RepositoryItemWriter
import org.springframework.batch.item.data.builder.RepositoryItemWriterBuilder
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider
import org.springframework.batch.item.database.JdbcBatchItemWriter
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder
import org.springframework.batch.item.file.FlatFileItemReader
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.ClassPathResource
import org.springframework.core.io.PathResource
import org.uqbar.politics.domain.Zona
import org.uqbar.politics.repository.ZonaRepository
import javax.sql.DataSource


@Configuration
@EnableBatchProcessing
class BatchConfiguration {

    @Autowired
    lateinit var jobBuilderFactory: JobBuilderFactory;

    @Autowired
    lateinit var stepBuilderFactory: StepBuilderFactory;

    @Autowired
    lateinit var zonaRepository: ZonaRepository

    @StepScope
    @Bean
    fun zonasReader(@Value("#{jobParameters['file.input']}") fileInput: String): FlatFileItemReader<Zona> {
        return FlatFileItemReaderBuilder<Zona>().name("zonasItemReader")
            .resource(PathResource(fileInput))
            .delimited()
            .names("descripcion")
            .fieldSetMapper(object : BeanWrapperFieldSetMapper<Zona>() {
                init {
                    setTargetType(Zona::class.java)
                }
            })
            .build()
    }

//    @Bean
//    fun zonasWriter(dataSource: DataSource): JdbcBatchItemWriter<Zona> {
//        return JdbcBatchItemWriterBuilder<Zona>()
//            .itemSqlParameterSourceProvider(BeanPropertyItemSqlParameterSourceProvider())
//            .sql("INSERT INTO zona (descripcion) VALUES (:descripcion)")
//            .dataSource(dataSource)
//            .build()
//    }

    @Bean
    fun zonasRepositoryWriter(): RepositoryItemWriter<Zona> {
        return RepositoryItemWriterBuilder<Zona>()
            .repository(zonaRepository)
            .methodName("save")
            .build()
    }

    @Bean
    fun importZonasJob(listener: JobCompletionNotificationListener, cargarZonas: Step): Job {
        return jobBuilderFactory["Job Importación Zonas"]
            .incrementer(RunIdIncrementer())
            .listener(listener)
            .flow(cargarZonas)
            .end()
            .build()
    }

    @Bean
    fun cargarZonas(reader: FlatFileItemReader<Zona>, writer: RepositoryItemWriter<Zona>): Step {
        return stepBuilderFactory["Cargar Zonas"]
            .chunk<Zona, Zona>(10)
            .reader(reader)
            .processor(processor())
            .writer(writer)
            .build()
    }

    @Bean
    fun processor(): ZonaItemProcessor {
        return ZonaItemProcessor()
    }
}