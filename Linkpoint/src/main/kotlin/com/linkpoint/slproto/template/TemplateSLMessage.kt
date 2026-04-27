package com.linkpoint.slproto.template

import com.linkpoint.slproto.SLCircuit
import com.linkpoint.slproto.SLMessage

class TemplateSLMessage(
    val template: TemplateMessage,
    val blocks: Map<String, List<Map<String, Any?>>>
) : SLMessage() {

    override fun getMessageName(): String = template.name

    override fun encode(buffer: java.nio.ByteBuffer) {
        throw UnsupportedOperationException("Template-generated message encoding not implemented")
    }

    override fun decode(buffer: java.nio.ByteBuffer) {
        throw UnsupportedOperationException("Template-generated message decoding handled externally")
    }

    override fun handleMessage(circuit: SLCircuit) {
        circuit.DefaultMessageHandler(this)
    }
}
