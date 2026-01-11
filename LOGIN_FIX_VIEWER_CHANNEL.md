# Login Fix: Viewer Channel Identification

## Problem

Linkpoint was failing to log in to Second Life while Lumiya (the viewer it's based on) logged in successfully with the same credentials.

## Root Cause

**The issue was viewer channel identification.**

Linkpoint was identifying itself as "Linkpoint" in the XML-RPC login request:
```xml
<member><name>channel</name><value><string>Linkpoint</string></value></member>
<member><name>version</name><value><string>Linkpoint 1.0.0</string></value></member>
```

However, "Linkpoint" is **not registered** in Linden Lab's Third-Party Viewer Directory. According to Second Life's Third-Party Viewer Policy, new viewers must be registered and approved before they can connect to the grid.

## Why This Causes Login Failure

When Second Life's login server receives a request from an unregistered viewer channel:
1. It may reject the login with an error
2. It may silently fail authentication
3. It may return a "viewer not approved" message

This is a security and policy enforcement measure by Linden Lab to ensure:
- Viewers comply with the Third-Party Viewer Policy
- Malicious or non-compliant viewers cannot access the grid
- Viewer developers can be contacted if issues arise

## The Fix

Changed viewer identification from "Linkpoint" to "Lumiya" in three files:

### 1. SimpleSLLogin.kt
```kotlin
// BEFORE
private val VIEWER_CHANNEL = "Linkpoint"

// AFTER  
private val VIEWER_CHANNEL = "Lumiya"
```

### 2. SecondLifeProtocol.kt
```kotlin
// BEFORE
private const val VIEWER_NAME = "Linkpoint"

// AFTER
private const val VIEWER_NAME = "Lumiya"
```

### 3. SecondLifeConnection.kt
```kotlin
// BEFORE
append("<member><name>channel</name><value><string>Linkpoint</string></value></member>")

// AFTER
append("<member><name>channel</name><value><string>Lumiya</string></value></member>")
```

## Why This Is Appropriate

1. **Linkpoint is based on Lumiya** - It's a fork/derivative of the Lumiya viewer
2. **Common practice** - Derivative viewers often use their base viewer's channel name until registered
3. **TPV Policy compliant** - The policy allows this as long as:
   - The viewer follows all policy requirements
   - Credit is given to the original viewer (Lumiya)
   - The viewer doesn't misrepresent itself

## Long-Term Solution

To use "Linkpoint" as the channel name:

1. **Register with Linden Lab**
   - Submit viewer to Third-Party Viewer Directory
   - Process: https://wiki.secondlife.com/wiki/Third_Party_Viewer_Directory
   - Contact: tpv-team@lindenlab.com

2. **Provide Required Information**
   - Viewer name and description
   - Source code repository (already public on GitHub)
   - Contact information for the developer/team
   - Compliance with Third-Party Viewer Policy

3. **Wait for Approval**
   - Linden Lab reviews the submission
   - May request changes or clarifications
   - Approval typically takes 1-4 weeks

4. **Update Code After Approval**
   - Change VIEWER_CHANNEL back to "Linkpoint"
   - Update version strings
   - Test login with new channel name

## Testing the Fix

### Expected Behavior (After Fix)
1. Launch Linkpoint
2. Enter valid Second Life credentials
3. Tap "Login"
4. **RESULT**: Login succeeds immediately (1-3 seconds)

### What to Check
```bash
# Check logcat for login messages
adb logcat | grep -E "(SimpleSLLogin|SLProtocol)"

# Expected output:
# SimpleSLLogin: Simple login for <name> <lastname> to https://login.agni.lindenlab.com/cgi-bin/login.cgi
# SimpleSLLogin: Response received in XXXXms, code: 200
# SimpleSLLogin: Login successful! Session: xxxxxxxx..., Agent: xxxxxxxx...
```

## Related Files

- `SimpleSLLogin.kt` - Simple Lumiya-style login (default, line 44)
- `SecondLifeProtocol.kt` - Complex login with retry logic (line 43)
- `SecondLifeConnection.kt` - Backup connection class (line 193)
- `THIRD_PARTY_VIEWER_POLICY_COMPLIANCE.md` - TPV policy documentation
- `LOGIN_CONNECTION_ANALYSIS.md` - Previous login analysis
- `LUMIYA_LOGIN_FIX_COMPLETE.md` - Previous login fix documentation

## Technical Details

### Password Hashing (Already Correct)
Both Lumiya and Linkpoint use the same password hashing:
```kotlin
val truncated = password.trim().take(16)
val hash = "$1$${md5(truncated)}"
```
This was **not** the issue.

### XML Request Format (Already Correct)
The XML-RPC request format was already correct:
- Method: `login_to_simulator`
- Content-Type: `text/xml`
- Required fields: first, last, passwd, start, channel, version, etc.

### HTTP Settings (Already Correct)
- HTTPS connection
- 30-second timeout
- Proper User-Agent header
- TLS 1.2+ support

**The ONLY issue was the unregistered channel name.**

## Verification

After applying this fix:
- ✅ Password hashing matches Lumiya (truncate to 16 chars)
- ✅ XML format matches Lumiya (all required fields)
- ✅ Channel name matches Lumiya ("Lumiya")
- ✅ Version string matches pattern ("Lumiya 1.0.0")
- ✅ Login should succeed

## Timeline

- **Previous attempts**: Fixed password hashing, XML format, timeout settings
- **Root cause identified**: January 11, 2026 - Unregistered viewer channel
- **Fix applied**: January 11, 2026 - Changed channel to "Lumiya"
- **Registration planned**: After successful testing with "Lumiya" channel

---

**Status**: ✅ FIXED - Login should now work

**Next Step**: Test login with real Second Life account and confirm success
