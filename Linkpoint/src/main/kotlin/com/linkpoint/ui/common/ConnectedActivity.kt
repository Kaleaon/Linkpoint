package com.linkpoint.ui.common

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.support.annotation.Nullable
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentTransaction
import android.support.v4.view.ActionProvider
import android.support.v4.view.MenuItemCompat
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.TextView
import com.linkpoint.Debug
import com.linkpoint.GridConnectionService
import com.linkpoint.R
import com.linkpoint.eventbus.EventBus
import com.linkpoint.eventbus.EventHandler
import com.linkpoint.react.UIThreadExecutor
import com.linkpoint.slproto.SLGridConnection
import com.linkpoint.slproto.chat.generic.SLChatEvent
import com.linkpoint.slproto.events.SLConnectionStateChangedEvent
import com.linkpoint.slproto.events.SLDisconnectEvent
import com.linkpoint.slproto.users.manager.ObjectPopupsManager
import com.linkpoint.slproto.users.manager.UserManager
import com.linkpoint.ui.login.LoginActivity
import com.linkpoint.ui.objpopup.ObjectPopupsActionProvider
import com.linkpoint.ui.objpopup.ObjectPopupsFragment
import com.linkpoint.ui.objpopup.SingleObjectPopupFragment
import java.util.UUID

class ConnectedActivity : ThemedActivity(), ObjectPopupsActionProvider.ObjectPopupsClickListener, ObjectPopupsManager.ObjectPopupListener {
    const val OBJECT_POPUP_NOTIFICATION: String = "objectPopupNotification"
    private NavDrawerActivityHelper navDrawerHelper
    private ObjectPopupsActionProvider objectPopupsActionProvider
    private Boolean objectPopupsDisplayed = false
    private val View.OnClickListener reconnectButtonListener = $Lambda$Zi2fvFRNZlQXFOmQ50cSiiV_3Qw(this)
    private Boolean singleObjectPopupsDisplayed = false
    private Boolean wantedShowObjectPopups = false

     private fun displayObjectPopups() {
        val supportFragmentManager: FragmentManager = getSupportFragmentManager()
        val activeAgentID: UUID = ActivityUtils.getActiveAgentID(getIntent())
        if (activeAgentID != null) {
            val userManager: UserManager = UserManager.getUserManager(activeAgentID)
            if (userManager != null) {
                userManager.getObjectPopupsManager().dismissDisplayedObjectPopup((SLChatEvent) null)
            }
            this.singleObjectPopupsDisplayed = false
            this.objectPopupsDisplayed = true
            val currentFocus: View = getCurrentFocus()
            if (currentFocus != null) {
                currentFocus.clearFocus()
                ((InputMethodManager) getSystemService("input_method")).hideSoftInputFromWindow(currentFocus.getWindowToken(), 0)
            }
            val beginTransaction: FragmentTransaction = supportFragmentManager.beginTransaction()
            beginTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            beginTransaction.replace(R.id.object_popups_container, ObjectPopupsFragment.create(activeAgentID))
            beginTransaction.commit()
        }
    }

     private fun hideSingleObjectPopup() {
        if (this.singleObjectPopupsDisplayed) {
            this.singleObjectPopupsDisplayed = false
            val supportFragmentManager: FragmentManager = getSupportFragmentManager()
            val findFragmentById: Fragment = supportFragmentManager.findFragmentById(R.id.object_popups_container)
            if (findFragmentById instanceof SingleObjectPopupFragment) {
                val beginTransaction: FragmentTransaction = supportFragmentManager.beginTransaction()
                beginTransaction.setCustomAnimations(0, R.anim.slide_to_above)
                beginTransaction.remove(findFragmentById)
                beginTransaction.commit()
            }
        }
    }

     private fun removeObjectPopupsFragment(): Boolean {
        if (!this.objectPopupsDisplayed && !this.singleObjectPopupsDisplayed) {
            return false
        }
        this.objectPopupsDisplayed = false
        this.singleObjectPopupsDisplayed = false
        val supportFragmentManager: FragmentManager = getSupportFragmentManager()
        val findFragmentById: Fragment = supportFragmentManager.findFragmentById(R.id.object_popups_container)
        if (findFragmentById == null) {
            return true
        }
        val beginTransaction: FragmentTransaction = supportFragmentManager.beginTransaction()
        beginTransaction.setTransition(8194)
        beginTransaction.remove(findFragmentById)
        beginTransaction.commit()
        return true
    }

     private fun updateConnectionStatus() {
        if (handleConnectionEvents() && !isFinishing()) {
            val findViewById: View = findViewById(R.id.offline_notify_status_layout)
            if (findViewById instanceof ViewGroup) {
                val gridConnection: SLGridConnection = GridConnectionService.getGridConnection()
                SLGridConnection.ConnectionState connectionState = gridConnection.getConnectionState()
                if (connectionState == SLGridConnection.ConnectionState.Connected) {
                    findViewById.setVisibility(8)
                } else if (connectionState == SLGridConnection.ConnectionState.Connecting) {
                    findViewById.setVisibility(0)
                    if (gridConnection.getIsReconnecting()) {
                        ((TextView) findViewById.findViewById(R.id.offline_notify_message)).setText(getString(R.string.reconnecting_offline_message, Array<Any>{Integer.valueOf(gridConnection.getReconnectAttempt())}))
                    } else {
                        ((TextView) findViewById.findViewById(R.id.offline_notify_message)).setText(R.string.connecting_message)
                    }
                    ((Button) findViewById.findViewById(R.id.offline_connect_button)).setText(R.string.cancel)
                    findViewById.findViewById(R.id.offline_notify_reconnect).setVisibility(0)
                } else if (connectionState == SLGridConnection.ConnectionState.Idle) {
                    findViewById.setVisibility(0)
                    ((TextView) findViewById.findViewById(R.id.offline_notify_message)).setText(R.string.disconnnected_message)
                    ((Button) findViewById.findViewById(R.id.offline_connect_button)).setText(R.string.offline_connect_button)
                    findViewById.findViewById(R.id.offline_notify_reconnect).setVisibility(8)
                }
            }
        }
    }

    fun dismissSingleObjectPopup() {
        hideSingleObjectPopup()
        val userManager: UserManager = ActivityUtils.getUserManager(getIntent())
        if (userManager != null) {
            userManager.getObjectPopupsManager().dismissDisplayedObjectPopup((SLChatEvent) null)
        }
    }

    /* access modifiers changed from: protected */
     public fun handleBackPressed(): Boolean {
        return false
    }

    /* access modifiers changed from: protected */
     public fun handleConnectionEvents(): Boolean {
        return true
    }

    @EventHandler
    fun handleConnectionStateChangedEvent(sLConnectionStateChangedEvent: SLConnectionStateChangedEvent) {
        updateConnectionStatus()
    }

    @EventHandler
    fun handleDisconnectEvent(sLDisconnectEvent: SLDisconnectEvent) {
        if (handleConnectionEvents()) {
            Debug.Printf("ConnectedActivity: disconnect event, normalDisconnect %b", Boolean.valueOf(sLDisconnectEvent.normalDisconnect))
            if (sLDisconnectEvent.normalDisconnect) {
                Debug.Printf("ConnectedActivity: starting login activity", Object[0])
                ActivityCompat.finishAffinity(this)
                startActivity(Intent(this, LoginActivity.class).setFlags(335577088))
                return
            }
            updateConnectionStatus()
        }
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$-com_lumiyaviewer_lumiya_ui_common_ConnectedActivity_3108  reason: not valid java name */
    public /* synthetic */ Unit m527lambda$com_lumiyaviewer_lumiya_ui_common_ConnectedActivity_3108(View view) {
        val gridConnection: SLGridConnection = GridConnectionService.getGridConnection()
        SLGridConnection.ConnectionState connectionState = gridConnection.getConnectionState()
        if (connectionState == SLGridConnection.ConnectionState.Connecting) {
            gridConnection.Disconnect()
        } else if (connectionState == SLGridConnection.ConnectionState.Idle) {
            EventBus.getInstance().publish(SLDisconnectEvent(true, (String) null))
            ActivityCompat.finishAffinity(this)
            startActivity(Intent(this, LoginActivity.class).setFlags(335577088))
        }
    }

    override fun onBackPressed() {
        if (!this.navDrawerHelper.onBackPressed()) {
            if ((!handleConnectionEvents() || !removeObjectPopupsFragment()) && !handleBackPressed()) {
                super.onBackPressed()
            }
        }
    }

    override fun onConfigurationChanged(configuration: Configuration) {
        super.onConfigurationChanged(configuration)
        this.navDrawerHelper.onConfigurationChanged(configuration)
    }

    /* access modifiers changed from: protected */
    override fun onCreate(bundle: Bundle) {
        super.onCreate(bundle)
        if (!handleConnectionEvents()) {
            return
        }
        if (bundle != null) {
            this.objectPopupsDisplayed = bundle.getBoolean("objectPopupsDisplayed")
            this.singleObjectPopupsDisplayed = bundle.getBoolean("singleObjectPopupsDisplayed")
            this.wantedShowObjectPopups = bundle.getBoolean("wantedShowObjectPopups")
            return
        }
        this.wantedShowObjectPopups = getIntent().getBooleanExtra(OBJECT_POPUP_NOTIFICATION, false)
    }

     public override fun onCreateOptionsMenu(menu: Menu): Boolean {
        Debug.Printf("ObjectPopup: createOptionsMenu", Object[0])
        if (!handleConnectionEvents()) {
            return super.onCreateOptionsMenu(menu)
        }
        getMenuInflater().inflate(R.menu.object_popups_action_menu, menu)
        val actionProvider: ActionProvider = MenuItemCompat.getActionProvider(menu.findItem(R.id.item_object_popups))
        if (actionProvider instanceof ObjectPopupsActionProvider) {
            this.objectPopupsActionProvider = (ObjectPopupsActionProvider) actionProvider
            this.objectPopupsActionProvider.setObjectPopupsClickListener(this)
            val userManager: UserManager = ActivityUtils.getUserManager(getIntent())
            if (userManager == null || this.objectPopupsActionProvider == null) {
                return true
            }
            onObjectPopupCountChanged(userManager.getObjectPopupsManager().getObjectPopupCount())
            return true
        }
        this.objectPopupsActionProvider = null
        return true
    }

    /* access modifiers changed from: protected */
    fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        if (!handleConnectionEvents()) {
            return
        }
        if (intent.getBooleanExtra(OBJECT_POPUP_NOTIFICATION, false)) {
            this.wantedShowObjectPopups = true
            return
        }
        val userManager: UserManager = ActivityUtils.getUserManager(getIntent())
        if (userManager != null) {
            userManager.getObjectPopupsManager().dismissDisplayedObjectPopup((SLChatEvent) null)
        }
        removeObjectPopupsFragment()
    }

    fun onNewObjectPopup(sLChatEvent: SLChatEvent) {
        UUID activeAgentID
        if (findViewById(R.id.object_popups_container) != null && (activeAgentID = ActivityUtils.getActiveAgentID(getIntent())) != null) {
            val supportFragmentManager: FragmentManager = getSupportFragmentManager()
            if (this.objectPopupsDisplayed) {
                val userManager: UserManager = UserManager.getUserManager(activeAgentID)
                if (userManager != null) {
                    userManager.getObjectPopupsManager().dismissDisplayedObjectPopup(sLChatEvent)
                    return
                }
            } else if (this.singleObjectPopupsDisplayed && sLChatEvent == null) {
                this.singleObjectPopupsDisplayed = false
                val findFragmentById: Fragment = supportFragmentManager.findFragmentById(R.id.object_popups_container)
                if (findFragmentById != null) {
                    val beginTransaction: FragmentTransaction = supportFragmentManager.beginTransaction()
                    beginTransaction.setTransition(8194)
                    beginTransaction.remove(findFragmentById)
                    beginTransaction.commit()
                }
            }
            if (sLChatEvent != null) {
                this.singleObjectPopupsDisplayed = true
                this.objectPopupsDisplayed = false
                val beginTransaction2: FragmentTransaction = supportFragmentManager.beginTransaction()
                beginTransaction2.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                beginTransaction2.replace(R.id.object_popups_container, SingleObjectPopupFragment.create(activeAgentID))
                beginTransaction2.commit()
            }
        }
    }

    fun onObjectPopupCountChanged(i: Int) {
        if (this.objectPopupsActionProvider != null) {
            this.objectPopupsActionProvider.setObjectPopupCount(i)
        }
        if (i == 0 && this.objectPopupsDisplayed) {
            this.objectPopupsDisplayed = false
            val supportFragmentManager: FragmentManager = getSupportFragmentManager()
            val findFragmentById: Fragment = supportFragmentManager.findFragmentById(R.id.object_popups_container)
            if (findFragmentById instanceof ObjectPopupsFragment) {
                val beginTransaction: FragmentTransaction = supportFragmentManager.beginTransaction()
                beginTransaction.setTransition(8194)
                beginTransaction.remove(findFragmentById)
                beginTransaction.commit()
            }
        }
    }

    fun onObjectPopupsClicked() {
        if (findViewById(R.id.object_popups_container) != null) {
            val supportFragmentManager: FragmentManager = getSupportFragmentManager()
            if (this.objectPopupsDisplayed) {
                this.objectPopupsDisplayed = false
                val findFragmentById: Fragment = supportFragmentManager.findFragmentById(R.id.object_popups_container)
                if (findFragmentById != null) {
                    val beginTransaction: FragmentTransaction = supportFragmentManager.beginTransaction()
                    beginTransaction.setTransition(8194)
                    beginTransaction.remove(findFragmentById)
                    beginTransaction.commit()
                    return
                }
                return
            }
            displayObjectPopups()
        }
    }

     public override fun onOptionsItemSelected(menuItem: MenuItem): Boolean {
        if (!this.navDrawerHelper.onOptionsItemSelected(menuItem)) {
            return super.onOptionsItemSelected(menuItem)
        }
        return true
    }

    /* access modifiers changed from: protected */
    override fun onPause() {
        val userManager: UserManager = ActivityUtils.getUserManager(getIntent())
        if (userManager != null && handleConnectionEvents()) {
            userManager.getObjectPopupsManager().removeObjectPopupListener(this)
            userManager.getObjectPopupsManager().removePopupWatcher(this)
        }
        super.onPause()
    }

    /* access modifiers changed from: protected */
    fun onPostCreate(bundle: Bundle) {
        super.onPostCreate(bundle)
        if (handleConnectionEvents()) {
            val findViewById: View = findViewById(R.id.offline_notify_status_layout)
            if (findViewById instanceof ViewGroup) {
                findViewById.findViewById(R.id.offline_connect_button).setOnClickListener(this.reconnectButtonListener)
            }
        }
        this.navDrawerHelper = NavDrawerActivityHelper(this)
        this.navDrawerHelper.syncState()
    }

    /* access modifiers changed from: protected */
    override fun onResume() {
        super.onResume()
        val userManager: UserManager = ActivityUtils.getUserManager(getIntent())
        if (userManager != null && handleConnectionEvents()) {
            val objectPopupCount: Int = userManager.getObjectPopupsManager().getObjectPopupCount()
            if (this.objectPopupsActionProvider != null) {
                onObjectPopupCountChanged(objectPopupCount)
            }
            userManager.getObjectPopupsManager().addPopupWatcher(this)
            userManager.getObjectPopupsManager().setObjectPopupListener(this, UIThreadExecutor.getInstance())
            if (this.wantedShowObjectPopups) {
                this.wantedShowObjectPopups = false
                if (objectPopupCount != 0 && (!this.objectPopupsDisplayed)) {
                    displayObjectPopups()
                }
            }
        }
        updateConnectionStatus()
    }

    /* access modifiers changed from: protected */
    override fun onSaveInstanceState(bundle: Bundle) {
        super.onSaveInstanceState(bundle)
        if (handleConnectionEvents()) {
            bundle.putBoolean("objectPopupsDisplayed", this.objectPopupsDisplayed)
            bundle.putBoolean("singleObjectPopupsDisplayed", this.singleObjectPopupsDisplayed)
            bundle.putBoolean("wantedShowObjectPopups", this.wantedShowObjectPopups)
        }
    }
}
