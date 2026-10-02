package com.linkpoint.network

import android.content.Context
import com.linkpoint.inventory.AisHttpRequest
import com.linkpoint.inventory.AisHttpResponse
import com.linkpoint.inventory.AisTransport

/**
 * Legacy Apache HttpAdapter replacement wrapper.
 *
 * @deprecated Replaced by [CronetTransportAdapter] in accordance with modern Android
 * transport requirements. Purged legacy `org.apache.http` compatibility library dependencies.
 * Directs all requests through [CronetTransportAdapter] with dynamic fallback to standard HTTP transport.
 */
@Deprecated(
    message = "ApacheHttpAdapter is deprecated. Use CronetTransportAdapter for asynchronous Cronet execution.",
    replaceWith = ReplaceWith("CronetTransportAdapter")
)
class ApacheHttpAdapter(
    private val delegate: CronetTransportAdapter
) : AisTransport {

    constructor(context: Context) : this(CronetTransportAdapter.create(context))

    override suspend fun execute(request: AisHttpRequest): AisHttpResponse {
        return delegate.execute(request)
    }
}
