// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.ui.chat;

import android.util.TypedValue;
import android.support.v4.content.ContextCompat;
import com.google.common.base.Objects;
import java.util.UUID;
import android.graphics.Bitmap;
import com.lumiyaviewer.lumiya.Debug;
import android.graphics.Paint$Align;
import android.graphics.Paint$Style;
import android.graphics.Canvas;
import android.annotation.TargetApi;
import android.util.AttributeSet;
import android.content.Context;
import android.graphics.drawable.Drawable;
import com.lumiyaviewer.lumiya.slproto.users.ChatterID;
import android.graphics.Paint;
import android.graphics.Rect;
import javax.annotation.Nullable;
import com.lumiyaviewer.lumiya.slproto.users.chatsrc.ChatMessageSource;
import android.view.View;

public class ChatterPicView extends View
{
    @Nullable
    private ChatMessageSource attachedMessageSource;
    private final Rect bitmapDestRect;
    private final Paint bitmapPaint;
    private final Rect bitmapSrcRect;
    @Nullable
    private ChatterID chatterID;
    @Nullable
    private String chatterName;
    @Nullable
    private Drawable defaultIconDrawable;
    @Nullable
    private Drawable forceIcon;
    @Nullable
    private ChatterThumbnailData thumbnailData;
    private int thumbnailDefaultIcon;
    
    public ChatterPicView(final Context context) {
        super(context);
        this.thumbnailData = null;
        this.chatterID = null;
        this.attachedMessageSource = null;
        this.chatterName = null;
        this.thumbnailDefaultIcon = -1;
        this.defaultIconDrawable = null;
        this.forceIcon = null;
        this.bitmapPaint = new Paint();
        this.bitmapSrcRect = new Rect();
        this.bitmapDestRect = new Rect();
    }
    
    public ChatterPicView(final Context context, final AttributeSet set) {
        super(context, set);
        this.thumbnailData = null;
        this.chatterID = null;
        this.attachedMessageSource = null;
        this.chatterName = null;
        this.thumbnailDefaultIcon = -1;
        this.defaultIconDrawable = null;
        this.forceIcon = null;
        this.bitmapPaint = new Paint();
        this.bitmapSrcRect = new Rect();
        this.bitmapDestRect = new Rect();
    }
    
    public ChatterPicView(final Context context, final AttributeSet set, final int n) {
        super(context, set, n);
        this.thumbnailData = null;
        this.chatterID = null;
        this.attachedMessageSource = null;
        this.chatterName = null;
        this.thumbnailDefaultIcon = -1;
        this.defaultIconDrawable = null;
        this.forceIcon = null;
        this.bitmapPaint = new Paint();
        this.bitmapSrcRect = new Rect();
        this.bitmapDestRect = new Rect();
    }
    
    @TargetApi(21)
    public ChatterPicView(final Context context, final AttributeSet set, final int n, final int n2) {
        super(context, set, n, n2);
        this.thumbnailData = null;
        this.chatterID = null;
        this.attachedMessageSource = null;
        this.chatterName = null;
        this.thumbnailDefaultIcon = -1;
        this.defaultIconDrawable = null;
        this.forceIcon = null;
        this.bitmapPaint = new Paint();
        this.bitmapSrcRect = new Rect();
        this.bitmapDestRect = new Rect();
    }
    
    @Nullable
    public ChatMessageSource getAttachedMessageSource() {
        return this.attachedMessageSource;
    }
    
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (this.thumbnailData == null && this.chatterID != null) {
            this.thumbnailData = new ChatterThumbnailData(this.chatterID, this);
        }
    }
    
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (this.thumbnailData != null) {
            this.thumbnailData.dispose();
            this.thumbnailData = null;
        }
    }
    
    protected void onDraw(final Canvas canvas) {
        final int n = 192;
        int i = 64;
        final int width = this.getWidth();
        final int height = this.getHeight();
        this.bitmapPaint.setStyle(Paint$Style.STROKE);
        this.bitmapPaint.setARGB(255, 255, 255, 255);
        this.bitmapPaint.setTextAlign(Paint$Align.CENTER);
        this.bitmapPaint.setTextSize(height / 2.0f);
        this.bitmapPaint.setAntiAlias(true);
        if (this.forceIcon != null) {
            this.forceIcon.setBounds(0, 0, width, height);
            this.forceIcon.draw(canvas);
        }
        else if (this.thumbnailData != null) {
            final Bitmap bitmapData = this.thumbnailData.getBitmapData();
            if (bitmapData != null) {
                this.bitmapSrcRect.left = 0;
                this.bitmapSrcRect.top = 0;
                this.bitmapSrcRect.right = bitmapData.getWidth();
                this.bitmapSrcRect.bottom = bitmapData.getHeight();
                this.bitmapDestRect.left = 0;
                this.bitmapDestRect.top = 0;
                this.bitmapDestRect.right = width;
                this.bitmapDestRect.bottom = height;
                canvas.drawBitmap(bitmapData, this.bitmapSrcRect, this.bitmapDestRect, this.bitmapPaint);
            }
            else if (this.defaultIconDrawable != null) {
                this.defaultIconDrawable.setBounds(0, 0, width, height);
                this.defaultIconDrawable.draw(canvas);
            }
            else {
                UUID optionalChatterUUID;
                if (this.chatterID != null) {
                    optionalChatterUUID = this.chatterID.getOptionalChatterUUID();
                }
                else {
                    optionalChatterUUID = null;
                }
                int k;
                int l;
                if (optionalChatterUUID != null) {
                    final int j = Math.abs(optionalChatterUUID.hashCode()) % 6;
                    if (j < 3) {
                        int n2;
                        if (j == 0) {
                            n2 = 192;
                        }
                        else {
                            n2 = 32;
                        }
                        int n3;
                        if (j == 1) {
                            n3 = 192;
                        }
                        else {
                            n3 = 32;
                        }
                        if (j == 2) {
                            final int n4 = n3;
                            i = n2;
                            k = n4;
                            l = n;
                        }
                        else {
                            l = 32;
                            final int n5 = n2;
                            k = n3;
                            i = n5;
                        }
                    }
                    else {
                        int n6;
                        if (j != 3) {
                            n6 = 192;
                        }
                        else {
                            n6 = 32;
                        }
                        int n7;
                        if (j != 4) {
                            n7 = 192;
                        }
                        else {
                            n7 = 32;
                        }
                        if (j != 5) {
                            final int n8 = n7;
                            i = n6;
                            l = n;
                            k = n8;
                        }
                        else {
                            l = 32;
                            final int n9 = n7;
                            i = n6;
                            k = n9;
                        }
                    }
                    Debug.Printf("colorize: uuid %s, hash %x, comp %d, rgb %d, %d, %d", optionalChatterUUID.toString(), optionalChatterUUID.hashCode(), j, i, k, l);
                }
                else {
                    l = 64;
                    k = 64;
                }
                canvas.drawRGB(i, k, l);
                if (this.chatterName != null) {
                    int index = 0;
                    while (true) {
                        while (index < this.chatterName.length()) {
                            final char char1 = this.chatterName.charAt(index);
                            if (Character.isLetter(char1)) {
                                final String value = String.valueOf(char1);
                                if (value != null) {
                                    canvas.drawText(value.toUpperCase(), width / 2.0f, height / 2.0f - (this.bitmapPaint.descent() + this.bitmapPaint.ascent()) / 2.0f, this.bitmapPaint);
                                }
                                return;
                            }
                            else {
                                ++index;
                            }
                        }
                        final String value = null;
                        continue;
                    }
                }
            }
        }
        else {
            canvas.drawRGB(64, 64, 64);
        }
    }
    
    public void setAttachedMessageSource(@Nullable final ChatMessageSource attachedMessageSource) {
        this.attachedMessageSource = attachedMessageSource;
    }
    
    public void setChatterID(@Nullable final ChatterID chatterID, @Nullable final String chatterName) {
        final int n = 1;
        int n2 = 0;
        if (this.forceIcon != null) {
            n2 = n2;
            if (chatterID != null) {
                this.forceIcon = null;
                n2 = 1;
            }
        }
        if (!Objects.equal(this.chatterID, chatterID)) {
            if ((this.chatterID = chatterID) != null) {
                this.thumbnailData = new ChatterThumbnailData(chatterID, this);
            }
            else {
                if (this.thumbnailData != null) {
                    this.thumbnailData.dispose();
                }
                this.thumbnailData = null;
            }
            n2 = 1;
        }
        if (!Objects.equal(this.chatterName, chatterName)) {
            this.chatterName = chatterName;
            n2 = n;
        }
        if (n2 != 0) {
            this.postInvalidate();
        }
    }
    
    public void setDefaultIcon(final int thumbnailDefaultIcon, final boolean b) {
        if (this.thumbnailDefaultIcon != thumbnailDefaultIcon) {
            this.thumbnailDefaultIcon = thumbnailDefaultIcon;
            if (this.thumbnailDefaultIcon == -1) {
                this.defaultIconDrawable = null;
            }
            else if (b) {
                this.defaultIconDrawable = ContextCompat.getDrawable(this.getContext(), thumbnailDefaultIcon);
            }
            else {
                final TypedValue typedValue = new TypedValue();
                this.getContext().getTheme().resolveAttribute(thumbnailDefaultIcon, typedValue, true);
                this.defaultIconDrawable = ContextCompat.getDrawable(this.getContext(), typedValue.resourceId);
            }
            this.postInvalidate();
        }
    }
    
    public void setForceIcon(final int n) {
        if (n != -1) {
            this.forceIcon = ContextCompat.getDrawable(this.getContext(), n);
            this.setChatterID(null, null);
            this.postInvalidate();
        }
        else {
            this.forceIcon = null;
            this.postInvalidate();
        }
    }
}
