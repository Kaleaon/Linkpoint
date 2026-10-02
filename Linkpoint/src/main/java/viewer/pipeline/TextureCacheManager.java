package viewer.pipeline;

import com.linkpoint.assets.TextureDecoder;

/**
 * Pipeline adapter for TextureCacheManager.
 */
public class TextureCacheManager extends com.linkpoint.assets.TextureCacheManager {

    public TextureCacheManager() {
        super();
    }

    public TextureCacheManager(int maxSizeBytes) {
        super(maxSizeBytes);
    }
}
