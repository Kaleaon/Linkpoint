// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.users;

import com.google.common.base.Objects;
import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.GlobalOptions;
import com.lumiyaviewer.lumiya.dao.UserName;
import com.lumiyaviewer.lumiya.react.Subscribable;
import com.lumiyaviewer.lumiya.react.Subscription;
import com.lumiyaviewer.lumiya.react.SubscriptionSingleDataPool;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import com.lumiyaviewer.lumiya.slproto.messages.GroupProfileReply;
import com.lumiyaviewer.lumiya.slproto.users.manager.CurrentLocationInfo;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import java.lang.ref.WeakReference;
import java.util.concurrent.Executor;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.users:
//            ParcelData, ChatterID, SLMessageResponseCacher

public class ChatterNameRetriever
{
    public static interface OnChatterNameUpdated
    {

        public abstract void onChatterNameUpdated(ChatterNameRetriever chatternameretriever);
    }


    public final ChatterID chatterID;
    private final Executor executor;
    private final WeakReference listener;
    private volatile String resolvedName;
    private volatile String resolvedSecondaryName;
    private Subscription subscription;

    public ChatterNameRetriever(ChatterID chatterid, OnChatterNameUpdated onchatternameupdated, Executor executor1)
    {
        subscription = null;
        chatterID = chatterid;
        listener = new WeakReference(onchatternameupdated);
        executor = executor1;
        subscribe();
    }

    public ChatterNameRetriever(ChatterID chatterid, OnChatterNameUpdated onchatternameupdated, Executor executor1, boolean flag)
    {
        subscription = null;
        chatterID = chatterid;
        listener = new WeakReference(onchatternameupdated);
        executor = executor1;
        if (flag)
        {
            subscribe();
        }
    }

    private void onCurrentLocation(CurrentLocationInfo currentlocationinfo)
    {
        Object obj = null;
        ParcelData parceldata = currentlocationinfo.parcelData();
        currentlocationinfo = obj;
        if (parceldata != null)
        {
            currentlocationinfo = parceldata.getName();
        }
        if (!Objects.equal(resolvedName, currentlocationinfo))
        {
            resolvedName = currentlocationinfo;
            currentlocationinfo = (OnChatterNameUpdated)listener.get();
            if (currentlocationinfo != null)
            {
                currentlocationinfo.onChatterNameUpdated(this);
            }
        }
    }

    private void onGroupProfile(GroupProfileReply groupprofilereply)
    {
        resolvedName = SLMessage.stringFromVariableOEM(groupprofilereply.GroupData_Field.Name);
        resolvedSecondaryName = SLMessage.stringFromVariableOEM(groupprofilereply.GroupData_Field.Name);
        groupprofilereply = (OnChatterNameUpdated)listener.get();
        if (groupprofilereply != null)
        {
            groupprofilereply.onChatterNameUpdated(this);
        }
    }

    private void onUserName(UserName username)
    {
        Debug.Printf("Resolved name for %s", new Object[] {
            username.getUuid()
        });
        if (GlobalOptions.getInstance().isLegacyUserNames())
        {
            resolvedName = username.getUserName();
            resolvedSecondaryName = username.getDisplayName();
        } else
        {
            resolvedName = username.getDisplayName();
            resolvedSecondaryName = username.getUserName();
        }
        username = (OnChatterNameUpdated)listener.get();
        if (username != null)
        {
            username.onChatterNameUpdated(this);
        }
    }

    void _2D_com_lumiyaviewer_lumiya_slproto_users_ChatterNameRetriever_2D_mthref_2D_0(CurrentLocationInfo currentlocationinfo)
    {
        onCurrentLocation(currentlocationinfo);
    }

    void _2D_com_lumiyaviewer_lumiya_slproto_users_ChatterNameRetriever_2D_mthref_2D_1(UserName username)
    {
        onUserName(username);
    }

    void _2D_com_lumiyaviewer_lumiya_slproto_users_ChatterNameRetriever_2D_mthref_2D_2(GroupProfileReply groupprofilereply)
    {
        onGroupProfile(groupprofilereply);
    }

    public void dispose()
    {
        if (subscription != null)
        {
            subscription.unsubscribe();
        }
    }

    public String getResolvedName()
    {
        return resolvedName;
    }

    public String getResolvedSecondaryName()
    {
        return resolvedSecondaryName;
    }

    public void subscribe()
    {
        UserManager usermanager = chatterID.getUserManager();
        if (usermanager != null)
        {
            if (chatterID.getChatterType() == ChatterID.ChatterType.Local)
            {
                subscription = usermanager.getCurrentLocationInfo().subscribe(SubscriptionSingleDataPool.getSingleDataKey(), executor, new _2D_.Lambda.Tr7QBnh_GnHDqFtHSpMdsLw3Yfs(this));
                return;
            }
            if (chatterID instanceof ChatterID.ChatterIDUser)
            {
                subscription = usermanager.getUserNames().subscribe(((ChatterID.ChatterIDUser)chatterID).getChatterUUID(), executor, new _2D_.Lambda.Tr7QBnh_GnHDqFtHSpMdsLw3Yfs._cls1(this));
                return;
            }
            if (chatterID instanceof ChatterID.ChatterIDGroup)
            {
                subscription = usermanager.getCachedGroupProfiles().getPool().subscribe(((ChatterID.ChatterIDGroup)chatterID).getChatterUUID(), executor, new _2D_.Lambda.Tr7QBnh_GnHDqFtHSpMdsLw3Yfs._cls2(this));
                return;
            } else
            {
                subscription = null;
                return;
            }
        } else
        {
            subscription = null;
            return;
        }
    }
}
