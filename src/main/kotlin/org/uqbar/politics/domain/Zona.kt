package org.uqbar.politics.domain

import com.fasterxml.jackson.databind.annotation.JsonSerialize
import org.uqbar.politics.domain.exceptions.UserException
import org.uqbar.politics.serializers.ZonaParaGrillaSerializer
import javax.persistence.*
import kotlin.collections.*

// Definir como default este serializador es una opción
// Otra es utilizar la variante del mapper por cada método del endpoint
// Y finalmente podemos construir nuestras sealed classes con DTOs
@JsonSerialize(using=ZonaParaGrillaSerializer::class)
@Entity
class Zona(@Column(length=150) var descripcion: String = "") {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    @OneToMany(fetch=FetchType.LAZY)
    var candidates: MutableSet<Candidate> = mutableSetOf()

    fun validar() {
        if (descripcion.trim() == "") {
            throw UserException("Debe ingresar descripcion")
        }
        if (candidates.isEmpty()) {
            throw UserException("Debe haber al menos un candidato en la zona")
        }
    }

    override fun toString(): String = descripcion
}