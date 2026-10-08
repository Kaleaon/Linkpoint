package com.linkpoint.protocol.capabilities

import com.linkpoint.protocol.llsd.LLSDValue

class FakeCapabilityRequester : CapabilityRequester {
    data class Invocation(val capName: String, val body: LLSDValue?)

    val invocations = mutableListOf<Invocation>()
    private val responses = mutableMapOf<String, LLSDValue?>()

    fun enqueueResponse(capName: String, response: LLSDValue?) {
        responses[capName] = response
    }

    override suspend fun request(capName: String, body: LLSDValue?): LLSDValue? {
        invocations.add(Invocation(capName, body))
        return responses[capName]
    }

    override fun getCapability(name: String): String? = "mock://$name"

    override fun hasCapability(name: String): Boolean = true
}
