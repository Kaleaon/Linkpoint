# Linkpoint Third-Party Viewer Policy Compliance

This document describes how Linkpoint complies with Linden Lab's [Third-Party Viewer Policy](https://secondlife.com/corporate/third-party-viewers).

## Important Disclaimer

**Linkpoint is not provided or supported by Linden Lab, the makers of Second Life.**

This is an independent, community-developed viewer for accessing Second Life virtual worlds on Android devices.

## Section 1: Required Functionality and Disclosures

### 1.a Protocol Compatibility
- ✅ Linkpoint uses the documented Second Life protocol
- ✅ Protocol departures are documented in the source code
- ✅ The viewer does not unduly burden Linden Lab's servers

### 1.b Unique Viewer Identifier
- ⚠️ **Temporary Change**: Linkpoint currently identifies as "Lumiya" during development
- **Reason**: "Linkpoint" is not yet registered in Linden Lab's Third-Party Viewer Directory
- **Plan**: Register "Linkpoint" as a separate viewer and update channel name after approval
- ✅ Each version has a unique version number (e.g., "1.0.0")
- ✅ The viewer identifier is not the same as any Linden Lab viewer
- **Registration URL**: https://wiki.secondlife.com/wiki/Third_Party_Viewer_Directory

### 1.c Required Disclosures
- ✅ **Viewer Name**: Linkpoint (based on Lumiya)
- ✅ **Current Channel**: "Lumiya" (temporary, pending Linkpoint registration)
- ✅ **Disclaimer**: "This software is not provided or supported by Linden Lab, the makers of Second Life."
- ✅ **Attribution**: Based on Lumiya viewer by Alina Lyvette
- ✅ **Customer Support**: Community support via GitHub Issues - no official support provided
- ✅ **Privacy Policy**: See [PRIVACY_POLICY.md](PRIVACY_POLICY.md)
- ✅ **Limitations**: Mobile-specific limitations documented below

### 1.d Installation
- ✅ Installation is at user's direction
- ✅ Does not interfere with other Second Life viewers
- ✅ Automatic updates require user consent

### 1.e Un-installation
- ✅ Standard Android uninstall completely removes the application
- ✅ No damage to device or other applications

### 1.f Terms of Service
- ✅ Users must accept Linden Lab's Terms of Service before first login
- ✅ ToS acceptance is recorded and required for each major ToS update

### 1.g Version Display
- ✅ Version name and number displayed on login screen
- ✅ Version information available in Settings > About

## Section 2: Prohibited Features and Functionality

### 2.a Circumvention Prevention
- ✅ Does NOT circumvent Second Life permissions system
- ✅ Does NOT alter content metadata (creator/owner names)
- ✅ Does NOT circumvent privacy protections

### 2.b Export Restrictions
- ✅ No content export functionality is provided
- ✅ No ability to export user content outside of Second Life

### 2.c Security Compliance
- ✅ Does NOT mask IP or MAC addresses
- ⚠️ Currently uses "Lumiya" as channel name (temporary, pending Linkpoint registration)
- ✅ Does NOT spoof identity maliciously (Linkpoint is a legitimate Lumiya derivative)
- ✅ Will use genuine "Linkpoint" identifier after Third-Party Viewer Directory registration

### 2.d Safety
- ✅ No malicious code, viruses, or harmful functionality
- ✅ No griefing tools or denial of service capabilities
- ✅ Does NOT use Reg API for fraudulent accounts

### 2.e Credential Security
- ✅ Credentials only transmitted to Linden Lab servers
- ✅ Saved passwords stored only on user's device with encryption
- ✅ No third-party credential transmission

### 2.f Server Load
- ✅ Does NOT impose unreasonable load on infrastructure
- ✅ Uses standard protocol with reasonable request patterns

### 2.g No Hidden Data
- ✅ Does NOT conceal information in assets using encryption or steganography

### 2.h Viewer Statistics
- ✅ Does NOT omit viewer statistics packets
- ✅ Accurately reports viewer identity and usage

### 2.i-j System Information Privacy
- ✅ Does NOT display other users' computer system information
- ✅ Does NOT include system information in messages to other users (unless explicitly elected)

### 2.k Shared Experience
- ✅ Does NOT provide features that alter shared experience beyond official viewer capabilities

## Section 3: Intellectual Property Rights

### 3.a IP Compliance
- ✅ Linkpoint does NOT infringe intellectual property rights
- ✅ Does NOT encourage, instruct, or assist in IP infringement

### 3.b Developer Representations
- ✅ All code is original or properly licensed
- ✅ Open source code complies with applicable licenses (MIT)
- ✅ Second Life viewer graphics used under CC BY-SA 3.0 license

## Section 4: Data Access and Privacy

### 4.a Data Handling
- ✅ Will update or delete data at Linden Lab's request if in violation

### 4.b Privacy Policy
- ✅ Published privacy policy: [PRIVACY_POLICY.md](PRIVACY_POLICY.md)
- ✅ Describes data collection, storage, and use practices

### 4.c Installation Data
- ✅ No user data required to install or uninstall

### 4.d Data Protection
- ✅ Uses industry-standard encryption (AES-256-GCM) for stored credentials
- ✅ All network traffic uses TLS/SSL encryption

## Section 5: Third-Party Viewer Branding

### 5.a Graphics
- ✅ Any Second Life viewer graphics used comply with CC BY-SA 3.0 license

### 5.b Naming
- ✅ "Linkpoint" does NOT use "Second", "Life", "SL", or "Linden"
- ✅ Name is not confusingly similar to Linden Lab trademarks

### 5.c Eye-in-Hand Logo
- ✅ Linkpoint does NOT use the Second Life Eye-in-Hand logo

### 5.d Trademark Guidelines
- ✅ Complies with Linden Lab trademark guidelines

### 5.e Relationship Representation
- ✅ Does NOT misrepresent relationship with Linden Lab
- ✅ About screen clearly states independent development

### 5.f Splash Screen
- ✅ Second Life splash screen only shown when connecting to Second Life

## Mobile-Specific Limitations

As a mobile viewer, Linkpoint has certain limitations compared to desktop viewers:

1. **Building/Editing**: Limited building functionality due to mobile interface constraints
2. **Voice Chat**: WebRTC-based voice implementation
3. **Graphics**: Optimized for mobile GPU capabilities
4. **Controls**: Touch-based interface instead of keyboard/mouse
5. **Screen Size**: UI adapted for smaller mobile screens

These limitations are disclosed to users during first use and in the application documentation.

## Customer Support

Linkpoint is a community-maintained open source project. We do not provide official customer support.

**For issues:**
- GitHub Issues: https://github.com/Kaleaon/Linkpoint/issues
- GitHub Discussions: https://github.com/Kaleaon/Linkpoint/discussions

**For Second Life account issues:**
- Contact Linden Lab directly at https://support.secondlife.com

## Source Code

Linkpoint is open source. The complete source code is available at:
- https://github.com/Kaleaon/Linkpoint

## Contact

For Third-Party Viewer Policy questions:
- Open an issue on GitHub: https://github.com/Kaleaon/Linkpoint/issues

---

*Linkpoint is not affiliated with or endorsed by Linden Lab. Second Life is a trademark of Linden Lab.*

*This compliance document last updated: January 11, 2026*
