# Contributing to Linkpoint (Lumiya Viewer)

Thank you for your interest in contributing to the Linkpoint project! This document provides guidelines for contributing to this Second Life viewer for Android.

## Development Setup

### Prerequisites
- Android Studio (latest stable version)
- Android SDK with API level 14+ (minimum) and 34+ (target)
- Java 8 or higher
- Git

### Setting up the Development Environment

1. Clone the repository:
   ```bash
   git clone https://github.com/Kaleaon/Linkpoint.git
   cd Linkpoint
   ```

2. Open the project in Android Studio

3. Let Android Studio sync the Gradle files

4. Build the project to ensure everything is working

## Code Style Guidelines

### Java Code Style
- Follow standard Java naming conventions
- Use 4 spaces for indentation (no tabs)
- Keep line length under 120 characters when possible
- Use meaningful variable and method names
- Add JavaDoc comments for public methods and classes

### Android Specific Guidelines
- Follow Android best practices for lifecycle management
- Use appropriate Android components (Activities, Services, etc.)
- Handle configuration changes properly
- Implement proper error handling and logging

### Performance Considerations
- This is a 3D graphics application, so performance is critical
- Be mindful of memory usage and garbage collection
- Use efficient data structures and algorithms
- Profile code changes for performance impact

## Testing

### Before Submitting
- Test on multiple Android devices/versions when possible
- Ensure the app builds without warnings
- Test basic functionality (login, navigation, rendering)
- Check for memory leaks in graphics-intensive operations

### Code Quality
- Remove unused imports
- Avoid System.out.println() - use proper logging
- Handle exceptions appropriately
- Follow the existing code patterns in the project

## Submitting Changes

### Pull Request Process
1. Fork the repository
2. Create a feature branch from `main`
3. Make your changes with clear, descriptive commit messages
4. Test thoroughly
5. Submit a pull request with a clear description

### Commit Messages
- Use clear, descriptive commit messages
- Start with a capital letter
- Use imperative mood ("Add feature" not "Added feature")
- Keep first line under 50 characters
- Add detailed description if needed

### Pull Request Description
- Describe what the change does and why
- Reference any related issues
- Include screenshots for UI changes
- Note any breaking changes

## Code Architecture

### Project Structure
```
app/
├── src/main/java/com/lumiyaviewer/lumiya/
│   ├── render/          # 3D rendering system
│   ├── res/             # Resource management
│   ├── slproto/         # Second Life protocol
│   ├── ui/              # User interface
│   └── utils/           # Utility classes
└── resources/           # Android resources and manifest
```

### Key Components
- **Resource Management**: Handles textures, animations, geometry
- **Network Layer**: Second Life protocol implementation
- **Rendering System**: OpenGL-based 3D graphics
- **UI Layer**: Android Activities and UI components

## Getting Help

- Check existing issues and documentation first
- Create an issue for bugs or feature requests
- Be specific about the problem and include relevant details
- Provide steps to reproduce for bugs

## License

By contributing to this project, you agree that your contributions will be licensed under the MIT License.

---

Thank you for contributing to Linkpoint!