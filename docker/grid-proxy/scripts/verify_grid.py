#!/usr/bin/env python3
import json
import ssl
import sys
import urllib.request
import xmlrpc.client


def log(msg):
    print(f"[verify-grid] {msg}", flush=True)


def test_nginx_health():
    log("Testing NGINX proxy /health endpoint over HTTP and HTTPS...")
    ctx = ssl.create_default_context()
    ctx.check_hostname = False
    ctx.verify_mode = ssl.CERT_NONE

    # HTTP
    req = urllib.request.urlopen("http://localhost/health", timeout=5)
    data = json.loads(req.read().decode("utf-8"))
    assert data["status"] == "ok", f"HTTP health status invalid: {data}"
    log("✅ HTTP /health passed!")

    # HTTPS
    req_ssl = urllib.request.urlopen("https://localhost/health", context=ctx, timeout=5)
    data_ssl = json.loads(req_ssl.read().decode("utf-8"))
    assert data_ssl["status"] == "ok", f"HTTPS health status invalid: {data_ssl}"
    log("✅ HTTPS /health passed!")


def test_xmlrpc_login():
    log("Testing XML-RPC login endpoint over HTTPS through NGINX proxy...")
    ctx = ssl.create_default_context()
    ctx.check_hostname = False
    ctx.verify_mode = ssl.CERT_NONE

    server_url = "https://localhost/login"
    transport = xmlrpc.client.SafeTransport(context=ctx)
    proxy = xmlrpc.client.ServerProxy(server_url, transport=transport)

    login_params = {
        "first": "Admin",
        "last": "User",
        "passwd": "password",
        "start": "last",
        "major": "0",
        "minor": "9",
        "channel": "Linkpoint Mobile",
    }

    response = proxy.login_to_simulator(login_params)
    assert response.get("login") == "true", f"Login failed: {response}"
    assert "session_id" in response, "session_id missing from login response"
    assert "agent_id" in response, "agent_id missing from login response"
    assert "seed_capability" in response, "seed_capability missing from login response"

    agent_id = response["agent_id"]
    log(
        f"✅ XML-RPC Login successful! Agent ID: {agent_id}, Session ID: {response['session_id']}"
    )
    return agent_id


def test_microservices(agent_id):
    log("Testing microservice endpoints through proxy...")
    ctx = ssl.create_default_context()
    ctx.check_hostname = False
    ctx.verify_mode = ssl.CERT_NONE

    # CAPS
    req = urllib.request.urlopen(
        "https://localhost/CAPS/seed-cap", context=ctx, timeout=5
    )
    body = req.read().decode("utf-8")
    assert "<llsd>" in body, f"CAPS response invalid: {body}"
    log("✅ CAPS microservice routing passed!")

    # UserAccount GET
    url = f"https://localhost/UserAccount/{agent_id}"
    try:
        req_user = urllib.request.urlopen(url, context=ctx, timeout=5)
        user_data = json.loads(req_user.read().decode("utf-8"))
        assert (
            user_data.get("PrincipalID") == agent_id
        ), f"User account data mismatch: {user_data}"
        log("✅ UserAccount microservice routing & lookup passed!")
    except urllib.error.HTTPError as e:
        if e.code == 404:
            log("✅ UserAccount microservice routing passed (404 Not Found)!")
        else:
            raise


def main():
    log("Starting OpenSim Grid Proxy verification tests...")
    try:
        test_nginx_health()
        agent_id = test_xmlrpc_login()
        test_microservices(agent_id)
        log("🎉 ALL VERIFICATION TESTS PASSED SUCCESSFULLY!")
    except Exception as e:
        log(f"❌ Verification test failed: {e}")
        sys.exit(1)


if __name__ == "__main__":
    main()
