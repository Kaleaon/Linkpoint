# Conversion Batch 1: Simple Interfaces and Enums

## Target Files (10 files)
These are simple, low-risk files with no complex dependencies:

### Interfaces (4 files)
1. `utils/HasPriority.java` - Simple interface with one method
2. `utils/Identifiable.java` - Generic interface with one method
3. `cloud/common/Bundleable.java` - Simple interface with one method
4. `utils/reqset/RequestListener.java` - Interface (need to check)

### Enums (2 files)
5. `cloud/common/MessageType.java` - Simple enum with constants

### Event Bus (3 files)
6. `eventbus/EventHandler.java` - Likely an interface or simple class
7. `eventbus/EventRateLimiter.java` - Utility class

### Request Set Utilities (1 file)
8. `utils/reqset/RequestCompleteListener.java` - Likely an interface

## Conversion Strategy
1. Convert each file manually with proper Kotlin idioms
2. Use `interface` for Java interfaces
3. Use `enum class` for Java enums
4. Use `object` for singleton patterns
5. Apply Kotlin best practices (null safety, default parameters, etc.)
6. Run ktlint after each conversion
7. Verify compilation after batch completion

## Expected Outcomes
- Clean, idiomatic Kotlin code
- No compilation errors
- Proper null safety
- Reduced boilerplate compared to Java