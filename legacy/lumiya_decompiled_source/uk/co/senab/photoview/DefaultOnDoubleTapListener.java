package uk.co.senab.photoview;

import android.graphics.RectF;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.ImageView;
/* loaded from: classes.dex */
public class DefaultOnDoubleTapListener implements GestureDetector.OnDoubleTapListener {
    private PhotoViewAttacher photoViewAttacher;

    public DefaultOnDoubleTapListener(PhotoViewAttacher photoViewAttacher) {
        setPhotoViewAttacher(photoViewAttacher);
    }

    @Override // android.view.GestureDetector.OnDoubleTapListener
    public boolean onDoubleTap(MotionEvent motionEvent) {
        if (this.photoViewAttacher != null) {
            try {
                float scale = this.photoViewAttacher.getScale();
                float x = motionEvent.getX();
                float y = motionEvent.getY();
                if (scale < this.photoViewAttacher.getMediumScale()) {
                    this.photoViewAttacher.setScale(this.photoViewAttacher.getMediumScale(), x, y, true);
                } else if (scale < this.photoViewAttacher.getMediumScale() || scale >= this.photoViewAttacher.getMaximumScale()) {
                    this.photoViewAttacher.setScale(this.photoViewAttacher.getMinimumScale(), x, y, true);
                } else {
                    this.photoViewAttacher.setScale(this.photoViewAttacher.getMaximumScale(), x, y, true);
                }
            } catch (ArrayIndexOutOfBoundsException e) {
            }
            return true;
        }
        return false;
    }

    @Override // android.view.GestureDetector.OnDoubleTapListener
    public boolean onDoubleTapEvent(MotionEvent motionEvent) {
        return false;
    }

    @Override // android.view.GestureDetector.OnDoubleTapListener
    public boolean onSingleTapConfirmed(MotionEvent motionEvent) {
        RectF displayRect;
        if (this.photoViewAttacher != null) {
            ImageView imageView = this.photoViewAttacher.getImageView();
            if (this.photoViewAttacher.getOnPhotoTapListener() != null && (displayRect = this.photoViewAttacher.getDisplayRect()) != null) {
                float x = motionEvent.getX();
                float y = motionEvent.getY();
                if (displayRect.contains(x, y)) {
                    this.photoViewAttacher.getOnPhotoTapListener().onPhotoTap(imageView, (x - displayRect.left) / displayRect.width(), (y - displayRect.top) / displayRect.height());
                    return true;
                }
                this.photoViewAttacher.getOnPhotoTapListener().onOutsidePhotoTap();
            }
            if (this.photoViewAttacher.getOnViewTapListener() != null) {
                this.photoViewAttacher.getOnViewTapListener().onViewTap(imageView, motionEvent.getX(), motionEvent.getY());
            }
            return false;
        }
        return false;
    }

    public void setPhotoViewAttacher(PhotoViewAttacher photoViewAttacher) {
        this.photoViewAttacher = photoViewAttacher;
    }
}
