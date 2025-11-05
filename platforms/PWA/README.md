# Linkpoint PWA

Progressive Web App for Second Life and OpenSimulator grids built with Next.js 14, React, and TypeScript.

## Features

- **Modern UI**: Responsive design with Tailwind CSS
- **PWA Support**: Installable on mobile and desktop devices
- **Real-time Chat**: Local, group, and instant messaging
- **Inventory Management**: Browse and manage virtual items
- **3D World View**: Canvas-based rendering (Three.js integration ready)
- **Multi-Grid Support**: Connect to Second Life, OSGrid, and custom grids
- **Offline Support**: Service worker for offline functionality

## Tech Stack

- **Framework**: Next.js 14 (App Router)
- **UI Library**: React 18
- **Styling**: Tailwind CSS
- **Language**: TypeScript
- **3D Graphics**: Three.js (ready for integration)
- **State Management**: React Hooks

## Project Structure

```
PWA/
├── app/
│   ├── layout.tsx              # Root layout with PWA metadata
│   ├── page.tsx                # Main app entry point
│   └── globals.css             # Global styles
├── components/
│   ├── LoginPage.tsx           # Login interface
│   ├── MainApp.tsx             # Main application shell
│   ├── WorldView.tsx           # 3D world viewport
│   ├── InventoryView.tsx       # Inventory management
│   ├── ChatView.tsx            # Chat interface
│   └── ProfileView.tsx         # User profile
├── public/
│   └── manifest.json           # PWA manifest
├── package.json                # Dependencies
├── next.config.js              # Next.js configuration
├── tailwind.config.js          # Tailwind configuration
├── tsconfig.json               # TypeScript configuration
└── README.md                   # This file
```

## Getting Started

### Prerequisites

- Node.js 18.0 or higher
- npm or yarn

### Installation

```bash
# Install dependencies
npm install

# Run development server
npm run dev

# Build for production
npm run build

# Start production server
npm start
```

The app will be available at `http://localhost:3000`

## PWA Installation

### Desktop
1. Open the app in Chrome/Edge
2. Click the install icon in the address bar
3. Follow the prompts to install

### Mobile
1. Open the app in mobile browser
2. Tap "Add to Home Screen"
3. The app will install as a native-like application

## Features in Detail

### Authentication
- Multi-grid support (Second Life, OSGrid, custom grids)
- XMLRPC authentication (to be implemented)
- Session persistence with localStorage

### World View
- Canvas-based 3D rendering
- Movement controls
- Mini-map
- Region information
- Ready for Three.js/WebGL integration

### Inventory
- Folder-based organization
- Search functionality
- Item categorization
- Recent items view

### Chat
- Multiple channels (Local, Group, IM, System)
- Real-time messaging
- Message history
- Typing indicators (ready for implementation)

### Profile
- User information display
- Statistics dashboard
- Settings management
- Account preferences

## Integration with OpenSimulator

The PWA is designed to work with existing OpenSimulator/Second Life backends:

- **XMLRPC Authentication**: Standard login protocol
- **WebSocket Communication**: Real-time updates
- **REST APIs**: For inventory and profile management
- **WebRTC**: Voice chat support (to be implemented)

## Development Roadmap

### Phase 1: Core Features (Current)
- ✅ Basic UI and navigation
- ✅ Login system
- ✅ Chat interface
- ✅ Inventory view
- ✅ Profile management

### Phase 2: Backend Integration
- [ ] XMLRPC client implementation
- [ ] WebSocket connection
- [ ] Real authentication
- [ ] Inventory API integration

### Phase 3: 3D Rendering
- [ ] Three.js integration
- [ ] Basic 3D scene rendering
- [ ] Avatar rendering
- [ ] Object loading

### Phase 4: Advanced Features
- [ ] Voice chat (WebRTC)
- [ ] Group management
- [ ] Scripting support
- [ ] Marketplace integration

## Performance Optimization

- Code splitting with Next.js
- Image optimization
- Lazy loading of components
- Service worker caching
- Optimized bundle size

## Browser Support

- Chrome/Edge 90+
- Firefox 88+
- Safari 14+
- Mobile browsers (iOS Safari, Chrome Mobile)

## Related Documentation

- See `../Android/` for Android implementation
- See `../iOS/` for iOS implementation
- See main repository README for overall project documentation

## Contributing

Contributions are welcome! Please read the contributing guidelines in the main repository.

## License

See LICENSE file in the main repository.