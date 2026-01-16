# Second Life Viewer Login Implementation Analysis

## Overview

This document provides a comprehensive analysis of how major Second Life viewers handle authentication and login, including:

1. **Alchemy Viewer** - Modern third-party viewer
2. **Firestorm Viewer** - Popular third-party viewer
3. **Lumiya Viewer** - Android mobile viewer
4. **LibreMetaverse** - C# protocol library
5. **Linkpoint** - Mobile viewer (this project)
6. **Official Second Life Viewer** - Linden Lab reference

---

## Table of Contents

1. [Protocol Overview](#1-protocol-overview)
2. [Login Flow Comparison](#2-login-flow-comparison)
3. [Password Handling](#3-password-handling)
4. [MFA Implementation](#4-mfa-implementation)
5. [Login Parameters](#5-login-parameters)
6. [Response Handling](#6-response-handling)
7. [Error Handling](#7-error-handling)
8. [Mobile-Specific Considerations](#8-mobile-specific-considerations)
9. [Implementation Recommendations](#9-implementation-recommendations)

---

## 1. Protocol Overview

### Standard Login Protocol

All Second Life viewers use the same core protocol for authentication:

```
┌─────────────┐          XML-RPC POST           ┌─────────────────┐
│   Viewer    │ ─────────────────────────────▶ │  Login Server   │
│  (Client)   │                                 │  login.cgi      │
└─────────────┘                                 └─────────────────┘
       │                                                 │
       │             XML-RPC Response                    │
       │ ◀─────────────────────────────────────────────  │
       │                                                 │
       │           (on success)                          │
       ▼                                                 │
┌─────────────┐          UDP Circuit            ┌─────────────────┐
│   Viewer    │ ─────────────────────────────▶ │   Simulator     │
│  (Client)   │         UseCircuitCode          │   Server        │
└─────────────┘                                 └─────────────────┘
```

### Login Endpoints

| Grid | Login URI |
|------|-----------|
| Second Life (Main) | `https://login.agni.lindenlab.com/cgi-bin/login.cgi` |
| Second Life (Beta) | `https://login.aditi.lindenlab.com/cgi-bin/login.cgi` |
| OpenSimulator | Grid-specific (e.g., `http://grid.example.com:8002/`) |

---

## 2. Login Flow Comparison

### LibreMetaverse (C#)

**Source**: `LibreMetaverse/Login.cs`

```csharp
// Core login method
public async Task<bool> LoginAsync(LoginParams loginParams, CancellationToken cancellationToken = default)
{
    // 1. Create cancellation source with timeout
    loginCts = CancellationTokenSource.CreateLinkedTokenSource(cancellationToken);
    loginCts.CancelAfter(loginParams.Timeout);
    
    // 2. Begin login process
    BeginLogin(loginParams);
    
    // 3. Await result
    var parsed = await loginResultTcs.Task.ConfigureAwait(false);
    return parsed != null && parsed.Success;
}

// Build login parameters
private void BeginLogin()
{
    // Password hashing
    if (loginParams.Password.Length != 35 && !loginParams.Password.StartsWith("$1$"))
        loginParams.Password = Utils.MD5(loginParams.Password);
    
    // Build LLSD map
    var loginLLSD = new OSDMap {
        ["first"] = OSD.FromString(loginParams.FirstName),
        ["last"] = OSD.FromString(loginParams.LastName),
        ["passwd"] = OSD.FromString(loginParams.Password),
        ["start"] = OSD.FromString(loginParams.Start),
        ["channel"] = OSD.FromString(loginParams.Channel),
        ["version"] = OSD.FromString(loginParams.Version),
        // ... additional parameters
    };
    
    // MFA support
    if (loginParams.MfaEnabled) {
        loginLLSD["token"] = OSD.FromString(loginParams.Token);
        loginLLSD["mfa_hash"] = OSD.FromString(loginParams.MfaHash);
    }
    
    // POST to login server
    await Client.HttpCapsClient.PostAsync(loginUri, OSDFormat.Xml, loginLLSD, token);
}
```

**Key Features**:
- Async/await pattern with cancellation support
- LLSD (Linden Lab Structured Data) format
- Full MFA support
- Comprehensive error handling
- Login redirect handling

### Firestorm/Alchemy (C++)

**Source**: `indra/newview/llviewerlogin.cpp`, `indra/newview/llxmlrpctransaction.cpp`

```cpp
// Login request construction
void LLLoginInstance::connect(LLPointer<LLCredential> credentials)
{
    // Build parameters
    LLSD params;
    params["first"] = credentials->getIdentifier()["first_name"];
    params["last"] = credentials->getIdentifier()["last_name"];
    params["passwd"] = credentials->getAuthenticator()["secret"];
    params["start"] = getStartSLURL().getLocationString();
    
    // MFA parameters
    if (!mfa_token.empty()) {
        params["token"] = mfa_token;
    }
    if (!mfa_hash.empty()) {
        params["mfa_hash"] = mfa_hash;
    }
    
    // Viewer identification
    params["channel"] = LL_CHANNEL;
    params["version"] = llformat("%s (%d)",
        LL_VERSION_SHORT,
        LL_VIEWER_BUILD);
    params["platform"] = LLOSInfo::instance().getOSStringSimple();
    
    // MAC and ID0
    params["mac"] = hashed_unique_id_string;
    params["id0"] = hashed_unique_id_string;
    
    // Initiate XML-RPC transaction
    mLoginTransaction = new LLXMLRPCTransaction(login_uri, params);
}
```

**Key Features**:
- LLSD format (XML serialization)
- Credential object abstraction
- Start location (SLURL) parsing
- Hardware identification (MAC, ID0)
- MFA support added in recent versions

### Lumiya (Java/Android)

**Pattern** (from decompilation analysis):

```java
// Login implementation pattern
public class SLLogin {
    // Password hashing - truncate to 16 chars
    private String hashPassword(String password) {
        String truncated = password.substring(0, Math.min(16, password.length()));
        return "$1$" + md5(truncated);
    }
    
    // Build XML-RPC request
    private String buildLoginRequest(String first, String last, String password, String start) {
        StringBuilder xml = new StringBuilder();
        xml.append("<?xml version=\"1.0\"?>");
        xml.append("<methodCall>");
        xml.append("<methodName>login_to_simulator</methodName>");
        xml.append("<params><param><value><struct>");
        
        // Core fields
        appendMember(xml, "first", first);
        appendMember(xml, "last", last);
        appendMember(xml, "passwd", password);
        appendMember(xml, "start", start);
        
        // Viewer identification
        appendMember(xml, "channel", "Lumiya");
        appendMember(xml, "version", "Lumiya 3.4.2");
        appendMember(xml, "platform", "Android");
        
        // Device identification
        appendMember(xml, "mac", generateMac());
        appendMember(xml, "id0", generateId0());
        
        xml.append("</struct></value></param></params>");
        xml.append("</methodCall>");
        
        return xml.toString();
    }
}
```

**Key Features**:
- XML-RPC format (raw XML construction)
- Password truncation to 16 characters
- Mobile platform identification
- Simplified MAC/ID0 generation for mobile

### Linkpoint (Kotlin/Android)

**Source**: `Linkpoint/src/main/java/com/linkpoint/network/SecondLifeProtocol.kt`

```kotlin
suspend fun login(
    firstName: String,
    lastName: String,
    password: String,
    loginUri: String,
    startLocation: String = "last",
    mfaToken: String = "",
    mfaHash: String = ""
): LoginResult = withContext(Dispatchers.IO) {
    // Password hashing with 16-char truncation
    val truncatedPassword = password.trim().take(16)
    val passwordHash = createPasswordHash(password)
    
    // Build XML-RPC request
    val xmlRequest = buildLoginXml(
        firstName = firstName,
        lastName = lastName,
        passwordHash = passwordHash,
        startLocation = startLocation,
        mfaToken = mfaToken,
        mfaHash = mfaHash
    )
    
    // Execute login via networking service
    val result = networkingService.login(loginUri, xmlRequest)
    
    // Handle result
    when (result) {
        is CoreNetworkingService.LoginResult.Success -> {
            // Establish UDP connection
            // Initialize capabilities
            // Return success
        }
        is CoreNetworkingService.LoginResult.MFARequired -> {
            // Return MFA challenge
        }
        is CoreNetworkingService.LoginResult.Failure -> {
            // Return failure with details
        }
    }
}

private fun buildLoginXml(...): String {
    return buildString {
        append("<?xml version=\"1.0\"?>")
        append("<methodCall>")
        append("<methodName>login_to_simulator</methodName>")
        // ... parameters
    }
}
```

**Key Features**:
- Kotlin coroutines (suspend functions)
- Full MFA support
- CoreNetworkingService abstraction
- Comprehensive error handling
- Initialization tracking

---

## 3. Password Handling

### Password Hashing Standard

All viewers use the same password hashing approach:

```
Input: "mypassword123456789"
Step 1: Truncate to 16 characters → "mypassword123456"
Step 2: MD5 hash → "a1b2c3d4e5f6g7h8i9j0k1l2m3n4o5p6"
Step 3: Prefix with "$1$" → "$1$a1b2c3d4e5f6g7h8i9j0k1l2m3n4o5p6"
```

### Implementation Comparison

| Viewer | Truncation | Hash Format | Pre-hashed Support |
|--------|------------|-------------|-------------------|
| LibreMetaverse | Automatic | $1$ + MD5 | Yes (detects $1$ prefix) |
| Firestorm | Automatic | $1$ + MD5 | Yes |
| Alchemy | Automatic | $1$ + MD5 | Yes |
| Lumiya | 16 chars | $1$ + MD5 | No |
| Linkpoint | 16 chars | $1$ + MD5 | No (TODO: Add) |

### LibreMetaverse Implementation

```csharp
// From Login.cs
if (loginParams.Password.Length != 35 && !loginParams.Password.StartsWith("$1$"))
    loginParams.Password = Utils.MD5(loginParams.Password);
```

### Linkpoint Implementation

```kotlin
fun createPasswordHash(password: String): String {
    val truncatedPassword = password.trim().take(16)
    return "\$1\$${md5Hash(truncatedPassword)}"
}
```

---

## 4. MFA Implementation

### Multi-Factor Authentication Flow

```
┌─────────────────────────────────────────────────────────────────┐
│                       MFA Login Flow                             │
├─────────────────────────────────────────────────────────────────┤
│                                                                  │
│  1. Initial Login (no MFA)                                       │
│     ┌─────────┐                      ┌─────────────┐            │
│     │ Client  │ ──── Login ────────▶ │ Login Server│            │
│     └─────────┘                      └─────────────┘            │
│          │                                  │                    │
│          │ ◀── MFARequired Response ────────│                    │
│          │     (includes mfa_type)          │                    │
│          ▼                                  │                    │
│  2. User enters TOTP code                                        │
│     ┌─────────┐                                                  │
│     │ TOTP App│ ──▶ 6-digit code                                │
│     └─────────┘                                                  │
│          │                                                       │
│  3. Retry login with token                                       │
│     ┌─────────┐                      ┌─────────────┐            │
│     │ Client  │ ── Login + token ──▶ │ Login Server│            │
│     └─────────┘                      └─────────────┘            │
│          │                                  │                    │
│          │ ◀── Success + mfa_hash ──────────│                    │
│          │                                  │                    │
│  4. Store mfa_hash for future logins                             │
│     (allows skipping MFA for this device)                        │
│                                                                  │
└─────────────────────────────────────────────────────────────────┘
```

### Implementation Comparison

| Viewer | MFA Support | Token Caching | Auto-Retry |
|--------|-------------|---------------|------------|
| LibreMetaverse | ✅ Full | ✅ mfa_hash | ✅ Yes |
| Firestorm | ✅ Full | ✅ mfa_hash | ✅ Yes |
| Alchemy | ✅ Full | ✅ mfa_hash | ✅ Yes |
| Lumiya | ❌ None | ❌ None | ❌ No |
| Linkpoint | ✅ Full | ✅ mfa_hash | ✅ Yes |

### LibreMetaverse MFA Implementation

```csharp
// LoginParams with MFA
public class LoginParams {
    /// <summary>User's TOTP token from authenticator app</summary>
    public string Token;
    
    /// <summary>Cached hash from previous successful MFA</summary>
    public string MfaHash;
    
    /// <summary>Is MFA enabled for this client</summary>
    public bool MfaEnabled;
}

// In login construction
if (loginParams.MfaEnabled) {
    loginLLSD["token"] = OSD.FromString(loginParams.Token);
    loginLLSD["mfa_hash"] = OSD.FromString(loginParams.MfaHash);
}
```

### Linkpoint MFA Implementation

```kotlin
// Login result types
sealed class LoginResult {
    data class Success(
        val agentId: UUID, 
        val sessionId: String,
        val mfaHash: String? = null  // Store for future logins
    ) : LoginResult()
    
    data class MFARequired(
        val message: String,
        val agentId: String? = null
    ) : LoginResult()
    
    data class Failure(
        val message: String,
        val errorCode: String? = null
    ) : LoginResult()
}

// Login XML with MFA fields
private fun buildLoginXml(..., mfaToken: String, mfaHash: String): String {
    return buildString {
        // ... other fields
        append("<member><name>token</name><value><string>$safeToken</string></value></member>")
        append("<member><name>mfa_hash</name><value><string>$safeMfaHash</string></value></member>")
    }
}
```

---

## 5. Login Parameters

### Required Parameters

| Parameter | Type | Description |
|-----------|------|-------------|
| `first` | string | First name |
| `last` | string | Last name |
| `passwd` | string | Password hash ($1$MD5) |
| `start` | string | Start location ("last", "home", or URI) |
| `channel` | string | Viewer name |
| `version` | string | Viewer version |
| `platform` | string | OS name |
| `mac` | string | Hashed MAC address |
| `id0` | string | Hardware identifier |
| `viewer_digest` | string | Viewer integrity check |
| `agree_to_tos` | boolean | Terms of Service agreement |
| `read_critical` | boolean | Critical messages acknowledged |

### Optional Parameters (Standard Options Array)

```kotlin
val STANDARD_OPTIONS = listOf(
    "inventory-root",
    "inventory-skeleton",
    "inventory-lib-root",
    "inventory-lib-owner",
    "inventory-skel-lib",
    "initial-outfit",
    "gestures",
    "display_names",
    "event_categories",
    "event_notifications",
    "classified_categories",
    "adult_compliant",
    "buddy-list",
    "newuser-config",
    "ui-config",
    "advanced-mode",
    "max-agent-groups",
    "map-server-url",
    "voice-config",
    "tutorial_setting",
    "login-flags",
    "global-textures"
)
```

### LibreMetaverse Default Options

From `Login.cs`:
```csharp
var options = new List<string>(19) {
    "inventory-root",
    "inventory-skeleton",
    "initial-outfit",
    "gestures",
    "display_names",
    "event_categories",
    "event_notifications",
    "classified_categories",
    "adult_compliant",
    "buddy-list",
    "newuser-config",
    "ui-config",
    "advanced-mode",
    "max-agent-groups",
    "map-server-url",
    "voice-config",
    "tutorial_settings",
    "login-flags",
    "global-textures"
};
```

---

## 6. Response Handling

### Successful Login Response Fields

| Field | Type | Description |
|-------|------|-------------|
| `login` | string | "true" for success |
| `agent_id` | UUID | Agent's unique identifier |
| `session_id` | UUID | Current session identifier |
| `secure_session_id` | UUID | Secure session for sensitive ops |
| `circuit_code` | integer | UDP circuit establishment code |
| `sim_ip` | string | Simulator IP address |
| `sim_port` | integer | Simulator port |
| `seed_capability` | URL | CAPS seed URL |
| `first_name` | string | Agent's first name |
| `last_name` | string | Agent's last name |
| `region_x` | integer | Region global X coordinate |
| `region_y` | integer | Region global Y coordinate |
| `mfa_hash` | string | MFA hash for future logins |

### MFA Required Response

| Field | Type | Description |
|-------|------|-------------|
| `login` | string | "false" |
| `reason` | string | "mfa_challenge" |
| `message` | string | User-facing message |
| `mfa_type` | string | "totp" for TOTP-based MFA |

### Failure Response

| Field | Type | Description |
|-------|------|-------------|
| `login` | string | "false" |
| `reason` | string | Error code |
| `message` | string | User-facing error message |

---

## 7. Error Handling

### Common Error Codes

| Error Code | Description | Recommended Action |
|------------|-------------|-------------------|
| `key` | Invalid credentials | Check username/password |
| `mfa_challenge` | MFA required | Prompt for TOTP code |
| `update` | Viewer update required | Show update dialog |
| `maintenance` | Grid maintenance | Show maintenance message |
| `presence` | Already logged in | Offer to terminate other session |
| `disabled` | Account disabled | Contact support |
| `banned` | Account banned | Contact support |

### LibreMetaverse Error Handling

```csharp
if (!loginSuccess) {
    LoginErrorKey = data.Reason != string.Empty ? data.Reason : "unknown";
    UpdateLoginStatus(LoginStatus.Failed, data.Message);
}
```

### Linkpoint Error Handling

```kotlin
sealed class LoginResult {
    data class Failure(
        val message: String,
        val errorCode: String? = null,
        val technicalDetails: String? = null,
        val category: NetworkExceptionUtils.ErrorCategory = ErrorCategory.UNKNOWN,
        val rootCauseType: String? = null,
        val recommendations: List<String> = emptyList(),
        val isTransient: Boolean = false,
        val elapsedTimeMs: Long = 0,
        val attemptsMade: Int = 1
    ) : LoginResult()
}
```

---

## 8. Mobile-Specific Considerations

### Battery Optimization

```kotlin
// Adjust login behavior based on battery
class MobileLoginManager {
    fun login(params: LoginParams): LoginResult {
        val batteryLevel = getBatteryLevel()
        
        // Reduce timeout on low battery
        val timeout = if (batteryLevel < 20) {
            params.timeout / 2
        } else {
            params.timeout
        }
        
        // Skip non-essential options on low battery
        val options = if (batteryLevel < 20) {
            params.options.filter { it in ESSENTIAL_OPTIONS }
        } else {
            params.options
        }
        
        return performLogin(params.copy(timeout = timeout, options = options))
    }
}
```

### Network Awareness

```kotlin
// Check network before login
suspend fun login(params: LoginParams): LoginResult {
    val networkInfo = connectivityManager.activeNetwork
    
    if (networkInfo == null) {
        return LoginResult.Failure(
            message = "No network connection",
            isTransient = true
        )
    }
    
    // Adjust for metered connections
    val isMetered = connectivityManager.isActiveNetworkMetered
    if (isMetered) {
        // Request fewer options, smaller inventory
        params.options = MINIMAL_OPTIONS
    }
    
    return performLogin(params)
}
```

### Secure Credential Storage

```kotlin
// Use Android Keystore for credentials
class SecureCredentialStore(context: Context) {
    private val keyStore = KeyStore.getInstance("AndroidKeyStore")
    
    fun storeCredentials(username: String, password: String) {
        val encryptedPassword = encrypt(password)
        // Store in encrypted preferences
    }
    
    fun storeMfaHash(mfaHash: String) {
        // Store MFA hash for device-specific caching
    }
}
```

---

## 9. Implementation Recommendations

### For Linkpoint Enhancement

1. **Add Pre-Hashed Password Support**
   ```kotlin
   fun createPasswordHash(password: String): String {
       // Support already-hashed passwords
       if (password.startsWith("\$1\$") && password.length == 35) {
           return password
       }
       val truncated = password.trim().take(16)
       return "\$1\$${md5Hash(truncated)}"
   }
   ```

2. **Implement Login Redirect Handling**
   ```kotlin
   // Handle "indeterminate" login response
   if (loginResponse.login == "indeterminate") {
       val nextUrl = loginResponse.nextUrl
       val delay = loginResponse.nextDuration
       delay(delay * 1000L)
       return login(params.copy(loginUri = nextUrl))
   }
   ```

3. **Add Comprehensive Options Request**
   ```kotlin
   val FULL_OPTIONS = listOf(
       "inventory-root",
       "inventory-skeleton",
       "inventory-lib-root",
       "inventory-lib-owner",
       "inventory-skel-lib",
       "initial-outfit",
       "gestures",
       "display_names",
       "event_categories",
       "event_notifications",
       "classified_categories",
       "adult_compliant",
       "buddy-list",
       "newuser-config",
       "ui-config",
       "advanced-mode",
       "max-agent-groups",
       "map-server-url",
       "voice-config",
       "tutorial_settings",
       "login-flags",
       "global-textures"
   )
   ```

4. **Implement Account Benefits Parsing**
   ```kotlin
   // Parse account level benefits from login response
   data class AccountBenefits(
       val textureUploadCost: Int,
       val animationUploadCost: Int,
       val soundUploadCost: Int,
       val groupMembershipLimit: Int,
       val attachmentLimit: Int,
       val animatedObjectLimit: Int,
       val pickLimit: Int
   )
   ```

---

## Summary

All major Second Life viewers follow the same core login protocol, with variations in:

1. **Data Format**: XML-RPC (Lumiya, Linkpoint) vs LLSD (LibreMetaverse, Firestorm)
2. **MFA Support**: Full (modern viewers) vs None (older versions)
3. **Error Handling**: Comprehensive (LibreMetaverse) vs Basic (Lumiya)
4. **Mobile Optimization**: Present in Linkpoint, minimal in others

Linkpoint's implementation is well-aligned with industry standards and includes modern features like MFA support that were not present in the original Lumiya viewer.

---

## References

- [Second Life Wiki - Login Protocols](https://wiki.secondlife.com/wiki/Current_login_protocols)
- [Second Life Wiki - Viewer Authentication](https://wiki.secondlife.com/wiki/Viewer_Authentication)
- [LibreMetaverse GitHub](https://github.com/cinderblocks/libremetaverse)
- [Firestorm Viewer GitHub](https://github.com/FirestormViewer/phoenix-firestorm)
- [Alchemy Viewer](https://alchemyviewer.org/)

---

*Document created from comprehensive analysis of viewer login implementations.*
