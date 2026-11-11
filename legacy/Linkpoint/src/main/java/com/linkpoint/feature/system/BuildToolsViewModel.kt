package com.linkpoint.feature.system

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.linkpoint.objects.Axis
import com.linkpoint.objects.BuildTools
import com.linkpoint.objects.ObjectManager
import com.linkpoint.objects.PrimCreateParams
import com.linkpoint.protocol.types.LLVector3
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class BuildToolsViewModel(
    private val objectManager: ObjectManager,
    private val buildTools: BuildTools
) : ViewModel() {

    val uiState: StateFlow<BuildToolsUiState> = objectManager.selectedObjects
        .map { selected ->
            if (selected.isEmpty()) {
                BuildToolsUiState.EmptySelection
            } else {
                val firstId = selected.first()
                val obj = objectManager.getObject(firstId)
                if (obj != null) {
                    val nameStr = if (obj.name.isNotBlank()) obj.name else "Selected object"
                    val metaStr = if (selected.size == 1) {
                        "Local ID: ${obj.localId} · Single prim"
                    } else {
                        "Local ID: ${obj.localId} · ${selected.size} prims selected"
                    }
                    BuildToolsUiState.Ready(
                        selectionName = nameStr,
                        selectionMeta = metaStr,
                        primaryObjectLocalId = obj.localId,
                        position = obj.position,
                        isPhantom = obj.isPhantom,
                        isPhysical = obj.isPhysical,
                        selectedCount = selected.size
                    )
                } else {
                    BuildToolsUiState.EmptySelection
                }
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = BuildToolsUiState.Loading
        )

    fun updatePosition(localId: Int, newPos: LLVector3) {
        objectManager.updateObjectPosition(localId, newPos)
    }

    fun setFlags(localId: Int, usePhysics: Boolean, isPhantom: Boolean) {
        objectManager.sendObjectFlags(localId, usePhysics, isPhantom)
    }

    fun createPrim(params: PrimCreateParams) {
        objectManager.createPrim(params)
    }

    fun duplicateSelection(offset: LLVector3 = LLVector3(0.5f, 0.5f, 0f)) {
        buildTools.duplicateSelection(offset)
    }

    fun distributeSelection(axis: Axis) {
        buildTools.distributeSelection(axis)
    }
}
