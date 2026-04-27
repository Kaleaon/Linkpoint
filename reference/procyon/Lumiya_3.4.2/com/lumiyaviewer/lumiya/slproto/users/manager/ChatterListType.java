// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.users.manager;

public enum ChatterListType
{
    Active("Active", 2), 
    Friends("Friends", 1), 
    FriendsOnline("FriendsOnline", 0), 
    Groups("Groups", 3), 
    Nearby("Nearby", 4);
    
    private ChatterListType(final String name, final int ordinal) {
    }
}
