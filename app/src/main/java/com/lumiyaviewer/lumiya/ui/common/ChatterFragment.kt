package com.lumiyaviewer.lumiya.ui.common

import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.app.FragmentActivity
import com.google.common.base.Objects
import com.lumiyaviewer.lumiya.Debug
import com.lumiyaviewer.lumiya.R
import com.lumiyaviewer.lumiya.react.UIThreadExecutor
import com.lumiyaviewer.lumiya.slproto.users.ChatterID
import com.lumiyaviewer.lumiya.slproto.users.ChatterNameRetriever
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager
import javax.annotation.Nullable

abstract class ChatterFragment : FragmentWithTitle : ChatterNameRetriever.OnChatterNameUpdated {
    val CHATTER_ID_KEY: String = "chatterID"
    /* access modifiers changed from: protected */
    @Nullable
    ChatterID chatterID
    protected ChatterNameRetriever nameRetriever
    private Boolean showChatterTitle = true
    /* access modifiers changed from: protected */
    UserManager userManager

    private ChatterNameRetriever getNameRetriever(ChatterID chatterID2) {
        Any[] objArr = Any[1]
        objArr[0] = chatterID2 != null ? chatterID2.toString() : "null"
        Debug.Printf("UserFunctionsFragment: ChatterNameRetriever: requesting for %s", objArr)
        if (chatterID2 != null) {
            return ChatterNameRetriever(chatterID2, this, UIThreadExecutor.getInstance())
        }
        return null
    }

    Bundle makeSelection(ChatterID chatterID2) {
        Bundle bundle = Bundle()
        bundle.putParcelable(CHATTER_ID_KEY, chatterID2)
        return bundle
    }

    private Unit updateFragmentTitle(ChatterNameRetriever chatterNameRetriever) {
        Debug.Printf("updateTitle: updating fragment title: retriever = %s, showChatterTitle %b", chatterNameRetriever, Boolean.valueOf(this.showChatterTitle))
        if (!this.showChatterTitle) {
            return
        }
        if (chatterNameRetriever == null) {
            setTitle((String) null, (String) null)
            return
        }
        String resolvedName = chatterNameRetriever.getResolvedName()
        if (resolvedName != null) {
            setTitle(decorateFragmentTitle(resolvedName), (String) null)
        } else {
            setTitle(getString(R.string.name_loading_title), (String) null)
        }
    }

    /* access modifiers changed from: protected */
    String decorateFragmentTitle(String str) {
        return str
    }

    Unit onChatterNameUpdated(ChatterNameRetriever chatterNameRetriever) {
        Any[] objArr = Any[1]
        objArr[0] = this.chatterID != null ? this.chatterID.toString() : "null"
        Debug.Printf("updateTitle: ChatterNameRetriever: retrieved for %s", objArr)
        if (this.chatterID != null && Objects.equal(chatterNameRetriever.chatterID, this.chatterID)) {
            Debug.Printf("UserFunctionsFragment: updating fragment title", Any[0])
            updateFragmentTitle(chatterNameRetriever)
            FragmentActivity activity = getActivity()
            if (activity != null) {
                ActivityCompat.invalidateOptionsMenu(activity)
            }
        }
    }

    /* access modifiers changed from: protected */
    abstract Unit onShowUser(@Nullable ChatterID chatterID2)

    Unit onStart() {
        super.onStart()
        setNewUser((ChatterID) getArguments().getParcelable(CHATTER_ID_KEY))
    }

    Unit onStop() {
        setNewUser((ChatterID) null)
        super.onStop()
    }

    /* access modifiers changed from: package-private */
    Unit setNewUser(@Nullable ChatterID chatterID2) {
        UserManager userManager2 = null
        this.chatterID = chatterID2
        if (chatterID2 != null) {
            userManager2 = chatterID2.getUserManager()
        }
        this.userManager = userManager2
        if (this.nameRetriever == null) {
            this.nameRetriever = getNameRetriever(chatterID2)
        } else if (!Objects.equal(this.nameRetriever.chatterID, chatterID2)) {
            this.nameRetriever.dispose()
            this.nameRetriever = getNameRetriever(chatterID2)
        }
        updateFragmentTitle(this.nameRetriever)
        onShowUser(chatterID2)
    }

    /* access modifiers changed from: protected */
    Unit setShowChatterTitle(Boolean z) {
        this.showChatterTitle = z
    }
}
