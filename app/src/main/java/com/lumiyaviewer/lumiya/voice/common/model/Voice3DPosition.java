package com.lumiyaviewer.lumiya.voice.common.model;

import android.os.Bundle;
import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

@Immutable
public class Voice3DPosition {
    @Nonnull
    public final Voice3DVector atOrientation;
    @Nonnull
    public final Voice3DVector leftOrientation;
    @Nonnull
    public final Voice3DVector position;
    @Nonnull
    public final Voice3DVector upOrientation;
    @Nonnull
    public final Voice3DVector velocity;

    public Voice3DPosition(Bundle bundle) {
        this.position = new Voice3DVector(bundle.getBundle("position"));
        this.velocity = new Voice3DVector(bundle.getBundle("velocity"));
        this.atOrientation = new Voice3DVector(bundle.getBundle("atOrientation"));
        this.upOrientation = new Voice3DVector(bundle.getBundle("upOrientation"));
        this.leftOrientation = new Voice3DVector(bundle.getBundle("leftOrientation"));
    }

    public Voice3DPosition(@Nonnull Voice3DVector voice3DVector, @Nonnull Voice3DVector voice3DVector2, @Nonnull Voice3DVector voice3DVector3, @Nonnull Voice3DVector voice3DVector4, @Nonnull Voice3DVector voice3DVector5) {
        this.position = voice3DVector;
        this.velocity = voice3DVector2;
        this.atOrientation = voice3DVector3;
        this.upOrientation = voice3DVector4;
        this.leftOrientation = voice3DVector5;
    }

    public Bundle toBundle() {
        Bundle bundle = new Bundle();
        bundle.putBundle("position", this.position.toBundle());
        bundle.putBundle("velocity", this.velocity.toBundle());
        bundle.putBundle("atOrientation", this.atOrientation.toBundle());
        bundle.putBundle("upOrientation", this.upOrientation.toBundle());
        bundle.putBundle("leftOrientation", this.leftOrientation.toBundle());
        return bundle;
    }

    public String toString() {
        return String.format("(pos %s vel %s at %s up %s left %s)", new Object[]{this.position.toString(), this.velocity.toString(), this.atOrientation.toString(), this.upOrientation.toString(), this.leftOrientation.toString()});
    }
}
