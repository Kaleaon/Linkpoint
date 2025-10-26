package com.linkpoint.ui.accounts

import android.content.Context
import android.content.SharedPreferences
import android.os.Parcel
import android.os.Parcelable
import android.preference.PreferenceManager
import java.util.ArrayList
import java.util.List
import java.util.UUID

class AccountList {
    private ArrayList<AccountInfo> accounts = ArrayList<>()
    private Context context

    @JvmStatic
    class AccountInfo : Parcelable {
        const val Parcelable.Creator<AccountInfo> CREATOR = Parcelable.Creator<AccountInfo>() {
             public fun createFromParcel(parcel: Parcel): AccountInfo {
                return AccountInfo(parcel, (AccountInfo) null)
            }

            public Array<AccountInfo> newArray(Int i) {
                return AccountInfo[i]
            }
        }
        private UUID GridUUID
        private String LoginName
        private String PasswordHash

        public AccountInfo(SharedPreferences sharedPreferences, String str) {
            this.LoginName = sharedPreferences.getString(str + "_login_name", "")
            this.PasswordHash = sharedPreferences.getString(str + "_pwd_hash", "")
            this.GridUUID = UUID.fromString(sharedPreferences.getString(str + "_grid", ""))
        }

        private AccountInfo(Parcel parcel) {
            this.LoginName = parcel.readString()
            this.PasswordHash = parcel.readString()
            val readString: String = parcel.readString()
            if (!readString.equals("")) {
                this.GridUUID = UUID.fromString(readString)
            } else {
                this.GridUUID = null
            }
        }

        /* synthetic */ AccountInfo(Parcel parcel, AccountInfo accountInfo) {
            this(parcel)
        }

        public AccountInfo(String str, String str2, UUID uuid) {
            this.LoginName = str
            this.PasswordHash = str2
            this.GridUUID = uuid
        }

         public fun describeContents(): Int {
            return 0
        }

         public fun getGridUUID(): UUID {
            return this.GridUUID
        }

         public fun getLoginName(): String {
            return this.LoginName
        }

         public fun getPasswordHash(): String {
            return this.PasswordHash
        }

        fun saveToPreferences(SharedPreferences.Editor editor, str: String) {
            editor.putString(str + "_login_name", this.LoginName)
            editor.putString(str + "_pwd_hash", this.PasswordHash)
            editor.putString(str + "_grid", this.GridUUID.toString())
        }

        fun setGridUUID(uuid: UUID) {
            this.GridUUID = uuid
        }

        fun setLoginName(str: String) {
            this.LoginName = str
        }

        fun setPasswordHash(str: String) {
            this.PasswordHash = str
        }

        fun writeToParcel(parcel: Parcel, i: Int) {
            parcel.writeString(this.LoginName)
            parcel.writeString(this.PasswordHash)
            if (this.GridUUID != null) {
                parcel.writeString(this.GridUUID.toString())
            } else {
                parcel.writeString("")
            }
        }
    }

    public AccountList(Context context2) {
        this.context = context2
        loadAccounts()
    }

    fun addNewAccount(accountInfo: AccountInfo) {
        this.accounts.add(accountInfo)
    }

    fun deleteAccount(accountInfo: AccountInfo) {
        this.accounts.remove(accountInfo)
    }

     public fun findAccount(str: String, uuid: UUID): AccountInfo {
        for (AccountInfo accountInfo : this.accounts) {
            if (accountInfo.getLoginName().equals(str) && accountInfo.getGridUUID().equals(uuid)) {
                return accountInfo
            }
        }
        return null
    }

     public fun findOrAddAccount(str: String, str2: String, uuid: UUID): AccountInfo {
        for (AccountInfo accountInfo : this.accounts) {
            if (accountInfo.getLoginName().equals(str) && accountInfo.getGridUUID().equals(uuid)) {
                accountInfo.setPasswordHash(str2)
                savePreferences()
                return accountInfo
            }
        }
        val accountInfo2: AccountInfo = AccountInfo(str, str2, uuid)
        this.accounts.add(accountInfo2)
        savePreferences()
        return accountInfo2
    }

    public List<AccountInfo> getAccountList() {
        return this.accounts
    }

    public List<AccountInfo> getAccountList(List<AccountInfo> list) {
        list.clear()
        list.addAll(this.accounts)
        return list
    }

    fun loadAccounts() {
        this.accounts.clear()
        val defaultSharedPreferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(this.context.getApplicationContext())
        val i: Int = defaultSharedPreferences.getInt("accounts_count", 0)
        for (Int i2 = 0; i2 < i; i2++) {
            this.accounts.add(AccountInfo(defaultSharedPreferences, "account_" + i2))
        }
    }

    fun savePreferences() {
        SharedPreferences.Editor edit = PreferenceManager.getDefaultSharedPreferences(this.context.getApplicationContext()).edit()
        edit.putInt("accounts_count", this.accounts.size())
        val i: Int = 0
        while (true) {
            val i2: Int = i
            if (i2 < this.accounts.size()) {
                this.accounts.get(i2).saveToPreferences(edit, "account_" + i2)
                i = i2 + 1
            } else {
                edit.commit()
                return
            }
        }
    }
}
