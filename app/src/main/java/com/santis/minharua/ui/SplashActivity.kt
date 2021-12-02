package com.santis.minharua.ui

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.santis.minharua.MinhaRua
import com.santis.minharua.R
import com.santis.minharua.data.model.CEP
import com.santis.minharua.databinding.ActivitySplashBinding
import com.santis.minharua.util.ConvertStreamString
import com.santis.minharua.util.ViewAnimation
import com.santis.minharua.util.hideKeyboard
import com.santis.minharua.util.parteFrase
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

class SplashActivity : AppCompatActivity() {

    // Define Variaveis
    // Loading - ProgressBar

    // ViewBinding
    lateinit var binding: ActivitySplashBinding

    // SharePreferences
    lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Menu
        setSupportActionBar(binding.toolbar)

        // Checa CEP em SharedPreferences e Devine Objeto CEP
        sharedPreferences = getSharedPreferences("cep_key", Context.MODE_PRIVATE)
        binding.txtCep.setText(carregaCep())
        MinhaRua.cep = null
        checaCep(false)

        // Samples.popularSamples(this)

        // OnClickListener
        binding.cmdOk.setOnClickListener {
            salvarCep(binding.txtCep.text.toString())
            checaCep(true)
            it.hideKeyboard()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        android.os.Process.killProcess(android.os.Process.myPid())
    }
    // Verifica Cep digitado, define Objeto CEP e SharedPreferences
    private fun checaCep(checaerro: Boolean) {
        // Checa CEP e Vai para MainActivity
        binding.tilCep.error = null
        val strCep = binding.txtCep.getParsedText().toString()
        if (strCep.length != 8) {
            if (checaerro == true) {
                binding.tilCep.error = "CEP inválido ou incompleto."
            }
        } else {
            if (checaerro == true) {
                val url = "https://viacep.com.br/ws/" + binding.txtCep.getParsedText()
                    .toString() + "/json/"
                ViewAnimation.fadeIn(binding.pgCep)
                MyAsyncTask().execute(url)
            }
        }
    }

    // Classe Interna para Verficação Assincrona em BackGround de CEP via API
    inner class MyAsyncTask : AsyncTask<String, String, String>() {
        override fun onPreExecute() {
        }

        override fun doInBackground(vararg params: String?): String {
            try {
                val url = URL(params[0])
                val urlConnection = url.openConnection() as HttpURLConnection
                urlConnection.connectTimeout = 7000
                var instring = ConvertStreamString(urlConnection.inputStream)
                publishProgress(instring)
            } catch (ex: Exception) {
                Log.d("Erro: ", ex.toString())
            }
            return ""
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            if (MinhaRua.cep == null) {
                binding.tilCep.error = "CEP inválido ou incompleto."
            } else {
                val intent = Intent(applicationContext, MainActivity::class.java)
                startActivity(intent)
            }
        }
        // Cria Progressbar enquando faz consulta do CEP
        override fun onProgressUpdate(vararg params: String?) {
            try {
                MinhaRua.cep = null
                var json = JSONObject(params[0])
                val cep = json.getString("cep")
                val logradouro = parteFrase(json.getString("logradouro"), "L")
                val endereco = parteFrase(json.getString("logradouro"), "E")
                val bairro = json.getString("bairro")
                val cidade = json.getString("localidade")
                val estado = json.getString("uf")
                val complemento = json.getString("complemento")
                ViewAnimation.fadeOut(binding.pgCep)
                MinhaRua.cep = CEP(cep, logradouro, endereco, bairro, cidade, estado, complemento)
                salvarCep(cep)
            } catch (ex: Exception) {
                Log.d("Erro: ", ex.toString())
            }
        }
    }

    // Funcoes para SharedPreferences

    fun salvarCep(texto: String) {
        val editor: SharedPreferences.Editor = sharedPreferences.edit()
        editor.putString("cep_key", texto)
        editor.apply()
    }
    fun carregaCep(): String {
        return sharedPreferences.getString("cep_key", "") ?: ""
    }

    // Funções do Menu

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_sair) {
            finish()
        }
        return true
    }
}
