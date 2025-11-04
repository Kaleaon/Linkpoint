// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.orm;

import android.content.ContentValues;
import java.nio.ByteBuffer;
import android.database.sqlite.SQLiteStatement;
import android.database.sqlite.SQLiteDatabase$CursorFactory;
import android.database.sqlite.SQLiteDatabase;
import android.database.Cursor;
import android.os.Parcel;
import java.util.UUID;
import android.os.Parcelable$Creator;
import android.os.Parcelable;

public class InventoryEntryDBObject extends DBObject implements Parcelable
{
    public static final Parcelable$Creator<InventoryEntryDBObject> CREATOR;
    protected static final String[] fieldNames;
    public static final String insertQuery = "INSERT INTO Entries (parent_id,uuid_high,uuid_low,parentUUID_high,parentUUID_low,name,isFolder,typeDefault,version,sessionID_high,sessionID_low,fetchFailed,description,flags,invType,assetType,creationDate,_blobField) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?);";
    public static final int insertUpdateParamCount = 18;
    public static final String tableName = "Entries";
    public static final String updateQuery = "UPDATE Entries SET parent_id=?,uuid_high=?,uuid_low=?,parentUUID_high=?,parentUUID_low=?,name=?,isFolder=?,typeDefault=?,version=?,sessionID_high=?,sessionID_low=?,fetchFailed=?,description=?,flags=?,invType=?,assetType=?,creationDate=?,_blobField=?";
    public UUID agentUUID;
    public int assetType;
    public UUID assetUUID;
    public int baseMask;
    public int creationDate;
    public UUID creatorUUID;
    public String description;
    public int everyoneMask;
    public boolean fetchFailed;
    public int flags;
    public int groupMask;
    public UUID groupUUID;
    public int invType;
    public boolean isFolder;
    public boolean isGroupOwned;
    public UUID lastOwnerUUID;
    public String name;
    public int nextOwnerMask;
    public int ownerMask;
    public UUID ownerUUID;
    public UUID parentUUID;
    public long parent_id;
    public int salePrice;
    public int saleType;
    public UUID sessionID;
    public int typeDefault;
    public UUID uuid;
    public int version;
    
    static {
        fieldNames = new String[] { "_id", "parent_id", "uuid_high", "uuid_low", "parentUUID_high", "parentUUID_low", "name", "isFolder", "typeDefault", "version", "sessionID_high", "sessionID_low", "fetchFailed", "description", "flags", "invType", "assetType", "creationDate", "_blobField" };
        CREATOR = (Parcelable$Creator)new Parcelable$Creator<InventoryEntryDBObject>() {
            public InventoryEntryDBObject createFromParcel(final Parcel parcel) {
                return new InventoryEntryDBObject(parcel);
            }
            
            public InventoryEntryDBObject[] newArray(final int n) {
                return new InventoryEntryDBObject[n];
            }
        };
    }
    
    public InventoryEntryDBObject() {
    }
    
    public InventoryEntryDBObject(final Cursor cursor) {
        super(cursor);
    }
    
    public InventoryEntryDBObject(final SQLiteDatabase sqLiteDatabase, final long n) throws DatabaseBindingException {
        super(sqLiteDatabase, n);
    }
    
    protected InventoryEntryDBObject(final Parcel parcel) {
        final boolean b = true;
        this._id = parcel.readLong();
        this.parent_id = parcel.readLong();
        this.uuid = new UUID(parcel.readLong(), parcel.readLong());
        this.agentUUID = new UUID(parcel.readLong(), parcel.readLong());
        this.parentUUID = new UUID(parcel.readLong(), parcel.readLong());
        this.name = parcel.readString();
        this.isFolder = (parcel.readByte() != 0);
        this.typeDefault = parcel.readInt();
        this.version = parcel.readInt();
        this.sessionID = new UUID(parcel.readLong(), parcel.readLong());
        this.fetchFailed = (parcel.readByte() != 0);
        this.description = parcel.readString();
        this.flags = parcel.readInt();
        this.invType = parcel.readInt();
        this.assetType = parcel.readInt();
        this.assetUUID = new UUID(parcel.readLong(), parcel.readLong());
        this.creationDate = parcel.readInt();
        this.creatorUUID = new UUID(parcel.readLong(), parcel.readLong());
        this.ownerUUID = new UUID(parcel.readLong(), parcel.readLong());
        this.groupUUID = new UUID(parcel.readLong(), parcel.readLong());
        this.lastOwnerUUID = new UUID(parcel.readLong(), parcel.readLong());
        this.isGroupOwned = (parcel.readByte() != 0 && b);
        this.baseMask = parcel.readInt();
        this.groupMask = parcel.readInt();
        this.ownerMask = parcel.readInt();
        this.nextOwnerMask = parcel.readInt();
        this.everyoneMask = parcel.readInt();
        this.saleType = parcel.readInt();
        this.salePrice = parcel.readInt();
    }
    
    public static String[] getCreateTableStatements() {
        return new String[] { "DROP TABLE IF EXISTS Entries;", "CREATE TABLE Entries (_id INTEGER PRIMARY KEY,parent_id BIGINT,uuid_high BIGINT,uuid_low BIGINT,parentUUID_high BIGINT,parentUUID_low BIGINT,name TEXT,isFolder BOOLEAN,typeDefault INTEGER,version INTEGER,sessionID_high BIGINT,sessionID_low BIGINT,fetchFailed BOOLEAN,description TEXT,flags INTEGER,invType INTEGER,assetType INTEGER,creationDate INTEGER,_blobField BLOB);", "CREATE INDEX Entries_parent_id ON Entries (parent_id);", "CREATE INDEX Entries_uuid ON Entries (uuid_high, uuid_low);" };
    }
    
    public static Cursor query(final SQLiteDatabase sqLiteDatabase, final String s, final String[] array, final String s2) throws DatabaseBindingException {
        if (sqLiteDatabase == null) {
            throw new DatabaseBindingException("Database not opened");
        }
        return sqLiteDatabase.query("Entries", InventoryEntryDBObject.fieldNames, s, array, (String)null, (String)null, s2);
    }
    
    public static Cursor query(final DBHandle dbHandle, final String s, final String[] array, final String s2) throws DatabaseBindingException {
        if (dbHandle == null) {
            throw new DatabaseBindingException("Database not opened");
        }
        return dbHandle.getDB().queryWithFactory((SQLiteDatabase$CursorFactory)dbHandle, false, "Entries", InventoryEntryDBObject.fieldNames, s, array, (String)null, (String)null, s2, (String)null);
    }
    
    @Override
    public void bindInsertOrUpdate(final SQLiteStatement sqLiteStatement) {
        final int n = 1;
        sqLiteStatement.bindLong(1, this.parent_id);
        if (this.uuid != null) {
            sqLiteStatement.bindLong(2, this.uuid.getMostSignificantBits());
            sqLiteStatement.bindLong(3, this.uuid.getLeastSignificantBits());
        }
        else {
            sqLiteStatement.bindLong(2, 0L);
            sqLiteStatement.bindLong(3, 0L);
        }
        if (this.parentUUID != null) {
            sqLiteStatement.bindLong(4, this.parentUUID.getMostSignificantBits());
            sqLiteStatement.bindLong(5, this.parentUUID.getLeastSignificantBits());
        }
        else {
            sqLiteStatement.bindLong(4, 0L);
            sqLiteStatement.bindLong(5, 0L);
        }
        if (this.name != null) {
            sqLiteStatement.bindString(6, this.name);
        }
        else {
            sqLiteStatement.bindNull(6);
        }
        int n2;
        if (this.isFolder) {
            n2 = 1;
        }
        else {
            n2 = 0;
        }
        sqLiteStatement.bindLong(7, (long)n2);
        sqLiteStatement.bindLong(8, (long)this.typeDefault);
        sqLiteStatement.bindLong(9, (long)this.version);
        if (this.sessionID != null) {
            sqLiteStatement.bindLong(10, this.sessionID.getMostSignificantBits());
            sqLiteStatement.bindLong(11, this.sessionID.getLeastSignificantBits());
        }
        else {
            sqLiteStatement.bindLong(10, 0L);
            sqLiteStatement.bindLong(11, 0L);
        }
        int n3;
        if (this.fetchFailed) {
            n3 = 1;
        }
        else {
            n3 = 0;
        }
        sqLiteStatement.bindLong(12, (long)n3);
        if (this.description != null) {
            sqLiteStatement.bindString(13, this.description);
        }
        else {
            sqLiteStatement.bindNull(13);
        }
        sqLiteStatement.bindLong(14, (long)this.flags);
        sqLiteStatement.bindLong(15, (long)this.invType);
        sqLiteStatement.bindLong(16, (long)this.assetType);
        sqLiteStatement.bindLong(17, (long)this.creationDate);
        final ByteBuffer wrap = ByteBuffer.wrap(new byte[125]);
        if (this.agentUUID != null) {
            wrap.putLong(this.agentUUID.getMostSignificantBits());
            wrap.putLong(this.agentUUID.getLeastSignificantBits());
        }
        else {
            wrap.putLong(0L);
            wrap.putLong(0L);
        }
        if (this.assetUUID != null) {
            wrap.putLong(this.assetUUID.getMostSignificantBits());
            wrap.putLong(this.assetUUID.getLeastSignificantBits());
        }
        else {
            wrap.putLong(0L);
            wrap.putLong(0L);
        }
        if (this.creatorUUID != null) {
            wrap.putLong(this.creatorUUID.getMostSignificantBits());
            wrap.putLong(this.creatorUUID.getLeastSignificantBits());
        }
        else {
            wrap.putLong(0L);
            wrap.putLong(0L);
        }
        if (this.ownerUUID != null) {
            wrap.putLong(this.ownerUUID.getMostSignificantBits());
            wrap.putLong(this.ownerUUID.getLeastSignificantBits());
        }
        else {
            wrap.putLong(0L);
            wrap.putLong(0L);
        }
        if (this.groupUUID != null) {
            wrap.putLong(this.groupUUID.getMostSignificantBits());
            wrap.putLong(this.groupUUID.getLeastSignificantBits());
        }
        else {
            wrap.putLong(0L);
            wrap.putLong(0L);
        }
        if (this.lastOwnerUUID != null) {
            wrap.putLong(this.lastOwnerUUID.getMostSignificantBits());
            wrap.putLong(this.lastOwnerUUID.getLeastSignificantBits());
        }
        else {
            wrap.putLong(0L);
            wrap.putLong(0L);
        }
        int n4;
        if (this.isGroupOwned) {
            n4 = n;
        }
        else {
            n4 = 0;
        }
        wrap.put((byte)n4);
        wrap.putInt(this.baseMask);
        wrap.putInt(this.groupMask);
        wrap.putInt(this.ownerMask);
        wrap.putInt(this.nextOwnerMask);
        wrap.putInt(this.everyoneMask);
        wrap.putInt(this.saleType);
        wrap.putInt(this.salePrice);
        sqLiteStatement.bindBlob(18, wrap.array());
    }
    
    public int describeContents() {
        return 0;
    }
    
    @Override
    public ContentValues getContentValues() {
        final ContentValues contentValues = new ContentValues();
        contentValues.put("parent_id", Long.valueOf(this.parent_id));
        if (this.uuid != null) {
            contentValues.put("uuid_high", Long.valueOf(this.uuid.getMostSignificantBits()));
            contentValues.put("uuid_low", Long.valueOf(this.uuid.getLeastSignificantBits()));
        }
        else {
            contentValues.put("uuid_high", Long.valueOf(0L));
            contentValues.put("uuid_low", Long.valueOf(0L));
        }
        if (this.parentUUID != null) {
            contentValues.put("parentUUID_high", Long.valueOf(this.parentUUID.getMostSignificantBits()));
            contentValues.put("parentUUID_low", Long.valueOf(this.parentUUID.getLeastSignificantBits()));
        }
        else {
            contentValues.put("parentUUID_high", Long.valueOf(0L));
            contentValues.put("parentUUID_low", Long.valueOf(0L));
        }
        contentValues.put("name", this.name);
        contentValues.put("isFolder", Boolean.valueOf(this.isFolder));
        contentValues.put("typeDefault", Integer.valueOf(this.typeDefault));
        contentValues.put("version", Integer.valueOf(this.version));
        if (this.sessionID != null) {
            contentValues.put("sessionID_high", Long.valueOf(this.sessionID.getMostSignificantBits()));
            contentValues.put("sessionID_low", Long.valueOf(this.sessionID.getLeastSignificantBits()));
        }
        else {
            contentValues.put("sessionID_high", Long.valueOf(0L));
            contentValues.put("sessionID_low", Long.valueOf(0L));
        }
        contentValues.put("fetchFailed", Boolean.valueOf(this.fetchFailed));
        contentValues.put("description", this.description);
        contentValues.put("flags", Integer.valueOf(this.flags));
        contentValues.put("invType", Integer.valueOf(this.invType));
        contentValues.put("assetType", Integer.valueOf(this.assetType));
        contentValues.put("creationDate", Integer.valueOf(this.creationDate));
        final ByteBuffer wrap = ByteBuffer.wrap(new byte[125]);
        if (this.agentUUID != null) {
            wrap.putLong(this.agentUUID.getMostSignificantBits());
            wrap.putLong(this.agentUUID.getLeastSignificantBits());
        }
        else {
            wrap.putLong(0L);
            wrap.putLong(0L);
        }
        if (this.assetUUID != null) {
            wrap.putLong(this.assetUUID.getMostSignificantBits());
            wrap.putLong(this.assetUUID.getLeastSignificantBits());
        }
        else {
            wrap.putLong(0L);
            wrap.putLong(0L);
        }
        if (this.creatorUUID != null) {
            wrap.putLong(this.creatorUUID.getMostSignificantBits());
            wrap.putLong(this.creatorUUID.getLeastSignificantBits());
        }
        else {
            wrap.putLong(0L);
            wrap.putLong(0L);
        }
        if (this.ownerUUID != null) {
            wrap.putLong(this.ownerUUID.getMostSignificantBits());
            wrap.putLong(this.ownerUUID.getLeastSignificantBits());
        }
        else {
            wrap.putLong(0L);
            wrap.putLong(0L);
        }
        if (this.groupUUID != null) {
            wrap.putLong(this.groupUUID.getMostSignificantBits());
            wrap.putLong(this.groupUUID.getLeastSignificantBits());
        }
        else {
            wrap.putLong(0L);
            wrap.putLong(0L);
        }
        if (this.lastOwnerUUID != null) {
            wrap.putLong(this.lastOwnerUUID.getMostSignificantBits());
            wrap.putLong(this.lastOwnerUUID.getLeastSignificantBits());
        }
        else {
            wrap.putLong(0L);
            wrap.putLong(0L);
        }
        boolean b;
        if (this.isGroupOwned) {
            b = true;
        }
        else {
            b = false;
        }
        wrap.put((byte)(b ? 1 : 0));
        wrap.putInt(this.baseMask);
        wrap.putInt(this.groupMask);
        wrap.putInt(this.ownerMask);
        wrap.putInt(this.nextOwnerMask);
        wrap.putInt(this.everyoneMask);
        wrap.putInt(this.saleType);
        wrap.putInt(this.salePrice);
        contentValues.put("_blobField", wrap.array());
        return contentValues;
    }
    
    public String[] getFieldNames() {
        return InventoryEntryDBObject.fieldNames;
    }
    
    public String getTableName() {
        return "Entries";
    }
    
    @Override
    public void loadFromCursor(final Cursor cursor) {
        final boolean b = true;
        this._id = cursor.getLong(0);
        this.parent_id = cursor.getLong(1);
        this.uuid = new UUID(cursor.getLong(2), cursor.getLong(3));
        this.parentUUID = new UUID(cursor.getLong(4), cursor.getLong(5));
        this.name = cursor.getString(6);
        this.isFolder = (cursor.getInt(7) != 0);
        this.typeDefault = cursor.getInt(8);
        this.version = cursor.getInt(9);
        this.sessionID = new UUID(cursor.getLong(10), cursor.getLong(11));
        this.fetchFailed = (cursor.getInt(12) != 0);
        this.description = cursor.getString(13);
        this.flags = cursor.getInt(14);
        this.invType = cursor.getInt(15);
        this.assetType = cursor.getInt(16);
        this.creationDate = cursor.getInt(17);
        final ByteBuffer wrap = ByteBuffer.wrap(cursor.getBlob(18));
        this.agentUUID = new UUID(wrap.getLong(), wrap.getLong());
        this.assetUUID = new UUID(wrap.getLong(), wrap.getLong());
        this.creatorUUID = new UUID(wrap.getLong(), wrap.getLong());
        this.ownerUUID = new UUID(wrap.getLong(), wrap.getLong());
        this.groupUUID = new UUID(wrap.getLong(), wrap.getLong());
        this.lastOwnerUUID = new UUID(wrap.getLong(), wrap.getLong());
        this.isGroupOwned = (wrap.get() != 0 && b);
        this.baseMask = wrap.getInt();
        this.groupMask = wrap.getInt();
        this.ownerMask = wrap.getInt();
        this.nextOwnerMask = wrap.getInt();
        this.everyoneMask = wrap.getInt();
        this.saleType = wrap.getInt();
        this.salePrice = wrap.getInt();
    }
    
    public void writeToParcel(final Parcel parcel, int n) {
        final int n2 = 1;
        parcel.writeLong(this._id);
        parcel.writeLong(this.parent_id);
        if (this.uuid != null) {
            parcel.writeLong(this.uuid.getMostSignificantBits());
            parcel.writeLong(this.uuid.getLeastSignificantBits());
        }
        else {
            parcel.writeLong(0L);
            parcel.writeLong(0L);
        }
        if (this.agentUUID != null) {
            parcel.writeLong(this.agentUUID.getMostSignificantBits());
            parcel.writeLong(this.agentUUID.getLeastSignificantBits());
        }
        else {
            parcel.writeLong(0L);
            parcel.writeLong(0L);
        }
        if (this.parentUUID != null) {
            parcel.writeLong(this.parentUUID.getMostSignificantBits());
            parcel.writeLong(this.parentUUID.getLeastSignificantBits());
        }
        else {
            parcel.writeLong(0L);
            parcel.writeLong(0L);
        }
        parcel.writeString(this.name);
        int n3;
        if (this.isFolder) {
            n3 = 1;
        }
        else {
            n3 = 0;
        }
        parcel.writeByte((byte)n3);
        parcel.writeInt(this.typeDefault);
        parcel.writeInt(this.version);
        if (this.sessionID != null) {
            parcel.writeLong(this.sessionID.getMostSignificantBits());
            parcel.writeLong(this.sessionID.getLeastSignificantBits());
        }
        else {
            parcel.writeLong(0L);
            parcel.writeLong(0L);
        }
        int n4;
        if (this.fetchFailed) {
            n4 = 1;
        }
        else {
            n4 = 0;
        }
        parcel.writeByte((byte)n4);
        parcel.writeString(this.description);
        parcel.writeInt(n);
        parcel.writeInt(this.invType);
        parcel.writeInt(this.assetType);
        if (this.assetUUID != null) {
            parcel.writeLong(this.assetUUID.getMostSignificantBits());
            parcel.writeLong(this.assetUUID.getLeastSignificantBits());
        }
        else {
            parcel.writeLong(0L);
            parcel.writeLong(0L);
        }
        parcel.writeInt(this.creationDate);
        if (this.creatorUUID != null) {
            parcel.writeLong(this.creatorUUID.getMostSignificantBits());
            parcel.writeLong(this.creatorUUID.getLeastSignificantBits());
        }
        else {
            parcel.writeLong(0L);
            parcel.writeLong(0L);
        }
        if (this.ownerUUID != null) {
            parcel.writeLong(this.ownerUUID.getMostSignificantBits());
            parcel.writeLong(this.ownerUUID.getLeastSignificantBits());
        }
        else {
            parcel.writeLong(0L);
            parcel.writeLong(0L);
        }
        if (this.groupUUID != null) {
            parcel.writeLong(this.groupUUID.getMostSignificantBits());
            parcel.writeLong(this.groupUUID.getLeastSignificantBits());
        }
        else {
            parcel.writeLong(0L);
            parcel.writeLong(0L);
        }
        if (this.lastOwnerUUID != null) {
            parcel.writeLong(this.lastOwnerUUID.getMostSignificantBits());
            parcel.writeLong(this.lastOwnerUUID.getLeastSignificantBits());
        }
        else {
            parcel.writeLong(0L);
            parcel.writeLong(0L);
        }
        if (this.isGroupOwned) {
            n = n2;
        }
        else {
            n = 0;
        }
        parcel.writeByte((byte)n);
        parcel.writeInt(this.baseMask);
        parcel.writeInt(this.groupMask);
        parcel.writeInt(this.ownerMask);
        parcel.writeInt(this.nextOwnerMask);
        parcel.writeInt(this.everyoneMask);
        parcel.writeInt(this.saleType);
        parcel.writeInt(this.salePrice);
    }
}
