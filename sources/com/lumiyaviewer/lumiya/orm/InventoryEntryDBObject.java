// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.orm;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.Parcel;
import android.os.Parcelable;
import java.nio.ByteBuffer;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.orm:
//            DBObject, DBHandle

public class InventoryEntryDBObject extends DBObject
    implements Parcelable
{

    public static final android.os.Parcelable.Creator CREATOR = new android.os.Parcelable.Creator() {

        public InventoryEntryDBObject createFromParcel(Parcel parcel)
        {
            return new InventoryEntryDBObject(parcel);
        }

        public volatile Object createFromParcel(Parcel parcel)
        {
            return createFromParcel(parcel);
        }

        public InventoryEntryDBObject[] newArray(int i)
        {
            return new InventoryEntryDBObject[i];
        }

        public volatile Object[] newArray(int i)
        {
            return newArray(i);
        }

    };
    protected static final String fieldNames[] = {
        "_id", "parent_id", "uuid_high", "uuid_low", "parentUUID_high", "parentUUID_low", "name", "isFolder", "typeDefault", "version", 
        "sessionID_high", "sessionID_low", "fetchFailed", "description", "flags", "invType", "assetType", "creationDate", "_blobField"
    };
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

    public InventoryEntryDBObject()
    {
    }

    public InventoryEntryDBObject(Cursor cursor)
    {
        super(cursor);
    }

    public InventoryEntryDBObject(SQLiteDatabase sqlitedatabase, long l)
        throws DBObject.DatabaseBindingException
    {
        super(sqlitedatabase, l);
    }

    protected InventoryEntryDBObject(Parcel parcel)
    {
        boolean flag1 = true;
        super();
        _id = parcel.readLong();
        parent_id = parcel.readLong();
        uuid = new UUID(parcel.readLong(), parcel.readLong());
        agentUUID = new UUID(parcel.readLong(), parcel.readLong());
        parentUUID = new UUID(parcel.readLong(), parcel.readLong());
        name = parcel.readString();
        boolean flag;
        if (parcel.readByte() != 0)
        {
            flag = true;
        } else
        {
            flag = false;
        }
        isFolder = flag;
        typeDefault = parcel.readInt();
        version = parcel.readInt();
        sessionID = new UUID(parcel.readLong(), parcel.readLong());
        if (parcel.readByte() != 0)
        {
            flag = true;
        } else
        {
            flag = false;
        }
        fetchFailed = flag;
        description = parcel.readString();
        flags = parcel.readInt();
        invType = parcel.readInt();
        assetType = parcel.readInt();
        assetUUID = new UUID(parcel.readLong(), parcel.readLong());
        creationDate = parcel.readInt();
        creatorUUID = new UUID(parcel.readLong(), parcel.readLong());
        ownerUUID = new UUID(parcel.readLong(), parcel.readLong());
        groupUUID = new UUID(parcel.readLong(), parcel.readLong());
        lastOwnerUUID = new UUID(parcel.readLong(), parcel.readLong());
        if (parcel.readByte() != 0)
        {
            flag = flag1;
        } else
        {
            flag = false;
        }
        isGroupOwned = flag;
        baseMask = parcel.readInt();
        groupMask = parcel.readInt();
        ownerMask = parcel.readInt();
        nextOwnerMask = parcel.readInt();
        everyoneMask = parcel.readInt();
        saleType = parcel.readInt();
        salePrice = parcel.readInt();
    }

    public static String[] getCreateTableStatements()
    {
        return (new String[] {
            "DROP TABLE IF EXISTS Entries;", "CREATE TABLE Entries (_id INTEGER PRIMARY KEY,parent_id BIGINT,uuid_high BIGINT,uuid_low BIGINT,parentUUID_high BIGINT,parentUUID_low BIGINT,name TEXT,isFolder BOOLEAN,typeDefault INTEGER,version INTEGER,sessionID_high BIGINT,sessionID_low BIGINT,fetchFailed BOOLEAN,description TEXT,flags INTEGER,invType INTEGER,assetType INTEGER,creationDate INTEGER,_blobField BLOB);", "CREATE INDEX Entries_parent_id ON Entries (parent_id);", "CREATE INDEX Entries_uuid ON Entries (uuid_high, uuid_low);"
        });
    }

    public static Cursor query(SQLiteDatabase sqlitedatabase, String s, String as[], String s1)
        throws DBObject.DatabaseBindingException
    {
        if (sqlitedatabase == null)
        {
            throw new DBObject.DatabaseBindingException("Database not opened");
        } else
        {
            return sqlitedatabase.query("Entries", fieldNames, s, as, null, null, s1);
        }
    }

    public static Cursor query(DBHandle dbhandle, String s, String as[], String s1)
        throws DBObject.DatabaseBindingException
    {
        if (dbhandle == null)
        {
            throw new DBObject.DatabaseBindingException("Database not opened");
        } else
        {
            return dbhandle.getDB().queryWithFactory(dbhandle, false, "Entries", fieldNames, s, as, null, null, s1, null);
        }
    }

    public void bindInsertOrUpdate(SQLiteStatement sqlitestatement)
    {
        boolean flag = true;
        sqlitestatement.bindLong(1, parent_id);
        ByteBuffer bytebuffer;
        int i;
        if (uuid != null)
        {
            sqlitestatement.bindLong(2, uuid.getMostSignificantBits());
            sqlitestatement.bindLong(3, uuid.getLeastSignificantBits());
        } else
        {
            sqlitestatement.bindLong(2, 0L);
            sqlitestatement.bindLong(3, 0L);
        }
        if (parentUUID != null)
        {
            sqlitestatement.bindLong(4, parentUUID.getMostSignificantBits());
            sqlitestatement.bindLong(5, parentUUID.getLeastSignificantBits());
        } else
        {
            sqlitestatement.bindLong(4, 0L);
            sqlitestatement.bindLong(5, 0L);
        }
        if (name != null)
        {
            sqlitestatement.bindString(6, name);
        } else
        {
            sqlitestatement.bindNull(6);
        }
        if (isFolder)
        {
            i = 1;
        } else
        {
            i = 0;
        }
        sqlitestatement.bindLong(7, i);
        sqlitestatement.bindLong(8, typeDefault);
        sqlitestatement.bindLong(9, version);
        if (sessionID != null)
        {
            sqlitestatement.bindLong(10, sessionID.getMostSignificantBits());
            sqlitestatement.bindLong(11, sessionID.getLeastSignificantBits());
        } else
        {
            sqlitestatement.bindLong(10, 0L);
            sqlitestatement.bindLong(11, 0L);
        }
        if (fetchFailed)
        {
            i = 1;
        } else
        {
            i = 0;
        }
        sqlitestatement.bindLong(12, i);
        if (description != null)
        {
            sqlitestatement.bindString(13, description);
        } else
        {
            sqlitestatement.bindNull(13);
        }
        sqlitestatement.bindLong(14, flags);
        sqlitestatement.bindLong(15, invType);
        sqlitestatement.bindLong(16, assetType);
        sqlitestatement.bindLong(17, creationDate);
        bytebuffer = ByteBuffer.wrap(new byte[125]);
        if (agentUUID != null)
        {
            bytebuffer.putLong(agentUUID.getMostSignificantBits());
            bytebuffer.putLong(agentUUID.getLeastSignificantBits());
        } else
        {
            bytebuffer.putLong(0L);
            bytebuffer.putLong(0L);
        }
        if (assetUUID != null)
        {
            bytebuffer.putLong(assetUUID.getMostSignificantBits());
            bytebuffer.putLong(assetUUID.getLeastSignificantBits());
        } else
        {
            bytebuffer.putLong(0L);
            bytebuffer.putLong(0L);
        }
        if (creatorUUID != null)
        {
            bytebuffer.putLong(creatorUUID.getMostSignificantBits());
            bytebuffer.putLong(creatorUUID.getLeastSignificantBits());
        } else
        {
            bytebuffer.putLong(0L);
            bytebuffer.putLong(0L);
        }
        if (ownerUUID != null)
        {
            bytebuffer.putLong(ownerUUID.getMostSignificantBits());
            bytebuffer.putLong(ownerUUID.getLeastSignificantBits());
        } else
        {
            bytebuffer.putLong(0L);
            bytebuffer.putLong(0L);
        }
        if (groupUUID != null)
        {
            bytebuffer.putLong(groupUUID.getMostSignificantBits());
            bytebuffer.putLong(groupUUID.getLeastSignificantBits());
        } else
        {
            bytebuffer.putLong(0L);
            bytebuffer.putLong(0L);
        }
        if (lastOwnerUUID != null)
        {
            bytebuffer.putLong(lastOwnerUUID.getMostSignificantBits());
            bytebuffer.putLong(lastOwnerUUID.getLeastSignificantBits());
        } else
        {
            bytebuffer.putLong(0L);
            bytebuffer.putLong(0L);
        }
        if (isGroupOwned)
        {
            i = ((flag) ? 1 : 0);
        } else
        {
            i = 0;
        }
        bytebuffer.put((byte)i);
        bytebuffer.putInt(baseMask);
        bytebuffer.putInt(groupMask);
        bytebuffer.putInt(ownerMask);
        bytebuffer.putInt(nextOwnerMask);
        bytebuffer.putInt(everyoneMask);
        bytebuffer.putInt(saleType);
        bytebuffer.putInt(salePrice);
        sqlitestatement.bindBlob(18, bytebuffer.array());
    }

    public int describeContents()
    {
        return 0;
    }

    public ContentValues getContentValues()
    {
        ContentValues contentvalues = new ContentValues();
        contentvalues.put("parent_id", Long.valueOf(parent_id));
        ByteBuffer bytebuffer;
        int i;
        if (uuid != null)
        {
            contentvalues.put("uuid_high", Long.valueOf(uuid.getMostSignificantBits()));
            contentvalues.put("uuid_low", Long.valueOf(uuid.getLeastSignificantBits()));
        } else
        {
            contentvalues.put("uuid_high", Long.valueOf(0L));
            contentvalues.put("uuid_low", Long.valueOf(0L));
        }
        if (parentUUID != null)
        {
            contentvalues.put("parentUUID_high", Long.valueOf(parentUUID.getMostSignificantBits()));
            contentvalues.put("parentUUID_low", Long.valueOf(parentUUID.getLeastSignificantBits()));
        } else
        {
            contentvalues.put("parentUUID_high", Long.valueOf(0L));
            contentvalues.put("parentUUID_low", Long.valueOf(0L));
        }
        contentvalues.put("name", name);
        contentvalues.put("isFolder", Boolean.valueOf(isFolder));
        contentvalues.put("typeDefault", Integer.valueOf(typeDefault));
        contentvalues.put("version", Integer.valueOf(version));
        if (sessionID != null)
        {
            contentvalues.put("sessionID_high", Long.valueOf(sessionID.getMostSignificantBits()));
            contentvalues.put("sessionID_low", Long.valueOf(sessionID.getLeastSignificantBits()));
        } else
        {
            contentvalues.put("sessionID_high", Long.valueOf(0L));
            contentvalues.put("sessionID_low", Long.valueOf(0L));
        }
        contentvalues.put("fetchFailed", Boolean.valueOf(fetchFailed));
        contentvalues.put("description", description);
        contentvalues.put("flags", Integer.valueOf(flags));
        contentvalues.put("invType", Integer.valueOf(invType));
        contentvalues.put("assetType", Integer.valueOf(assetType));
        contentvalues.put("creationDate", Integer.valueOf(creationDate));
        bytebuffer = ByteBuffer.wrap(new byte[125]);
        if (agentUUID != null)
        {
            bytebuffer.putLong(agentUUID.getMostSignificantBits());
            bytebuffer.putLong(agentUUID.getLeastSignificantBits());
        } else
        {
            bytebuffer.putLong(0L);
            bytebuffer.putLong(0L);
        }
        if (assetUUID != null)
        {
            bytebuffer.putLong(assetUUID.getMostSignificantBits());
            bytebuffer.putLong(assetUUID.getLeastSignificantBits());
        } else
        {
            bytebuffer.putLong(0L);
            bytebuffer.putLong(0L);
        }
        if (creatorUUID != null)
        {
            bytebuffer.putLong(creatorUUID.getMostSignificantBits());
            bytebuffer.putLong(creatorUUID.getLeastSignificantBits());
        } else
        {
            bytebuffer.putLong(0L);
            bytebuffer.putLong(0L);
        }
        if (ownerUUID != null)
        {
            bytebuffer.putLong(ownerUUID.getMostSignificantBits());
            bytebuffer.putLong(ownerUUID.getLeastSignificantBits());
        } else
        {
            bytebuffer.putLong(0L);
            bytebuffer.putLong(0L);
        }
        if (groupUUID != null)
        {
            bytebuffer.putLong(groupUUID.getMostSignificantBits());
            bytebuffer.putLong(groupUUID.getLeastSignificantBits());
        } else
        {
            bytebuffer.putLong(0L);
            bytebuffer.putLong(0L);
        }
        if (lastOwnerUUID != null)
        {
            bytebuffer.putLong(lastOwnerUUID.getMostSignificantBits());
            bytebuffer.putLong(lastOwnerUUID.getLeastSignificantBits());
        } else
        {
            bytebuffer.putLong(0L);
            bytebuffer.putLong(0L);
        }
        if (isGroupOwned)
        {
            i = 1;
        } else
        {
            i = 0;
        }
        bytebuffer.put((byte)i);
        bytebuffer.putInt(baseMask);
        bytebuffer.putInt(groupMask);
        bytebuffer.putInt(ownerMask);
        bytebuffer.putInt(nextOwnerMask);
        bytebuffer.putInt(everyoneMask);
        bytebuffer.putInt(saleType);
        bytebuffer.putInt(salePrice);
        contentvalues.put("_blobField", bytebuffer.array());
        return contentvalues;
    }

    public String[] getFieldNames()
    {
        return fieldNames;
    }

    public String getTableName()
    {
        return "Entries";
    }

    public void loadFromCursor(Cursor cursor)
    {
        boolean flag1 = true;
        _id = cursor.getLong(0);
        parent_id = cursor.getLong(1);
        uuid = new UUID(cursor.getLong(2), cursor.getLong(3));
        parentUUID = new UUID(cursor.getLong(4), cursor.getLong(5));
        name = cursor.getString(6);
        boolean flag;
        if (cursor.getInt(7) != 0)
        {
            flag = true;
        } else
        {
            flag = false;
        }
        isFolder = flag;
        typeDefault = cursor.getInt(8);
        version = cursor.getInt(9);
        sessionID = new UUID(cursor.getLong(10), cursor.getLong(11));
        if (cursor.getInt(12) != 0)
        {
            flag = true;
        } else
        {
            flag = false;
        }
        fetchFailed = flag;
        description = cursor.getString(13);
        flags = cursor.getInt(14);
        invType = cursor.getInt(15);
        assetType = cursor.getInt(16);
        creationDate = cursor.getInt(17);
        cursor = ByteBuffer.wrap(cursor.getBlob(18));
        agentUUID = new UUID(cursor.getLong(), cursor.getLong());
        assetUUID = new UUID(cursor.getLong(), cursor.getLong());
        creatorUUID = new UUID(cursor.getLong(), cursor.getLong());
        ownerUUID = new UUID(cursor.getLong(), cursor.getLong());
        groupUUID = new UUID(cursor.getLong(), cursor.getLong());
        lastOwnerUUID = new UUID(cursor.getLong(), cursor.getLong());
        if (cursor.get() != 0)
        {
            flag = flag1;
        } else
        {
            flag = false;
        }
        isGroupOwned = flag;
        baseMask = cursor.getInt();
        groupMask = cursor.getInt();
        ownerMask = cursor.getInt();
        nextOwnerMask = cursor.getInt();
        everyoneMask = cursor.getInt();
        saleType = cursor.getInt();
        salePrice = cursor.getInt();
    }

    public void writeToParcel(Parcel parcel, int i)
    {
        boolean flag = true;
        parcel.writeLong(_id);
        parcel.writeLong(parent_id);
        int j;
        if (uuid != null)
        {
            parcel.writeLong(uuid.getMostSignificantBits());
            parcel.writeLong(uuid.getLeastSignificantBits());
        } else
        {
            parcel.writeLong(0L);
            parcel.writeLong(0L);
        }
        if (agentUUID != null)
        {
            parcel.writeLong(agentUUID.getMostSignificantBits());
            parcel.writeLong(agentUUID.getLeastSignificantBits());
        } else
        {
            parcel.writeLong(0L);
            parcel.writeLong(0L);
        }
        if (parentUUID != null)
        {
            parcel.writeLong(parentUUID.getMostSignificantBits());
            parcel.writeLong(parentUUID.getLeastSignificantBits());
        } else
        {
            parcel.writeLong(0L);
            parcel.writeLong(0L);
        }
        parcel.writeString(name);
        if (isFolder)
        {
            j = 1;
        } else
        {
            j = 0;
        }
        parcel.writeByte((byte)j);
        parcel.writeInt(typeDefault);
        parcel.writeInt(version);
        if (sessionID != null)
        {
            parcel.writeLong(sessionID.getMostSignificantBits());
            parcel.writeLong(sessionID.getLeastSignificantBits());
        } else
        {
            parcel.writeLong(0L);
            parcel.writeLong(0L);
        }
        if (fetchFailed)
        {
            j = 1;
        } else
        {
            j = 0;
        }
        parcel.writeByte((byte)j);
        parcel.writeString(description);
        parcel.writeInt(i);
        parcel.writeInt(invType);
        parcel.writeInt(assetType);
        if (assetUUID != null)
        {
            parcel.writeLong(assetUUID.getMostSignificantBits());
            parcel.writeLong(assetUUID.getLeastSignificantBits());
        } else
        {
            parcel.writeLong(0L);
            parcel.writeLong(0L);
        }
        parcel.writeInt(creationDate);
        if (creatorUUID != null)
        {
            parcel.writeLong(creatorUUID.getMostSignificantBits());
            parcel.writeLong(creatorUUID.getLeastSignificantBits());
        } else
        {
            parcel.writeLong(0L);
            parcel.writeLong(0L);
        }
        if (ownerUUID != null)
        {
            parcel.writeLong(ownerUUID.getMostSignificantBits());
            parcel.writeLong(ownerUUID.getLeastSignificantBits());
        } else
        {
            parcel.writeLong(0L);
            parcel.writeLong(0L);
        }
        if (groupUUID != null)
        {
            parcel.writeLong(groupUUID.getMostSignificantBits());
            parcel.writeLong(groupUUID.getLeastSignificantBits());
        } else
        {
            parcel.writeLong(0L);
            parcel.writeLong(0L);
        }
        if (lastOwnerUUID != null)
        {
            parcel.writeLong(lastOwnerUUID.getMostSignificantBits());
            parcel.writeLong(lastOwnerUUID.getLeastSignificantBits());
        } else
        {
            parcel.writeLong(0L);
            parcel.writeLong(0L);
        }
        if (isGroupOwned)
        {
            i = ((flag) ? 1 : 0);
        } else
        {
            i = 0;
        }
        parcel.writeByte((byte)i);
        parcel.writeInt(baseMask);
        parcel.writeInt(groupMask);
        parcel.writeInt(ownerMask);
        parcel.writeInt(nextOwnerMask);
        parcel.writeInt(everyoneMask);
        parcel.writeInt(saleType);
        parcel.writeInt(salePrice);
    }

}
