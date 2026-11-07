#!/data/data/com.termux/files/usr/bin/bash
set -e

mkdir -p app/src/main/java/org/rescue/vpn
mkdir -p .github/workflows

cat << 'KTS' > app/build.gradle.kts
plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "org.rescue.vpn"
    compileSdk = 34

    defaultConfig {
        applicationId = "org.rescue.vpn"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0.0"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"))
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
}
KTS

cat << 'KT' > app/src/main/java/org/rescue/vpn/SafeVpnService.kt
package org.rescue.vpn

import android.net.VpnService
import android.content.Intent
import android.os.ParcelFileDescriptor

class SafeVpnService : VpnService() {
    private var vpnInterface: ParcelFileDescriptor? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val builder = Builder()
            .setSession("RescueVpnSecure")
            .addAddress("10.0.0.2", 24)
            .addDnsServer("1.1.1.1")
            .addRoute("0.0.0.0", 0)

        vpnInterface = builder.establish()
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        vpnInterface?.close()
        vpnInterface = null
    }
}
KT

cat << 'YML' > .github/workflows/android.yml
name: Rescue Build
on: [push, workflow_dispatch]
jobs:
  build:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - name: Set up JDK 17
        uses: actions/setup-java@v4
        with:
          java-version: '17'
          distribution: 'temurin'
      - name: Setup Gradle
        uses: gradle/actions/setup-gradle@v3
      - name: Build APK
        run: ./gradlew assembleRelease --no-daemon --stacktrace
      - name: Upload Artifact
        uses: actions/upload-artifact@v4
        with:
          name: rescue-vpn-release
          path: app/build/outputs/apk/release/*.apk
YML

git add app/build.gradle.kts app/src/main/java/org/rescue/vpn/SafeVpnService.kt .github/workflows/android.yml
git commit -m "fix(stage5): lightweight reliable zero-dep core"
git push origin main
