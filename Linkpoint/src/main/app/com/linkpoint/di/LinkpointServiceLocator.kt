package com.linkpoint.di

import android.app.Application
import com.linkpoint.chat.ChatRepository
import com.linkpoint.chat.LinkpointChatClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit

/**
 * Minimal service container for the modernized Linkpoint app.
 * Keeps heavy singletons (OkHttp, coroutine scopes, repositories) in one place.
 */
class LinkpointServiceLocator(app: Application) {

    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val okHttpClient: OkHttpClient = OkHttpClient.Builder()
        .callTimeout(20, TimeUnit.SECONDS)
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(20, TimeUnit.SECONDS)
        .writeTimeout(20, TimeUnit.SECONDS)
        .pingInterval(25, TimeUnit.SECONDS)
        .retryOnConnectionFailure(true)
        .build()

    private val chatClient = LinkpointChatClient(okHttpClient)

    val chatRepository: ChatRepository = ChatRepository(chatClient, applicationScope)
}
