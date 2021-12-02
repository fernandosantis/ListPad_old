package com.santis.minharua

import android.app.Application
import android.content.SharedPreferences
import com.santis.minharua.data.model.CEP

class MinhaRua : Application() {
    companion object {
        @JvmField
        var cep: CEP? = null

        // SharePreferences
        lateinit var sharedPreferences: SharedPreferences

        // Funcoes para SharedPreferences

        fun salvarCep(texto: String) {
            val editor: SharedPreferences.Editor = MinhaRua.sharedPreferences.edit()
            editor.putString("cep_key", texto)
            editor.apply()
        }
        fun carregaCep(): String {
            return MinhaRua.sharedPreferences.getString("cep_key", "") ?: ""
        }
    }

    override fun onTerminate() {
        super.onTerminate()
        System.exit(0)
    }
}
