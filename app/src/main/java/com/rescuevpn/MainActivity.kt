package com.rescuevpn

import android.app.Activity
import android.os.Bundle
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.TextView
import android.graphics.Color
import android.graphics.drawable.GradientDrawable

class MainActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setBackgroundColor(Color.parseColor("#0F131D"))
            setPadding(48, 48, 48, 48)
        }

        val titleView = TextView(this).apply {
            text = "RescueVPN • اتصال امن نئونی"
            setTextColor(Color.parseColor("#4EDEA3"))
            textSize = 20f
            gravity = Gravity.CENTER
        }

        val statusCard = TextView(this).apply {
            text = "هسته آماده به‌کار بدون XML"
            setTextColor(Color.parseColor("#DFE2F1"))
            textSize = 14f
            gravity = Gravity.CENTER
            setPadding(32, 24, 32, 24)
            val bg = GradientDrawable().apply {
                setColor(Color.parseColor("#1C1F2A"))
                cornerRadius = 24f
            }
            background = bg
        }

        val spacer = LinearLayout(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 32
            )
        }

        root.addView(titleView)
        root.addView(spacer)
        root.addView(statusCard)
        setContentView(root)
    }
}
