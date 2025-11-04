package com.lumiyaviewer.lumiya.ui.chat;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import com.google.common.base.Objects;
import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.slproto.users.ChatterID;
import com.lumiyaviewer.lumiya.slproto.users.chatsrc.ChatMessageSource;
import java.util.UUID;
import javax.annotation.Nullable;

/* loaded from: classes.dex */
public class ChatterPicView extends View {

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

    public ChatterPicView(Context context) {
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

    public ChatterPicView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
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

    public ChatterPicView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
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
    public ChatterPicView(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
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

    @Override // android.view.View
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (this.thumbnailData != null || this.chatterID == null) {
            return;
        }
        this.thumbnailData = new ChatterThumbnailData(this.chatterID, this);
    }

    @Override // android.view.View
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (this.thumbnailData != null) {
            this.thumbnailData.dispose();
            this.thumbnailData = null;
        }
    }

    @Override // android.view.View
    protected void onDraw(Canvas canvas) {
        int i;
        String str;
        int i2 = 192;
        int i3 = 64;
        int width = getWidth();
        int height = getHeight();
        this.bitmapPaint.setStyle(Paint.Style.STROKE);
        this.bitmapPaint.setARGB(255, 255, 255, 255);
        this.bitmapPaint.setTextAlign(Paint.Align.CENTER);
        this.bitmapPaint.setTextSize(height / 2.0f);
        this.bitmapPaint.setAntiAlias(true);
        if (this.forceIcon != null) {
            this.forceIcon.setBounds(0, 0, width, height);
            this.forceIcon.draw(canvas);
            return;
        }
        if (this.thumbnailData == null) {
            canvas.drawRGB(64, 64, 64);
            return;
        }
        Bitmap bitmapData = this.thumbnailData.getBitmapData();
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
            return;
        }
        if (this.defaultIconDrawable != null) {
            this.defaultIconDrawable.setBounds(0, 0, width, height);
            this.defaultIconDrawable.draw(canvas);
            return;
        }
        UUID optionalChatterUUID = this.chatterID != null ? this.chatterID.getOptionalChatterUUID() : null;
        if (optionalChatterUUID != null) {
            int abs = Math.abs(optionalChatterUUID.hashCode()) % 6;
            if (abs < 3) {
                int i4 = abs == 0 ? 192 : 32;
                int i5 = abs == 1 ? 192 : 32;
                if (abs == 2) {
                    i = i5;
                    i3 = i4;
                } else {
                    i2 = 32;
                    i = i5;
                    i3 = i4;
                }
            } else {
                int i6 = abs != 3 ? 192 : 32;
                int i7 = abs != 4 ? 192 : 32;
                if (abs != 5) {
                    i = i7;
                    i3 = i6;
                } else {
                    i2 = 32;
                    i = i7;
                    i3 = i6;
                }
            }
            Debug.Printf("colorize: uuid %s, hash %x, comp %d, rgb %d, %d, %d", optionalChatterUUID.toString(), Integer.valueOf(optionalChatterUUID.hashCode()), Integer.valueOf(abs), Integer.valueOf(i3), Integer.valueOf(i), Integer.valueOf(i2));
        } else {
            i2 = 64;
            i = 64;
        }
        canvas.drawRGB(i3, i, i2);
        if (this.chatterName != null) {
            int i8 = 0;
            while (true) {
                if (i8 >= this.chatterName.length()) {
                    str = null;
                    break;
                }
                char charAt = this.chatterName.charAt(i8);
                if (Character.isLetter(charAt)) {
                    str = String.valueOf(charAt);
                    break;
                }
                i8++;
            }
            if (str != null) {
                canvas.drawText(str.toUpperCase(), width / 2.0f, (height / 2.0f) - ((this.bitmapPaint.descent() + this.bitmapPaint.ascent()) / 2.0f), this.bitmapPaint);
            }
        }
    }

    public void setAttachedMessageSource(@Nullable ChatMessageSource chatMessageSource) {
        this.attachedMessageSource = chatMessageSource;
    }

    public void setChatterID(@Nullable ChatterID chatterID, @Nullable String str) {
        boolean z = true;
        boolean z2 = false;
        if (this.forceIcon != null && chatterID != null) {
            this.forceIcon = null;
            z2 = true;
        }
        if (!Objects.equal(this.chatterID, chatterID)) {
            this.chatterID = chatterID;
            if (chatterID != null) {
                this.thumbnailData = new ChatterThumbnailData(chatterID, this);
            } else {
                if (this.thumbnailData != null) {
                    this.thumbnailData.dispose();
                }
                this.thumbnailData = null;
            }
            z2 = true;
        }
        if (Objects.equal(this.chatterName, str)) {
            z = z2;
        } else {
            this.chatterName = str;
        }
        if (z) {
            postInvalidate();
        }
    }

    public void setDefaultIcon(int i, boolean z) {
        if (this.thumbnailDefaultIcon != i) {
            this.thumbnailDefaultIcon = i;
            if (this.thumbnailDefaultIcon == -1) {
                this.defaultIconDrawable = null;
            } else if (z) {
                this.defaultIconDrawable = ContextCompat.getDrawable(getContext(), i);
            } else {
                TypedValue typedValue = new TypedValue();
                getContext().getTheme().resolveAttribute(i, typedValue, true);
                this.defaultIconDrawable = ContextCompat.getDrawable(getContext(), typedValue.resourceId);
            }
            postInvalidate();
        }
    }

    public void setForceIcon(int i) {
        if (i == -1) {
            this.forceIcon = null;
            postInvalidate();
        } else {
            this.forceIcon = ContextCompat.getDrawable(getContext(), i);
            setChatterID(null, null);
            postInvalidate();
        }
    }
}
