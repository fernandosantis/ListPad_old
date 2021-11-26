package com.santis.minharua.data.model

import androidx.room.*
import com.santis.minharua.R
import java.io.Serializable

@Entity(
    tableName = "incidentes",
    foreignKeys = [
        ForeignKey(
            entity = Categoria::class,
            parentColumns = arrayOf("cat_id"),
            childColumns = arrayOf("id_cat"),
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Incidente(
    @PrimaryKey(autoGenerate = true) val inc_id: Int? = null,
    @ColumnInfo(name = "inc_titulo") var tituloInc: String?,
    @ColumnInfo(name = "inc_descricao") var descricaoInc: String?,
    @ColumnInfo(name = "inc_imagem") var imagemInc: Int = R.drawable.ic_city,
    @ColumnInfo(name = "inc_cep") var cepInc: String,
    @ColumnInfo(name = "id_cat") var catId: Int
) : Serializable
