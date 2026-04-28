package com.linkpoint.linden.llfilesystem

import com.linkpoint.linden.llcommon.LLQueuedThread
import com.linkpoint.linden.llcommon.LLUUID
import com.linkpoint.linden.llcommon.AssetType

typealias VFSCallback = (id: LLUUID, type: AssetType, data: ByteArray?) -> Unit

class LLVFSThread(
    private val fileSystem: LLFileSystem
) : LLQueuedThread("LLVFSThread") {

    sealed class VFSPayload {
        class Read(val id: LLUUID, val type: AssetType, val callback: VFSCallback) : VFSPayload()
        class Write(val id: LLUUID, val type: AssetType, val data: ByteArray, val callback: VFSCallback?) : VFSPayload()
    }

    fun readFile(id: LLUUID, type: AssetType, callback: VFSCallback): Long =
        addRequest(payload = VFSPayload.Read(id, type, callback))

    fun writeFile(id: LLUUID, type: AssetType, data: ByteArray, callback: VFSCallback? = null): Long =
        addRequest(payload = VFSPayload.Write(id, type, data, callback))

    override fun processRequest(request: Request) {
        when (val p = request.payload) {
            is VFSPayload.Read -> {
                val data = fileSystem.get(p.id, p.type)
                p.callback(p.id, p.type, data)
            }
            is VFSPayload.Write -> {
                fileSystem.put(p.id, p.type, p.data)
                p.callback?.invoke(p.id, p.type, p.data)
            }
            else -> {}
        }
    }
}
