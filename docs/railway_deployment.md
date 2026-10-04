# Deploying Sentinel AI Backend to Railway

This guide walks through deploying the **Sentinel AI Backend** to [Railway](https://railway.app/) to make the real-time AI security detection engines, Supabase authentication, and contextual assistants publicly accessible to all Android phones and external devices.

---

## 1. Prerequisites

1. A Railway account ([railway.app](https://railway.app/))
2. Your GitHub repository: `https://github.com/testbot00012-jpg/sentinel`

---

## 2. Deployment Methods

### Method A: One-Click GitHub Integration (Recommended)

1. Open the [Railway Dashboard](https://railway.app/dashboard).
2. Click **"+ New Project"** → Select **"Deploy from GitHub repo"**.
3. Choose the repository: **`testbot00012-jpg/sentinel`**.
4. Railway will automatically detect the root `railway.json` and `Dockerfile`.
5. Click **"Deploy Now"**.

---

### Method B: Railway CLI

1. Open your terminal in the repository root (`Ddd/`).
2. Login to Railway:
   ```bash
   railway login
   ```
3. Link or create a project:
   ```bash
   railway init
   ```
4. Deploy the repository:
   ```bash
   railway up
   ```

---

## 3. Environment Variables Configuration

In the Railway project dashboard, navigate to **Variables** and verify or add the following environment variables (already pre-configured in `backend/.env`):

| Variable | Recommended Value | Notes |
| :--- | :--- | :--- |
| `ENV` | `production` | Enables production mode |
| `PORT` | `8000` | (Railway assigns dynamic port automatically; Dockerfile handles `${PORT:-8000}`) |
| `PROJECT_NAME` | `Sentinel AI Security Platform` | Application title |
| `SECRET_KEY` | `cybershield-super-secret-production-grade-key-2026` | JWT secret |
| `SUPABASE_URL` | `https://fcgjgdysvkqvdinymirn.supabase.co` | Supabase project URL |
| `SUPABASE_KEY` | `sb_publishable_NY9bUK5uhI4Vf_-T9NdQMA_J7hLG9R5` | Supabase publishable key |
| `SUPABASE_SERVICE_ROLE_KEY` | `c2Jfc2VjcmV0X2xKQzJhZ3g3U0hPaDQzeldOX1ZLdkFfaGZrRlFHTlc=` | Base64-encoded secret key |
| `MODELS_DIR` | `/app/ml/saved_models` | Machine learning model checkpoints |

---

## 4. Generating a Public HTTPS Domain

1. In Railway, click on your deployed backend service.
2. Go to **Settings** → Scroll to **Public Networking**.
3. Click **"Generate Domain"**.
4. Railway will generate a public HTTPS URL, for example:
   ```
   https://sentinel-production-xxxx.up.railway.app
   ```
5. Test your live deployment in your browser or curl:
   ```bash
   curl https://sentinel-production-xxxx.up.railway.app/health
   ```
   **Expected Response:**
   ```json
   {
     "status": "HEALTHY",
     "service": "Sentinel AI Security Platform",
     "version": "1.0.0",
     "environment": "production"
   }
   ```
   Interactive Swagger documentation is live at:
   ```
   https://sentinel-production-xxxx.up.railway.app/docs
   ```

---

## 5. Connecting Real Android Devices to Railway

On any physical Android phone running Sentinel AI:

1. Open **Settings** → **Network Guard** or **Protection**.
2. Under **Cloud Gateway URL (Railway)**, enter your generated Railway URL:
   ```
   https://sentinel-production-xxxx.up.railway.app
   ```
3. Tap **"Connect to Gateway"**.
4. The Android app will immediately route all URL inspections, SMS scans, QR scans, APK analyses, Supabase auth logins, and embedded Sentinel AI conversations through the public cloud backend over Wi-Fi, 4G, or 5G!
