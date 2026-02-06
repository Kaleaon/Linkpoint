# Phase 2: Core Protocol Extensions

This directory contains the first incremental batch of Phase 2 modules for the Android-to-JavaScript port.

## Overview

Phase 2 focuses on core protocol extensions, inventory management, and communication features. This is the first batch of 10 modules (features 5-50) as outlined in the [ANDROID_PORT_ROADMAP.md](../ANDROID_PORT_ROADMAP.md).

## Modules in This Batch

### Priority 1: Critical Protocol Features (Features 5-20)

#### 1. eventqueue.js (Features 5-8)
**Android Source**: `app/src/main/java/com/lumiyaviewer/lumiya/slproto/modules/`

Manages Second Life event queue polling and processing.
- Event queue polling with exponential backoff
- Event deserialization
- Event handler registration
- Capability-based event processing

**API Surface**:
```javascript
const eventQueue = new EventQueueManager(protocolInstance);
eventQueue.registerHandler('ChatFromSimulator', handler);
await eventQueue.startPolling(seedCapability);
eventQueue.processEvents();
```

#### 2. capabilities.js (Features 9-12)
**Android Source**: `app/src/main/java/com/lumiyaviewer/lumiya/slproto/modules/`

Manages capability URLs with caching and retry logic.
- Capability caching with expiration
- Seed capability parsing
- Capability URL resolution
- Timeout and retry logic

**API Surface**:
```javascript
const caps = new CapabilitiesManager();
await caps.parseSeedCapability(seedData);
const url = caps.resolveCapability('EventQueueGet');
```

#### 3. avatar.js (Features 13-16)
**Android Source**: `app/src/main/java/com/lumiyaviewer/lumiya/slproto/users/`

Manages avatar appearance, attachments, and visual parameters.
- Avatar appearance parameters
- Attachment points
- Visual parameters (shape, skin)
- Avatar skeleton basics

**API Surface**:
```javascript
const avatar = new AvatarManager();
avatar.setVisualParam(33, 0.5);
avatar.attachObject(3, objectData);
```

#### 4. objects-extended.js (Features 17-20)
**Android Source**: `app/src/main/java/com/lumiyaviewer/lumiya/slproto/objects/`

Extends object management with prim parameters and relationships.
- Prim parameters (shape, material, texture)
- Object permissions
- Object selection
- Parent-child relationships

**API Surface**:
```javascript
const objMgr = new ObjectManagerExtended();
objMgr.setPrimParams(objectId, primParams);
objMgr.linkChild(parentId, childId);
```

### Priority 2: Inventory System (Features 21-35)

#### 5. inventory-core.js (Features 21-24)
**Android Source**: `app/src/main/java/com/lumiyaviewer/lumiya/slproto/modules/inventory/`

Core inventory folder and item management.
- Inventory folder structure
- Item properties
- Folder sorting
- Item movement

**API Surface**:
```javascript
const inventory = new InventoryCore();
const folder = inventory.createFolder(folderId, folderData);
inventory.moveItem(itemId, targetFolderId);
```

#### 6. inventory-ops.js (Features 26-30)
**Android Source**: `app/src/main/java/com/lumiyaviewer/lumiya/slproto/modules/inventory/`

Inventory CRUD operations.
- Create folder
- Delete item/folder
- Move item
- Copy item
- Rename operations

**API Surface**:
```javascript
const inventoryOps = new InventoryOperations(inventoryCore);
await inventoryOps.createFolder(parentId, 'My Folder');
await inventoryOps.copyItem(itemId, targetFolderId);
```

#### 7. inventory-types.js (Features 31-35)
**Android Source**: `app/src/main/java/com/lumiyaviewer/lumiya/slproto/modules/inventory/`

Special inventory item types.
- Gestures
- Animations
- Scripts
- Sounds
- Textures

**API Surface**:
```javascript
const specialTypes = new InventorySpecialTypes();
specialTypes.registerGesture(gestureId, gestureData);
specialTypes.registerAnimation(animId, animData);
```

### Priority 3: Communication Features (Features 36-50)

#### 8. chat-extended.js (Features 36-40)
**Android Source**: `app/src/main/java/com/lumiyaviewer/lumiya/slproto/users/chatsrc/`

Extended chat functionality.
- Chat history persistence
- Chat filtering
- Mute list
- Chat range (whisper/shout)
- Typing indicators

**API Surface**:
```javascript
const chat = new ChatExtended();
chat.addToHistory(message);
chat.muteUser(userId);
await chat.sendWithRange('Hello!', 'shout');
```

#### 9. groups.js (Features 41-45)
**Android Source**: `app/src/main/java/com/lumiyaviewer/lumiya/slproto/modules/groups/`

Group management system.
- Group info
- Group members
- Group roles
- Group chat
- Group notices

**API Surface**:
```javascript
const groups = new GroupsManager();
groups.setGroupInfo(groupId, groupInfo);
groups.addGroupMember(groupId, memberId);
await groups.sendGroupChat(groupId, message);
```

#### 10. friends-extended.js (Features 46-50)
**Android Source**: `app/src/main/java/com/lumiyaviewer/lumiya/slproto/users/`

Extended friend functionality.
- Friend requests
- Online notifications
- Friend permissions
- Calling cards
- Friend groups

**API Surface**:
```javascript
const friends = new FriendsExtended();
await friends.sendFriendRequest(userId, message);
friends.updateFriendStatus(friendId, 'online');
friends.setFriendPermissions(friendId, permissions);
```

## Module Mapping to Android Source

| Module | Android Package | Features |
|--------|----------------|----------|
| eventqueue.js | slproto/modules/ | 5-8 |
| capabilities.js | slproto/modules/ | 9-12 |
| avatar.js | slproto/users/ | 13-16 |
| objects-extended.js | slproto/objects/ | 17-20 |
| inventory-core.js | slproto/modules/inventory/ | 21-24 |
| inventory-ops.js | slproto/modules/inventory/ | 26-30 |
| inventory-types.js | slproto/modules/inventory/ | 31-35 |
| chat-extended.js | slproto/users/chatsrc/ | 36-40 |
| groups.js | slproto/modules/groups/ | 41-45 |
| friends-extended.js | slproto/users/ | 46-50 |

## Development Guidelines

### Running Tests

```bash
# From PWA-demo directory
npm test

# Run specific test file
npm test tests/phase2/eventqueue.test.js

# Run with coverage
npm test -- --coverage
```

### Running Lint

```bash
# Check syntax (all files are valid JavaScript)
node -c js/phase2/eventqueue.js

# If ESLint is available:
npx eslint js/phase2/*.js
```

### File Structure

Each module follows this pattern:
1. **Header comment** - Links to roadmap and Android source
2. **JSDoc documentation** - For all public methods
3. **Class/singleton export** - Main functionality
4. **TODOs** - Link to specific Android implementation details
5. **Example usage** - In comments at end of file

### Code Style

- **Validation**: All inputs are validated with clear error messages
- **Logging**: Console logging for debugging (prefix: `[ModuleName]`)
- **Error handling**: Try-catch blocks where appropriate
- **Comments**: TODO comments link back to Android source
- **Export**: CommonJS exports for Node.js compatibility

## Testing

Unit tests are provided in `tests/phase2/` for:
- eventqueue.test.js - Event queue operations
- capabilities.test.js - Capability caching and resolution
- inventory-core.test.js - Inventory folder/item management

Tests use Jest framework and validate:
- Input validation and error handling
- Basic functionality (enqueue/dequeue, cache set/get)
- Edge cases (null inputs, empty collections)

## Next Steps

### Future TODOs (From Roadmap)

Each module contains detailed TODOs for future implementation:

1. **Event Queue**: Exponential backoff, LLSD deserialization
2. **Capabilities**: IndexedDB persistence, circuit breaker pattern
3. **Avatar**: Full appearance serialization, baked textures
4. **Objects**: Full prim validation, sculpt maps
5. **Inventory Core**: IndexedDB persistence, search
6. **Inventory Ops**: Server-side API integration, trash folder
7. **Inventory Types**: Asset parsing, preloading
8. **Chat**: IndexedDB history, search functionality
9. **Groups**: Group data fetching via capabilities
10. **Friends**: Full permission system implementation

### Phase 2 Remaining Work

This is batch 1 of Phase 2. Remaining batches will add:
- Mesh and texture management (features 51-65)
- Animation system (features 66-80)
- World interaction (features 81-100)

See [ANDROID_PORT_ROADMAP.md](../ANDROID_PORT_ROADMAP.md) for complete roadmap.

## Integration

To use these modules in your PWA:

```javascript
// Import modules
import { EventQueueManager } from './js/phase2/eventqueue.js';
import { CapabilitiesManager } from './js/phase2/capabilities.js';

// Or in Node.js:
const { EventQueueManager } = require('./js/phase2/eventqueue.js');

// Initialize
const protocol = new ProtocolHandler();
const eventQueue = new EventQueueManager(protocol);
const caps = new CapabilitiesManager();

// Use together
await caps.parseSeedCapability(seedData);
const eventQueueUrl = caps.resolveCapability('EventQueueGet');
await eventQueue.startPolling(eventQueueUrl);
```

## Contributing

When adding new features or fixing bugs:

1. Update relevant TODO comments
2. Add tests for new functionality
3. Update this README with API changes
4. Link to Android source in comments
5. Follow existing code style patterns

## License

GPL-2.0 - Same as parent Linkpoint project

---

**Phase 2 Status**: First incremental batch complete (10 modules, features 5-50)
**Last Updated**: 2025-10-18
**Next Batch**: Features 51-65 (Mesh and textures)
