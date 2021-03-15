package org.uqbar.politics

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.collection.IsEmptyCollection
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.batch.core.explore.JobExplorer
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.mock.web.MockMultipartFile
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.transaction.support.TransactionTemplate
import org.uqbar.politics.domain.Zona
import org.uqbar.politics.repository.ZonaRepository

data class ZonaGrillaDTO(var id: Long, var descripcion: String, var candidates: List<CandidateGrillaDTO>)
data class CandidateGrillaDTO(var id: Long, var partido: String, var nombre: String, var votos: Int)

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("Dado un controller de zonas")
class ZonaControllerTest {
    private val mapper = jacksonObjectMapper()

    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var repoZonas: ZonaRepository

    @Autowired
    lateinit var jobExplorer: JobExplorer

    @Autowired
    lateinit var transactionTemplate: TransactionTemplate

    @Test
    @DisplayName("las zonas solo traen los datos de primer nivel")
    fun todasLasZonas() {
        val responseEntity = mockMvc.perform(MockMvcRequestBuilders.get("/zonas")).andReturn().response
        val zonas = mapper.readValue<List<Zona>>(responseEntity.contentAsString)
        assertEquals(200, responseEntity.status)
        assertEquals(2, zonas.size)
        // los zonas no traen candidatos
        assertThat(zonas.first().candidates, IsEmptyCollection())
    }

    @Test
    @DisplayName("al mandarle un csv a /zonas, se crean las zonas del csv")
    @DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
    fun crearZonas() {
        val zonasCsv = MockMultipartFile("file", "zonas.csv", "text/plain", "LOMA HERMOSA\nSAN MARTIN".toByteArray())
        mockMvc.perform(MockMvcRequestBuilders.multipart("/zonas").file(zonasCsv)).andExpect(status().`is`(200))
        val nombresDeZonasEsperadas = listOf("LOMA HERMOSA", "SAN MARTIN")
        val nombresDeZonasActuales = repoZonas.findAllByOrderByDescripcionAsc().map { it.descripcion }
        assertTrue(nombresDeZonasActuales.containsAll(nombresDeZonasEsperadas))
    }

    @Test
    @DisplayName("al traer el dato de una zona trae las personas candidatas también")
    fun zonaConCandidates() {
        val zonas = repoZonas.findAll().toList()
        assert(zonas.isNotEmpty(), { "No hay zonas cargadas en el sistema" })
        val ID_ZONA = zonas.first().id
        val responseEntity = mockMvc.perform(MockMvcRequestBuilders.get("/zonas/$ID_ZONA")).andReturn().response
        assertEquals(200, responseEntity.status)
        val zona = mapper.readValue<ZonaGrillaDTO>(responseEntity.contentAsString)
        assert(zona.candidates.isNotEmpty()) { "La zona debería tener candidates" }
    }

    @Test
    @DisplayName("no podemos traer información de una zona inexistente")
    fun profesorInexistente() {
        val responseEntity = mockMvc.perform(MockMvcRequestBuilders.get("/profesores/100")).andReturn().response
        assertEquals(404, responseEntity.status)
    }

}