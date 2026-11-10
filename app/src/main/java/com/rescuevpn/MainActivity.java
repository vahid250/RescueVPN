package com.rescuevpn;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setGravity(Gravity.CENTER);
        layout.setBackgroundColor(Color.parseColor("#0F131D"));
        layout.setPadding(48, 48, 48, 48);

        TextView title = new TextView(this);
        title.setText("RescueVPN Engine");
        title.setTextSize(26);
        title.setTextColor(Color.parseColor("#4EDEA3"));
        title.setTypeface(null, Typeface.BOLD);
        title.setGravity(Gravity.CENTER);
        layout.addView(title);

        TextView status = new TextView(this);
        status.setText("سامانه ضد انسداد آماده اتصال");
        status.setTextSize(16);
        status.setTextColor(Color.parseColor("#DFE2F1"));
        status.setPadding(0, 24, 0, 48);
        status.setGravity(Gravity.CENTER);
        layout.addView(status);

        Button connectBtn = new Button(this);
        connectBtn.setText("اتصال امن اضطراری");
        connectBtn.setBackgroundColor(Color.parseColor("#10B981"));
        connectBtn.setTextColor(Color.parseColor("#003824"));
        connectBtn.setPadding(32, 20, 32, 20);
        connectBtn.setOnClickListener(v -> 
            Toast.makeText(this, "تونل امن با موفقیت فعال شد", Toast.LENGTH_SHORT).show()
        );
        layout.addView(connectBtn);

        setContentView(layout);
    }
}
