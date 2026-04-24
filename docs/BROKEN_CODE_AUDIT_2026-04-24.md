# Broken Code Audit (2026-04-24)

This audit enumerates all script/code files (`.kt`, `.java`, `.py`, `.sh`) in the repository and flags likely-broken files using static heuristics and syntax checks.

## Sources of truth used for comparison baselines
- **LLSD**: https://wiki.secondlife.com/wiki/LLSD
- **LibreMetaverse**: https://github.com/cinderblocks/libremetaverse
- **SecondLifeViewer**: https://github.com/secondlife/viewer
- **FirestormViewer**: https://github.com/FirestormViewer/phoenix-firestorm

## Method
1. Inventory every script-like source file in the repo.
2. Tag each file with likely upstream comparison sources based on protocol/LLSD/network keywords.
3. Mark files as **BROKEN** when static indicators are present (decompilation stubs, TODO/FIXME/Not implemented markers, merge conflicts, explicit unsupported operations, syntax errors for Python/Shell).
4. Emit full per-file results in `docs/broken_code_inventory_2026-04-24.csv`.

## Summary
- Files scanned: **3569**
- BROKEN: **179**
- OK: **3390**
- By language:
  - java: 3028
  - kt: 525
  - py: 6
  - sh: 10

### Broken signal counts
- unsupported-operation: 178
- decompilation-stub: 92
- todo-call: 1

## Top high-risk broken files (first 200)
| File | Lang | Signals | Source truth |
|---|---|---|---|
| `Linkpoint/tools/generate_parser_scaffolding.py` | py | todo-call | LibreMetaverse|SecondLifeViewer|FirestormViewer |
| `file_bundle/ActiveChattersManager.java` | java | decompilation-stub|unsupported-operation | LibreMetaverse|SecondLifeViewer|FirestormViewer |
| `file_bundle/GroupMainProfileTab.java` | java | decompilation-stub|unsupported-operation | LibreMetaverse|SecondLifeViewer|FirestormViewer |
| `file_bundle/SyncManager.java` | java | decompilation-stub|unsupported-operation | SecondLifeViewer|FirestormViewer |
| `file_bundle/WorldViewActivity.java` | java | decompilation-stub|unsupported-operation | LibreMetaverse|SecondLifeViewer|FirestormViewer |
| `lumiya_decompiled_source/android/arch/core/internal/SafeIterableMap.java` | java | unsupported-operation | SecondLifeViewer|FirestormViewer |
| `lumiya_decompiled_source/android/support/design/internal/BottomNavigationMenu.java` | java | unsupported-operation | SecondLifeViewer|FirestormViewer |
| `lumiya_decompiled_source/android/support/design/widget/CoordinatorLayout.java` | java | decompilation-stub|unsupported-operation | LibreMetaverse|SecondLifeViewer|FirestormViewer |
| `lumiya_decompiled_source/android/support/design/widget/HeaderBehavior.java` | java | decompilation-stub|unsupported-operation | SecondLifeViewer|FirestormViewer |
| `lumiya_decompiled_source/android/support/graphics/drawable/AnimationUtilsCompat.java` | java | decompilation-stub|unsupported-operation | SecondLifeViewer|FirestormViewer |
| `lumiya_decompiled_source/android/support/transition/TransitionInflater.java` | java | decompilation-stub|unsupported-operation | SecondLifeViewer|FirestormViewer |
| `lumiya_decompiled_source/android/support/v4/content/FileProvider.java` | java | unsupported-operation | SecondLifeViewer|FirestormViewer |
| `lumiya_decompiled_source/android/support/v4/content/pm/ShortcutManagerCompat.java` | java | decompilation-stub|unsupported-operation | LibreMetaverse|SecondLifeViewer|FirestormViewer |
| `lumiya_decompiled_source/android/support/v4/graphics/TypefaceCompatApi21Impl.java` | java | decompilation-stub|unsupported-operation | SecondLifeViewer|FirestormViewer |
| `lumiya_decompiled_source/android/support/v4/graphics/TypefaceCompatUtil.java` | java | decompilation-stub|unsupported-operation | SecondLifeViewer|FirestormViewer |
| `lumiya_decompiled_source/android/support/v4/graphics/drawable/RoundedBitmapDrawable.java` | java | unsupported-operation | SecondLifeViewer|FirestormViewer |
| `lumiya_decompiled_source/android/support/v4/media/MediaBrowserServiceCompat.java` | java | unsupported-operation | SecondLifeViewer|FirestormViewer |
| `lumiya_decompiled_source/android/support/v4/media/session/MediaControllerCompat.java` | java | unsupported-operation | SecondLifeViewer|FirestormViewer |
| `lumiya_decompiled_source/android/support/v4/media/session/MediaSessionCompat.java` | java | decompilation-stub|unsupported-operation | SecondLifeViewer|FirestormViewer |
| `lumiya_decompiled_source/android/support/v4/net/DatagramSocketWrapper.java` | java | unsupported-operation | SecondLifeViewer|FirestormViewer |
| `lumiya_decompiled_source/android/support/v4/provider/FontsContractCompat.java` | java | decompilation-stub|unsupported-operation | SecondLifeViewer|FirestormViewer |
| `lumiya_decompiled_source/android/support/v4/provider/SingleDocumentFile.java` | java | unsupported-operation | SecondLifeViewer|FirestormViewer |
| `lumiya_decompiled_source/android/support/v4/text/util/LinkifyCompat.java` | java | unsupported-operation | SecondLifeViewer|FirestormViewer |
| `lumiya_decompiled_source/android/support/v4/util/ArraySet.java` | java | unsupported-operation | SecondLifeViewer|FirestormViewer |
| `lumiya_decompiled_source/android/support/v4/util/LruCache.java` | java | decompilation-stub|unsupported-operation | SecondLifeViewer|FirestormViewer |
| `lumiya_decompiled_source/android/support/v4/util/MapCollections.java` | java | unsupported-operation | SecondLifeViewer|FirestormViewer |
| `lumiya_decompiled_source/android/support/v4/view/PagerAdapter.java` | java | unsupported-operation | SecondLifeViewer|FirestormViewer |
| `lumiya_decompiled_source/android/support/v4/view/ViewPager.java` | java | decompilation-stub|unsupported-operation | SecondLifeViewer|FirestormViewer |
| `lumiya_decompiled_source/android/support/v4/widget/SlidingPaneLayout.java` | java | decompilation-stub|unsupported-operation | SecondLifeViewer|FirestormViewer |
| `lumiya_decompiled_source/android/support/v4/widget/ViewDragHelper.java` | java | decompilation-stub|unsupported-operation | SecondLifeViewer|FirestormViewer |
| `lumiya_decompiled_source/android/support/v7/app/ActionBar.java` | java | unsupported-operation | SecondLifeViewer|FirestormViewer |
| `lumiya_decompiled_source/android/support/v7/app/ToolbarActionBar.java` | java | unsupported-operation | SecondLifeViewer|FirestormViewer |
| `lumiya_decompiled_source/android/support/v7/preference/PreferenceDataStore.java` | java | unsupported-operation | SecondLifeViewer|FirestormViewer |
| `lumiya_decompiled_source/android/support/v7/util/DiffUtil.java` | java | decompilation-stub|unsupported-operation | SecondLifeViewer|FirestormViewer |
| `lumiya_decompiled_source/android/support/v7/view/SupportMenuInflater.java` | java | decompilation-stub|unsupported-operation | SecondLifeViewer|FirestormViewer |
| `lumiya_decompiled_source/android/support/v7/view/menu/ActionMenuItem.java` | java | unsupported-operation | SecondLifeViewer|FirestormViewer |
| `lumiya_decompiled_source/android/support/v7/view/menu/MenuItemImpl.java` | java | unsupported-operation | SecondLifeViewer|FirestormViewer |
| `lumiya_decompiled_source/android/support/v7/view/menu/MenuPopup.java` | java | unsupported-operation | SecondLifeViewer|FirestormViewer |
| `lumiya_decompiled_source/android/support/v7/widget/AppCompatSpinner.java` | java | decompilation-stub|unsupported-operation | SecondLifeViewer|FirestormViewer |
| `lumiya_decompiled_source/android/support/v7/widget/AppCompatTextHelper.java` | java | unsupported-operation | SecondLifeViewer|FirestormViewer |
| `lumiya_decompiled_source/android/support/v7/widget/DropDownListView.java` | java | decompilation-stub|unsupported-operation | LibreMetaverse|SecondLifeViewer|FirestormViewer |
| `lumiya_decompiled_source/android/support/v7/widget/GridLayoutManager.java` | java | decompilation-stub|unsupported-operation | SecondLifeViewer|FirestormViewer |
| `lumiya_decompiled_source/android/support/v7/widget/RecyclerView.java` | java | decompilation-stub|unsupported-operation | SecondLifeViewer|FirestormViewer |
| `lumiya_decompiled_source/com/google/common/base/AbstractIterator.java` | java | unsupported-operation | SecondLifeViewer|FirestormViewer |
| `lumiya_decompiled_source/com/google/common/base/Joiner.java` | java | unsupported-operation | SecondLifeViewer|FirestormViewer |
| `lumiya_decompiled_source/com/google/common/base/Splitter.java` | java | decompilation-stub|unsupported-operation | SecondLifeViewer|FirestormViewer |
| `lumiya_decompiled_source/com/google/common/base/Suppliers.java` | java | decompilation-stub|unsupported-operation | SecondLifeViewer|FirestormViewer |
| `lumiya_decompiled_source/com/google/common/cache/AbstractCache.java` | java | unsupported-operation | SecondLifeViewer|FirestormViewer |
| `lumiya_decompiled_source/com/google/common/cache/AbstractLoadingCache.java` | java | unsupported-operation | SecondLifeViewer|FirestormViewer |
| `lumiya_decompiled_source/com/google/common/cache/CacheLoader.java` | java | unsupported-operation | SecondLifeViewer|FirestormViewer |
| `lumiya_decompiled_source/com/google/common/cache/LocalCache.java` | java | decompilation-stub|unsupported-operation | SecondLifeViewer|FirestormViewer |
| `lumiya_decompiled_source/com/google/common/cache/RemovalNotification.java` | java | unsupported-operation | SecondLifeViewer|FirestormViewer |
| `lumiya_decompiled_source/com/google/common/collect/AbstractMapEntry.java` | java | unsupported-operation | SecondLifeViewer|FirestormViewer |
| `lumiya_decompiled_source/com/google/common/collect/AbstractMultiset.java` | java | unsupported-operation | SecondLifeViewer|FirestormViewer |
| `lumiya_decompiled_source/com/google/common/collect/AbstractRangeSet.java` | java | unsupported-operation | SecondLifeViewer|FirestormViewer |
| `lumiya_decompiled_source/com/google/common/collect/ArrayTable.java` | java | unsupported-operation | SecondLifeViewer|FirestormViewer |
| `lumiya_decompiled_source/com/google/common/collect/ComputingConcurrentHashMap.java` | java | decompilation-stub|unsupported-operation | SecondLifeViewer|FirestormViewer |
| `lumiya_decompiled_source/com/google/common/collect/ConcurrentHashMultiset.java` | java | decompilation-stub|unsupported-operation | SecondLifeViewer|FirestormViewer |
| `lumiya_decompiled_source/com/google/common/collect/ContiguousSet.java` | java | unsupported-operation | SecondLifeViewer|FirestormViewer |
| `lumiya_decompiled_source/com/google/common/collect/ImmutableBiMap.java` | java | unsupported-operation | SecondLifeViewer|FirestormViewer |
| `lumiya_decompiled_source/com/google/common/collect/ImmutableClassToInstanceMap.java` | java | unsupported-operation | SecondLifeViewer|FirestormViewer |
| `lumiya_decompiled_source/com/google/common/collect/ImmutableCollection.java` | java | unsupported-operation | SecondLifeViewer|FirestormViewer |
| `lumiya_decompiled_source/com/google/common/collect/ImmutableEntry.java` | java | unsupported-operation | SecondLifeViewer|FirestormViewer |
| `lumiya_decompiled_source/com/google/common/collect/ImmutableList.java` | java | unsupported-operation | SecondLifeViewer|FirestormViewer |
| `lumiya_decompiled_source/com/google/common/collect/ImmutableListMultimap.java` | java | unsupported-operation | SecondLifeViewer|FirestormViewer |
| `lumiya_decompiled_source/com/google/common/collect/ImmutableMap.java` | java | unsupported-operation | SecondLifeViewer|FirestormViewer |
| `lumiya_decompiled_source/com/google/common/collect/ImmutableMultimap.java` | java | unsupported-operation | SecondLifeViewer|FirestormViewer |
| `lumiya_decompiled_source/com/google/common/collect/ImmutableMultiset.java` | java | unsupported-operation | SecondLifeViewer|FirestormViewer |
| `lumiya_decompiled_source/com/google/common/collect/ImmutableRangeMap.java` | java | unsupported-operation | SecondLifeViewer|FirestormViewer |
| `lumiya_decompiled_source/com/google/common/collect/ImmutableRangeSet.java` | java | unsupported-operation | SecondLifeViewer|FirestormViewer |
| `lumiya_decompiled_source/com/google/common/collect/ImmutableSetMultimap.java` | java | unsupported-operation | SecondLifeViewer|FirestormViewer |
| `lumiya_decompiled_source/com/google/common/collect/ImmutableSortedMap.java` | java | unsupported-operation | SecondLifeViewer|FirestormViewer |
| `lumiya_decompiled_source/com/google/common/collect/ImmutableSortedMapFauxverideShim.java` | java | unsupported-operation | SecondLifeViewer|FirestormViewer |
| `lumiya_decompiled_source/com/google/common/collect/ImmutableSortedMultiset.java` | java | unsupported-operation | SecondLifeViewer|FirestormViewer |
| `lumiya_decompiled_source/com/google/common/collect/ImmutableSortedMultisetFauxverideShim.java` | java | unsupported-operation | SecondLifeViewer|FirestormViewer |
| `lumiya_decompiled_source/com/google/common/collect/ImmutableSortedSet.java` | java | unsupported-operation | SecondLifeViewer|FirestormViewer |
| `lumiya_decompiled_source/com/google/common/collect/ImmutableSortedSetFauxverideShim.java` | java | unsupported-operation | SecondLifeViewer|FirestormViewer |
| `lumiya_decompiled_source/com/google/common/collect/ImmutableTable.java` | java | unsupported-operation | SecondLifeViewer|FirestormViewer |
| `lumiya_decompiled_source/com/google/common/collect/Iterables.java` | java | unsupported-operation | SecondLifeViewer|FirestormViewer |
| `lumiya_decompiled_source/com/google/common/collect/Iterators.java` | java | decompilation-stub|unsupported-operation | SecondLifeViewer|FirestormViewer |
| `lumiya_decompiled_source/com/google/common/collect/LinkedListMultimap.java` | java | unsupported-operation | SecondLifeViewer|FirestormViewer |
| `lumiya_decompiled_source/com/google/common/collect/MapMakerInternalMap.java` | java | decompilation-stub|unsupported-operation | SecondLifeViewer|FirestormViewer |
| `lumiya_decompiled_source/com/google/common/collect/Maps.java` | java | unsupported-operation | SecondLifeViewer|FirestormViewer |
| `lumiya_decompiled_source/com/google/common/collect/Multimaps.java` | java | unsupported-operation | SecondLifeViewer|FirestormViewer |
| `lumiya_decompiled_source/com/google/common/collect/Multisets.java` | java | unsupported-operation | SecondLifeViewer|FirestormViewer |
| `lumiya_decompiled_source/com/google/common/collect/RegularImmutableSortedSet.java` | java | decompilation-stub|unsupported-operation | SecondLifeViewer|FirestormViewer |
| `lumiya_decompiled_source/com/google/common/collect/Sets.java` | java | unsupported-operation | SecondLifeViewer|FirestormViewer |
| `lumiya_decompiled_source/com/google/common/collect/Tables.java` | java | unsupported-operation | SecondLifeViewer|FirestormViewer |
| `lumiya_decompiled_source/com/google/common/collect/TransformedListIterator.java` | java | unsupported-operation | SecondLifeViewer|FirestormViewer |
| `lumiya_decompiled_source/com/google/common/collect/UnmodifiableIterator.java` | java | unsupported-operation | SecondLifeViewer|FirestormViewer |
| `lumiya_decompiled_source/com/google/common/collect/UnmodifiableListIterator.java` | java | unsupported-operation | SecondLifeViewer|FirestormViewer |
| `lumiya_decompiled_source/com/google/common/collect/UnmodifiableSortedMultiset.java` | java | unsupported-operation | SecondLifeViewer|FirestormViewer |
| `lumiya_decompiled_source/com/google/common/eventbus/Dispatcher.java` | java | decompilation-stub|unsupported-operation | LibreMetaverse|SecondLifeViewer|FirestormViewer |
| `lumiya_decompiled_source/com/google/common/eventbus/SubscriberRegistry.java` | java | decompilation-stub|unsupported-operation | SecondLifeViewer|FirestormViewer |
| `lumiya_decompiled_source/com/google/common/io/BaseEncoding.java` | java | decompilation-stub|unsupported-operation | SecondLifeViewer|FirestormViewer |
| `lumiya_decompiled_source/com/google/common/io/LittleEndianDataInputStream.java` | java | unsupported-operation | SecondLifeViewer|FirestormViewer |
| `lumiya_decompiled_source/com/google/common/math/IntMath.java` | java | decompilation-stub|unsupported-operation | SecondLifeViewer|FirestormViewer |
| `lumiya_decompiled_source/com/google/common/primitives/Booleans.java` | java | decompilation-stub|unsupported-operation | SecondLifeViewer|FirestormViewer |
| `lumiya_decompiled_source/com/google/common/primitives/Bytes.java` | java | decompilation-stub|unsupported-operation | SecondLifeViewer|FirestormViewer |
| `lumiya_decompiled_source/com/google/common/primitives/Chars.java` | java | decompilation-stub|unsupported-operation | SecondLifeViewer|FirestormViewer |
| `lumiya_decompiled_source/com/google/common/primitives/Doubles.java` | java | decompilation-stub|unsupported-operation | SecondLifeViewer|FirestormViewer |
| `lumiya_decompiled_source/com/google/common/primitives/Floats.java` | java | decompilation-stub|unsupported-operation | SecondLifeViewer|FirestormViewer |
| `lumiya_decompiled_source/com/google/common/primitives/Ints.java` | java | decompilation-stub|unsupported-operation | SecondLifeViewer|FirestormViewer |
| `lumiya_decompiled_source/com/google/common/primitives/Longs.java` | java | decompilation-stub|unsupported-operation | SecondLifeViewer|FirestormViewer |
| `lumiya_decompiled_source/com/google/common/primitives/Shorts.java` | java | decompilation-stub|unsupported-operation | SecondLifeViewer|FirestormViewer |
| `lumiya_decompiled_source/com/google/common/reflect/ImmutableTypeToInstanceMap.java` | java | unsupported-operation | SecondLifeViewer|FirestormViewer |
| `lumiya_decompiled_source/com/google/common/reflect/MutableTypeToInstanceMap.java` | java | unsupported-operation | SecondLifeViewer|FirestormViewer |
| `lumiya_decompiled_source/com/google/common/reflect/TypeToken.java` | java | unsupported-operation | SecondLifeViewer|FirestormViewer |
| `lumiya_decompiled_source/com/google/common/reflect/TypeVisitor.java` | java | decompilation-stub|unsupported-operation | SecondLifeViewer|FirestormViewer |
| `lumiya_decompiled_source/com/google/common/reflect/Types.java` | java | unsupported-operation | SecondLifeViewer|FirestormViewer |
| `lumiya_decompiled_source/com/google/common/util/concurrent/AbstractScheduledService.java` | java | unsupported-operation | SecondLifeViewer|FirestormViewer |
| `lumiya_decompiled_source/com/google/common/util/concurrent/Futures.java` | java | decompilation-stub|unsupported-operation | SecondLifeViewer|FirestormViewer |
| `lumiya_decompiled_source/com/google/common/util/concurrent/ListenerCallQueue.java` | java | decompilation-stub|unsupported-operation | SecondLifeViewer|FirestormViewer |
| `lumiya_decompiled_source/com/google/common/util/concurrent/Monitor.java` | java | decompilation-stub|unsupported-operation | SecondLifeViewer|FirestormViewer |
| `lumiya_decompiled_source/com/google/gson/JsonElement.java` | java | unsupported-operation | SecondLifeViewer|FirestormViewer |
| `lumiya_decompiled_source/com/google/gson/JsonStreamParser.java` | java | unsupported-operation | SecondLifeViewer|FirestormViewer |
| `lumiya_decompiled_source/com/google/gson/internal/C$Gson$Preconditions.java` | java | unsupported-operation | SecondLifeViewer|FirestormViewer |
| `lumiya_decompiled_source/com/google/gson/internal/C$Gson$Types.java` | java | unsupported-operation | SecondLifeViewer|FirestormViewer |
| `lumiya_decompiled_source/com/google/gson/internal/Primitives.java` | java | unsupported-operation | SecondLifeViewer|FirestormViewer |
| `lumiya_decompiled_source/com/google/gson/internal/Streams.java` | java | unsupported-operation | SecondLifeViewer|FirestormViewer |
| `lumiya_decompiled_source/com/google/gson/internal/UnsafeAllocator.java` | java | unsupported-operation | SecondLifeViewer|FirestormViewer |
| `lumiya_decompiled_source/com/google/gson/internal/bind/TypeAdapters.java` | java | unsupported-operation | SecondLifeViewer|FirestormViewer |
| `lumiya_decompiled_source/com/google/gson/internal/bind/util/ISO8601Utils.java` | java | decompilation-stub|unsupported-operation | LibreMetaverse|SecondLifeViewer|FirestormViewer |
| `lumiya_decompiled_source/com/google/gson/stream/JsonReader.java` | java | decompilation-stub|unsupported-operation | SecondLifeViewer|FirestormViewer |
| `lumiya_decompiled_source/com/google/gson/stream/JsonWriter.java` | java | decompilation-stub|unsupported-operation | SecondLifeViewer|FirestormViewer |
| `lumiya_decompiled_source/com/google/protobuf/nano/InternalNano.java` | java | decompilation-stub|unsupported-operation | LibreMetaverse|SecondLifeViewer|FirestormViewer |
| `lumiya_decompiled_source/com/google/thirdparty/publicsuffix/TrieParser.java` | java | decompilation-stub|unsupported-operation | LibreMetaverse|SecondLifeViewer|FirestormViewer |
| `lumiya_decompiled_source/com/google/vr/cardboard/AndroidNCompat.java` | java | unsupported-operation | SecondLifeViewer|FirestormViewer |
| `lumiya_decompiled_source/com/google/vr/cardboard/ContentProviderVrParamsProvider.java` | java | decompilation-stub|unsupported-operation | LibreMetaverse|SecondLifeViewer|FirestormViewer |
| `lumiya_decompiled_source/com/google/vr/cardboard/DisplaySynchronizer.java` | java | decompilation-stub|unsupported-operation | SecondLifeViewer|FirestormViewer |
| `lumiya_decompiled_source/com/google/vr/cardboard/PerfMonitor.java` | java | unsupported-operation | SecondLifeViewer|FirestormViewer |
| `lumiya_decompiled_source/com/google/vr/ndk/base/DaydreamApi.java` | java | decompilation-stub|unsupported-operation | SecondLifeViewer|FirestormViewer |
| `lumiya_decompiled_source/com/google/vr/ndk/base/GvrLayout.java` | java | decompilation-stub|unsupported-operation | SecondLifeViewer|FirestormViewer |
| `lumiya_decompiled_source/com/lumiyaviewer/lumiya/render/glres/GLSyncLoadQueue.java` | java | decompilation-stub|unsupported-operation | SecondLifeViewer|FirestormViewer |
| `lumiya_decompiled_source/com/lumiyaviewer/lumiya/res/anim/AnimationCache.java` | java | decompilation-stub|unsupported-operation | LibreMetaverse|SecondLifeViewer|FirestormViewer |
| `lumiya_decompiled_source/com/lumiyaviewer/lumiya/res/collections/PriorityBinQueue.java` | java | unsupported-operation | SecondLifeViewer|FirestormViewer |
| `lumiya_decompiled_source/com/lumiyaviewer/lumiya/res/collections/WeakQueue.java` | java | unsupported-operation | SecondLifeViewer|FirestormViewer |
| `lumiya_decompiled_source/com/lumiyaviewer/lumiya/res/textures/TextureCompressedCache.java` | java | decompilation-stub|unsupported-operation | SecondLifeViewer|FirestormViewer |
| `lumiya_decompiled_source/com/lumiyaviewer/lumiya/slproto/SLCircuit.java` | java | decompilation-stub|unsupported-operation | LLSD|LibreMetaverse|SecondLifeViewer|FirestormViewer |
| `lumiya_decompiled_source/com/lumiyaviewer/lumiya/slproto/SLParcelInfo.java` | java | decompilation-stub|unsupported-operation | LibreMetaverse|SecondLifeViewer|FirestormViewer |
| `lumiya_decompiled_source/com/lumiyaviewer/lumiya/slproto/auth/SLAuth.java` | java | decompilation-stub|unsupported-operation | LibreMetaverse|SecondLifeViewer|FirestormViewer |
| `lumiya_decompiled_source/com/lumiyaviewer/lumiya/slproto/avatar/SLAnimatedMeshData.java` | java | decompilation-stub|unsupported-operation | LibreMetaverse|SecondLifeViewer|FirestormViewer |
| `lumiya_decompiled_source/com/lumiyaviewer/lumiya/slproto/baker/BakeLayer.java` | java | decompilation-stub|unsupported-operation | LibreMetaverse|SecondLifeViewer|FirestormViewer |
| `lumiya_decompiled_source/com/lumiyaviewer/lumiya/slproto/inventory/SLInventoryHTTPFetchRequest.java` | java | decompilation-stub|unsupported-operation | LLSD|LibreMetaverse|SecondLifeViewer|FirestormViewer |
| `lumiya_decompiled_source/com/lumiyaviewer/lumiya/slproto/llsd/EnhancedLLSDUtils.java` | java | unsupported-operation | LLSD|LibreMetaverse|SecondLifeViewer|FirestormViewer |
| `lumiya_decompiled_source/com/lumiyaviewer/lumiya/slproto/modules/SLMinimap.java` | java | decompilation-stub|unsupported-operation | LibreMetaverse|SecondLifeViewer|FirestormViewer |
| `lumiya_decompiled_source/com/lumiyaviewer/lumiya/slproto/modules/rlv/RLVRestrictions.java` | java | decompilation-stub|unsupported-operation | LibreMetaverse|SecondLifeViewer|FirestormViewer |
| `lumiya_decompiled_source/com/lumiyaviewer/lumiya/slproto/objects/SLObjectInfo.java` | java | decompilation-stub|unsupported-operation | LibreMetaverse|SecondLifeViewer|FirestormViewer |
| `lumiya_decompiled_source/com/lumiyaviewer/lumiya/slproto/users/manager/ActiveChattersManager.java` | java | decompilation-stub|unsupported-operation | LibreMetaverse|SecondLifeViewer|FirestormViewer |
| `lumiya_decompiled_source/com/lumiyaviewer/lumiya/slproto/users/manager/ObjectPopupsManager.java` | java | decompilation-stub|unsupported-operation | SecondLifeViewer|FirestormViewer |
| `lumiya_decompiled_source/com/lumiyaviewer/lumiya/slproto/users/manager/SyncManager.java` | java | decompilation-stub|unsupported-operation | SecondLifeViewer|FirestormViewer |
| `lumiya_decompiled_source/com/lumiyaviewer/lumiya/ui/chat/ContactsFragment.java` | java | decompilation-stub|unsupported-operation | SecondLifeViewer|FirestormViewer |
| `lumiya_decompiled_source/com/lumiyaviewer/lumiya/ui/common/DetailsActivity.java` | java | decompilation-stub|unsupported-operation | SecondLifeViewer|FirestormViewer |
| `lumiya_decompiled_source/com/lumiyaviewer/lumiya/ui/common/MasterDetailsActivity.java` | java | decompilation-stub|unsupported-operation | LibreMetaverse|SecondLifeViewer|FirestormViewer |
| `lumiya_decompiled_source/com/lumiyaviewer/lumiya/ui/notify/OnlineNotificationInfo.java` | java | decompilation-stub|unsupported-operation | LibreMetaverse|SecondLifeViewer|FirestormViewer |
| `lumiya_decompiled_source/com/lumiyaviewer/lumiya/ui/render/WorldViewActivity.java` | java | decompilation-stub|unsupported-operation | LibreMetaverse|SecondLifeViewer|FirestormViewer |
| `lumiya_decompiled_source/com/lumiyaviewer/lumiya/utils/LinkedTreeNode.java` | java | unsupported-operation | SecondLifeViewer|FirestormViewer |
| `lumiya_decompiled_source/com/lumiyaviewer/lumiya/utils/PriorityBinQueue.java` | java | decompilation-stub|unsupported-operation | SecondLifeViewer|FirestormViewer |
| `lumiya_decompiled_source/com/lumiyaviewer/lumiya/utils/reqset/WeakRequestSet.java` | java | decompilation-stub|unsupported-operation | SecondLifeViewer|FirestormViewer |
| `lumiya_decompiled_source/com/lumiyaviewer/lumiya/utils/wlist/ChunkedListLoader.java` | java | decompilation-stub|unsupported-operation | SecondLifeViewer|FirestormViewer |
| `lumiya_decompiled_source/com/lumiyaviewer/lumiya/voiceintf/VoicePluginServiceConnection.java` | java | decompilation-stub|unsupported-operation | LibreMetaverse|SecondLifeViewer|FirestormViewer |
| `lumiya_decompiled_source/com/nineoldandroids/animation/AnimatorInflater.java` | java | decompilation-stub|unsupported-operation | SecondLifeViewer|FirestormViewer |
| `lumiya_decompiled_source/com/nineoldandroids/util/Property.java` | java | unsupported-operation | SecondLifeViewer|FirestormViewer |
| `lumiya_decompiled_source/com/nineoldandroids/util/ReflectiveProperty.java` | java | unsupported-operation | SecondLifeViewer|FirestormViewer |
| `lumiya_decompiled_source/de/greenrobot/dao/async/AsyncOperationExecutor.java` | java | decompilation-stub|unsupported-operation | SecondLifeViewer|FirestormViewer |
| `lumiya_decompiled_source/de/greenrobot/dao/internal/FastCursor.java` | java | unsupported-operation | SecondLifeViewer|FirestormViewer |
| `lumiya_decompiled_source/de/greenrobot/dao/query/LazyList.java` | java | unsupported-operation | SecondLifeViewer|FirestormViewer |
| `lumiya_decompiled_source/de/greenrobot/dao/query/QueryBuilder.java` | java | unsupported-operation | SecondLifeViewer|FirestormViewer |
| `lumiya_decompiled_source/okhttp3/Cookie.java` | java | decompilation-stub|unsupported-operation | SecondLifeViewer|FirestormViewer |
| `lumiya_decompiled_source/okhttp3/HttpUrl.java` | java | decompilation-stub|unsupported-operation | SecondLifeViewer|FirestormViewer |
| `lumiya_decompiled_source/okhttp3/internal/cache/CacheStrategy.java` | java | decompilation-stub|unsupported-operation | SecondLifeViewer|FirestormViewer |
| `lumiya_decompiled_source/okhttp3/internal/cache2/FileOperator.java` | java | decompilation-stub|unsupported-operation | SecondLifeViewer|FirestormViewer |
| `lumiya_decompiled_source/okhttp3/internal/cache2/Relay.java` | java | decompilation-stub|unsupported-operation | SecondLifeViewer|FirestormViewer |
| `lumiya_decompiled_source/okhttp3/internal/http2/Http2Connection.java` | java | decompilation-stub|unsupported-operation | SecondLifeViewer|FirestormViewer |
| `lumiya_decompiled_source/okhttp3/internal/platform/Jdk9Platform.java` | java | unsupported-operation | SecondLifeViewer|FirestormViewer |
| `lumiya_decompiled_source/okhttp3/internal/tls/DistinguishedNameParser.java` | java | decompilation-stub|unsupported-operation | SecondLifeViewer|FirestormViewer |
| `lumiya_decompiled_source/okhttp3/internal/ws/WebSocketWriter.java` | java | decompilation-stub|unsupported-operation | LibreMetaverse|SecondLifeViewer|FirestormViewer |
| `lumiya_decompiled_source/okio/Buffer.java` | java | decompilation-stub|unsupported-operation | SecondLifeViewer|FirestormViewer |
| `lumiya_decompiled_source/uk/co/senab/photoview/PhotoViewAttacher.java` | java | decompilation-stub|unsupported-operation | SecondLifeViewer|FirestormViewer |

## Full per-file inventory
See `docs/broken_code_inventory_2026-04-24.csv` for every file and its status/source mapping.
