package com.linkpoint.ui.accounts

import android.content.Context
import android.content.SharedPreferences
import android.os.Parcel
import android.os.Parcelable
import androidx.preference.PreferenceManager
import java.util.UUID

class AccountList(private val context: Context) {
    private val accounts = ArrayList<AccountInfo>()

    class AccountInfo : Parcelable {
        var LoginName: String
        var PasswordHash: String
        var GridUUID: UUID?

        constructor(sharedPreferences: SharedPreferences, str: String) {
            LoginName = sharedPreferences.getString("${str}_login_name", "") ?: ""
            PasswordHash = sharedPreferences.getString("${str}_pwd_hash", "") ?: ""
            GridUUID = sharedPreferences.getString("${str}_grid", null)?.let { UUID.fromString(it) }
        }

        constructor(loginName: String, passwordHash: String, gridUUID: UUID?) {
            this.LoginName = loginName
            this.PasswordHash = passwordHash
            this.GridUUID = gridUUID
        }

        private constructor(parcel: Parcel) {
            LoginName = parcel.readString()!!
            PasswordHash = parcel.readString()!!
            GridUUID = parcel.readString()?.let { if (it.isNotEmpty()) UUID.fromString(it) else null }
        }

        override fun describeContents(): Int {
            return 0
        }

        fun getGridUUID(): UUID? {
            return GridUUID
        }

        fun getLoginName(): String {
            return LoginName
        }

        fun getPasswordHash(): String {
            return PasswordHash
        }

        fun saveToPreferences(
            editor: SharedPreferences.Editor,
            str: String,
        ) {
            editor.putString("${str}_login_name", LoginName)
            editor.putString("${str}_pwd_hash", PasswordHash)
            editor.putString("${str}_grid", GridUUID?.toString())
        }

        fun setGridUUID(uuid: UUID?) {
            this.GridUUID = uuid
        }

        fun setLoginName(str: String) {
            this.LoginName = str
        }

        fun setPasswordHash(str: String) {
            this.PasswordHash = str
        }

        override fun writeToParcel(
            parcel: Parcel,
            i: Int,
        ) {
            parcel.writeString(LoginName)
            parcel.writeString(PasswordHash)
            parcel.writeString(GridUUID?.toString() ?: "")
        }

        companion object CREATOR : Parcelable.Creator<AccountInfo> {
            override fun createFromParcel(parcel: Parcel): AccountInfo {
                return AccountInfo(parcel)
            }

            override fun newArray(size: Int): Array<AccountInfo?> {
                return arrayOfNulls(size)
            }
        }
    }

    init {
        loadAccounts()
    }

    fun addNewAccount(accountInfo: AccountInfo) {
        accounts.add(accountInfo)
    }

    fun deleteAccount(accountInfo: AccountInfo) {
        accounts.remove(accountInfo)
    }

    fun findAccount(
        loginName: String,
        uuid: UUID,
    ): AccountInfo? {
        return accounts.firstOrNull { it.getLoginName() == loginName && it.getGridUUID() == uuid }
    }

    fun findOrAddAccount(
        loginName: String,
        passwordHash: String,
        uuid: UUID,
    ): AccountInfo {
        val existingAccount = findAccount(loginName, uuid)
        if (existingAccount != null) {
            existingAccount.setPasswordHash(passwordHash)
            savePreferences()
            return existingAccount
        }
        val newAccount = AccountInfo(loginName, passwordHash, uuid)
        accounts.add(newAccount)
        savePreferences()
        return newAccount
    }

    fun getAccountList(): List<AccountInfo> {
        return accounts
    }

    fun getAccountList(list: MutableList<AccountInfo>): List<AccountInfo> {
        list.clear()
        list.addAll(accounts)
        return list
    }

    fun loadAccounts() {
        accounts.clear()
        val defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context.applicationContext)
        val accountsCount = defaultSharedPreferences.getInt("accounts_count", 0)
        for (i in 0 until accountsCount) {
            accounts.add(AccountInfo(defaultSharedPreferences, "account_$i"))
        }
    }

    fun savePreferences() {
        val edit = PreferenceManager.getDefaultSharedPreferences(context.applicationContext).edit()
        edit.putInt("accounts_count", accounts.size)
        accounts.forEachIndexed { i, accountInfo ->
            accountInfo.saveToPreferences(edit, "account_$i")
        }
        edit.apply()
    }
}
