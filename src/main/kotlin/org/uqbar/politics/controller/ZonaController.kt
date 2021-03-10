package org.uqbar.politics.controller

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.databind.module.SimpleModule
import io.swagger.annotations.ApiOperation
import org.springframework.batch.core.Job
import org.springframework.batch.core.JobParameter
import org.springframework.batch.core.JobParameters
import org.springframework.batch.core.launch.JobLauncher
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.server.ResponseStatusException
import org.uqbar.politics.domain.Zona
import org.uqbar.politics.repository.ZonaRepository
import org.uqbar.politics.serializers.ZonaParaGrillaSerializer
import org.uqbar.politics.serializers.ZonaPlanaDTO
import java.io.File
import java.io.FileOutputStream

import java.io.OutputStream




@RestController
@CrossOrigin(origins = ["*"], methods= [RequestMethod.GET, RequestMethod.POST])
class ZonaController {

    @Autowired
    private lateinit var zonaRepository: ZonaRepository

    @Autowired
    lateinit var jobLauncher: JobLauncher

    @Autowired
    lateinit var job: Job


    @GetMapping("/zonas")
    @ApiOperation("Devuelve todas las zonas de votación")
    fun getZonas(): List<ZonaPlanaDTO> = zonaRepository.findAll().map { zona -> ZonaPlanaDTO(zona.id!!, zona.descripcion) }

    @GetMapping("/zonas/{id}")
    @ApiOperation("Muestra la información de una zona de votación con sus candidates")
    fun getZona(@PathVariable id: Long): Zona {
        mapper.registerModule(
            SimpleModule().addSerializer(ZonaParaGrillaSerializer())
        )

        return zonaRepository
            .findById(id)
            .orElseThrow {
                ResponseStatusException(HttpStatus.NOT_FOUND, "La zona con identificador $id no existe")
            }
    }

    @PostMapping("/zonas")
    fun cargarZonas(@RequestParam("file") file: MultipartFile) {
        val tempFile = File("./zonasTemp.csv")
        FileOutputStream(tempFile).use { os -> os.write(file.bytes) }
        jobLauncher.run(job, JobParameters(hashMapOf(Pair("file.input", JobParameter("./zonasTemp.csv")), Pair("time", JobParameter(System.currentTimeMillis())))))
    }

    companion object {
        val mapper: ObjectMapper = ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
            .configure(SerializationFeature.INDENT_OUTPUT, true)
    }

}