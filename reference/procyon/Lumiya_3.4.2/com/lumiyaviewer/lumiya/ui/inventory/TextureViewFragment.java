// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.ui.inventory;

import com.lumiyaviewer.lumiya.res.ResourceMemoryCache;
import com.lumiyaviewer.lumiya.render.tex.DrawableTextureParams;
import com.lumiyaviewer.lumiya.render.tex.TextureClass;
import com.lumiyaviewer.lumiya.res.textures.TextureCache;
import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.openjpeg.OpenJPEG;
import com.lumiyaviewer.lumiya.res.ResourceConsumer;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import com.lumiyaviewer.lumiya.utils.UUIDPool;
import android.view.View;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.os.Bundle;
import java.util.UUID;
import android.widget.ImageView;
import uk.co.senab.photoview.PhotoViewAttacher;
import com.lumiyaviewer.lumiya.ui.common.LoadingLayout;
import javax.annotation.Nullable;
import com.lumiyaviewer.lumiya.ui.common.StateAwareFragment;

public class TextureViewFragment extends StateAwareFragment
{
    private static final String ASSET_UUID_KEY = "assetUUID";
    @Nullable
    private LoadAssetImageTask loadAssetImageTask;
    private LoadingLayout loadingLayout;
    private PhotoViewAttacher photoViewAttacher;
    private ImageView textureImageView;
    
    public TextureViewFragment() {
        this.loadAssetImageTask = null;
    }
    
    public static Bundle makeArguments(final UUID uuid, final UUID uuid2) {
        final Bundle bundle = new Bundle();
        bundle.putString("activeAgentUUID", uuid.toString());
        bundle.putString("assetUUID", uuid2.toString());
        return bundle;
    }
    
    @Nullable
    @Override
    public View onCreateView(final LayoutInflater layoutInflater, @Nullable final ViewGroup viewGroup, @Nullable final Bundle bundle) {
        final View inflate = layoutInflater.inflate(2130968746, viewGroup, false);
        this.loadingLayout = (LoadingLayout)inflate.findViewById(2131755197);
        this.textureImageView = (ImageView)inflate.findViewById(2131755667);
        this.photoViewAttacher = new PhotoViewAttacher(this.textureImageView);
        return inflate;
    }
    
    @Override
    public void onStart() {
        super.onStart();
        final UUID uuid = UUIDPool.getUUID(this.getArguments().getString("assetUUID"));
        if (uuid != null) {
            if (this.loadAssetImageTask != null) {
                this.loadAssetImageTask.cancel(true);
                this.loadAssetImageTask = null;
            }
            (this.loadAssetImageTask = new LoadAssetImageTask((LoadAssetImageTask)null)).execute((Object[])new UUID[] { uuid });
        }
    }
    
    @Override
    public void onStop() {
        if (this.loadAssetImageTask != null) {
            this.loadAssetImageTask.cancel(true);
            this.loadAssetImageTask = null;
        }
        super.onStop();
    }
    
    private class LoadAssetImageTask extends AsyncTask<UUID, Void, Bitmap> implements ResourceConsumer
    {
        private volatile OpenJPEG texture;
        private final Object textureReady;
        
        private LoadAssetImageTask() {
            this.textureReady = new Object();
        }
        
        public void OnResourceReady(final Object o, final boolean b) {
            if (o instanceof OpenJPEG) {
                this.texture = (OpenJPEG)o;
            }
            synchronized (this.textureReady) {
                this.textureReady.notify();
            }
        }
        
        protected Bitmap doInBackground(final UUID... array) {
            Debug.Printf("loading asset ID %s", array[0].toString());
            ((ResourceMemoryCache<DrawableTextureParams, ResourceType>)TextureCache.getInstance()).RequestResource(DrawableTextureParams.create(array[0], TextureClass.Asset), this);
            synchronized (this.textureReady) {
                while (true) {
                    Label_0131: {
                        if (this.texture != null) {
                            break Label_0131;
                        }
                        Debug.Printf("asset ID %s is not available, waiting", array[0].toString());
                        try {
                            this.textureReady.wait();
                            Debug.Printf("done waiting for asset ID %s", array[0].toString());
                            monitorexit(this.textureReady);
                            if (this.texture != null) {
                                return this.texture.getAsBitmap();
                            }
                            return null;
                        }
                        catch (final InterruptedException ex) {
                            Debug.Printf("interrupted while waiting for asset ID %s", array[0].toString());
                            return null;
                        }
                    }
                    Debug.Printf("asset ID %s is already available", array[0].toString());
                    continue;
                }
            }
            return null;
        }
        
        protected void onPostExecute(final Bitmap imageBitmap) {
            if (TextureViewFragment.this.isFragmentStarted() && TextureViewFragment.this.textureImageView != null && TextureViewFragment.this.loadingLayout != null) {
                if (imageBitmap != null) {
                    TextureViewFragment.this.loadingLayout.showContent(null);
                    TextureViewFragment.this.textureImageView.setImageBitmap(imageBitmap);
                    TextureViewFragment.this.photoViewAttacher.update();
                }
                else {
                    TextureViewFragment.this.loadingLayout.showMessage(TextureViewFragment.this.getString(2131296540));
                    TextureViewFragment.this.textureImageView.setImageBitmap((Bitmap)null);
                    TextureViewFragment.this.photoViewAttacher.update();
                }
            }
            TextureViewFragment.this.loadAssetImageTask = null;
        }
        
        protected void onPreExecute() {
            if (TextureViewFragment.this.isFragmentStarted() && TextureViewFragment.this.loadingLayout != null) {
                TextureViewFragment.this.loadingLayout.showLoading();
            }
        }
    }
}
