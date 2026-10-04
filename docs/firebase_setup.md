# CyberShield — Firebase Integration Guide

CyberShield integrates Firebase Authentication to provide seamless, secure sign-in across Android and web clients while validating cryptographic tokens on the FastAPI backend.

---

## 1. Firebase Console Setup

1. Open the [Firebase Console](https://console.firebase.google.com/).
2. Create or select your project: `cybershield-security`.
3. Under **Authentication > Sign-in method**, enable:
   - **Email/Password**
   - **Google Sign-In** (optional)
4. Under **Project settings > General**, register an Android application:
   - Android package name: `com.cybershield.app`
   - Download the `google-services.json` file.
   - Place `google-services.json` in `CyberShieldAndroid_AllScreens/app/google-services.json`.

---

## 2. Backend Service Account Setup

1. In Firebase Console, navigate to **Project settings > Service accounts**.
2. Click **Generate new private key** and download the JSON key.
3. Save the key on your backend host (e.g. at `/etc/secrets/firebase-service-account.json` or `./backend/firebase-key.json`).
4. Set the environment variable in `backend/.env`:
   ```bash
   FIREBASE_CREDENTIALS_PATH="/etc/secrets/firebase-service-account.json"
   FIREBASE_PROJECT_ID="cybershield-security"
   ```
5. When configured, `app.auth.firebase.verify_firebase_id_token` validates incoming tokens with the official Firebase Admin SDK.
   - For offline test environments, development fallback mode is provided for mock tokens (`mock_*` or `test_*`).
