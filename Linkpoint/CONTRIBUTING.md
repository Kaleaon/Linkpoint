# Contributing to Linkpoint

Thank you for your interest in contributing to Linkpoint! This document provides guidelines for contributing to the project.

## 🤝 Ways to Contribute

- 🐛 **Report bugs** - Submit detailed bug reports
- 💡 **Suggest features** - Propose new features or improvements
- 📝 **Improve documentation** - Help make docs clearer
- 🔧 **Submit code** - Fix bugs or implement features
- 🧪 **Write tests** - Improve test coverage
- 🎨 **Design UI/UX** - Contribute to visual design

## 🚀 Getting Started

### 1. Fork the Repository

Click the "Fork" button on GitHub to create your own copy.

### 2. Clone Your Fork

```bash
git clone https://github.com/YOUR_USERNAME/linkpoint.git
cd linkpoint
```

### 3. Set Up Development Environment

```bash
# Open in Android Studio
studio Linkpoint/

# Or use command line
./gradlew build
```

### 4. Create a Branch

```bash
git checkout -b feature/your-feature-name
# or
git checkout -b fix/bug-description
```

## 📋 Code Guidelines

### Kotlin Style

Follow [Kotlin Coding Conventions](https://kotlinlang.org/docs/coding-conventions.html):

```kotlin
// Good
class VoiceManager(
    private val context: Context,
    private val callback: VoiceCallback
) {
    fun connectVoice() {
        // Implementation
    }
}

// Bad
class VoiceManager(context: Context, callback: VoiceCallback) {
    val context = context
    fun ConnectVoice() { }
}
```

### Naming Conventions

- **Classes**: PascalCase (e.g., `LinkpointVoiceManager`)
- **Functions**: camelCase (e.g., `connectToVoiceChannel`)
- **Constants**: UPPER_SNAKE_CASE (e.g., `MAX_CONNECTIONS`)
- **Private fields**: camelCase with underscore prefix (e.g., `_sessionId`)

### Documentation

All public APIs must have KDoc comments:

```kotlin
/**
 * Connects to Second Life voice channel
 *
 * @param channelUri The SIP URI of the voice channel
 * @param authToken Authentication token from login
 * @return True if connection successful, false otherwise
 * @throws VoiceException if WebRTC not initialized
 */
suspend fun connectToVoiceChannel(
    channelUri: String,
    authToken: String
): Boolean {
    // Implementation
}
```

### Async Code

Use coroutines, not callbacks:

```kotlin
// Good
suspend fun fetchData(): Result<Data> = withContext(Dispatchers.IO) {
    try {
        val data = api.fetch()
        Result.success(data)
    } catch (e: Exception) {
        Result.failure(e)
    }
}

// Bad
fun fetchData(callback: Callback) {
    Thread {
        try {
            val data = api.fetch()
            callback.onSuccess(data)
        } catch (e: Exception) {
            callback.onError(e)
        }
    }.start()
}
```

## 🧪 Testing

### Write Tests

All new features must include tests:

```kotlin
@Test
fun `connectToVoiceChannel should succeed with valid credentials`() = runTest {
    val manager = LinkpointVoiceManager(context, callback)
    
    val result = manager.connectToVoiceChannel(
        channelUri = "sip:test@example.com",
        authToken = "valid-token"
    )
    
    assertTrue(result)
}
```

### Run Tests

```bash
# Unit tests
./gradlew test

# Integration tests
./gradlew connectedAndroidTest

# All tests
./gradlew check
```

## 📝 Commit Messages

Use [Conventional Commits](https://www.conventionalcommits.org/):

```
feat: add WebRTC voice manager
fix: resolve audio echo issue
docs: update integration guide
test: add voice manager tests
refactor: improve protocol manager
perf: optimize texture loading
style: format code with ktlint
```

## 🔄 Pull Request Process

### 1. Update Your Branch

```bash
git fetch upstream
git rebase upstream/main
```

### 2. Run Checks

```bash
./gradlew check
./gradlew ktlintCheck
```

### 3. Commit Changes

```bash
git add .
git commit -m "feat: your feature description"
```

### 4. Push to Your Fork

```bash
git push origin feature/your-feature-name
```

### 5. Create Pull Request

Go to GitHub and click "New Pull Request"

### PR Template

```markdown
## Description
Brief description of changes

## Type of Change
- [ ] Bug fix
- [ ] New feature
- [ ] Breaking change
- [ ] Documentation update

## Testing
- [ ] Unit tests pass
- [ ] Integration tests pass
- [ ] Manual testing completed

## Screenshots (if applicable)
Add screenshots here

## Checklist
- [ ] Code follows style guidelines
- [ ] Self-review completed
- [ ] Comments added for complex code
- [ ] Documentation updated
- [ ] No new warnings generated
- [ ] Tests added
- [ ] Tests pass
```

## 🐛 Bug Reports

Use this template for bug reports:

```markdown
**Description**
Clear description of the bug

**To Reproduce**
1. Go to '...'
2. Click on '....'
3. See error

**Expected Behavior**
What should happen

**Screenshots**
If applicable

**Device Info**
- Device: [e.g. Pixel 6]
- Android Version: [e.g. 13]
- Linkpoint Version: [e.g. 1.0.0]

**Additional Context**
Any other relevant information
```

## 💡 Feature Requests

Use this template for feature requests:

```markdown
**Problem**
What problem does this solve?

**Proposed Solution**
How should it work?

**Alternatives**
What else did you consider?

**Additional Context**
Any other relevant information
```

## 📚 Resources

### Documentation
- [Kotlin Docs](https://kotlinlang.org/docs/home.html)
- [Android Docs](https://developer.android.com/)
- [WebRTC Docs](https://webrtc.org/)

### Style Guides
- [Kotlin Style Guide](https://kotlinlang.org/docs/coding-conventions.html)
- [Android Code Style](https://source.android.com/docs/setup/contribute/code-style)

### Tools
- [ktlint](https://ktlint.github.io/) - Kotlin linter
- [detekt](https://detekt.dev/) - Static analysis
- [Android Studio](https://developer.android.com/studio)

## ⚖️ Code of Conduct

### Our Pledge

We pledge to make participation in our project a harassment-free experience for everyone, regardless of age, body size, disability, ethnicity, gender identity, level of experience, nationality, personal appearance, race, religion, or sexual identity and orientation.

### Our Standards

**Positive behavior:**
- Using welcoming and inclusive language
- Being respectful of differing viewpoints
- Gracefully accepting constructive criticism
- Focusing on what is best for the community
- Showing empathy towards others

**Unacceptable behavior:**
- Trolling, insulting/derogatory comments
- Public or private harassment
- Publishing others' private information
- Other conduct which could reasonably be considered inappropriate

### Enforcement

Violations may be reported to [conduct@linkpoint.app](mailto:conduct@linkpoint.app). All complaints will be reviewed and investigated.

## 🎓 Learning Resources

### For Beginners
1. [Kotlin Koans](https://kotlinlang.org/docs/koans.html)
2. [Android Basics in Kotlin](https://developer.android.com/courses/android-basics-kotlin/course)
3. [Git Basics](https://git-scm.com/book/en/v2/Getting-Started-Git-Basics)

### For Contributors
1. [Coroutines Guide](https://kotlinlang.org/docs/coroutines-guide.html)
2. [Android Architecture](https://developer.android.com/topic/architecture)
3. [Testing Guide](https://developer.android.com/training/testing)

## 📞 Getting Help

- 💬 **Discord**: Join our community server
- 📧 **Email**: support@linkpoint.app
- 🐛 **GitHub Issues**: For bug reports
- 💡 **GitHub Discussions**: For questions

## 🏆 Recognition

Contributors will be:
- Listed in AUTHORS.md
- Credited in release notes
- Invited to contributors channel
- Given "Contributor" badge

Thank you for contributing to Linkpoint! 🎉