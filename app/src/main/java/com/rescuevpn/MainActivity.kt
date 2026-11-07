package com.rescuevpn
import android.os.Bundle
import android.widget.TextView
import android.view.Gravity
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val tv = TextView(this).apply {
            text = "🛡️ Rescue VPN AI - Active & Ready"
            textSize = 20f
            gravity = Gravity.CENTER
        }
        setContentView(tv)
    }
}
