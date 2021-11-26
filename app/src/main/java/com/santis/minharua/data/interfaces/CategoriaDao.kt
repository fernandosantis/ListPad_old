package com.santis.minharua.data.interfaces

import androidx.room.*
import com.santis.minharua.data.model.Categoria

@Dao
interface CategoriaDao {

    @Update
    fun atualizarCategoria(categoria: Categoria)

    @Query("SELECT * FROM categorias WHERE cat_id = :id LIMIT 1")
    fun buscaCatPorId(id: Int): Categoria

    @Query("SELECT * FROM categorias WHERE cat_nome LIKE :nome LIMIT 1")
    fun buscaCatPorNome(nome: String): Categoria

    @Delete
    fun excluirCategoria(categoria: Categoria)

    @Insert
    fun inserirCategoria(categoria: Categoria)

    @Query("SELECT * FROM categorias ORDER BY cat_nome")
    fun todasCategorias(): List<Categoria>

    @Query("SELECT cat_nome FROM categorias ORDER BY cat_nome")
    fun todosNomesCategorias(): List<String>
}
