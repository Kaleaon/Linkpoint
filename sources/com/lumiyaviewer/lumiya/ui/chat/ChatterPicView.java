// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.ui.chat;

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

// Referenced classes of package com.lumiyaviewer.lumiya.ui.chat:
//            ChatterThumbnailData

public class ChatterPicView extends View
{

    private ChatMessageSource attachedMessageSource;
    private final Rect bitmapDestRect;
    private final Paint bitmapPaint;
    private final Rect bitmapSrcRect;
    private ChatterID chatterID;
    private String chatterName;
    private Drawable defaultIconDrawable;
    private Drawable forceIcon;
    private ChatterThumbnailData thumbnailData;
    private int thumbnailDefaultIcon;

    public ChatterPicView(Context context)
    {
        super(context);
        thumbnailData = null;
        chatterID = null;
        attachedMessageSource = null;
        chatterName = null;
        thumbnailDefaultIcon = -1;
        defaultIconDrawable = null;
        forceIcon = null;
        bitmapPaint = new Paint();
        bitmapSrcRect = new Rect();
        bitmapDestRect = new Rect();
    }

    public ChatterPicView(Context context, AttributeSet attributeset)
    {
        super(context, attributeset);
        thumbnailData = null;
        chatterID = null;
        attachedMessageSource = null;
        chatterName = null;
        thumbnailDefaultIcon = -1;
        defaultIconDrawable = null;
        forceIcon = null;
        bitmapPaint = new Paint();
        bitmapSrcRect = new Rect();
        bitmapDestRect = new Rect();
    }

    public ChatterPicView(Context context, AttributeSet attributeset, int i)
    {
        super(context, attributeset, i);
        thumbnailData = null;
        chatterID = null;
        attachedMessageSource = null;
        chatterName = null;
        thumbnailDefaultIcon = -1;
        defaultIconDrawable = null;
        forceIcon = null;
        bitmapPaint = new Paint();
        bitmapSrcRect = new Rect();
        bitmapDestRect = new Rect();
    }

    public ChatterPicView(Context context, AttributeSet attributeset, int i, int j)
    {
        super(context, attributeset, i, j);
        thumbnailData = null;
        chatterID = null;
        attachedMessageSource = null;
        chatterName = null;
        thumbnailDefaultIcon = -1;
        defaultIconDrawable = null;
        forceIcon = null;
        bitmapPaint = new Paint();
        bitmapSrcRect = new Rect();
        bitmapDestRect = new Rect();
    }

    public ChatMessageSource getAttachedMessageSource()
    {
        return attachedMessageSource;
    }

    protected void onAttachedToWindow()
    {
        super.onAttachedToWindow();
        if (thumbnailData == null && chatterID != null)
        {
            thumbnailData = new ChatterThumbnailData(chatterID, this);
        }
    }

    protected void onDetachedFromWindow()
    {
        super.onDetachedFromWindow();
        if (thumbnailData != null)
        {
            thumbnailData.dispose();
            thumbnailData = null;
        }
    }

    protected void onDraw(Canvas canvas)
    {
        char c1;
        char c6;
        int j;
        int k;
        c6 = '\300';
        c1 = '@';
        j = getWidth();
        k = getHeight();
        bitmapPaint.setStyle(android.graphics.Paint.Style.STROKE);
        bitmapPaint.setARGB(255, 255, 255, 255);
        bitmapPaint.setTextAlign(android.graphics.Paint.Align.CENTER);
        bitmapPaint.setTextSize((float)k / 2.0F);
        bitmapPaint.setAntiAlias(true);
        if (forceIcon == null) goto _L2; else goto _L1
_L1:
        forceIcon.setBounds(0, 0, j, k);
        forceIcon.draw(canvas);
_L6:
        return;
_L2:
        if (thumbnailData == null) goto _L4; else goto _L3
_L3:
        Object obj;
        int i;
        obj = thumbnailData.getBitmapData();
        if (obj != null)
        {
            bitmapSrcRect.left = 0;
            bitmapSrcRect.top = 0;
            bitmapSrcRect.right = ((Bitmap) (obj)).getWidth();
            bitmapSrcRect.bottom = ((Bitmap) (obj)).getHeight();
            bitmapDestRect.left = 0;
            bitmapDestRect.top = 0;
            bitmapDestRect.right = j;
            bitmapDestRect.bottom = k;
            canvas.drawBitmap(((Bitmap) (obj)), bitmapSrcRect, bitmapDestRect, bitmapPaint);
            return;
        }
        if (defaultIconDrawable != null)
        {
            defaultIconDrawable.setBounds(0, 0, j, k);
            defaultIconDrawable.draw(canvas);
            return;
        }
        char c2;
        if (chatterID != null)
        {
            obj = chatterID.getOptionalChatterUUID();
        } else
        {
            obj = null;
        }
        if (obj != null)
        {
            int l = Math.abs(((UUID) (obj)).hashCode()) % 6;
            if (l < 3)
            {
                char c;
                if (l == 0)
                {
                    i = 192;
                } else
                {
                    i = 32;
                }
                if (l == 1)
                {
                    c1 = '\300';
                } else
                {
                    c1 = ' ';
                }
                if (l == 2)
                {
                    c2 = c1;
                    c1 = i;
                    i = c2;
                    c2 = c6;
                } else
                {
                    c2 = ' ';
                    char c3 = i;
                    i = c1;
                    c1 = c3;
                }
            } else
            {
                if (l != 3)
                {
                    i = 192;
                } else
                {
                    i = 32;
                }
                if (l != 4)
                {
                    c1 = '\300';
                } else
                {
                    c1 = ' ';
                }
                if (l != 5)
                {
                    char c4 = i;
                    c2 = c6;
                    i = c1;
                    c1 = c4;
                } else
                {
                    c2 = ' ';
                    char c5 = i;
                    i = c1;
                    c1 = c5;
                }
            }
            Debug.Printf("colorize: uuid %s, hash %x, comp %d, rgb %d, %d, %d", new Object[] {
                ((UUID) (obj)).toString(), Integer.valueOf(((UUID) (obj)).hashCode()), Integer.valueOf(l), Integer.valueOf(c1), Integer.valueOf(i), Integer.valueOf(c2)
            });
        } else
        {
            c2 = '@';
            i = 64;
        }
        canvas.drawRGB(c1, i, c2);
        if (chatterName == null) goto _L6; else goto _L5
_L5:
        i = 0;
_L9:
        if (i >= chatterName.length())
        {
            break MISSING_BLOCK_LABEL_634;
        }
        c = chatterName.charAt(i);
        if (!Character.isLetter(c)) goto _L8; else goto _L7
_L7:
        obj = String.valueOf(c);
_L10:
        if (obj != null)
        {
            canvas.drawText(((String) (obj)).toUpperCase(), (float)j / 2.0F, (float)k / 2.0F - (bitmapPaint.descent() + bitmapPaint.ascent()) / 2.0F, bitmapPaint);
            return;
        }
          goto _L6
_L8:
        i++;
          goto _L9
_L4:
        canvas.drawRGB(64, 64, 64);
        return;
        obj = null;
          goto _L10
    }

    public void setAttachedMessageSource(ChatMessageSource chatmessagesource)
    {
        attachedMessageSource = chatmessagesource;
    }

    public void setChatterID(ChatterID chatterid, String s)
    {
        boolean flag1 = true;
        boolean flag2 = false;
        boolean flag = flag2;
        if (forceIcon != null)
        {
            flag = flag2;
            if (chatterid != null)
            {
                forceIcon = null;
                flag = true;
            }
        }
        if (!Objects.equal(chatterID, chatterid))
        {
            chatterID = chatterid;
            if (chatterid != null)
            {
                thumbnailData = new ChatterThumbnailData(chatterid, this);
            } else
            {
                if (thumbnailData != null)
                {
                    thumbnailData.dispose();
                }
                thumbnailData = null;
            }
            flag = true;
        }
        if (!Objects.equal(chatterName, s))
        {
            chatterName = s;
            flag = flag1;
        }
        if (flag)
        {
            postInvalidate();
        }
    }

    public void setDefaultIcon(int i, boolean flag)
    {
        if (thumbnailDefaultIcon != i)
        {
            thumbnailDefaultIcon = i;
            if (thumbnailDefaultIcon == -1)
            {
                defaultIconDrawable = null;
            } else
            if (flag)
            {
                defaultIconDrawable = ContextCompat.getDrawable(getContext(), i);
            } else
            {
                TypedValue typedvalue = new TypedValue();
                getContext().getTheme().resolveAttribute(i, typedvalue, true);
                defaultIconDrawable = ContextCompat.getDrawable(getContext(), typedvalue.resourceId);
            }
            postInvalidate();
        }
    }

    public void setForceIcon(int i)
    {
        if (i != -1)
        {
            forceIcon = ContextCompat.getDrawable(getContext(), i);
            setChatterID(null, null);
            postInvalidate();
            return;
        } else
        {
            forceIcon = null;
            postInvalidate();
            return;
        }
    }
}
