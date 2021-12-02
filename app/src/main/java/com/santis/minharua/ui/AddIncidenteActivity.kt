package com.santis.minharua.ui

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import com.santis.minharua.BuildConfig
import com.santis.minharua.MinhaRua
import com.santis.minharua.R
import com.santis.minharua.data.MinhaRuaDatabase
import com.santis.minharua.data.model.CategoriaIncidentes
import com.santis.minharua.data.model.Incidente
import com.santis.minharua.databinding.ActivityAddIncidenteBinding
import com.santis.minharua.util.resourceUri
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.io.File

class AddIncidenteActivity : AppCompatActivity() {

    // TODO Verificar Caso da Camera e Activity

    // Declarações e Funções para Acessar o App de Câmera
    val REQUEST_CODE = 200
    private val binding by lazy { ActivityAddIncidenteBinding.inflate(layoutInflater) }
    lateinit var categoriaAdapter: ArrayAdapter<String>
    lateinit var categoriaLista: List<String>
    var catinc: CategoriaIncidentes? = null
    val contexto: Context = this
    private var latestTmpUri: Uri? = null
    private val takeImageResult = registerForActivityResult(ActivityResultContracts.TakePicture()) { isSuccess ->
        if (isSuccess) {
            latestTmpUri?.let { uri ->
                binding.imgFoto.setImageURI(uri)
                // Seta Tag com a URI
                binding.imgFoto.tag = uri.toString()
            }
        }
    }

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
            if (pedirPermissaoCamera()) {
                takeImage()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
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
                        val path = binding.imgFoto.tag as String?
                        val iFot = if (path.isNullOrEmpty()) {
                            contexto.resourceUri(R.drawable.bg_no_item).toString()
                        } else {
                            binding.imgFoto.tag.toString()
                        }
                        val catId = db.categoriaDao()
                            .buscaCatPorNome(binding.cboCatAutocomplete.text.toString()).cat_id!!

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

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_CODE -> {
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permissão liberada
                    takeImage()
                } else {
                    // permissão negada
                    pedirPermissaoCamera()
                }
                return
            }
        }
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

    private fun getTmpFileUri(): Uri {
        val tmpFile = File.createTempFile("tmp_image_file", ".png", cacheDir).apply {
            createNewFile()
            deleteOnExit()
        }

        return FileProvider.getUriForFile(applicationContext, "${BuildConfig.APPLICATION_ID}.provider", tmpFile)
    }

    private fun mostrarDialogoPermissoesNegadas() {
        AlertDialog.Builder(this)
            .setTitle("Permissão Negada")
            .setMessage("\n" + "A permissão foi negada, permita as permissões nas configurações do aplicativo.")
            .setPositiveButton(
                "Configurações",
                DialogInterface.OnClickListener { dialogInterface, i ->
                    // enviar par ao app se as permissões foram negadas para sempre
                    val intent = Intent()
                    intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                    val uri = Uri.fromParts("package", packageName, null)
                    intent.data = uri
                    startActivity(intent)
                }
            )
            .setNegativeButton("Cancelar", null)
            .show()
    }

    fun pedirPermissaoCamera(): Boolean {
        if (!permissaoCameraLiberada()) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this as Activity,
                    Manifest.permission.CAMERA
                )
            ) {
                mostrarDialogoPermissoesNegadas()
            } else {
                ActivityCompat.requestPermissions(
                    this as Activity,
                    arrayOf(Manifest.permission.CAMERA),
                    REQUEST_CODE
                )
            }
            return false
        }
        return true
    }

    fun permissaoCameraLiberada(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun takeImage() {
        lifecycleScope.launchWhenStarted {
            getTmpFileUri().let { uri ->
                latestTmpUri = uri
                takeImageResult.launch(uri)
            }
        }
    }

    private fun updateUI() {

        val autoCompleteTextView: AutoCompleteTextView = binding.cboCatAutocomplete
        val db = MinhaRuaDatabase.abrirBanco(applicationContext)

        runBlocking {
            GlobalScope.launch { categoriaLista = db.categoriaDao().todosNomesCategorias() }.join()
            categoriaAdapter =
                ArrayAdapter(applicationContext, R.layout.item_categoria, categoriaLista)
            if (catinc != null) {
                autoCompleteTextView.setText(catinc!!.categoria.nomeCat.toString())
            }
            autoCompleteTextView.setAdapter(categoriaAdapter)
        }

        // Em caso de Edição, preenche dados
        if (catinc != null) {
            binding.txtTitulo.editText?.setText(catinc!!.incidentes.first().tituloInc.toString())
            binding.txtDescricao.editText?.setText(catinc!!.incidentes.first().descricaoInc.toString())
            binding.imgFoto.setImageURI(Uri.parse(catinc!!.incidentes.first().imagemInc))
        }

        // Preenche Localização
        binding.lblLocal.text = "${MinhaRua.cep?.logradouro} ${MinhaRua.cep?.endereco}"
        binding.lblBairro.text = MinhaRua.cep?.bairro
        binding.lblCep.text = MinhaRua.cep?.cep
    }
}
