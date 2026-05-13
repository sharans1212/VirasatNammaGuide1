# Virasat-Namma Guide

Kotlin Android app for the tourism and travel specification. The app acts as a smart heritage guide for Karnataka hidden gems.

## Features

- Simulated nearby heritage site discovery.
- Rich English and Kannada site descriptions.
- QR scanner using Google ML Kit Barcode Scanning and CameraX.
- Mock QR scan button for emulator testing.
- Audio guide play and pause using Android `MediaPlayer`.
- Digital travel passport check-ins persisted with Room DB.
- Temple-inspired UI using warm stone, terracotta, indigo, and green accents.

## Demo QR Site IDs

Use any QR generator and encode one of these exact text values:

- `SITE-BELUR-001`
- `SITE-HAMPI-002`
- `SITE-AIH-003`

The emulator can also use the `Mock Scan: SITE-BELUR-001` button on the QR screen.

## Open and Run in Android Studio

1. Open Android Studio.
2. Select **File > Open**.
3. Choose this project folder:
   `C:\Users\shara\Documents\Codex\2026-05-13\can-you-build-the-android-app`
4. Let Android Studio finish **Gradle Sync**. If prompted, allow Android Studio to download the Gradle distribution and Android Gradle Plugin dependencies.
5. Install Android SDK Platform **35** if Android Studio asks for it:
   **Tools > SDK Manager > Android SDK > SDK Platforms > Android 15.0 / API 35**.
6. Create an emulator:
   **Tools > Device Manager > Create Device > Pixel 6 or Pixel 7 > API 35 image**.
7. Press **Run**.

## Test Flow

1. Home screen shows simulated nearby heritage sites.
2. Open any site and switch between English and Kannada.
3. Tap **Play** and **Pause** to test the audio guide.
4. Tap **Check In**.
5. Return to home and open **Travel Passport**. The check-in should still be listed.
6. Open **Scan QR**. Use a real QR code with one of the site IDs, or use the mock scan button in the emulator.

## Main Source Files

- `app/src/main/java/com/example/virasatnammaguide/MainActivity.kt`
- `app/src/main/java/com/example/virasatnammaguide/SiteDetailActivity.kt`
- `app/src/main/java/com/example/virasatnammaguide/QrScannerActivity.kt`
- `app/src/main/java/com/example/virasatnammaguide/HeritageRepository.kt`
- `app/src/main/java/com/example/virasatnammaguide/AppDatabase.kt`
- `app/src/main/res/layout/activity_main.xml`
- `app/src/main/res/layout/activity_site_detail.xml`
- `app/src/main/res/layout/activity_qr_scanner.xml`
