package com.linkpoint.ui.navigation

import android.content.Intent
import androidx.navigation.NavHostController

/** Standard conversion between legacy activity intent extras and nav route args. */
object RouteArgs {
    const val EXTRA_IM_SESSION_ID = "route_im_session_id"
    const val EXTRA_SLURL_REGION = "route_slurl_region"
    const val EXTRA_SLURL_X = "route_slurl_x"
    const val EXTRA_SLURL_Y = "route_slurl_y"
    const val EXTRA_SLURL_Z = "route_slurl_z"
    const val EXTRA_USE_COMPOSE = "route_use_compose"

    data class Chat(val sessionId: String? = null)
    data class Login(val slurlRegion: String? = null, val x: Float = 128f, val y: Float = 128f, val z: Float = 0f)

    fun chatFromIntent(intent: Intent?): Chat {
        val sessionId = intent?.getStringExtra(EXTRA_IM_SESSION_ID)
        return Chat(sessionId = sessionId)
    }

    fun putChat(intent: Intent, args: Chat): Intent = intent.apply {
        args.sessionId?.let { putExtra(EXTRA_IM_SESSION_ID, it) }
    }

    fun loginFromIntent(intent: Intent?): Login {
        return Login(
            slurlRegion = intent?.getStringExtra(EXTRA_SLURL_REGION),
            x = intent?.getFloatExtra(EXTRA_SLURL_X, 128f) ?: 128f,
            y = intent?.getFloatExtra(EXTRA_SLURL_Y, 128f) ?: 128f,
            z = intent?.getFloatExtra(EXTRA_SLURL_Z, 0f) ?: 0f
        )
    }

    fun putLogin(intent: Intent, args: Login): Intent = intent.apply {
        args.slurlRegion?.let { putExtra(EXTRA_SLURL_REGION, it) }
        putExtra(EXTRA_SLURL_X, args.x)
        putExtra(EXTRA_SLURL_Y, args.y)
        putExtra(EXTRA_SLURL_Z, args.z)
    }
}

fun finishOrPopBackStack(navController: NavHostController?, finish: () -> Unit) {
    val popped = navController?.popBackStack() ?: false
    if (!popped) finish()
}
