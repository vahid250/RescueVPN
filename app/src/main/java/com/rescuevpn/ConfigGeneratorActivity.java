package com.rescuevpn;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.wireguard.config.Config;
import com.wireguard.crypto.KeyPair;

import java.io.ByteArrayInputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class ConfigGeneratorActivity extends Activity {
    private static final int REQUEST_SAVE_CONFIG = 4101;

    private EditText endpointInput;
    private EditText serverPublicKeyInput;
    private EditText addressInput;
    private EditText dnsInput;
    private EditText allowedIpsInput;
    private EditText keepaliveInput;
    private EditText presharedKeyInput;
    private TextView publicKeyView;
    private String pendingConfig;
    private String generatedPublicKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ScrollView scroll = new ScrollView(this);
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(32, 24, 32, 32);
        scroll.addView(layout);

        TextView heading = new TextView(this);
        heading.setText("تولید کانفیگ WireGuard");
        heading.setTextSize(22);
        heading.setGravity(Gravity.CENTER);
        layout.addView(heading);

        TextView notice = new TextView(this);
        notice.setText("این ابزار یک کلید خصوصی تازه روی همین گوشی می‌سازد. برای اتصال، کلید عمومی را باید در سمت سرور ثبت کنید. فایل خروجی شامل کلید خصوصی است؛ آن را محرمانه نگه دارید. این ابزار فقط کانفیگ استاندارد WireGuard/UDP تولید می‌کند.");
        notice.setTextSize(14);
        notice.setPadding(0, 16, 0, 20);
        layout.addView(notice);

        endpointInput = addField(layout, "آدرس سرور (دامنه یا IP به‌همراه پورت)", "vpn.example.com:51820", InputType.TYPE_CLASS_TEXT);
        serverPublicKeyInput = addField(layout, "کلید عمومی سرور", "کلید عمومی سرور را وارد کنید", InputType.TYPE_CLASS_TEXT);
        addressInput = addField(layout, "آدرس تونل این دستگاه (CIDR)", "10.7.0.2/32", InputType.TYPE_CLASS_TEXT);
        dnsInput = addField(layout, "DNS (اختیاری)", "مثلاً 1.1.1.1", InputType.TYPE_CLASS_TEXT);
        allowedIpsInput = addField(layout, "AllowedIPs", "0.0.0.0/0, ::/0", InputType.TYPE_CLASS_TEXT);
        keepaliveInput = addField(layout, "PersistentKeepalive برحسب ثانیه (اختیاری)", "25", InputType.TYPE_CLASS_NUMBER);
        presharedKeyInput = addField(layout, "PreSharedKey (اختیاری)", "در صورت نیاز وارد کنید", InputType.TYPE_CLASS_TEXT);

        Button generateButton = new Button(this);
        generateButton.setText("ساخت کلید و آماده‌سازی کانفیگ");
        layout.addView(generateButton);

        publicKeyView = new TextView(this);
        publicKeyView.setText("کلید عمومی دستگاه پس از تولید اینجا نمایش داده می‌شود.");
        publicKeyView.setTextIsSelectable(true);
        publicKeyView.setTextSize(14);
        publicKeyView.setPadding(0, 18, 0, 8);
        layout.addView(publicKeyView);

        Button copyButton = new Button(this);
        copyButton.setText("کپی کلید عمومی");
        layout.addView(copyButton);
        copyButton.setOnClickListener(v -> copyPublicKey());

        Button saveButton = new Button(this);
        saveButton.setText("ذخیره فایل کانفیگ");
        layout.addView(saveButton);
        saveButton.setOnClickListener(v -> saveConfig());

        generateButton.setOnClickListener(v -> generateConfig());
        setContentView(scroll);
    }

    private EditText addField(LinearLayout layout, String label,
                              String hint, int inputType) {
        TextView title = new TextView(this);
        title.setText(label);
        title.setTextSize(14);
        title.setPadding(0, 12, 0, 4);
        layout.addView(title);

        EditText input = new EditText(this);
        input.setSingleLine(true);
        input.setTextDirection(android.view.View.TEXT_DIRECTION_LTR);
        input.setInputType(inputType);
        input.setHint(hint);
        layout.addView(input);
        return input;
    }

    private void generateConfig() {
        String endpoint = value(endpointInput);
        String serverKey = value(serverPublicKeyInput);
        String address = value(addressInput);
        String dns = value(dnsInput);
        String allowedIps = value(allowedIpsInput);
        String keepalive = value(keepaliveInput);
        String presharedKey = value(presharedKeyInput);

        if (endpoint.isEmpty() || serverKey.isEmpty() || address.isEmpty()) {
            showError("آدرس سرور، کلید عمومی سرور و آدرس تونل الزامی هستند.");
            return;
        }
        if (!endpoint.matches("^(?:\\[[0-9A-Fa-f:.]+\\]|[^\\s:]+):[0-9]{1,5}$")) {
            showError("آدرس سرور باید به شکل host:port یا IP:port باشد.");
            return;
        }
        if (keepalive.isEmpty()) keepalive = "25";
        try {
            int seconds = Integer.parseInt(keepalive);
            if (seconds < 0 || seconds > 65535) {
                showError("مقدار Keepalive معتبر نیست.");
                return;
            }
        } catch (NumberFormatException e) {
            showError("مقدار Keepalive باید عددی باشد.");
            return;
        }
        if (allowedIps.isEmpty()) allowedIps = "0.0.0.0/0, ::/0";

        try {
            KeyPair keyPair = new KeyPair();
            generatedPublicKey = keyPair.getPublicKey().toBase64();

            StringBuilder configText = new StringBuilder();
            configText.append("[Interface]\n");
            configText.append("PrivateKey = ")
                    .append(keyPair.getPrivateKey().toBase64()).append('\n');
            configText.append("Address = ").append(address).append('\n');
            if (!dns.isEmpty()) {
                configText.append("DNS = ").append(dns).append('\n');
            }
            configText.append("\n[Peer]\n");
            configText.append("PublicKey = ").append(serverKey).append('\n');
            if (!presharedKey.isEmpty()) {
                configText.append("PresharedKey = ").append(presharedKey).append('\n');
            }
            configText.append("AllowedIPs = ").append(allowedIps).append('\n');
            configText.append("Endpoint = ").append(endpoint).append('\n');
            if (!"0".equals(keepalive)) {
                configText.append("PersistentKeepalive = ").append(keepalive).append('\n');
            }

            byte[] bytes = configText.toString().getBytes(StandardCharsets.UTF_8);
            Config.parse(new ByteArrayInputStream(bytes));
            pendingConfig = configText.toString();
            publicKeyView.setText("کلید عمومی دستگاه:\n" + generatedPublicKey
                    + "\n\nاین کلید را در پنل یا سمت سرور ثبت کنید.");
            Toast.makeText(this, "کانفیگ معتبر ساخته شد.", Toast.LENGTH_LONG).show();
        } catch (Exception | LinkageError e) {
            pendingConfig = null;
            generatedPublicKey = null;
            publicKeyView.setText("ساخت کانفیگ انجام نشد.");
            showError("اطلاعات واردشده معتبر نیست. مقادیر را بررسی کنید.");
        }
    }

    private void copyPublicKey() {
        if (generatedPublicKey == null) {
            showError("ابتدا کانفیگ را بسازید.");
            return;
        }
        ClipboardManager clipboard =
                (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        if (clipboard != null) {
            clipboard.setPrimaryClip(
                    ClipData.newPlainText("WireGuard public key", generatedPublicKey));
            Toast.makeText(this, "کلید عمومی کپی شد.", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveConfig() {
        if (pendingConfig == null) {
            showError("ابتدا یک کانفیگ معتبر بسازید.");
            return;
        }
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("application/octet-stream");
        intent.putExtra(Intent.EXTRA_TITLE, "RMA-WireGuard.conf");
        startActivityForResult(intent, REQUEST_SAVE_CONFIG);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode != REQUEST_SAVE_CONFIG) return;

        if (resultCode == RESULT_OK && data != null && data.getData() != null
                && pendingConfig != null) {
            try (OutputStream output =
                         getContentResolver().openOutputStream(data.getData(), "w")) {
                if (output == null) throw new IllegalStateException();
                output.write(pendingConfig.getBytes(StandardCharsets.UTF_8));
                Toast.makeText(this,
                        "فایل ذخیره شد. آن را محرمانه نگه دارید.",
                        Toast.LENGTH_LONG).show();
            } catch (Exception e) {
                showError("ذخیره فایل ناموفق بود.");
            } finally {
                pendingConfig = null;
            }
        } else {
            pendingConfig = null;
        }
    }

    private String value(EditText field) {
        return field.getText().toString().trim();
    }

    private void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onDestroy() {
        pendingConfig = null;
        generatedPublicKey = null;
        super.onDestroy();
    }
}
