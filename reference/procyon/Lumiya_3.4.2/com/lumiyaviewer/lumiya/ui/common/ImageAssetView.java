// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.ui.common;

import com.lumiyaviewer.lumiya.res.ResourceMemoryCache;
import com.lumiyaviewer.lumiya.render.tex.DrawableTextureParams;
import com.lumiyaviewer.lumiya.render.tex.TextureClass;
import com.lumiyaviewer.lumiya.res.textures.TextureCache;
import com.lumiyaviewer.lumiya.openjpeg.OpenJPEG;
import com.lumiyaviewer.lumiya.res.ResourceConsumer;
import android.os.AsyncTask;
import com.google.common.base.Objects;
import com.lumiyaviewer.lumiya.Debug;
import android.view.View$MeasureSpec;
import android.os.AsyncTask$Status;
import com.lumiyaviewer.lumiya.utils.UUIDPool;
import android.graphics.Canvas;
import android.util.DisplayMetrics;
import android.graphics.Paint$Align;
import android.graphics.Paint$Style;
import android.util.TypedValue;
import android.util.AttributeSet;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.graphics.Rect;
import java.util.UUID;
import android.view.View;

public class ImageAssetView extends View
{
    private boolean alignTop;
    private UUID assetID;
    private final Rect bitmapDestRect;
    private final Paint bitmapPaint;
    private final Rect bitmapSrcRect;
    private Bitmap imageBitmap;
    private LoadAssetImageTask loadTask;
    private final Paint textPaint;
    private boolean verticalFit;
    
    public ImageAssetView(final Context context) {
        super(context);
        this.bitmapPaint = new Paint();
        this.bitmapSrcRect = new Rect();
        this.bitmapDestRect = new Rect();
        this.textPaint = new Paint();
        this.alignTop = false;
        this.verticalFit = false;
    }
    
    public ImageAssetView(final Context context, final AttributeSet set) {
        super(context, set);
        this.bitmapPaint = new Paint();
        this.bitmapSrcRect = new Rect();
        this.bitmapDestRect = new Rect();
        this.textPaint = new Paint();
        this.alignTop = false;
        this.verticalFit = false;
    }
    
    public ImageAssetView(final Context context, final AttributeSet set, final int n) {
        super(context, set, n);
        this.bitmapPaint = new Paint();
        this.bitmapSrcRect = new Rect();
        this.bitmapDestRect = new Rect();
        this.textPaint = new Paint();
        this.alignTop = false;
        this.verticalFit = false;
    }
    
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        final DisplayMetrics displayMetrics = this.getResources().getDisplayMetrics();
        final TypedValue typedValue = new TypedValue();
        this.getContext().getTheme().resolveAttribute(2130772060, typedValue, true);
        final int data = typedValue.data;
        this.textPaint.setStyle(Paint$Style.STROKE);
        this.textPaint.setColor(data);
        this.textPaint.setTextAlign(Paint$Align.CENTER);
        this.textPaint.setAntiAlias(true);
        this.textPaint.setTextSize(TypedValue.applyDimension(2, 14.0f, displayMetrics));
    }
    
    protected void onDraw(final Canvas canvas) {
        final int width = this.getWidth();
        final int height = this.getHeight();
        this.bitmapPaint.setStyle(Paint$Style.STROKE);
        this.bitmapPaint.setARGB(255, 192, 192, 192);
        this.bitmapPaint.setTextAlign(Paint$Align.CENTER);
        if (this.imageBitmap != null && width != 0 && height != 0) {
            final int width2 = this.imageBitmap.getWidth();
            final int height2 = this.imageBitmap.getHeight();
            final float max = Math.max(width2 / (float)width, height2 / (float)height);
            final int round = Math.round(width2 / max);
            final int round2 = Math.round(height2 / max);
            final int n = width / 2;
            final int n2 = height / 2;
            final int n3 = n - round / 2;
            int n4;
            if (this.alignTop) {
                n4 = 0;
            }
            else {
                n4 = n2 - round2 / 2;
            }
            this.bitmapDestRect.left = n3 + 1;
            this.bitmapDestRect.top = n4 + 1;
            this.bitmapDestRect.right = round + n3 - 1;
            this.bitmapDestRect.bottom = n4 + round2 - 1;
            if (this.bitmapDestRect.left < 1) {
                this.bitmapDestRect.left = 1;
            }
            if (this.bitmapDestRect.top < 1) {
                this.bitmapDestRect.top = 1;
            }
            if (this.bitmapDestRect.right > width - 1) {
                this.bitmapDestRect.right = width - 1;
            }
            if (this.bitmapDestRect.bottom > height - 1) {
                this.bitmapDestRect.bottom = height - 1;
            }
            this.bitmapSrcRect.left = 0;
            this.bitmapSrcRect.top = 0;
            this.bitmapSrcRect.right = width2;
            this.bitmapSrcRect.bottom = height2;
            canvas.drawBitmap(this.imageBitmap, this.bitmapSrcRect, this.bitmapDestRect, this.bitmapPaint);
            final Rect bitmapDestRect = this.bitmapDestRect;
            --bitmapDestRect.left;
            final Rect bitmapDestRect2 = this.bitmapDestRect;
            --bitmapDestRect2.top;
            canvas.drawRect(this.bitmapDestRect, this.bitmapPaint);
        }
        else {
            canvas.drawARGB(50, 0, 0, 0);
            String s;
            if (this.assetID == null || UUIDPool.ZeroUUID.equals(this.assetID)) {
                s = "No image";
            }
            else if (this.loadTask == null) {
                s = "Failed to load";
            }
            else if (this.loadTask.getStatus() == AsyncTask$Status.FINISHED) {
                s = "Failed to load";
            }
            else {
                s = "Loading...";
            }
            this.textPaint.getTextBounds(s, 0, s.length(), this.bitmapSrcRect);
            canvas.drawText(s, width / 2.0f, height / 2.0f + this.bitmapSrcRect.height() / 2.0f, this.textPaint);
        }
    }
    
    protected void onMeasure(int size, int n) {
        if (View$MeasureSpec.getMode(size) == 0 && View$MeasureSpec.getMode(n) == 0) {
            super.onMeasure(size, n);
        }
        else {
            int size2;
            if (View$MeasureSpec.getMode(size) != 0) {
                size2 = View$MeasureSpec.getSize(size);
            }
            else {
                size2 = Integer.MAX_VALUE;
            }
            int size3;
            if (View$MeasureSpec.getMode(n) != 0) {
                size3 = View$MeasureSpec.getSize(n);
            }
            else {
                size3 = Integer.MAX_VALUE;
            }
            final int min = Math.min(size3, size2);
            int size4;
            if (View$MeasureSpec.getMode(size) == 1073741824) {
                size4 = View$MeasureSpec.getSize(size);
            }
            else {
                size4 = min;
            }
            size = min;
            if (View$MeasureSpec.getMode(n) == 1073741824) {
                size = View$MeasureSpec.getSize(n);
            }
            n = size;
            if (this.verticalFit) {
                n = size;
                if (this.imageBitmap != null) {
                    n = size;
                    if (size4 != Integer.MAX_VALUE) {
                        n = size;
                        if (size4 > 0) {
                            n = this.imageBitmap.getHeight() * size4 / this.imageBitmap.getWidth();
                        }
                    }
                }
            }
            this.setMeasuredDimension(size4, n);
        }
    }
    
    public void setAlignTop(final boolean alignTop) {
        this.alignTop = alignTop;
        this.invalidate();
    }
    
    public void setAssetID(final UUID uuid) {
        String string;
        if (uuid != null) {
            string = uuid.toString();
        }
        else {
            string = null;
        }
        Debug.Printf("new asset ID: %s", string);
        UUID assetID = uuid;
        if (uuid != null) {
            assetID = uuid;
            if (uuid.equals(UUIDPool.ZeroUUID)) {
                assetID = null;
            }
        }
        if (!Objects.equal(this.assetID, assetID)) {
            if (this.loadTask != null) {
                this.loadTask.cancel(true);
                this.loadTask = null;
            }
            this.assetID = assetID;
            if (this.imageBitmap != null) {
                this.imageBitmap.recycle();
            }
            this.imageBitmap = null;
            if (this.assetID != null) {
                Debug.Printf("requested to view asset ID %s", assetID);
                (this.loadTask = new LoadAssetImageTask((LoadAssetImageTask)null)).execute((Object[])new UUID[] { assetID });
            }
            this.invalidate();
        }
    }
    
    public void setVerticalFit(final boolean verticalFit) {
        this.verticalFit = verticalFit;
        this.requestLayout();
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
        
        protected void onPostExecute(final Bitmap bitmap) {
            ImageAssetView.this.imageBitmap = bitmap;
            if (ImageAssetView.this.verticalFit) {
                ImageAssetView.this.requestLayout();
            }
            ImageAssetView.this.invalidate();
            ImageAssetView.this.loadTask = null;
        }
    }
}
