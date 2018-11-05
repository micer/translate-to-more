package eu.micer.translatetomore

import android.support.multidex.MultiDexApplication
import com.github.ajalt.timberkt.Timber
import com.github.ajalt.timberkt.Timber.DebugTree
import org.koin.android.ext.android.startKoin

class MainApplication : MultiDexApplication() {
    override fun onCreate() {
        super.onCreate()

        Timber.plant(DebugTree())

        // Start Koin
        startKoin(this, allModules)
    }
}