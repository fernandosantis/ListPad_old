package com.santis.minharua.data

import android.content.Context
import com.santis.minharua.R
import com.santis.minharua.data.model.Incidente
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class Samples {
    companion object {
        fun popularSamples(contexto: Context) {
            val db = MinhaRuaDatabase.abrirBanco(contexto)
            GlobalScope.launch {
                for (x in 1..5) {
                    db.incidenteDao().inserirIncidente(
                        Incidente(null, "Problema $x", "Aqui no minha rua está ocorrendo um grave problema do tipo $x que não tem mais jeito.", R.drawable.ic_city, "13660-040", x)
                    )
                }
                for (x in 6..10) {
                    db.incidenteDao().inserirIncidente(
                        Incidente(null, "Problema $x", "Aqui no minha rua está ocorrendo um grave problema do tipo $x que não tem mais jeito.", R.drawable.ic_city, "13660-200", x)
                    )
                }
                for (x in 11..20) {
                    db.incidenteDao().inserirIncidente(
                        Incidente(null, "Problema $x", "Aqui no minha rua está ocorrendo um grave problema do tipo $x que não tem mais jeito.", R.drawable.ic_city, "13660-039", x)
                    )
                }
                for (x in 17..21) {
                    db.incidenteDao().inserirIncidente(
                        Incidente(null, "Problema $x", "Aqui no minha rua está ocorrendo um grave problema do tipo $x que não tem mais jeito.", R.drawable.ic_city, "13660-100", x)
                    )
                }
            }
        }
    }
}
