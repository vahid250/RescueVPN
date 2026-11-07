package com.rescue.vpn

import android.app.Activity
import android.content.Intent
import android.net.VpnService
import android.os.Bundle
import android.widget.Button
import android.widget.Toast

class MainActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val btn = Button(this).apply {
            text = "اتصال تونل امن RescueVPN"
            setOnClickListener { prepareAndConnect() }
        }
        setContentView(btn)
    }

    private fun prepareAndConnect() {
        val intent = VpnService.prepare(this)
        if (intent != null) {
            startActivityForResult(intent, 1001)
        } else {
            onActivityResult(1001, RESULT_OK, null)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1001 && resultCode == RESULT_OK) {
            val sIntent = Intent(this, SafeVpnService::class.java)
            startService(sIntent)
            Toast.makeText(this, "تونل امن با موفقیت فعال شد", Toast.LENGTH_SHORT).show()
        }
    }
}
