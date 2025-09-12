package com.lumiyaviewer.lumiya.ui.accounts;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Parcel;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AccountList {
    private ArrayList<AccountInfo> accounts = new ArrayList<>();
    private Context context;

    public static class AccountInfo implements Parcelable {
        public static final Parcelable.Creator<AccountInfo> CREATOR = new Parcelable.Creator<AccountInfo>() {
            public AccountInfo createFromParcel(Parcel parcel) {
                return new AccountInfo(parcel, (AccountInfo) null);
            }

            public AccountInfo[] newArray(int i) {
                return new AccountInfo[i];
            }
        };
        private UUID GridUUID;
        private String LoginName;
        private String PasswordHash;

        public AccountInfo(SharedPreferences sharedPreferences, String str) {
            this.LoginName = sharedPreferences.getString(str + "_login_name", "");
            this.PasswordHash = sharedPreferences.getString(str + "_pwd_hash", "");
            this.GridUUID = UUID.fromString(sharedPreferences.getString(str + "_grid", ""));
        }

        private AccountInfo(Parcel parcel) {
            this.LoginName = parcel.readString();
            this.PasswordHash = parcel.readString();
            String readString = parcel.readString();
            if (!readString.equals("")) {
                this.GridUUID = UUID.fromString(readString);
            } else {
                this.GridUUID = null;
            }
        }

        /* synthetic */ AccountInfo(Parcel parcel, AccountInfo accountInfo) {
            this(parcel);
        }

        public AccountInfo(String str, String str2, UUID uuid) {
            this.LoginName = str;
            this.PasswordHash = str2;
            this.GridUUID = uuid;
        }

        public int describeContents() {
            return 0;
        }

        public UUID getGridUUID() {
            return this.GridUUID;
        }

        public String getLoginName() {
            return this.LoginName;
        }

        public String getPasswordHash() {
            return this.PasswordHash;
        }

        public void saveToPreferences(SharedPreferences.Editor editor, String str) {
            editor.putString(str + "_login_name", this.LoginName);
            editor.putString(str + "_pwd_hash", this.PasswordHash);
            editor.putString(str + "_grid", this.GridUUID.toString());
        }

        public void setGridUUID(UUID uuid) {
            this.GridUUID = uuid;
        }

        public void setLoginName(String str) {
            this.LoginName = str;
        }

        public void setPasswordHash(String str) {
            this.PasswordHash = str;
        }

        public void writeToParcel(Parcel parcel, int i) {
            parcel.writeString(this.LoginName);
            parcel.writeString(this.PasswordHash);
            if (this.GridUUID != null) {
                parcel.writeString(this.GridUUID.toString());
            } else {
                parcel.writeString("");
            }
        }
    }

    public AccountList(Context context2) {
        this.context = context2;
        loadAccounts();
    }

    public void addNewAccount(AccountInfo accountInfo) {
        this.accounts.add(accountInfo);
    }

    public void deleteAccount(AccountInfo accountInfo) {
        this.accounts.remove(accountInfo);
    }

    public AccountInfo findAccount(String str, UUID uuid) {
        for (AccountInfo accountInfo : this.accounts) {
            if (accountInfo.getLoginName().equals(str) && accountInfo.getGridUUID().equals(uuid)) {
                return accountInfo;
            }
        }
        return null;
    }

    public AccountInfo findOrAddAccount(String str, String str2, UUID uuid) {
        for (AccountInfo accountInfo : this.accounts) {
            if (accountInfo.getLoginName().equals(str) && accountInfo.getGridUUID().equals(uuid)) {
                accountInfo.setPasswordHash(str2);
                savePreferences();
                return accountInfo;
            }
        }
        AccountInfo accountInfo2 = new AccountInfo(str, str2, uuid);
        this.accounts.add(accountInfo2);
        savePreferences();
        return accountInfo2;
    }

    public List<AccountInfo> getAccountList() {
        return this.accounts;
    }

    public List<AccountInfo> getAccountList(List<AccountInfo> list) {
        list.clear();
        list.addAll(this.accounts);
        return list;
    }

    public void loadAccounts() {
        this.accounts.clear();
        SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this.context.getApplicationContext());
        int i = defaultSharedPreferences.getInt("accounts_count", 0);
        for (int i2 = 0; i2 < i; i2++) {
            this.accounts.add(new AccountInfo(defaultSharedPreferences, "account_" + i2));
        }
    }

    public void savePreferences() {
        SharedPreferences.Editor edit = PreferenceManager.getDefaultSharedPreferences(this.context.getApplicationContext()).edit();
        edit.putInt("accounts_count", this.accounts.size());
        int i = 0;
        while (true) {
            int i2 = i;
            if (i2 < this.accounts.size()) {
                this.accounts.get(i2).saveToPreferences(edit, "account_" + i2);
                i = i2 + 1;
            } else {
                edit.commit();
                return;
            }
        }
    }
}
