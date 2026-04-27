// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.inventory;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteStatement;
import android.os.Parcel;
import android.os.Parcelable;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Table;
import com.lumiyaviewer.lumiya.orm.DBHandle;
import com.lumiyaviewer.lumiya.orm.InventoryEntryDBObject;
import com.lumiyaviewer.lumiya.slproto.assets.SLWearable;
import com.lumiyaviewer.lumiya.slproto.assets.SLWearableType;
import com.lumiyaviewer.lumiya.utils.SimpleStringParser;
import java.util.Iterator;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.inventory:
//            SLSaleType, SLAssetType, SLInventoryType

public class SLInventoryEntry extends InventoryEntryDBObject
    implements Parcelable
{

    public static final android.os.Parcelable.Creator CREATOR = new android.os.Parcelable.Creator() {

        public SLInventoryEntry createFromParcel(Parcel parcel)
        {
            return new SLInventoryEntry(parcel, null);
        }

        public volatile Object createFromParcel(Parcel parcel)
        {
            return createFromParcel(parcel);
        }

        public SLInventoryEntry[] newArray(int i)
        {
            return new SLInventoryEntry[i];
        }

        public volatile Object[] newArray(int i)
        {
            return newArray(i);
        }

    };
    private static final String DELIM_ANY = " \t\n";
    private static final String DELIM_EOL = "\n";
    public static final int FT_ANIMATION = 20;
    public static final int FT_BASIC_ROOT = 52;
    public static final int FT_BODYPART = 13;
    public static final int FT_CALLINGCARD = 2;
    public static final int FT_CLOTHING = 5;
    public static final int FT_CURRENT_OUTFIT = 46;
    public static final int FT_ENSEMBLE_END = 45;
    public static final int FT_ENSEMBLE_START = 26;
    public static final int FT_FAVORITE = 23;
    public static final int FT_GESTURE = 21;
    public static final int FT_INBOX = 50;
    public static final int FT_LANDMARK = 3;
    public static final int FT_LOST_AND_FOUND = 16;
    public static final int FT_LSL_TEXT = 10;
    public static final int FT_MESH = 49;
    public static final int FT_MY_OUTFITS = 48;
    public static final int FT_NOTECARD = 7;
    public static final int FT_OBJECT = 6;
    public static final int FT_OUTBOX = 51;
    public static final int FT_OUTFIT = 47;
    public static final int FT_ROOT_INVENTORY = 8;
    public static final int FT_SNAPSHOT_CATEGORY = 15;
    public static final int FT_SOUND = 1;
    public static final int FT_TEXTURE = 0;
    public static final int FT_TRASH = 14;
    public static final int II_FLAGS_WEARABLES_MASK = 255;
    public static final int IT_ANIMATION = 19;
    public static final int IT_ATTACHMENT = 17;
    public static final int IT_BODYPART = 13;
    public static final int IT_CALLINGCARD = 2;
    public static final int IT_CATEGORY = 8;
    public static final int IT_CLOTHING = 5;
    public static final int IT_COUNT = 21;
    public static final int IT_GESTURE = 20;
    public static final int IT_LANDMARK = 3;
    public static final int IT_LOST_AND_FOUND = 16;
    public static final int IT_LSL = 10;
    public static final int IT_LSL_BYTECODE = 11;
    public static final int IT_NOTECARD = 7;
    public static final int IT_OBJECT = 6;
    public static final int IT_ROOT_CATEGORY = 9;
    public static final int IT_SCRIPT = 4;
    public static final int IT_SNAPSHOT = 15;
    public static final int IT_SOUND = 1;
    public static final int IT_TEXTURE = 0;
    public static final int IT_TEXTURE_TGA = 12;
    public static final int IT_TRASH = 14;
    public static final int IT_WEARABLE = 18;
    public static final int PERM_COPY = 32768;
    public static final int PERM_FULL = 0x7fffffff;
    public static final int PERM_MODIFY = 16384;
    public static final int PERM_TRANSFER = 8192;

    public SLInventoryEntry()
    {
    }

    public SLInventoryEntry(Cursor cursor)
    {
        super(cursor);
    }

    public SLInventoryEntry(SQLiteDatabase sqlitedatabase, long l)
        throws com.lumiyaviewer.lumiya.orm.DBObject.DatabaseBindingException
    {
        super(sqlitedatabase, l);
    }

    private SLInventoryEntry(Parcel parcel)
    {
        super(parcel);
    }

    SLInventoryEntry(Parcel parcel, SLInventoryEntry slinventoryentry)
    {
        this(parcel);
    }

    public SLInventoryEntry(DBHandle dbhandle, long l)
        throws com.lumiyaviewer.lumiya.orm.DBObject.DatabaseBindingException
    {
        SQLiteDatabase sqlitedatabase = null;
        if (dbhandle != null)
        {
            sqlitedatabase = dbhandle.getDB();
        }
        super(sqlitedatabase, l);
    }

    public static SLInventoryEntry find(SQLiteDatabase sqlitedatabase, UUID uuid)
    {
        try
        {
            sqlitedatabase = sqlitedatabase.query("Entries", fieldNames, "uuid_low = ? AND uuid_high = ?", new String[] {
                Long.toString(uuid.getLeastSignificantBits()), Long.toString(uuid.getMostSignificantBits())
            }, null, null, null);
        }
        // Misplaced declaration of an exception variable
        catch (SQLiteDatabase sqlitedatabase)
        {
            sqlitedatabase.printStackTrace();
            return null;
        }
        if (sqlitedatabase.moveToFirst())
        {
            uuid = new SLInventoryEntry(sqlitedatabase);
            sqlitedatabase.close();
            return uuid;
        } else
        {
            sqlitedatabase.close();
            return null;
        }
    }

    public static SLInventoryEntry findOrCreate(SQLiteDatabase sqlitedatabase, UUID uuid)
        throws com.lumiyaviewer.lumiya.orm.DBObject.DatabaseBindingException
    {
        if (sqlitedatabase == null)
        {
            throw new com.lumiyaviewer.lumiya.orm.DBObject.DatabaseBindingException(com/lumiyaviewer/lumiya/slproto/inventory/SLInventoryEntry, "database is null");
        }
        if (uuid == null)
        {
            throw new com.lumiyaviewer.lumiya.orm.DBObject.DatabaseBindingException(com/lumiyaviewer/lumiya/slproto/inventory/SLInventoryEntry, "folderUUID is null");
        }
        sqlitedatabase = sqlitedatabase.query("Entries", fieldNames, "uuid_low = ? AND uuid_high = ?", new String[] {
            Long.toString(uuid.getLeastSignificantBits()), Long.toString(uuid.getMostSignificantBits())
        }, null, null, null);
        if (sqlitedatabase.moveToFirst())
        {
            uuid = new SLInventoryEntry(sqlitedatabase);
            sqlitedatabase.close();
            return uuid;
        } else
        {
            sqlitedatabase.close();
            sqlitedatabase = new SLInventoryEntry();
            sqlitedatabase.uuid = uuid;
            return sqlitedatabase;
        }
    }

    public static SLInventoryEntry findOrCreateForUpdate(SQLiteDatabase sqlitedatabase, UUID uuid)
        throws com.lumiyaviewer.lumiya.orm.DBObject.DatabaseBindingException
    {
        if (sqlitedatabase == null)
        {
            throw new com.lumiyaviewer.lumiya.orm.DBObject.DatabaseBindingException(com/lumiyaviewer/lumiya/slproto/inventory/SLInventoryEntry, "database is null");
        }
        if (uuid == null)
        {
            throw new com.lumiyaviewer.lumiya.orm.DBObject.DatabaseBindingException(com/lumiyaviewer/lumiya/slproto/inventory/SLInventoryEntry, "folderUUID is null");
        }
        String s = Long.toString(uuid.getLeastSignificantBits());
        String s1 = Long.toString(uuid.getMostSignificantBits());
        sqlitedatabase = sqlitedatabase.query("Entries", new String[] {
            "_id"
        }, "uuid_low = ? AND uuid_high = ?", new String[] {
            s, s1
        }, null, null, null);
        if (sqlitedatabase.moveToFirst())
        {
            SLInventoryEntry slinventoryentry = new SLInventoryEntry();
            slinventoryentry._id = sqlitedatabase.getLong(0);
            slinventoryentry.uuid = uuid;
            sqlitedatabase.close();
            return slinventoryentry;
        } else
        {
            sqlitedatabase.close();
            sqlitedatabase = new SLInventoryEntry();
            sqlitedatabase.uuid = uuid;
            return sqlitedatabase;
        }
    }

    private static int getDrawableResourceForType(int i)
    {
        switch (i)
        {
        default:
            return -1;

        case 3: // '\003'
            return 0x7f0200c3;

        case 0: // '\0'
        case 12: // '\f'
        case 15: // '\017'
            return 0x7f0200c2;

        case 1: // '\001'
            return 0x7f0200ca;

        case 2: // '\002'
            return 0x7f0200cd;

        case 4: // '\004'
        case 10: // '\n'
        case 11: // '\013'
            return 0x7f0200c8;

        case 5: // '\005'
        case 17: // '\021'
        case 18: // '\022'
            return 0x7f0200bf;

        case 6: // '\006'
            return 0x7f0200c6;

        case 7: // '\007'
            return 0x7f0200c5;

        case 8: // '\b'
        case 9: // '\t'
            return 0x7f0200c0;

        case 13: // '\r'
            return 0x7f0200c1;

        case 14: // '\016'
            return 0x7f0200cb;

        case 16: // '\020'
            return 0x7f0200c7;

        case 19: // '\023'
            return 0x7f0200bd;

        case 20: // '\024'
            return 0x7f0200c9;
        }
    }

    public static SQLiteStatement getInsertStatement(SQLiteDatabase sqlitedatabase)
        throws com.lumiyaviewer.lumiya.orm.DBObject.DatabaseBindingException
    {
        if (sqlitedatabase == null)
        {
            throw new com.lumiyaviewer.lumiya.orm.DBObject.DatabaseBindingException("Database is closed");
        }
        if (!sqlitedatabase.isOpen())
        {
            throw new com.lumiyaviewer.lumiya.orm.DBObject.DatabaseBindingException("Database is closed");
        }
        try
        {
            sqlitedatabase = sqlitedatabase.compileStatement("INSERT INTO Entries (parent_id,uuid_high,uuid_low,parentUUID_high,parentUUID_low,name,isFolder,typeDefault,version,sessionID_high,sessionID_low,fetchFailed,description,flags,invType,assetType,creationDate,_blobField) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?);");
        }
        // Misplaced declaration of an exception variable
        catch (SQLiteDatabase sqlitedatabase)
        {
            com.lumiyaviewer.lumiya.orm.DBObject.DatabaseBindingException databasebindingexception = new com.lumiyaviewer.lumiya.orm.DBObject.DatabaseBindingException(sqlitedatabase.getMessage());
            databasebindingexception.initCause(sqlitedatabase);
            throw databasebindingexception;
        }
        return sqlitedatabase;
    }

    public static SQLiteStatement getUpdateStatement(SQLiteDatabase sqlitedatabase)
        throws com.lumiyaviewer.lumiya.orm.DBObject.DatabaseBindingException
    {
        if (sqlitedatabase == null)
        {
            throw new com.lumiyaviewer.lumiya.orm.DBObject.DatabaseBindingException("Database is closed");
        }
        if (!sqlitedatabase.isOpen())
        {
            throw new com.lumiyaviewer.lumiya.orm.DBObject.DatabaseBindingException("Database is closed");
        }
        try
        {
            sqlitedatabase = sqlitedatabase.compileStatement("UPDATE Entries SET parent_id=?,uuid_high=?,uuid_low=?,parentUUID_high=?,parentUUID_low=?,name=?,isFolder=?,typeDefault=?,version=?,sessionID_high=?,sessionID_low=?,fetchFailed=?,description=?,flags=?,invType=?,assetType=?,creationDate=?,_blobField=? WHERE uuid_high = ? AND uuid_low = ?");
        }
        // Misplaced declaration of an exception variable
        catch (SQLiteDatabase sqlitedatabase)
        {
            com.lumiyaviewer.lumiya.orm.DBObject.DatabaseBindingException databasebindingexception = new com.lumiyaviewer.lumiya.orm.DBObject.DatabaseBindingException(sqlitedatabase.getMessage());
            databasebindingexception.initCause(sqlitedatabase);
            throw databasebindingexception;
        }
        return sqlitedatabase;
    }

    private static void parsePermissions(SimpleStringParser simplestringparser, SLInventoryEntry slinventoryentry)
        throws com.lumiyaviewer.lumiya.utils.SimpleStringParser.StringParsingException
    {
        simplestringparser.expectToken("{", "\n");
        do
        {
            String s = simplestringparser.nextToken(" \t\n");
            if (s.equals("}"))
            {
                return;
            }
            if (s.equals("base_mask"))
            {
                slinventoryentry.baseMask = simplestringparser.getHexToken("\n");
            } else
            if (s.equals("owner_mask"))
            {
                slinventoryentry.ownerMask = simplestringparser.getHexToken("\n");
            } else
            if (s.equals("group_mask"))
            {
                slinventoryentry.groupMask = simplestringparser.getHexToken("\n");
            } else
            if (s.equals("everyone_mask"))
            {
                slinventoryentry.everyoneMask = simplestringparser.getHexToken("\n");
            } else
            if (s.equals("next_owner_mask"))
            {
                slinventoryentry.nextOwnerMask = simplestringparser.getHexToken("\n");
            } else
            if (s.equals("creator_id"))
            {
                slinventoryentry.creatorUUID = UUID.fromString(simplestringparser.nextToken("\n"));
            } else
            if (s.equals("owner_id"))
            {
                slinventoryentry.ownerUUID = UUID.fromString(simplestringparser.nextToken("\n"));
            } else
            if (s.equals("last_owner_id"))
            {
                slinventoryentry.lastOwnerUUID = UUID.fromString(simplestringparser.nextToken("\n"));
            } else
            if (s.equals("group_id"))
            {
                slinventoryentry.groupUUID = UUID.fromString(simplestringparser.nextToken("\n"));
            } else
            {
                simplestringparser.nextToken("\n");
            }
        } while (true);
    }

    private static void parseSaleInfo(SimpleStringParser simplestringparser, SLInventoryEntry slinventoryentry)
        throws com.lumiyaviewer.lumiya.utils.SimpleStringParser.StringParsingException
    {
        simplestringparser.expectToken("{", "\n");
        do
        {
            String s = simplestringparser.nextToken(" \t\n");
            if (s.equals("}"))
            {
                return;
            }
            if (s.equals("sale_type"))
            {
                slinventoryentry.saleType = SLSaleType.getByString(simplestringparser.nextToken("\n")).getTypeCode();
            } else
            if (s.equals("sale_price"))
            {
                slinventoryentry.salePrice = simplestringparser.getIntToken("\n");
            } else
            {
                simplestringparser.nextToken("\n");
            }
        } while (true);
    }

    public static SLInventoryEntry parseString(SimpleStringParser simplestringparser)
        throws com.lumiyaviewer.lumiya.utils.SimpleStringParser.StringParsingException
    {
        SLInventoryEntry slinventoryentry = new SLInventoryEntry();
        simplestringparser.expectToken("{", "\n");
        do
        {
            String s = simplestringparser.nextToken(" \t\n");
            if (s.equals("}"))
            {
                return slinventoryentry;
            }
            if (s.equals("item_id"))
            {
                slinventoryentry.uuid = UUID.fromString(simplestringparser.nextToken("\n"));
            } else
            if (s.equals("parent_id"))
            {
                slinventoryentry.parentUUID = UUID.fromString(simplestringparser.nextToken("\n"));
            } else
            if (s.equals("asset_id"))
            {
                slinventoryentry.assetUUID = UUID.fromString(simplestringparser.nextToken("\n"));
            } else
            if (s.equals("type"))
            {
                slinventoryentry.assetType = SLAssetType.getByString(simplestringparser.nextToken("\n")).getTypeCode();
            } else
            if (s.equals("inv_type"))
            {
                slinventoryentry.invType = SLInventoryType.getByString(simplestringparser.nextToken("\n")).getTypeCode();
            } else
            if (s.equals("flags"))
            {
                slinventoryentry.flags = simplestringparser.getHexToken("\n");
            } else
            if (s.equals("name"))
            {
                slinventoryentry.name = simplestringparser.getPipeTerminatedString("\n");
            } else
            if (s.equals("desc"))
            {
                slinventoryentry.description = simplestringparser.getPipeTerminatedString("\n");
            } else
            if (s.equals("creation_date"))
            {
                slinventoryentry.creationDate = simplestringparser.getIntToken("\n");
            } else
            if (s.equals("permissions"))
            {
                simplestringparser.nextToken("\n");
                parsePermissions(simplestringparser, slinventoryentry);
            } else
            if (s.equals("sale_info"))
            {
                simplestringparser.nextToken("\n");
                parseSaleInfo(simplestringparser, slinventoryentry);
            } else
            {
                simplestringparser.nextToken("\n");
            }
        } while (true);
    }

    public static Cursor query(SQLiteDatabase sqlitedatabase, String s, String as[], String s1)
    {
        if (sqlitedatabase == null)
        {
            return null;
        }
        try
        {
            sqlitedatabase = InventoryEntryDBObject.query(sqlitedatabase, s, as, s1);
        }
        // Misplaced declaration of an exception variable
        catch (SQLiteDatabase sqlitedatabase)
        {
            sqlitedatabase.printStackTrace();
            return null;
        }
        return sqlitedatabase;
    }

    public static Cursor query(DBHandle dbhandle, String s, String as[], String s1)
    {
        if (dbhandle == null)
        {
            return null;
        }
        try
        {
            dbhandle = InventoryEntryDBObject.query(dbhandle, s, as, s1);
        }
        // Misplaced declaration of an exception variable
        catch (DBHandle dbhandle)
        {
            dbhandle.printStackTrace();
            return null;
        }
        return dbhandle;
    }

    public int getActionDescriptionResId()
    {
        SLAssetType slassettype = SLAssetType.getByType(assetType);
        if (slassettype != null)
        {
            return slassettype.getActionDescription();
        }
        switch (invType)
        {
        default:
            return -1;

        case 3: // '\003'
            return 0x7f09004f;

        case 7: // '\007'
            return 0x7f09004d;

        case 10: // '\n'
            return 0x7f09004d;

        case 0: // '\0'
        case 15: // '\017'
            if (assetType == SLAssetType.AT_TEXTURE.getTypeCode())
            {
                return 0x7f090050;
            }
            // fall through

        case 6: // '\006'
            return 0x7f09004e;
        }
    }

    public int getDrawableResource()
    {
        if (isFolder)
        {
            return 0x7f0200c0;
        }
        if (!isLink())
        {
            SLAssetType slassettype = SLAssetType.getByType(assetType);
            if (slassettype != null)
            {
                return slassettype.getDrawableResource();
            }
        }
        return getDrawableResourceForType(invType);
    }

    public String getReadableTextForLink()
    {
        SLInventoryType slinventorytype = SLInventoryType.getByType(invType);
        return (new StringBuilder()).append(slinventorytype.getReadableName()).append(": ").append(name).toString();
    }

    public int getSubtypeDrawableResource()
    {
        if (!isFolder) goto _L2; else goto _L1
_L1:
        typeDefault;
        JVM INSTR lookupswitch 19: default 172
    //                   0: 174
    //                   1: 177
    //                   2: 180
    //                   3: 183
    //                   5: 186
    //                   6: 192
    //                   7: 189
    //                   10: 195
    //                   13: 198
    //                   14: 201
    //                   15: 174
    //                   16: 204
    //                   20: 207
    //                   21: 210
    //                   23: 183
    //                   46: 186
    //                   47: 186
    //                   48: 186
    //                   49: 192;
           goto _L3 _L4 _L5 _L6 _L7 _L8 _L9 _L10 _L11 _L12 _L13 _L4 _L14 _L15 _L16 _L7 _L8 _L8 _L8 _L9
_L3:
        return -1;
_L4:
        return 0x7f0200c2;
_L5:
        return 0x7f0200ca;
_L6:
        return 0x7f0200cd;
_L7:
        return 0x7f0200c3;
_L8:
        return 0x7f0200bf;
_L10:
        return 0x7f0200c5;
_L9:
        return 0x7f0200c6;
_L11:
        return 0x7f0200c8;
_L12:
        return 0x7f0200c1;
_L13:
        return 0x7f0200cb;
_L14:
        return 0x7f0200c7;
_L15:
        return 0x7f0200bd;
_L16:
        return 0x7f0200c9;
_L2:
        if (assetType == SLAssetType.AT_LINK.getTypeCode() || assetType == SLAssetType.AT_LINK_FOLDER.getTypeCode())
        {
            return 0x7f0200c4;
        }
        if (true) goto _L3; else goto _L17
_L17:
    }

    public int getTypeDescriptionResId()
    {
        SLAssetType slassettype = SLAssetType.getByType(assetType);
        if (slassettype != null)
        {
            return slassettype.getTypeDescription();
        }
        switch (invType)
        {
        default:
            return 0x7f090078;

        case 0: // '\0'
            return 0x7f090075;

        case 1: // '\001'
            return 0x7f090074;

        case 2: // '\002'
            return 0x7f090065;

        case 3: // '\003'
            return 0x7f090069;

        case 4: // '\004'
            return 0x7f090071;

        case 5: // '\005'
            return 0x7f090066;

        case 6: // '\006'
            return 0x7f09006f;

        case 7: // '\007'
            return 0x7f09006e;

        case 8: // '\b'
            return 0x7f090067;

        case 9: // '\t'
            return 0x7f090070;

        case 10: // '\n'
            return 0x7f09006c;

        case 11: // '\013'
            return 0x7f09006d;

        case 12: // '\f'
            return 0x7f090076;

        case 13: // '\r'
            return 0x7f090064;

        case 14: // '\016'
            return 0x7f090077;

        case 15: // '\017'
            return 0x7f090073;

        case 16: // '\020'
            return 0x7f09006b;

        case 17: // '\021'
            return 0x7f090063;

        case 18: // '\022'
            return 0x7f090079;

        case 19: // '\023'
            return 0x7f090062;

        case 20: // '\024'
            return 0x7f090068;
        }
    }

    public boolean isAnimation()
    {
        if (assetType == SLAssetType.AT_ANIMATION.getTypeCode())
        {
            return true;
        }
        return assetType == SLAssetType.AT_LINK.getTypeCode() && invType == 19;
    }

    public boolean isCopyable()
    {
        boolean flag = false;
        if ((ownerMask & baseMask & 0x8000) != 0)
        {
            flag = true;
        }
        return flag;
    }

    public boolean isFolderOrFolderLink()
    {
        return isFolder || assetType == SLAssetType.AT_LINK_FOLDER.getTypeCode();
    }

    public boolean isLink()
    {
        return assetType == SLAssetType.AT_LINK.getTypeCode() || assetType == SLAssetType.AT_LINK_FOLDER.getTypeCode();
    }

    public boolean isWearable()
    {
        while (assetType == SLAssetType.AT_BODYPART.getTypeCode() || assetType == SLAssetType.AT_CLOTHING.getTypeCode() || assetType == SLAssetType.AT_LINK.getTypeCode() && invType == SLInventoryType.IT_WEARABLE.getTypeCode()) 
        {
            return true;
        }
        return false;
    }

    public void updateOrInsert(SQLiteDatabase sqlitedatabase)
        throws com.lumiyaviewer.lumiya.orm.DBObject.DatabaseBindingException
    {
        super.updateOrInsert(sqlitedatabase, "uuid_low = ? AND uuid_high = ?", new String[] {
            Long.toString(uuid.getLeastSignificantBits()), Long.toString(uuid.getMostSignificantBits())
        });
    }

    public void updateOrInsert(SQLiteStatement sqlitestatement, SQLiteStatement sqlitestatement1)
        throws com.lumiyaviewer.lumiya.orm.DBObject.DatabaseBindingException
    {
        sqlitestatement.bindLong(19, uuid.getMostSignificantBits());
        sqlitestatement.bindLong(20, uuid.getLeastSignificantBits());
        super.updateOrInsert(sqlitestatement, sqlitestatement1);
    }

    public Object whatIsItemWornOn(ImmutableMap immutablemap, Table table, boolean flag)
    {
        if (assetType == SLAssetType.AT_LINK.getTypeCode())
        {
            if (invType == SLInventoryType.IT_WEARABLE.getTypeCode())
            {
                if (table == null)
                {
                    break MISSING_BLOCK_LABEL_287;
                }
                immutablemap = table.cellSet().iterator();
                Object obj;
                do
                {
                    UUID uuid;
                    do
                    {
                        if (!immutablemap.hasNext())
                        {
                            break MISSING_BLOCK_LABEL_287;
                        }
                        obj = (com.google.common.collect.Table.Cell)immutablemap.next();
                        table = (SLWearableType)((com.google.common.collect.Table.Cell) (obj)).getRowKey();
                        uuid = (UUID)((com.google.common.collect.Table.Cell) (obj)).getColumnKey();
                    } while (table == null || uuid == null || flag && !table.isBodyPart());
                    if (uuid.equals(assetUUID))
                    {
                        return table;
                    }
                    obj = (SLWearable)((com.google.common.collect.Table.Cell) (obj)).getValue();
                } while (obj == null || !((SLWearable) (obj)).itemID.equals(assetUUID));
                return table;
            }
            if (immutablemap != null && !flag && immutablemap.containsKey(assetUUID))
            {
                return Boolean.TRUE;
            }
        } else
        if (assetType == SLAssetType.AT_BODYPART.getTypeCode() || assetType == SLAssetType.AT_CLOTHING.getTypeCode())
        {
            immutablemap = SLWearableType.getByCode(flags & 0xff);
            if (immutablemap != null && table != null)
            {
                if (flag && immutablemap.isBodyPart() ^ true)
                {
                    return null;
                }
                if (table.contains(immutablemap, assetUUID))
                {
                    return immutablemap;
                }
            }
        } else
        if (immutablemap != null && !flag && (immutablemap.containsKey(this.uuid) || immutablemap.containsKey(assetUUID)))
        {
            return Boolean.TRUE;
        }
        return null;
    }

}
