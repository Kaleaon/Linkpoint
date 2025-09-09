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

public class ChatterPicView extends View {
    @Nullable
    private ChatMessageSource attachedMessageSource = null;
    private final Rect bitmapDestRect = new Rect();
    private final Paint bitmapPaint = new Paint();
    private final Rect bitmapSrcRect = new Rect();
    @Nullable
    private ChatterID chatterID = null;
    @Nullable
    private String chatterName = null;
    @Nullable
    private Drawable defaultIconDrawable = null;
    @Nullable
    private Drawable forceIcon = null;
    @Nullable
    private ChatterThumbnailData thumbnailData = null;
    private int thumbnailDefaultIcon = -1;

    public ChatterPicView(Context context) {
        super(context);
    }

    public ChatterPicView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public ChatterPicView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    @TargetApi(21)
    public ChatterPicView(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
    }

    @Nullable
    public ChatMessageSource getAttachedMessageSource() {
        return this.attachedMessageSource;
    }

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (this.thumbnailData == null && this.chatterID != null) {
            this.thumbnailData = new ChatterThumbnailData(this.chatterID, this);
        }
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (this.thumbnailData != null) {
            this.thumbnailData.dispose();
            this.thumbnailData = null;
        }
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        int i;
        String str;
        int i2 = 192;
        int i3 = 64;
        int width = getWidth();
        int height = getHeight();
        this.bitmapPaint.setStyle(Paint.Style.STROKE);
        this.bitmapPaint.setARGB(255, 255, 255, 255);
        this.bitmapPaint.setTextAlign(Paint.Align.CENTER);
        this.bitmapPaint.setTextSize(((float) height) / 2.0f);
        this.bitmapPaint.setAntiAlias(true);
        if (this.forceIcon != null) {
            this.forceIcon.setBounds(0, 0, width, height);
            this.forceIcon.draw(canvas);
        } else if (this.thumbnailData != null) {
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
            } else if (this.defaultIconDrawable != null) {
                this.defaultIconDrawable.setBounds(0, 0, width, height);
                this.defaultIconDrawable.draw(canvas);
            } else {
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
                        canvas.drawText(str.toUpperCase(), ((float) width) / 2.0f, (((float) height) / 2.0f) - ((this.bitmapPaint.descent() + this.bitmapPaint.ascent()) / 2.0f), this.bitmapPaint);
                    }
                }
            }
        } else {
            canvas.drawRGB(64, 64, 64);
        }
    }

    public void setAttachedMessageSource(@Nullable ChatMessageSource chatMessageSource) {
        this.attachedMessageSource = chatMessageSource;
    }

    public void setChatterID(@Nullable ChatterID chatterID2, @Nullable String str) {
        boolean z = true;
        boolean z2 = false;
        if (!(this.forceIcon == null || chatterID2 == null)) {
            this.forceIcon = null;
            z2 = true;
        }
        if (!Objects.equal(this.chatterID, chatterID2)) {
            this.chatterID = chatterID2;
            if (chatterID2 != null) {
                this.thumbnailData = new ChatterThumbnailData(chatterID2, this);
            } else {
                if (this.thumbnailData != null) {
                    this.thumbnailData.dispose();
                }
                this.thumbnailData = null;
            }
            z2 = true;
        }
        if (!Objects.equal(this.chatterName, str)) {
            this.chatterName = str;
        } else {
            z = z2;
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
        if (i != -1) {
            this.forceIcon = ContextCompat.getDrawable(getContext(), i);
            setChatterID((ChatterID) null, (String) null);
            postInvalidate();
            return;
        }
        this.forceIcon = null;
        postInvalidate();
    }
}
