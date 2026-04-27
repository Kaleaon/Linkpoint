package com.lumiyaviewer.lumiya.ui.login;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.text.method.SingleLineTransformationMethod;
import android.text.style.URLSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.GridConnectionService;
import com.lumiyaviewer.lumiya.LumiyaApp;
import com.lumiyaviewer.lumiya.R;
import com.lumiyaviewer.lumiya.eventbus.EventHandler;
import com.lumiyaviewer.lumiya.slproto.SLGridConnection;
import com.lumiyaviewer.lumiya.slproto.SLURL;
import com.lumiyaviewer.lumiya.slproto.auth.SLAuth;
import com.lumiyaviewer.lumiya.slproto.avatar.SLMoveEvents;
import com.lumiyaviewer.lumiya.slproto.events.SLLoginResultEvent;
import com.lumiyaviewer.lumiya.slproto.events.SLReconnectingEvent;
import com.lumiyaviewer.lumiya.ui.accounts.AccountList;
import com.lumiyaviewer.lumiya.ui.accounts.ManageAccountsActivity;
import com.lumiyaviewer.lumiya.ui.chat.ChatNewActivity;
import com.lumiyaviewer.lumiya.ui.common.ThemedActivity;
import com.lumiyaviewer.lumiya.ui.grids.GridEditDialog;
import com.lumiyaviewer.lumiya.ui.grids.GridList;
import com.lumiyaviewer.lumiya.ui.grids.ManageGridsActivity;
import com.lumiyaviewer.lumiya.ui.settings.SettingsActivity;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class LoginActivity extends ThemedActivity implements View.OnClickListener, TextWatcher, GridEditDialog.OnGridEditResultListener {
    private static final String KEY_CLIENT_ID = "client_id";
    private static final String KEY_LOGIN = "login";
    private static final String KEY_PASSWORD = "password";
    private static final String KEY_SAVE_PASSWORD = "save_password";
    private static final String KEY_SELECTED_GRID = "selected_grid";
    private static final String KEY_TOS_ACCEPTED = "tos_accepted";
    private AccountList accountList = null;
    private boolean enableAutoClear = false;
    private GridList.GridArrayAdapter gridDisplayAdapter = null;
    private List<GridList.GridInfo> gridDisplayList = new ArrayList();
    /* access modifiers changed from: private */
    public GridList gridList = null;
    /* access modifiers changed from: private */
    public int lastSelectedGrid = 0;
    /* access modifiers changed from: private */
    public UUID lastSelectedGridUUID;
    private boolean loggingIn = false;
    private ImmutableList<MenuItem> menuItems = ImmutableList.of();

    private void CheckTOSAndLogin() {
        View currentFocus = getCurrentFocus();
        if (currentFocus != null) {
            ((InputMethodManager) getSystemService("input_method")).hideSoftInputFromWindow(currentFocus.getWindowToken(), 0);
        }
        SharedPreferences preferences = getPreferences(0);
        GridList.GridInfo selectedGrid = getSelectedGrid();
        if (preferences.getBoolean(KEY_TOS_ACCEPTED, false) || (!selectedGrid.isLindenGrid())) {
            DoLogin();
        } else {
            startActivityForResult(new Intent(this, TOSActivity.class), 5);
        }
    }

    private void DoLogin() {
        boolean z;
        String str;
        String str2;
        SLURL slurl;
        SharedPreferences preferences = getPreferences(0);
        String editable = ((EditText) findViewById(R.id.editUserName)).getText().toString();
        String editable2 = ((EditText) findViewById(R.id.editPassword)).getText().toString();
        GridList.GridInfo selectedGrid = getSelectedGrid();
        boolean isChecked = ((CheckBox) findViewById(R.id.savePassword)).isChecked();
        String str3 = "";
        if (editable2.equals(getString(R.string.saved_password))) {
            str3 = preferences.getString(KEY_PASSWORD, "");
            z = true;
        } else {
            z = false;
        }
        if (!z) {
            String passwordHash = SLAuth.getPasswordHash(editable2);
            Debug.Log("Login: not using saved hash, password = " + editable2 + ", new hash: " + passwordHash);
            str = passwordHash;
        } else {
            AccountList.AccountInfo findAccount = this.accountList.findAccount(editable, selectedGrid.getGridUUID());
            if (findAccount != null && !findAccount.getPasswordHash().equals("")) {
                str3 = findAccount.getPasswordHash();
            }
            Debug.Log("Login: using saved hash, hash = " + str3);
            str = str3;
        }
        this.enableAutoClear = false;
        if (isChecked) {
            ((EditText) findViewById(R.id.editPassword)).setTransformationMethod(SingleLineTransformationMethod.getInstance());
            ((EditText) findViewById(R.id.editPassword)).setText(R.string.saved_password);
        } else {
            ((EditText) findViewById(R.id.editPassword)).setTransformationMethod(PasswordTransformationMethod.getInstance());
            ((EditText) findViewById(R.id.editPassword)).setText("");
        }
        this.enableAutoClear = true;
        String string = preferences.getString(KEY_CLIENT_ID, "");
        String string2 = PreferenceManager.getDefaultSharedPreferences(getBaseContext()).getString("start_location", "last");
        boolean saveUserName = getSaveUserName();
        boolean z2 = isChecked ? saveUserName : false;
        SharedPreferences.Editor edit = preferences.edit();
        edit.putString(KEY_LOGIN, saveUserName ? editable : "");
        edit.putBoolean(KEY_SAVE_PASSWORD, isChecked);
        if (!z || (!z2)) {
            edit.putString(KEY_PASSWORD, z2 ? str : "");
        }
        if (string.equals("")) {
            str2 = UUID.randomUUID().toString();
            edit.putString(KEY_CLIENT_ID, str2);
        } else {
            str2 = string;
        }
        edit.putString(KEY_SELECTED_GRID, selectedGrid.getGridUUID().toString());
        edit.apply();
        if (saveUserName) {
            this.accountList.findOrAddAccount(editable, z2 ? str : "", selectedGrid.getGridUUID());
        }
        try {
            slurl = new SLURL(getIntent());
        } catch (Exception e) {
            slurl = null;
        }
        String loginStartLocation = slurl != null ? slurl.getLoginStartLocation() : string2;
        Debug.Log("Start location (LoginActivity): " + loginStartLocation);
        this.loggingIn = true;
        Intent intent = new Intent(this, GridConnectionService.class);
        intent.setAction(GridConnectionService.LOGIN_ACTION);
        intent.putExtra(KEY_LOGIN, editable);
        intent.putExtra(KEY_PASSWORD, str);
        intent.putExtra(KEY_CLIENT_ID, str2);
        intent.putExtra("start_location", loginStartLocation);
        intent.putExtra("login_url", selectedGrid.getLoginURL());
        intent.putExtra("grid_name", selectedGrid.getGridName());
        startService(intent);
        showProgressView(true);
        ((TextView) findViewById(R.id.connect_status_text)).setText(R.string.status_logging_in);
    }

    private void checkIfGridAvailable() {
        Debug.Log("LoginActivity: checking if grid is available");
        SLGridConnection gridConnection = GridConnectionService.getGridConnection();
        if (gridConnection != null) {
            SLGridConnection.ConnectionState connectionState = gridConnection.getConnectionState();
            UUID activeAgentUUID = gridConnection.getActiveAgentUUID();
            Debug.Log("LoginActivity: connectionState = " + connectionState.toString());
            if (connectionState == SLGridConnection.ConnectionState.Connected && activeAgentUUID != null) {
                Debug.Log("LoginActivity: grid available and connected");
                startChatActivity(activeAgentUUID);
                finish();
                return;
            }
        }
        updateConnectingStatus();
    }

    private boolean getSaveUserName() {
        return !PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getBoolean("noSaveUserName", false);
    }

    private GridList.GridInfo getSelectedGrid() {
        Object selectedItem = ((Spinner) findViewById(R.id.spinnerGrid)).getSelectedItem();
        return selectedItem instanceof GridList.GridInfo ? (GridList.GridInfo) selectedItem : this.gridList.getDefaultGrid();
    }

    private void loadSavedLogin() {
        SharedPreferences preferences = getPreferences(0);
        if (getSaveUserName()) {
            String string = preferences.getString(KEY_PASSWORD, "");
            ((EditText) findViewById(R.id.editUserName)).setText(preferences.getString(KEY_LOGIN, ""));
            ((CheckBox) findViewById(R.id.savePassword)).setChecked(preferences.getBoolean(KEY_SAVE_PASSWORD, true));
            if (!string.equals("")) {
                ((EditText) findViewById(R.id.editPassword)).setTransformationMethod(SingleLineTransformationMethod.getInstance());
                ((EditText) findViewById(R.id.editPassword)).setText(R.string.saved_password);
            } else {
                ((EditText) findViewById(R.id.editPassword)).setTransformationMethod(PasswordTransformationMethod.getInstance());
                ((EditText) findViewById(R.id.editPassword)).setText("");
            }
        } else {
            ((EditText) findViewById(R.id.editUserName)).setText("");
            ((EditText) findViewById(R.id.editPassword)).setTransformationMethod(PasswordTransformationMethod.getInstance());
            ((EditText) findViewById(R.id.editPassword)).setText("");
        }
        this.enableAutoClear = true;
    }

    private boolean progressViewVisible() {
        View findViewById = findViewById(R.id.login_progress_layout);
        return findViewById != null && findViewById.getVisibility() == 0;
    }

    private void setSelectedGrid() {
        try {
            String string = getPreferences(0).getString(KEY_SELECTED_GRID, "");
            if (!string.equals("")) {
                int gridIndex = this.gridList.getGridIndex(UUID.fromString(string));
                ((Spinner) findViewById(R.id.spinnerGrid)).setSelection(gridIndex);
                this.lastSelectedGrid = gridIndex;
                Object selectedItem = ((Spinner) findViewById(R.id.spinnerGrid)).getSelectedItem();
                if (selectedItem instanceof GridList.GridInfo) {
                    this.lastSelectedGridUUID = ((GridList.GridInfo) selectedItem).getGridUUID();
                }
            }
        } catch (Exception e) {
        }
    }

    private void showProgressView(boolean z) {
        int i = 8;
        View findViewById = findViewById(R.id.login_progress_layout);
        View findViewById2 = findViewById(R.id.login_root_view);
        if (!(findViewById == null || findViewById2 == null)) {
            findViewById(R.id.login_progress_layout).setVisibility(z ? 0 : 8);
            View findViewById3 = findViewById(R.id.login_root_view);
            if (!z) {
                i = 0;
            }
            findViewById3.setVisibility(i);
        }
        updateMenuItems();
    }

    private void startChatActivity(UUID uuid) {
        Intent intent = new Intent(this, ChatNewActivity.class);
        intent.addFlags(SLMoveEvents.AGENT_CONTROL_TURN_RIGHT);
        intent.putExtra("activeAgentUUID", uuid.toString());
        startActivity(intent);
    }

    private void updateConnectingStatus() {
        SLGridConnection gridConnection;
        boolean z = this.loggingIn;
        if (!z && (gridConnection = GridConnectionService.getGridConnection()) != null && gridConnection.getConnectionState() == SLGridConnection.ConnectionState.Connecting) {
            showProgressView(true);
            if (gridConnection.getIsReconnecting()) {
                ((TextView) findViewById(R.id.connect_status_text)).setText(getString(R.string.status_reconnecting, new Object[]{Integer.valueOf(gridConnection.getReconnectAttempt())}));
                z = true;
            } else {
                ((TextView) findViewById(R.id.connect_status_text)).setText(R.string.status_logging_in);
                z = true;
            }
        }
        if (!z) {
            showProgressView(false);
        }
    }

    private void updateMenuItems() {
        boolean z = !progressViewVisible();
        for (MenuItem visible : this.menuItems) {
            visible.setVisible(z);
        }
    }

    public void afterTextChanged(Editable editable) {
    }

    public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        if (this.enableAutoClear) {
            EditText editText = (EditText) findViewById(R.id.editPassword);
            if (editText.getText().toString().equals(getString(R.string.saved_password))) {
                this.enableAutoClear = false;
                editText.setText("");
                editText.setTransformationMethod(PasswordTransformationMethod.getInstance());
                this.enableAutoClear = true;
            }
        }
    }

    public SharedPreferences getPreferences(int i) {
        return getSharedPreferences("LoginActivity", i);
    }

    @EventHandler
    public void handleLoginResult(SLLoginResultEvent sLLoginResultEvent) {
        this.loggingIn = false;
        Debug.Printf("LoginProgressActivity: result.success = %b", Boolean.valueOf(sLLoginResultEvent.success));
        if (sLLoginResultEvent.success) {
            startChatActivity(sLLoginResultEvent.activeAgentUUID);
            finish();
            return;
        }
        if (!isFinishing() && progressViewVisible()) {
            String str = "Login to Second Life has failed.";
            if (!Strings.isNullOrEmpty(sLLoginResultEvent.message)) {
                str = sLLoginResultEvent.message;
            }
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle((CharSequence) "Login failed");
            builder.setMessage((CharSequence) str);
            builder.setCancelable(true);
            builder.create().show();
        }
        showProgressView(false);
    }

    @EventHandler
    public void handleReconnectingEvent(SLReconnectingEvent sLReconnectingEvent) {
        updateConnectingStatus();
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$-com_lumiyaviewer_lumiya_ui_login_LoginActivity_5985  reason: not valid java name */
    public /* synthetic */ void m636lambda$com_lumiyaviewer_lumiya_ui_login_LoginActivity_5985() {
        if (findViewById(R.id.login_spacer).getHeight() < 2 && findViewById(R.id.whatsnewText).getVisibility() != 8) {
            findViewById(R.id.whatsnewText).setVisibility(8);
        }
    }

    /* access modifiers changed from: protected */
    public void onActivityResult(int i, int i2, Intent intent) {
        AccountList.AccountInfo accountInfo;
        Debug.Log("LoginActivity: onActivityResult: requestCode = " + i + ", resultCode = " + i2);
        if (intent != null) {
            Debug.Log("LoginActivity: onActivityResult: data = " + intent.getDataString() + ", " + intent.toString());
        } else {
            Debug.Log("LoginActivity: onActivityResult: data = null");
        }
        switch (i) {
            case 3:
                if (i2 == -1 && intent != null && (accountInfo = (AccountList.AccountInfo) intent.getParcelableExtra("selected_account")) != null) {
                    String passwordHash = accountInfo.getPasswordHash();
                    ((EditText) findViewById(R.id.editUserName)).setText(accountInfo.getLoginName());
                    ((CheckBox) findViewById(R.id.savePassword)).setChecked(!passwordHash.equals(""));
                    this.enableAutoClear = false;
                    if (!passwordHash.equals("")) {
                        ((EditText) findViewById(R.id.editPassword)).setTransformationMethod(SingleLineTransformationMethod.getInstance());
                        ((EditText) findViewById(R.id.editPassword)).setText(R.string.saved_password);
                    } else {
                        ((EditText) findViewById(R.id.editPassword)).setTransformationMethod(PasswordTransformationMethod.getInstance());
                        ((EditText) findViewById(R.id.editPassword)).setText("");
                    }
                    this.enableAutoClear = true;
                    if (accountInfo.getGridUUID() != null) {
                        int gridIndex = this.gridList.getGridIndex(accountInfo.getGridUUID());
                        ((Spinner) findViewById(R.id.spinnerGrid)).setSelection(gridIndex);
                        this.lastSelectedGrid = gridIndex;
                        Object selectedItem = ((Spinner) findViewById(R.id.spinnerGrid)).getSelectedItem();
                        if (selectedItem instanceof GridList.GridInfo) {
                            this.lastSelectedGridUUID = ((GridList.GridInfo) selectedItem).getGridUUID();
                        }
                    }
                    SharedPreferences.Editor edit = getPreferences(0).edit();
                    edit.putString(KEY_LOGIN, accountInfo.getLoginName());
                    edit.putBoolean(KEY_SAVE_PASSWORD, !passwordHash.equals(""));
                    edit.putString(KEY_PASSWORD, passwordHash);
                    if (accountInfo.getGridUUID() != null) {
                        edit.putString(KEY_SELECTED_GRID, accountInfo.getGridUUID().toString());
                    }
                    edit.apply();
                    return;
                }
                return;
            case 5:
                if (i2 == -1) {
                    SharedPreferences.Editor edit2 = getPreferences(0).edit();
                    edit2.putBoolean(KEY_TOS_ACCEPTED, true);
                    edit2.apply();
                    DoLogin();
                    return;
                }
                return;
            default:
                return;
        }
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.whatsnewText:
                startActivity(new Intent(this, WhatsNewActivity.class));
                return;
            case R.id.buttonLogin:
                CheckTOSAndLogin();
                return;
            case R.id.loginCancelButton:
                this.loggingIn = false;
                SLGridConnection gridConnection = GridConnectionService.getGridConnection();
                if (gridConnection != null) {
                    gridConnection.CancelConnect();
                }
                showProgressView(false);
                return;
            default:
                return;
        }
    }

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        SLGridConnection gridConnection = GridConnectionService.getGridConnection();
        if (gridConnection != null) {
            SLGridConnection.ConnectionState connectionState = gridConnection.getConnectionState();
            UUID activeAgentUUID = gridConnection.getActiveAgentUUID();
            Debug.Log("LoginActivity: connectionState = " + connectionState.toString());
            if (connectionState == SLGridConnection.ConnectionState.Connected && activeAgentUUID != null) {
                startChatActivity(activeAgentUUID);
                finish();
                return;
            }
        }
        setContentView((int) R.layout.login);
        Debug.Log("LoginActivity: created.");
        this.gridList = new GridList(this);
        this.accountList = new AccountList(this);
        this.gridList.getGridList(this.gridDisplayList, true);
        this.enableAutoClear = false;
        findViewById(R.id.buttonLogin).setOnClickListener(this);
        ((EditText) findViewById(R.id.editPassword)).addTextChangedListener(this);
        loadSavedLogin();
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
        spannableStringBuilder.append(getString(R.string.whatsnew_caption, new Object[]{LumiyaApp.getAppVersion()}));
        spannableStringBuilder.setSpan(new URLSpan(""), 0, spannableStringBuilder.length(), 33);
        ((TextView) findViewById(R.id.whatsnewText)).setText(spannableStringBuilder, TextView.BufferType.SPANNABLE);
        findViewById(R.id.whatsnewText).setClickable(true);
        findViewById(R.id.whatsnewText).setOnClickListener(this);
        this.gridDisplayAdapter = new GridList.GridArrayAdapter(this, this.gridDisplayList);
        ((Spinner) findViewById(R.id.spinnerGrid)).setAdapter(this.gridDisplayAdapter);
        setSelectedGrid();
        ((Spinner) findViewById(R.id.spinnerGrid)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            /* JADX WARNING: type inference failed for: r5v0, types: [android.widget.AdapterView<?>, android.widget.AdapterView] */
            /* JADX WARNING: Unknown variable types count: 1 */
            /* Code decompiled incorrectly, please refer to instructions dump. */
            public void onItemSelected(android.widget.AdapterView<?> r5, android.view.View r6, int r7, long r8) {
                /*
                    r4 = this;
                    r3 = 0
                    com.lumiyaviewer.lumiya.ui.login.LoginActivity r0 = com.lumiyaviewer.lumiya.ui.login.LoginActivity.this
                    int r0 = r0.lastSelectedGrid
                    if (r7 == r0) goto L_0x0032
                    android.widget.Adapter r0 = r5.getAdapter()
                    java.lang.Object r0 = r0.getItem(r7)
                    boolean r1 = r0 instanceof com.lumiyaviewer.lumiya.ui.grids.GridList.GridInfo
                    if (r1 == 0) goto L_0x0032
                    com.lumiyaviewer.lumiya.ui.grids.GridList$GridInfo r0 = (com.lumiyaviewer.lumiya.ui.grids.GridList.GridInfo) r0
                    java.lang.String r1 = r0.getLoginURL()
                    if (r1 != 0) goto L_0x0033
                    com.lumiyaviewer.lumiya.ui.grids.GridEditDialog r0 = new com.lumiyaviewer.lumiya.ui.grids.GridEditDialog
                    com.lumiyaviewer.lumiya.ui.login.LoginActivity r1 = com.lumiyaviewer.lumiya.ui.login.LoginActivity.this
                    com.lumiyaviewer.lumiya.ui.login.LoginActivity r2 = com.lumiyaviewer.lumiya.ui.login.LoginActivity.this
                    com.lumiyaviewer.lumiya.ui.grids.GridList r2 = r2.gridList
                    r0.<init>(r1, r2, r3)
                    com.lumiyaviewer.lumiya.ui.login.LoginActivity r1 = com.lumiyaviewer.lumiya.ui.login.LoginActivity.this
                    r0.setOnGridEditResultListener(r1)
                    r0.show()
                L_0x0032:
                    return
                L_0x0033:
                    com.lumiyaviewer.lumiya.ui.login.LoginActivity r1 = com.lumiyaviewer.lumiya.ui.login.LoginActivity.this
                    int unused = r1.lastSelectedGrid = r7
                    com.lumiyaviewer.lumiya.ui.login.LoginActivity r1 = com.lumiyaviewer.lumiya.ui.login.LoginActivity.this
                    java.util.UUID r0 = r0.getGridUUID()
                    java.util.UUID unused = r1.lastSelectedGridUUID = r0
                    goto L_0x0032
                */
                throw new UnsupportedOperationException("Method not decompiled: com.lumiyaviewer.lumiya.ui.login.LoginActivity.AnonymousClass1.onItemSelected(android.widget.AdapterView, android.view.View, int, long):void");
            }

            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
        findViewById(R.id.whatsnewText).getViewTreeObserver().addOnGlobalLayoutListener(new $Lambda$U_ZFuxgsYW8weMauiDTqAtaKePI(this));
        checkIfGridAvailable();
        findViewById(R.id.loginCancelButton).setOnClickListener(this);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.login_menu, menu);
        ImmutableList.Builder builder = ImmutableList.builder();
        builder.add((Object) menu.findItem(R.id.item_manage_accounts));
        builder.add((Object) menu.findItem(R.id.item_settings));
        builder.add((Object) menu.findItem(R.id.item_manage_grids));
        builder.add((Object) menu.findItem(R.id.item_show_password));
        this.menuItems = builder.build();
        return true;
    }

    public void onGridAdded(GridList.GridInfo gridInfo, boolean z) {
        if (z) {
            this.gridList.addNewGrid(gridInfo);
        }
        this.gridList.getGridList(this.gridDisplayList, true);
        this.gridDisplayAdapter.notifyDataSetChanged();
        int count = ((Spinner) findViewById(R.id.spinnerGrid)).getAdapter().getCount();
        if (count > 1) {
            ((Spinner) findViewById(R.id.spinnerGrid)).setSelection(count - 2);
            this.lastSelectedGrid = count - 2;
            Object selectedItem = ((Spinner) findViewById(R.id.spinnerGrid)).getSelectedItem();
            if (selectedItem instanceof GridList.GridInfo) {
                this.lastSelectedGridUUID = ((GridList.GridInfo) selectedItem).getGridUUID();
            }
        }
    }

    public void onGridDeleted(GridList.GridInfo gridInfo) {
    }

    public void onGridEditCancelled() {
        ((Spinner) findViewById(R.id.spinnerGrid)).setSelection(this.lastSelectedGrid);
    }

    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.item_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
            case R.id.item_manage_accounts:
                startActivityForResult(new Intent(this, ManageAccountsActivity.class), 3);
                return true;
            case R.id.item_manage_grids:
                startActivity(new Intent(this, ManageGridsActivity.class));
                return true;
            case R.id.item_show_password:
                EditText editText = (EditText) findViewById(R.id.editPassword);
                editText.setTransformationMethod(SingleLineTransformationMethod.getInstance());
                editText.setInputType(145);
                return true;
            default:
                return super.onOptionsItemSelected(menuItem);
        }
    }

    /* access modifiers changed from: protected */
    public void onResume() {
        super.onResume();
        Debug.Printf("LoginActivity: Resumed.", new Object[0]);
        checkIfGridAvailable();
        this.gridList.loadGrids();
        this.gridList.getGridList(this.gridDisplayList, true);
        this.gridDisplayAdapter.notifyDataSetChanged();
        if (this.lastSelectedGridUUID != null) {
            ((Spinner) findViewById(R.id.spinnerGrid)).setSelection(this.gridList.getGridIndex(this.lastSelectedGridUUID));
        }
        this.accountList.loadAccounts();
        if (getSaveUserName()) {
            findViewById(R.id.savePassword).setEnabled(true);
            return;
        }
        ((EditText) findViewById(R.id.editUserName)).setText("");
        ((EditText) findViewById(R.id.editPassword)).setTransformationMethod(PasswordTransformationMethod.getInstance());
        ((EditText) findViewById(R.id.editPassword)).setText("");
        findViewById(R.id.savePassword).setEnabled(false);
        ((CheckBox) findViewById(R.id.savePassword)).setChecked(false);
    }

    /* access modifiers changed from: protected */
    public void onStart() {
        super.onStart();
        checkIfGridAvailable();
    }

    /* access modifiers changed from: protected */
    public void onStop() {
        super.onStop();
    }

    public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
    }
}
