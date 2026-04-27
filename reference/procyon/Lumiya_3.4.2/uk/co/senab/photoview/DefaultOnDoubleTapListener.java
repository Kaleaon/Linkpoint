// 
// Decompiled by Procyon v0.6.0
// 

package uk.co.senab.photoview;

import android.graphics.RectF;
import android.widget.ImageView;
import android.view.View;
import android.view.MotionEvent;
import android.view.GestureDetector$OnDoubleTapListener;

public class DefaultOnDoubleTapListener implements GestureDetector$OnDoubleTapListener
{
    private PhotoViewAttacher photoViewAttacher;
    
    public DefaultOnDoubleTapListener(final PhotoViewAttacher photoViewAttacher) {
        this.setPhotoViewAttacher(photoViewAttacher);
    }
    
    public boolean onDoubleTap(final MotionEvent motionEvent) {
        if (this.photoViewAttacher == null) {
            return false;
        }
        while (true) {
            float x = 0.0f;
            float y = 0.0f;
            Label_0109: {
                try {
                    final float scale = this.photoViewAttacher.getScale();
                    x = motionEvent.getX();
                    y = motionEvent.getY();
                    if (scale < this.photoViewAttacher.getMediumScale()) {
                        this.photoViewAttacher.setScale(this.photoViewAttacher.getMediumScale(), x, y, true);
                    }
                    else {
                        if (scale < this.photoViewAttacher.getMediumScale() || scale >= this.photoViewAttacher.getMaximumScale()) {
                            break Label_0109;
                        }
                        this.photoViewAttacher.setScale(this.photoViewAttacher.getMaximumScale(), x, y, true);
                    }
                    return true;
                }
                catch (final ArrayIndexOutOfBoundsException ex) {
                    return true;
                }
                return true;
            }
            this.photoViewAttacher.setScale(this.photoViewAttacher.getMinimumScale(), x, y, true);
            return true;
        }
    }
    
    public boolean onDoubleTapEvent(final MotionEvent motionEvent) {
        return false;
    }
    
    public boolean onSingleTapConfirmed(final MotionEvent motionEvent) {
        if (this.photoViewAttacher != null) {
            final ImageView imageView = this.photoViewAttacher.getImageView();
            if (this.photoViewAttacher.getOnPhotoTapListener() != null) {
                final RectF displayRect = this.photoViewAttacher.getDisplayRect();
                if (displayRect != null) {
                    final float x = motionEvent.getX();
                    final float y = motionEvent.getY();
                    if (displayRect.contains(x, y)) {
                        this.photoViewAttacher.getOnPhotoTapListener().onPhotoTap((View)imageView, (x - displayRect.left) / displayRect.width(), (y - displayRect.top) / displayRect.height());
                        return true;
                    }
                    this.photoViewAttacher.getOnPhotoTapListener().onOutsidePhotoTap();
                }
            }
            if (this.photoViewAttacher.getOnViewTapListener() != null) {
                this.photoViewAttacher.getOnViewTapListener().onViewTap((View)imageView, motionEvent.getX(), motionEvent.getY());
            }
            return false;
        }
        return false;
    }
    
    public void setPhotoViewAttacher(final PhotoViewAttacher photoViewAttacher) {
        this.photoViewAttacher = photoViewAttacher;
    }
}
