package com.linkpoint.testing

import android.app.Application

/**
 * Lightweight Application used by Robolectric-driven unit tests.
 *
 * The production [com.linkpoint.LinkpointApp.onCreate] does heavy work
 * (Conscrypt JCA install, encrypted SharedPreferences keystore unlock,
 * NetworkManager bootstrap, Filament native init…) — none of which works
 * on a JVM-only test environment. This class is the explicit Application
 * Robolectric instantiates so production startup logic stays inert in
 * unit tests.
 *
 * Usage: annotate the test class with
 * `@Config(application = RobolectricTestApp::class)` or rely on the
 * project-wide `robolectric.properties` `application=` directive.
 */
open class RobolectricTestApp : Application()
