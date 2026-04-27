// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.users;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import com.google.common.base.Objects;
import com.lumiyaviewer.lumiya.dao.Chatter;
import com.lumiyaviewer.lumiya.react.Subscribable;
import com.lumiyaviewer.lumiya.react.Subscription;
import com.lumiyaviewer.lumiya.react.UIThreadExecutor;
import com.lumiyaviewer.lumiya.slproto.GridConnectionManager;
import com.lumiyaviewer.lumiya.slproto.SLGridConnection;
import com.lumiyaviewer.lumiya.slproto.messages.AvatarPropertiesReply;
import com.lumiyaviewer.lumiya.slproto.messages.GroupProfileReply;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import com.lumiyaviewer.lumiya.ui.settings.NotificationType;
import com.lumiyaviewer.lumiya.utils.UUIDPool;
import java.util.UUID;
import java.util.concurrent.Executor;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.users:
//            SLMessageResponseCacher

public abstract class ChatterID
    implements Parcelable, Comparable
{
    public static class ChatterIDGroup extends ChatterIDWithUUID
    {

        public static final android.os.Parcelable.Creator CREATOR = new android.os.Parcelable.Creator() {

            public ChatterIDGroup createFromParcel(Parcel parcel)
            {
                return new ChatterIDGroup(parcel, null);
            }

            public volatile Object createFromParcel(Parcel parcel)
            {
                return createFromParcel(parcel);
            }

            public ChatterIDGroup[] newArray(int i)
            {
                return new ChatterIDGroup[i];
            }

            public volatile Object[] newArray(int i)
            {
                return newArray(i);
            }

        };

        static void lambda$_2D_com_lumiyaviewer_lumiya_slproto_users_ChatterID$ChatterIDGroup_7487(OnChatterPictureIDListener onchatterpictureidlistener, GroupProfileReply groupprofilereply)
        {
            onchatterpictureidlistener.onChatterPictureID(groupprofilereply.GroupData_Field.InsigniaID);
        }

        public volatile int compareTo(ChatterID chatterid)
        {
            return super.compareTo(chatterid);
        }

        public boolean equals(Object obj)
        {
            if (obj instanceof ChatterIDGroup)
            {
                return super.equals(obj);
            } else
            {
                return false;
            }
        }

        public ChatterType getChatterType()
        {
            return ChatterType.Group;
        }

        public volatile UUID getChatterUUID()
        {
            return super.getChatterUUID();
        }

        public volatile UUID getOptionalChatterUUID()
        {
            return super.getOptionalChatterUUID();
        }

        public Subscription getPictureID(UserManager usermanager, Executor executor, OnChatterPictureIDListener onchatterpictureidlistener)
        {
            return usermanager.getCachedGroupProfiles().getPool().subscribe(uuid, UIThreadExecutor.getInstance(), new _2D_.Lambda._cls0dEDWURupJXcv_HDGgfxSQl02DE(onchatterpictureidlistener));
        }

        public volatile int hashCode()
        {
            return super.hashCode();
        }

        public volatile boolean isValidUUID()
        {
            return super.isValidUUID();
        }

        public volatile Bundle toBundle()
        {
            return super.toBundle();
        }

        public volatile String toString()
        {
            return super.toString();
        }

        public volatile void writeToParcel(Parcel parcel, int i)
        {
            super.writeToParcel(parcel, i);
        }


        private ChatterIDGroup(Parcel parcel)
        {
            super(parcel, null);
        }

        ChatterIDGroup(Parcel parcel, ChatterIDGroup chatteridgroup)
        {
            this(parcel);
        }

        private ChatterIDGroup(UUID uuid, UUID uuid1)
        {
            super(uuid, uuid1, null);
        }

        ChatterIDGroup(UUID uuid, UUID uuid1, ChatterIDGroup chatteridgroup)
        {
            this(uuid, uuid1);
        }
    }

    public static class ChatterIDLocal extends ChatterID
    {

        public static final android.os.Parcelable.Creator CREATOR = new android.os.Parcelable.Creator() {

            public ChatterIDLocal createFromParcel(Parcel parcel)
            {
                return new ChatterIDLocal(parcel, null);
            }

            public volatile Object createFromParcel(Parcel parcel)
            {
                return createFromParcel(parcel);
            }

            public ChatterIDLocal[] newArray(int i)
            {
                return new ChatterIDLocal[i];
            }

            public volatile Object[] newArray(int i)
            {
                return newArray(i);
            }

        };

        public boolean equals(Object obj)
        {
            if (obj instanceof ChatterIDLocal)
            {
                return equals(obj);
            } else
            {
                return false;
            }
        }

        public ChatterType getChatterType()
        {
            return ChatterType.Local;
        }


        private ChatterIDLocal(Parcel parcel)
        {
            super(parcel, null);
        }

        ChatterIDLocal(Parcel parcel, ChatterIDLocal chatteridlocal)
        {
            this(parcel);
        }

        private ChatterIDLocal(UUID uuid)
        {
            super(uuid, null);
        }

        ChatterIDLocal(UUID uuid, ChatterIDLocal chatteridlocal)
        {
            this(uuid);
        }
    }

    public static class ChatterIDUser extends ChatterIDWithUUID
    {

        public static final android.os.Parcelable.Creator CREATOR = new android.os.Parcelable.Creator() {

            public ChatterIDUser createFromParcel(Parcel parcel)
            {
                return new ChatterIDUser(parcel, null);
            }

            public volatile Object createFromParcel(Parcel parcel)
            {
                return createFromParcel(parcel);
            }

            public ChatterIDUser[] newArray(int i)
            {
                return new ChatterIDUser[i];
            }

            public volatile Object[] newArray(int i)
            {
                return newArray(i);
            }

        };

        static void lambda$_2D_com_lumiyaviewer_lumiya_slproto_users_ChatterID$ChatterIDUser_6120(OnChatterPictureIDListener onchatterpictureidlistener, AvatarPropertiesReply avatarpropertiesreply)
        {
            onchatterpictureidlistener.onChatterPictureID(avatarpropertiesreply.PropertiesData_Field.ImageID);
        }

        public volatile int compareTo(ChatterID chatterid)
        {
            return super.compareTo(chatterid);
        }

        public boolean equals(Object obj)
        {
            if (obj instanceof ChatterIDUser)
            {
                return super.equals(obj);
            } else
            {
                return false;
            }
        }

        public ChatterType getChatterType()
        {
            return ChatterType.User;
        }

        public volatile UUID getChatterUUID()
        {
            return super.getChatterUUID();
        }

        public volatile UUID getOptionalChatterUUID()
        {
            return super.getOptionalChatterUUID();
        }

        public Subscription getPictureID(UserManager usermanager, Executor executor, OnChatterPictureIDListener onchatterpictureidlistener)
        {
            return usermanager.getAvatarProperties().getPool().subscribe(uuid, executor, new _2D_.Lambda._cls0dEDWURupJXcv_HDGgfxSQl02DE._cls1(onchatterpictureidlistener));
        }

        public volatile int hashCode()
        {
            return super.hashCode();
        }

        public volatile boolean isValidUUID()
        {
            return super.isValidUUID();
        }

        public volatile Bundle toBundle()
        {
            return super.toBundle();
        }

        public volatile String toString()
        {
            return super.toString();
        }

        public volatile void writeToParcel(Parcel parcel, int i)
        {
            super.writeToParcel(parcel, i);
        }


        private ChatterIDUser(Parcel parcel)
        {
            super(parcel, null);
        }

        ChatterIDUser(Parcel parcel, ChatterIDUser chatteriduser)
        {
            this(parcel);
        }

        private ChatterIDUser(UUID uuid, UUID uuid1)
        {
            super(uuid, uuid1, null);
        }

        ChatterIDUser(UUID uuid, UUID uuid1, ChatterIDUser chatteriduser)
        {
            this(uuid, uuid1);
        }
    }

    private static abstract class ChatterIDWithUUID extends ChatterID
    {

        protected final UUID uuid;

        public int compareTo(ChatterID chatterid)
        {
            int i = compareTo(chatterid);
            if (i != 0)
            {
                return i;
            }
            if (chatterid instanceof ChatterIDWithUUID)
            {
                return uuid.compareTo(((ChatterIDWithUUID)chatterid).uuid);
            } else
            {
                return 0;
            }
        }

        public boolean equals(Object obj)
        {
            if (equals(obj) && (obj instanceof ChatterIDWithUUID))
            {
                return Objects.equal(uuid, ((ChatterIDWithUUID)obj).uuid);
            } else
            {
                return false;
            }
        }

        public UUID getChatterUUID()
        {
            return uuid;
        }

        public UUID getOptionalChatterUUID()
        {
            return uuid;
        }

        public int hashCode()
        {
            int j = hashCode();
            int i;
            if (uuid != null)
            {
                i = uuid.hashCode();
            } else
            {
                i = 0;
            }
            return i + j;
        }

        public boolean isValidUUID()
        {
            if (uuid != null)
            {
                return UUIDPool.ZeroUUID.equals(uuid) ^ true;
            } else
            {
                return false;
            }
        }

        public Bundle toBundle()
        {
            Bundle bundle = toBundle();
            String s;
            if (uuid != null)
            {
                s = uuid.toString();
            } else
            {
                s = UUIDPool.ZeroUUID.toString();
            }
            bundle.putString("chatterUUID", s);
            return bundle;
        }

        public String toString()
        {
            StringBuilder stringbuilder = (new StringBuilder()).append(toString()).append(":");
            String s;
            if (uuid != null)
            {
                s = uuid.toString();
            } else
            {
                s = "null";
            }
            return stringbuilder.append(s).toString();
        }

        public void writeToParcel(Parcel parcel, int i)
        {
            writeToParcel(parcel, i);
            if (uuid != null)
            {
                parcel.writeLong(uuid.getMostSignificantBits());
                parcel.writeLong(uuid.getLeastSignificantBits());
                return;
            } else
            {
                parcel.writeLong(0L);
                parcel.writeLong(0L);
                return;
            }
        }

        private ChatterIDWithUUID(Parcel parcel)
        {
            super(parcel, null);
            uuid = UUIDPool.getUUID(parcel.readLong(), parcel.readLong());
        }

        ChatterIDWithUUID(Parcel parcel, ChatterIDWithUUID chatteridwithuuid)
        {
            this(parcel);
        }

        private ChatterIDWithUUID(UUID uuid1, UUID uuid2)
        {
            super(uuid1, null);
            if (uuid2 == null)
            {
                uuid2 = UUIDPool.ZeroUUID;
            }
            uuid = uuid2;
        }

        ChatterIDWithUUID(UUID uuid1, UUID uuid2, ChatterIDWithUUID chatteridwithuuid)
        {
            this(uuid1, uuid2);
        }
    }

    public static final class ChatterType extends Enum
    {

        private static final ChatterType $VALUES[];
        public static final ChatterType Group;
        public static final ChatterType Local;
        public static final ChatterType User;
        public static final ChatterType VALUES[] = values();
        private final NotificationType notificationType;

        public static ChatterType valueOf(String s)
        {
            return (ChatterType)Enum.valueOf(com/lumiyaviewer/lumiya/slproto/users/ChatterID$ChatterType, s);
        }

        public static ChatterType[] values()
        {
            return $VALUES;
        }

        public NotificationType getNotificationType()
        {
            return notificationType;
        }

        static 
        {
            Local = new ChatterType("Local", 0, NotificationType.LocalChat);
            User = new ChatterType("User", 1, NotificationType.Private);
            Group = new ChatterType("Group", 2, NotificationType.Group);
            $VALUES = (new ChatterType[] {
                Local, User, Group
            });
        }

        private ChatterType(String s, int i, NotificationType notificationtype)
        {
            super(s, i);
            notificationType = notificationtype;
        }
    }

    public static interface OnChatterPictureIDListener
    {

        public abstract void onChatterPictureID(UUID uuid);
    }


    private static final int _2D_com_2D_lumiyaviewer_2D_lumiya_2D_slproto_2D_users_2D_ChatterID$ChatterTypeSwitchesValues[];
    public final UUID agentUUID;

    private static int[] _2D_getcom_2D_lumiyaviewer_2D_lumiya_2D_slproto_2D_users_2D_ChatterID$ChatterTypeSwitchesValues()
    {
        if (_2D_com_2D_lumiyaviewer_2D_lumiya_2D_slproto_2D_users_2D_ChatterID$ChatterTypeSwitchesValues != null)
        {
            return _2D_com_2D_lumiyaviewer_2D_lumiya_2D_slproto_2D_users_2D_ChatterID$ChatterTypeSwitchesValues;
        }
        int ai[] = new int[ChatterType.values().length];
        try
        {
            ai[ChatterType.Group.ordinal()] = 1;
        }
        catch (NoSuchFieldError nosuchfielderror2) { }
        try
        {
            ai[ChatterType.Local.ordinal()] = 2;
        }
        catch (NoSuchFieldError nosuchfielderror1) { }
        try
        {
            ai[ChatterType.User.ordinal()] = 3;
        }
        catch (NoSuchFieldError nosuchfielderror) { }
        _2D_com_2D_lumiyaviewer_2D_lumiya_2D_slproto_2D_users_2D_ChatterID$ChatterTypeSwitchesValues = ai;
        return ai;
    }

    private ChatterID(Parcel parcel)
    {
        agentUUID = UUIDPool.getUUID(parcel.readLong(), parcel.readLong());
    }

    ChatterID(Parcel parcel, ChatterID chatterid)
    {
        this(parcel);
    }

    private ChatterID(UUID uuid)
    {
        agentUUID = uuid;
    }

    ChatterID(UUID uuid, ChatterID chatterid)
    {
        this(uuid);
    }

    public static ChatterID fromBundle(Bundle bundle)
    {
        UUID uuid = UUID.fromString(bundle.getString("chatterAgentUUID"));
        ChatterType chattertype = ChatterType.VALUES[bundle.getInt("chatterType", 0)];
        switch (_2D_getcom_2D_lumiyaviewer_2D_lumiya_2D_slproto_2D_users_2D_ChatterID$ChatterTypeSwitchesValues()[chattertype.ordinal()])
        {
        default:
            return null;

        case 2: // '\002'
            return getLocalChatterID(uuid);

        case 3: // '\003'
            return getUserChatterID(uuid, UUID.fromString(bundle.getString("chatterUUID")));

        case 1: // '\001'
            return getGroupChatterID(uuid, UUID.fromString(bundle.getString("chatterUUID")));
        }
    }

    public static ChatterID fromDatabaseObject(UUID uuid, Chatter chatter)
    {
        ChatterType chattertype = ChatterType.VALUES[chatter.getType()];
        switch (_2D_getcom_2D_lumiyaviewer_2D_lumiya_2D_slproto_2D_users_2D_ChatterID$ChatterTypeSwitchesValues()[chattertype.ordinal()])
        {
        default:
            return null;

        case 2: // '\002'
            return getLocalChatterID(uuid);

        case 3: // '\003'
            return getUserChatterID(uuid, chatter.getUuid());

        case 1: // '\001'
            return getGroupChatterID(uuid, chatter.getUuid());
        }
    }

    public static ChatterID getGroupChatterID(UUID uuid, UUID uuid1)
    {
        return new ChatterIDGroup(uuid, uuid1, null);
    }

    public static ChatterID getLocalChatterID(UUID uuid)
    {
        return new ChatterIDLocal(uuid, null);
    }

    public static ChatterIDUser getUserChatterID(UUID uuid, UUID uuid1)
    {
        return new ChatterIDUser(uuid, uuid1, null);
    }

    public int compareTo(ChatterID chatterid)
    {
        int i = agentUUID.compareTo(chatterid.agentUUID);
        if (i != 0)
        {
            return i;
        } else
        {
            return getChatterType().compareTo(chatterid.getChatterType());
        }
    }

    public volatile int compareTo(Object obj)
    {
        return compareTo((ChatterID)obj);
    }

    public int describeContents()
    {
        return 0;
    }

    public boolean equals(Object obj)
    {
        if (obj instanceof ChatterID)
        {
            return ((ChatterID)obj).agentUUID.equals(agentUUID);
        } else
        {
            return false;
        }
    }

    public abstract ChatterType getChatterType();

    public SLGridConnection getConnection()
    {
        return GridConnectionManager.getConnection(agentUUID);
    }

    public UUID getOptionalChatterUUID()
    {
        return null;
    }

    public Subscription getPictureID(UserManager usermanager, Executor executor, OnChatterPictureIDListener onchatterpictureidlistener)
    {
        return null;
    }

    public UserManager getUserManager()
    {
        return UserManager.getUserManager(agentUUID);
    }

    public int hashCode()
    {
        return agentUUID.hashCode() + 1 + getChatterType().ordinal();
    }

    public boolean isValidUUID()
    {
        return false;
    }

    public Bundle toBundle()
    {
        Bundle bundle = new Bundle();
        bundle.putInt("chatterType", getChatterType().ordinal());
        bundle.putString("chatterAgentUUID", agentUUID.toString());
        return bundle;
    }

    public void toDatabaseObject(Chatter chatter)
    {
        chatter.setType(getChatterType().ordinal());
        chatter.setUuid(getOptionalChatterUUID());
    }

    public String toString()
    {
        return (new StringBuilder()).append("Chatter:").append(getChatterType().toString()).toString();
    }

    public void writeToParcel(Parcel parcel, int i)
    {
        parcel.writeLong(agentUUID.getMostSignificantBits());
        parcel.writeLong(agentUUID.getLeastSignificantBits());
    }
}
