package com.linkpoint.ui.minimap

import android.app.Activity
import android.os.Bundle
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentTransaction
import android.support.v7.app.ActionBar
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import butterknife.BindView
import butterknife.ButterKnife
import com.linkpoint.R
import com.linkpoint.react.SubscriptionData
import com.linkpoint.react.SubscriptionSingleKey
import com.linkpoint.react.UIThreadExecutor
import com.linkpoint.slproto.users.ParcelData
import com.linkpoint.slproto.users.manager.CurrentLocationInfo
import com.linkpoint.slproto.users.manager.UserManager
import com.linkpoint.ui.common.ActivityUtils
import com.linkpoint.ui.common.ConnectedActivity
import java.util.UUID
import javax.annotation.Nullable

class MinimapActivity : ConnectedActivity() {
    private val SubscriptionData<SubscriptionSingleKey, CurrentLocationInfo> currentLocationInfo = SubscriptionData<>(UIThreadExecutor.getInstance(), $Lambda$HQUtmVzLYkemE78mCklVmVxMXms(this))
    @BindView(2131755653)
    ViewGroup detailsLayout
    @BindView(2131755654)
    FrameLayout selectorLayout
    @BindView(2131755652)
    LinearLayout splitMainLayout
    @BindView(2131755657)
    View splitObjectPopupsLeftSpacer

    /* access modifiers changed from: private */
    /* renamed from: onCurrentLocationInfo */
    fun m639com_lumiyaviewer_lumiya_ui_minimap_MinimapActivitymthref0(currentLocationInfo2: CurrentLocationInfo) {
        val str: String = null
        if (currentLocationInfo2 != null) {
            val parcelData: ParcelData = currentLocationInfo2.parcelData()
            if (parcelData != null) {
                str = parcelData.getName()
            }
            if (str == null) {
                str = getString(R.string.name_loading_title)
            }
            setActivityTitle(str, getString(R.string.nearby_users_format, Array<Any>{Integer.valueOf(currentLocationInfo2.nearbyUsers())}))
        }
    }

     private fun setActivityTitle(str: String, str2: String) {
        val supportActionBar: ActionBar = getSupportActionBar()
        if (supportActionBar != null) {
            supportActionBar.setTitle((CharSequence) str)
            supportActionBar.setSubtitle((CharSequence) str2)
        }
        setTitle(str)
    }

    /* access modifiers changed from: protected */
    override fun onCreate(bundle: Bundle) {
        super.onCreate(bundle)
        setContentView((Int) R.layout.split_two_panels)
        ButterKnife.bind((Activity) this)
        if (getResources().getConfiguration().orientation == 2) {
            this.splitMainLayout.setOrientation(0)
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) this.selectorLayout.getLayoutParams()
            layoutParams.width = -2
            layoutParams.height = -1
            layoutParams.weight = 0.0f
            this.selectorLayout.setLayoutParams(layoutParams)
            LinearLayout.LayoutParams layoutParams2 = (LinearLayout.LayoutParams) this.detailsLayout.getLayoutParams()
            layoutParams2.width = -1
            layoutParams2.height = -1
            layoutParams2.weight = 1.0f
            this.detailsLayout.setLayoutParams(layoutParams2)
        } else {
            this.splitMainLayout.setOrientation(1)
            LinearLayout.LayoutParams layoutParams3 = (LinearLayout.LayoutParams) this.selectorLayout.getLayoutParams()
            layoutParams3.width = -1
            layoutParams3.height = -2
            layoutParams3.weight = 0.0f
            this.selectorLayout.setLayoutParams(layoutParams3)
            LinearLayout.LayoutParams layoutParams4 = (LinearLayout.LayoutParams) this.detailsLayout.getLayoutParams()
            layoutParams4.width = -1
            layoutParams4.height = -1
            layoutParams4.weight = 1.0f
            this.detailsLayout.setLayoutParams(layoutParams4)
            this.splitObjectPopupsLeftSpacer.setVisibility(8)
        }
        val activeAgentID: UUID = ActivityUtils.getActiveAgentID(getIntent())
        val supportFragmentManager: FragmentManager = getSupportFragmentManager()
        if (supportFragmentManager == null || activeAgentID == null) {
            finish()
            return
        }
        val beginTransaction: FragmentTransaction = supportFragmentManager.beginTransaction()
        if (supportFragmentManager.findFragmentById(R.id.selector) == null) {
            beginTransaction.add((Int) R.id.selector, MinimapFragment.newInstance(activeAgentID))
        }
        if (supportFragmentManager.findFragmentById(R.id.details) == null) {
            beginTransaction.add((Int) R.id.details, NearbyPeopleMinimapFragment.newInstance(activeAgentID))
        }
        beginTransaction.commit()
    }

    /* access modifiers changed from: protected */
    override fun onStart() {
        super.onStart()
        val userManager: UserManager = ActivityUtils.getUserManager(getIntent())
        if (userManager != null) {
            this.currentLocationInfo.subscribe(userManager.getCurrentLocationInfo(), SubscriptionSingleKey.Value)
        } else {
            this.currentLocationInfo.unsubscribe()
        }
    }

    /* access modifiers changed from: protected */
    override fun onStop() {
        this.currentLocationInfo.unsubscribe()
        super.onStop()
    }
}
