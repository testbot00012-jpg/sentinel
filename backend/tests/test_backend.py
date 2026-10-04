import pytest
import pytest_asyncio
from httpx import AsyncClient, ASGITransport
from app.main import app
from app.database.session import engine, Base, AsyncSessionLocal
from app.schemas.cyber import RiskLevelEnum

@pytest_asyncio.fixture(scope="module")
async def setup_db():
    async with engine.begin() as conn:
        await conn.run_sync(Base.metadata.create_all)
    yield
    async with engine.begin() as conn:
        await conn.run_sync(Base.metadata.drop_all)

@pytest_asyncio.fixture
async def client(setup_db):
    transport = ASGITransport(app=app)
    async with AsyncClient(transport=transport, base_url="http://testserver") as ac:
        yield ac

@pytest.mark.asyncio
async def test_health_check(client):
    resp = await client.get("/health")
    assert resp.status_code == 200
    data = resp.json()
    assert data["status"] == "HEALTHY"
    assert "CyberShield" in data["service"]

@pytest.mark.asyncio
async def test_auth_and_device_flow(client):
    # Register
    reg_payload = {
        "email": "analyst@cybershield.security",
        "password": "StrongSecurityPassword#2026",
        "full_name": "Security Analyst",
        "installation_id": "inst_device_test_001",
        "device_name": "Pixel 9 Pro"
    }
    reg_resp = await client.post("/api/v1/auth/register", json=reg_payload)
    assert reg_resp.status_code == 200
    token_data = reg_resp.json()
    assert "access_token" in token_data
    token = token_data["access_token"]
    headers = {"Authorization": f"Bearer {token}"}

    # Get Me
    me_resp = await client.get("/api/v1/auth/me", headers=headers)
    assert me_resp.status_code == 200
    assert me_resp.json()["email"] == reg_payload["email"]

    # Posture Sync
    posture_payload = {
        "is_screen_lock_enabled": True,
        "is_storage_encrypted": True,
        "is_developer_options_enabled": False,
        "is_adb_enabled": False,
        "is_root_detected": False,
        "root_signals": [],
        "play_integrity_verdict": "MEETS_STRONG_INTEGRITY",
        "battery_level": 88,
        "storage_used_percent": 42.5
    }
    sync_resp = await client.post("/api/v1/device/posture-sync", json=posture_payload, headers=headers)
    assert sync_resp.status_code == 200
    assert sync_resp.json()["status"] == "SUCCESS"

    # Security Score
    score_resp = await client.get("/api/v1/security/score", headers=headers)
    assert score_resp.status_code == 200
    score_data = score_resp.json()
    assert score_data["overall_score"] >= 80
    assert "breakdown" in score_data

@pytest.mark.asyncio
async def test_url_phishing_scanner(client):
    reg_payload = {
        "email": "url_tester@cybershield.security",
        "password": "Password123#",
        "installation_id": "inst_url_002",
        "device_name": "Samsung S24"
    }
    reg_resp = await client.post("/api/v1/auth/register", json=reg_payload)
    token = reg_resp.json()["access_token"]
    headers = {"Authorization": f"Bearer {token}"}

    # Scan Phishing URL
    phish_payload = {"url": "http://192.168.1.1/secure-sbi-banking-login/update-kyc.php?token=xyz"}
    scan_resp = await client.post("/api/v1/url/analyze", json=phish_payload, headers=headers)
    assert scan_resp.status_code == 200
    data = scan_resp.json()
    assert data["risk_score"] >= 60
    assert data["risk_level"] in [RiskLevelEnum.HIGH_RISK.value, RiskLevelEnum.CRITICAL.value]
    assert len(data["signals"]) >= 2
    assert "Direct IP Hostname" in [s["name"] for s in data["signals"]]

    # Scan Legitimate URL
    safe_payload = {"url": "https://google.com"}
    safe_resp = await client.post("/api/v1/url/analyze", json=safe_payload, headers=headers)
    assert safe_resp.status_code == 200
    safe_data = safe_resp.json()
    assert safe_data["risk_level"] == RiskLevelEnum.SAFE.value
    assert safe_data["risk_score"] < 25

@pytest.mark.asyncio
async def test_sms_message_scanner_multilingual(client):
    reg_payload = {
        "email": "sms_tester@cybershield.security",
        "password": "Password123#",
        "installation_id": "inst_sms_003",
        "device_name": "OnePlus 12"
    }
    reg_resp = await client.post("/api/v1/auth/register", json=reg_payload)
    token = reg_resp.json()["access_token"]
    headers = {"Authorization": f"Bearer {token}"}

    # English Urgent Fraud Message
    msg_en = {
        "message_text": "URGENT: Your SBI netbanking account is suspended. Share OTP immediately or click http://bit.ly/sbi-verify to avoid deactivation.",
        "language": "en"
    }
    res_en = await client.post("/api/v1/messages/analyze", json=msg_en, headers=headers)
    assert res_en.status_code == 200
    data_en = res_en.json()
    assert data_en["risk_score"] >= 75
    assert data_en["risk_level"] in ["HIGH_RISK", "CRITICAL"]

    # Hindi Fraud Message
    msg_hi = {
        "message_text": "बधाई! आपने 25 लाख की लॉटरी जीती है। तुरंत अपना पिन और खाता नंबर शेयर करें।",
        "language": "hi"
    }
    res_hi = await client.post("/api/v1/messages/analyze", json=msg_hi, headers=headers)
    assert res_hi.status_code == 200
    data_hi = res_hi.json()
    assert data_hi["risk_score"] >= 50

@pytest.mark.asyncio
async def test_qr_payment_scanner(client):
    reg_payload = {
        "email": "qr_tester@cybershield.security",
        "password": "Password123#",
        "installation_id": "inst_qr_004",
        "device_name": "Moto Edge"
    }
    reg_resp = await client.post("/api/v1/auth/register", json=reg_payload)
    token = reg_resp.json()["access_token"]
    headers = {"Authorization": f"Bearer {token}"}

    # QR with UPI debit request
    qr_payload = {"raw_payload": "upi://pay?pa=scammer99@ybl&pn=LotteryAgent&am=4999&cu=INR"}
    res = await client.post("/api/v1/qr/analyze", json=qr_payload, headers=headers)
    assert res.status_code == 200
    data = res.json()
    assert data["scanner_type"] == "QR_FRAUD"
    assert "UPI Payment Intent" in [s["name"] for s in data["signals"]]
    assert "Fixed Amount Debit Request" in [s["name"] for s in data["signals"]]

@pytest.mark.asyncio
async def test_apk_and_file_scanner(client):
    reg_payload = {
        "email": "apk_tester@cybershield.security",
        "password": "Password123#",
        "installation_id": "inst_apk_005",
        "device_name": "Xiaomi 14"
    }
    reg_resp = await client.post("/api/v1/auth/register", json=reg_payload)
    token = reg_resp.json()["access_token"]
    headers = {"Authorization": f"Bearer {token}"}

    # Dangerous Trojan APK profile
    apk_payload = {
        "package_name": "com.fraud.fakeflash",
        "target_sdk": 26,
        "installer_package": None,
        "is_sideloaded": True,
        "permissions": [
            "android.permission.BIND_ACCESSIBILITY_SERVICE",
            "android.permission.SYSTEM_ALERT_WINDOW",
            "android.permission.RECEIVE_SMS",
            "android.permission.REQUEST_INSTALL_PACKAGES"
        ]
    }
    apk_res = await client.post("/api/v1/apk/analyze", json=apk_payload, headers=headers)
    assert apk_res.status_code == 200
    apk_data = apk_res.json()
    assert apk_data["risk_score"] >= 80
    assert apk_data["risk_level"] in ["HIGH_RISK", "CRITICAL"]

    # Known IOC File Hash
    file_payload = {
        "file_name": "important_invoice.pdf.apk",
        "size_bytes": 1048576,
        "file_hash_sha256": "5e884898da28047151d0e56f8dc6292773603d0d6aabbdd62a11ef721d1542d8",
        "entropy": 7.8
    }
    file_res = await client.post("/api/v1/files/scan", json=file_payload, headers=headers)
    assert file_res.status_code == 200
    file_data = file_res.json()
    assert file_data["risk_score"] == 99
    assert file_data["risk_level"] == "CRITICAL"

@pytest.mark.asyncio
async def test_assistant_chat(client):
    reg_payload = {
        "email": "ai_tester@cybershield.security",
        "password": "Password123#",
        "installation_id": "inst_ai_006",
        "device_name": "Pixel 8"
    }
    reg_resp = await client.post("/api/v1/auth/register", json=reg_payload)
    token = reg_resp.json()["access_token"]
    headers = {"Authorization": f"Bearer {token}"}

    chat_payload = {
        "message": "Why is Accessibility permission dangerous for an unknown app?",
        "language": "en"
    }
    res = await client.post("/api/v1/assistant/chat", json=chat_payload, headers=headers)
    assert res.status_code == 200
    data = res.json()
    assert "response" in data
    assert len(data["recommended_safe_actions"]) > 0
    assert "limitations" in data

@pytest.mark.asyncio
async def test_incident_and_evidence(client):
    reg_payload = {
        "email": "inc_tester@cybershield.security",
        "password": "Password123#",
        "installation_id": "inst_inc_007",
        "device_name": "Pixel 7"
    }
    reg_resp = await client.post("/api/v1/auth/register", json=reg_payload)
    token = reg_resp.json()["access_token"]
    headers = {"Authorization": f"Bearer {token}"}

    # Create Incident
    inc_payload = {
        "title": "Phishing Attempt on Netbanking",
        "threat_category": "PHISHING",
        "description": "User received fraudulent SMS with link targeting credentials.",
        "priority": "HIGH",
        "evidence_type": "URL",
        "evidence_content": "http://192.168.1.1/fake-bank"
    }
    inc_res = await client.post("/api/v1/incidents/create", json=inc_payload, headers=headers)
    assert inc_res.status_code == 200
    inc_id = inc_res.json()["incident_id"]

    # Retrieve Incident
    get_res = await client.get(f"/api/v1/incidents/{inc_id}", headers=headers)
    assert get_res.status_code == 200
    assert get_res.json()["title"] == inc_payload["title"]
    assert get_res.json()["status"] == "NEW"

@pytest.mark.asyncio
async def test_supabase_credentials_and_account_activity(client):
    reg_payload = {
        "email": "supabase_user@cybershield.security",
        "password": "SecurePassword#2026",
        "full_name": "Supabase Account User",
        "installation_id": "inst_supa_008",
        "device_name": "Galaxy Fold 6"
    }
    # 1. Register (stores in Supabase Auth & logs registration activity)
    reg_resp = await client.post("/api/v1/auth/register", json=reg_payload)
    assert reg_resp.status_code == 200
    token = reg_resp.json()["access_token"]
    headers = {"Authorization": f"Bearer {token}"}

    # 2. Login with valid credentials (validates against Supabase Auth)
    login_resp = await client.post("/api/v1/auth/login", json={
        "email": reg_payload["email"],
        "password": reg_payload["password"],
        "installation_id": "inst_supa_008",
        "device_name": "Galaxy Fold 6"
    })
    assert login_resp.status_code == 200
    assert "access_token" in login_resp.json()

    # 3. Login with invalid credentials (rejected by Supabase Auth validation)
    bad_login_resp = await client.post("/api/v1/auth/login", json={
        "email": reg_payload["email"],
        "password": "WrongPassword999!",
        "installation_id": "inst_supa_008"
    })
    assert bad_login_resp.status_code == 401

    # 4. Retrieve recent activity for this particular account from Supabase
    act_resp = await client.get("/api/v1/account/activity", headers=headers)
    assert act_resp.status_code == 200
    activities = act_resp.json()
    assert len(activities) >= 2  # REGISTRATION and LOGIN
    types = [a["activity_type"] for a in activities]
    assert "REGISTRATION" in types
    assert "LOGIN" in types

    # 5. Log explicit security activity for this account
    new_act_payload = {
        "activity_type": "PASSWORD_HEALTH_CHECK",
        "description": "User verified password entropy and leak exposure.",
        "severity": "INFO",
        "metadata": {"entropy_score": 85}
    }
    log_resp = await client.post("/api/v1/account/activity", json=new_act_payload, headers=headers)
    assert log_resp.status_code == 200

    # 6. Retrieve login activity specifically
    logins_resp = await client.get("/api/v1/account/logins", headers=headers)
    assert logins_resp.status_code == 200
    login_records = logins_resp.json()
    assert all(a["activity_type"] in ["LOGIN", "REGISTRATION"] for a in login_records)

@pytest.mark.asyncio
async def test_sentinel_contextual_assistant_and_scan_comparison(client):
    reg_payload = {
        "email": "sentinel_tester@sentinel.security",
        "password": "StrongPassword2026!",
        "installation_id": "inst_sentinel_009",
        "device_name": "Google Pixel 9"
    }
    reg_resp = await client.post("/api/v1/auth/register", json=reg_payload)
    token = reg_resp.json()["access_token"]
    headers = {"Authorization": f"Bearer {token}"}

    # 1. Execute URL scan via canonical endpoint
    url_scan = await client.post(
        "/api/v1/scans/url",
        json={"url": "http://192.168.1.1/sbi-verify-kyc.php?token=fake"},
        headers=headers
    )
    assert url_scan.status_code == 200
    res = url_scan.json()
    scan_id = res["scan_id"]
    assert "security_score" in res
    assert res["security_score"] <= 59  # Red bracket: High Risk
    assert res["risk_level"] in ["HIGH_RISK", "CRITICAL"]
    assert len(res["why_this_score"]) >= 1

    # 2. Get detailed report
    report_resp = await client.get(f"/api/v1/scans/{scan_id}/report", headers=headers)
    assert report_resp.status_code == 200
    report = report_resp.json()
    assert report["report_header"] == "SENTINEL AI SECURITY REPORT"
    assert report["security_score"] == res["security_score"]

    # 3. Contextual Assistant: Why is this dangerous?
    q1_resp = await client.post(
        f"/api/v1/scans/{scan_id}/assistant",
        json={"message": "Why is this dangerous?"},
        headers=headers
    )
    assert q1_resp.status_code == 200
    q1 = q1_resp.json()
    assert "signals" in q1["answer"].lower() or "risk" in q1["answer"].lower()
    assert q1["confidence"] >= 0.80

    # 4. Contextual Assistant: Score explanation
    q2_resp = await client.post(
        f"/api/v1/scans/{scan_id}/assistant",
        json={"message": "Why did you give this score?"},
        headers=headers
    )
    assert q2_resp.status_code == 200
    q2 = q2_resp.json()
    assert str(res["security_score"]) in q2["answer"]

    # 5. Assistant conversation history for this exact scan
    history_resp = await client.get(f"/api/v1/scans/{scan_id}/assistant/history", headers=headers)
    assert history_resp.status_code == 200
    history = history_resp.json()
    assert len(history) == 4  # 2 questions + 2 answers

    # 6. Compare with another scan
    compare_resp = await client.post(
        f"/api/v1/scans/{scan_id}/compare",
        json={},
        headers=headers
    )
    assert compare_resp.status_code == 200
    comp = compare_resp.json()
    assert "comparison_explanation" in comp

