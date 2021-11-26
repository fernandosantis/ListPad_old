package com.santis.minharua.data.model

import androidx.room.Embedded
import androidx.room.Relation
import java.io.Serializable

class CategoriaIncidentes(
    @Embedded val categoria: Categoria,
    @Relation(
        parentColumn = "cat_id",
        entityColumn = "id_cat"
    )
    val incidentes: List<Incidente>
) : Serializable
