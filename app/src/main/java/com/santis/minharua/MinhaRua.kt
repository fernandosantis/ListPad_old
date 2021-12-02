package com.santis.minharua

import android.app.Application
import com.santis.minharua.data.model.CEP

class MinhaRua : Application() {
    companion object {
        @JvmField
        var cep: CEP? = null
    }

    override fun onTerminate() {
        super.onTerminate()
        System.exit(0)
    }
}
