package com.lumiyaviewer.lumiya.ui.common;

import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import com.google.common.base.Objects;
import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.R;
import com.lumiyaviewer.lumiya.react.UIThreadExecutor;
import com.lumiyaviewer.lumiya.slproto.users.ChatterID;
import com.lumiyaviewer.lumiya.slproto.users.ChatterNameRetriever;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import javax.annotation.Nullable;

/* loaded from: classes.dex */
public abstract class ChatterFragment extends FragmentWithTitle implements ChatterNameRetriever.OnChatterNameUpdated {
    public static final String CHATTER_ID_KEY = "chatterID";

    @Nullable
    protected ChatterID chatterID;
    protected ChatterNameRetriever nameRetriever;
    private boolean showChatterTitle = true;
    protected UserManager userManager;

    private ChatterNameRetriever getNameRetriever(ChatterID chatterID) {
        Object[] objArr = new Object[1];
        objArr[0] = chatterID != null ? chatterID.toString() : "null";
        Debug.Printf("UserFunctionsFragment: ChatterNameRetriever: requesting for %s", objArr);
        if (chatterID != null) {
            return new ChatterNameRetriever(chatterID, this, UIThreadExecutor.getInstance());
        }
        return null;
    }

    public static Bundle makeSelection(ChatterID chatterID) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(CHATTER_ID_KEY, chatterID);
        return bundle;
    }

    private void updateFragmentTitle(ChatterNameRetriever chatterNameRetriever) {
        Debug.Printf("updateTitle: updating fragment title: retriever = %s, showChatterTitle %b", chatterNameRetriever, Boolean.valueOf(this.showChatterTitle));
        if (this.showChatterTitle) {
            if (chatterNameRetriever == null) {
                setTitle(null, null);
                return;
            }
            String resolvedName = chatterNameRetriever.getResolvedName();
            if (resolvedName != null) {
                setTitle(decorateFragmentTitle(resolvedName), null);
            } else {
                setTitle(getString(R.string.name_loading_title), null);
            }
        }
    }

    protected String decorateFragmentTitle(String str) {
        return str;
    }

    public void onChatterNameUpdated(ChatterNameRetriever chatterNameRetriever) {
        Object[] objArr = new Object[1];
        objArr[0] = this.chatterID != null ? this.chatterID.toString() : "null";
        Debug.Printf("updateTitle: ChatterNameRetriever: retrieved for %s", objArr);
        if (this.chatterID == null || !Objects.equal(chatterNameRetriever.chatterID, this.chatterID)) {
            return;
        }
        Debug.Printf("UserFunctionsFragment: updating fragment title", new Object[0]);
        updateFragmentTitle(chatterNameRetriever);
        FragmentActivity activity = getActivity();
        if (activity != null) {
            ActivityCompat.invalidateOptionsMenu(activity);
        }
    }

    protected abstract void onShowUser(@Nullable ChatterID chatterID);

    @Override // com.lumiyaviewer.lumiya.ui.common.FragmentWithTitle, com.lumiyaviewer.lumiya.ui.common.StateAwareFragment, android.support.v4.app.Fragment
    public void onStart() {
        super.onStart();
        setNewUser((ChatterID) getArguments().getParcelable(CHATTER_ID_KEY));
    }

    @Override // com.lumiyaviewer.lumiya.ui.common.StateAwareFragment, android.support.v4.app.Fragment
    public void onStop() {
        setNewUser(null);
        super.onStop();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setNewUser(@Nullable ChatterID chatterID) {
        this.chatterID = chatterID;
        this.userManager = chatterID != null ? chatterID.getUserManager() : null;
        if (this.nameRetriever == null) {
            this.nameRetriever = getNameRetriever(chatterID);
        } else if (!Objects.equal(this.nameRetriever.chatterID, chatterID)) {
            this.nameRetriever.dispose();
            this.nameRetriever = getNameRetriever(chatterID);
        }
        updateFragmentTitle(this.nameRetriever);
        onShowUser(chatterID);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void setShowChatterTitle(boolean z) {
        this.showChatterTitle = z;
    }
}
