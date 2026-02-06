# Linkpoint Privacy Policy

**Last Updated: January 11, 2026**

## Overview

Linkpoint is a third-party viewer for accessing Second Life virtual worlds. This privacy policy explains our practices regarding the collection, storage, and use of user data.

## About Linkpoint

Linkpoint is developed and maintained by the Linkpoint open source project. This software is **not provided or supported by Linden Lab**, the makers of Second Life.

## Data Collection

### What We Collect

**Local Device Storage Only:**
- Login credentials (username, encrypted password if you choose to save them)
- Application preferences and settings
- Cache data (textures, meshes, sounds from Second Life)
- Network diagnostic logs (for troubleshooting)

### What We Do NOT Collect

- We do **not** transmit your credentials to any third-party servers
- We do **not** collect analytics or telemetry about your usage
- We do **not** track your location, contacts, or personal information
- We do **not** sell or share any user data with third parties

## Data Storage

### Credential Storage
If you choose to save your password:
- Your password is encrypted using Android Keystore (AES-256-GCM)
- Encrypted credentials are stored only on your device
- Credentials are never transmitted except to official Second Life/OpenSimulator login servers

### Cache Storage
- Cached assets (textures, meshes, sounds) are stored locally on your device
- Cache can be cleared at any time through the Settings menu
- Cache data is only used to improve loading performance

### Log Files
- Network diagnostic logs may be saved locally for troubleshooting
- Logs contain connection information, not message content
- Logs are stored only on your device and are not uploaded anywhere

## Data Transmission

### To Linden Lab
When you use Linkpoint to connect to Second Life, the following data is transmitted to Linden Lab's servers:
- Your login credentials (username and password hash)
- Device identifier (randomly generated, not your actual device ID)
- Viewer identification ("Linkpoint" channel name and version)
- Network information required for connection

This data transmission is governed by [Linden Lab's Privacy Policy](https://www.lindenlab.com/privacy).

### To OpenSimulator Grids
When connecting to OpenSimulator grids, similar data is transmitted to the grid operator. Please review the privacy policy of each grid you connect to.

## Your Rights

You have the right to:
- **Access**: View all data stored by Linkpoint on your device
- **Delete**: Clear all cached data, saved credentials, and logs at any time
- **Opt-out**: Choose not to save passwords or enable logging

## Children's Privacy

Linkpoint is not intended for use by children under 16 years of age. Users must agree to Linden Lab's Terms of Service, which require users to be at least 16 years old (18 for adult content).

## Third-Party Services

Linkpoint connects to:
- **Second Life** (Linden Lab) - Subject to [Linden Lab's Terms of Service](https://www.lindenlab.com/legal/second-life-terms-and-conditions) and [Privacy Policy](https://www.lindenlab.com/privacy)
- **OpenSimulator grids** (optional) - Subject to each grid's terms and privacy policy

## Security

We implement the following security measures:
- AES-256-GCM encryption for stored passwords using Android Keystore
- TLS/SSL for all network communications
- No storage of passwords on external servers
- Clear-text traffic is disabled by default

## Open Source

Linkpoint is open source software. You can review our source code at:
- [GitHub Repository](https://github.com/Kaleaon/Linkpoint)

## Contact

For privacy-related inquiries:
- Open an issue on our [GitHub repository](https://github.com/Kaleaon/Linkpoint/issues)
- This is a community-maintained open source project

## Changes to This Policy

We may update this privacy policy from time to time. Any changes will be reflected in the "Last Updated" date at the top of this document and in our GitHub repository.

## Compliance

This privacy policy is provided in compliance with the [Second Life Third-Party Viewer Policy](https://secondlife.com/corporate/third-party-viewers) Section 4 (Data Access and Privacy).

---

*Linkpoint is not affiliated with or endorsed by Linden Lab. Second Life is a trademark of Linden Lab.*
