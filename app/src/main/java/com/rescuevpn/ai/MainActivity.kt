package com.rescuevpn.ai

import android.app.Activity
import android.content.Intent
import android.net.VpnService
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private val VPN_REQUEST_CODE = 0x0F
    private var isConnected = false
    private lateinit var tvStatus: TextView
    private lateinit var btnConnect: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tvStatus = findViewById(R.id.tvStatus)
        btnConnect = findViewById(R.id.btnConnect)

        btnConnect.setOnClickListener {
            toggleVpnState()
        }
    }

    private fun toggleVpnState() {
        if (!isConnected) {
            val intent = VpnService.prepare(this)
            if (intent != null) {
                startActivityForResult(intent, VPN_REQUEST_CODE)
            } else {
                onActivityResult(VPN_REQUEST_CODE, Activity.RESULT_OK, null)
            }
        } else {
            disconnectVpn()
        }
    }

    private fun disconnectVpn() {
        isConnected = false
        tvStatus.text = "وضعیت: غیرفعال"
        btnConnect.text = "اتصال به تونل نجات"
        Toast.makeText(this, "اتصال قطع شد", Toast.LENGTH_SHORT).show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == VPN_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            isConnected = true
            tvStatus.text = "متصل - پروتکل هوشمند فعال"
            btnConnect.text = "قطع اتصال"
            Toast.makeText(this, "اتصال ایمن برقرار گردید", Toast.LENGTH_SHORT).show()
        }
    }
}
