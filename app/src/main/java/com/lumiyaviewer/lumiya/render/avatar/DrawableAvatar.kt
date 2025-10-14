package com.lumiyaviewer.lumiya.render.avatar

import android.opengl.GLES11
import android.opengl.GLES20
import android.opengl.Matrix
import com.google.common.collect.ArrayListMultimap
import com.google.common.collect.Multimap
import com.lumiyaviewer.lumiya.Debug
import com.lumiyaviewer.lumiya.render.DrawableObject
import com.lumiyaviewer.lumiya.render.DrawableStore
import com.lumiyaviewer.lumiya.render.RenderContext
import com.lumiyaviewer.lumiya.render.avatar.-$Lambda$0R0mXpfMxrM5lCygN3JijOMDexU.AnonymousClass1
import com.lumiyaviewer.lumiya.render.picking.CollisionBox
import com.lumiyaviewer.lumiya.render.picking.GLRayTrace
import com.lumiyaviewer.lumiya.render.picking.GLRayTrace.RayIntersectInfo
import com.lumiyaviewer.lumiya.render.picking.IntersectInfo
import com.lumiyaviewer.lumiya.render.picking.IntersectPickable
import com.lumiyaviewer.lumiya.render.picking.ObjectIntersectInfo
import com.lumiyaviewer.lumiya.render.spatial.DrawEntryList
import com.lumiyaviewer.lumiya.render.spatial.DrawEntryList.EntryRemovalListener
import com.lumiyaviewer.lumiya.render.spatial.DrawListEntry
import com.lumiyaviewer.lumiya.render.spatial.DrawListPrimEntry
import com.lumiyaviewer.lumiya.res.executors.PrimComputeExecutor
import com.lumiyaviewer.lumiya.slproto.avatar.MeshIndex
import com.lumiyaviewer.lumiya.slproto.avatar.SLAttachmentPoint
import com.lumiyaviewer.lumiya.slproto.avatar.SLBaseAvatar
import com.lumiyaviewer.lumiya.slproto.avatar.SLSkeletonBone
import com.lumiyaviewer.lumiya.slproto.avatar.SLSkeletonBoneID
import com.lumiyaviewer.lumiya.slproto.mesh.MeshJointTranslations
import com.lumiyaviewer.lumiya.slproto.objects.SLObjectAvatarInfo
import com.lumiyaviewer.lumiya.slproto.objects.SLObjectInfo
import com.lumiyaviewer.lumiya.slproto.types.LLVector3
import com.lumiyaviewer.lumiya.utils.IdentityMatrix
import com.lumiyaviewer.lumiya.utils.InlineListEntry
import com.lumiyaviewer.lumiya.utils.LinkedTreeNode
import java.util.Collections
import java.util.EnumMap
import java.util.HashMap
import java.util.IdentityHashMap
import java.util.Iterator
import java.util.Map
import java.util.Map.Entry
import java.util.Set
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicReference
import javax.annotation.Nonnull

data class DrawableAvatar(
    var headPosition: LLVector3,
    var jointMatrixUpdated: Boolean = false,
    var pelvisTranslateX: Float = 0.0f,
    var pelvisTranslateY: Float = 0.0f,
    var pelvisTranslateZ: Float = 0.0f
)
