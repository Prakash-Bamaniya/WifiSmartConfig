package com.wifiSmartConfig

import android.app.Application
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.location.LocationManager
import android.net.wifi.WifiManager
import android.os.Build
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer

class MyApp : Application() {
    private var wifiBroadcastData: MutableLiveData<String>? = null

    companion object {
        lateinit var instance: MyApp
    }

    override fun onCreate() {
        super.onCreate()
        instance = this

        wifiBroadcastData = MutableLiveData<String>()
        val filter = IntentFilter(WifiManager.NETWORK_STATE_CHANGED_ACTION)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            filter.addAction(LocationManager.PROVIDERS_CHANGED_ACTION)
        }
        registerReceiver(mReceiver, filter)
    }

    override fun onTerminate() {
        super.onTerminate()
        unregisterReceiver(mReceiver)
    }

    fun observeBroadcast(owner: LifecycleOwner, observer: Observer<String>) {
        wifiBroadcastData?.observe(owner, observer)
    }

    private val mReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action ?: return
            when (action) {
                WifiManager.NETWORK_STATE_CHANGED_ACTION, LocationManager.PROVIDERS_CHANGED_ACTION -> {
                    //action is data
                    wifiBroadcastData?.postValue(action)
                }
            }
        }
    }
}