package com.rescuevpn;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.wireguard.android.backend.GoBackend;
import com.wireguard.android.backend.Statistics;
import com.wireguard.android.backend.Tunnel;
import com.wireguard.config.Config;
import com.wireguard.crypto.Key;

import java.io.InputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends Activity {
    private static final int REQUEST_CONFIG = 1002;
    private static final int REQUEST_VPN_PERMISSION = 1003;

    private GoBackend backend;
    private Config selectedConfig;
    private TextView statusText;
    private TextView diagnosticsText;
    private Button connectBtn;
    private Button chooseBtn;
    private Button generatorBtn;
    private GradientDrawable connectShape;
    private boolean isConnected = false;
    private boolean isBusy = false;
    private final ExecutorService executor =
            Executors.newSingleThreadExecutor();
    private final Handler diagnosticsHandler = new Handler(Looper.getMainLooper());
    private boolean diagnosticsBusy = false;
    private final Runnable diagnosticsTask = new Runnable() {
        @Override
        public void run() {
            if (!isConnected || backend == null || isFinishing()) return;
            if (diagnosticsBusy) {
                diagnosticsHandler.postDelayed(this, 1000);
                return;
            }
            diagnosticsBusy = true;
            executor.execute(() -> {
                String report;
                try {
                    report = collectDiagnostics();
                } catch (Exception | LinkageError e) {
                    report = "آمار ارتباط فعلاً در دسترس نیست.";
                }
                final String result = report;
                runOnUiThread(() -> {
                    diagnosticsBusy = false;
                    if (isConnected && diagnosticsText != null) {
                        diagnosticsText.setText(result);
                        diagnosticsHandler.postDelayed(diagnosticsTask, 2000);
                    }
                });
            });
        }
    };

    private final Tunnel tunnel = new Tunnel() {
        @Override
        public String getName() {
            return "RescueVPN";
        }

        @Override
        public void onStateChange(State newState) {
            runOnUiThread(() -> {
                if (newState == State.UP) {
                    isConnected = true;
                    updateUi("تونل VPN فعال است", "#10B981");
                    startDiagnostics();
                } else if (newState == State.DOWN) {
                    isConnected = false;
                    stopDiagnostics();
                    updateUi("تونل VPN خاموش است", "#4EDEA3");
                    if (diagnosticsText != null) {
                        diagnosticsText.setText("برای بررسی ارتباط، ابتدا VPN را روشن کنید.");
                    }
                }
            });
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setGravity(Gravity.CENTER);
        layout.setBackgroundColor(Color.parseColor("#0F131D"));
        layout.setPadding(40, 40, 40, 40);

        TextView title = new TextView(this);
        title.setText("R&MA");
        title.setTextSize(26);
        title.setTextColor(Color.parseColor("#DFE2F1"));
        title.setTypeface(Typeface.DEFAULT_BOLD);
        title.setGravity(Gravity.CENTER);
        layout.addView(title);

        TextView subtitle = new TextView(this);
        subtitle.setText("WireGuard VPN Client");
        subtitle.setTextSize(12);
        subtitle.setTextColor(Color.parseColor("#BBCAAF"));
        subtitle.setGravity(Gravity.CENTER);
        subtitle.setPadding(0, 8, 0, 24);
        layout.addView(subtitle);

        statusText = new TextView(this);
        statusText.setText("ابتدا فایل کانفیگ را انتخاب کنید");
        statusText.setTextSize(15);
        statusText.setTextColor(Color.WHITE);
        statusText.setGravity(Gravity.CENTER);
        statusText.setPadding(0, 16, 0, 12);
        layout.addView(statusText);

        diagnosticsText = new TextView(this);
        diagnosticsText.setText("پس از روشن‌شدن VPN، وضعیت واقعی ارتباط با سرور و آمار ترافیک نمایش داده می‌شود.");
        diagnosticsText.setTextSize(13);
        diagnosticsText.setTextColor(Color.parseColor("#BBCAAF"));
        diagnosticsText.setGravity(Gravity.CENTER);
        diagnosticsText.setPadding(8, 8, 8, 24);
        layout.addView(diagnosticsText);

        chooseBtn = new Button(this);
        chooseBtn.setText("انتخاب فایل کانفیگ");
        layout.addView(chooseBtn);
        chooseBtn.setOnClickListener(v -> chooseConfig());

        generatorBtn = new Button(this);
        generatorBtn.setText("تولید کانفیگ WireGuard");
        layout.addView(generatorBtn);
        generatorBtn.setOnClickListener(v ->
                startActivity(new Intent(this, ConfigGeneratorActivity.class)));

        connectBtn = new Button(this);
        connectBtn.setText("اتصال امن");
        connectBtn.setTextSize(18);
        connectBtn.setTextColor(Color.parseColor("#0F131D"));
        connectBtn.setTypeface(Typeface.DEFAULT_BOLD);

        connectShape = new GradientDrawable();
        connectShape.setShape(GradientDrawable.OVAL);
        connectShape.setColor(Color.parseColor("#4EDEA3"));
        connectBtn.setBackground(connectShape);

        LinearLayout.LayoutParams btnParams =
                new LinearLayout.LayoutParams(400, 400);
        btnParams.gravity = Gravity.CENTER;
        connectBtn.setLayoutParams(btnParams);

        connectBtn.setOnClickListener(v -> {
            if (isBusy) return;
            if (isConnected) {
                disconnectVpn();
            } else if (selectedConfig == null) {
                chooseConfig();
            } else {
                requestVpnPermission();
            }
        });

        layout.addView(connectBtn);
        android.widget.ScrollView scroll = new android.widget.ScrollView(this);
        scroll.addView(layout);
        setContentView(scroll);
    }

    private void chooseConfig() {
        if (isBusy || isConnected) return;
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
        startActivityForResult(intent, REQUEST_CONFIG);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CONFIG && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            Uri uri = data.getData();
            setBusy(true, "در حال بررسی فایل کانفیگ...");

            executor.execute(() -> {
                try (InputStream input =
                             getContentResolver().openInputStream(uri)) {
                    if (input == null) {
                        throw new IllegalStateException();
                    }
                    Config parsed = Config.parse(input);
                    runOnUiThread(() -> {
                        selectedConfig = parsed;
                        setBusy(false, "فایل کانفیگ با موفقیت بررسی شد");
                        Toast.makeText(this, "کانفیگ آماده اتصال است",
                                Toast.LENGTH_SHORT).show();
                    });
                } catch (Exception e) {
                    runOnUiThread(() -> {
                        selectedConfig = null;
                        setBusy(false, "بررسی فایل ناموفق بود");
                        Toast.makeText(this,
                                "فایل انتخاب‌شده معتبر نیست یا قابل خواندن نیست",
                                Toast.LENGTH_LONG).show();
                    });
                }
            });
        } else if (requestCode == REQUEST_VPN_PERMISSION) {
            if (resultCode == RESULT_OK) {
                connectVpn();
            } else {
                setBusy(false, "مجوز VPN داده نشد");
            }
        }
    }

    private void requestVpnPermission() {
        Intent intent = android.net.VpnService.prepare(this);
        if (intent != null) {
            startActivityForResult(intent, REQUEST_VPN_PERMISSION);
        } else {
            connectVpn();
        }
    }

    private void connectVpn() {
        if (selectedConfig == null || isBusy) return;
        setBusy(true, "در حال برقراری اتصال...");

        executor.execute(() -> {
            try {
                if (backend == null) {
                    backend = new GoBackend(getApplicationContext());
                }
                backend.setState(tunnel, Tunnel.State.UP, selectedConfig);
                boolean connected =
                        backend.getState(tunnel) == Tunnel.State.UP;

                runOnUiThread(() -> {
                    setBusy(false, connected
                            ? "تونل VPN فعال شد؛ وضعیت سرور را در آمار ببینید"
                            : "تونل VPN فعال نشد");
                    if (connected) {
                        isConnected = true;
                        updateUi("تونل VPN فعال است", "#10B981");
                        startDiagnostics();
                    } else {
                        isConnected = false;
                        stopDiagnostics();
                    }
                });
            } catch (Exception | LinkageError e) {
                runOnUiThread(() -> {
                    isConnected = false;
                    stopDiagnostics();
                    setBusy(false, "اتصال ناموفق بود");
                    Toast.makeText(this,
                            "راه‌اندازی WireGuard ناموفق بود",
                            Toast.LENGTH_LONG).show();
                });
            }
        });
    }

    private void disconnectVpn() {
        if (backend == null || isBusy) return;
        setBusy(true, "در حال قطع اتصال...");

        executor.execute(() -> {
            try {
                backend.setState(tunnel, Tunnel.State.DOWN, null);
                boolean disconnected =
                        backend.getState(tunnel) == Tunnel.State.DOWN;

                runOnUiThread(() -> {
                    setBusy(false, disconnected
                            ? "اتصال قطع شد"
                            : "وضعیت قطع اتصال تأیید نشد");
                    if (disconnected) {
                        isConnected = false;
                        stopDiagnostics();
                        updateUi("تونل VPN خاموش است", "#4EDEA3");
                        diagnosticsText.setText("برای بررسی ارتباط، ابتدا VPN را روشن کنید.");
                    }
                });
            } catch (Exception | LinkageError e) {
                runOnUiThread(() -> {
                    setBusy(false, "قطع اتصال ناموفق بود");
                    Toast.makeText(this, "قطع VPN ناموفق بود",
                            Toast.LENGTH_LONG).show();
                });
            }
        });
    }

    private void startDiagnostics() {
        diagnosticsHandler.removeCallbacks(diagnosticsTask);
        diagnosticsText.setText("در حال دریافت آمار تونل و بررسی Handshake...");
        diagnosticsHandler.post(diagnosticsTask);
    }

    private void stopDiagnostics() {
        diagnosticsHandler.removeCallbacks(diagnosticsTask);
        diagnosticsBusy = false;
    }

    private String collectDiagnostics() {
        if (backend == null) return "موتور VPN آماده نیست.";
        Statistics stats = backend.getStatistics(tunnel);
        long tx = stats.totalTx();
        long rx = stats.totalRx();
        long latestHandshake = 0;
        for (Key peerKey : stats.peers()) {
            Statistics.PeerStats peerStats = stats.peer(peerKey);
            if (peerStats != null) {
                latestHandshake = Math.max(
                        latestHandshake, peerStats.latestHandshakeEpochMillis());
            }
        }

        String traffic = "ارسال: " + formatBytes(tx) + " | دریافت: " + formatBytes(rx);
        if (latestHandshake <= 0) {
            return "تونل روشن است، اما هنوز Handshake از سرور دریافت نشده است.\n"
                    + traffic + "\nاین وضعیت به معنی اتصال موفق به اینترنت نیست.";
        }

        long ageSeconds = Math.max(0, (System.currentTimeMillis() - latestHandshake) / 1000);
        String age;
        if (ageSeconds < 60) {
            age = ageSeconds + " ثانیه قبل";
        } else if (ageSeconds < 3600) {
            age = (ageSeconds / 60) + " دقیقه قبل";
        } else {
            age = (ageSeconds / 3600) + " ساعت قبل";
        }
        String handshakeStatus = ageSeconds > 180
                ? "آخرین Handshake قدیمی است: " + age
                : "آخرین Handshake: " + age;
        if (rx == 0) {
            return handshakeStatus + "\n" + traffic
                    + "\nPeer پاسخ داده، اما هنوز داده‌ای از سرور دریافت نشده؛ دسترسی اینترنت تأیید نشده است.";
        }
        return handshakeStatus + "\n" + traffic
                + "\nداده از سرور دریافت شده است؛ اگر سایت باز نمی‌شود، DNS یا مسیرهای AllowedIPs باید بررسی شوند.";
    }

    private String formatBytes(long bytes) {
        if (bytes < 1024) return bytes + " B";
        if (bytes < 1024L * 1024L) return String.format(java.util.Locale.US, "%.1f KB", bytes / 1024.0);
        if (bytes < 1024L * 1024L * 1024L) return String.format(java.util.Locale.US, "%.1f MB", bytes / (1024.0 * 1024.0));
        return String.format(java.util.Locale.US, "%.2f GB", bytes / (1024.0 * 1024.0 * 1024.0));
    }

    private void setBusy(boolean busy, String message) {
        isBusy = busy;
        statusText.setText(message);
        connectBtn.setEnabled(!busy);
        chooseBtn.setEnabled(!busy && !isConnected);
        generatorBtn.setEnabled(!busy && !isConnected);
    }

    private void updateUi(String message, String color) {
        statusText.setText(message);
        connectBtn.setText(isConnected ? "قطع اتصال" : "اتصال امن");
        connectShape.setColor(Color.parseColor(color));
        chooseBtn.setEnabled(!isConnected && !isBusy);
        generatorBtn.setEnabled(!isConnected && !isBusy);
    }

    @Override
    protected void onDestroy() {
        isConnected = false;
        stopDiagnostics();
        super.onDestroy();
        executor.shutdown();
    }
}
