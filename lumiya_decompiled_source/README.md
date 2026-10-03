# Lumiya Viewer Decompiled Source

This directory contains decompiled source code from the Lumiya Second Life Viewer APK (v3.7 from May 2019).

## Purpose

This code is provided for **educational and reference purposes only** to understand how Lumiya implements:

- UDP Circuit Communication (`slproto/SLCircuit.java`, `slproto/SLConnection.java`)
- Message Handling (`slproto/SLMessage.java`, `slproto/messages/`)
- Second Life Protocol (`slproto/SLAgentCircuit.java`)
- Mobile Network Compatibility

## Key Files for Protocol Understanding

### UDP/Circuit Communication
- `com/lumiyaviewer/lumiya/slproto/SLCircuit.java` - **CRITICAL**: Uses `DatagramChannel` with `connect()` for mobile network support
- `com/lumiyaviewer/lumiya/slproto/SLConnection.java` - Main connection handler with NIO Selector
- `com/lumiyaviewer/lumiya/slproto/SLAgentCircuit.java` - Agent circuit with message handlers

### Message Handling
- `com/lumiyaviewer/lumiya/slproto/SLMessage.java` - Base message class with pack/unpack
- `com/lumiyaviewer/lumiya/slproto/messages/UseCircuitCode.java` - Circuit establishment message
- `com/lumiyaviewer/lumiya/slproto/messages/RegionHandshakeReply.java` - Region handshake response

### Key Findings from Analysis

1. **DatagramChannel with connect()** (SLCircuit.java lines 100-104):
```java
this.datagramChannel = DatagramChannel.open();
this.datagramChannel.configureBlocking(false);
this.datagramChannel.connect(sLCircuitInfo.socketAddress);
```

2. **IPv4 Preference** (SLConnection.java lines 21-22):
```java
System.setProperty("java.net.preferIPv4Stack", "true");
System.setProperty("java.net.preferIPv6Addresses", "false");
```

3. **NIO Selector Pattern** for non-blocking UDP I/O

## Legal Notice

This decompiled code is provided for educational purposes under fair use for interoperability and research. 
Lumiya is © Alina Lyvette. All rights reserved.

## Directory Structure

```
lumiya_decompiled_source/
├── com/lumiyaviewer/lumiya/
│   ├── slproto/           # Second Life Protocol implementation
│   │   ├── auth/          # Authentication
│   │   ├── caps/          # Capabilities
│   │   ├── chat/          # Chat handling
│   │   ├── inventory/     # Inventory management
│   │   ├── llsd/          # LLSD data format
│   │   ├── messages/      # UDP message definitions
│   │   ├── modules/       # Feature modules
│   │   ├── objects/       # Object handling
│   │   ├── types/         # Data types (LLVector3, etc.)
│   │   └── users/         # User management
│   ├── ui/                # User interface
│   └── ...
├── android/support/       # Android support libraries
└── ...
```
