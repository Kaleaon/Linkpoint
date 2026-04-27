# Conversion Batch 2: Core Utility Classes

## Overview
This batch focuses on converting core utility classes that provide foundational data structures and helper functionality.

## Target Files (6 files)

### 1. InlineListEntry.kt (SIMPLE - Interface)
**Source:** `app/src/main/java.backup/com/lumiyaviewer/lumiya/utils/InlineListEntry.java`
**Target:** `app/src/main/java/com/lumiyaviewer/lumiya/utils/InlineListEntry.kt`
**Complexity:** Low
**Dependencies:** None
**Description:** Interface for entries in an inline linked list

### 2. InlineList.kt (MEDIUM - Generic Class)
**Source:** `app/src/main/java.backup/com/lumiyaviewer/lumiya/utils/InlineList.java`
**Target:** `app/src/main/java/com/lumiyaviewer/lumiya/utils/InlineList.kt`
**Complexity:** Medium
**Dependencies:** InlineListEntry
**Description:** Generic inline linked list implementation

### 3. WeakRequestSet.kt (MEDIUM - Thread-safe Collection)
**Source:** `app/src/main/java.backup/com/lumiyaviewer/lumiya/utils/reqset/WeakRequestSet.java`
**Target:** `app/src/main/java/com/lumiyaviewer/lumiya/utils/reqset/WeakRequestSet.kt`
**Complexity:** Medium
**Dependencies:** RequestCompleteListener
**Description:** Thread-safe weak reference request tracking

### 4. WeakPriorityRequestSet.kt (COMPLEX - Priority Queue)
**Source:** `app/src/main/java.backup/com/lumiyaviewer/lumiya/utils/reqset/WeakPriorityRequestSet.java`
**Target:** `app/src/main/java/com/lumiyaviewer/lumiya/utils/reqset/WeakPriorityRequestSet.kt`
**Complexity:** High
**Dependencies:** RequestListener, WeakRequestSet
**Description:** Priority-based request queue with weak references

### 5. ChunkedListLoader.kt (SIMPLE - Interface)
**Source:** `app/src/main/java.backup/com/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader.java`
**Target:** `app/src/main/java/com/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader.kt`
**Complexity:** Low
**Dependencies:** None
**Description:** Interface for loading chunked list data

### 6. ChunkedList.kt (COMPLEX - Custom Collection)
**Source:** `app/src/main/java.backup/com/lumiyaviewer/lumiya/utils/wlist/ChunkedList.java`
**Target:** `app/src/main/java/com/lumiyaviewer/lumiya/utils/wlist/ChunkedList.kt`
**Complexity:** High
**Dependencies:** ChunkedListLoader
**Description:** Custom list implementation with chunked storage

## Conversion Order
1. InlineListEntry (interface - no dependencies)
2. ChunkedListLoader (interface - no dependencies)
3. InlineList (depends on InlineListEntry)
4. WeakRequestSet (depends on RequestCompleteListener - already converted)
5. WeakPriorityRequestSet (depends on WeakRequestSet)
6. ChunkedList (depends on ChunkedListLoader)

## Key Conversion Considerations

### InlineListEntry & InlineList
- Generic type constraints need proper Kotlin syntax
- Nullable types must be explicit
- Property access patterns

### WeakRequestSet & WeakPriorityRequestSet
- Thread safety with synchronized blocks → use `synchronized()` in Kotlin
- WeakReference handling
- Collection iteration and modification
- Lock/Condition usage in Kotlin

### ChunkedList
- Extends AbstractList - proper Kotlin inheritance
- RandomAccess marker interface
- Complex indexing logic
- Inner interface ChunkFactory
- Binary search and sorting

## Testing Strategy
1. Verify compilation after each file
2. Check thread safety in concurrent scenarios
3. Test generic type constraints
4. Validate collection operations
5. Run ktlint formatting

## Success Criteria
- All files compile without errors
- ktlint passes with no violations
- No functionality regressions
- Proper Kotlin idioms used
- Thread safety preserved