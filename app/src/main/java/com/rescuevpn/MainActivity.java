package com.rescuevpn;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private boolean isConnected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageButton btnPower = findViewById(R.id.btnPowerToggle);
        ImageView btnSettings = findViewById(R.id.btnSettings);
        TextView tvStatus = findViewById(R.id.tvStatus);
        TextView tvHint = findViewById(R.id.tvHint);

        btnPower.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isConnected = !isConnected;
                if (isConnected) {
                    tvStatus.setText("ON (متصل شد 🛡️)");
                    tvStatus.setTextColor(0xFF10B981);
                    tvHint.setText("تونل امن وایرگارد فعال است");
                    Toast.makeText(MainActivity.this, "اتصال با موفقیت برقرار شد!", Toast.LENGTH_SHORT).show();
                } else {
                    tvStatus.setText("OFF (قطع شد)");
                    tvStatus.setTextColor(0xFFEF4444);
                    tvHint.setText("برای برقراری مجدد لمس کنید");
                    Toast.makeText(MainActivity.this, "اتصال قطع گردید.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "تنظیمات پیشرفته Rescue VPN", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
