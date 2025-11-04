// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.modules;

import android.graphics.Bitmap$Config;
import android.graphics.Bitmap;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import com.lumiyaviewer.lumiya.slproto.objects.SLObjectInfo;
import com.lumiyaviewer.lumiya.slproto.types.LLVector3;
import com.lumiyaviewer.lumiya.slproto.caps.SLCapEventQueue;
import com.lumiyaviewer.lumiya.slproto.handler.SLEventQueueMessageHandler;
import com.lumiyaviewer.lumiya.slproto.llsd.LLSDNode;
import android.graphics.Color;
import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.slproto.messages.ParcelOverlay;
import com.lumiyaviewer.lumiya.slproto.handler.SLMessageHandler;
import java.util.Iterator;
import java.util.Set;
import com.lumiyaviewer.lumiya.slproto.users.manager.ChatterListType;
import com.lumiyaviewer.lumiya.slproto.users.ChatterID;
import com.lumiyaviewer.lumiya.utils.UUIDPool;
import com.google.common.base.Objects;
import java.util.HashSet;
import com.lumiyaviewer.lumiya.slproto.messages.CoarseLocationUpdate;
import com.lumiyaviewer.lumiya.slproto.modules.voice.SLVoice;
import com.lumiyaviewer.lumiya.slproto.users.manager.CurrentLocationInfo;
import com.lumiyaviewer.lumiya.react.SimpleRequestHandler;
import java.util.concurrent.ConcurrentHashMap;
import com.lumiyaviewer.lumiya.slproto.SLAgentCircuit;
import java.util.UUID;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import com.lumiyaviewer.lumiya.react.ResultHandler;
import com.lumiyaviewer.lumiya.react.SubscriptionSingleKey;
import com.lumiyaviewer.lumiya.react.RequestHandler;
import com.lumiyaviewer.lumiya.slproto.users.ParcelData;
import java.util.Map;
import javax.annotation.Nullable;
import com.lumiyaviewer.lumiya.slproto.types.ImmutableVector;
import javax.annotation.Nonnull;

public class SLMinimap extends SLModule
{
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
    
    SLMinimap(final SLAgentCircuit slAgentCircuit) {
        boolean afterTeleport = false;
        super(slAgentCircuit);
        this.minimapBitmap = new MinimapBitmap(256, 256);
        this.parcelIDs = new int[4096];
        this.parcels = new ConcurrentHashMap<Integer, ParcelData>();
        this.nearbyUsersCount = 0;
        this.chatRangeUsersCount = 0;
        this.userPositions = new ConcurrentHashMap<UUID, UserLocation>(1, 0.75f, 2);
        this.myAvatarPosition = null;
        this.afterTeleport = false;
        this.myAvatarParcelDataIndex = -1;
        this.userLocationRequestHandler = new SimpleRequestHandler<SubscriptionSingleKey>() {
            @Override
            public void onRequest(@Nonnull final SubscriptionSingleKey subscriptionSingleKey) {
                if (SLMinimap.this.userLocationsResultHandler != null) {
                    SLMinimap.this.userLocationsResultHandler.onResultData(subscriptionSingleKey, new UserLocations(SLMinimap.this.myAvatarPosition, SLMinimap.this.getMyAvatarHeading(), SLMinimap.this.userPositions));
                }
            }
        };
        this.userManager = UserManager.getUserManager(this.agentCircuit.circuitInfo.agentID);
        if (this.userManager != null) {
            this.userLocationsResultHandler = this.userManager.getUserLocationsPool().attachRequestHandler(this.userLocationRequestHandler);
        }
        else {
            this.userLocationsResultHandler = null;
        }
        if (slAgentCircuit.getAuthReply().fromTeleport) {
            afterTeleport = (slAgentCircuit.getAuthReply().isTemporary ^ true);
        }
        this.afterTeleport = afterTeleport;
    }
    
    private float getMyAvatarHeading() {
        return this.agentCircuit.getModules().avatarControl.getAgentHeading() * 3.1415927f / 180.0f;
    }
    
    private int getParcelDataIndex(final ImmutableVector immutableVector) {
        int n = 0;
        final int n2 = (int)Math.floor(immutableVector.getX() * 64.0f / 256.0f);
        final int n3 = (int)Math.floor(immutableVector.getY() * 64.0f / 256.0f);
        int n4;
        if (n2 < 0) {
            n4 = 0;
        }
        else if ((n4 = n2) >= 64) {
            n4 = 63;
        }
        if (n3 >= 0) {
            if (n3 >= 64) {
                n = 63;
            }
            else {
                n = n3;
            }
        }
        return n * 64 + n4;
    }
    
    private void updateAvatarParcelData() {
        ParcelData parcelData = null;
        if (this.myAvatarParcelDataIndex >= 0) {
            parcelData = this.parcels.get(this.parcelIDs[this.myAvatarParcelDataIndex]);
        }
        if (parcelData != null && this.afterTeleport) {
            this.afterTeleport = false;
            this.userManager.getChatterList().getActiveChattersManager().notifyTeleportComplete(parcelData.getName());
        }
        final SLVoice voice = this.agentCircuit.getModules().voice;
        if (parcelData != null) {
            voice.setCurrentParcel(parcelData.getParcelID());
        }
        this.userManager.setCurrentLocationInfo(CurrentLocationInfo.create(parcelData, this.nearbyUsersCount, this.chatRangeUsersCount, voice.getCurrentParcelVoiceChannel()));
    }
    
    @Override
    public void HandleCloseCircuit() {
        if (this.userManager != null) {
            this.userManager.getUserLocationsPool().detachRequestHandler(this.userLocationRequestHandler);
        }
        super.HandleCloseCircuit();
    }
    
    @SLMessageHandler
    public void HandleCoarseLocationUpdate(final CoarseLocationUpdate coarseLocationUpdate) {
        final int n = 0;
        final int n2 = 0;
        ParcelData parcelData;
        if (this.myAvatarParcelDataIndex >= 0) {
            parcelData = this.parcels.get(this.parcelIDs[this.myAvatarParcelDataIndex]);
        }
        else {
            parcelData = null;
        }
        final HashSet set = new HashSet(coarseLocationUpdate.Location_Fields.size());
        ParcelData parcelData2 = parcelData;
        Iterable<UUID> iterable = null;
        int n3 = 0;
        int n4 = 0;
        ParcelData parcelData3;
        Set<UUID> set2;
        int n6;
        int n7;
        for (int n5 = 0; n5 < coarseLocationUpdate.Location_Fields.size() && n5 < coarseLocationUpdate.AgentData_Fields.size(); ++n5, parcelData2 = parcelData3, iterable = set2, n3 = n6, n4 = n7) {
            final CoarseLocationUpdate.Location location = coarseLocationUpdate.Location_Fields.get(n5);
            final ImmutableVector immutableVector = new ImmutableVector((float)location.X, (float)location.Y, (float)(location.Z * 4));
            if (n5 == coarseLocationUpdate.Index_Field.You) {
                parcelData3 = parcelData2;
                set2 = (Set<UUID>)iterable;
                n6 = n3;
                n7 = n4;
                if (!Objects.equal(immutableVector, this.myAvatarPosition)) {
                    this.myAvatarPosition = immutableVector;
                    final int parcelDataIndex = this.getParcelDataIndex(this.myAvatarPosition);
                    if (parcelDataIndex != this.myAvatarParcelDataIndex) {
                        this.myAvatarParcelDataIndex = parcelDataIndex;
                        parcelData3 = this.parcels.get(this.parcelIDs[this.myAvatarParcelDataIndex]);
                        n6 = 1;
                        n7 = n4;
                        set2 = (Set<UUID>)iterable;
                    }
                    else {
                        n6 = 1;
                        parcelData3 = parcelData2;
                        set2 = (Set<UUID>)iterable;
                        n7 = n4;
                    }
                }
            }
            else {
                final UUID agentID = ((CoarseLocationUpdate.AgentData)coarseLocationUpdate.AgentData_Fields.get(n5)).AgentID;
                parcelData3 = parcelData2;
                set2 = (Set<UUID>)iterable;
                n6 = n3;
                n7 = n4;
                if (!UUIDPool.ZeroUUID.equals(agentID)) {
                    final UserLocation userLocation = this.userPositions.get(agentID);
                    int n8;
                    if (userLocation != null) {
                        if (!immutableVector.equals(userLocation.location)) {
                            userLocation.location = immutableVector;
                            n8 = 1;
                        }
                        else {
                            n8 = 0;
                        }
                    }
                    else {
                        this.userPositions.put(agentID, new UserLocation(ChatterID.getUserChatterID(this.userManager.getUserID(), agentID), immutableVector));
                        n8 = 1;
                        n4 = 1;
                    }
                    set2 = (Set<UUID>)iterable;
                    if (n8 != 0) {
                        if ((set2 = (Set<UUID>)iterable) == null) {
                            set2 = new HashSet<UUID>();
                        }
                        set2.add(agentID);
                    }
                    set.add(agentID);
                    parcelData3 = parcelData2;
                    n6 = n3;
                    n7 = n4;
                }
            }
        }
        final Iterator<UUID> iterator = this.userPositions.keySet().iterator();
        int n9 = n4;
        while (iterator.hasNext()) {
            final UUID uuid = iterator.next();
            if (!set.contains(uuid)) {
                iterator.remove();
                Set<UUID> set3;
                if ((set3 = (Set<UUID>)iterable) == null) {
                    set3 = new HashSet<UUID>();
                }
                set3.add(uuid);
                n9 = 1;
                iterable = set3;
            }
        }
        while (true) {
            Label_0949: {
                if (this.myAvatarPosition == null) {
                    break Label_0949;
                }
                int n10;
                if (n3 != 0) {
                    for (final UserLocation userLocation2 : this.userPositions.values()) {
                        userLocation2.distance = this.myAvatarPosition.distanceTo(userLocation2.location);
                    }
                    n10 = 1;
                }
                else {
                    if (iterable == null) {
                        break Label_0949;
                    }
                    final Iterator<UUID> iterator3 = iterable.iterator();
                    while (iterator3.hasNext()) {
                        final UserLocation userLocation3 = this.userPositions.get(iterator3.next());
                        if (userLocation3 != null) {
                            userLocation3.distance = this.myAvatarPosition.distanceTo(userLocation3.location);
                        }
                    }
                    n10 = 1;
                }
                int n11 = 0;
                Label_0824: {
                    if (n10 == 0) {
                        n11 = n;
                        if (n9 == 0) {
                            break Label_0824;
                        }
                    }
                    final Iterator<Object> iterator4 = this.userPositions.values().iterator();
                    int chatRangeUsersCount = 0;
                    while (iterator4.hasNext()) {
                        if (iterator4.next().distance <= 20.0f) {
                            ++chatRangeUsersCount;
                        }
                    }
                    int n12 = n2;
                    if (chatRangeUsersCount != this.chatRangeUsersCount) {
                        this.chatRangeUsersCount = chatRangeUsersCount;
                        n12 = 1;
                    }
                    n11 = n12;
                    if (this.nearbyUsersCount != this.userPositions.size()) {
                        this.nearbyUsersCount = this.userPositions.size();
                        n11 = 1;
                    }
                }
                if (parcelData2 != parcelData || n11 != 0) {
                    this.requestUpdateAvatarParcelData();
                }
                if (n9 != 0) {
                    this.userManager.getChatterList().updateList(ChatterListType.Nearby);
                }
                if (n3 != 0) {
                    this.userManager.getChatterList().updateDistanceToAllUsers();
                }
                else if (iterable != null) {
                    final Iterator<UUID> iterator5 = iterable.iterator();
                    while (iterator5.hasNext()) {
                        this.userManager.getChatterList().updateDistanceToUser(iterator5.next());
                    }
                }
                if (n3 != 0 || iterable != null) {
                    this.userManager.getUserLocationsPool().requestUpdate(SubscriptionSingleKey.Value);
                }
                return;
            }
            int n10 = 0;
            continue;
        }
    }
    
    @SLMessageHandler
    public void HandleParcelOverlay(final ParcelOverlay parcelOverlay) {
        Debug.Log("ParcelOverlay: SequenceID = " + parcelOverlay.ParcelData_Field.SequenceID);
        final byte[] data = parcelOverlay.ParcelData_Field.Data;
        final int n = data.length / 64;
        final int[] array = new int[n * 4 * 64 * 4];
        int n2 = 0;
        int n3;
        for (int i = 0; i < n; ++i, n2 = n3) {
            final int sequenceID = parcelOverlay.ParcelData_Field.SequenceID;
            for (int j = 0, n3 = n2; j < 64; ++j, ++n3) {
                final byte b = (byte)(data[n3] & 0xF);
                int n4 = 0;
                switch (b) {
                    case 0: {
                        n4 = Color.rgb(0, 192, 0);
                        break;
                    }
                    case 1: {
                        n4 = Color.rgb(32, 128, 32);
                        break;
                    }
                    case 2: {
                        n4 = Color.rgb(0, 128, 128);
                        break;
                    }
                    case 3: {
                        n4 = Color.rgb(0, 255, 255);
                        break;
                    }
                    case 4: {
                        n4 = Color.rgb(128, 128, 0);
                        break;
                    }
                    case 5: {
                        n4 = Color.rgb(255, 255, 0);
                        break;
                    }
                }
                int rgb = n4;
                if ((data[n3] & 0x20) != 0x0) {
                    final int red = Color.red(n4);
                    final int green = Color.green(n4);
                    final int blue = Color.blue(n4);
                    final int n5 = red + 64;
                    int n6 = blue;
                    int n7 = green;
                    int n8;
                    if ((n8 = n5) >= 255) {
                        final int n9 = n5 - 255;
                        final int n10 = n5 - n9;
                        final int n11 = green - n9;
                        final int n12 = blue - n9;
                        int n13;
                        if ((n13 = n11) < 0) {
                            n13 = 0;
                        }
                        n6 = n12;
                        n7 = n13;
                        n8 = n10;
                        if (n12 < 0) {
                            n6 = 0;
                            n8 = n10;
                            n7 = n13;
                        }
                    }
                    rgb = Color.rgb(n8, n7, n6);
                }
                for (int k = 0; k < 4; ++k) {
                    for (int l = 0; l < 4; ++l) {
                        int n14;
                        if ((k != 0 || i + sequenceID * 16 == 0 || (data[n3] & 0xFFFFFF80) == 0x0) && (l != 0 || j == 0 || (data[n3] & 0x40) == 0x0)) {
                            n14 = rgb;
                        }
                        else {
                            n14 = -1;
                        }
                        array[(n * 4 - 1 - (i * 4 + k)) * 256 + j * 4 + l] = n14;
                    }
                }
            }
        }
        this.minimapBitmap = new MinimapBitmap(this.minimapBitmap, 0, (3 - parcelOverlay.ParcelData_Field.SequenceID) * 64, array);
        if (this.userManager != null) {
            this.userManager.getMinimapBitmapPool().setData(SubscriptionSingleKey.Value, this.minimapBitmap);
        }
    }
    
    @SLEventQueueMessageHandler(eventName = SLCapEventQueue.CapsEventType.ParcelProperties)
    public void HandleParcelProperties(final LLSDNode p0) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     1: ldc_w           "ParcelData"
        //     4: invokevirtual   com/lumiyaviewer/lumiya/slproto/llsd/LLSDNode.byKey:(Ljava/lang/String;)Lcom/lumiyaviewer/lumiya/slproto/llsd/LLSDNode;
        //     7: astore_2       
        //     8: iconst_0       
        //     9: istore_3       
        //    10: iconst_0       
        //    11: istore          4
        //    13: iload           4
        //    15: istore          5
        //    17: iload           4
        //    19: istore          6
        //    21: iload_3        
        //    22: aload_2        
        //    23: invokevirtual   com/lumiyaviewer/lumiya/slproto/llsd/LLSDNode.getCount:()I
        //    26: if_icmpge       180
        //    29: iload           4
        //    31: istore          6
        //    33: aload_2        
        //    34: iload_3        
        //    35: invokevirtual   com/lumiyaviewer/lumiya/slproto/llsd/LLSDNode.byIndex:(I)Lcom/lumiyaviewer/lumiya/slproto/llsd/LLSDNode;
        //    38: astore_1       
        //    39: new             Lcom/lumiyaviewer/lumiya/slproto/users/ParcelData;
        //    42: astore          7
        //    44: aload           7
        //    46: aload_1        
        //    47: invokespecial   com/lumiyaviewer/lumiya/slproto/users/ParcelData.<init>:(Lcom/lumiyaviewer/lumiya/slproto/llsd/LLSDNode;)V
        //    50: aload           7
        //    52: invokevirtual   com/lumiyaviewer/lumiya/slproto/users/ParcelData.getParcelID:()I
        //    55: istore          8
        //    57: aload_0        
        //    58: getfield        com/lumiyaviewer/lumiya/slproto/modules/SLMinimap.parcels:Ljava/util/Map;
        //    61: iload           8
        //    63: invokestatic    java/lang/Integer.valueOf:(I)Ljava/lang/Integer;
        //    66: aload           7
        //    68: invokeinterface java/util/Map.put:(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
        //    73: pop            
        //    74: aload           7
        //    76: invokevirtual   com/lumiyaviewer/lumiya/slproto/users/ParcelData.getParcelBitmap:()[Z
        //    79: astore_1       
        //    80: iconst_0       
        //    81: istore          5
        //    83: iload           4
        //    85: istore          6
        //    87: iload           5
        //    89: sipush          4096
        //    92: if_icmpge       158
        //    95: iload           4
        //    97: istore          6
        //    99: aload_1        
        //   100: iload           5
        //   102: baload         
        //   103: ifeq            135
        //   106: aload_0        
        //   107: getfield        com/lumiyaviewer/lumiya/slproto/modules/SLMinimap.parcelIDs:[I
        //   110: iload           5
        //   112: iload           8
        //   114: iastore        
        //   115: aload_0        
        //   116: getfield        com/lumiyaviewer/lumiya/slproto/modules/SLMinimap.myAvatarParcelDataIndex:I
        //   119: istore          9
        //   121: iload           4
        //   123: istore          6
        //   125: iload           5
        //   127: iload           9
        //   129: if_icmpne       135
        //   132: iconst_1       
        //   133: istore          6
        //   135: iinc            5, 1
        //   138: iload           6
        //   140: istore          4
        //   142: goto            83
        //   145: astore_1       
        //   146: iload           4
        //   148: istore          6
        //   150: aload_1        
        //   151: invokestatic    com/lumiyaviewer/lumiya/Debug.Warning:(Ljava/lang/Throwable;)V
        //   154: iload           4
        //   156: istore          6
        //   158: iinc            3, 1
        //   161: iload           6
        //   163: istore          4
        //   165: goto            13
        //   168: astore_1       
        //   169: iconst_0       
        //   170: istore          6
        //   172: aload_1        
        //   173: invokevirtual   com/lumiyaviewer/lumiya/slproto/llsd/LLSDException.printStackTrace:()V
        //   176: iload           6
        //   178: istore          5
        //   180: iload           5
        //   182: ifeq            189
        //   185: aload_0        
        //   186: invokevirtual   com/lumiyaviewer/lumiya/slproto/modules/SLMinimap.requestUpdateAvatarParcelData:()V
        //   189: return         
        //   190: astore_1       
        //   191: goto            172
        //   194: astore_1       
        //   195: goto            146
        //    Exceptions:
        //  Try           Handler
        //  Start  End    Start  End    Type                                                
        //  -----  -----  -----  -----  ----------------------------------------------------
        //  0      8      168    172    Lcom/lumiyaviewer/lumiya/slproto/llsd/LLSDException;
        //  21     29     190    194    Lcom/lumiyaviewer/lumiya/slproto/llsd/LLSDException;
        //  33     39     190    194    Lcom/lumiyaviewer/lumiya/slproto/llsd/LLSDException;
        //  39     80     145    146    Lcom/lumiyaviewer/lumiya/slproto/llsd/LLSDException;
        //  106    121    194    198    Lcom/lumiyaviewer/lumiya/slproto/llsd/LLSDException;
        //  150    154    190    194    Lcom/lumiyaviewer/lumiya/slproto/llsd/LLSDException;
        // 
        // The error that occurred was:
        // 
        // java.lang.IndexOutOfBoundsException: Index 99 out of bounds for length 99
        //     at java.base/jdk.internal.util.Preconditions.outOfBounds(Preconditions.java:100)
        //     at java.base/jdk.internal.util.Preconditions.outOfBoundsCheckIndex(Preconditions.java:106)
        //     at java.base/jdk.internal.util.Preconditions.checkIndex(Preconditions.java:302)
        //     at java.base/java.util.Objects.checkIndex(Objects.java:385)
        //     at java.base/java.util.ArrayList.get(ArrayList.java:427)
        //     at com.strobel.decompiler.ast.AstBuilder.convertToAst(AstBuilder.java:3362)
        //     at com.strobel.decompiler.ast.AstBuilder.build(AstBuilder.java:112)
        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:203)
        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:93)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethodBody(AstBuilder.java:868)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethod(AstBuilder.java:761)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addTypeMembers(AstBuilder.java:638)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeCore(AstBuilder.java:605)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeNoCache(AstBuilder.java:195)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createType(AstBuilder.java:162)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addType(AstBuilder.java:137)
        //     at com.strobel.decompiler.languages.java.JavaLanguage.buildAst(JavaLanguage.java:71)
        //     at com.strobel.decompiler.languages.java.JavaLanguage.decompileType(JavaLanguage.java:59)
        //     at com.strobel.decompiler.DecompilerDriver.decompileType(DecompilerDriver.java:333)
        //     at com.strobel.decompiler.DecompilerDriver.decompileJar(DecompilerDriver.java:254)
        //     at com.strobel.decompiler.DecompilerDriver.main(DecompilerDriver.java:144)
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public Float getDistanceToUser(@Nullable final UUID uuid) {
        if (uuid == null) {
            return null;
        }
        final UserLocation userLocation = this.userPositions.get(uuid);
        if (userLocation != null) {
            return userLocation.distance;
        }
        return Float.NaN;
    }
    
    @Nullable
    public LLVector3 getNearbyAgentLocation(final UUID uuid) {
        if (this.gridConn != null && this.gridConn.parcelInfo != null) {
            final SLObjectInfo avatarObject = this.gridConn.parcelInfo.getAvatarObject(uuid);
            if (avatarObject != null) {
                return avatarObject.getAbsolutePosition();
            }
        }
        if (Objects.equal(uuid, this.circuitInfo.agentID) && this.myAvatarPosition != null) {
            return new LLVector3(this.myAvatarPosition.getX(), this.myAvatarPosition.getY(), this.myAvatarPosition.getZ());
        }
        return null;
    }
    
    public List<ChatterID> getNearbyChatterList() {
        final ArrayList list = new ArrayList(this.userPositions.size());
        final Iterator<Object> iterator = this.userPositions.values().iterator();
        while (iterator.hasNext()) {
            list.add(iterator.next().chatterID);
        }
        return list;
    }
    
    public void requestUpdateAvatarParcelData() {
        this.agentCircuit.execute(new _$Lambda$eaDiotW55nmaHN5_b1ikeJpLLsk(this));
    }
    
    public static class MinimapBitmap
    {
        private final int bitmapHeight;
        private final int bitmapWidth;
        final int[] colors;
        
        MinimapBitmap(final int bitmapWidth, final int bitmapHeight) {
            this.bitmapWidth = bitmapWidth;
            this.bitmapHeight = bitmapHeight;
            this.colors = new int[bitmapWidth * bitmapHeight];
        }
        
        MinimapBitmap(final MinimapBitmap minimapBitmap, final int n, final int n2, final int[] array) {
            this.bitmapWidth = minimapBitmap.bitmapWidth;
            this.bitmapHeight = minimapBitmap.bitmapHeight;
            System.arraycopy(array, 0, this.colors = Arrays.copyOf(minimapBitmap.colors, minimapBitmap.colors.length), this.bitmapHeight * n2 + n, array.length);
        }
        
        public Bitmap makeBitmap() {
            return Bitmap.createBitmap(this.colors, this.bitmapWidth, this.bitmapHeight, Bitmap$Config.ARGB_8888);
        }
        
        public void updateBitmap(final Bitmap bitmap) {
            bitmap.setPixels(this.colors, 0, this.bitmapWidth, 0, 0, this.bitmapWidth, this.bitmapHeight);
        }
    }
    
    public static class UserLocation
    {
        @Nonnull
        public final ChatterID chatterID;
        public volatile float distance;
        @Nonnull
        public volatile ImmutableVector location;
        
        UserLocation(@Nonnull final ChatterID chatterID, @Nonnull final ImmutableVector location) {
            this.chatterID = chatterID;
            this.location = location;
            this.distance = Float.NaN;
        }
    }
    
    public static class UserLocations
    {
        public final float myAvatarHeading;
        @Nullable
        public final ImmutableVector myAvatarPosition;
        public final Map<UUID, UserLocation> userPositions;
        
        UserLocations(@Nullable final ImmutableVector myAvatarPosition, final float myAvatarHeading, final Map<UUID, UserLocation> userPositions) {
            this.myAvatarPosition = myAvatarPosition;
            this.myAvatarHeading = myAvatarHeading;
            this.userPositions = userPositions;
        }
    }
}
