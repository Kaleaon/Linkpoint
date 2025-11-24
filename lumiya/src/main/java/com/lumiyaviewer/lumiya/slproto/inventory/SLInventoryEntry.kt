package com.lumiyaviewer.lumiya.slproto.inventory

import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteStatement
import android.os.Parcel
import android.os.Parcelable
import com.google.common.collect.ImmutableMap
import com.google.common.collect.Table
import com.lumiyaviewer.lumiya.R
import com.lumiyaviewer.lumiya.orm.DBHandle
import com.lumiyaviewer.lumiya.orm.DBObject
import com.lumiyaviewer.lumiya.orm.InventoryEntryDBObject
import com.lumiyaviewer.lumiya.slproto.assets.SLWearable
import com.lumiyaviewer.lumiya.slproto.assets.SLWearableType
import com.lumiyaviewer.lumiya.utils.SimpleStringParser
import java.util.UUID

class SLInventoryEntry : InventoryEntryDBObject {

    constructor() : super()
    constructor(cursor: Cursor) : super(cursor)
    constructor(db: SQLiteDatabase, id: Long) : super(db, id)
    constructor(parcel: Parcel) : super(parcel)
    constructor(dbHandle: DBHandle?, id: Long) : super(dbHandle?.getDB() ?: throw IllegalArgumentException("DBHandle cannot be null"), id)

    companion object {
        const val DELIM_ANY = " \t\n"
        const val DELIM_EOL = "\n"
        
        const val FT_ANIMATION = 20
        const val FT_BASIC_ROOT = 52
        const val FT_BODYPART = 13
        const val FT_CALLINGCARD = 2
        const val FT_CLOTHING = 5
        const val FT_CURRENT_OUTFIT = 46
        const val FT_ENSEMBLE_END = 45
        const val FT_ENSEMBLE_START = 26
        const val FT_FAVORITE = 23
        const val FT_GESTURE = 21
        const val FT_INBOX = 50
        const val FT_LANDMARK = 3
        const val FT_LOST_AND_FOUND = 16
        const val FT_LSL_TEXT = 10
        const val FT_MESH = 49
        const val FT_MY_OUTFITS = 48
        const val FT_NOTECARD = 7
        const val FT_OBJECT = 6
        const val FT_OUTBOX = 51
        const val FT_OUTFIT = 47
        const val FT_ROOT_INVENTORY = 8
        const val FT_SNAPSHOT_CATEGORY = 15
        const val FT_SOUND = 1
        const val FT_TEXTURE = 0
        const val FT_TRASH = 14
        const val II_FLAGS_WEARABLES_MASK = 255
        const val IT_ANIMATION = 19
        const val IT_ATTACHMENT = 17
        const val IT_BODYPART = 13
        const val IT_CALLINGCARD = 2
        const val IT_CATEGORY = 8
        const val IT_CLOTHING = 5
        const val IT_COUNT = 21
        const val IT_GESTURE = 20
        const val IT_LANDMARK = 3
        const val IT_LOST_AND_FOUND = 16
        const val IT_LSL = 10
        const val IT_LSL_BYTECODE = 11
        const val IT_NOTECARD = 7
        const val IT_OBJECT = 6
        const val IT_ROOT_CATEGORY = 9
        const val IT_SCRIPT = 4
        const val IT_SNAPSHOT = 15
        const val IT_SOUND = 1
        const val IT_TEXTURE = 0
        const val IT_TEXTURE_TGA = 12
        const val IT_TRASH = 14
        const val IT_WEARABLE = 18
        const val PERM_COPY = 32768
        const val PERM_FULL = Integer.MAX_VALUE
        const val PERM_MODIFY = 16384
        const val PERM_TRANSFER = 8192

        @JvmField
        val CREATOR = object : Parcelable.Creator<SLInventoryEntry> {
            override fun createFromParcel(parcel: Parcel): SLInventoryEntry {
                return SLInventoryEntry(parcel)
            }

            override fun newArray(size: Int): Array<SLInventoryEntry?> {
                return arrayOfNulls(size)
            }
        }

        fun find(db: SQLiteDatabase, uuid: UUID): SLInventoryEntry? {
            try {
                val cursor = db.query(
                    TABLE_NAME, null, // null means all columns
                    "uuid_low = ? AND uuid_high = ?",
                    arrayOf(uuid.leastSignificantBits.toString(), uuid.mostSignificantBits.toString()),
                    null, null, null
                )
                return cursor.use {
                    if (it.moveToFirst()) SLInventoryEntry(it) else null
                }
            } catch (e: SQLiteException) {
                e.printStackTrace()
                return null
            }
        }

        fun query(db: SQLiteDatabase, selection: String?, selectionArgs: Array<String>?, orderBy: String?): Cursor {
            return InventoryEntryDBObject.query(db, selection, selectionArgs, orderBy)
        }
    }

    fun isFolderOrFolderLink(): Boolean {
        return isFolder || assetType == SLAssetType.AT_LINK_FOLDER.typeCode
    }
    
    fun isLink(): Boolean {
        return assetType == SLAssetType.AT_LINK.typeCode || assetType == SLAssetType.AT_LINK_FOLDER.typeCode
    }
}
