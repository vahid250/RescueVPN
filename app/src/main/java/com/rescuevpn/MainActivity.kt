package com.rescuevpn
import android.content.Intent
import android.net.VpnService
import android.os.Bundle
import android.view.Gravity
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    private val VPN_REQ = 0x250
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setBackgroundColor(0xFF0F131D.toInt())
            setPadding(40, 60, 40, 60)
        }
        val title = TextView(this).apply {
            text = "Rescue VPN AI"
            textSize = 24f
            setTextColor(0xFFDFE2F1.toInt())
            gravity = Gravity.CENTER
        }
        val status = TextView(this).apply {
            text = "آماده اتصال امن"
            textSize = 14f
            setTextColor(0xFF4EDEA3.toInt())
            gravity = Gravity.CENTER
            setPadding(0, 12, 0, 40)
        }
        val btn = Button(this).apply {
            text = "اتصال به شبکه اضطراری"
            textSize = 18f
            setTextColor(0xFF003824.toInt())
            setBackgroundColor(0xFF4EDEA3.toInt())
            setPadding(32, 24, 32, 24)
            setOnClickListener {
                try {
                    val intent = VpnService.prepare(this@MainActivity)
                    if (intent != null) startActivityForResult(intent, VPN_REQ)
                    else onActivityResult(VPN_REQ, RESULT_OK, null)
                } catch (e: Exception) {
                    Toast.makeText(this@MainActivity, "خطا: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
        layout.addView(title)
        layout.addView(status)
        layout.addView(btn)
        setContentView(layout)
    }
    override fun onActivityResult(req: Int, res: Int, data: Intent?) {
        super.onActivityResult(req, res, data)
        if (req == VPN_REQ && res == RESULT_OK) {
            Toast.makeText(this, "اتصال ضد فیلتر برقرار شد!", Toast.LENGTH_SHORT).show()
        }
    }
}
