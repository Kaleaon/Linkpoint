# Linkpoint testing infrastructure

This is a quick reference for the test stack that runs under
`./gradlew :testStableDebugUnitTest` (and equivalents). Most of it landed
alongside the Linkpoint 2.0 UI work — before that, 40 tests were red on
every CI run because the JVM unit-test classpath couldn't resolve Android
framework classes (`android.util.Log` natively, encrypted SharedPreferences,
SurfaceTexture, Conscrypt JNI, etc.).

## Stack

| Concern | Tool | Notes |
| --- | --- | --- |
| JVM-side Android framework stubs | **Robolectric 4.11.1** | Pinned to 4.11.x — 4.12+ requires JVM 11. |
| AndroidX test runner | `androidx.test:core-ktx`, `runner`, `rules`, `junit-ktx` | Provides `AndroidJUnit4` which delegates to Robolectric on JVM. |
| Mocking | **Mockito 4.11 + mockito-kotlin 4.1** | Pinned to 4.x for JVM 1.8 compat. |
| Coroutines | `kotlinx-coroutines-test 1.7.3` | TestScope, runTest, etc. |
| Flow / StateFlow assertions | **Turbine 1.0** | Cleaner than collecting into lists. |
| OkHttp integration | `okhttp 4.12 + mockwebserver` | For protocol tests that hit a fake server. |
| Cryptography | `conscrypt-openjdk-uber 2.5.2` | Replaces `conscrypt-android` on the test classpath; see classpath note below. |

## Robolectric configuration

`src/test/resources/robolectric.properties` pins:

```
sdk=33
application=com.linkpoint.testing.RobolectricTestApp
```

Why a custom `RobolectricTestApp`? The production
[`com.linkpoint.LinkpointApp.onCreate`](../src/main/java/com/linkpoint/LinkpointApp.kt)
installs Conscrypt as the JCA provider, unlocks an encrypted-prefs keystore,
boots the network manager and several native libraries — none of which
exists on a JVM-only test environment. The stub Application is
`open class RobolectricTestApp : Application()` — empty on purpose.

If a test really does need the production Application's behaviour, override
it for that class:

```kotlin
@RunWith(AndroidJUnit4::class)
@Config(application = LinkpointApp::class)
class MyIntegrationTest { ... }
```

## conscrypt-android vs conscrypt-openjdk-uber

The production app pulls in `org.conscrypt:conscrypt-android:2.5.2`. Its
`libconscrypt_jni.so` only ships for ARM/x86 Android, so any JVM unit
test that touches HTTPS or the JCA provider chain crashes with
`UnsatisfiedLinkError: no conscrypt_jni in java.library.path`.

We swap it for `conscrypt-openjdk-uber:2.5.2` on every unit-test
configuration, via a `dependencySubstitution` rule in `build.gradle.kts`.
The two artefacts share the same `org.conscrypt` Java package but have
different jar signers — the substitution avoids the
`SecurityException: signer information does not match` that hits when
both are on the classpath.

## Annotating an existing test for Robolectric

For tests whose production code calls `Log.*`, `Resources.getSystem()`,
`Context.getSystemService(...)` or any other Android framework API:

```kotlin
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MyUnitTest { ... }
```

`AndroidJUnit4` resolves to `RobolectricTestRunner` on the JVM and to
the on-device runner inside `androidTest`, so the same test class can be
moved between source sets without changes.

## Smoke tests for the Linkpoint 2.0 nav graph

`Linkpoint2WiringTest` (under
`src/test/kotlin/com/linkpoint/ui/linkpoint2/Linkpoint2WiringTest.kt`)
locks down five invariants:

1. Every `Routes.*` constant has a `RouteMetadata` entry.
2. The 5-tab bottom bar has exactly five entries and every drawer route
   resolves.
3. `resolveRouteMetadata` matches parameterised paths
   (`profile/{userId}`, `places/{placeId}`, `group_profile/{groupId}`).
4. `Routes.profile("me")` and `Routes.groupProfile(...)` produce paths
   that resolve to the same metadata as their canonical constants.
5. Routes flagged as full-bleed (login, world, modals, onboarding) have
   `MenuPlacement.NONE`.

Add to it whenever a new `Routes.*` constant lands.

## Known disabled tests

| Test | Reason |
| --- | --- |
| `StateComponentsSnapshotTest` | Paparazzi 1.3.5 collides with the conscrypt-android AAR's signed `org.conscrypt.R` class. Tracked in the build.gradle plugins block. Snapshots will move to AndroidJUnit4 instrumented tests in a follow-up PR. |
| `LLSDStreamingParserLimitsTest > parseBinary rejects huge arrays` | Real production parser bug — does not reject oversized arrays. Predates the UI work; reassign to the protocol team. |
| `MessageParserConformanceFixtureTest > message id and payload extraction fixtures conform` | Real production parser bug — extracts message id `-256` instead of `-65388`. Predates the UI work; reassign to the protocol team. |

Last verified counts: **266 tests, 2 failed (above), 12 ignored.**
