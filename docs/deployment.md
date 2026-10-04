# CyberShield — Production Deployment Guide

---

## 1. Prerequisites

- Docker Engine 24.0+ and Docker Compose v2.20+
- Host with at least 4 GB RAM and 20 GB storage
- Domain name with TLS termination (Nginx, Caddy, or Cloudflare)

---

## 2. Docker Compose Deployment

1. Copy `.env.example` to `backend/.env`:
   ```bash
   cp backend/.env.example backend/.env
   ```
2. Configure production secrets (`SECRET_KEY`, `DATABASE_URL`, `POSTGRES_PASSWORD`).
3. Start the backend services:
   ```bash
   docker compose up -d --build
   ```
4. Check running containers:
   ```bash
   docker compose ps
   ```
5. Verify health:
   ```bash
   curl http://localhost:8000/health
   ```

---

## 3. Database Schema Migrations

On startup, CyberShield's FastAPI lifespan automatically provisions normalized database tables via SQLAlchemy declarative models:
- `users`, `devices`, `device_postures`, `security_scores`, `scans`, `scan_results`, `scan_signals`, `alerts`, `incidents`, `incident_events`, `evidence`, `applications`, `app_risk_snapshots`, `permission_snapshots`, `network_snapshots`, `behavior_baselines`, `behavior_events`, `risk_events`, `reports`, `model_registry`, `model_predictions`, `assistant_sessions`, `notification_preferences`.

---

## 4. Android Client Configuration

1. In `CyberShieldAndroid_AllScreens/app/src/main/java/com/cybershield/app/core/network/CyberShieldApiClient.kt`, set `baseUrl` to your production HTTPS domain:
   ```kotlin
   class CyberShieldApiClient(private val baseUrl: String = "https://api.cybershield.security/api/v1")
   ```
2. Build the production release APK:
   ```bash
   ./gradlew assembleRelease
   ```
