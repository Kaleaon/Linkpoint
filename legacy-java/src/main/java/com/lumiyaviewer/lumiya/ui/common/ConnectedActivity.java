package com.lumiyaviewer.lumiya.ui.common;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ActionProvider;
import android.support.v4.view.MenuItemCompat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;
import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.GridConnectionService;
import com.lumiyaviewer.lumiya.R;
import com.lumiyaviewer.lumiya.eventbus.EventBus;
import com.lumiyaviewer.lumiya.eventbus.EventHandler;
import com.lumiyaviewer.lumiya.react.UIThreadExecutor;
import com.lumiyaviewer.lumiya.slproto.SLGridConnection;
import com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatEvent;
import com.lumiyaviewer.lumiya.slproto.events.SLConnectionStateChangedEvent;
import com.lumiyaviewer.lumiya.slproto.events.SLDisconnectEvent;
import com.lumiyaviewer.lumiya.slproto.users.manager.ObjectPopupsManager;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import com.lumiyaviewer.lumiya.ui.login.LoginActivity;
import com.lumiyaviewer.lumiya.ui.objpopup.ObjectPopupsActionProvider;
import com.lumiyaviewer.lumiya.ui.objpopup.ObjectPopupsFragment;
import com.lumiyaviewer.lumiya.ui.objpopup.SingleObjectPopupFragment;
import java.util.UUID;

/* loaded from: classes.dex */
public class ConnectedActivity extends ThemedActivity implements ObjectPopupsActionProvider.ObjectPopupsClickListener, ObjectPopupsManager.ObjectPopupListener {
    public static final String OBJECT_POPUP_NOTIFICATION = "objectPopupNotification";
    private NavDrawerActivityHelper navDrawerHelper;

    @Nullable
    private ObjectPopupsActionProvider objectPopupsActionProvider;
    private boolean objectPopupsDisplayed = false;
    private boolean singleObjectPopupsDisplayed = false;
    private boolean wantedShowObjectPopups = false;
    private final View.OnClickListener reconnectButtonListener = new View.OnClickListener() { // from class: com.lumiyaviewer.lumiya.ui.common.-$Lambda$Zi2fvFRNZlQXFOmQ50cSiiV_3Qw
        private final /* synthetic */ void $m$0(View view) {
            ((ConnectedActivity) this).m537lambda$com_lumiyaviewer_lumiya_ui_common_ConnectedActivity_3108(view);
        }

        @Override // android.view.View.OnClickListener
        public final void onClick(View view) {
            $m$0(view);
        }
    };

    private void displayObjectPopups() {
        FragmentManager supportFragmentManager = getSupportFragmentManager();
        UUID activeAgentID = ActivityUtils.getActiveAgentID(getIntent());
        if (activeAgentID != null) {
            UserManager userManager = UserManager.getUserManager(activeAgentID);
            if (userManager != null) {
                userManager.getObjectPopupsManager().dismissDisplayedObjectPopup(null);
            }
            this.singleObjectPopupsDisplayed = false;
            this.objectPopupsDisplayed = true;
            View currentFocus = getCurrentFocus();
            if (currentFocus != null) {
                currentFocus.clearFocus();
                ((InputMethodManager) getSystemService("input_method")).hideSoftInputFromWindow(currentFocus.getWindowToken(), 0);
            }
            FragmentTransaction beginTransaction = supportFragmentManager.beginTransaction();
            beginTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            beginTransaction.replace(R.id.object_popups_container, ObjectPopupsFragment.create(activeAgentID));
            beginTransaction.commit();
        }
    }

    private void hideSingleObjectPopup() {
        if (this.singleObjectPopupsDisplayed) {
            this.singleObjectPopupsDisplayed = false;
            FragmentManager supportFragmentManager = getSupportFragmentManager();
            Fragment findFragmentById = supportFragmentManager.findFragmentById(R.id.object_popups_container);
            if (findFragmentById instanceof SingleObjectPopupFragment) {
                FragmentTransaction beginTransaction = supportFragmentManager.beginTransaction();
                beginTransaction.setCustomAnimations(0, R.anim.slide_to_above);
                beginTransaction.remove(findFragmentById);
                beginTransaction.commit();
            }
        }
    }

    private boolean removeObjectPopupsFragment() {
        if (!this.objectPopupsDisplayed && !this.singleObjectPopupsDisplayed) {
            return false;
        }
        this.objectPopupsDisplayed = false;
        this.singleObjectPopupsDisplayed = false;
        FragmentManager supportFragmentManager = getSupportFragmentManager();
        Fragment findFragmentById = supportFragmentManager.findFragmentById(R.id.object_popups_container);
        if (findFragmentById == null) {
            return true;
        }
        FragmentTransaction beginTransaction = supportFragmentManager.beginTransaction();
        beginTransaction.setTransition(8194);
        beginTransaction.remove(findFragmentById);
        beginTransaction.commit();
        return true;
    }

    private void updateConnectionStatus() {
        if (!handleConnectionEvents() || isFinishing()) {
            return;
        }
        View findViewById = findViewById(R.id.offline_notify_status_layout);
        if (findViewById instanceof ViewGroup) {
            SLGridConnection gridConnection = GridConnectionService.getGridConnection();
            SLGridConnection.ConnectionState connectionState = gridConnection.getConnectionState();
            if (connectionState == SLGridConnection.ConnectionState.Connected) {
                findViewById.setVisibility(8);
                return;
            }
            if (connectionState != SLGridConnection.ConnectionState.Connecting) {
                if (connectionState == SLGridConnection.ConnectionState.Idle) {
                    findViewById.setVisibility(0);
                    ((TextView) findViewById.findViewById(R.id.offline_notify_message)).setText(R.string.disconnnected_message);
                    ((Button) findViewById.findViewById(R.id.offline_connect_button)).setText(R.string.offline_connect_button);
                    findViewById.findViewById(R.id.offline_notify_reconnect).setVisibility(8);
                    return;
                }
                return;
            }
            findViewById.setVisibility(0);
            if (gridConnection.getIsReconnecting()) {
                ((TextView) findViewById.findViewById(R.id.offline_notify_message)).setText(getString(R.string.reconnecting_offline_message, new Object[]{Integer.valueOf(gridConnection.getReconnectAttempt())}));
            } else {
                ((TextView) findViewById.findViewById(R.id.offline_notify_message)).setText(R.string.connecting_message);
            }
            ((Button) findViewById.findViewById(R.id.offline_connect_button)).setText(R.string.cancel);
            findViewById.findViewById(R.id.offline_notify_reconnect).setVisibility(0);
        }
    }

    public void dismissSingleObjectPopup() {
        hideSingleObjectPopup();
        UserManager userManager = ActivityUtils.getUserManager(getIntent());
        if (userManager != null) {
            userManager.getObjectPopupsManager().dismissDisplayedObjectPopup(null);
        }
    }

    protected boolean handleBackPressed() {
        return false;
    }

    protected boolean handleConnectionEvents() {
        return true;
    }

    @EventHandler
    public void handleConnectionStateChangedEvent(SLConnectionStateChangedEvent sLConnectionStateChangedEvent) {
        updateConnectionStatus();
    }

    @EventHandler
    public void handleDisconnectEvent(SLDisconnectEvent sLDisconnectEvent) {
        if (handleConnectionEvents()) {
            Debug.Printf("ConnectedActivity: disconnect event, normalDisconnect %b", Boolean.valueOf(sLDisconnectEvent.normalDisconnect));
            if (!sLDisconnectEvent.normalDisconnect) {
                updateConnectionStatus();
                return;
            }
            Debug.Printf("ConnectedActivity: starting login activity", new Object[0]);
            ActivityCompat.finishAffinity(this);
            startActivity(new Intent(this, (Class<?>) LoginActivity.class).setFlags(335577088));
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: lambda$-com_lumiyaviewer_lumiya_ui_common_ConnectedActivity_3108, reason: not valid java name */
    public /* synthetic */ void m537lambda$com_lumiyaviewer_lumiya_ui_common_ConnectedActivity_3108(View view) {
        SLGridConnection gridConnection = GridConnectionService.getGridConnection();
        SLGridConnection.ConnectionState connectionState = gridConnection.getConnectionState();
        if (connectionState == SLGridConnection.ConnectionState.Connecting) {
            gridConnection.Disconnect();
        } else if (connectionState == SLGridConnection.ConnectionState.Idle) {
            EventBus.getInstance().publish(new SLDisconnectEvent(true, null));
            ActivityCompat.finishAffinity(this);
            startActivity(new Intent(this, (Class<?>) LoginActivity.class).setFlags(335577088));
        }
    }

    @Override // android.support.v4.app.FragmentActivity, android.app.Activity
    public void onBackPressed() {
        if (this.navDrawerHelper.onBackPressed()) {
            return;
        }
        if ((handleConnectionEvents() && removeObjectPopupsFragment()) || handleBackPressed()) {
            return;
        }
        super.onBackPressed();
    }

    @Override // android.support.v7.app.AppCompatActivity, android.support.v4.app.FragmentActivity, android.app.Activity, android.content.ComponentCallbacks
    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        this.navDrawerHelper.onConfigurationChanged(configuration);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.lumiyaviewer.lumiya.ui.common.ThemedActivity, android.support.v7.app.AppCompatActivity, android.support.v4.app.FragmentActivity, android.support.v4.app.SupportActivity, android.app.Activity
    public void onCreate(@Nullable Bundle bundle) {
        super.onCreate(bundle);
        if (handleConnectionEvents()) {
            if (bundle == null) {
                this.wantedShowObjectPopups = getIntent().getBooleanExtra(OBJECT_POPUP_NOTIFICATION, false);
                return;
            }
            this.objectPopupsDisplayed = bundle.getBoolean("objectPopupsDisplayed");
            this.singleObjectPopupsDisplayed = bundle.getBoolean("singleObjectPopupsDisplayed");
            this.wantedShowObjectPopups = bundle.getBoolean("wantedShowObjectPopups");
        }
    }

    @Override // android.app.Activity
    public boolean onCreateOptionsMenu(Menu menu) {
        Debug.Printf("ObjectPopup: createOptionsMenu", new Object[0]);
        if (!handleConnectionEvents()) {
            return super.onCreateOptionsMenu(menu);
        }
        getMenuInflater().inflate(R.menu.object_popups_action_menu, menu);
        ActionProvider actionProvider = MenuItemCompat.getActionProvider(menu.findItem(R.id.item_object_popups));
        if (!(actionProvider instanceof ObjectPopupsActionProvider)) {
            this.objectPopupsActionProvider = null;
            return true;
        }
        this.objectPopupsActionProvider = (ObjectPopupsActionProvider) actionProvider;
        this.objectPopupsActionProvider.setObjectPopupsClickListener(this);
        UserManager userManager = ActivityUtils.getUserManager(getIntent());
        if (userManager == null || this.objectPopupsActionProvider == null) {
            return true;
        }
        onObjectPopupCountChanged(userManager.getObjectPopupsManager().getObjectPopupCount());
        return true;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.support.v4.app.FragmentActivity, android.app.Activity
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (handleConnectionEvents()) {
            if (intent.getBooleanExtra(OBJECT_POPUP_NOTIFICATION, false)) {
                this.wantedShowObjectPopups = true;
                return;
            }
            UserManager userManager = ActivityUtils.getUserManager(getIntent());
            if (userManager != null) {
                userManager.getObjectPopupsManager().dismissDisplayedObjectPopup(null);
            }
            removeObjectPopupsFragment();
        }
    }

    @Override // com.lumiyaviewer.lumiya.slproto.users.manager.ObjectPopupsManager.ObjectPopupListener
    public void onNewObjectPopup(SLChatEvent sLChatEvent) {
        UUID activeAgentID;
        if (findViewById(R.id.object_popups_container) == null || (activeAgentID = ActivityUtils.getActiveAgentID(getIntent())) == null) {
            return;
        }
        FragmentManager supportFragmentManager = getSupportFragmentManager();
        if (this.objectPopupsDisplayed) {
            UserManager userManager = UserManager.getUserManager(activeAgentID);
            if (userManager != null) {
                userManager.getObjectPopupsManager().dismissDisplayedObjectPopup(sLChatEvent);
                return;
            }
        } else if (this.singleObjectPopupsDisplayed && sLChatEvent == null) {
            this.singleObjectPopupsDisplayed = false;
            Fragment findFragmentById = supportFragmentManager.findFragmentById(R.id.object_popups_container);
            if (findFragmentById != null) {
                FragmentTransaction beginTransaction = supportFragmentManager.beginTransaction();
                beginTransaction.setTransition(8194);
                beginTransaction.remove(findFragmentById);
                beginTransaction.commit();
            }
        }
        if (sLChatEvent != null) {
            this.singleObjectPopupsDisplayed = true;
            this.objectPopupsDisplayed = false;
            FragmentTransaction beginTransaction2 = supportFragmentManager.beginTransaction();
            beginTransaction2.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            beginTransaction2.replace(R.id.object_popups_container, SingleObjectPopupFragment.create(activeAgentID));
            beginTransaction2.commit();
        }
    }

    @Override // com.lumiyaviewer.lumiya.slproto.users.manager.ObjectPopupsManager.ObjectPopupListener
    public void onObjectPopupCountChanged(int i) {
        if (this.objectPopupsActionProvider != null) {
            this.objectPopupsActionProvider.setObjectPopupCount(i);
        }
        if (i == 0 && this.objectPopupsDisplayed) {
            this.objectPopupsDisplayed = false;
            FragmentManager supportFragmentManager = getSupportFragmentManager();
            Fragment findFragmentById = supportFragmentManager.findFragmentById(R.id.object_popups_container);
            if (findFragmentById instanceof ObjectPopupsFragment) {
                FragmentTransaction beginTransaction = supportFragmentManager.beginTransaction();
                beginTransaction.setTransition(8194);
                beginTransaction.remove(findFragmentById);
                beginTransaction.commit();
            }
        }
    }

    @Override // com.lumiyaviewer.lumiya.ui.objpopup.ObjectPopupsActionProvider.ObjectPopupsClickListener
    public void onObjectPopupsClicked() {
        if (findViewById(R.id.object_popups_container) != null) {
            FragmentManager supportFragmentManager = getSupportFragmentManager();
            if (!this.objectPopupsDisplayed) {
                displayObjectPopups();
                return;
            }
            this.objectPopupsDisplayed = false;
            Fragment findFragmentById = supportFragmentManager.findFragmentById(R.id.object_popups_container);
            if (findFragmentById != null) {
                FragmentTransaction beginTransaction = supportFragmentManager.beginTransaction();
                beginTransaction.setTransition(8194);
                beginTransaction.remove(findFragmentById);
                beginTransaction.commit();
            }
        }
    }

    @Override // android.app.Activity
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (this.navDrawerHelper.onOptionsItemSelected(menuItem)) {
            return true;
        }
        return super.onOptionsItemSelected(menuItem);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.support.v4.app.FragmentActivity, android.app.Activity
    public void onPause() {
        UserManager userManager = ActivityUtils.getUserManager(getIntent());
        if (userManager != null && handleConnectionEvents()) {
            userManager.getObjectPopupsManager().removeObjectPopupListener(this);
            userManager.getObjectPopupsManager().removePopupWatcher(this);
        }
        super.onPause();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.support.v7.app.AppCompatActivity, android.app.Activity
    public void onPostCreate(@Nullable Bundle bundle) {
        super.onPostCreate(bundle);
        if (handleConnectionEvents()) {
            View findViewById = findViewById(R.id.offline_notify_status_layout);
            if (findViewById instanceof ViewGroup) {
                findViewById.findViewById(R.id.offline_connect_button).setOnClickListener(this.reconnectButtonListener);
            }
        }
        this.navDrawerHelper = new NavDrawerActivityHelper(this);
        this.navDrawerHelper.syncState();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.lumiyaviewer.lumiya.ui.common.ThemedActivity, android.support.v4.app.FragmentActivity, android.app.Activity
    public void onResume() {
        super.onResume();
        UserManager userManager = ActivityUtils.getUserManager(getIntent());
        if (userManager != null && handleConnectionEvents()) {
            int objectPopupCount = userManager.getObjectPopupsManager().getObjectPopupCount();
            if (this.objectPopupsActionProvider != null) {
                onObjectPopupCountChanged(objectPopupCount);
            }
            userManager.getObjectPopupsManager().addPopupWatcher(this);
            userManager.getObjectPopupsManager().setObjectPopupListener(this, UIThreadExecutor.getInstance());
            if (this.wantedShowObjectPopups) {
                this.wantedShowObjectPopups = false;
                if (objectPopupCount != 0 && (!this.objectPopupsDisplayed)) {
                    displayObjectPopups();
                }
            }
        }
        updateConnectionStatus();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.support.v7.app.AppCompatActivity, android.support.v4.app.FragmentActivity, android.support.v4.app.SupportActivity, android.app.Activity
    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        if (handleConnectionEvents()) {
            bundle.putBoolean("objectPopupsDisplayed", this.objectPopupsDisplayed);
            bundle.putBoolean("singleObjectPopupsDisplayed", this.singleObjectPopupsDisplayed);
            bundle.putBoolean("wantedShowObjectPopups", this.wantedShowObjectPopups);
        }
    }
}
