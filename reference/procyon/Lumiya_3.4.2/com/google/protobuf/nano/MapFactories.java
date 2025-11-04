// 
// Decompiled by Procyon v0.6.0
// 

package com.google.protobuf.nano;

import java.util.HashMap;
import java.util.Map;

public final class MapFactories
{
    private static volatile MapFactory mapFactory;
    
    static {
        MapFactories.mapFactory = (MapFactory)new DefaultMapFactory();
    }
    
    private MapFactories() {
    }
    
    public static MapFactory getMapFactory() {
        return MapFactories.mapFactory;
    }
    
    static void setMapFactory(final MapFactory mapFactory) {
        MapFactories.mapFactory = mapFactory;
    }
    
    private static class DefaultMapFactory implements MapFactory
    {
        @Override
        public <K, V> Map<K, V> forMap(final Map<K, V> map) {
            if (map != null) {
                return map;
            }
            return new HashMap<K, V>();
        }
    }
    
    public interface MapFactory
    {
         <K, V> Map<K, V> forMap(final Map<K, V> p0);
    }
}
