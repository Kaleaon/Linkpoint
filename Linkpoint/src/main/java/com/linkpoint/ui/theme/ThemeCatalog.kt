package com.linkpoint.ui.theme

object ThemeCatalog {
    enum class Family(val displayName: String) {
        LINKPOINT("Linkpoint"),
        VIEWER_INSPIRED("Viewer Inspired"),
        METALLIC_DARK("Metallic Dark"),
        COMMUNITY("Community")
    }

    fun familyFor(theme: ThemePack): Family {
        if (!theme.isBuiltIn) return Family.COMMUNITY
        return when (theme.id) {
            BuiltInThemes.LINKPOINT_DEFAULT.id -> Family.LINKPOINT
            BuiltInThemes.SL_CLASSIC.id,
            BuiltInThemes.FIRESTORM.id -> Family.VIEWER_INSPIRED
            else -> Family.METALLIC_DARK
        }
    }
}
