package com.rescuevpn;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;

public class MainActivity extends AppCompatActivity {
    private boolean isConnected = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView tvStatus = findViewById(R.id.tvStatus);
        MaterialButton btnPower = findViewById(R.id.btnPower);

        findViewById(R.id.btnSettings).setOnClickListener(v -> 
            Toast.makeText(this, "Settings clicked", Toast.LENGTH_SHORT).show()
        );

        btnPower.setOnClickListener(v -> {
            isConnected = !isConnected;
            if (isConnected) {
                tvStatus.setText(getString(R.string.status_connected));
                tvStatus.setTextColor(0xFF4EDEA3);
                btnPower.setBackgroundColor(0xFF4EDEA3);
            } else {
                tvStatus.setText(getString(R.string.status_disconnected));
                tvStatus.setTextColor(0xFFBBFFDF);
                btnPower.setBackgroundColor(0xFF313540);
            }
        });
    }
}
