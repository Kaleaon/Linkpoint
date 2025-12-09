package com.linkpoint.ui.common

import android.os.Bundle
import androidx.fragment.app.ActivityCompat
import androidx.fragment.app.FragmentActivity
import com.google.common.base.Objects
import com.linkpoint.Debug
import com.linkpoint.R
import com.linkpoint.react.UIThreadExecutor
import com.linkpoint.slproto.users.ChatterID
import com.linkpoint.slproto.users.ChatterNameRetriever
import com.linkpoint.slproto.users.manager.UserManager
import androidx.annotation.Nullable

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

    fun makeSelection(ChatterID chatterID2): Bundle {
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
    fun decorateFragmentTitle(String str): String {
        return str
    }

    fun onChatterNameUpdated(ChatterNameRetriever chatterNameRetriever): Unit {
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

    fun onStart(): Unit {
        super.onStart()
        setNewUser((ChatterID) getArguments().getParcelable(CHATTER_ID_KEY))
    }

    fun onStop(): Unit {
        setNewUser((ChatterID) null)
        super.onStop()
    }

    /* access modifiers changed from: package-private */
    fun setNewUser(@Nullable ChatterID chatterID2): Unit {
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
    fun setShowChatterTitle(Boolean z): Unit {
        this.showChatterTitle = z
    }
}
