package com.linkpoint.ui.inventory

import android.graphics.Bitmap
import android.os.AsyncTask
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.linkpoint.Debug
import com.linkpoint.R
import com.linkpoint.openjpeg.OpenJPEG
import com.linkpoint.render.tex.DrawableTextureParams
import com.linkpoint.render.tex.TextureClass
import com.linkpoint.res.ResourceConsumer
import com.linkpoint.res.textures.TextureCache
import com.linkpoint.ui.common.LoadingLayout
import com.linkpoint.ui.common.StateAwareFragment
import com.linkpoint.utils.UUIDPool
import java.util.UUID
import javax.annotation.Nullable
import uk.co.senab.photoview.PhotoViewAttacher

class TextureViewFragment : StateAwareFragment() {
    private const val ASSET_UUID_KEY: String = "assetUUID"
    /* access modifiers changed from: private */
    public LoadAssetImageTask loadAssetImageTask = null
    /* access modifiers changed from: private */
    public LoadingLayout loadingLayout
    /* access modifiers changed from: private */
    public PhotoViewAttacher photoViewAttacher
    /* access modifiers changed from: private */
    public ImageView textureImageView

    private class LoadAssetImageTask : AsyncTask()<UUID, Void, Bitmap> : ResourceConsumer {
        private volatile OpenJPEG texture
        private val Object textureReady

        private LoadAssetImageTask() {
            this.textureReady = Object()
        }

        /* synthetic */ LoadAssetImageTask(TextureViewFragment textureViewFragment, LoadAssetImageTask loadAssetImageTask) {
            this()
        }

        public Unit OnResourceReady(Object obj, Boolean z) {
            if (obj instanceof OpenJPEG) {
                this.texture = (OpenJPEG) obj
            }
            synchronized (this.textureReady) {
                this.textureReady.notify()
            }
        }

        /* access modifiers changed from: protected */
        public Bitmap doInBackground(UUID... uuidArr) {
            Debug.Printf("loading asset ID %s", uuidArr[0].toString())
            TextureCache.getInstance().RequestResource(DrawableTextureParams.create(uuidArr[0], TextureClass.Asset), this)
            synchronized (this.textureReady) {
                if (this.texture == null) {
                    Debug.Printf("asset ID %s is not available, waiting", uuidArr[0].toString())
                    try {
                        this.textureReady.wait()
                        Debug.Printf("done waiting for asset ID %s", uuidArr[0].toString())
                    } catch (InterruptedException e) {
                        Debug.Printf("interrupted while waiting for asset ID %s", uuidArr[0].toString())
                        return null
                    }
                } else {
                    Debug.Printf("asset ID %s is already available", uuidArr[0].toString())
                }
            }
            if (this.texture != null) {
                return this.texture.getAsBitmap()
            }
            return null
        }

        /* access modifiers changed from: protected */
        public Unit onPostExecute(Bitmap bitmap) {
            if (!(!TextureViewFragment.this.isFragmentStarted() || TextureViewFragment.this.textureImageView == null || TextureViewFragment.this.loadingLayout == null)) {
                if (bitmap != null) {
                    TextureViewFragment.this.loadingLayout.showContent((String) null)
                    TextureViewFragment.this.textureImageView.setImageBitmap(bitmap)
                    TextureViewFragment.this.photoViewAttacher.update()
                } else {
                    TextureViewFragment.this.loadingLayout.showMessage(TextureViewFragment.this.getString(R.string.failed_to_download_texture))
                    TextureViewFragment.this.textureImageView.setImageBitmap((Bitmap) null)
                    TextureViewFragment.this.photoViewAttacher.update()
                }
            }
            LoadAssetImageTask unused = TextureViewFragment.this.loadAssetImageTask = null
        }

        /* access modifiers changed from: protected */
        public Unit onPreExecute() {
            if (TextureViewFragment.this.isFragmentStarted() && TextureViewFragment.this.loadingLayout != null) {
                TextureViewFragment.this.loadingLayout.showLoading()
            }
        }
    }

    @JvmStatic
    Bundle makeArguments(UUID uuid, UUID uuid2) {
        Bundle bundle = Bundle()
        bundle.putString("activeAgentUUID", uuid.toString())
        bundle.putString(ASSET_UUID_KEY, uuid2.toString())
        return bundle
    }

    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View inflate = layoutInflater.inflate(R.layout.texture_view_fragment, viewGroup, false)
        this.loadingLayout = (LoadingLayout) inflate.findViewById(R.id.loading_layout)
        this.textureImageView = (ImageView) inflate.findViewById(R.id.texture_image_view)
        this.photoViewAttacher = PhotoViewAttacher(this.textureImageView)
        return inflate
    }

    public Unit onStart() {
        super.onStart()
        UUID uuid = UUIDPool.getUUID(getArguments().getString(ASSET_UUID_KEY))
        if (uuid != null) {
            if (this.loadAssetImageTask != null) {
                this.loadAssetImageTask.cancel(true)
                this.loadAssetImageTask = null
            }
            this.loadAssetImageTask = LoadAssetImageTask(this, (LoadAssetImageTask) null)
            this.loadAssetImageTask.execute(UUID[]{uuid})
        }
    }

    public Unit onStop() {
        if (this.loadAssetImageTask != null) {
            this.loadAssetImageTask.cancel(true)
            this.loadAssetImageTask = null
        }
        super.onStop()
    }
}
