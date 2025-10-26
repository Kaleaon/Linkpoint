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
import javax.annotation.Nonnull
import javax.annotation.Nullable

abstract class ChatterID : Parcelable, Comparable<ChatterID> {

    /* renamed from: -com-lumiyaviewer-lumiya-slproto-users-ChatterID$ChatterTypeSwitchesValues  reason: not valid java name */
    private const val /* synthetic */ IntArray f150comlumiyaviewerlumiyaslprotousersChatterID$ChatterTypeSwitchesValues = null
    val UUID agentUUID

    @JvmStatic
    class ChatterIDGroup : ChatterIDWithUUID() {
        const val Parcelable.Creator<ChatterIDGroup> CREATOR = Parcelable.Creator<ChatterIDGroup>() {
             public fun createFromParcel(parcel: Parcel): ChatterIDGroup {
                return ChatterIDGroup(parcel, (ChatterIDGroup) null)
            }

            public Array<ChatterIDGroup> newArray(Int i) {
                return ChatterIDGroup[i]
            }
        }

        private ChatterIDGroup(Parcel parcel) {
            super(parcel, (ChatterIDWithUUID) null)
        }

        /* synthetic */ ChatterIDGroup(Parcel parcel, ChatterIDGroup chatterIDGroup) {
            this(parcel)
        }

        private ChatterIDGroup(UUID uuid, UUID uuid2) {
            super(uuid, uuid2, (ChatterIDWithUUID) null)
        }

        /* synthetic */ ChatterIDGroup(UUID uuid, UUID uuid2, ChatterIDGroup chatterIDGroup) {
            this(uuid, uuid2)
        }

        public /* bridge */ /* synthetic */ Int compareTo(ChatterID chatterID) {
            return super.compareTo(chatterID)
        }

         public fun equals(obj: Object): Boolean {
            if (obj instanceof ChatterIDGroup) {
                return super.equals(obj)
            }
            return false
        }

         public fun getChatterType(): ChatterType {
            return ChatterType.Group
        }

        public /* bridge */ /* synthetic */ UUID getChatterUUID() {
            return super.getChatterUUID()
        }

        public /* bridge */ /* synthetic */ UUID getOptionalChatterUUID() {
            return super.getOptionalChatterUUID()
        }

         public fun getPictureID(userManager: UserManager, executor: Executor, onChatterPictureIDListener: OnChatterPictureIDListener): Subscription {
            return userManager.getCachedGroupProfiles().getPool().subscribe(this.uuid, UIThreadExecutor.getInstance(), $Lambda$0dEDWURupJXcv_HDGgfxSQl02DE(onChatterPictureIDListener))
        }

        public /* bridge */ /* synthetic */ Int hashCode() {
            return super.hashCode()
        }

        public /* bridge */ /* synthetic */ Boolean isValidUUID() {
            return super.isValidUUID()
        }

        public /* bridge */ /* synthetic */ Bundle toBundle() {
            return super.toBundle()
        }

        public /* bridge */ /* synthetic */ String toString() {
            return super.toString()
        }

        public /* bridge */ /* synthetic */ Unit writeToParcel(Parcel parcel, Int i) {
            super.writeToParcel(parcel, i)
        }
    }

    @JvmStatic
    class ChatterIDLocal : ChatterID() {
        const val Parcelable.Creator<ChatterIDLocal> CREATOR = Parcelable.Creator<ChatterIDLocal>() {
             public fun createFromParcel(parcel: Parcel): ChatterIDLocal {
                return ChatterIDLocal(parcel, (ChatterIDLocal) null)
            }

            public Array<ChatterIDLocal> newArray(Int i) {
                return ChatterIDLocal[i]
            }
        }

        private ChatterIDLocal(Parcel parcel) {
            super(parcel, (ChatterID) null)
        }

        /* synthetic */ ChatterIDLocal(Parcel parcel, ChatterIDLocal chatterIDLocal) {
            this(parcel)
        }

        private ChatterIDLocal(UUID uuid) {
            super(uuid, (ChatterID) null)
        }

        /* synthetic */ ChatterIDLocal(UUID uuid, ChatterIDLocal chatterIDLocal) {
            this(uuid)
        }

         public fun equals(obj: Object): Boolean {
            if (obj instanceof ChatterIDLocal) {
                return ChatterID.super.equals(obj)
            }
            return false
        }

         public fun getChatterType(): ChatterType {
            return ChatterType.Local
        }
    }

    @JvmStatic
    class ChatterIDUser : ChatterIDWithUUID() {
        const val Parcelable.Creator<ChatterIDUser> CREATOR = Parcelable.Creator<ChatterIDUser>() {
             public fun createFromParcel(parcel: Parcel): ChatterIDUser {
                return ChatterIDUser(parcel, (ChatterIDUser) null)
            }

            public Array<ChatterIDUser> newArray(Int i) {
                return ChatterIDUser[i]
            }
        }

        private ChatterIDUser(Parcel parcel) {
            super(parcel, (ChatterIDWithUUID) null)
        }

        /* synthetic */ ChatterIDUser(Parcel parcel, ChatterIDUser chatterIDUser) {
            this(parcel)
        }

        private ChatterIDUser(UUID uuid, UUID uuid2) {
            super(uuid, uuid2, (ChatterIDWithUUID) null)
        }

        /* synthetic */ ChatterIDUser(UUID uuid, UUID uuid2, ChatterIDUser chatterIDUser) {
            this(uuid, uuid2)
        }

        public /* bridge */ /* synthetic */ Int compareTo(ChatterID chatterID) {
            return super.compareTo(chatterID)
        }

         public fun equals(obj: Object): Boolean {
            if (obj instanceof ChatterIDUser) {
                return super.equals(obj)
            }
            return false
        }

         public fun getChatterType(): ChatterType {
            return ChatterType.User
        }

        public /* bridge */ /* synthetic */ UUID getChatterUUID() {
            return super.getChatterUUID()
        }

        public /* bridge */ /* synthetic */ UUID getOptionalChatterUUID() {
            return super.getOptionalChatterUUID()
        }

         public fun getPictureID(userManager: UserManager, executor: Executor, onChatterPictureIDListener: OnChatterPictureIDListener): Subscription {
            return userManager.getAvatarProperties().getPool().subscribe(this.uuid, executor, Subscription.OnData(onChatterPictureIDListener) {

                /* renamed from: -$f0 */
                private val /* synthetic */ Object f145$f0

                private val /* synthetic */ Unit $m$0(
/*
Method generation error in method: com.lumiyaviewer.lumiya.slproto.users.-$Lambda$0dEDWURupJXcv_HDGgfxSQl02DE.1.$m$0(java.lang.Object):Unit, dex: classes.dex
                jadx.core.utils.exceptions.JadxRuntimeException: Method args not loaded: com.lumiyaviewer.lumiya.slproto.users.-$Lambda$0dEDWURupJXcv_HDGgfxSQl02DE.1.$m$0(java.lang.Object):Unit, class status: UNLOADED
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

        public /* bridge */ /* synthetic */ Int hashCode() {
            return super.hashCode()
        }

        public /* bridge */ /* synthetic */ Boolean isValidUUID() {
            return super.isValidUUID()
        }

        public /* bridge */ /* synthetic */ Bundle toBundle() {
            return super.toBundle()
        }

        public /* bridge */ /* synthetic */ String toString() {
            return super.toString()
        }

        public /* bridge */ /* synthetic */ Unit writeToParcel(Parcel parcel, Int i) {
            super.writeToParcel(parcel, i)
        }
    }

    @JvmStatic
private abstract class ChatterIDWithUUID : ChatterID() {
        protected val UUID uuid

        private ChatterIDWithUUID(Parcel parcel) {
            super(parcel, (ChatterID) null)
            this.uuid = UUIDPool.getUUID(parcel.readLong(), parcel.readLong())
        }

        /* synthetic */ ChatterIDWithUUID(Parcel parcel, ChatterIDWithUUID chatterIDWithUUID) {
            this(parcel)
        }

        private ChatterIDWithUUID(UUID uuid2, UUID uuid3) {
            super(uuid2, (ChatterID) null)
            this.uuid = uuid3 == null ? UUIDPool.ZeroUUID : uuid3
        }

        /* synthetic */ ChatterIDWithUUID(UUID uuid2, UUID uuid3, ChatterIDWithUUID chatterIDWithUUID) {
            this(uuid2, uuid3)
        }

         public fun compareTo(chatterID: ChatterID): Int {
            val compareTo: Int = ChatterID.super.compareTo(chatterID)
            if (compareTo != 0) {
                return compareTo
            }
            if (chatterID instanceof ChatterIDWithUUID) {
                return this.uuid.compareTo(((ChatterIDWithUUID) chatterID).uuid)
            }
            return 0
        }

         public fun equals(obj: Object): Boolean {
            if (!ChatterID.super.equals(obj) || !(obj instanceof ChatterIDWithUUID)) {
                return false
            }
            return Objects.equal(this.uuid, ((ChatterIDWithUUID) obj).uuid)
        }

         public fun getChatterUUID(): UUID {
            return this.uuid
        }

         public fun getOptionalChatterUUID(): UUID {
            return this.uuid
        }

         public fun hashCode(): Int {
            return (this.uuid != null ? this.uuid.hashCode() : 0) + ChatterID.super.hashCode()
        }

         public fun isValidUUID(): Boolean {
            if (this.uuid != null) {
                return !UUIDPool.ZeroUUID.equals(this.uuid)
            }
            return false
        }

         public fun toBundle(): Bundle {
            val bundle: Bundle = ChatterID.super.toBundle()
            bundle.putString("chatterUUID", this.uuid != null ? this.uuid.toString() : UUIDPool.ZeroUUID.toString())
            return bundle
        }

         public fun toString(): String {
            return ChatterID.super.toString() + ":" + (this.uuid != null ? this.uuid.toString() : "null")
        }

        fun writeToParcel(parcel: Parcel, i: Int) {
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

    enum class ChatterType {
        Local(NotificationType.LocalChat),
        User(NotificationType.Private),
        Group(NotificationType.Group)
        
        const val Array<ChatterType> VALUES = null
        private val NotificationType notificationType

        static {
            VALUES = values()
        }

        private ChatterType(NotificationType notificationType2) {
            this.notificationType = notificationType2
        }

         public fun getNotificationType(): NotificationType {
            return this.notificationType
        }
    }

    interface OnChatterPictureIDListener {
         fun onChatterPictureID(uuid: UUID)
    }

    /* renamed from: -getcom-lumiyaviewer-lumiya-slproto-users-ChatterID$ChatterTypeSwitchesValues  reason: not valid java name */
    @JvmStatic
private /* synthetic */ IntArray m259getcomlumiyaviewerlumiyaslprotousersChatterID$ChatterTypeSwitchesValues() {
        if (f150comlumiyaviewerlumiyaslprotousersChatterID$ChatterTypeSwitchesValues != null) {
            return f150comlumiyaviewerlumiyaslprotousersChatterID$ChatterTypeSwitchesValues
        }
        val iArr: IntArray = Int[ChatterType.values().length]
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

    private ChatterID(UUID uuid) {
        this.agentUUID = uuid
    }

    /* synthetic */ ChatterID(UUID uuid, ChatterID chatterID) {
        this(uuid)
    }

    @JvmStatic
     fun fromBundle(bundle: Bundle): ChatterID {
        val fromString: UUID = UUID.fromString(bundle.getString("chatterAgentUUID"))
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

    @JvmStatic
     fun fromDatabaseObject(uuid: UUID, chatter: Chatter): ChatterID {
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

    @JvmStatic
     fun getGroupChatterID(uuid: UUID, uuid2: UUID): ChatterID {
        return ChatterIDGroup(uuid, uuid2, (ChatterIDGroup) null)
    }

    @JvmStatic
     fun getLocalChatterID(uuid: UUID): ChatterID {
        return ChatterIDLocal(uuid, (ChatterIDLocal) null)
    }

    @JvmStatic
     fun getUserChatterID(uuid: UUID, uuid2: UUID): ChatterIDUser {
        return ChatterIDUser(uuid, uuid2, (ChatterIDUser) null)
    }

     public fun compareTo(chatterID: ChatterID): Int {
        val compareTo: Int = this.agentUUID.compareTo(chatterID.agentUUID)
        return compareTo != 0 ? compareTo : getChatterType().compareTo(chatterID.getChatterType())
    }

     public fun describeContents(): Int {
        return 0
    }

     public fun equals(obj: Object): Boolean {
        if (obj instanceof ChatterID) {
            return ((ChatterID) obj).agentUUID.equals(this.agentUUID)
        }
        return false
    }

    public abstract ChatterType getChatterType()

     public fun getConnection(): SLGridConnection {
        return GridConnectionManager.getConnection(this.agentUUID)
    }

     public fun getOptionalChatterUUID(): UUID {
        return null
    }

     public fun getPictureID(userManager: UserManager, executor: Executor, onChatterPictureIDListener: OnChatterPictureIDListener): Subscription {
        return null
    }

     public fun getUserManager(): UserManager {
        return UserManager.getUserManager(this.agentUUID)
    }

     public fun hashCode(): Int {
        return this.agentUUID.hashCode() + 1 + getChatterType().ordinal()
    }

     public fun isValidUUID(): Boolean {
        return false
    }

     public fun toBundle(): Bundle {
        val bundle: Bundle = Bundle()
        bundle.putInt("chatterType", getChatterType().ordinal())
        bundle.putString("chatterAgentUUID", this.agentUUID.toString())
        return bundle
    }

    fun toDatabaseObject(chatter: Chatter) {
        chatter.setType(getChatterType().ordinal())
        chatter.setUuid(getOptionalChatterUUID())
    }

     public fun toString(): String {
        return "Chatter:" + getChatterType().toString()
    }

    fun writeToParcel(parcel: Parcel, i: Int) {
        parcel.writeLong(this.agentUUID.getMostSignificantBits())
        parcel.writeLong(this.agentUUID.getLeastSignificantBits())
    }
}
