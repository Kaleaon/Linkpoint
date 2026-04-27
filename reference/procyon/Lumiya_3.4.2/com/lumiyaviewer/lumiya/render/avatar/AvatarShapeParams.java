// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.render.avatar;

import java.util.Arrays;
import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.slproto.messages.AvatarAppearance;
import javax.annotation.Nullable;
import javax.annotation.Nonnull;

public class AvatarShapeParams
{
    @Nonnull
    private final int[] visualParamValues;
    
    private AvatarShapeParams(@Nonnull final int[] visualParamValues) {
        this.visualParamValues = visualParamValues;
    }
    
    @Nonnull
    public static AvatarShapeParams create(@Nullable final AvatarShapeParams avatarShapeParams, final AvatarAppearance avatarAppearance) {
        Debug.Log("DrawableAvatar: new appearance for avatar " + avatarAppearance.Sender_Field.ID + ", numParams = " + avatarAppearance.VisualParam_Fields.size() + ", appData = " + avatarAppearance.AppearanceData_Fields.size());
        for (int i = 0; i < avatarAppearance.AppearanceData_Fields.size(); ++i) {
            Debug.Printf("appData[%d]: appVer %d, cofVer %d, flags 0x%x", i, ((AvatarAppearance.AppearanceData)avatarAppearance.AppearanceData_Fields.get(i)).AppearanceVersion, ((AvatarAppearance.AppearanceData)avatarAppearance.AppearanceData_Fields.get(i)).CofVersion, ((AvatarAppearance.AppearanceData)avatarAppearance.AppearanceData_Fields.get(i)).Flags);
        }
        final int[] array = new int[218];
        for (int j = 0; j < 218; ++j) {
            if (j < avatarAppearance.VisualParam_Fields.size()) {
                array[j] = ((AvatarAppearance.VisualParam)avatarAppearance.VisualParam_Fields.get(j)).ParamValue;
            }
            else {
                int n;
                if (avatarShapeParams != null) {
                    n = avatarShapeParams.visualParamValues[j];
                }
                else {
                    n = 0;
                }
                array[j] = n;
            }
        }
        return new AvatarShapeParams(array);
    }
    
    @Nonnull
    public static AvatarShapeParams create(@Nullable final AvatarShapeParams avatarShapeParams, int[] array) {
        if (array.length != 218) {
            final int[] array2 = new int[218];
            System.arraycopy(array, 0, array2, 0, Math.min(array.length, 218));
            if (array.length < 218 && avatarShapeParams != null) {
                System.arraycopy(avatarShapeParams.visualParamValues, array.length, array2, array.length, 218 - array.length);
                array = array2;
            }
            else {
                array = array2;
            }
        }
        return new AvatarShapeParams(array);
    }
    
    @Override
    public boolean equals(final Object o) {
        return o instanceof AvatarShapeParams && Arrays.equals(this.visualParamValues, ((AvatarShapeParams)o).visualParamValues);
    }
    
    public int getParamCount() {
        return 218;
    }
    
    public int getParamValue(final int n) {
        if (n >= 0 && n < 218) {
            return this.visualParamValues[n];
        }
        return 0;
    }
    
    @Override
    public int hashCode() {
        return Arrays.hashCode(this.visualParamValues);
    }
}
