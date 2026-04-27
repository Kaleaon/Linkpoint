// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.ui.common;

import android.support.v4.app.FragmentActivity;
import android.app.Activity;
import android.support.v4.app.ActivityCompat;
import com.google.common.base.Objects;
import android.os.Parcelable;
import android.os.Bundle;
import com.lumiyaviewer.lumiya.react.UIThreadExecutor;
import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import javax.annotation.Nullable;
import com.lumiyaviewer.lumiya.slproto.users.ChatterID;
import com.lumiyaviewer.lumiya.slproto.users.ChatterNameRetriever;

public abstract class ChatterFragment extends FragmentWithTitle implements OnChatterNameUpdated
{
    public static final String CHATTER_ID_KEY = "chatterID";
    @Nullable
    protected ChatterID chatterID;
    protected ChatterNameRetriever nameRetriever;
    private boolean showChatterTitle;
    protected UserManager userManager;
    
    public ChatterFragment() {
        this.showChatterTitle = true;
    }
    
    private ChatterNameRetriever getNameRetriever(final ChatterID chatterID) {
        String string;
        if (chatterID != null) {
            string = chatterID.toString();
        }
        else {
            string = "null";
        }
        Debug.Printf("UserFunctionsFragment: ChatterNameRetriever: requesting for %s", string);
        ChatterNameRetriever chatterNameRetriever;
        if (chatterID != null) {
            chatterNameRetriever = new ChatterNameRetriever(chatterID, (ChatterNameRetriever.OnChatterNameUpdated)this, UIThreadExecutor.getInstance());
        }
        else {
            chatterNameRetriever = null;
        }
        return chatterNameRetriever;
    }
    
    public static Bundle makeSelection(final ChatterID chatterID) {
        final Bundle bundle = new Bundle();
        bundle.putParcelable("chatterID", (Parcelable)chatterID);
        return bundle;
    }
    
    private void updateFragmentTitle(final ChatterNameRetriever chatterNameRetriever) {
        Debug.Printf("updateTitle: updating fragment title: retriever = %s, showChatterTitle %b", chatterNameRetriever, this.showChatterTitle);
        if (this.showChatterTitle) {
            if (chatterNameRetriever == null) {
                this.setTitle(null, null);
            }
            else {
                final String resolvedName = chatterNameRetriever.getResolvedName();
                if (resolvedName != null) {
                    this.setTitle(this.decorateFragmentTitle(resolvedName), null);
                }
                else {
                    this.setTitle(this.getString(2131296712), null);
                }
            }
        }
    }
    
    protected String decorateFragmentTitle(final String s) {
        return s;
    }
    
    @Override
    public void onChatterNameUpdated(final ChatterNameRetriever chatterNameRetriever) {
        String string;
        if (this.chatterID != null) {
            string = this.chatterID.toString();
        }
        else {
            string = "null";
        }
        Debug.Printf("updateTitle: ChatterNameRetriever: retrieved for %s", string);
        if (this.chatterID != null && Objects.equal(chatterNameRetriever.chatterID, this.chatterID)) {
            Debug.Printf("UserFunctionsFragment: updating fragment title", new Object[0]);
            this.updateFragmentTitle(chatterNameRetriever);
            final FragmentActivity activity = this.getActivity();
            if (activity != null) {
                ActivityCompat.invalidateOptionsMenu(activity);
            }
        }
    }
    
    protected abstract void onShowUser(@Nullable final ChatterID p0);
    
    @Override
    public void onStart() {
        super.onStart();
        this.setNewUser((ChatterID)this.getArguments().getParcelable("chatterID"));
    }
    
    @Override
    public void onStop() {
        this.setNewUser(null);
        super.onStop();
    }
    
    void setNewUser(@Nullable final ChatterID chatterID) {
        UserManager userManager = null;
        this.chatterID = chatterID;
        if (chatterID != null) {
            userManager = chatterID.getUserManager();
        }
        this.userManager = userManager;
        if (this.nameRetriever != null) {
            if (!Objects.equal(this.nameRetriever.chatterID, chatterID)) {
                this.nameRetriever.dispose();
                this.nameRetriever = this.getNameRetriever(chatterID);
            }
        }
        else {
            this.nameRetriever = this.getNameRetriever(chatterID);
        }
        this.updateFragmentTitle(this.nameRetriever);
        this.onShowUser(chatterID);
    }
    
    protected void setShowChatterTitle(final boolean showChatterTitle) {
        this.showChatterTitle = showChatterTitle;
    }
}
