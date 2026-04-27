# Conversion Batch 3: Simple Enums and Constants

## Overview
This batch focuses on converting simple enum classes and constant definitions that have no complex dependencies.

## Target Files

### 1. EDeRezDestination.kt (SIMPLE - Enum)
**Source:** `app/src/main/java.backup/com/lumiyaviewer/lumiya/slproto/types/EDeRezDestination.java`
**Target:** `app/src/main/java/com/lumiyaviewer/lumiya/slproto/types/EDeRezDestination.kt`
**Complexity:** Low
**Dependencies:** None
**Description:** Enum for de-rez destination codes

## Conversion Order
1. EDeRezDestination (simple enum with integer codes)

## Key Conversion Considerations

### EDeRezDestination
- Enum with integer code property
- Use Kotlin enum class with constructor parameter
- Maintain all enum values and codes

## Testing Strategy
1. Verify compilation after conversion
2. Check enum values are accessible
3. Validate code property values
4. Run ktlint formatting

## Success Criteria
- File compiles without errors
- ktlint passes with no violations
- All enum values preserved
- Code property accessible