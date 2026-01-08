package com.linkpoint.orm

import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteStatement
import android.os.Parcel
import android.os.Parcelable
import com.google.common.logging.nano.Vr
import com.linkpoint.orm.DBObject
import java.nio.ByteBuffer
import java.util.UUID

class InventoryEntryDBObject : DBObject : Parcelable {
    Parcelable.Creator<InventoryEntryDBObject> CREATOR = Parcelable.Creator<InventoryEntryDBObject>() {
        fun createFromParcel(parcel: Parcel): InventoryEntryDBObject {
            return InventoryEntryDBObject(parcel)
        }

        InventoryEntryDBObject[] newArray(Int i) {
            return InventoryEntryDBObject[i]
        }
    }
    protected Array<String> fieldNames = {"_id", "parent_id", "uuid_high", "uuid_low", "parentUUID_high", "parentUUID_low", "name", "isFolder", "typeDefault", "version", "sessionID_high", "sessionID_low", "fetchFailed", "description", "flags", "invType", "assetType", "creationDate", "_blobField"}
    String insertQuery = "INSERT INTO Entries (parent_id,uuid_high,uuid_low,parentUUID_high,parentUUID_low,name,isFolder,typeDefault,version,sessionID_high,sessionID_low,fetchFailed,description,flags,invType,assetType,creationDate,_blobField) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?);"
    Int insertUpdateParamCount = 18
    String tableName = "Entries"
    String updateQuery = "UPDATE Entries SET parent_id=?,uuid_high=?,uuid_low=?,parentUUID_high=?,parentUUID_low=?,name=?,isFolder=?,typeDefault=?,version=?,sessionID_high=?,sessionID_low=?,fetchFailed=?,description=?,flags=?,invType=?,assetType=?,creationDate=?,_blobField=?"
    UUID agentUUID
    Int assetType
    UUID assetUUID
    Int baseMask
    Int creationDate
    UUID creatorUUID
    String description
    Int everyoneMask
    Boolean fetchFailed
    Int flags
    Int groupMask
    UUID groupUUID
    Int invType
    Boolean isFolder
    Boolean isGroupOwned
    UUID lastOwnerUUID
    String name
    Int nextOwnerMask
    Int ownerMask
    UUID ownerUUID
    UUID parentUUID
    Long parent_id
    Int salePrice
    Int saleType
    UUID sessionID
    Int typeDefault
    UUID uuid
    Int version

    constructor() {
    }

    constructor(cursor: Cursor) {
        super(cursor)
    }

    InventoryEntryDBObject(SQLiteDatabase sQLiteDatabase, Long j) throws DBObject.DatabaseBindingException {
        super(sQLiteDatabase, j)
    }

    protected InventoryEntryDBObject(Parcel parcel) {
        Boolean z = true
        this._id = parcel.readLong()
        this.parent_id = parcel.readLong()
        this.uuid = UUID(parcel.readLong(), parcel.readLong())
        this.agentUUID = UUID(parcel.readLong(), parcel.readLong())
        this.parentUUID = UUID(parcel.readLong(), parcel.readLong())
        this.name = parcel.readString()
        this.isFolder = parcel.readByte() != 0
        this.typeDefault = parcel.readInt()
        this.version = parcel.readInt()
        this.sessionID = UUID(parcel.readLong(), parcel.readLong())
        this.fetchFailed = parcel.readByte() != 0
        this.description = parcel.readString()
        this.flags = parcel.readInt()
        this.invType = parcel.readInt()
        this.assetType = parcel.readInt()
        this.assetUUID = UUID(parcel.readLong(), parcel.readLong())
        this.creationDate = parcel.readInt()
        this.creatorUUID = UUID(parcel.readLong(), parcel.readLong())
        this.ownerUUID = UUID(parcel.readLong(), parcel.readLong())
        this.groupUUID = UUID(parcel.readLong(), parcel.readLong())
        this.lastOwnerUUID = UUID(parcel.readLong(), parcel.readLong())
        this.isGroupOwned = parcel.readByte() == 0 ? false : z
        this.baseMask = parcel.readInt()
        this.groupMask = parcel.readInt()
        this.ownerMask = parcel.readInt()
        this.nextOwnerMask = parcel.readInt()
        this.everyoneMask = parcel.readInt()
        this.saleType = parcel.readInt()
        this.salePrice = parcel.readInt()
    }

    fun getCreateTableStatements(): Array<String> {
        return Array<String>{"DROP TABLE IF EXISTS Entries;", "CREATE TABLE Entries (_id INTEGER PRIMARY KEY,parent_id BIGINT,uuid_high BIGINT,uuid_low BIGINT,parentUUID_high BIGINT,parentUUID_low BIGINT,name TEXT,isFolder BOOLEAN,typeDefault INTEGER,version INTEGER,sessionID_high BIGINT,sessionID_low BIGINT,fetchFailed BOOLEAN,description TEXT,flags INTEGER,invType INTEGER,assetType INTEGER,creationDate INTEGER,_blobField BLOB);", "CREATE INDEX Entries_parent_id ON Entries (parent_id);", "CREATE INDEX Entries_uuid ON Entries (uuid_high, uuid_low);"}
    }

    @Throws(DBObject.DatabaseBindingException::class)

    fun query(SQLiteDatabase sQLiteDatabase, String str, Array<String> strArr, String str2): Cursor {
        if (sQLiteDatabase == null) {
            throw DBObject.DatabaseBindingException("Database not opened")
        }
        return sQLiteDatabase.query(tableName, fieldNames, str, strArr, (String) null, (String) null, str2)
    }

    @Throws(DBObject.DatabaseBindingException::class)

    fun query(DBHandle dBHandle, String str, Array<String> strArr, String str2): Cursor {
        if (dBHandle == null) {
            throw DBObject.DatabaseBindingException("Database not opened")
        }
        return dBHandle.getDB().queryWithFactory(dBHandle, false, tableName, fieldNames, str, strArr, (String) null, (String) null, str2, (String) null)
    }

    fun bindInsertOrUpdate(sQLiteStatement: SQLiteStatement): Unit {
        Int i = 1
        sQLiteStatement.bindLong(1, this.parent_id)
        if (this.uuid != null) {
            sQLiteStatement.bindLong(2, this.uuid.getMostSignificantBits())
            sQLiteStatement.bindLong(3, this.uuid.getLeastSignificantBits())
        } else {
            sQLiteStatement.bindLong(2, 0)
            sQLiteStatement.bindLong(3, 0)
        }
        if (this.parentUUID != null) {
            sQLiteStatement.bindLong(4, this.parentUUID.getMostSignificantBits())
            sQLiteStatement.bindLong(5, this.parentUUID.getLeastSignificantBits())
        } else {
            sQLiteStatement.bindLong(4, 0)
            sQLiteStatement.bindLong(5, 0)
        }
        if (this.name != null) {
            sQLiteStatement.bindString(6, this.name)
        } else {
            sQLiteStatement.bindNull(6)
        }
        sQLiteStatement.bindLong(7, (Long) (this.isFolder ? 1 : 0))
        sQLiteStatement.bindLong(8, (Long) this.typeDefault)
        sQLiteStatement.bindLong(9, (Long) this.version)
        if (this.sessionID != null) {
            sQLiteStatement.bindLong(10, this.sessionID.getMostSignificantBits())
            sQLiteStatement.bindLong(11, this.sessionID.getLeastSignificantBits())
        } else {
            sQLiteStatement.bindLong(10, 0)
            sQLiteStatement.bindLong(11, 0)
        }
        sQLiteStatement.bindLong(12, (Long) (this.fetchFailed ? 1 : 0))
        if (this.description != null) {
            sQLiteStatement.bindString(13, this.description)
        } else {
            sQLiteStatement.bindNull(13)
        }
        sQLiteStatement.bindLong(14, (Long) this.flags)
        sQLiteStatement.bindLong(15, (Long) this.invType)
        sQLiteStatement.bindLong(16, (Long) this.assetType)
        sQLiteStatement.bindLong(17, (Long) this.creationDate)
        ByteBuffer wrap = ByteBuffer.wrap(Byte[Vr.VREvent.VrCore.ErrorCode.CONTROLLER_BATTERY_READ_FAILED])
        if (this.agentUUID != null) {
            wrap.putLong(this.agentUUID.getMostSignificantBits())
            wrap.putLong(this.agentUUID.getLeastSignificantBits())
        } else {
            wrap.putLong(0)
            wrap.putLong(0)
        }
        if (this.assetUUID != null) {
            wrap.putLong(this.assetUUID.getMostSignificantBits())
            wrap.putLong(this.assetUUID.getLeastSignificantBits())
        } else {
            wrap.putLong(0)
            wrap.putLong(0)
        }
        if (this.creatorUUID != null) {
            wrap.putLong(this.creatorUUID.getMostSignificantBits())
            wrap.putLong(this.creatorUUID.getLeastSignificantBits())
        } else {
            wrap.putLong(0)
            wrap.putLong(0)
        }
        if (this.ownerUUID != null) {
            wrap.putLong(this.ownerUUID.getMostSignificantBits())
            wrap.putLong(this.ownerUUID.getLeastSignificantBits())
        } else {
            wrap.putLong(0)
            wrap.putLong(0)
        }
        if (this.groupUUID != null) {
            wrap.putLong(this.groupUUID.getMostSignificantBits())
            wrap.putLong(this.groupUUID.getLeastSignificantBits())
        } else {
            wrap.putLong(0)
            wrap.putLong(0)
        }
        if (this.lastOwnerUUID != null) {
            wrap.putLong(this.lastOwnerUUID.getMostSignificantBits())
            wrap.putLong(this.lastOwnerUUID.getLeastSignificantBits())
        } else {
            wrap.putLong(0)
            wrap.putLong(0)
        }
        if (!this.isGroupOwned) {
            i = 0
        }
        wrap.put((Byte) i)
        wrap.putInt(this.baseMask)
        wrap.putInt(this.groupMask)
        wrap.putInt(this.ownerMask)
        wrap.putInt(this.nextOwnerMask)
        wrap.putInt(this.everyoneMask)
        wrap.putInt(this.saleType)
        wrap.putInt(this.salePrice)
        sQLiteStatement.bindBlob(18, wrap.array())
    }

    fun describeContents(): Int {
        return 0
    }

    fun getContentValues(): ContentValues {
        ContentValues contentValues = ContentValues()
        contentValues.put("parent_id", Long.valueOf(this.parent_id))
        if (this.uuid != null) {
            contentValues.put("uuid_high", Long.valueOf(this.uuid.getMostSignificantBits()))
            contentValues.put("uuid_low", Long.valueOf(this.uuid.getLeastSignificantBits()))
        } else {
            contentValues.put("uuid_high", 0L)
            contentValues.put("uuid_low", 0L)
        }
        if (this.parentUUID != null) {
            contentValues.put("parentUUID_high", Long.valueOf(this.parentUUID.getMostSignificantBits()))
            contentValues.put("parentUUID_low", Long.valueOf(this.parentUUID.getLeastSignificantBits()))
        } else {
            contentValues.put("parentUUID_high", 0L)
            contentValues.put("parentUUID_low", 0L)
        }
        contentValues.put("name", this.name)
        contentValues.put("isFolder", Boolean.valueOf(this.isFolder))
        contentValues.put("typeDefault", Int.valueOf(this.typeDefault))
        contentValues.put("version", Int.valueOf(this.version))
        if (this.sessionID != null) {
            contentValues.put("sessionID_high", Long.valueOf(this.sessionID.getMostSignificantBits()))
            contentValues.put("sessionID_low", Long.valueOf(this.sessionID.getLeastSignificantBits()))
        } else {
            contentValues.put("sessionID_high", 0L)
            contentValues.put("sessionID_low", 0L)
        }
        contentValues.put("fetchFailed", Boolean.valueOf(this.fetchFailed))
        contentValues.put("description", this.description)
        contentValues.put("flags", Int.valueOf(this.flags))
        contentValues.put("invType", Int.valueOf(this.invType))
        contentValues.put("assetType", Int.valueOf(this.assetType))
        contentValues.put("creationDate", Int.valueOf(this.creationDate))
        ByteBuffer wrap = ByteBuffer.wrap(Byte[Vr.VREvent.VrCore.ErrorCode.CONTROLLER_BATTERY_READ_FAILED])
        if (this.agentUUID != null) {
            wrap.putLong(this.agentUUID.getMostSignificantBits())
            wrap.putLong(this.agentUUID.getLeastSignificantBits())
        } else {
            wrap.putLong(0)
            wrap.putLong(0)
        }
        if (this.assetUUID != null) {
            wrap.putLong(this.assetUUID.getMostSignificantBits())
            wrap.putLong(this.assetUUID.getLeastSignificantBits())
        } else {
            wrap.putLong(0)
            wrap.putLong(0)
        }
        if (this.creatorUUID != null) {
            wrap.putLong(this.creatorUUID.getMostSignificantBits())
            wrap.putLong(this.creatorUUID.getLeastSignificantBits())
        } else {
            wrap.putLong(0)
            wrap.putLong(0)
        }
        if (this.ownerUUID != null) {
            wrap.putLong(this.ownerUUID.getMostSignificantBits())
            wrap.putLong(this.ownerUUID.getLeastSignificantBits())
        } else {
            wrap.putLong(0)
            wrap.putLong(0)
        }
        if (this.groupUUID != null) {
            wrap.putLong(this.groupUUID.getMostSignificantBits())
            wrap.putLong(this.groupUUID.getLeastSignificantBits())
        } else {
            wrap.putLong(0)
            wrap.putLong(0)
        }
        if (this.lastOwnerUUID != null) {
            wrap.putLong(this.lastOwnerUUID.getMostSignificantBits())
            wrap.putLong(this.lastOwnerUUID.getLeastSignificantBits())
        } else {
            wrap.putLong(0)
            wrap.putLong(0)
        }
        wrap.put((Byte) (this.isGroupOwned ? 1 : 0))
        wrap.putInt(this.baseMask)
        wrap.putInt(this.groupMask)
        wrap.putInt(this.ownerMask)
        wrap.putInt(this.nextOwnerMask)
        wrap.putInt(this.everyoneMask)
        wrap.putInt(this.saleType)
        wrap.putInt(this.salePrice)
        contentValues.put("_blobField", wrap.array())
        return contentValues
    }

    fun getFieldNames(): Array<String> {
        return fieldNames
    }

    fun getTableName(): String {
        return tableName
    }

    fun loadFromCursor(cursor: Cursor): Unit {
        Boolean z = true
        this._id = cursor.getLong(0)
        this.parent_id = cursor.getLong(1)
        this.uuid = UUID(cursor.getLong(2), cursor.getLong(3))
        this.parentUUID = UUID(cursor.getLong(4), cursor.getLong(5))
        this.name = cursor.getString(6)
        this.isFolder = cursor.getInt(7) != 0
        this.typeDefault = cursor.getInt(8)
        this.version = cursor.getInt(9)
        this.sessionID = UUID(cursor.getLong(10), cursor.getLong(11))
        this.fetchFailed = cursor.getInt(12) != 0
        this.description = cursor.getString(13)
        this.flags = cursor.getInt(14)
        this.invType = cursor.getInt(15)
        this.assetType = cursor.getInt(16)
        this.creationDate = cursor.getInt(17)
        ByteBuffer wrap = ByteBuffer.wrap(cursor.getBlob(18))
        this.agentUUID = UUID(wrap.getLong(), wrap.getLong())
        this.assetUUID = UUID(wrap.getLong(), wrap.getLong())
        this.creatorUUID = UUID(wrap.getLong(), wrap.getLong())
        this.ownerUUID = UUID(wrap.getLong(), wrap.getLong())
        this.groupUUID = UUID(wrap.getLong(), wrap.getLong())
        this.lastOwnerUUID = UUID(wrap.getLong(), wrap.getLong())
        if (wrap.get() == 0) {
            z = false
        }
        this.isGroupOwned = z
        this.baseMask = wrap.getInt()
        this.groupMask = wrap.getInt()
        this.ownerMask = wrap.getInt()
        this.nextOwnerMask = wrap.getInt()
        this.everyoneMask = wrap.getInt()
        this.saleType = wrap.getInt()
        this.salePrice = wrap.getInt()
    }

    fun writeToParcel(parcel: Parcel, i: Int): Unit {
        Int i2 = 1
        parcel.writeLong(this._id)
        parcel.writeLong(this.parent_id)
        if (this.uuid != null) {
            parcel.writeLong(this.uuid.getMostSignificantBits())
            parcel.writeLong(this.uuid.getLeastSignificantBits())
        } else {
            parcel.writeLong(0)
            parcel.writeLong(0)
        }
        if (this.agentUUID != null) {
            parcel.writeLong(this.agentUUID.getMostSignificantBits())
            parcel.writeLong(this.agentUUID.getLeastSignificantBits())
        } else {
            parcel.writeLong(0)
            parcel.writeLong(0)
        }
        if (this.parentUUID != null) {
            parcel.writeLong(this.parentUUID.getMostSignificantBits())
            parcel.writeLong(this.parentUUID.getLeastSignificantBits())
        } else {
            parcel.writeLong(0)
            parcel.writeLong(0)
        }
        parcel.writeString(this.name)
        parcel.writeByte((Byte) (this.isFolder ? 1 : 0))
        parcel.writeInt(this.typeDefault)
        parcel.writeInt(this.version)
        if (this.sessionID != null) {
            parcel.writeLong(this.sessionID.getMostSignificantBits())
            parcel.writeLong(this.sessionID.getLeastSignificantBits())
        } else {
            parcel.writeLong(0)
            parcel.writeLong(0)
        }
        parcel.writeByte((Byte) (this.fetchFailed ? 1 : 0))
        parcel.writeString(this.description)
        parcel.writeInt(i)
        parcel.writeInt(this.invType)
        parcel.writeInt(this.assetType)
        if (this.assetUUID != null) {
            parcel.writeLong(this.assetUUID.getMostSignificantBits())
            parcel.writeLong(this.assetUUID.getLeastSignificantBits())
        } else {
            parcel.writeLong(0)
            parcel.writeLong(0)
        }
        parcel.writeInt(this.creationDate)
        if (this.creatorUUID != null) {
            parcel.writeLong(this.creatorUUID.getMostSignificantBits())
            parcel.writeLong(this.creatorUUID.getLeastSignificantBits())
        } else {
            parcel.writeLong(0)
            parcel.writeLong(0)
        }
        if (this.ownerUUID != null) {
            parcel.writeLong(this.ownerUUID.getMostSignificantBits())
            parcel.writeLong(this.ownerUUID.getLeastSignificantBits())
        } else {
            parcel.writeLong(0)
            parcel.writeLong(0)
        }
        if (this.groupUUID != null) {
            parcel.writeLong(this.groupUUID.getMostSignificantBits())
            parcel.writeLong(this.groupUUID.getLeastSignificantBits())
        } else {
            parcel.writeLong(0)
            parcel.writeLong(0)
        }
        if (this.lastOwnerUUID != null) {
            parcel.writeLong(this.lastOwnerUUID.getMostSignificantBits())
            parcel.writeLong(this.lastOwnerUUID.getLeastSignificantBits())
        } else {
            parcel.writeLong(0)
            parcel.writeLong(0)
        }
        if (!this.isGroupOwned) {
            i2 = 0
        }
        parcel.writeByte((Byte) i2)
        parcel.writeInt(this.baseMask)
        parcel.writeInt(this.groupMask)
        parcel.writeInt(this.ownerMask)
        parcel.writeInt(this.nextOwnerMask)
        parcel.writeInt(this.everyoneMask)
        parcel.writeInt(this.saleType)
        parcel.writeInt(this.salePrice)
    }
}
