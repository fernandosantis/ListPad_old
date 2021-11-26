package com.santis.minharua

import android.app.Application
import com.santis.minharua.data.model.CEP

public class MinhaRua : Application() {
    companion object {
        @JvmField
        var cep: CEP? = null
    }

    override fun onCreate() {
        super.onCreate()
    }

    override fun onTerminate() {
        super.onTerminate()
        System.exit(0)
    }
}
