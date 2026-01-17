package com.lumiyaviewer.lumiya.ui.inventory;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.R;
import com.lumiyaviewer.lumiya.openjpeg.OpenJPEG;
import com.lumiyaviewer.lumiya.render.tex.DrawableTextureParams;
import com.lumiyaviewer.lumiya.render.tex.TextureClass;
import com.lumiyaviewer.lumiya.res.ResourceConsumer;
import com.lumiyaviewer.lumiya.res.textures.TextureCache;
import com.lumiyaviewer.lumiya.ui.common.LoadingLayout;
import com.lumiyaviewer.lumiya.ui.common.StateAwareFragment;
import com.lumiyaviewer.lumiya.utils.UUIDPool;
import java.util.UUID;
import javax.annotation.Nullable;
import uk.co.senab.photoview.PhotoViewAttacher;
/* loaded from: classes.dex */
public class TextureViewFragment extends StateAwareFragment {
    private static final String ASSET_UUID_KEY = "assetUUID";
    @Nullable
    private LoadAssetImageTask loadAssetImageTask = null;
    private LoadingLayout loadingLayout;
    private PhotoViewAttacher photoViewAttacher;
    private ImageView textureImageView;

    /* loaded from: classes.dex */
    private class LoadAssetImageTask extends AsyncTask<UUID, Void, Bitmap> implements ResourceConsumer {
        private volatile OpenJPEG texture;
        private final Object textureReady;

        private LoadAssetImageTask() {
            this.textureReady = new Object();
        }

        /* synthetic */ LoadAssetImageTask(TextureViewFragment textureViewFragment, LoadAssetImageTask loadAssetImageTask) {
            this();
        }

        @Override // com.lumiyaviewer.lumiya.res.ResourceConsumer
        public void OnResourceReady(Object obj, boolean z) {
            if (obj instanceof OpenJPEG) {
                this.texture = (OpenJPEG) obj;
            }
            synchronized (this.textureReady) {
                this.textureReady.notify();
            }
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public Bitmap doInBackground(UUID... uuidArr) {
            Debug.Printf("loading asset ID %s", uuidArr[0].toString());
            TextureCache.getInstance().RequestResource(DrawableTextureParams.create(uuidArr[0], TextureClass.Asset), this);
            synchronized (this.textureReady) {
                if (this.texture == null) {
                    Debug.Printf("asset ID %s is not available, waiting", uuidArr[0].toString());
                    try {
                        this.textureReady.wait();
                        Debug.Printf("done waiting for asset ID %s", uuidArr[0].toString());
                    } catch (InterruptedException e) {
                        Debug.Printf("interrupted while waiting for asset ID %s", uuidArr[0].toString());
                        return null;
                    }
                } else {
                    Debug.Printf("asset ID %s is already available", uuidArr[0].toString());
                }
            }
            if (this.texture != null) {
                return this.texture.getAsBitmap();
            }
            return null;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public void onPostExecute(Bitmap bitmap) {
            if (TextureViewFragment.this.isFragmentStarted() && TextureViewFragment.this.textureImageView != null && TextureViewFragment.this.loadingLayout != null) {
                if (bitmap != null) {
                    TextureViewFragment.this.loadingLayout.showContent(null);
                    TextureViewFragment.this.textureImageView.setImageBitmap(bitmap);
                    TextureViewFragment.this.photoViewAttacher.update();
                } else {
                    TextureViewFragment.this.loadingLayout.showMessage(TextureViewFragment.this.getString(R.string.failed_to_download_texture));
                    TextureViewFragment.this.textureImageView.setImageBitmap(null);
                    TextureViewFragment.this.photoViewAttacher.update();
                }
            }
            TextureViewFragment.this.loadAssetImageTask = null;
        }

        @Override // android.os.AsyncTask
        protected void onPreExecute() {
            if (!TextureViewFragment.this.isFragmentStarted() || TextureViewFragment.this.loadingLayout == null) {
                return;
            }
            TextureViewFragment.this.loadingLayout.showLoading();
        }
    }

    public static Bundle makeArguments(UUID uuid, UUID uuid2) {
        Bundle bundle = new Bundle();
        bundle.putString("activeAgentUUID", uuid.toString());
        bundle.putString(ASSET_UUID_KEY, uuid2.toString());
        return bundle;
    }

    @Override // android.support.v4.app.Fragment
    @Nullable
    public View onCreateView(LayoutInflater layoutInflater, @Nullable ViewGroup viewGroup, @Nullable Bundle bundle) {
        View inflate = layoutInflater.inflate(R.layout.texture_view_fragment, viewGroup, false);
        this.loadingLayout = (LoadingLayout) inflate.findViewById(R.id.loading_layout);
        this.textureImageView = (ImageView) inflate.findViewById(R.id.texture_image_view);
        this.photoViewAttacher = new PhotoViewAttacher(this.textureImageView);
        return inflate;
    }

    @Override // com.lumiyaviewer.lumiya.ui.common.StateAwareFragment, android.support.v4.app.Fragment
    public void onStart() {
        super.onStart();
        UUID uuid = UUIDPool.getUUID(getArguments().getString(ASSET_UUID_KEY));
        if (uuid != null) {
            if (this.loadAssetImageTask != null) {
                this.loadAssetImageTask.cancel(true);
                this.loadAssetImageTask = null;
            }
            this.loadAssetImageTask = new LoadAssetImageTask(this, null);
            this.loadAssetImageTask.execute(uuid);
        }
    }

    @Override // com.lumiyaviewer.lumiya.ui.common.StateAwareFragment, android.support.v4.app.Fragment
    public void onStop() {
        if (this.loadAssetImageTask != null) {
            this.loadAssetImageTask.cancel(true);
            this.loadAssetImageTask = null;
        }
        super.onStop();
    }
}
