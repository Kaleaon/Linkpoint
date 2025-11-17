package com.linkpoint.render.tex

import com.linkpoint.slproto.avatar.AvatarTextureFaceIndex
import java.util.UUID

internal class AutoValue_DrawableTextureParams(
    private val uuidValue: UUID,
    private val textureClassValue: TextureClass,
    private val avatarFaceIndexValue: AvatarTextureFaceIndex?,
    private val avatarUuidValue: UUID?,
) : DrawableTextureParams() {

    init {
        requireNotNull(uuidValue) { "Null uuid" }
        requireNotNull(textureClassValue) { "Null textureClass" }
    }

    override fun uuid(): UUID = uuidValue

    override fun textureClass(): TextureClass = textureClassValue

    override fun avatarFaceIndex(): AvatarTextureFaceIndex? = avatarFaceIndexValue

    override fun avatarUUID(): UUID? = avatarUuidValue

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is DrawableTextureParams) return false

        return uuidValue == other.uuid() &&
            textureClassValue == other.textureClass() &&
            avatarFaceIndexValue == other.avatarFaceIndex() &&
            avatarUuidValue == other.avatarUUID()
    }

    override fun hashCode(): Int {
        var result = uuidValue.hashCode()
        result = 31 * result + textureClassValue.hashCode()
        result = 31 * result + (avatarFaceIndexValue?.hashCode() ?: 0)
        result = 31 * result + (avatarUuidValue?.hashCode() ?: 0)
        return result
    }

    override fun toString(): String = buildString {
        append("DrawableTextureParams{uuid=")
        append(uuidValue)
        append(", textureClass=")
        append(textureClassValue)
        append(", avatarFaceIndex=")
        append(avatarFaceIndexValue)
        append(", avatarUUID=")
        append(avatarUuidValue)
        append('}')
    }
}
