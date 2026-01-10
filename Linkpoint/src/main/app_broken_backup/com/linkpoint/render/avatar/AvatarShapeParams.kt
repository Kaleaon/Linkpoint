package com.linkpoint.render.avatar

import kotlin.math.*

import com.linkpoint.Debug
import com.linkpoint.slproto.messages.AvatarAppearance
import com.linkpoint.slproto.messages.AvatarAppearance.AppearanceData
import com.linkpoint.slproto.messages.AvatarAppearance.VisualParam
import java.util.Arrays
import androidx.annotation.NonNull
import androidx.annotation.Nullable

object AvatarShapeParams {
    @NonNull
    private int[] visualParamValues

    private AvatarShapeParams(@NonNull int[] iArr) {
        this.visualParamValues = iArr
    }

    @NonNull
    fun create(avatarShapeParams: AvatarShapeParams, avatarAppearance: AvatarAppearance): AvatarShapeParams {
        Debug.Log("DrawableAvatar: new appearance for avatar " + avatarAppearance.Sender_Field.ID + ", numParams = " + avatarAppearance.VisualParam_Fields.size() + ", appData = " + avatarAppearance.AppearanceData_Fields.size())
        for (i = 0; i < avatarAppearance.AppearanceData_Fields.size(); i++) {
            Debug.Printf("appData[%d]: appVer %d, cofVer %d, flags 0x%x", Integer.valueOf(i), Integer.valueOf(((AppearanceData) avatarAppearance.AppearanceData_Fields.get(i)).AppearanceVersion), Integer.valueOf(((AppearanceData) avatarAppearance.AppearanceData_Fields.get(i)).CofVersion), Integer.valueOf(((AppearanceData) avatarAppearance.AppearanceData_Fields.get(i)).Flags))
        }
        int[] iArr = IntArray(218)
        i = 0
        while (i < 218) {
            if (i < avatarAppearance.VisualParam_Fields.size()) {
                iArr[i] = ((VisualParam) avatarAppearance.VisualParam_Fields.get(i)).ParamValue
            } else {
                iArr[i] = avatarShapeParams != null ? avatarShapeParams.visualParamValues[i] : 0
            }
            i++
        }
        return AvatarShapeParams(iArr)
    }

    @NonNull
    fun create(avatarShapeParams: AvatarShapeParams, iArr: IntArray): AvatarShapeParams {
        if (iArr.length != 218) {
            Object obj = IntArray(218)
            System.arraycopy(iArr, 0, obj, 0, min(iArr.length, 218))
            Object iArr2
            if (iArr2.length >= 218 || avatarShapeParams == null) {
                iArr2 = obj
            } else {
                System.arraycopy(avatarShapeParams.visualParamValues, iArr2.length, obj, iArr2.length, 218 - iArr2.length)
                iArr2 = obj
            }
        }
        return AvatarShapeParams(iArr2)
    }

    fun equals(obj: Any): Boolean {
        return obj is AvatarShapeParams ? Arrays.equals(this.visualParamValues, ((AvatarShapeParams) obj).visualParamValues) : false
    }

    fun getParamCount(): Int {
        return 218
    }

    fun getParamValue(i: Int): Int {
        return (i < 0 || i >= 218) ? 0 : this.visualParamValues[i]
    }

    fun hashCode(): Int {
        return Arrays.hashCode(this.visualParamValues)
    }
}
