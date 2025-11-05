# Mobile UI Translation Guide

This guide documents the existing Lumiya (Java-based) mobile UI architecture and describes translation approaches for Kotlin (Jetpack Compose / XML) and Apple platforms (SwiftUI / UIKit). The intent is to reuse legacy Java assets while transitioning toward modern, cross-platform UI patterns that align with the universal integration map.

## 1. Legacy Lumiya UI Architecture (Java Base)

- **Structure**: Android application built with Activities, Fragments, and custom `View` subclasses. Layouts defined in XML under `res/layout`, using `ListView`, `ViewPager`, and `SurfaceView` for rendering.
- **Navigation**: Manual fragment transactions handled via `FragmentManager`; context-specific menus implemented with `ActionBar`/`Toolbar`.
- **Data Binding**: Imperative updates via `Handler`/`AsyncTask`, with UI components observing network events from the viewer core.
- **Rendering**: OpenGL ES surface embedded through `GLSurfaceView`, managed by renderer thread from the native viewer core.
- **Key Packages** (representative):
  - `com.lumiyaviewer.lumiya.ui` – Activities/Fragments (e.g., `MainActivity`, `ChatFragment`).
  - `com.lumiyaviewer.lumiya.widgets` – Custom views for inventory lists, chat windows.
  - `com.lumiyaviewer.lumiya.adapters` – Adapter classes bridging data models to UI lists.

### Example: Legacy Java Activity

```java
public class MainActivity extends FragmentActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private GLSurfaceView glSurfaceView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        drawerLayout = findViewById(R.id.drawer_layout);
        glSurfaceView = findViewById(R.id.viewer_surface);

        if (savedInstanceState == null) {
            getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.content_frame, new ChatFragment())
                .commit();
        }
    }
}
```

## 2. Translating Java to Kotlin (Android)

### 2.1 Direct Kotlin Conversion

- Use IDE tools (`Code → Convert Java File to Kotlin`) to obtain Kotlin baseline.
- Replace anonymous inner classes with lambdas; leverage `by viewModels()` for fragment-scoped view models.
- Adopt `coroutines` + `Flow` for asynchronous updates.

```kotlin
class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var glSurfaceView: GLSurfaceView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        drawerLayout = findViewById(R.id.drawer_layout)
        glSurfaceView = findViewById(R.id.viewer_surface)

        if (savedInstanceState == null) {
            supportFragmentManager.commit {
                replace(R.id.content_frame, ChatFragment())
            }
        }
    }
}
```

### 2.2 Moving to Jetpack Compose

| Legacy Java Component | Compose Equivalent | Migration Notes |
| --- | --- | --- |
| `Activity` with fragments | `ComponentActivity` + `setContent {}` | Compose manages navigation via `Navigation Compose` |
| `ListView` / `RecyclerView` | `LazyColumn` / `LazyRow` | Convert `Adapter` data to immutable list of state objects |
| `Toolbar` menus | `TopAppBar`, `DropdownMenu` | Use state hoisting for menu visibility |
| `GLSurfaceView` | Compose `AndroidView` hosting `GLSurfaceView` | Wrap existing renderer until Filament/Compose integration is ready |

```kotlin
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ViewerApp()
        }
    }
}

@Composable
fun ViewerApp(viewModel: ViewerViewModel = hiltViewModel()) {
    Scaffold(topBar = { ViewerTopBar() }) { padding ->
        ViewerContent(
            state = viewModel.uiState.collectAsState().value,
            modifier = Modifier.padding(padding)
        )
    }
}

@Composable
fun ViewerContent(state: ViewerUiState, modifier: Modifier = Modifier) {
    Column(modifier.fillMaxSize()) {
        LazyColumn(modifier.weight(1f)) {
            items(state.chatMessages) { message -> ChatBubble(message) }
        }
        AndroidView(factory = { context ->
            GLSurfaceView(context).apply { setRenderer(state.renderer) }
        })
    }
}
```

## 3. Translating Android XML to SwiftUI (Apple Platforms)

### 3.1 UI Layout Mapping

| Android XML Concept | SwiftUI Equivalent | Notes |
| --- | --- | --- |
| `LinearLayout` (vertical) | `VStack` | Use `Spacer()` for distribution |
| `DrawerLayout` | `NavigationSplitView` / `NavigationStack` | Implement multi-column navigation |
| `RecyclerView` | `List` / `ForEach` | Bind to `ObservableObject` collections |
| `GLSurfaceView` | `MetalView` / custom `UIViewRepresentable` | Wrap Metal renderer or adopt `SceneKit` for prototype |

### 3.2 Example SwiftUI Translation

```swift
struct ViewerApp: View {
    @StateObject private var viewModel = ViewerViewModel()

    var body: some View {
        NavigationSplitView {
            List(viewModel.chatMessages) { message in
                ChatRow(message: message)
            }
        } detail: {
            MetalView(renderer: viewModel.renderer)
                .ignoresSafeArea()
        }
        .toolbar { ViewerToolbar(actions: viewModel.actions) }
    }
}

struct MetalView: UIViewRepresentable {
    let renderer: ViewerRenderer

    func makeUIView(context: Context) -> MTKView {
        let view = MTKView()
        view.device = MTLCreateSystemDefaultDevice()
        view.delegate = renderer
        return view
    }

    func updateUIView(_ uiView: MTKView, context: Context) {}
}
```

## 4. Cross-Platform Command Bus & State Model

- **Command Bus**: Define platform-agnostic commands (e.g., `OpenInventory`, `SendChat`) in shared protobuf schema. Generate Kotlin/Swift bindings for use in Compose/SwiftUI layers.
- **State Model**: Maintain `ViewerUiState` struct/class with mirrored fields across Kotlin and Swift (leverage Kotlin Multiplatform shared models or protobuf DTOs).
- **Rendering Bridge**: Until Metal renderer is available, reuse Android rendering via streaming or remote interoperability; long-term target is Filament (Android) and Metal/WGPU (Apple).

## 5. Automation & Documentation

- Store Java → Kotlin conversion notes in `docs/ADR/adr-android-modernization.md`.
- Use Dokka (Kotlin) and DocC (Swift) to auto-generate API docs from source.
- Integrate lint checks (Detekt, SwiftLint) and Compose/SwiftUI preview snapshots in CI.
- Maintain translation playbooks in GitHub wiki or MkDocs site referencing this guide.

## 6. Next Actions

1. Inventory existing Java Activities/Fragments; categorize by feature (chat, inventory, settings).
2. Create Kotlin Compose prototypes per feature, ensuring parity with legacy XML.
3. Extract shared UI state and commands into multiplatform module.
4. Scaffold SwiftUI project using skeleton repo, generating bindings from shared schema.
5. Establish automated snapshot/regression tests to guarantee UI parity across platforms.

## 7. VR-Specific UI Considerations

- **Firestorm VR Mod Learnings**: Legacy wxWidgets UI is projected as curved panels in VR. Compose/SwiftUI layers must emit metadata (panel type, ideal distance) so VR renderers can reconstruct similar overlays.
- **Layout Metadata Schema**: Extend shared command/state models to include hints like `displayMode = FLAT_PANEL | CURVED_PANEL`, `preferredDistanceMeters`, `interactionMode = POINTER | DIRECT`.
- **Renderer Adaptation**: Android/Java `GLSurfaceView` and Swift `MetalView` should support rendering UI surfaces off-screen (e.g., via `RenderNode` or `CALayer`) for use in OpenXR compositors.
- **Input Mapping**: Introduce VR input events (raycast pointer, trigger, grip) in the command bus. Compose and SwiftUI components expose semantics to handle focus/activation for VR controllers.
- **Performance**: Maintain 90 Hz target by throttling heavy UI recompositions. Use selective updates and memoization in Compose/SwiftUI to reduce GPU load.
- **Testing**: Incorporate VR snapshot tests by capturing off-screen UI textures and verifying layout metrics. Manual QA should validate comfort metrics (FOV, legibility) using guidelines from Firestorm VR Mod.

For deeper research and recommendations, see `docs/vr_ui_research.md`.
