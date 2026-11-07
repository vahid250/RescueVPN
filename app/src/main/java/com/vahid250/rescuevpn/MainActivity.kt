package com.vahid250.rescuevpn

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val tv = TextView(this).apply {
            text = "مرکز اتصال فعال RescueVPN - آماده اتصال امن"
            textSize = 20f
            setPadding(40, 80, 40, 40)
        }
        setContentView(tv)
    }
}
