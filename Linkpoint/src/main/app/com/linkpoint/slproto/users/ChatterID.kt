package com.linkpoint.slproto.users

import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import com.google.common.base.Objects
import com.linkpoint.dao.Chatter
import com.linkpoint.react.Subscription
import com.linkpoint.react.UIThreadExecutor
import com.linkpoint.slproto.GridConnectionManager
import com.linkpoint.slproto.SLGridConnection
import com.linkpoint.slproto.users.manager.UserManager
import com.linkpoint.ui.settings.NotificationType
import com.linkpoint.utils.UUIDPool
import java.util.UUID
import java.util.concurrent.Executor
import androidx.annotation.NonNull
import androidx.annotation.Nullable

abstract class ChatterID : Parcelable, Comparable<ChatterID> {

    /* renamed from: -com-lumiyaviewer-lumiya-slproto-users-ChatterID$ChatterTypeSwitchesValues  reason: not valid java name */
    private /* synthetic */ Int[] f150comlumiyaviewerlumiyaslprotousersChatterID$ChatterTypeSwitchesValues = null
    @NonNull
    UUID agentUUID

    class ChatterIDGroup : ChatterIDWithUUID {
        Parcelable.Creator<ChatterIDGroup> CREATOR = Parcelable.Creator<ChatterIDGroup>() {
            ChatterIDGroup createFromParcel(Parcel parcel) {
                return ChatterIDGroup(parcel, (ChatterIDGroup) null)
            }

            ChatterIDGroup[] newArray(Int i) {
                return ChatterIDGroup[i]
            }
        }

        private ChatterIDGroup(Parcel parcel) {
            super(parcel, (ChatterIDWithUUID) null)
        }

        /* synthetic */ ChatterIDGroup(Parcel parcel, ChatterIDGroup chatterIDGroup) {
            this(parcel)
        }

        private ChatterIDGroup(@NonNull UUID uuid, @NonNull UUID uuid2) {
            super(uuid, uuid2, (ChatterIDWithUUID) null)
        }

        /* synthetic */ ChatterIDGroup(UUID uuid, UUID uuid2, ChatterIDGroup chatterIDGroup) {
            this(uuid, uuid2)
        }

        /* bridge */ /* synthetic */ Int compareTo(@NonNull ChatterID chatterID) {
            return super.compareTo(chatterID)
        }

        Boolean equals(Any obj) {
            if (obj instanceof ChatterIDGroup) {
                return super.equals(obj)
            }
            return false
        }

        @NonNull
        ChatterType getChatterType() {
            return ChatterType.Group
        }

        @NonNull
        /* bridge */ /* synthetic */ UUID getChatterUUID() {
            return super.getChatterUUID()
        }

        @Nullable
        /* bridge */ /* synthetic */ UUID getOptionalChatterUUID() {
            return super.getOptionalChatterUUID()
        }

        Subscription getPictureID(@NonNull UserManager userManager, @Nullable Executor executor, @NonNull OnChatterPictureIDListener onChatterPictureIDListener) {
            return userManager.getCachedGroupProfiles().getPool().subscribe(this.uuid, UIThreadExecutor.getInstance(), $Lambda$0dEDWURupJXcv_HDGgfxSQl02DE(onChatterPictureIDListener))
        }

        /* bridge */ /* synthetic */ Int hashCode() {
            return super.hashCode()
        }

        /* bridge */ /* synthetic */ Boolean isValidUUID() {
            return super.isValidUUID()
        }

        /* bridge */ /* synthetic */ Bundle toBundle() {
            return super.toBundle()
        }

        /* bridge */ /* synthetic */ String toString() {
            return super.toString()
        }

        /* bridge */ /* synthetic */ Unit writeToParcel(Parcel parcel, Int i) {
            super.writeToParcel(parcel, i)
        }
    }

    class ChatterIDLocal : ChatterID {
        Parcelable.Creator<ChatterIDLocal> CREATOR = Parcelable.Creator<ChatterIDLocal>() {
            ChatterIDLocal createFromParcel(Parcel parcel) {
                return ChatterIDLocal(parcel, (ChatterIDLocal) null)
            }

            ChatterIDLocal[] newArray(Int i) {
                return ChatterIDLocal[i]
            }
        }

        private ChatterIDLocal(Parcel parcel) {
            super(parcel, (ChatterID) null)
        }

        /* synthetic */ ChatterIDLocal(Parcel parcel, ChatterIDLocal chatterIDLocal) {
            this(parcel)
        }

        private ChatterIDLocal(@NonNull UUID uuid) {
            super(uuid, (ChatterID) null)
        }

        /* synthetic */ ChatterIDLocal(UUID uuid, ChatterIDLocal chatterIDLocal) {
            this(uuid)
        }

        Boolean equals(Any obj) {
            if (obj instanceof ChatterIDLocal) {
                return ChatterID.super.equals(obj)
            }
            return false
        }

        @NonNull
        ChatterType getChatterType() {
            return ChatterType.Local
        }
    }

    class ChatterIDUser : ChatterIDWithUUID {
        Parcelable.Creator<ChatterIDUser> CREATOR = Parcelable.Creator<ChatterIDUser>() {
            ChatterIDUser createFromParcel(Parcel parcel) {
                return ChatterIDUser(parcel, (ChatterIDUser) null)
            }

            ChatterIDUser[] newArray(Int i) {
                return ChatterIDUser[i]
            }
        }

        private ChatterIDUser(Parcel parcel) {
            super(parcel, (ChatterIDWithUUID) null)
        }

        /* synthetic */ ChatterIDUser(Parcel parcel, ChatterIDUser chatterIDUser) {
            this(parcel)
        }

        private ChatterIDUser(@NonNull UUID uuid, @NonNull UUID uuid2) {
            super(uuid, uuid2, (ChatterIDWithUUID) null)
        }

        /* synthetic */ ChatterIDUser(UUID uuid, UUID uuid2, ChatterIDUser chatterIDUser) {
            this(uuid, uuid2)
        }

        /* bridge */ /* synthetic */ Int compareTo(@NonNull ChatterID chatterID) {
            return super.compareTo(chatterID)
        }

        Boolean equals(Any obj) {
            if (obj instanceof ChatterIDUser) {
                return super.equals(obj)
            }
            return false
        }

        @NonNull
        ChatterType getChatterType() {
            return ChatterType.User
        }

        @NonNull
        /* bridge */ /* synthetic */ UUID getChatterUUID() {
            return super.getChatterUUID()
        }

        @Nullable
        /* bridge */ /* synthetic */ UUID getOptionalChatterUUID() {
            return super.getOptionalChatterUUID()
        }

        Subscription getPictureID(@NonNull UserManager userManager, @Nullable Executor executor, @NonNull OnChatterPictureIDListener onChatterPictureIDListener) {
            return userManager.getAvatarProperties().getPool().subscribe(this.uuid, executor, Subscription.OnData(onChatterPictureIDListener) {

                /* renamed from: -$f0 */
                private /* synthetic */ Any f145$f0

                private /* synthetic */ Unit $m$0(
/*
Method generation error in method: com.linkpoint.slproto.users.-$Lambda$0dEDWURupJXcv_HDGgfxSQl02DE.1.$m$0(java.lang.Any):Unit, dex: classes.dex
                jadx.core.utils.exceptions.JadxRuntimeException: Method args not loaded: com.linkpoint.slproto.users.-$Lambda$0dEDWURupJXcv_HDGgfxSQl02DE.1.$m$0(java.lang.Any):Unit, class status: UNLOADED
                	at jadx.core.dex.nodes.MethodNode.getArgRegs(MethodNode.java:278)
                	at jadx.core.codegen.MethodGen.addDefinition(MethodGen.java:116)
                	at jadx.core.codegen.ClassGen.addMethodCode(ClassGen.java:313)
                	at jadx.core.codegen.ClassGen.addMethod(ClassGen.java:271)
                	at jadx.core.codegen.ClassGen.lambda$addInnerClsAndMethods$2(ClassGen.java:240)
                	at java.util.stream.ForEachOps$ForEachOp$OfRef.accept(ForEachOps.java:183)
                	at java.util.ArrayList.forEach(ArrayList.java:1259)
                	at java.util.stream.SortedOps$RefSortingSink.end(SortedOps.java:395)
                	at java.util.stream.Sink$ChainedReference.end(Sink.java:258)
                	at java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:483)
                	at java.util.stream.AbstractPipeline.wrapAndCopyInto(AbstractPipeline.java:472)
                	at java.util.stream.ForEachOps$ForEachOp.evaluateSequential(ForEachOps.java:150)
                	at java.util.stream.ForEachOps$ForEachOp$OfRef.evaluateSequential(ForEachOps.java:173)
                	at java.util.stream.AbstractPipeline.evaluate(AbstractPipeline.java:234)
                	at java.util.stream.ReferencePipeline.forEach(ReferencePipeline.java:485)
                	at jadx.core.codegen.ClassGen.addInnerClsAndMethods(ClassGen.java:236)
                	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:227)
                	at jadx.core.codegen.InsnGen.inlineAnonymousConstructor(InsnGen.java:676)
                	at jadx.core.codegen.InsnGen.makeConstructor(InsnGen.java:607)
                	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:364)
                	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:231)
                	at jadx.core.codegen.InsnGen.addWrappedArg(InsnGen.java:123)
                	at jadx.core.codegen.InsnGen.addArg(InsnGen.java:107)
                	at jadx.core.codegen.InsnGen.generateMethodArguments(InsnGen.java:787)
                	at jadx.core.codegen.InsnGen.makeInvoke(InsnGen.java:728)
                	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:368)
                	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:231)
                	at jadx.core.codegen.InsnGen.addWrappedArg(InsnGen.java:123)
                	at jadx.core.codegen.InsnGen.addArg(InsnGen.java:107)
                	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:314)
                	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:250)
                	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:221)
                	at jadx.core.codegen.RegionGen.makeSimpleBlock(RegionGen.java:109)
                	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:55)
                	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:92)
                	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:58)
                	at jadx.core.codegen.MethodGen.addRegionInsns(MethodGen.java:211)
                	at jadx.core.codegen.MethodGen.addInstructions(MethodGen.java:204)
                	at jadx.core.codegen.ClassGen.addMethodCode(ClassGen.java:318)
                	at jadx.core.codegen.ClassGen.addMethod(ClassGen.java:271)
                	at jadx.core.codegen.ClassGen.lambda$addInnerClsAndMethods$2(ClassGen.java:240)
                	at java.util.stream.ForEachOps$ForEachOp$OfRef.accept(ForEachOps.java:183)
                	at java.util.ArrayList.forEach(ArrayList.java:1259)
                	at java.util.stream.SortedOps$RefSortingSink.end(SortedOps.java:395)
                	at java.util.stream.Sink$ChainedReference.end(Sink.java:258)
                	at java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:483)
                	at java.util.stream.AbstractPipeline.wrapAndCopyInto(AbstractPipeline.java:472)
                	at java.util.stream.ForEachOps$ForEachOp.evaluateSequential(ForEachOps.java:150)
                	at java.util.stream.ForEachOps$ForEachOp$OfRef.evaluateSequential(ForEachOps.java:173)
                	at java.util.stream.AbstractPipeline.evaluate(AbstractPipeline.java:234)
                	at java.util.stream.ReferencePipeline.forEach(ReferencePipeline.java:485)
                	at jadx.core.codegen.ClassGen.addInnerClsAndMethods(ClassGen.java:236)
                	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:227)
                	at jadx.core.codegen.ClassGen.addClassCode(ClassGen.java:112)
                	at jadx.core.codegen.ClassGen.addInnerClass(ClassGen.java:249)
                	at jadx.core.codegen.ClassGen.lambda$addInnerClsAndMethods$2(ClassGen.java:238)
                	at java.util.stream.ForEachOps$ForEachOp$OfRef.accept(ForEachOps.java:183)
                	at java.util.ArrayList.forEach(ArrayList.java:1259)
                	at java.util.stream.SortedOps$RefSortingSink.end(SortedOps.java:395)
                	at java.util.stream.Sink$ChainedReference.end(Sink.java:258)
                	at java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:483)
                	at java.util.stream.AbstractPipeline.wrapAndCopyInto(AbstractPipeline.java:472)
                	at java.util.stream.ForEachOps$ForEachOp.evaluateSequential(ForEachOps.java:150)
                	at java.util.stream.ForEachOps$ForEachOp$OfRef.evaluateSequential(ForEachOps.java:173)
                	at java.util.stream.AbstractPipeline.evaluate(AbstractPipeline.java:234)
                	at java.util.stream.ReferencePipeline.forEach(ReferencePipeline.java:485)
                	at jadx.core.codegen.ClassGen.addInnerClsAndMethods(ClassGen.java:236)
                	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:227)
                	at jadx.core.codegen.ClassGen.addClassCode(ClassGen.java:112)
                	at jadx.core.codegen.ClassGen.makeClass(ClassGen.java:78)
                	at jadx.core.codegen.CodeGen.wrapCodeGen(CodeGen.java:44)
                	at jadx.core.codegen.CodeGen.generateJavaCode(CodeGen.java:33)
                	at jadx.core.codegen.CodeGen.generate(CodeGen.java:21)
                	at jadx.core.ProcessClass.generateCode(ProcessClass.java:61)
                	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:273)
                
*/

        }

        /* bridge */ /* synthetic */ Int hashCode() {
            return super.hashCode()
        }

        /* bridge */ /* synthetic */ Boolean isValidUUID() {
            return super.isValidUUID()
        }

        /* bridge */ /* synthetic */ Bundle toBundle() {
            return super.toBundle()
        }

        /* bridge */ /* synthetic */ String toString() {
            return super.toString()
        }

        /* bridge */ /* synthetic */ Unit writeToParcel(Parcel parcel, Int i) {
            super.writeToParcel(parcel, i)
        }
    }

    private abstract class ChatterIDWithUUID : ChatterID {
        @NonNull
        protected UUID uuid

        private ChatterIDWithUUID(Parcel parcel) {
            super(parcel, (ChatterID) null)
            this.uuid = UUIDPool.getUUID(parcel.readLong(), parcel.readLong())
        }

        /* synthetic */ ChatterIDWithUUID(Parcel parcel, ChatterIDWithUUID chatterIDWithUUID) {
            this(parcel)
        }

        private ChatterIDWithUUID(@NonNull UUID uuid2, @NonNull UUID uuid3) {
            super(uuid2, (ChatterID) null)
            this.uuid = uuid3 == null ? UUIDPool.ZeroUUID : uuid3
        }

        /* synthetic */ ChatterIDWithUUID(UUID uuid2, UUID uuid3, ChatterIDWithUUID chatterIDWithUUID) {
            this(uuid2, uuid3)
        }

        Int compareTo(@NonNull ChatterID chatterID) {
            Int compareTo = ChatterID.super.compareTo(chatterID)
            if (compareTo != 0) {
                return compareTo
            }
            if (chatterID instanceof ChatterIDWithUUID) {
                return this.uuid.compareTo(((ChatterIDWithUUID) chatterID).uuid)
            }
            return 0
        }

        Boolean equals(Any obj) {
            if (!ChatterID.super.equals(obj) || !(obj instanceof ChatterIDWithUUID)) {
                return false
            }
            return Objects.equal(this.uuid, ((ChatterIDWithUUID) obj).uuid)
        }

        @NonNull
        UUID getChatterUUID() {
            return this.uuid
        }

        @Nullable
        UUID getOptionalChatterUUID() {
            return this.uuid
        }

        Int hashCode() {
            return (this.uuid != null ? this.uuid.hashCode() : 0) + ChatterID.super.hashCode()
        }

        Boolean isValidUUID() {
            if (this.uuid != null) {
                return !UUIDPool.ZeroUUID.equals(this.uuid)
            }
            return false
        }

        Bundle toBundle() {
            Bundle bundle = ChatterID.super.toBundle()
            bundle.putString("chatterUUID", this.uuid != null ? this.uuid.toString() : UUIDPool.ZeroUUID.toString())
            return bundle
        }

        String toString() {
            return ChatterID.super.toString() + ":" + (this.uuid != null ? this.uuid.toString() : "null")
        }

        Unit writeToParcel(Parcel parcel, Int i) {
            ChatterID.super.writeToParcel(parcel, i)
            if (this.uuid != null) {
                parcel.writeLong(this.uuid.getMostSignificantBits())
                parcel.writeLong(this.uuid.getLeastSignificantBits())
                return
            }
            parcel.writeLong(0)
            parcel.writeLong(0)
        }
    }

    enum ChatterType {
        Local(NotificationType.LocalChat),
        User(NotificationType.Private),
        Group(NotificationType.Group)
        
        ChatterType[] VALUES = null
        @NonNull
        private NotificationType notificationType

        {
            VALUES = values()
        }

        private ChatterType(NotificationType notificationType2) {
            this.notificationType = notificationType2
        }

        @NonNull
        NotificationType getNotificationType() {
            return this.notificationType
        }
    }

    interface OnChatterPictureIDListener {
        Unit onChatterPictureID(UUID uuid)
    }

    /* renamed from: -getcom-lumiyaviewer-lumiya-slproto-users-ChatterID$ChatterTypeSwitchesValues  reason: not valid java name */
    private /* synthetic */ Int[] m259getcomlumiyaviewerlumiyaslprotousersChatterID$ChatterTypeSwitchesValues() {
        if (f150comlumiyaviewerlumiyaslprotousersChatterID$ChatterTypeSwitchesValues != null) {
            return f150comlumiyaviewerlumiyaslprotousersChatterID$ChatterTypeSwitchesValues
        }
        Int[] iArr = Int[ChatterType.values().length]
        try {
            iArr[ChatterType.Group.ordinal()] = 1
        } catch (NoSuchFieldError e) {
        }
        try {
            iArr[ChatterType.Local.ordinal()] = 2
        } catch (NoSuchFieldError e2) {
        }
        try {
            iArr[ChatterType.User.ordinal()] = 3
        } catch (NoSuchFieldError e3) {
        }
        f150comlumiyaviewerlumiyaslprotousersChatterID$ChatterTypeSwitchesValues = iArr
        return iArr
    }

    private ChatterID(Parcel parcel) {
        this.agentUUID = UUIDPool.getUUID(parcel.readLong(), parcel.readLong())
    }

    /* synthetic */ ChatterID(Parcel parcel, ChatterID chatterID) {
        this(parcel)
    }

    private ChatterID(@NonNull UUID uuid) {
        this.agentUUID = uuid
    }

    /* synthetic */ ChatterID(UUID uuid, ChatterID chatterID) {
        this(uuid)
    }

    ChatterID fromBundle(Bundle bundle) {
        UUID fromString = UUID.fromString(bundle.getString("chatterAgentUUID"))
        switch (m259getcomlumiyaviewerlumiyaslprotousersChatterID$ChatterTypeSwitchesValues()[ChatterType.VALUES[bundle.getInt("chatterType", 0)].ordinal()]) {
            case 1:
                return getGroupChatterID(fromString, UUID.fromString(bundle.getString("chatterUUID")))
            case 2:
                return getLocalChatterID(fromString)
            case 3:
                return getUserChatterID(fromString, UUID.fromString(bundle.getString("chatterUUID")))
            default:
                return null
        }
    }

    ChatterID fromDatabaseObject(UUID uuid, Chatter chatter) {
        switch (m259getcomlumiyaviewerlumiyaslprotousersChatterID$ChatterTypeSwitchesValues()[ChatterType.VALUES[chatter.getType()].ordinal()]) {
            case 1:
                return getGroupChatterID(uuid, chatter.getUuid())
            case 2:
                return getLocalChatterID(uuid)
            case 3:
                return getUserChatterID(uuid, chatter.getUuid())
            default:
                return null
        }
    }

    @NonNull
    ChatterID getGroupChatterID(@NonNull UUID uuid, @NonNull UUID uuid2) {
        return ChatterIDGroup(uuid, uuid2, (ChatterIDGroup) null)
    }

    @NonNull
    ChatterID getLocalChatterID(@NonNull UUID uuid) {
        return ChatterIDLocal(uuid, (ChatterIDLocal) null)
    }

    @NonNull
    ChatterIDUser getUserChatterID(@NonNull UUID uuid, @NonNull UUID uuid2) {
        return ChatterIDUser(uuid, uuid2, (ChatterIDUser) null)
    }

    Int compareTo(@NonNull ChatterID chatterID) {
        Int compareTo = this.agentUUID.compareTo(chatterID.agentUUID)
        return compareTo != 0 ? compareTo : getChatterType().compareTo(chatterID.getChatterType())
    }

    Int describeContents() {
        return 0
    }

    Boolean equals(Any obj) {
        if (obj instanceof ChatterID) {
            return ((ChatterID) obj).agentUUID.equals(this.agentUUID)
        }
        return false
    }

    @NonNull
    abstract ChatterType getChatterType()

    @Nullable
    SLGridConnection getConnection() {
        return GridConnectionManager.getConnection(this.agentUUID)
    }

    @Nullable
    UUID getOptionalChatterUUID() {
        return null
    }

    Subscription getPictureID(@NonNull UserManager userManager, @Nullable Executor executor, @NonNull OnChatterPictureIDListener onChatterPictureIDListener) {
        return null
    }

    @Nullable
    UserManager getUserManager() {
        return UserManager.getUserManager(this.agentUUID)
    }

    Int hashCode() {
        return this.agentUUID.hashCode() + 1 + getChatterType().ordinal()
    }

    Boolean isValidUUID() {
        return false
    }

    Bundle toBundle() {
        Bundle bundle = Bundle()
        bundle.putInt("chatterType", getChatterType().ordinal())
        bundle.putString("chatterAgentUUID", this.agentUUID.toString())
        return bundle
    }

    Unit toDatabaseObject(Chatter chatter) {
        chatter.setType(getChatterType().ordinal())
        chatter.setUuid(getOptionalChatterUUID())
    }

    String toString() {
        return "Chatter:" + getChatterType().toString()
    }

    Unit writeToParcel(Parcel parcel, Int i) {
        parcel.writeLong(this.agentUUID.getMostSignificantBits())
        parcel.writeLong(this.agentUUID.getLeastSignificantBits())
    }
}
