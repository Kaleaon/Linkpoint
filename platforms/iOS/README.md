# Linkpoint iOS

Modern iOS client for Second Life and OpenSimulator grids built with SwiftUI.

## Features

- **SwiftUI Interface**: Modern, native iOS interface with Material Design principles
- **MVVM Architecture**: Clean separation of concerns with ViewModels
- **Multi-Grid Support**: Connect to Second Life, OSGrid, and custom OpenSimulator grids
- **Real-time Chat**: Local and group chat functionality
- **Inventory Management**: Browse and manage your virtual inventory
- **3D World View**: OpenGL/Metal-based 3D rendering (to be implemented)
- **Profile Management**: View and edit your avatar profile

## Requirements

- iOS 15.0+
- Xcode 14.0+
- Swift 5.7+

## Project Structure

```
iOS/
├── App.swift                    # Main app entry point
├── Models/
│   └── User.swift              # User and Grid data models
├── ViewModels/
│   └── AuthViewModel.swift     # Authentication logic
├── Views/
│   ├── LoginView.swift         # Login screen
│   └── MainTabView.swift       # Main app interface
├── Info.plist                  # App configuration
└── README.md                   # This file
```

## Building

1. Open the project in Xcode
2. Select your target device or simulator
3. Build and run (⌘R)

## Architecture

### MVVM Pattern

The app follows the Model-View-ViewModel pattern:

- **Models**: Data structures (User, Grid, ChatMessage)
- **Views**: SwiftUI views for UI presentation
- **ViewModels**: Business logic and state management

### Authentication Flow

1. User selects a grid (Second Life, OSGrid, or custom)
2. Enters credentials (username, password, first name, last name)
3. AuthViewModel handles XMLRPC authentication
4. On success, user is navigated to MainTabView

### State Management

- Uses `@StateObject` and `@EnvironmentObject` for state management
- `AuthViewModel` is the single source of truth for authentication state
- Persists user session to UserDefaults

## Integration with OpenSimulator

The iOS client is designed to work with existing OpenSimulator/Second Life backends:

- **XMLRPC Authentication**: Standard Second Life login protocol
- **LLSD Protocol**: For messaging and data exchange
- **UDP Protocol**: For real-time world updates (to be implemented)
- **Capabilities**: For advanced features (to be implemented)

## Next Steps

1. **Implement XMLRPC Client**: Real authentication with grid servers
2. **Add 3D Rendering**: Metal-based rendering engine
3. **Implement UDP Protocol**: Real-time world updates
4. **Add Voice Chat**: WebRTC integration
5. **Implement Inventory System**: Full inventory management
6. **Add Scripting Support**: LSL script execution

## Related Documentation

- See `../Android/` for Android implementation
- See `../PWA/` for Progressive Web App implementation
- See main repository README for overall project documentation