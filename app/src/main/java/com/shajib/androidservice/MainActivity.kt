package com.shajib.androidservice

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.shajib.androidservice.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    var myService: MyService? = null
    var mbound = false

    val TAG = "MainActivity"

    private val serviceConnect: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(p0: ComponentName?, service: IBinder?) {
            val binder: MyService.LocalBinder = service as MyService.LocalBinder
            myService = binder.getService()
            mbound = true

            bringServiceToForeground()
        }

        private fun bringServiceToForeground() {
            myService?.let {
                if (!it.isForegroundService) {
                    val intent = Intent(this@MainActivity, MyService::class.java)
                    intent.action = MyService.FOREGROUND_SERVICE
                    ContextCompat.startForegroundService(this@MainActivity, intent)
                    it.doForegroundThings()
                } else {
                    Log.d(TAG, "Service is already in foreground")
                }
            } ?: Log.d(TAG, "Service is null")
        }

        override fun onServiceDisconnected(p0: ComponentName?) {
            mbound = false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnStartServiceSmartly.setOnClickListener {
            val intent = Intent(this, MyService::class.java)
            intent.action = MyService.START_SERVICE
            startService(intent)

            bindWithService()
        }

        binding.btnStartForegroundService.setOnClickListener {
            val intent = Intent(this, MyService::class.java)
            intent.action = MyService.FOREGROUND_SERVICE
            ContextCompat.startForegroundService(this, intent)
        }

        binding.btnStopService.setOnClickListener {
            val intent = Intent(this, MyService::class.java)
            intent.action = MyService.STOP_SERVICE
            startService(intent)
        }
    }

    private fun bindWithService() {
        val intent = Intent(this, MyService::class.java)
        bindService(intent, serviceConnect, BIND_IMPORTANT)
    }
}