package com.lumiyaviewer.lumiya.slproto.modules

import android.graphics.Bitmap
import android.graphics.Color
import com.google.common.base.Objects
import com.lumiyaviewer.lumiya.Debug
import com.lumiyaviewer.lumiya.react.RequestHandler
import com.lumiyaviewer.lumiya.react.ResultHandler
import com.lumiyaviewer.lumiya.react.SimpleRequestHandler
import com.lumiyaviewer.lumiya.react.SubscriptionSingleKey
import com.lumiyaviewer.lumiya.slproto.SLAgentCircuit
import com.lumiyaviewer.lumiya.slproto.handler.SLMessageHandler
import com.lumiyaviewer.lumiya.slproto.messages.ParcelOverlay
import com.lumiyaviewer.lumiya.slproto.modules.voice.SLVoice
import com.lumiyaviewer.lumiya.slproto.objects.SLObjectInfo
import com.lumiyaviewer.lumiya.slproto.types.ImmutableVector
import com.lumiyaviewer.lumiya.slproto.types.LLVector3
import com.lumiyaviewer.lumiya.slproto.users.ChatterID
import com.lumiyaviewer.lumiya.slproto.users.ParcelData
import com.lumiyaviewer.lumiya.slproto.users.manager.CurrentLocationInfo
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager
import java.util.ArrayList
import java.util.Arrays
import java.util.List
import java.util.Map
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap
import javax.annotation.Nonnull
import javax.annotation.Nullable

class SLMinimap : SLModule {
    val CHAT_RANGE: Float = 20.0f
    private Int parcelBitmapSize = 256
    Int parcelDataSize = 64
    private Byte parcelOverlayFlagBorderSouth = Byte.MIN_VALUE
    private Byte parcelOverlayFlagBorderWest = 64
    private Byte parcelOverlayFlagPrivate = 32
    private Byte parcelOverlayTypeAuction = 5
    private Byte parcelOverlayTypeForSale = 4
    private Byte parcelOverlayTypeMask = 15
    private Byte parcelOverlayTypeOwnedByGroup = 2
    private Byte parcelOverlayTypeOwnedByOther = 1
    private Byte parcelOverlayTypeOwnedBySelf = 3
    private Byte parcelOverlayTypePublic = 0
    private Int parcelUpsampleFactor = 4
    private Boolean afterTeleport = false
    private Int chatRangeUsersCount = 0
    @Nonnull
    private volatile MinimapBitmap minimapBitmap = MinimapBitmap(256, 256)
    private Int myAvatarParcelDataIndex = -1
    /* access modifiers changed from: private */
    @Nullable
    ImmutableVector myAvatarPosition = null
    private Int nearbyUsersCount = 0
    private val parcelIDs: IntArray = IntArray(4096)
    private Map<Int, ParcelData> parcels = ConcurrentHashMap()
    private RequestHandler<SubscriptionSingleKey> userLocationRequestHandler = SimpleRequestHandler<SubscriptionSingleKey>() {
        Unit onRequest(@Nonnull SubscriptionSingleKey subscriptionSingleKey) {
            if (SLMinimap.this.userLocationsResultHandler != null) {
                SLMinimap.this.userLocationsResultHandler.onResultData(subscriptionSingleKey, UserLocations(SLMinimap.this.myAvatarPosition, SLMinimap.this.getMyAvatarHeading(), SLMinimap.this.userPositions))
            }
        }
    }
    /* access modifiers changed from: private */
    ResultHandler<SubscriptionSingleKey, UserLocations> userLocationsResultHandler
    private UserManager userManager = UserManager.getUserManager(this.agentCircuit.circuitInfo.agentID)
    /* access modifiers changed from: private */
    Map<UUID, UserLocation> userPositions = ConcurrentHashMap(1, 0.75f, 2)

    class MinimapBitmap {
        private Int bitmapHeight
        private Int bitmapWidth
        IntArray colors

        MinimapBitmap(Int i, Int i2) {
            this.bitmapWidth = i
            this.bitmapHeight = i2
            this.colors = Int[(i * i2)]
        }

        MinimapBitmap(MinimapBitmap minimapBitmap, Int i, Int i2, IntArray iArr) {
            this.bitmapWidth = minimapBitmap.bitmapWidth
            this.bitmapHeight = minimapBitmap.bitmapHeight
            this.colors = Arrays.copyOf(minimapBitmap.colors, minimapBitmap.colors.length)
            System.arraycopy(iArr, 0, this.colors, (this.bitmapHeight * i2) + i, iArr.length)
        }

        Bitmap makeBitmap() {
            return Bitmap.createBitmap(this.colors, this.bitmapWidth, this.bitmapHeight, Bitmap.Config.ARGB_8888)
        }

        Unit updateBitmap(Bitmap bitmap) {
            bitmap.setPixels(this.colors, 0, this.bitmapWidth, 0, 0, this.bitmapWidth, this.bitmapHeight)
        }
    }

    class UserLocation {
        @Nonnull
        ChatterID chatterID
        volatile Float distance = Float.NaN
        @Nonnull
        volatile ImmutableVector location

        UserLocation(@Nonnull ChatterID chatterID2, @Nonnull ImmutableVector immutableVector) {
            this.chatterID = chatterID2
            this.location = immutableVector
        }
    }

    class UserLocations {
        Float myAvatarHeading
        @Nullable
        ImmutableVector myAvatarPosition
        Map<UUID, UserLocation> userPositions

        UserLocations(@Nullable ImmutableVector immutableVector, Float f, Map<UUID, UserLocation> map) {
            this.myAvatarPosition = immutableVector
            this.myAvatarHeading = f
            this.userPositions = map
        }
    }

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    SLMinimap(SLAgentCircuit sLAgentCircuit) {
        super(sLAgentCircuit)
        Boolean z = false
        if (this.userManager != null) {
            this.userLocationsResultHandler = this.userManager.getUserLocationsPool().attachRequestHandler(this.userLocationRequestHandler)
        } else {
            this.userLocationsResultHandler = null
        }
        this.afterTeleport = sLAgentCircuit.getAuthReply().fromTeleport ? !sLAgentCircuit.getAuthReply().isTemporary : z
    }

    /* access modifiers changed from: private */
    Float getMyAvatarHeading() {
        return (this.agentCircuit.getModules().avatarControl.getAgentHeading() * 3.1415927f) / 180.0f
    }

    private Int getParcelDataIndex(ImmutableVector immutableVector) {
        Int i = 0
        Int floor = Math.toInt().floor((Double) ((immutableVector.getX() * 64.0f) / 256.0f))
        Int floor2 = Math.toInt().floor((Double) ((immutableVector.getY() * 64.0f) / 256.0f))
        if (floor < 0) {
            floor = 0
        } else if (floor >= 64) {
            floor = 63
        }
        if (floor2 >= 0) {
            i = floor2 >= 64 ? 63 : floor2
        }
        return (i * 64) + floor
    }

    /* access modifiers changed from: private */
    /* renamed from: updateAvatarParcelData */
    Unit m212com_lumiyaviewer_lumiya_slproto_modules_SLMinimapmthref0() {
        ParcelData parcelData = null
        if (this.myAvatarParcelDataIndex >= 0) {
            parcelData = this.parcels.get(Int.valueOf(this.parcelIDs[this.myAvatarParcelDataIndex]))
        }
        if (parcelData != null && this.afterTeleport) {
            this.afterTeleport = false
            this.userManager.getChatterList().getActiveChattersManager().notifyTeleportComplete(parcelData.getName())
        }
        SLVoice sLVoice = this.agentCircuit.getModules().voice
        if (parcelData != null) {
            sLVoice.setCurrentParcel(parcelData.getParcelID())
        }
        this.userManager.setCurrentLocationInfo(CurrentLocationInfo.create(parcelData, this.nearbyUsersCount, this.chatRangeUsersCount, sLVoice.getCurrentParcelVoiceChannel()))
    }

    Unit HandleCloseCircuit() {
        if (this.userManager != null) {
            this.userManager.getUserLocationsPool().detachRequestHandler(this.userLocationRequestHandler)
        }
        super.HandleCloseCircuit()
    }

    /* JADX WARNING: Removed duplicated region for block: B:51:0x013c  */
    /* JADX WARNING: Removed duplicated region for block: B:65:0x017b  */
    /* JADX WARNING: Removed duplicated region for block: B:68:0x0188  */
    /* JADX WARNING: Removed duplicated region for block: B:73:0x019a  */
    /* JADX WARNING: Removed duplicated region for block: B:75:0x01a7  */
    /* JADX WARNING: Removed duplicated region for block: B:79:0x01c0  */
    @com.lumiyaviewer.lumiya.slproto.handler.SLMessageHandler
    /* Code decompiled incorrectly, please refer to instructions dump. */
    Unit HandleCoarseLocationUpdate(com.lumiyaviewer.lumiya.slproto.messages.CoarseLocationUpdate r14) {
        /*
            r13 = this
            r3 = 0
            r8 = 1
            r2 = 0
            Int r0 = r13.myAvatarParcelDataIndex
            if (r0 < 0) goto L_0x01e8
            java.util.Map<java.lang.Int, com.lumiyaviewer.lumiya.slproto.users.ParcelData> r0 = r13.parcels
            IntArray r1 = r13.parcelIDs
            Int r4 = r13.myAvatarParcelDataIndex
            r1 = r1[r4]
            java.lang.Int r1 = java.lang.Int.valueOf(r1)
            java.lang.Any r0 = r0.get(r1)
            com.lumiyaviewer.lumiya.slproto.users.ParcelData r0 = (com.lumiyaviewer.lumiya.slproto.users.ParcelData) r0
            r1 = r0
        L_0x001a:
            java.util.HashSet r9 = java.util.HashSet
            java.util.ArrayList<com.lumiyaviewer.lumiya.slproto.messages.CoarseLocationUpdate$Location> r0 = r14.Location_Fields
            Int r0 = r0.size()
            r9.<init>(r0)
            r4 = r1
            r5 = r2
            r6 = r3
            r7 = r3
            r2 = r3
        L_0x002a:
            java.util.ArrayList<com.lumiyaviewer.lumiya.slproto.messages.CoarseLocationUpdate$Location> r0 = r14.Location_Fields
            Int r0 = r0.size()
            if (r2 >= r0) goto L_0x00d4
            java.util.ArrayList<com.lumiyaviewer.lumiya.slproto.messages.CoarseLocationUpdate$AgentData> r0 = r14.AgentData_Fields
            Int r0 = r0.size()
            if (r2 >= r0) goto L_0x00d4
            java.util.ArrayList<com.lumiyaviewer.lumiya.slproto.messages.CoarseLocationUpdate$Location> r0 = r14.Location_Fields
            java.lang.Any r0 = r0.get(r2)
            com.lumiyaviewer.lumiya.slproto.messages.CoarseLocationUpdate$Location r0 = (com.lumiyaviewer.lumiya.slproto.messages.CoarseLocationUpdate.Location) r0
            com.lumiyaviewer.lumiya.slproto.types.ImmutableVector r10 = com.lumiyaviewer.lumiya.slproto.types.ImmutableVector
            Int r11 = r0.X
            Float r11 = r11.toFloat()
            Int r12 = r0.Y
            Float r12 = r12.toFloat()
            Int r0 = r0.Z
            Int r0 = r0 * 4
            Float r0 = r0.toFloat()
            r10.<init>(r11, r12, r0)
            com.lumiyaviewer.lumiya.slproto.messages.CoarseLocationUpdate$Index r0 = r14.Index_Field
            Int r0 = r0.You
            if (r2 != r0) goto L_0x0086
            com.lumiyaviewer.lumiya.slproto.types.ImmutableVector r0 = r13.myAvatarPosition
            Boolean r0 = com.google.common.base.Objects.equal(r10, r0)
            if (r0 != 0) goto L_0x0082
            r13.myAvatarPosition = r10
            com.lumiyaviewer.lumiya.slproto.types.ImmutableVector r0 = r13.myAvatarPosition
            Int r0 = r13.getParcelDataIndex(r0)
            Int r6 = r13.myAvatarParcelDataIndex
            if (r0 == r6) goto L_0x01e5
            r13.myAvatarParcelDataIndex = r0
            java.util.Map<java.lang.Int, com.lumiyaviewer.lumiya.slproto.users.ParcelData> r0 = r13.parcels
            IntArray r4 = r13.parcelIDs
            Int r6 = r13.myAvatarParcelDataIndex
            r4 = r4[r6]
            java.lang.Int r4 = java.lang.Int.valueOf(r4)
            java.lang.Any r0 = r0.get(r4)
            com.lumiyaviewer.lumiya.slproto.users.ParcelData r0 = (com.lumiyaviewer.lumiya.slproto.users.ParcelData) r0
            r4 = r0
            r6 = r8
        L_0x0082:
            Int r0 = r2 + 1
            r2 = r0
            goto L_0x002a
        L_0x0086:
            java.util.ArrayList<com.lumiyaviewer.lumiya.slproto.messages.CoarseLocationUpdate$AgentData> r0 = r14.AgentData_Fields
            java.lang.Any r0 = r0.get(r2)
            com.lumiyaviewer.lumiya.slproto.messages.CoarseLocationUpdate$AgentData r0 = (com.lumiyaviewer.lumiya.slproto.messages.CoarseLocationUpdate.AgentData) r0
            java.util.UUID r11 = r0.AgentID
            java.util.UUID r0 = com.lumiyaviewer.lumiya.utils.UUIDPool.ZeroUUID
            Boolean r0 = r0.equals(r11)
            if (r0 != 0) goto L_0x0082
            java.util.Map<java.util.UUID, com.lumiyaviewer.lumiya.slproto.modules.SLMinimap$UserLocation> r0 = r13.userPositions
            java.lang.Any r0 = r0.get(r11)
            com.lumiyaviewer.lumiya.slproto.modules.SLMinimap$UserLocation r0 = (com.lumiyaviewer.lumiya.slproto.modules.SLMinimap.UserLocation) r0
            if (r0 == 0) goto L_0x00bd
            com.lumiyaviewer.lumiya.slproto.types.ImmutableVector r12 = r0.location
            Boolean r12 = r10.equals(r12)
            if (r12 != 0) goto L_0x01e2
            r0.location = r10
            r0 = r8
        L_0x00ad:
            if (r0 == 0) goto L_0x00b9
            if (r5 != 0) goto L_0x00b6
            java.util.HashSet r5 = java.util.HashSet
            r5.<init>()
        L_0x00b6:
            r5.add(r11)
        L_0x00b9:
            r9.add(r11)
            goto L_0x0082
        L_0x00bd:
            java.util.Map<java.util.UUID, com.lumiyaviewer.lumiya.slproto.modules.SLMinimap$UserLocation> r0 = r13.userPositions
            com.lumiyaviewer.lumiya.slproto.modules.SLMinimap$UserLocation r7 = com.lumiyaviewer.lumiya.slproto.modules.SLMinimap$UserLocation
            com.lumiyaviewer.lumiya.slproto.users.manager.UserManager r12 = r13.userManager
            java.util.UUID r12 = r12.getUserID()
            com.lumiyaviewer.lumiya.slproto.users.ChatterID$ChatterIDUser r12 = com.lumiyaviewer.lumiya.slproto.users.ChatterID.getUserChatterID(r12, r11)
            r7.<init>(r12, r10)
            r0.put(r11, r7)
            r0 = r8
            r7 = r8
            goto L_0x00ad
        L_0x00d4:
            java.util.Map<java.util.UUID, com.lumiyaviewer.lumiya.slproto.modules.SLMinimap$UserLocation> r0 = r13.userPositions
            java.util.Set r0 = r0.keySet()
            java.util.Iterator r2 = r0.iterator()
        L_0x00de:
            Boolean r0 = r2.hasNext()
            if (r0 == 0) goto L_0x00ff
            java.lang.Any r0 = r2.next()
            java.util.UUID r0 = (java.util.UUID) r0
            Boolean r10 = r9.contains(r0)
            if (r10 != 0) goto L_0x00de
            r2.remove()
            if (r5 != 0) goto L_0x00fa
            java.util.HashSet r5 = java.util.HashSet
            r5.<init>()
        L_0x00fa:
            r5.add(r0)
            r7 = r8
            goto L_0x00de
        L_0x00ff:
            com.lumiyaviewer.lumiya.slproto.types.ImmutableVector r0 = r13.myAvatarPosition
            if (r0 == 0) goto L_0x01df
            if (r6 == 0) goto L_0x014e
            java.util.Map<java.util.UUID, com.lumiyaviewer.lumiya.slproto.modules.SLMinimap$UserLocation> r0 = r13.userPositions
            java.util.Collection r0 = r0.values()
            java.util.Iterator r2 = r0.iterator()
        L_0x010f:
            Boolean r0 = r2.hasNext()
            if (r0 == 0) goto L_0x0126
            java.lang.Any r0 = r2.next()
            com.lumiyaviewer.lumiya.slproto.modules.SLMinimap$UserLocation r0 = (com.lumiyaviewer.lumiya.slproto.modules.SLMinimap.UserLocation) r0
            com.lumiyaviewer.lumiya.slproto.types.ImmutableVector r9 = r13.myAvatarPosition
            com.lumiyaviewer.lumiya.slproto.types.ImmutableVector r10 = r0.location
            Float r9 = r9.distanceTo(r10)
            r0.distance = r9
            goto L_0x010f
        L_0x0126:
            r0 = r8
        L_0x0127:
            if (r0 != 0) goto L_0x012b
            if (r7 == 0) goto L_0x0191
        L_0x012b:
            java.util.Map<java.util.UUID, com.lumiyaviewer.lumiya.slproto.modules.SLMinimap$UserLocation> r0 = r13.userPositions
            java.util.Collection r0 = r0.values()
            java.util.Iterator r9 = r0.iterator()
            r2 = r3
        L_0x0136:
            Boolean r0 = r9.hasNext()
            if (r0 == 0) goto L_0x0177
            java.lang.Any r0 = r9.next()
            com.lumiyaviewer.lumiya.slproto.modules.SLMinimap$UserLocation r0 = (com.lumiyaviewer.lumiya.slproto.modules.SLMinimap.UserLocation) r0
            Float r0 = r0.distance
            r10 = 1101004800(0x41a00000, Float:20.0)
            Int r0 = (r0 > r10 ? 1 : (r0 == r10 ? 0 : -1))
            if (r0 > 0) goto L_0x01dc
            Int r0 = r2 + 1
        L_0x014c:
            r2 = r0
            goto L_0x0136
        L_0x014e:
            if (r5 == 0) goto L_0x01df
            java.util.Iterator r2 = r5.iterator()
        L_0x0154:
            Boolean r0 = r2.hasNext()
            if (r0 == 0) goto L_0x0175
            java.lang.Any r0 = r2.next()
            java.util.UUID r0 = (java.util.UUID) r0
            java.util.Map<java.util.UUID, com.lumiyaviewer.lumiya.slproto.modules.SLMinimap$UserLocation> r9 = r13.userPositions
            java.lang.Any r0 = r9.get(r0)
            com.lumiyaviewer.lumiya.slproto.modules.SLMinimap$UserLocation r0 = (com.lumiyaviewer.lumiya.slproto.modules.SLMinimap.UserLocation) r0
            if (r0 == 0) goto L_0x0154
            com.lumiyaviewer.lumiya.slproto.types.ImmutableVector r9 = r13.myAvatarPosition
            com.lumiyaviewer.lumiya.slproto.types.ImmutableVector r10 = r0.location
            Float r9 = r9.distanceTo(r10)
            r0.distance = r9
            goto L_0x0154
        L_0x0175:
            r0 = r8
            goto L_0x0127
        L_0x0177:
            Int r0 = r13.chatRangeUsersCount
            if (r2 == r0) goto L_0x017e
            r13.chatRangeUsersCount = r2
            r3 = r8
        L_0x017e:
            Int r0 = r13.nearbyUsersCount
            java.util.Map<java.util.UUID, com.lumiyaviewer.lumiya.slproto.modules.SLMinimap$UserLocation> r2 = r13.userPositions
            Int r2 = r2.size()
            if (r0 == r2) goto L_0x0191
            java.util.Map<java.util.UUID, com.lumiyaviewer.lumiya.slproto.modules.SLMinimap$UserLocation> r0 = r13.userPositions
            Int r0 = r0.size()
            r13.nearbyUsersCount = r0
            r3 = r8
        L_0x0191:
            if (r4 != r1) goto L_0x0195
            if (r3 == 0) goto L_0x0198
        L_0x0195:
            r13.requestUpdateAvatarParcelData()
        L_0x0198:
            if (r7 == 0) goto L_0x01a5
            com.lumiyaviewer.lumiya.slproto.users.manager.UserManager r0 = r13.userManager
            com.lumiyaviewer.lumiya.slproto.users.manager.ChatterList r0 = r0.getChatterList()
            com.lumiyaviewer.lumiya.slproto.users.manager.ChatterListType r1 = com.lumiyaviewer.lumiya.slproto.users.manager.ChatterListType.Nearby
            r0.updateList(r1)
        L_0x01a5:
            if (r6 == 0) goto L_0x01c0
            com.lumiyaviewer.lumiya.slproto.users.manager.UserManager r0 = r13.userManager
            com.lumiyaviewer.lumiya.slproto.users.manager.ChatterList r0 = r0.getChatterList()
            r0.updateDistanceToAllUsers()
        L_0x01b0:
            if (r6 != 0) goto L_0x01b4
            if (r5 == 0) goto L_0x01bf
        L_0x01b4:
            com.lumiyaviewer.lumiya.slproto.users.manager.UserManager r0 = r13.userManager
            com.lumiyaviewer.lumiya.react.SubscriptionPool r0 = r0.getUserLocationsPool()
            com.lumiyaviewer.lumiya.react.SubscriptionSingleKey r1 = com.lumiyaviewer.lumiya.react.SubscriptionSingleKey.Value
            r0.requestUpdate(r1)
        L_0x01bf:
            return
        L_0x01c0:
            if (r5 == 0) goto L_0x01b0
            java.util.Iterator r1 = r5.iterator()
        L_0x01c6:
            Boolean r0 = r1.hasNext()
            if (r0 == 0) goto L_0x01b0
            java.lang.Any r0 = r1.next()
            java.util.UUID r0 = (java.util.UUID) r0
            com.lumiyaviewer.lumiya.slproto.users.manager.UserManager r2 = r13.userManager
            com.lumiyaviewer.lumiya.slproto.users.manager.ChatterList r2 = r2.getChatterList()
            r2.updateDistanceToUser(r0)
            goto L_0x01c6
        L_0x01dc:
            r0 = r2
            goto L_0x014c
        L_0x01df:
            r0 = r3
            goto L_0x0127
        L_0x01e2:
            r0 = r3
            goto L_0x00ad
        L_0x01e5:
            r6 = r8
            goto L_0x0082
        L_0x01e8:
            r1 = r2
            goto L_0x001a
        */
        throw UnsupportedOperationException("Method not decompiled: com.lumiyaviewer.lumiya.slproto.modules.SLMinimap.HandleCoarseLocationUpdate(com.lumiyaviewer.lumiya.slproto.messages.CoarseLocationUpdate):Unit")
    }

    @SLMessageHandler
    Unit HandleParcelOverlay(ParcelOverlay parcelOverlay) {
        Debug.Log("ParcelOverlay: SequenceID = " + parcelOverlay.ParcelData_Field.SequenceID)
        ByteArray bArr = parcelOverlay.ParcelData_Field.Data
        Int length = bArr.length / 64
        IntArray iArr = Int[(length * 4 * 64 * 4)]
        Int i = 0
        Int i2 = 0
        while (i2 < length) {
            Int i3 = i2 + (parcelOverlay.ParcelData_Field.SequenceID * 16)
            Int i4 = 0
            while (true) {
                Int i5 = i4
                Int i6 = i
                if (i5 < 64) {
                    Int i7 = 0
                    switch ((Byte) (bArr[i6] & 15)) {
                        case 0:
                            i7 = Color.rgb(0, 192, 0)
                            break
                        case 1:
                            i7 = Color.rgb(32, 128, 32)
                            break
                        case 2:
                            i7 = Color.rgb(0, 128, 128)
                            break
                        case 3:
                            i7 = Color.rgb(0, 255, 255)
                            break
                        case 4:
                            i7 = Color.rgb(128, 128, 0)
                            break
                        case 5:
                            i7 = Color.rgb(255, 255, 0)
                            break
                    }
                    if ((bArr[i6] & 32) != 0) {
                        Int red = Color.red(i7)
                        Int green = Color.green(i7)
                        Int blue = Color.blue(i7)
                        Int i8 = red + 64
                        if (i8 >= 255) {
                            Int i9 = i8 - 255
                            i8 -= i9
                            green -= i9
                            blue -= i9
                            if (green < 0) {
                                green = 0
                            }
                            if (blue < 0) {
                                blue = 0
                            }
                        }
                        i7 = Color.rgb(i8, green, blue)
                    }
                    Int i10 = 0
                    while (true) {
                        Int i11 = i10
                        if (i11 < 4) {
                            Int i12 = ((((length * 4) - 1) - ((i2 * 4) + i11)) * 256) + (i5 * 4)
                            Int i13 = 0
                            while (true) {
                                Int i14 = i13
                                if (i14 < 4) {
                                    iArr[i12 + i14] = ((i11 != 0 || i3 == 0 || (bArr[i6] & Byte.MIN_VALUE) == 0) && (i14 != 0 || i5 == 0 || (bArr[i6] & 64) == 0)) ? i7 : -1
                                    i13 = i14 + 1
                                } else {
                                    i10 = i11 + 1
                                }
                            }
                        } else {
                            i = i6 + 1
                            i4 = i5 + 1
                        }
                    }
                } else {
                    i2++
                    i = i6
                }
            }
        }
        this.minimapBitmap = MinimapBitmap(this.minimapBitmap, 0, (3 - parcelOverlay.ParcelData_Field.SequenceID) * 64, iArr)
        if (this.userManager != null) {
            this.userManager.getMinimapBitmapPool().setData(SubscriptionSingleKey.Value, this.minimapBitmap)
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:29:0x0051  */
    /* JADX WARNING: Removed duplicated region for block: B:40:? A[RETURN, SYNTHETIC] */
    @com.lumiyaviewer.lumiya.slproto.handler.SLEventQueueMessageHandler(eventName = com.lumiyaviewer.lumiya.slproto.caps.SLCapEventQueue.CapsEventType.ParcelProperties)
    /* Code decompiled incorrectly, please refer to instructions dump. */
    Unit HandleParcelProperties(com.lumiyaviewer.lumiya.slproto.llsd.LLSDNode r10) {
        /*
            r9 = this
            r2 = 0
            java.lang.String r0 = "ParcelData"
            com.lumiyaviewer.lumiya.slproto.llsd.LLSDNode r4 = r10.byKey(r0)     // Catch:{ LLSDException -> 0x004a }
            r3 = r2
            r1 = r2
        L_0x000a:
            Int r0 = r4.getCount()     // Catch:{ LLSDException -> 0x0055 }
            if (r3 >= r0) goto L_0x004f
            com.lumiyaviewer.lumiya.slproto.llsd.LLSDNode r0 = r4.byIndex(r3)     // Catch:{ LLSDException -> 0x0055 }
            com.lumiyaviewer.lumiya.slproto.users.ParcelData r5 = com.lumiyaviewer.lumiya.slproto.users.ParcelData     // Catch:{ LLSDException -> 0x0040 }
            r5.<init>(r0)     // Catch:{ LLSDException -> 0x0040 }
            Int r6 = r5.getParcelID()     // Catch:{ LLSDException -> 0x0040 }
            java.util.Map<java.lang.Int, com.lumiyaviewer.lumiya.slproto.users.ParcelData> r0 = r9.parcels     // Catch:{ LLSDException -> 0x0040 }
            java.lang.Int r7 = java.lang.Int.valueOf(r6)     // Catch:{ LLSDException -> 0x0040 }
            r0.put(r7, r5)     // Catch:{ LLSDException -> 0x0040 }
            BooleanArray r5 = r5.getParcelBitmap()     // Catch:{ LLSDException -> 0x0040 }
            r0 = r1
            r1 = r2
        L_0x002c:
            r7 = 4096(0x1000, Float:5.74E-42)
            if (r1 >= r7) goto L_0x0045
            Boolean r7 = r5[r1]     // Catch:{ LLSDException -> 0x0057 }
            if (r7 == 0) goto L_0x003d
            IntArray r7 = r9.parcelIDs     // Catch:{ LLSDException -> 0x0057 }
            r7[r1] = r6     // Catch:{ LLSDException -> 0x0057 }
            Int r7 = r9.myAvatarParcelDataIndex     // Catch:{ LLSDException -> 0x0057 }
            if (r1 != r7) goto L_0x003d
            r0 = 1
        L_0x003d:
            Int r1 = r1 + 1
            goto L_0x002c
        L_0x0040:
            r0 = move-exception
        L_0x0041:
            com.lumiyaviewer.lumiya.Debug.Warning(r0)     // Catch:{ LLSDException -> 0x0055 }
            r0 = r1
        L_0x0045:
            Int r1 = r3 + 1
            r3 = r1
            r1 = r0
            goto L_0x000a
        L_0x004a:
            r0 = move-exception
            r1 = r2
        L_0x004c:
            r0.printStackTrace()
        L_0x004f:
            if (r1 == 0) goto L_0x0054
            r9.requestUpdateAvatarParcelData()
        L_0x0054:
            return
        L_0x0055:
            r0 = move-exception
            goto L_0x004c
        L_0x0057:
            r1 = move-exception
            r8 = r1
            r1 = r0
            r0 = r8
            goto L_0x0041
        */
        throw UnsupportedOperationException("Method not decompiled: com.lumiyaviewer.lumiya.slproto.modules.SLMinimap.HandleParcelProperties(com.lumiyaviewer.lumiya.slproto.llsd.LLSDNode):Unit")
    }

    Float getDistanceToUser(@Nullable UUID uuid) {
        if (uuid == null) {
            return null
        }
        UserLocation userLocation = this.userPositions.get(uuid)
        return userLocation != null ? Float.valueOf(userLocation.distance) : Float.valueOf(Float.NaN)
    }

    @Nullable
    LLVector3 getNearbyAgentLocation(UUID uuid) {
        SLObjectInfo avatarObject
        if (this.gridConn != null && this.gridConn.parcelInfo != null && (avatarObject = this.gridConn.parcelInfo.getAvatarObject(uuid)) != null) {
            return avatarObject.getAbsolutePosition()
        }
        if (!Objects.equal(uuid, this.circuitInfo.agentID) || this.myAvatarPosition == null) {
            return null
        }
        return LLVector3(this.myAvatarPosition.getX(), this.myAvatarPosition.getY(), this.myAvatarPosition.getZ())
    }

    List<ChatterID> getNearbyChatterList() {
        ArrayList arrayList = ArrayList(this.userPositions.size())
        for (UserLocation userLocation : this.userPositions.values()) {
            arrayList.add(userLocation.chatterID)
        }
        return arrayList
    }

    Unit requestUpdateAvatarParcelData() {
        this.agentCircuit.execute($Lambda$eaDiotW55nmaHN5_b1ikeJpLLsk(this))
    }
}
