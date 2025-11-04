// 
// Decompiled by Procyon v0.6.0
// 

package android.support.v4.media;

import android.support.annotation.NonNull;
import java.lang.reflect.InvocationTargetException;
import android.util.Log;
import android.media.AudioAttributes;
import java.lang.reflect.Method;
import android.support.annotation.RequiresApi;

@RequiresApi(21)
class AudioAttributesCompatApi21
{
    private static final String TAG = "AudioAttributesCompat";
    private static Method sAudioAttributesToLegacyStreamType;
    
    public static int toLegacyStreamType(final Wrapper wrapper) {
        final AudioAttributes unwrap = wrapper.unwrap();
        try {
            if (AudioAttributesCompatApi21.sAudioAttributesToLegacyStreamType == null) {
                AudioAttributesCompatApi21.sAudioAttributesToLegacyStreamType = AudioAttributes.class.getMethod("toLegacyStreamType", AudioAttributes.class);
            }
            return (int)AudioAttributesCompatApi21.sAudioAttributesToLegacyStreamType.invoke(null, unwrap);
        }
        catch (final NoSuchMethodException | InvocationTargetException | IllegalAccessException | ClassCastException ex) {
            Log.w("AudioAttributesCompat", "getLegacyStreamType() failed on API21+", (Throwable)ex);
            return -1;
        }
    }
    
    static final class Wrapper
    {
        private AudioAttributes mWrapped;
        
        private Wrapper(final AudioAttributes mWrapped) {
            this.mWrapped = mWrapped;
        }
        
        public static Wrapper wrap(@NonNull final AudioAttributes audioAttributes) {
            if (audioAttributes != null) {
                return new Wrapper(audioAttributes);
            }
            throw new IllegalArgumentException("AudioAttributesApi21.Wrapper cannot wrap null");
        }
        
        public AudioAttributes unwrap() {
            return this.mWrapped;
        }
    }
}
