// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.ui.common;

import com.lumiyaviewer.lumiya.react.UIThreadExecutor;
import android.view.MenuItem;
import android.support.v4.view.ActionProvider;
import android.support.v4.view.MenuItemCompat;
import android.view.Menu;
import android.os.Bundle;
import android.content.res.Configuration;
import com.lumiyaviewer.lumiya.eventbus.EventBus;
import android.content.Context;
import android.content.Intent;
import com.lumiyaviewer.lumiya.ui.login.LoginActivity;
import android.app.Activity;
import android.support.v4.app.ActivityCompat;
import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.slproto.events.SLDisconnectEvent;
import com.lumiyaviewer.lumiya.eventbus.EventHandler;
import com.lumiyaviewer.lumiya.slproto.events.SLConnectionStateChangedEvent;
import android.widget.Button;
import android.widget.TextView;
import com.lumiyaviewer.lumiya.slproto.SLGridConnection;
import com.lumiyaviewer.lumiya.GridConnectionService;
import android.view.ViewGroup;
import com.lumiyaviewer.lumiya.ui.objpopup.SingleObjectPopupFragment;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import java.util.UUID;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.Fragment;
import com.lumiyaviewer.lumiya.ui.objpopup.ObjectPopupsFragment;
import android.view.inputmethod.InputMethodManager;
import com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatEvent;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import android.view.View$OnClickListener;
import android.support.annotation.Nullable;
import com.lumiyaviewer.lumiya.slproto.users.manager.ObjectPopupsManager;
import com.lumiyaviewer.lumiya.ui.objpopup.ObjectPopupsActionProvider;

public class ConnectedActivity extends ThemedActivity implements ObjectPopupsClickListener, ObjectPopupListener
{
    public static final String OBJECT_POPUP_NOTIFICATION = "objectPopupNotification";
    private NavDrawerActivityHelper navDrawerHelper;
    @Nullable
    private ObjectPopupsActionProvider objectPopupsActionProvider;
    private boolean objectPopupsDisplayed;
    private final View$OnClickListener reconnectButtonListener;
    private boolean singleObjectPopupsDisplayed;
    private boolean wantedShowObjectPopups;
    
    public ConnectedActivity() {
        this.objectPopupsDisplayed = false;
        this.singleObjectPopupsDisplayed = false;
        this.wantedShowObjectPopups = false;
        this.reconnectButtonListener = (View$OnClickListener)new _$Lambda$Zi2fvFRNZlQXFOmQ50cSiiV_3Qw(this);
    }
    
    private void displayObjectPopups() {
        final FragmentManager supportFragmentManager = this.getSupportFragmentManager();
        final UUID activeAgentID = ActivityUtils.getActiveAgentID(this.getIntent());
        if (activeAgentID != null) {
            final UserManager userManager = UserManager.getUserManager(activeAgentID);
            if (userManager != null) {
                userManager.getObjectPopupsManager().dismissDisplayedObjectPopup(null);
            }
            this.singleObjectPopupsDisplayed = false;
            this.objectPopupsDisplayed = true;
            final View currentFocus = this.getCurrentFocus();
            if (currentFocus != null) {
                currentFocus.clearFocus();
                ((InputMethodManager)this.getSystemService("input_method")).hideSoftInputFromWindow(currentFocus.getWindowToken(), 0);
            }
            final FragmentTransaction beginTransaction = supportFragmentManager.beginTransaction();
            beginTransaction.setTransition(4097);
            beginTransaction.replace(2131755655, ObjectPopupsFragment.create(activeAgentID));
            beginTransaction.commit();
        }
    }
    
    private void hideSingleObjectPopup() {
        if (this.singleObjectPopupsDisplayed) {
            this.singleObjectPopupsDisplayed = false;
            final FragmentManager supportFragmentManager = this.getSupportFragmentManager();
            final Fragment fragmentById = supportFragmentManager.findFragmentById(2131755655);
            if (fragmentById instanceof SingleObjectPopupFragment) {
                final FragmentTransaction beginTransaction = supportFragmentManager.beginTransaction();
                beginTransaction.setCustomAnimations(0, 2131034129);
                beginTransaction.remove(fragmentById);
                beginTransaction.commit();
            }
        }
    }
    
    private boolean removeObjectPopupsFragment() {
        if (this.objectPopupsDisplayed || this.singleObjectPopupsDisplayed) {
            this.objectPopupsDisplayed = false;
            this.singleObjectPopupsDisplayed = false;
            final FragmentManager supportFragmentManager = this.getSupportFragmentManager();
            final Fragment fragmentById = supportFragmentManager.findFragmentById(2131755655);
            if (fragmentById != null) {
                final FragmentTransaction beginTransaction = supportFragmentManager.beginTransaction();
                beginTransaction.setTransition(8194);
                beginTransaction.remove(fragmentById);
                beginTransaction.commit();
            }
            return true;
        }
        return false;
    }
    
    private void updateConnectionStatus() {
        if (this.handleConnectionEvents() && !this.isFinishing()) {
            final View viewById = this.findViewById(2131755586);
            if (viewById instanceof ViewGroup) {
                final SLGridConnection gridConnection = GridConnectionService.getGridConnection();
                final SLGridConnection.ConnectionState connectionState = gridConnection.getConnectionState();
                if (connectionState == SLGridConnection.ConnectionState.Connected) {
                    viewById.setVisibility(8);
                }
                else if (connectionState == SLGridConnection.ConnectionState.Connecting) {
                    viewById.setVisibility(0);
                    if (gridConnection.getIsReconnecting()) {
                        ((TextView)viewById.findViewById(2131755588)).setText((CharSequence)this.getString(2131296926, new Object[] { gridConnection.getReconnectAttempt() }));
                    }
                    else {
                        ((TextView)viewById.findViewById(2131755588)).setText(2131296459);
                    }
                    ((Button)viewById.findViewById(2131755589)).setText(2131296424);
                    viewById.findViewById(2131755587).setVisibility(0);
                }
                else if (connectionState == SLGridConnection.ConnectionState.Idle) {
                    viewById.setVisibility(0);
                    ((TextView)viewById.findViewById(2131755588)).setText(2131296514);
                    ((Button)viewById.findViewById(2131755589)).setText(2131296848);
                    viewById.findViewById(2131755587).setVisibility(8);
                }
            }
        }
    }
    
    public void dismissSingleObjectPopup() {
        this.hideSingleObjectPopup();
        final UserManager userManager = ActivityUtils.getUserManager(this.getIntent());
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
    public void handleConnectionStateChangedEvent(final SLConnectionStateChangedEvent slConnectionStateChangedEvent) {
        this.updateConnectionStatus();
    }
    
    @EventHandler
    public void handleDisconnectEvent(final SLDisconnectEvent slDisconnectEvent) {
        if (this.handleConnectionEvents()) {
            Debug.Printf("ConnectedActivity: disconnect event, normalDisconnect %b", slDisconnectEvent.normalDisconnect);
            if (slDisconnectEvent.normalDisconnect) {
                Debug.Printf("ConnectedActivity: starting login activity", new Object[0]);
                ActivityCompat.finishAffinity(this);
                this.startActivity(new Intent((Context)this, (Class)LoginActivity.class).setFlags(335577088));
            }
            else {
                this.updateConnectionStatus();
            }
        }
    }
    
    @Override
    public void onBackPressed() {
        if (this.navDrawerHelper.onBackPressed()) {
            return;
        }
        if (this.handleConnectionEvents() && this.removeObjectPopupsFragment()) {
            return;
        }
        if (!this.handleBackPressed()) {
            super.onBackPressed();
        }
    }
    
    @Override
    public void onConfigurationChanged(final Configuration configuration) {
        super.onConfigurationChanged(configuration);
        this.navDrawerHelper.onConfigurationChanged(configuration);
    }
    
    @Override
    protected void onCreate(@Nullable final Bundle bundle) {
        super.onCreate(bundle);
        if (this.handleConnectionEvents()) {
            if (bundle != null) {
                this.objectPopupsDisplayed = bundle.getBoolean("objectPopupsDisplayed");
                this.singleObjectPopupsDisplayed = bundle.getBoolean("singleObjectPopupsDisplayed");
                this.wantedShowObjectPopups = bundle.getBoolean("wantedShowObjectPopups");
            }
            else {
                this.wantedShowObjectPopups = this.getIntent().getBooleanExtra("objectPopupNotification", false);
            }
        }
    }
    
    public boolean onCreateOptionsMenu(final Menu menu) {
        Debug.Printf("ObjectPopup: createOptionsMenu", new Object[0]);
        if (this.handleConnectionEvents()) {
            this.getMenuInflater().inflate(2131886102, menu);
            final ActionProvider actionProvider = MenuItemCompat.getActionProvider(menu.findItem(2131755831));
            if (actionProvider instanceof ObjectPopupsActionProvider) {
                (this.objectPopupsActionProvider = (ObjectPopupsActionProvider)actionProvider).setObjectPopupsClickListener((ObjectPopupsActionProvider.ObjectPopupsClickListener)this);
                final UserManager userManager = ActivityUtils.getUserManager(this.getIntent());
                if (userManager != null && this.objectPopupsActionProvider != null) {
                    this.onObjectPopupCountChanged(userManager.getObjectPopupsManager().getObjectPopupCount());
                }
            }
            else {
                this.objectPopupsActionProvider = null;
            }
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }
    
    @Override
    protected void onNewIntent(final Intent intent) {
        super.onNewIntent(intent);
        if (this.handleConnectionEvents()) {
            if (intent.getBooleanExtra("objectPopupNotification", false)) {
                this.wantedShowObjectPopups = true;
            }
            else {
                final UserManager userManager = ActivityUtils.getUserManager(this.getIntent());
                if (userManager != null) {
                    userManager.getObjectPopupsManager().dismissDisplayedObjectPopup(null);
                }
                this.removeObjectPopupsFragment();
            }
        }
    }
    
    @Override
    public void onNewObjectPopup(final SLChatEvent slChatEvent) {
        if (this.findViewById(2131755655) != null) {
            final UUID activeAgentID = ActivityUtils.getActiveAgentID(this.getIntent());
            if (activeAgentID != null) {
                final FragmentManager supportFragmentManager = this.getSupportFragmentManager();
                if (this.objectPopupsDisplayed) {
                    final UserManager userManager = UserManager.getUserManager(activeAgentID);
                    if (userManager != null) {
                        userManager.getObjectPopupsManager().dismissDisplayedObjectPopup(slChatEvent);
                        return;
                    }
                }
                else if (this.singleObjectPopupsDisplayed && slChatEvent == null) {
                    this.singleObjectPopupsDisplayed = false;
                    final Fragment fragmentById = supportFragmentManager.findFragmentById(2131755655);
                    if (fragmentById != null) {
                        final FragmentTransaction beginTransaction = supportFragmentManager.beginTransaction();
                        beginTransaction.setTransition(8194);
                        beginTransaction.remove(fragmentById);
                        beginTransaction.commit();
                    }
                }
                if (slChatEvent != null) {
                    this.singleObjectPopupsDisplayed = true;
                    this.objectPopupsDisplayed = false;
                    final FragmentTransaction beginTransaction2 = supportFragmentManager.beginTransaction();
                    beginTransaction2.setTransition(4097);
                    beginTransaction2.replace(2131755655, SingleObjectPopupFragment.create(activeAgentID));
                    beginTransaction2.commit();
                }
            }
        }
    }
    
    @Override
    public void onObjectPopupCountChanged(final int objectPopupCount) {
        if (this.objectPopupsActionProvider != null) {
            this.objectPopupsActionProvider.setObjectPopupCount(objectPopupCount);
        }
        if (objectPopupCount == 0 && this.objectPopupsDisplayed) {
            this.objectPopupsDisplayed = false;
            final FragmentManager supportFragmentManager = this.getSupportFragmentManager();
            final Fragment fragmentById = supportFragmentManager.findFragmentById(2131755655);
            if (fragmentById instanceof ObjectPopupsFragment) {
                final FragmentTransaction beginTransaction = supportFragmentManager.beginTransaction();
                beginTransaction.setTransition(8194);
                beginTransaction.remove(fragmentById);
                beginTransaction.commit();
            }
        }
    }
    
    @Override
    public void onObjectPopupsClicked() {
        if (this.findViewById(2131755655) != null) {
            final FragmentManager supportFragmentManager = this.getSupportFragmentManager();
            if (this.objectPopupsDisplayed) {
                this.objectPopupsDisplayed = false;
                final Fragment fragmentById = supportFragmentManager.findFragmentById(2131755655);
                if (fragmentById != null) {
                    final FragmentTransaction beginTransaction = supportFragmentManager.beginTransaction();
                    beginTransaction.setTransition(8194);
                    beginTransaction.remove(fragmentById);
                    beginTransaction.commit();
                }
            }
            else {
                this.displayObjectPopups();
            }
        }
    }
    
    public boolean onOptionsItemSelected(final MenuItem menuItem) {
        return this.navDrawerHelper.onOptionsItemSelected(menuItem) || super.onOptionsItemSelected(menuItem);
    }
    
    @Override
    protected void onPause() {
        final UserManager userManager = ActivityUtils.getUserManager(this.getIntent());
        if (userManager != null && this.handleConnectionEvents()) {
            userManager.getObjectPopupsManager().removeObjectPopupListener((ObjectPopupsManager.ObjectPopupListener)this);
            userManager.getObjectPopupsManager().removePopupWatcher(this);
        }
        super.onPause();
    }
    
    @Override
    protected void onPostCreate(@Nullable final Bundle bundle) {
        super.onPostCreate(bundle);
        if (this.handleConnectionEvents()) {
            final View viewById = this.findViewById(2131755586);
            if (viewById instanceof ViewGroup) {
                viewById.findViewById(2131755589).setOnClickListener(this.reconnectButtonListener);
            }
        }
        (this.navDrawerHelper = new NavDrawerActivityHelper(this)).syncState();
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        final UserManager userManager = ActivityUtils.getUserManager(this.getIntent());
        if (userManager != null && this.handleConnectionEvents()) {
            final int objectPopupCount = userManager.getObjectPopupsManager().getObjectPopupCount();
            if (this.objectPopupsActionProvider != null) {
                this.onObjectPopupCountChanged(objectPopupCount);
            }
            userManager.getObjectPopupsManager().addPopupWatcher(this);
            userManager.getObjectPopupsManager().setObjectPopupListener((ObjectPopupsManager.ObjectPopupListener)this, UIThreadExecutor.getInstance());
            if (this.wantedShowObjectPopups) {
                this.wantedShowObjectPopups = false;
                if (objectPopupCount != 0 && (this.objectPopupsDisplayed ^ true)) {
                    this.displayObjectPopups();
                }
            }
        }
        this.updateConnectionStatus();
    }
    
    @Override
    protected void onSaveInstanceState(final Bundle bundle) {
        super.onSaveInstanceState(bundle);
        if (this.handleConnectionEvents()) {
            bundle.putBoolean("objectPopupsDisplayed", this.objectPopupsDisplayed);
            bundle.putBoolean("singleObjectPopupsDisplayed", this.singleObjectPopupsDisplayed);
            bundle.putBoolean("wantedShowObjectPopups", this.wantedShowObjectPopups);
        }
    }
}
