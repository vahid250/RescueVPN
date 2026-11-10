package com.rescuevpn;
import android.app.Activity;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
public class MainActivity extends Activity {
    private boolean isConnected = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setGravity(Gravity.CENTER);
        layout.setBackgroundColor(Color.parseColor("#0F131D"));
        layout.setPadding(40, 40, 40, 40);
        TextView title = new TextView(this);
        title.setText("Rescue VPN");
        title.setTextSize(26);
        title.setTextColor(Color.parseColor("#DFE2F1"));
        title.setTypeface(Typeface.DEFAULT_BOLD);
        title.setGravity(Gravity.CENTER);
        layout.addView(title);
        TextView subtitle = new TextView(this);
        subtitle.setText("WireGuard Anti-Censorship Core");
        subtitle.setTextSize(12);
        subtitle.setTextColor(Color.parseColor("#BBCAAF"));
        subtitle.setGravity(Gravity.CENTER);
        subtitle.setPadding(0, 8, 0, 80);
        layout.addView(subtitle);
        final Button connectBtn = new Button(this);
        connectBtn.setText("اتصال امن");
        connectBtn.setTextSize(18);
        connectBtn.setTextColor(Color.parseColor("#0F131D"));
        connectBtn.setTypeface(Typeface.DEFAULT_BOLD);
        final GradientDrawable shape = new GradientDrawable();
        shape.setShape(GradientDrawable.OVAL);
        shape.setColor(Color.parseColor("#4EDEA3"));
        connectBtn.setBackground(shape);
        LinearLayout.LayoutParams btnParams = new LinearLayout.LayoutParams(400, 400);
        btnParams.gravity = Gravity.CENTER;
        connectBtn.setLayoutParams(btnParams);
        connectBtn.setOnClickListener(v -> {
            isConnected = !isConnected;
            if (isConnected) {
                connectBtn.setText("متصل - امن");
                shape.setColor(Color.parseColor("#10B981"));
                Toast.makeText(this, "اتصال پایدار برقرار شد", Toast.LENGTH_SHORT).show();
            } else {
                connectBtn.setText("اتصال امن");
                shape.setColor(Color.parseColor("#4EDEA3"));
                Toast.makeText(this, "اتصال قطع شد", Toast.LENGTH_SHORT).show();
            }
        });
        layout.addView(connectBtn);
        setContentView(layout);
    }
}
