package com.linkpoint.render.avatar

import android.opengl.GLES11
import android.opengl.GLES20
import android.opengl.Matrix
import com.google.common.collect.ArrayListMultimap
import com.google.common.collect.Multimap
import com.linkpoint.Debug
import com.linkpoint.render.DrawableObject
import com.linkpoint.render.DrawableStore
import com.linkpoint.render.RenderContext
import com.linkpoint.render.avatar.-$Lambda$0R0mXpfMxrM5lCygN3JijOMDexU.AnonymousClass1
import com.linkpoint.render.picking.CollisionBox
import com.linkpoint.render.picking.GLRayTrace
import com.linkpoint.render.picking.GLRayTrace.RayIntersectInfo
import com.linkpoint.render.picking.IntersectInfo
import com.linkpoint.render.picking.IntersectPickable
import com.linkpoint.render.picking.ObjectIntersectInfo
import com.linkpoint.render.spatial.DrawEntryList
import com.linkpoint.render.spatial.DrawEntryList.EntryRemovalListener
import com.linkpoint.render.spatial.DrawListEntry
import com.linkpoint.render.spatial.DrawListPrimEntry
import com.linkpoint.res.executors.PrimComputeExecutor
import com.linkpoint.slproto.avatar.MeshIndex
import com.linkpoint.slproto.avatar.SLAttachmentPoint
import com.linkpoint.slproto.avatar.SLBaseAvatar
import com.linkpoint.slproto.avatar.SLSkeletonBone
import com.linkpoint.slproto.avatar.SLSkeletonBoneID
import com.linkpoint.slproto.mesh.MeshJointTranslations
import com.linkpoint.slproto.objects.SLObjectAvatarInfo
import com.linkpoint.slproto.objects.SLObjectInfo
import com.linkpoint.slproto.types.LLVector3
import com.linkpoint.utils.IdentityMatrix
import com.linkpoint.utils.InlineListEntry
import com.linkpoint.utils.LinkedTreeNode
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
import androidx.annotation.NonNull

data class DrawableAvatar(
    var headPosition: LLVector3,
    var jointMatrixUpdated: Boolean = false,
    var pelvisTranslateX: Float = 0.0f,
    var pelvisTranslateY: Float = 0.0f,
    var pelvisTranslateZ: Float = 0.0f
)
