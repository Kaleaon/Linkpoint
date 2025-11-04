// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.render.avatar;

import java.util.Set;
import com.lumiyaviewer.lumiya.render.DrawableStore;
import com.lumiyaviewer.lumiya.slproto.textures.SLTextureEntry;
import com.lumiyaviewer.lumiya.slproto.messages.AvatarAppearance;
import java.util.Iterator;
import java.util.Collections;
import java.util.Collection;
import java.util.HashSet;
import com.lumiyaviewer.lumiya.slproto.messages.AvatarAnimation;
import com.lumiyaviewer.lumiya.slproto.objects.SLObjectInfo;
import com.lumiyaviewer.lumiya.render.spatial.SpatialIndex;
import com.lumiyaviewer.lumiya.Debug;
import java.util.concurrent.ConcurrentHashMap;
import com.lumiyaviewer.lumiya.slproto.objects.SLObjectAvatarInfo;
import java.util.Map;
import java.util.UUID;
import com.google.common.collect.ImmutableSet;

public class AvatarVisualState
{
    private static final ImmutableSet<UUID> basicAnimations;
    private static final UUID defaultStandingAnimation;
    private final UUID agentUUID;
    private final Map<UUID, AnimationSequenceInfo> animations;
    private final SLObjectAvatarInfo avatarObject;
    private volatile AvatarShapeParams avatarShapeParams;
    private final UUID avatarUUID;
    private final AvatarTextures textures;
    
    static {
        defaultStandingAnimation = UUID.fromString("2408fe9e-df1d-1d7d-f4ff-1384fa7b350f");
        basicAnimations = new ImmutableSet.Builder<UUID>().add(UUID.fromString("2408fe9e-df1d-1d7d-f4ff-1384fa7b350f")).add(UUID.fromString("15468e00-3400-bb66-cecc-646d7c14458e")).add(UUID.fromString("370f3a20-6ca6-9971-848c-9a01bc42ae3c")).add(UUID.fromString("42b46214-4b44-79ae-deb8-0df61424ff4b")).add(UUID.fromString("f22fed8b-a5ed-2c93-64d5-bdd8b93c889f")).add(UUID.fromString("201f3fdf-cb1f-dbec-201f-7333e328ae7c")).add(UUID.fromString("47f5f6fb-22e5-ae44-f871-73aaaf4a6022")).add(UUID.fromString("aec4610c-757f-bc4e-c092-c6e9caf18daf")).add(UUID.fromString("2b5a38b2-5e00-3a97-a495-4c826bc443e6")).add(UUID.fromString("4ae8016b-31b9-03bb-c401-b1ea941db41d")).add(UUID.fromString("20f063ea-8306-2562-0b07-5c853b37b31e")).add(UUID.fromString("62c5de58-cb33-5743-3d07-9e4cd4352864")).add(UUID.fromString("05ddbff8-aaa9-92a1-2b74-8fe77a29b445")).add(UUID.fromString("6ed24bd8-91aa-4b12-ccc7-c97c857ab4e0")).add(UUID.fromString("f5fc7433-043d-e819-8298-f519a119b688")).build();
    }
    
    public AvatarVisualState(final UUID agentUUID, final SLObjectAvatarInfo avatarObject, final UUID avatarUUID) {
        this.textures = new AvatarTextures();
        this.animations = new ConcurrentHashMap<UUID, AnimationSequenceInfo>();
        this.agentUUID = agentUUID;
        this.avatarObject = avatarObject;
        this.avatarUUID = avatarUUID;
    }
    
    private void startAnimation(final UUID uuid, int i, final long n, final DrawableAvatar drawableAvatar) {
        final AnimationSequenceInfo animationSequenceInfo = this.animations.get(uuid);
        AnimationSequenceInfo animationSequenceInfo2;
        if (animationSequenceInfo == null) {
            Debug.Printf("Anim: Starting new animation %s seqID %d", uuid.toString(), i);
            final AnimationSequenceInfo sequence = AnimationSequenceInfo.newSequence(uuid, n, i);
            this.animations.put(uuid, sequence);
            animationSequenceInfo2 = sequence;
            i = 1;
        }
        else if (i != animationSequenceInfo.sequenceID) {
            final AnimationSequenceInfo restartSequence = AnimationSequenceInfo.restartSequence(n, i, animationSequenceInfo);
            this.animations.put(uuid, restartSequence);
            animationSequenceInfo2 = restartSequence;
            i = 1;
        }
        else {
            animationSequenceInfo2 = animationSequenceInfo;
            i = 0;
        }
        if (i != 0 && drawableAvatar != null) {
            drawableAvatar.AnimationUpdate(animationSequenceInfo2);
        }
    }
    
    private void updateAvatarShape() {
        synchronized (this) {
            final DrawableAvatar drawableAvatar = SpatialIndex.getInstance().getDrawableAvatar(this.avatarObject);
            if (drawableAvatar != null) {
                drawableAvatar.UpdateShapeParams(this.avatarShapeParams);
            }
        }
    }
    
    private void updateTextures() {
        synchronized (this) {
            final DrawableAvatar drawableAvatar = SpatialIndex.getInstance().getDrawableAvatar(this.avatarObject);
            if (drawableAvatar != null) {
                drawableAvatar.UpdateTextures(this.textures);
            }
        }
    }
    
    public void ApplyAvatarAnimation(final AvatarAnimation avatarAnimation) {
        final long currentTimeMillis;
        final HashSet c1;
        final HashSet set;
        final DrawableAvatar drawableAvatar;
        synchronized (this) {
            currentTimeMillis = System.currentTimeMillis();
            c1 = new HashSet();
            set = new HashSet();
            set.addAll(this.animations.keySet());
            drawableAvatar = SpatialIndex.getInstance().getDrawableAvatar(this.avatarObject);
            for (final AvatarAnimation.AnimationList list : avatarAnimation.AnimationList_Fields) {
                final UUID animID = list.AnimID;
                c1.add(animID);
                set.remove(animID);
                this.startAnimation(animID, list.AnimSequenceID, currentTimeMillis, drawableAvatar);
            }
        }
        if (Collections.disjoint(c1, AvatarVisualState.basicAnimations)) {
            set.remove(AvatarVisualState.defaultStandingAnimation);
            this.startAnimation(AvatarVisualState.defaultStandingAnimation, 1, currentTimeMillis, drawableAvatar);
        }
        for (final UUID uuid : set) {
            AnimationSequenceInfo stopSequence = this.animations.get(uuid);
            if (stopSequence != null) {
                boolean b;
                int n;
                if (stopSequence.sequenceID != 0) {
                    stopSequence = AnimationSequenceInfo.stopSequence(currentTimeMillis, stopSequence);
                    if (stopSequence != null) {
                        this.animations.put(uuid, stopSequence);
                        b = true;
                        n = 0;
                    }
                    else {
                        b = true;
                        n = 1;
                    }
                }
                else {
                    b = false;
                    n = 0;
                }
                int n2 = n;
                if (stopSequence != null) {
                    n2 = (n | (stopSequence.hasStopped(currentTimeMillis) ? 1 : 0));
                }
                int n3 = n2;
                if (drawableAvatar != null) {
                    if (b && (n2 ^ 0x1) && stopSequence != null) {
                        drawableAvatar.AnimationUpdate(stopSequence);
                    }
                    n3 = (n2 | (drawableAvatar.IsAnimationStopped(uuid) ? 1 : 0));
                }
                if (n3 == 0) {
                    continue;
                }
                Debug.Printf("Anim: Stopping animation %s", uuid.toString());
                this.animations.remove(uuid);
                if (drawableAvatar == null) {
                    continue;
                }
                drawableAvatar.AnimationRemove(uuid);
            }
        }
        monitorexit(this);
    }
    
    public void ApplyAvatarAppearance(final AvatarAppearance avatarAppearance) {
        synchronized (this) {
            final AvatarShapeParams avatarShapeParams = this.avatarShapeParams;
            this.avatarShapeParams = AvatarShapeParams.create(avatarShapeParams, avatarAppearance);
            if (!this.avatarShapeParams.equals(avatarShapeParams)) {
                this.updateAvatarShape();
            }
            if (this.textures.ApplyAvatarAppearance(avatarAppearance)) {
                this.updateTextures();
            }
        }
    }
    
    public void ApplyTextures(final SLTextureEntry slTextureEntry, final boolean b) {
        synchronized (this) {
            if (this.textures.ApplyTextures(slTextureEntry, b)) {
                this.updateTextures();
            }
        }
    }
    
    public void ApplyVisualParams(final int[] array) {
        synchronized (this) {
            final AvatarShapeParams avatarShapeParams = this.avatarShapeParams;
            this.avatarShapeParams = AvatarShapeParams.create(avatarShapeParams, array);
            if (!this.avatarShapeParams.equals(avatarShapeParams)) {
                this.updateAvatarShape();
            }
        }
    }
    
    public DrawableAvatar createDrawableAvatar(final DrawableStore drawableStore) {
        synchronized (this) {
            final DrawableAvatar drawableAvatar = new DrawableAvatar(drawableStore, this.agentUUID, this.avatarObject, this.avatarUUID, this.animations);
            drawableAvatar.UpdateShapeParams(this.avatarShapeParams);
            drawableAvatar.UpdateTextures(this.textures);
            return drawableAvatar;
        }
    }
    
    public DrawableAvatarStub createDrawableAvatarStub(final DrawableStore drawableStore) {
        synchronized (this) {
            return new DrawableAvatarStub(drawableStore, this.agentUUID, this.avatarObject);
        }
    }
    
    public Set<UUID> getRunningAnimations() {
        synchronized (this) {
            return this.animations.keySet();
        }
    }
}
