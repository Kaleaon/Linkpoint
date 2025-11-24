package com.lumiyaviewer.lumiya.orm

import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteStatement
import android.os.Parcel
import android.os.Parcelable
import com.lumiyaviewer.lumiya.utils.UUIDPool
import java.nio.ByteBuffer
import java.util.UUID

open class InventoryEntryDBObject : DBObject, Parcelable {

    var parent_id: Long = 0
    var uuid: UUID = UUIDPool.ZeroUUID
    var agentUUID: UUID = UUIDPool.ZeroUUID
    var parentUUID: UUID = UUIDPool.ZeroUUID
    var name: String = ""
    var isFolder: Boolean = false
    var typeDefault: Int = 0
    var version: Int = 0
    var sessionID: UUID = UUIDPool.ZeroUUID
    var fetchFailed: Boolean = false
    var description: String = ""
    var flags: Int = 0
    var invType: Int = 0
    var assetType: Int = 0
    var assetUUID: UUID = UUIDPool.ZeroUUID
    var creationDate: Int = 0
    var creatorUUID: UUID = UUIDPool.ZeroUUID
    var ownerUUID: UUID = UUIDPool.ZeroUUID
    var groupUUID: UUID = UUIDPool.ZeroUUID
    var lastOwnerUUID: UUID = UUIDPool.ZeroUUID
    var isGroupOwned: Boolean = false
    var baseMask: Int = 0
    var groupMask: Int = 0
    var ownerMask: Int = 0
    var nextOwnerMask: Int = 0
    var everyoneMask: Int = 0
    var saleType: Int = 0
    var salePrice: Int = 0

    companion object {
        const val TABLE_NAME = "Entries"
        private val FIELD_NAMES = arrayOf(
            "_id", "parent_id", "uuid_high", "uuid_low", "parentUUID_high", "parentUUID_low",
            "name", "isFolder", "typeDefault", "version", "sessionID_high", "sessionID_low",
            "fetchFailed", "description", "flags", "invType", "assetType", "creationDate", "_blobField"
        )
        private const val INSERT_QUERY = "INSERT INTO Entries (parent_id,uuid_high,uuid_low,parentUUID_high,parentUUID_low,name,isFolder,typeDefault,version,sessionID_high,sessionID_low,fetchFailed,description,flags,invType,assetType,creationDate,_blobField) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?);"
        private const val UPDATE_QUERY = "UPDATE Entries SET parent_id=?,uuid_high=?,uuid_low=?,parentUUID_high=?,parentUUID_low=?,name=?,isFolder=?,typeDefault=?,version=?,sessionID_high=?,sessionID_low=?,fetchFailed=?,description=?,flags=?,invType=?,assetType=?,creationDate=?,_blobField=? WHERE uuid_high = ? AND uuid_low = ?"

        @JvmField
        val CREATOR = object : Parcelable.Creator<InventoryEntryDBObject> {
            override fun createFromParcel(parcel: Parcel): InventoryEntryDBObject {
                return InventoryEntryDBObject(parcel)
            }

            override fun newArray(size: Int): Array<InventoryEntryDBObject?> {
                return arrayOfNulls(size)
            }
        }

        fun query(db: SQLiteDatabase, selection: String?, selectionArgs: Array<String>?, orderBy: String?): Cursor {
            return db.query(TABLE_NAME, FIELD_NAMES, selection, selectionArgs, null, null, orderBy)
        }

        fun query(dbHandle: DBHandle, selection: String?, selectionArgs: Array<String>?, orderBy: String?): Cursor {
            return dbHandle.getDB().queryWithFactory(dbHandle, false, TABLE_NAME, FIELD_NAMES, selection, selectionArgs, null, null, orderBy, null)
        }
    }

    constructor() : super()

    constructor(cursor: Cursor) : super(cursor)

    constructor(db: SQLiteDatabase, id: Long) : super(db, id)

    constructor(parcel: Parcel) {
        _id = parcel.readLong()
        parent_id = parcel.readLong()
        uuid = UUID(parcel.readLong(), parcel.readLong())
        agentUUID = UUID(parcel.readLong(), parcel.readLong())
        parentUUID = UUID(parcel.readLong(), parcel.readLong())
        name = parcel.readString() ?: ""
        isFolder = parcel.readByte() != 0.toByte()
        typeDefault = parcel.readInt()
        version = parcel.readInt()
        sessionID = UUID(parcel.readLong(), parcel.readLong())
        fetchFailed = parcel.readByte() != 0.toByte()
        description = parcel.readString() ?: ""
        flags = parcel.readInt()
        invType = parcel.readInt()
        assetType = parcel.readInt()
        assetUUID = UUID(parcel.readLong(), parcel.readLong())
        creationDate = parcel.readInt()
        creatorUUID = UUID(parcel.readLong(), parcel.readLong())
        ownerUUID = UUID(parcel.readLong(), parcel.readLong())
        groupUUID = UUID(parcel.readLong(), parcel.readLong())
        lastOwnerUUID = UUID(parcel.readLong(), parcel.readLong())
        isGroupOwned = parcel.readByte() != 0.toByte()
        baseMask = parcel.readInt()
        groupMask = parcel.readInt()
        ownerMask = parcel.readInt()
        nextOwnerMask = parcel.readInt()
        everyoneMask = parcel.readInt()
        saleType = parcel.readInt()
        salePrice = parcel.readInt()
    }

    override fun describeContents(): Int = 0

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(_id)
        parcel.writeLong(parent_id)
        writeUUID(parcel, uuid)
        writeUUID(parcel, agentUUID)
        writeUUID(parcel, parentUUID)
        parcel.writeString(name)
        parcel.writeByte(if (isFolder) 1 else 0)
        parcel.writeInt(typeDefault)
        parcel.writeInt(version)
        writeUUID(parcel, sessionID)
        parcel.writeByte(if (fetchFailed) 1 else 0)
        parcel.writeString(description)
        parcel.writeInt(flags) // Extra flags placeholder from original code?
        parcel.writeInt(invType)
        parcel.writeInt(assetType)
        writeUUID(parcel, assetUUID)
        parcel.writeInt(creationDate)
        writeUUID(parcel, creatorUUID)
        writeUUID(parcel, ownerUUID)
        writeUUID(parcel, groupUUID)
        writeUUID(parcel, lastOwnerUUID)
        parcel.writeByte(if (isGroupOwned) 1 else 0)
        parcel.writeInt(baseMask)
        parcel.writeInt(groupMask)
        parcel.writeInt(ownerMask)
        parcel.writeInt(nextOwnerMask)
        parcel.writeInt(everyoneMask)
        parcel.writeInt(saleType)
        parcel.writeInt(salePrice)
    }

    private fun writeUUID(parcel: Parcel, uuid: UUID?) {
        if (uuid != null) {
            parcel.writeLong(uuid.mostSignificantBits)
            parcel.writeLong(uuid.leastSignificantBits)
        } else {
            parcel.writeLong(0)
            parcel.writeLong(0)
        }
    }

    override fun getFieldNames(): Array<String> = FIELD_NAMES

    override fun getTableName(): String = TABLE_NAME

    override fun loadFromCursor(cursor: Cursor) {
        _id = cursor.getLong(0)
        parent_id = cursor.getLong(1)
        uuid = UUID(cursor.getLong(2), cursor.getLong(3))
        parentUUID = UUID(cursor.getLong(4), cursor.getLong(5))
        name = cursor.getString(6)
        isFolder = cursor.getInt(7) != 0
        typeDefault = cursor.getInt(8)
        version = cursor.getInt(9)
        sessionID = UUID(cursor.getLong(10), cursor.getLong(11))
        fetchFailed = cursor.getInt(12) != 0
        description = cursor.getString(13)
        flags = cursor.getInt(14)
        invType = cursor.getInt(15)
        assetType = cursor.getInt(16)
        creationDate = cursor.getInt(17)
        
        val blob = cursor.getBlob(18)
        if (blob != null) {
            val buffer = ByteBuffer.wrap(blob)
            agentUUID = UUID(buffer.long, buffer.long)
            assetUUID = UUID(buffer.long, buffer.long)
            creatorUUID = UUID(buffer.long, buffer.long)
            ownerUUID = UUID(buffer.long, buffer.long)
            groupUUID = UUID(buffer.long, buffer.long)
            lastOwnerUUID = UUID(buffer.long, buffer.long)
            isGroupOwned = buffer.get().toInt() != 0
            baseMask = buffer.int
            groupMask = buffer.int
            ownerMask = buffer.int
            nextOwnerMask = buffer.int
            everyoneMask = buffer.int
            saleType = buffer.int
            salePrice = buffer.int
        }
    }

    override fun getContentValues(): ContentValues {
        val values = ContentValues()
        values.put("parent_id", parent_id)
        values.put("uuid_high", uuid.mostSignificantBits)
        values.put("uuid_low", uuid.leastSignificantBits)
        values.put("parentUUID_high", parentUUID.mostSignificantBits)
        values.put("parentUUID_low", parentUUID.leastSignificantBits)
        values.put("name", name)
        values.put("isFolder", isFolder)
        values.put("typeDefault", typeDefault)
        values.put("version", version)
        values.put("sessionID_high", sessionID.mostSignificantBits)
        values.put("sessionID_low", sessionID.leastSignificantBits)
        values.put("fetchFailed", fetchFailed)
        values.put("description", description)
        values.put("flags", flags)
        values.put("invType", invType)
        values.put("assetType", assetType)
        values.put("creationDate", creationDate)

        val buffer = ByteBuffer.allocate(100) // Sufficient size
        putUUID(buffer, agentUUID)
        putUUID(buffer, assetUUID)
        putUUID(buffer, creatorUUID)
        putUUID(buffer, ownerUUID)
        putUUID(buffer, groupUUID)
        putUUID(buffer, lastOwnerUUID)
        buffer.put(if (isGroupOwned) 1.toByte() else 0.toByte())
        buffer.putInt(baseMask)
        buffer.putInt(groupMask)
        buffer.putInt(ownerMask)
        buffer.putInt(nextOwnerMask)
        buffer.putInt(everyoneMask)
        buffer.putInt(saleType)
        buffer.putInt(salePrice)
        
        values.put("_blobField", buffer.array())
        return values
    }

    private fun putUUID(buffer: ByteBuffer, uuid: UUID?) {
        if (uuid != null) {
            buffer.putLong(uuid.mostSignificantBits)
            buffer.putLong(uuid.leastSignificantBits)
        } else {
            buffer.putLong(0)
            buffer.putLong(0)
        }
    }

    override fun bindInsertOrUpdate(statement: SQLiteStatement) {
        statement.bindLong(1, parent_id)
        bindUUID(statement, 2, uuid)
        bindUUID(statement, 4, parentUUID)
        if (name != null) statement.bindString(6, name) else statement.bindNull(6)
        statement.bindLong(7, if (isFolder) 1 else 0)
        statement.bindLong(8, typeDefault.toLong())
        statement.bindLong(9, version.toLong())
        bindUUID(statement, 10, sessionID)
        statement.bindLong(12, if (fetchFailed) 1 else 0)
        if (description != null) statement.bindString(13, description) else statement.bindNull(13)
        statement.bindLong(14, flags.toLong())
        statement.bindLong(15, invType.toLong())
        statement.bindLong(16, assetType.toLong())
        statement.bindLong(17, creationDate.toLong())
        
        val buffer = ByteBuffer.allocate(100)
        putUUID(buffer, agentUUID)
        putUUID(buffer, assetUUID)
        putUUID(buffer, creatorUUID)
        putUUID(buffer, ownerUUID)
        putUUID(buffer, groupUUID)
        putUUID(buffer, lastOwnerUUID)
        buffer.put(if (isGroupOwned) 1.toByte() else 0.toByte())
        buffer.putInt(baseMask)
        buffer.putInt(groupMask)
        buffer.putInt(ownerMask)
        buffer.putInt(nextOwnerMask)
        buffer.putInt(everyoneMask)
        buffer.putInt(saleType)
        buffer.putInt(salePrice)
        
        statement.bindBlob(18, buffer.array())
    }

    private fun bindUUID(statement: SQLiteStatement, startIndex: Int, uuid: UUID?) {
        if (uuid != null) {
            statement.bindLong(startIndex, uuid.mostSignificantBits)
            statement.bindLong(startIndex + 1, uuid.leastSignificantBits)
        } else {
            statement.bindLong(startIndex, 0)
            statement.bindLong(startIndex + 1, 0)
        }
    }
    
    fun getCreateTableStatements(): Array<String> {
        return arrayOf(
            "DROP TABLE IF EXISTS Entries;",
            "CREATE TABLE Entries (_id INTEGER PRIMARY KEY,parent_id BIGINT,uuid_high BIGINT,uuid_low BIGINT,parentUUID_high BIGINT,parentUUID_low BIGINT,name TEXT,isFolder BOOLEAN,typeDefault INTEGER,version INTEGER,sessionID_high BIGINT,sessionID_low BIGINT,fetchFailed BOOLEAN,description TEXT,flags INTEGER,invType INTEGER,assetType INTEGER,creationDate INTEGER,_blobField BLOB);",
            "CREATE INDEX Entries_parent_id ON Entries (parent_id);",
            "CREATE INDEX Entries_uuid ON Entries (uuid_high, uuid_low);"
        )
    }
    
    fun getInsertQuery(): String = INSERT_QUERY
}
