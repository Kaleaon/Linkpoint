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

    private Unit displayObjectPopups() {
        FragmentManager supportFragmentManager = getSupportFragmentManager()
        UUID activeAgentID = ActivityUtils.getActiveAgentID(getIntent())
        if (activeAgentID != null) {
            UserManager userManager = UserManager.getUserManager(activeAgentID)
            if (userManager != null) {
                userManager.getObjectPopupsManager().dismissDisplayedObjectPopup((SLChatEvent) null)
            }
            this.singleObjectPopupsDisplayed = false
            this.objectPopupsDisplayed = true
            View currentFocus = getCurrentFocus()
            if (currentFocus != null) {
                currentFocus.clearFocus()
                ((InputMethodManager) getSystemService("input_method")).hideSoftInputFromWindow(currentFocus.getWindowToken(), 0)
            }
            FragmentTransaction beginTransaction = supportFragmentManager.beginTransaction()
            beginTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            beginTransaction.replace(R.id.object_popups_container, ObjectPopupsFragment.create(activeAgentID))
            beginTransaction.commit()
        }
    }

    private Unit hideSingleObjectPopup() {
        if (this.singleObjectPopupsDisplayed) {
            this.singleObjectPopupsDisplayed = false
            FragmentManager supportFragmentManager = getSupportFragmentManager()
            Fragment findFragmentById = supportFragmentManager.findFragmentById(R.id.object_popups_container)
            if (findFragmentById instanceof SingleObjectPopupFragment) {
                FragmentTransaction beginTransaction = supportFragmentManager.beginTransaction()
                beginTransaction.setCustomAnimations(0, R.anim.slide_to_above)
                beginTransaction.remove(findFragmentById)
                beginTransaction.commit()
            }
        }
    }

    private Boolean removeObjectPopupsFragment() {
        if (!this.objectPopupsDisplayed && !this.singleObjectPopupsDisplayed) {
            return false
        }
        this.objectPopupsDisplayed = false
        this.singleObjectPopupsDisplayed = false
        FragmentManager supportFragmentManager = getSupportFragmentManager()
        Fragment findFragmentById = supportFragmentManager.findFragmentById(R.id.object_popups_container)
        if (findFragmentById == null) {
            return true
        }
        FragmentTransaction beginTransaction = supportFragmentManager.beginTransaction()
        beginTransaction.setTransition(8194)
        beginTransaction.remove(findFragmentById)
        beginTransaction.commit()
        return true
    }

    private Unit updateConnectionStatus() {
        if (handleConnectionEvents() && !isFinishing()) {
            View findViewById = findViewById(R.id.offline_notify_status_layout)
            if (findViewById instanceof ViewGroup) {
                SLGridConnection gridConnection = GridConnectionService.getGridConnection()
                SLGridConnection.ConnectionState connectionState = gridConnection.getConnectionState()
                if (connectionState == SLGridConnection.ConnectionState.Connected) {
                    findViewById.setVisibility(8)
                } else if (connectionState == SLGridConnection.ConnectionState.Connecting) {
                    findViewById.setVisibility(0)
                    if (gridConnection.getIsReconnecting()) {
                        ((TextView) findViewById.findViewById(R.id.offline_notify_message)).setText(getString(R.string.reconnecting_offline_message, Object[]{Integer.valueOf(gridConnection.getReconnectAttempt())}))
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
        UserManager userManager = ActivityUtils.getUserManager(getIntent())
        if (userManager != null) {
            userManager.getObjectPopupsManager().dismissDisplayedObjectPopup((SLChatEvent) null)
        }
    }

    /* access modifiers changed from: protected */
    public Boolean handleBackPressed() {
        return false
    }

    /* access modifiers changed from: protected */
    public Boolean handleConnectionEvents() {
        return true
    }

    @EventHandler
    fun handleConnectionStateChangedEvent(SLConnectionStateChangedEvent sLConnectionStateChangedEvent) {
        updateConnectionStatus()
    }

    @EventHandler
    fun handleDisconnectEvent(SLDisconnectEvent sLDisconnectEvent) {
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
        SLGridConnection gridConnection = GridConnectionService.getGridConnection()
        SLGridConnection.ConnectionState connectionState = gridConnection.getConnectionState()
        if (connectionState == SLGridConnection.ConnectionState.Connecting) {
            gridConnection.Disconnect()
        } else if (connectionState == SLGridConnection.ConnectionState.Idle) {
            EventBus.getInstance().publish(SLDisconnectEvent(true, (String) null))
            ActivityCompat.finishAffinity(this)
            startActivity(Intent(this, LoginActivity.class).setFlags(335577088))
        }
    }

    fun onBackPressed() {
        if (!this.navDrawerHelper.onBackPressed()) {
            if ((!handleConnectionEvents() || !removeObjectPopupsFragment()) && !handleBackPressed()) {
                super.onBackPressed()
            }
        }
    }

    fun onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration)
        this.navDrawerHelper.onConfigurationChanged(configuration)
    }

    /* access modifiers changed from: protected */
    fun onCreate(Bundle bundle) {
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

    public Boolean onCreateOptionsMenu(Menu menu) {
        Debug.Printf("ObjectPopup: createOptionsMenu", Object[0])
        if (!handleConnectionEvents()) {
            return super.onCreateOptionsMenu(menu)
        }
        getMenuInflater().inflate(R.menu.object_popups_action_menu, menu)
        ActionProvider actionProvider = MenuItemCompat.getActionProvider(menu.findItem(R.id.item_object_popups))
        if (actionProvider instanceof ObjectPopupsActionProvider) {
            this.objectPopupsActionProvider = (ObjectPopupsActionProvider) actionProvider
            this.objectPopupsActionProvider.setObjectPopupsClickListener(this)
            UserManager userManager = ActivityUtils.getUserManager(getIntent())
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
    fun onNewIntent(Intent intent) {
        super.onNewIntent(intent)
        if (!handleConnectionEvents()) {
            return
        }
        if (intent.getBooleanExtra(OBJECT_POPUP_NOTIFICATION, false)) {
            this.wantedShowObjectPopups = true
            return
        }
        UserManager userManager = ActivityUtils.getUserManager(getIntent())
        if (userManager != null) {
            userManager.getObjectPopupsManager().dismissDisplayedObjectPopup((SLChatEvent) null)
        }
        removeObjectPopupsFragment()
    }

    fun onNewObjectPopup(SLChatEvent sLChatEvent) {
        UUID activeAgentID
        if (findViewById(R.id.object_popups_container) != null && (activeAgentID = ActivityUtils.getActiveAgentID(getIntent())) != null) {
            FragmentManager supportFragmentManager = getSupportFragmentManager()
            if (this.objectPopupsDisplayed) {
                UserManager userManager = UserManager.getUserManager(activeAgentID)
                if (userManager != null) {
                    userManager.getObjectPopupsManager().dismissDisplayedObjectPopup(sLChatEvent)
                    return
                }
            } else if (this.singleObjectPopupsDisplayed && sLChatEvent == null) {
                this.singleObjectPopupsDisplayed = false
                Fragment findFragmentById = supportFragmentManager.findFragmentById(R.id.object_popups_container)
                if (findFragmentById != null) {
                    FragmentTransaction beginTransaction = supportFragmentManager.beginTransaction()
                    beginTransaction.setTransition(8194)
                    beginTransaction.remove(findFragmentById)
                    beginTransaction.commit()
                }
            }
            if (sLChatEvent != null) {
                this.singleObjectPopupsDisplayed = true
                this.objectPopupsDisplayed = false
                FragmentTransaction beginTransaction2 = supportFragmentManager.beginTransaction()
                beginTransaction2.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                beginTransaction2.replace(R.id.object_popups_container, SingleObjectPopupFragment.create(activeAgentID))
                beginTransaction2.commit()
            }
        }
    }

    fun onObjectPopupCountChanged(Int i) {
        if (this.objectPopupsActionProvider != null) {
            this.objectPopupsActionProvider.setObjectPopupCount(i)
        }
        if (i == 0 && this.objectPopupsDisplayed) {
            this.objectPopupsDisplayed = false
            FragmentManager supportFragmentManager = getSupportFragmentManager()
            Fragment findFragmentById = supportFragmentManager.findFragmentById(R.id.object_popups_container)
            if (findFragmentById instanceof ObjectPopupsFragment) {
                FragmentTransaction beginTransaction = supportFragmentManager.beginTransaction()
                beginTransaction.setTransition(8194)
                beginTransaction.remove(findFragmentById)
                beginTransaction.commit()
            }
        }
    }

    fun onObjectPopupsClicked() {
        if (findViewById(R.id.object_popups_container) != null) {
            FragmentManager supportFragmentManager = getSupportFragmentManager()
            if (this.objectPopupsDisplayed) {
                this.objectPopupsDisplayed = false
                Fragment findFragmentById = supportFragmentManager.findFragmentById(R.id.object_popups_container)
                if (findFragmentById != null) {
                    FragmentTransaction beginTransaction = supportFragmentManager.beginTransaction()
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

    public Boolean onOptionsItemSelected(MenuItem menuItem) {
        if (!this.navDrawerHelper.onOptionsItemSelected(menuItem)) {
            return super.onOptionsItemSelected(menuItem)
        }
        return true
    }

    /* access modifiers changed from: protected */
    fun onPause() {
        UserManager userManager = ActivityUtils.getUserManager(getIntent())
        if (userManager != null && handleConnectionEvents()) {
            userManager.getObjectPopupsManager().removeObjectPopupListener(this)
            userManager.getObjectPopupsManager().removePopupWatcher(this)
        }
        super.onPause()
    }

    /* access modifiers changed from: protected */
    fun onPostCreate(Bundle bundle) {
        super.onPostCreate(bundle)
        if (handleConnectionEvents()) {
            View findViewById = findViewById(R.id.offline_notify_status_layout)
            if (findViewById instanceof ViewGroup) {
                findViewById.findViewById(R.id.offline_connect_button).setOnClickListener(this.reconnectButtonListener)
            }
        }
        this.navDrawerHelper = NavDrawerActivityHelper(this)
        this.navDrawerHelper.syncState()
    }

    /* access modifiers changed from: protected */
    fun onResume() {
        super.onResume()
        UserManager userManager = ActivityUtils.getUserManager(getIntent())
        if (userManager != null && handleConnectionEvents()) {
            Int objectPopupCount = userManager.getObjectPopupsManager().getObjectPopupCount()
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
    fun onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle)
        if (handleConnectionEvents()) {
            bundle.putBoolean("objectPopupsDisplayed", this.objectPopupsDisplayed)
            bundle.putBoolean("singleObjectPopupsDisplayed", this.singleObjectPopupsDisplayed)
            bundle.putBoolean("wantedShowObjectPopups", this.wantedShowObjectPopups)
        }
    }
}
