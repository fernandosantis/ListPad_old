package com.santis.minharua.ui

import android.content.Context
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isEmpty
import com.google.android.material.snackbar.Snackbar
import com.santis.minharua.MinhaRua
import com.santis.minharua.R
import com.santis.minharua.data.MinhaRuaDatabase
import com.santis.minharua.data.model.CategoriaIncidentes
import com.santis.minharua.data.model.Incidente
import com.santis.minharua.databinding.ActivityAddIncidenteBinding
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class AddIncidenteActivity : AppCompatActivity() {

    private val binding by lazy { ActivityAddIncidenteBinding.inflate(layoutInflater) }
    lateinit var categoriaLista: List<String>
    lateinit var categoriaAdapter: ArrayAdapter<String>

    var catinc: CategoriaIncidentes? = null

    val contexto: Context = this

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        // Em caso de Edição, Pega o Objeto CategoriaIncidentes
        if (this.intent.getBooleanExtra("edicao", false)) {
            catinc = this.intent.getSerializableExtra("catinc") as CategoriaIncidentes
        }
        // Menu
        setSupportActionBar(binding.toolbar)
        binding.toolbar.setNavigationOnClickListener { finish() }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Preencher Categorias
        updateUI()

        binding.cmdFoto.setOnClickListener {
            // TODO Implementar Funcionalidade para Capturar Foto da Camera
            Toast.makeText(applicationContext, "Função a Implementar", Toast.LENGTH_LONG).show()
        }
    }

    private fun updateUI() {

        val autoCompleteTextView: AutoCompleteTextView = binding.cboCatAutocomplete
        val db = MinhaRuaDatabase.abrirBanco(applicationContext)

        runBlocking {
            GlobalScope.launch { categoriaLista = db.categoriaDao().todosNomesCategorias() }.join()
            categoriaAdapter = ArrayAdapter(applicationContext, R.layout.categoria_item, categoriaLista)
            if (catinc != null) {
                autoCompleteTextView.setText(catinc!!.categoria.nomeCat.toString())
            }
            autoCompleteTextView.setAdapter(categoriaAdapter)
        }

        // Em caso de Edição, preenche dados
        if (catinc != null) {
            binding.txtTitulo.editText?.setText(catinc!!.incidentes.first().tituloInc.toString())
            binding.txtDescricao.editText?.setText(catinc!!.incidentes.first().descricaoInc.toString())
            binding.imgFoto.setImageResource(catinc!!.incidentes.first().imagemInc)
        }

        // Preenche Localização
        binding.lblLocal.text = "${MinhaRua.cep?.logradouro} ${MinhaRua.cep?.endereco}"
        binding.lblBairro.text = MinhaRua.cep?.bairro
        binding.lblCep.text = MinhaRua.cep?.cep
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_add_incidente, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val db = MinhaRuaDatabase.abrirBanco(applicationContext)
        if (item.itemId == R.id.action_salvar) {

            //

            if (checaForm()) {

                runBlocking {
                    GlobalScope.launch {
                        var incidente: Incidente? = null

                        val iTit = binding.txtTitulo.editText!!.text.toString()
                        val iDesc = binding.txtDescricao.editText!!.text.toString()
                        val iCep = binding.lblCep.text.toString()
                        val catId = db.categoriaDao()
                            .buscaCatPorNome(binding.cboCatAutocomplete.text.toString()).cat_id!!
                        val iFot = R.drawable.bg_no_item /*TODO Buscar da Imagem */
                        if (catinc == null) {
                            // Adiciona
                            incidente = Incidente(null, iTit, iDesc, iFot, iCep, catId)
                            db.incidenteDao().inserirIncidente(incidente)
                        } else {
                            // Atualiza
                            incidente = catinc!!.incidentes.first()
                            incidente.tituloInc = iTit
                            incidente.descricaoInc = iDesc
                            incidente.imagemInc = iFot
                            incidente.cepInc = iCep
                            incidente.catId = catId
                            db.incidenteDao().atualizarIncidente(incidente)
                        }
                    }.join()
                    // Snackbar.make(binding.root, "Informações Salvas", Snackbar.LENGTH_LONG).show()
                    Toast.makeText(contexto, "Informações Salvas", Toast.LENGTH_LONG).show()
                    finish()
                }
            }
        }

        return super.onOptionsItemSelected(item)
    }

    private fun checaForm(): Boolean {

        var temerro: Boolean = false
        binding.txtTitulo.error = null
        if (binding.txtTitulo.editText?.text.isNullOrBlank()) {
                binding.txtTitulo
                    .error = binding.txtTitulo.helperText
                temerro = true
            }

        binding.txtDescricao.error = null
        if (binding.txtDescricao.editText?.text.isNullOrBlank()) {
            binding.txtDescricao
                .error = binding.txtDescricao.helperText
            temerro = true
        }

        binding.cboCategoria.error = null
        if (binding.cboCatAutocomplete.text.isNullOrBlank()) {
            binding.cboCategoria
                .error = binding.cboCategoria.helperText
            temerro = true
        }
        if (temerro) {
            Snackbar.make(binding.root, "Informações Incompletas", Snackbar.LENGTH_LONG).show()
        }
        return !temerro
    }
}
