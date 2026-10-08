package com.lumiyaviewer.lumiya.slproto.modules;

import android.graphics.Bitmap;
import android.graphics.Color;
import com.google.common.base.Objects;
import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.react.RequestHandler;
import com.lumiyaviewer.lumiya.react.ResultHandler;
import com.lumiyaviewer.lumiya.react.SimpleRequestHandler;
import com.lumiyaviewer.lumiya.react.SubscriptionSingleKey;
import com.lumiyaviewer.lumiya.slproto.SLAgentCircuit;
import com.lumiyaviewer.lumiya.slproto.handler.SLMessageHandler;
import com.lumiyaviewer.lumiya.slproto.messages.ParcelOverlay;
import com.lumiyaviewer.lumiya.slproto.modules.voice.SLVoice;
import com.lumiyaviewer.lumiya.slproto.objects.SLObjectInfo;
import com.lumiyaviewer.lumiya.slproto.types.ImmutableVector;
import com.lumiyaviewer.lumiya.slproto.types.LLVector3;
import com.lumiyaviewer.lumiya.slproto.users.ChatterID;
import com.lumiyaviewer.lumiya.slproto.users.ParcelData;
import com.lumiyaviewer.lumiya.slproto.users.manager.CurrentLocationInfo;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
/* loaded from: classes.dex */
public class SLMinimap extends SLModule {
    public static final float CHAT_RANGE = 20.0f;
    private static final int parcelBitmapSize = 256;
    public static final int parcelDataSize = 64;
    private static final byte parcelOverlayFlagBorderSouth = Byte.MIN_VALUE;
    private static final byte parcelOverlayFlagBorderWest = 64;
    private static final byte parcelOverlayFlagPrivate = 32;
    private static final byte parcelOverlayTypeAuction = 5;
    private static final byte parcelOverlayTypeForSale = 4;
    private static final byte parcelOverlayTypeMask = 15;
    private static final byte parcelOverlayTypeOwnedByGroup = 2;
    private static final byte parcelOverlayTypeOwnedByOther = 1;
    private static final byte parcelOverlayTypeOwnedBySelf = 3;
    private static final byte parcelOverlayTypePublic = 0;
    private static final int parcelUpsampleFactor = 4;
    private boolean afterTeleport;
    private int chatRangeUsersCount;
    @Nonnull
    private volatile MinimapBitmap minimapBitmap;
    private int myAvatarParcelDataIndex;
    @Nullable
    private ImmutableVector myAvatarPosition;
    private int nearbyUsersCount;
    private final int[] parcelIDs;
    private final Map<Integer, ParcelData> parcels;
    private final RequestHandler<SubscriptionSingleKey> userLocationRequestHandler;
    private final ResultHandler<SubscriptionSingleKey, UserLocations> userLocationsResultHandler;
    private final UserManager userManager;
    private final Map<UUID, UserLocation> userPositions;

    /* loaded from: classes.dex */
    public static class MinimapBitmap {
        private final int bitmapHeight;
        private final int bitmapWidth;
        final int[] colors;

        MinimapBitmap(int i, int i2) {
            this.bitmapWidth = i;
            this.bitmapHeight = i2;
            this.colors = new int[i * i2];
        }

        MinimapBitmap(MinimapBitmap minimapBitmap, int i, int i2, int[] iArr) {
            this.bitmapWidth = minimapBitmap.bitmapWidth;
            this.bitmapHeight = minimapBitmap.bitmapHeight;
            this.colors = Arrays.copyOf(minimapBitmap.colors, minimapBitmap.colors.length);
            System.arraycopy(iArr, 0, this.colors, (this.bitmapHeight * i2) + i, iArr.length);
        }

        public Bitmap makeBitmap() {
            return Bitmap.createBitmap(this.colors, this.bitmapWidth, this.bitmapHeight, Bitmap.Config.ARGB_8888);
        }

        public void updateBitmap(Bitmap bitmap) {
            bitmap.setPixels(this.colors, 0, this.bitmapWidth, 0, 0, this.bitmapWidth, this.bitmapHeight);
        }
    }

    /* loaded from: classes.dex */
    public static class UserLocation {
        @Nonnull
        public final ChatterID chatterID;
        public volatile float distance = Float.NaN;
        @Nonnull
        public volatile ImmutableVector location;

        UserLocation(@Nonnull ChatterID chatterID, @Nonnull ImmutableVector immutableVector) {
            this.chatterID = chatterID;
            this.location = immutableVector;
        }
    }

    /* loaded from: classes.dex */
    public static class UserLocations {
        public final float myAvatarHeading;
        @Nullable
        public final ImmutableVector myAvatarPosition;
        public final Map<UUID, UserLocation> userPositions;

        UserLocations(@Nullable ImmutableVector immutableVector, float f, Map<UUID, UserLocation> map) {
            this.myAvatarPosition = immutableVector;
            this.myAvatarHeading = f;
            this.userPositions = map;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public SLMinimap(SLAgentCircuit sLAgentCircuit) {
        super(sLAgentCircuit);
        this.minimapBitmap = new MinimapBitmap(256, 256);
        this.parcelIDs = new int[4096];
        this.parcels = new ConcurrentHashMap();
        this.nearbyUsersCount = 0;
        this.chatRangeUsersCount = 0;
        this.userPositions = new ConcurrentHashMap(1, 0.75f, 2);
        this.myAvatarPosition = null;
        this.afterTeleport = false;
        this.myAvatarParcelDataIndex = -1;
        this.userLocationRequestHandler = new SimpleRequestHandler<SubscriptionSingleKey>() { // from class: com.lumiyaviewer.lumiya.slproto.modules.SLMinimap.1
            @Override // com.lumiyaviewer.lumiya.react.RequestHandler
            public void onRequest(@Nonnull SubscriptionSingleKey subscriptionSingleKey) {
                if (SLMinimap.this.userLocationsResultHandler != null) {
                    SLMinimap.this.userLocationsResultHandler.onResultData(subscriptionSingleKey, new UserLocations(SLMinimap.this.myAvatarPosition, SLMinimap.this.getMyAvatarHeading(), SLMinimap.this.userPositions));
                }
            }
        };
        this.userManager = UserManager.getUserManager(this.agentCircuit.circuitInfo.agentID);
        if (this.userManager != null) {
            this.userLocationsResultHandler = this.userManager.getUserLocationsPool().attachRequestHandler(this.userLocationRequestHandler);
        } else {
            this.userLocationsResultHandler = null;
        }
        this.afterTeleport = sLAgentCircuit.getAuthReply().fromTeleport ? !sLAgentCircuit.getAuthReply().isTemporary : false;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public float getMyAvatarHeading() {
        return (this.agentCircuit.getModules().avatarControl.getAgentHeading() * 3.1415927f) / 180.0f;
    }

    private int getParcelDataIndex(ImmutableVector immutableVector) {
        int floor = (int) Math.floor((immutableVector.getX() * 64.0f) / 256.0f);
        int floor2 = (int) Math.floor((immutableVector.getY() * 64.0f) / 256.0f);
        if (floor < 0) {
            floor = 0;
        } else if (floor >= 64) {
            floor = 63;
        }
        return ((floor2 >= 0 ? floor2 >= 64 ? 63 : floor2 : 0) * 64) + floor;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: updateAvatarParcelData */
    public void m223com_lumiyaviewer_lumiya_slproto_modules_SLMinimapmthref0() {
        ParcelData parcelData = this.myAvatarParcelDataIndex >= 0 ? this.parcels.get(Integer.valueOf(this.parcelIDs[this.myAvatarParcelDataIndex])) : null;
        if (parcelData != null && this.afterTeleport) {
            this.afterTeleport = false;
            this.userManager.getChatterList().getActiveChattersManager().notifyTeleportComplete(parcelData.getName());
        }
        SLVoice sLVoice = this.agentCircuit.getModules().voice;
        if (parcelData != null) {
            sLVoice.setCurrentParcel(parcelData.getParcelID());
        }
        this.userManager.setCurrentLocationInfo(CurrentLocationInfo.create(parcelData, this.nearbyUsersCount, this.chatRangeUsersCount, sLVoice.getCurrentParcelVoiceChannel()));
    }

    @Override // com.lumiyaviewer.lumiya.slproto.modules.SLModule
    public void HandleCloseCircuit() {
        if (this.userManager != null) {
            this.userManager.getUserLocationsPool().detachRequestHandler(this.userLocationRequestHandler);
        }
        super.HandleCloseCircuit();
    }

    /* JADX WARN: Removed duplicated region for block: B:53:0x013c  */
    /* JADX WARN: Removed duplicated region for block: B:67:0x017b  */
    /* JADX WARN: Removed duplicated region for block: B:70:0x0188  */
    /* JADX WARN: Removed duplicated region for block: B:75:0x019a  */
    /* JADX WARN: Removed duplicated region for block: B:77:0x01a7  */
    /* JADX WARN: Removed duplicated region for block: B:82:0x01c0  */
    @com.lumiyaviewer.lumiya.slproto.handler.SLMessageHandler
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public void HandleCoarseLocationUpdate(com.lumiyaviewer.lumiya.slproto.messages.CoarseLocationUpdate r14) {
        /*
            Method dump skipped, instructions count: 491
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: com.lumiyaviewer.lumiya.slproto.modules.SLMinimap.HandleCoarseLocationUpdate(com.lumiyaviewer.lumiya.slproto.messages.CoarseLocationUpdate):void");
    }

    @SLMessageHandler
    public void HandleParcelOverlay(ParcelOverlay parcelOverlay) {
        int i;
        Debug.Log("ParcelOverlay: SequenceID = " + parcelOverlay.ParcelData_Field.SequenceID);
        byte[] bArr = parcelOverlay.ParcelData_Field.Data;
        int length = bArr.length / 64;
        int[] iArr = new int[length * 4 * 64 * 4];
        int i2 = 0;
        int i3 = 0;
        while (i3 < length) {
            int i4 = i3 + (parcelOverlay.ParcelData_Field.SequenceID * 16);
            int i5 = 0;
            while (true) {
                int i6 = i5;
                i = i2;
                if (i6 < 64) {
                    int i7 = 0;
                    switch ((byte) (bArr[i] & 15)) {
                        case 0:
                            i7 = Color.rgb(0, 192, 0);
                            break;
                        case 1:
                            i7 = Color.rgb(32, 128, 32);
                            break;
                        case 2:
                            i7 = Color.rgb(0, 128, 128);
                            break;
                        case 3:
                            i7 = Color.rgb(0, 255, 255);
                            break;
                        case 4:
                            i7 = Color.rgb(128, 128, 0);
                            break;
                        case 5:
                            i7 = Color.rgb(255, 255, 0);
                            break;
                    }
                    if ((bArr[i] & 32) != 0) {
                        int red = Color.red(i7);
                        int green = Color.green(i7);
                        int blue = Color.blue(i7);
                        int i8 = red + 64;
                        if (i8 >= 255) {
                            int i9 = i8 - 255;
                            i8 -= i9;
                            green -= i9;
                            blue -= i9;
                            if (green < 0) {
                                green = 0;
                            }
                            if (blue < 0) {
                                blue = 0;
                            }
                        }
                        i7 = Color.rgb(i8, green, blue);
                    }
                    int i10 = 0;
                    while (true) {
                        int i11 = i10;
                        if (i11 < 4) {
                            int i12 = ((((length * 4) - 1) - ((i3 * 4) + i11)) * 256) + (i6 * 4);
                            int i13 = 0;
                            while (true) {
                                int i14 = i13;
                                if (i14 < 4) {
                                    iArr[i12 + i14] = ((i11 != 0 || i4 == 0 || (bArr[i] & Byte.MIN_VALUE) == 0) && (i14 != 0 || i6 == 0 || (bArr[i] & 64) == 0)) ? i7 : -1;
                                    i13 = i14 + 1;
                                }
                            }
                            i10 = i11 + 1;
                        }
                    }
                    i2 = i + 1;
                    i5 = i6 + 1;
                }
            }
            i3++;
            i2 = i;
        }
        this.minimapBitmap = new MinimapBitmap(this.minimapBitmap, 0, (3 - parcelOverlay.ParcelData_Field.SequenceID) * 64, iArr);
        if (this.userManager != null) {
            this.userManager.getMinimapBitmapPool().setData(SubscriptionSingleKey.Value, this.minimapBitmap);
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:26:0x0051  */
    /* JADX WARN: Removed duplicated region for block: B:46:? A[RETURN, SYNTHETIC] */
    @com.lumiyaviewer.lumiya.slproto.handler.SLEventQueueMessageHandler(eventName = com.lumiyaviewer.lumiya.slproto.caps.SLCapEventQueue.CapsEventType.ParcelProperties)
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public void HandleParcelProperties(com.lumiyaviewer.lumiya.slproto.llsd.LLSDNode r10) {
        /*
            r9 = this;
            r2 = 0
            java.lang.String r0 = "ParcelData"
            com.lumiyaviewer.lumiya.slproto.llsd.LLSDNode r4 = r10.byKey(r0)     // Catch: com.lumiyaviewer.lumiya.slproto.llsd.LLSDException -> L4a
            r3 = r2
            r1 = r2
        La:
            int r0 = r4.getCount()     // Catch: com.lumiyaviewer.lumiya.slproto.llsd.LLSDException -> L55
            if (r3 >= r0) goto L4f
            com.lumiyaviewer.lumiya.slproto.llsd.LLSDNode r0 = r4.byIndex(r3)     // Catch: com.lumiyaviewer.lumiya.slproto.llsd.LLSDException -> L55
            com.lumiyaviewer.lumiya.slproto.users.ParcelData r5 = new com.lumiyaviewer.lumiya.slproto.users.ParcelData     // Catch: com.lumiyaviewer.lumiya.slproto.llsd.LLSDException -> L40
            r5.<init>(r0)     // Catch: com.lumiyaviewer.lumiya.slproto.llsd.LLSDException -> L40
            int r6 = r5.getParcelID()     // Catch: com.lumiyaviewer.lumiya.slproto.llsd.LLSDException -> L40
            java.util.Map<java.lang.Integer, com.lumiyaviewer.lumiya.slproto.users.ParcelData> r0 = r9.parcels     // Catch: com.lumiyaviewer.lumiya.slproto.llsd.LLSDException -> L40
            java.lang.Integer r7 = java.lang.Integer.valueOf(r6)     // Catch: com.lumiyaviewer.lumiya.slproto.llsd.LLSDException -> L40
            r0.put(r7, r5)     // Catch: com.lumiyaviewer.lumiya.slproto.llsd.LLSDException -> L40
            boolean[] r5 = r5.getParcelBitmap()     // Catch: com.lumiyaviewer.lumiya.slproto.llsd.LLSDException -> L40
            r0 = r1
            r1 = r2
        L2c:
            r7 = 4096(0x1000, float:5.74E-42)
            if (r1 >= r7) goto L45
            boolean r7 = r5[r1]     // Catch: com.lumiyaviewer.lumiya.slproto.llsd.LLSDException -> L57
            if (r7 == 0) goto L3d
            int[] r7 = r9.parcelIDs     // Catch: com.lumiyaviewer.lumiya.slproto.llsd.LLSDException -> L57
            r7[r1] = r6     // Catch: com.lumiyaviewer.lumiya.slproto.llsd.LLSDException -> L57
            int r7 = r9.myAvatarParcelDataIndex     // Catch: com.lumiyaviewer.lumiya.slproto.llsd.LLSDException -> L57
            if (r1 != r7) goto L3d
            r0 = 1
        L3d:
            int r1 = r1 + 1
            goto L2c
        L40:
            r0 = move-exception
        L41:
            com.lumiyaviewer.lumiya.Debug.Warning(r0)     // Catch: com.lumiyaviewer.lumiya.slproto.llsd.LLSDException -> L55
            r0 = r1
        L45:
            int r1 = r3 + 1
            r3 = r1
            r1 = r0
            goto La
        L4a:
            r0 = move-exception
            r1 = r2
        L4c:
            r0.printStackTrace()
        L4f:
            if (r1 == 0) goto L54
            r9.requestUpdateAvatarParcelData()
        L54:
            return
        L55:
            r0 = move-exception
            goto L4c
        L57:
            r1 = move-exception
            r8 = r1
            r1 = r0
            r0 = r8
            goto L41
        */
        throw new UnsupportedOperationException("Method not decompiled: com.lumiyaviewer.lumiya.slproto.modules.SLMinimap.HandleParcelProperties(com.lumiyaviewer.lumiya.slproto.llsd.LLSDNode):void");
    }

    public Float getDistanceToUser(@Nullable UUID uuid) {
        if (uuid != null) {
            UserLocation userLocation = this.userPositions.get(uuid);
            return userLocation != null ? Float.valueOf(userLocation.distance) : Float.valueOf(Float.NaN);
        }
        return null;
    }

    @Nullable
    public LLVector3 getNearbyAgentLocation(UUID uuid) {
        SLObjectInfo avatarObject;
        if (this.gridConn == null || this.gridConn.parcelInfo == null || (avatarObject = this.gridConn.parcelInfo.getAvatarObject(uuid)) == null) {
            if (!Objects.equal(uuid, this.circuitInfo.agentID) || this.myAvatarPosition == null) {
                return null;
            }
            return new LLVector3(this.myAvatarPosition.getX(), this.myAvatarPosition.getY(), this.myAvatarPosition.getZ());
        }
        return avatarObject.getAbsolutePosition();
    }

    public List<ChatterID> getNearbyChatterList() {
        ArrayList arrayList = new ArrayList(this.userPositions.size());
        for (UserLocation userLocation : this.userPositions.values()) {
            arrayList.add(userLocation.chatterID);
        }
        return arrayList;
    }

    public void requestUpdateAvatarParcelData() {
        this.agentCircuit.execute(new Runnable() { // from class: com.lumiyaviewer.lumiya.slproto.modules.-$Lambda$eaDiotW55nmaHN5_b1ikeJpLLsk
            private final /* synthetic */ void $m$0() {
                ((SLMinimap) this).m223com_lumiyaviewer_lumiya_slproto_modules_SLMinimapmthref0();
            }

            @Override // java.lang.Runnable
            public final void run() {
                $m$0();
            }
        });
    }
}
