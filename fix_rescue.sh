#!/bin/bash
set -e
mkdir -p app/src/main/res/values app/src/main/res/layout app/src/main/java/com/rescue/vpn

cat << 'MANI_EOF' > app/src/main/AndroidManifest.xml
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android" package="com.rescue.vpn">
    <uses-permission android:name="android.permission.INTERNET"/>
    <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"/>
    <application android:allowBackup="true" android:icon="@mipmap/ic_launcher" android:label="@string/app_name" android:roundIcon="@mipmap/ic_launcher_round" android:supportsRtl="true" android:theme="@style/Theme.AppCompat.NoActionBar">
        <activity android:name=".MainActivity" android:exported="true">
            <intent-filter>
                <action android:name="android.intent.action.MAIN"/>
                <category android:name="android.intent.category.LAUNCHER"/>
            </intent-filter>
        </activity>
    </application>
</manifest>
MANI_EOF

cat << 'STR_EOF' > app/src/main/res/values/strings.xml
<resources>
    <string name="app_name">Rescue VPN</string>
</resources>
STR_EOF

cat << 'LAY_EOF' > app/src/main/res/layout/activity_main.xml
<?xml version="1.0" encoding="utf-8"?>
<FrameLayout xmlns:android="http://schemas.android.com/apk/res/android" android:layout_width="match_parent" android:layout_height="match_parent" android:background="#0F131D">
    <TextView android:layout_width="wrap_content" android:layout_height="wrap_content" android:layout_gravity="center" android:text="Rescue VPN Active" android:textColor="#4EDEA3" android:textSize="20sp"/>
</FrameLayout>
LAY_EOF

cat << 'JAVA_EOF' > app/src/main/java/com/rescue/vpn/MainActivity.java
package com.rescue.vpn;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}
JAVA_EOF

git add .
git commit -m "fix(manifest): inject valid XML roots & layout trees"
git push origin main
echo "[SUCCESS] Build Triggered."
