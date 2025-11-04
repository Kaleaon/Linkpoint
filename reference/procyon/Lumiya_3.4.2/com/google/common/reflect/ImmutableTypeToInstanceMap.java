// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.reflect;

import java.util.Map;
import com.google.common.collect.ImmutableMap;
import com.google.common.annotations.Beta;
import com.google.common.collect.ForwardingMap;

@Beta
public final class ImmutableTypeToInstanceMap<B> extends ForwardingMap<TypeToken<? extends B>, B> implements TypeToInstanceMap<B>
{
    private final ImmutableMap<TypeToken<? extends B>, B> delegate;
    
    private ImmutableTypeToInstanceMap(final ImmutableMap<TypeToken<? extends B>, B> delegate) {
        this.delegate = delegate;
    }
    
    public static <B> Builder<B> builder() {
        return new Builder<B>();
    }
    
    public static <B> ImmutableTypeToInstanceMap<B> of() {
        return new ImmutableTypeToInstanceMap<B>(ImmutableMap.of());
    }
    
    private <T extends B> T trustedGet(final TypeToken<T> typeToken) {
        return (T)this.delegate.get(typeToken);
    }
    
    @Override
    protected Map<TypeToken<? extends B>, B> delegate() {
        return this.delegate;
    }
    
    @Override
    public <T extends B> T getInstance(final TypeToken<T> typeToken) {
        return (T)this.trustedGet((TypeToken<Object>)typeToken.rejectTypeVariables());
    }
    
    @Override
    public <T extends B> T getInstance(final Class<T> clazz) {
        return this.trustedGet((TypeToken<T>)TypeToken.of((Class<T>)clazz));
    }
    
    @Override
    public <T extends B> T putInstance(final TypeToken<T> typeToken, final T t) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public <T extends B> T putInstance(final Class<T> clazz, final T t) {
        throw new UnsupportedOperationException();
    }
    
    @Beta
    public static final class Builder<B>
    {
        private final ImmutableMap.Builder<TypeToken<? extends B>, B> mapBuilder;
        
        private Builder() {
            this.mapBuilder = ImmutableMap.builder();
        }
        
        public ImmutableTypeToInstanceMap<B> build() {
            return new ImmutableTypeToInstanceMap<B>(this.mapBuilder.build(), null);
        }
        
        public <T extends B> Builder<B> put(final TypeToken<T> typeToken, final T t) {
            this.mapBuilder.put((TypeToken<? extends B>)typeToken.rejectTypeVariables(), t);
            return this;
        }
        
        public <T extends B> Builder<B> put(final Class<T> clazz, final T t) {
            this.mapBuilder.put(TypeToken.of((Class<? extends B>)clazz), t);
            return this;
        }
    }
}
