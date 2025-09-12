// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.modules;

import android.graphics.Bitmap;
import android.graphics.Color;
import com.google.common.base.Objects;
import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.react.RequestHandler;
import com.lumiyaviewer.lumiya.react.ResultHandler;
import com.lumiyaviewer.lumiya.react.SimpleRequestHandler;
import com.lumiyaviewer.lumiya.react.SubscriptionPool;
import com.lumiyaviewer.lumiya.react.SubscriptionSingleDataPool;
import com.lumiyaviewer.lumiya.react.SubscriptionSingleKey;
import com.lumiyaviewer.lumiya.slproto.SLAgentCircuit;
import com.lumiyaviewer.lumiya.slproto.SLCircuitInfo;
import com.lumiyaviewer.lumiya.slproto.SLGridConnection;
import com.lumiyaviewer.lumiya.slproto.SLParcelInfo;
import com.lumiyaviewer.lumiya.slproto.auth.SLAuthReply;
import com.lumiyaviewer.lumiya.slproto.llsd.LLSDException;
import com.lumiyaviewer.lumiya.slproto.llsd.LLSDNode;
import com.lumiyaviewer.lumiya.slproto.messages.CoarseLocationUpdate;
import com.lumiyaviewer.lumiya.slproto.messages.ParcelOverlay;
import com.lumiyaviewer.lumiya.slproto.modules.voice.SLVoice;
import com.lumiyaviewer.lumiya.slproto.objects.SLObjectInfo;
import com.lumiyaviewer.lumiya.slproto.types.ImmutableVector;
import com.lumiyaviewer.lumiya.slproto.types.LLVector3;
import com.lumiyaviewer.lumiya.slproto.users.ChatterID;
import com.lumiyaviewer.lumiya.slproto.users.ParcelData;
import com.lumiyaviewer.lumiya.slproto.users.manager.ActiveChattersManager;
import com.lumiyaviewer.lumiya.slproto.users.manager.ChatterList;
import com.lumiyaviewer.lumiya.slproto.users.manager.ChatterListType;
import com.lumiyaviewer.lumiya.slproto.users.manager.CurrentLocationInfo;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import com.lumiyaviewer.lumiya.utils.UUIDPool;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.modules:
//            SLModule, SLModules, SLAvatarControl

public class SLMinimap extends SLModule
{
    public static class MinimapBitmap
    {

        private final int bitmapHeight;
        private final int bitmapWidth;
        final int colors[];

        public Bitmap makeBitmap()
        {
            return Bitmap.createBitmap(colors, bitmapWidth, bitmapHeight, android.graphics.Bitmap.Config.ARGB_8888);
        }

        public void updateBitmap(Bitmap bitmap)
        {
            bitmap.setPixels(colors, 0, bitmapWidth, 0, 0, bitmapWidth, bitmapHeight);
        }

        MinimapBitmap(int i, int j)
        {
            bitmapWidth = i;
            bitmapHeight = j;
            colors = new int[i * j];
        }

        MinimapBitmap(MinimapBitmap minimapbitmap, int i, int j, int ai[])
        {
            bitmapWidth = minimapbitmap.bitmapWidth;
            bitmapHeight = minimapbitmap.bitmapHeight;
            colors = Arrays.copyOf(minimapbitmap.colors, minimapbitmap.colors.length);
            System.arraycopy(ai, 0, colors, bitmapHeight * j + i, ai.length);
        }
    }

    public static class UserLocation
    {

        public final ChatterID chatterID;
        public volatile float distance;
        public volatile ImmutableVector location;

        UserLocation(ChatterID chatterid, ImmutableVector immutablevector)
        {
            chatterID = chatterid;
            location = immutablevector;
            distance = (0.0F / 0.0F);
        }
    }

    public static class UserLocations
    {

        public final float myAvatarHeading;
        public final ImmutableVector myAvatarPosition;
        public final Map userPositions;

        UserLocations(ImmutableVector immutablevector, float f, Map map)
        {
            myAvatarPosition = immutablevector;
            myAvatarHeading = f;
            userPositions = map;
        }
    }


    public static final float CHAT_RANGE = 20F;
    private static final int parcelBitmapSize = 256;
    public static final int parcelDataSize = 64;
    private static final byte parcelOverlayFlagBorderSouth = -128;
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
    private volatile MinimapBitmap minimapBitmap;
    private int myAvatarParcelDataIndex;
    private ImmutableVector myAvatarPosition;
    private int nearbyUsersCount;
    private final int parcelIDs[] = new int[4096];
    private final Map parcels = new ConcurrentHashMap();
    private final RequestHandler userLocationRequestHandler = new SimpleRequestHandler() {

        final SLMinimap this$0;

        public void onRequest(SubscriptionSingleKey subscriptionsinglekey)
        {
            if (SLMinimap._2D_get1(SLMinimap.this) != null)
            {
                SLMinimap._2D_get1(SLMinimap.this).onResultData(subscriptionsinglekey, new UserLocations(SLMinimap._2D_get0(SLMinimap.this), SLMinimap._2D_wrap0(SLMinimap.this), SLMinimap._2D_get2(SLMinimap.this)));
            }
        }

        public volatile void onRequest(Object obj)
        {
            onRequest((SubscriptionSingleKey)obj);
        }

            
            {
                this$0 = SLMinimap.this;
                super();
            }
    };
    private final ResultHandler userLocationsResultHandler;
    private final UserManager userManager;
    private final Map userPositions = new ConcurrentHashMap(1, 0.75F, 2);

    static ImmutableVector _2D_get0(SLMinimap slminimap)
    {
        return slminimap.myAvatarPosition;
    }

    static ResultHandler _2D_get1(SLMinimap slminimap)
    {
        return slminimap.userLocationsResultHandler;
    }

    static Map _2D_get2(SLMinimap slminimap)
    {
        return slminimap.userPositions;
    }

    static float _2D_wrap0(SLMinimap slminimap)
    {
        return slminimap.getMyAvatarHeading();
    }

    SLMinimap(SLAgentCircuit slagentcircuit)
    {
        boolean flag = false;
        super(slagentcircuit);
        minimapBitmap = new MinimapBitmap(256, 256);
        nearbyUsersCount = 0;
        chatRangeUsersCount = 0;
        myAvatarPosition = null;
        afterTeleport = false;
        myAvatarParcelDataIndex = -1;
        userManager = UserManager.getUserManager(agentCircuit.circuitInfo.agentID);
        if (userManager != null)
        {
            userLocationsResultHandler = userManager.getUserLocationsPool().attachRequestHandler(userLocationRequestHandler);
        } else
        {
            userLocationsResultHandler = null;
        }
        if (slagentcircuit.getAuthReply().fromTeleport)
        {
            flag = slagentcircuit.getAuthReply().isTemporary ^ true;
        }
        afterTeleport = flag;
    }

    private float getMyAvatarHeading()
    {
        return (agentCircuit.getModules().avatarControl.getAgentHeading() * 3.141593F) / 180F;
    }

    private int getParcelDataIndex(ImmutableVector immutablevector)
    {
        int j = 0;
        int l = (int)Math.floor((immutablevector.getX() * 64F) / 256F);
        int k = (int)Math.floor((immutablevector.getY() * 64F) / 256F);
        int i;
        if (l < 0)
        {
            i = 0;
        } else
        {
            i = l;
            if (l >= 64)
            {
                i = 63;
            }
        }
        if (k >= 0)
        {
            if (k >= 64)
            {
                j = 63;
            } else
            {
                j = k;
            }
        }
        return j * 64 + i;
    }

    private void updateAvatarParcelData()
    {
        Object obj = null;
        if (myAvatarParcelDataIndex >= 0)
        {
            obj = (ParcelData)parcels.get(Integer.valueOf(parcelIDs[myAvatarParcelDataIndex]));
        }
        if (obj != null && afterTeleport)
        {
            afterTeleport = false;
            userManager.getChatterList().getActiveChattersManager().notifyTeleportComplete(((ParcelData) (obj)).getName());
        }
        SLVoice slvoice = agentCircuit.getModules().voice;
        if (obj != null)
        {
            slvoice.setCurrentParcel(((ParcelData) (obj)).getParcelID());
        }
        obj = CurrentLocationInfo.create(((ParcelData) (obj)), nearbyUsersCount, chatRangeUsersCount, slvoice.getCurrentParcelVoiceChannel());
        userManager.setCurrentLocationInfo(((CurrentLocationInfo) (obj)));
    }

    void _2D_com_lumiyaviewer_lumiya_slproto_modules_SLMinimap_2D_mthref_2D_0()
    {
        updateAvatarParcelData();
    }

    public void HandleCloseCircuit()
    {
        if (userManager != null)
        {
            userManager.getUserLocationsPool().detachRequestHandler(userLocationRequestHandler);
        }
        super.HandleCloseCircuit();
    }

    public void HandleCoarseLocationUpdate(CoarseLocationUpdate coarselocationupdate)
    {
        boolean flag1 = false;
        boolean flag = false;
        Object obj;
        Object obj1;
        ParcelData parceldata;
        HashSet hashset;
        int i;
        int k;
        int l;
        if (myAvatarParcelDataIndex >= 0)
        {
            obj1 = (ParcelData)parcels.get(Integer.valueOf(parcelIDs[myAvatarParcelDataIndex]));
        } else
        {
            obj1 = null;
        }
        hashset = new HashSet(coarselocationupdate.Location_Fields.size());
        parceldata = ((ParcelData) (obj1));
        obj = null;
        k = 0;
        i = 0;
        l = 0;
        while (l < coarselocationupdate.Location_Fields.size() && l < coarselocationupdate.AgentData_Fields.size()) 
        {
            com.lumiyaviewer.lumiya.slproto.messages.CoarseLocationUpdate.Location location = (com.lumiyaviewer.lumiya.slproto.messages.CoarseLocationUpdate.Location)coarselocationupdate.Location_Fields.get(l);
            ImmutableVector immutablevector = new ImmutableVector(location.X, location.Y, location.Z * 4);
            if (l == coarselocationupdate.Index_Field.You)
            {
                Object obj3 = parceldata;
                Object obj2 = obj;
                int j = k;
                int i1 = i;
                if (!Objects.equal(immutablevector, myAvatarPosition))
                {
                    myAvatarPosition = immutablevector;
                    j = getParcelDataIndex(myAvatarPosition);
                    UUID uuid;
                    if (j != myAvatarParcelDataIndex)
                    {
                        myAvatarParcelDataIndex = j;
                        obj3 = (ParcelData)parcels.get(Integer.valueOf(parcelIDs[myAvatarParcelDataIndex]));
                        j = 1;
                        i1 = i;
                        obj2 = obj;
                    } else
                    {
                        j = 1;
                        obj3 = parceldata;
                        obj2 = obj;
                        i1 = i;
                    }
                }
            } else
            {
                uuid = ((com.lumiyaviewer.lumiya.slproto.messages.CoarseLocationUpdate.AgentData)coarselocationupdate.AgentData_Fields.get(l)).AgentID;
                obj3 = parceldata;
                obj2 = obj;
                j = k;
                i1 = i;
                if (!UUIDPool.ZeroUUID.equals(uuid))
                {
                    obj2 = (UserLocation)userPositions.get(uuid);
                    if (obj2 != null)
                    {
                        if (!immutablevector.equals(((UserLocation) (obj2)).location))
                        {
                            obj2.location = immutablevector;
                            j = 1;
                        } else
                        {
                            j = 0;
                        }
                    } else
                    {
                        userPositions.put(uuid, new UserLocation(ChatterID.getUserChatterID(userManager.getUserID(), uuid), immutablevector));
                        j = 1;
                        i = 1;
                    }
                    obj2 = obj;
                    if (j != 0)
                    {
                        obj2 = obj;
                        if (obj == null)
                        {
                            obj2 = new HashSet();
                        }
                        ((Set) (obj2)).add(uuid);
                    }
                    hashset.add(uuid);
                    obj3 = parceldata;
                    j = k;
                    i1 = i;
                }
            }
            l++;
            parceldata = ((ParcelData) (obj3));
            obj = obj2;
            k = j;
            i = i1;
        }
        obj2 = userPositions.keySet().iterator();
        l = i;
        do
        {
            if (!((Iterator) (obj2)).hasNext())
            {
                break;
            }
            obj3 = (UUID)((Iterator) (obj2)).next();
            if (!hashset.contains(obj3))
            {
                ((Iterator) (obj2)).remove();
                coarselocationupdate = ((CoarseLocationUpdate) (obj));
                if (obj == null)
                {
                    coarselocationupdate = new HashSet();
                }
                coarselocationupdate.add(obj3);
                l = 1;
                obj = coarselocationupdate;
            }
        } while (true);
        if (myAvatarPosition == null) goto _L2; else goto _L1
_L1:
        if (k == 0) goto _L4; else goto _L3
_L3:
        for (coarselocationupdate = userPositions.values().iterator(); coarselocationupdate.hasNext();)
        {
            obj2 = (UserLocation)coarselocationupdate.next();
            obj2.distance = myAvatarPosition.distanceTo(((UserLocation) (obj2)).location);
        }

        i = 1;
_L12:
        if (i != 0) goto _L6; else goto _L5
_L5:
        j = ((flag1) ? 1 : 0);
        if (l == 0) goto _L7; else goto _L6
_L4:
        if (obj != null)
        {
            coarselocationupdate = ((Iterable) (obj)).iterator();
            do
            {
                if (!coarselocationupdate.hasNext())
                {
                    break;
                }
                obj2 = (UUID)coarselocationupdate.next();
                obj2 = (UserLocation)userPositions.get(obj2);
                if (obj2 != null)
                {
                    obj2.distance = myAvatarPosition.distanceTo(((UserLocation) (obj2)).location);
                }
            } while (true);
            i = 1;
            continue; /* Loop/switch isn't completed */
        }
          goto _L2
_L6:
        coarselocationupdate = userPositions.values().iterator();
        i = 0;
        do
        {
            if (!coarselocationupdate.hasNext())
            {
                break;
            }
            if (((UserLocation)coarselocationupdate.next()).distance <= 20F)
            {
                i++;
            }
        } while (true);
        j = ((flag) ? 1 : 0);
        if (i != chatRangeUsersCount)
        {
            chatRangeUsersCount = i;
            j = 1;
        }
        if (nearbyUsersCount != userPositions.size())
        {
            nearbyUsersCount = userPositions.size();
            j = 1;
        }
_L7:
        if (parceldata != obj1 || j != 0)
        {
            requestUpdateAvatarParcelData();
        }
        if (l != 0)
        {
            userManager.getChatterList().updateList(ChatterListType.Nearby);
        }
        if (k == 0) goto _L9; else goto _L8
_L8:
        userManager.getChatterList().updateDistanceToAllUsers();
_L10:
        if (k != 0 || obj != null)
        {
            userManager.getUserLocationsPool().requestUpdate(SubscriptionSingleKey.Value);
        }
        return;
_L9:
        if (obj != null)
        {
            coarselocationupdate = ((Iterable) (obj)).iterator();
            while (coarselocationupdate.hasNext()) 
            {
                obj1 = (UUID)coarselocationupdate.next();
                userManager.getChatterList().updateDistanceToUser(((UUID) (obj1)));
            }
        }
        if (true) goto _L10; else goto _L2
_L2:
        i = 0;
        if (true) goto _L12; else goto _L11
_L11:
    }

    public void HandleParcelOverlay(ParcelOverlay parceloverlay)
    {
        byte abyte0[];
        int ai[];
        int i;
        int l;
        int l2;
        Debug.Log((new StringBuilder()).append("ParcelOverlay: SequenceID = ").append(parceloverlay.ParcelData_Field.SequenceID).toString());
        abyte0 = parceloverlay.ParcelData_Field.Data;
        l2 = abyte0.length / 64;
        ai = new int[l2 * 4 * 64 * 4];
        i = 0;
        l = 0;
_L13:
        if (l >= l2) goto _L2; else goto _L1
_L1:
        int j;
        int i1;
        int i3;
        i3 = parceloverlay.ParcelData_Field.SequenceID;
        i1 = 0;
        j = i;
_L12:
        byte byte0;
        if (i1 >= 64)
        {
            break; /* Loop/switch isn't completed */
        }
        byte0 = (byte)(abyte0[j] & 0xf);
        i = 0;
        byte0;
        JVM INSTR tableswitch 0 5: default 144
    //                   0 372
    //                   1 385
    //                   2 400
    //                   3 415
    //                   4 430
    //                   5 445;
           goto _L3 _L4 _L5 _L6 _L7 _L8 _L9
_L9:
        break MISSING_BLOCK_LABEL_445;
_L3:
        break; /* Loop/switch isn't completed */
_L4:
        i = Color.rgb(0, 192, 0);
          goto _L10
_L5:
        i = Color.rgb(32, 128, 32);
          goto _L10
_L6:
        i = Color.rgb(0, 128, 128);
          goto _L10
_L7:
        i = Color.rgb(0, 255, 255);
          goto _L10
_L8:
        i = Color.rgb(128, 128, 0);
          goto _L10
        i = Color.rgb(255, 255, 0);
_L10:
        int k = i;
        if ((abyte0[j] & 0x20) != 0)
        {
            int j1 = Color.red(i);
            k = Color.green(i);
            int k2 = Color.blue(i);
            int j2 = j1 + 64;
            i = k2;
            j1 = k;
            int l1 = j2;
            if (j2 >= 255)
            {
                j1 = j2 - 255;
                j2 -= j1;
                i = k - j1;
                k2 -= j1;
                k = i;
                if (i < 0)
                {
                    k = 0;
                }
                i = k2;
                j1 = k;
                l1 = j2;
                if (k2 < 0)
                {
                    i = 0;
                    l1 = j2;
                    j1 = k;
                }
            }
            k = Color.rgb(l1, j1, i);
        }
label0:
        for (i = 0; i < 4; i++)
        {
            int k1 = 0;
            do
            {
                if (k1 >= 4)
                {
                    continue label0;
                }
                int i2;
                if (i == 0 && l + i3 * 16 != 0 && (abyte0[j] & 0xffffff80) != 0 || k1 == 0 && i1 != 0 && (abyte0[j] & 0x40) != 0)
                {
                    i2 = -1;
                } else
                {
                    i2 = k;
                }
                ai[(l2 * 4 - 1 - (l * 4 + i)) * 256 + i1 * 4 + k1] = i2;
                k1++;
            } while (true);
        }

        i1++;
        j++;
        if (true) goto _L12; else goto _L11
_L11:
        l++;
        i = j;
          goto _L13
_L2:
        minimapBitmap = new MinimapBitmap(minimapBitmap, 0, (3 - parceloverlay.ParcelData_Field.SequenceID) * 64, ai);
        if (userManager != null)
        {
            userManager.getMinimapBitmapPool().setData(SubscriptionSingleKey.Value, minimapBitmap);
        }
        return;
    }

    public void HandleParcelProperties(LLSDNode llsdnode)
    {
        LLSDNode llsdnode1 = llsdnode.byKey("ParcelData");
        boolean flag;
        int i;
        i = 0;
        flag = false;
_L5:
        boolean flag1;
        int j;
        j = ((flag) ? 1 : 0);
        flag1 = flag;
        if (i >= llsdnode1.getCount()) goto _L2; else goto _L1
_L1:
        flag1 = flag;
        llsdnode = llsdnode1.byIndex(i);
        int k;
        llsdnode = new ParcelData(llsdnode);
        k = llsdnode.getParcelID();
        parcels.put(Integer.valueOf(k), llsdnode);
        llsdnode = llsdnode.getParcelBitmap();
        j = 0;
_L4:
        flag1 = flag;
        if (j >= 4096)
        {
            break; /* Loop/switch isn't completed */
        }
        flag1 = flag;
        if (llsdnode[j] == 0)
        {
            break MISSING_BLOCK_LABEL_126;
        }
        int l;
        parcelIDs[j] = k;
        l = myAvatarParcelDataIndex;
        flag1 = flag;
        if (j == l)
        {
            flag1 = true;
        }
        j++;
        flag = flag1;
        if (true) goto _L4; else goto _L3
        llsdnode;
_L8:
        flag1 = flag;
        Debug.Warning(llsdnode);
        flag1 = flag;
_L3:
        i++;
        flag = flag1;
          goto _L5
        llsdnode;
        flag1 = false;
_L7:
        llsdnode.printStackTrace();
        j = ((flag1) ? 1 : 0);
_L2:
        if (j != 0)
        {
            requestUpdateAvatarParcelData();
        }
        return;
        llsdnode;
        if (true) goto _L7; else goto _L6
_L6:
        llsdnode;
          goto _L8
    }

    public Float getDistanceToUser(UUID uuid)
    {
        if (uuid != null)
        {
            uuid = (UserLocation)userPositions.get(uuid);
            if (uuid != null)
            {
                return Float.valueOf(((UserLocation) (uuid)).distance);
            } else
            {
                return Float.valueOf((0.0F / 0.0F));
            }
        } else
        {
            return null;
        }
    }

    public LLVector3 getNearbyAgentLocation(UUID uuid)
    {
        if (gridConn != null && gridConn.parcelInfo != null)
        {
            SLObjectInfo slobjectinfo = gridConn.parcelInfo.getAvatarObject(uuid);
            if (slobjectinfo != null)
            {
                return slobjectinfo.getAbsolutePosition();
            }
        }
        if (Objects.equal(uuid, circuitInfo.agentID) && myAvatarPosition != null)
        {
            return new LLVector3(myAvatarPosition.getX(), myAvatarPosition.getY(), myAvatarPosition.getZ());
        } else
        {
            return null;
        }
    }

    public List getNearbyChatterList()
    {
        ArrayList arraylist = new ArrayList(userPositions.size());
        for (Iterator iterator = userPositions.values().iterator(); iterator.hasNext(); arraylist.add(((UserLocation)iterator.next()).chatterID)) { }
        return arraylist;
    }

    public void requestUpdateAvatarParcelData()
    {
        agentCircuit.execute(new _2D_.Lambda.eaDiotW55nmaHN5_b1ikeJpLLsk(this));
    }
}
