package com.rescue.vpn

import android.net.VpnService
import android.content.Intent
import android.os.ParcelFileDescriptor

class SafeVpnService : VpnService() {
    private var vpnInterface: ParcelFileDescriptor? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        try {
            if (vpnInterface == null) {
                val builder = Builder()
                builder.setSession("RescueSecureTunnel")
                builder.addAddress("10.8.0.2", 24)
                builder.addDnsServer("1.1.1.1")
                builder.addRoute("0.0.0.0", 0)
                vpnInterface = builder.establish()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return START_STICKY
    }

    override fun onDestroy() {
        try {
            vpnInterface?.close()
            vpnInterface = null
        } catch (e: Exception) {
            e.printStackTrace()
        }
        super.onDestroy()
    }
}
