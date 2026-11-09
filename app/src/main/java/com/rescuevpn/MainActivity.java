package com.rescuevpn;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
    private boolean isConnected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final ImageButton btnPower = findViewById(R.id.btnPower);
        final ImageButton btnSettings = findViewById(R.id.btnSettings);
        final TextView tvStatus = findViewById(R.id.tvStatus);

        btnPower.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isConnected = !isConnected;
                if (isConnected) {
                    tvStatus.setText(R.string.status_conn);
                    tvStatus.setTextColor(0xFF10B981);
                    Toast.makeText(MainActivity.this, "Connected", Toast.LENGTH_SHORT).show();
                } else {
                    tvStatus.setText(R.string.status_disconn);
                    tvStatus.setTextColor(0xFFEF4444);
                    Toast.makeText(MainActivity.this, "Disconnected", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "Settings", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
