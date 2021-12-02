package com.santis.minharua.ui

import android.content.Intent
import android.os.Bundle
import android.os.StrictMode
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.santis.minharua.MinhaRua
import com.santis.minharua.data.MainAdapter
import com.santis.minharua.data.MinhaRuaDatabase
import com.santis.minharua.data.model.CategoriaIncidentes
import com.santis.minharua.databinding.ActivityMainBinding
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var catincidentesAdapter: MainAdapter
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {

        // Contornar Restrições para Compartilhar Cards
        val builder: StrictMode.VmPolicy.Builder = StrictMode.VmPolicy.Builder()
        StrictMode.setVmPolicy(builder.build())

        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        // Menu
        setSupportActionBar(binding.bottomAppBar)
        binding.bottomAppBar.setNavigationOnClickListener { finish() }

        // Adicionar Incidente - FAB Buttom
        binding.cmdAddIncidente.setOnClickListener {
            val intent = Intent(this, AddIncidenteActivity::class.java)
            intent.putExtra("edicao", false)
            startActivity(intent)
        }
/*        Implementacao.implementaCategorias(this)

       Para implementar manualmente as Categorias
       Atualmente estão sendo implantadas na
       criação do banco de dados.*/

        updateUI()
    }

    private fun updateUI() {

        val db = MinhaRuaDatabase.abrirBanco(this)
        var incidentesLista: MutableList<CategoriaIncidentes>

        val recyclerview = binding.rvIncidentes
        recyclerview.layoutManager = LinearLayoutManager(this)

        GlobalScope.launch {
            incidentesLista =
                db.catincidenteDao().getCategoriaeIncidentes(MinhaRua.cep?.cep.toString())
            catincidentesAdapter = MainAdapter(incidentesLista)
            recyclerview.adapter = catincidentesAdapter
            if (incidentesLista.isEmpty()) {
                recyclerview.visibility = View.GONE
                binding.noLayIncidente.visibility = View.VISIBLE
            } else {
                binding.noLayIncidente.visibility = View.GONE
                recyclerview.visibility = View.VISIBLE
            }
        }
        // Preenche Placa de Rua
        binding.lblLogradouro.text = MinhaRua.cep?.logradouro
        binding.lblEndereco.text = MinhaRua.cep?.endereco
        binding.lblNumero.text = MinhaRua.cep?.complemento
        binding.lblBairro.text = MinhaRua.cep?.bairro
    }

    override fun onResume() {
        super.onResume()
        updateUI()
    }
}
