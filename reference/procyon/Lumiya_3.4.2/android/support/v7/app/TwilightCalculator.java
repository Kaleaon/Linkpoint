// 
// Decompiled by Procyon v0.6.0
// 

package android.support.v7.app;

class TwilightCalculator
{
    private static final float ALTIDUTE_CORRECTION_CIVIL_TWILIGHT = -0.10471976f;
    private static final float C1 = 0.0334196f;
    private static final float C2 = 3.49066E-4f;
    private static final float C3 = 5.236E-6f;
    public static final int DAY = 0;
    private static final float DEGREES_TO_RADIANS = 0.017453292f;
    private static final float J0 = 9.0E-4f;
    public static final int NIGHT = 1;
    private static final float OBLIQUITY = 0.4092797f;
    private static final long UTC_2000 = 946728000000L;
    private static TwilightCalculator sInstance;
    public int state;
    public long sunrise;
    public long sunset;
    
    static TwilightCalculator getInstance() {
        if (TwilightCalculator.sInstance == null) {
            TwilightCalculator.sInstance = new TwilightCalculator();
        }
        return TwilightCalculator.sInstance;
    }
    
    public void calculateTwilight(final long n, double sin, double n2) {
        final float n3 = (n - 946728000000L) / 8.64E7f;
        final float n4 = 0.01720197f * n3 + 6.24006f;
        final double a = n4 + Math.sin(n4) * 0.03341960161924362 + Math.sin(2.0f * n4) * 3.4906598739326E-4 + Math.sin(3.0f * n4) * 5.236000106378924E-6 + 1.796593063 + 3.141592653589793;
        final double n5 = -n2 / 360.0;
        n2 = Math.round(n3 - 9.0E-4f - n5) + 9.0E-4f;
        n2 = Math.sin(n4) * 0.0053 + (n5 + n2) + Math.sin(2.0 * a) * -0.0069;
        final double asin = Math.asin(Math.sin(a) * Math.sin(0.4092797040939331));
        final double n6 = 0.01745329238474369 * sin;
        final double sin2 = Math.sin(-0.10471975803375244);
        sin = Math.sin(n6);
        sin = (sin2 - sin * Math.sin(asin)) / (Math.cos(asin) * Math.cos(n6));
        if (sin >= 1.0) {
            this.state = 1;
            this.sunset = -1L;
            this.sunrise = -1L;
            return;
        }
        if (sin <= -1.0) {
            this.state = 0;
            this.sunset = -1L;
            this.sunrise = -1L;
            return;
        }
        final float n7 = (float)(Math.acos(sin) / 6.283185307179586);
        this.sunset = Math.round((n7 + n2) * 8.64E7) + 946728000000L;
        this.sunrise = Math.round((n2 - n7) * 8.64E7) + 946728000000L;
        int n8;
        if (this.sunrise >= n) {
            n8 = 1;
        }
        else {
            n8 = 0;
        }
        Label_0360: {
            if (n8 != 0) {
                break Label_0360;
            }
            int n9;
            if (this.sunset <= n) {
                n9 = 1;
            }
            else {
                n9 = 0;
            }
            if (n9 != 0) {
                break Label_0360;
            }
            this.state = 0;
            return;
        }
        this.state = 1;
    }
}
