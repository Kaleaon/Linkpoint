// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.ui.common;

import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import com.google.common.base.Objects;
import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.react.UIThreadExecutor;
import com.lumiyaviewer.lumiya.slproto.users.ChatterID;
import com.lumiyaviewer.lumiya.slproto.users.ChatterNameRetriever;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;

// Referenced classes of package com.lumiyaviewer.lumiya.ui.common:
//            FragmentWithTitle

public abstract class ChatterFragment extends FragmentWithTitle
    implements com.lumiyaviewer.lumiya.slproto.users.ChatterNameRetriever.OnChatterNameUpdated
{

    public static final String CHATTER_ID_KEY = "chatterID";
    protected ChatterID chatterID;
    protected ChatterNameRetriever nameRetriever;
    private boolean showChatterTitle;
    protected UserManager userManager;

    public ChatterFragment()
    {
        showChatterTitle = true;
    }

    private ChatterNameRetriever getNameRetriever(ChatterID chatterid)
    {
        String s;
        if (chatterid != null)
        {
            s = chatterid.toString();
        } else
        {
            s = "null";
        }
        Debug.Printf("UserFunctionsFragment: ChatterNameRetriever: requesting for %s", new Object[] {
            s
        });
        if (chatterid != null)
        {
            return new ChatterNameRetriever(chatterid, this, UIThreadExecutor.getInstance());
        } else
        {
            return null;
        }
    }

    public static Bundle makeSelection(ChatterID chatterid)
    {
        Bundle bundle = new Bundle();
        bundle.putParcelable("chatterID", chatterid);
        return bundle;
    }

    private void updateFragmentTitle(ChatterNameRetriever chatternameretriever)
    {
label0:
        {
            Debug.Printf("updateTitle: updating fragment title: retriever = %s, showChatterTitle %b", new Object[] {
                chatternameretriever, Boolean.valueOf(showChatterTitle)
            });
            if (showChatterTitle)
            {
                if (chatternameretriever != null)
                {
                    break label0;
                }
                setTitle(null, null);
            }
            return;
        }
        chatternameretriever = chatternameretriever.getResolvedName();
        if (chatternameretriever != null)
        {
            setTitle(decorateFragmentTitle(chatternameretriever), null);
            return;
        } else
        {
            setTitle(getString(0x7f0901c8), null);
            return;
        }
    }

    protected String decorateFragmentTitle(String s)
    {
        return s;
    }

    public void onChatterNameUpdated(ChatterNameRetriever chatternameretriever)
    {
        String s;
        if (chatterID != null)
        {
            s = chatterID.toString();
        } else
        {
            s = "null";
        }
        Debug.Printf("updateTitle: ChatterNameRetriever: retrieved for %s", new Object[] {
            s
        });
        if (chatterID != null && Objects.equal(chatternameretriever.chatterID, chatterID))
        {
            Debug.Printf("UserFunctionsFragment: updating fragment title", new Object[0]);
            updateFragmentTitle(chatternameretriever);
            chatternameretriever = getActivity();
            if (chatternameretriever != null)
            {
                ActivityCompat.invalidateOptionsMenu(chatternameretriever);
            }
        }
    }

    protected abstract void onShowUser(ChatterID chatterid);

    public void onStart()
    {
        super.onStart();
        setNewUser((ChatterID)getArguments().getParcelable("chatterID"));
    }

    public void onStop()
    {
        setNewUser(null);
        super.onStop();
    }

    void setNewUser(ChatterID chatterid)
    {
        UserManager usermanager = null;
        chatterID = chatterid;
        if (chatterid != null)
        {
            usermanager = chatterid.getUserManager();
        }
        userManager = usermanager;
        if (nameRetriever != null)
        {
            if (!Objects.equal(nameRetriever.chatterID, chatterid))
            {
                nameRetriever.dispose();
                nameRetriever = getNameRetriever(chatterid);
            }
        } else
        {
            nameRetriever = getNameRetriever(chatterid);
        }
        updateFragmentTitle(nameRetriever);
        onShowUser(chatterid);
    }

    protected void setShowChatterTitle(boolean flag)
    {
        showChatterTitle = flag;
    }
}
