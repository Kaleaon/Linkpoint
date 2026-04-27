// 
// Decompiled by Procyon v0.6.0
// 

package android.support.v4.text;

import java.util.Locale;
import java.nio.CharBuffer;

public final class TextDirectionHeuristicsCompat
{
    public static final TextDirectionHeuristicCompat ANYRTL_LTR;
    public static final TextDirectionHeuristicCompat FIRSTSTRONG_LTR;
    public static final TextDirectionHeuristicCompat FIRSTSTRONG_RTL;
    public static final TextDirectionHeuristicCompat LOCALE;
    public static final TextDirectionHeuristicCompat LTR;
    public static final TextDirectionHeuristicCompat RTL;
    private static final int STATE_FALSE = 1;
    private static final int STATE_TRUE = 0;
    private static final int STATE_UNKNOWN = 2;
    
    static {
        LTR = new TextDirectionHeuristicInternal(null, false);
        RTL = new TextDirectionHeuristicInternal(null, true);
        FIRSTSTRONG_LTR = new TextDirectionHeuristicInternal(FirstStrong.INSTANCE, false);
        FIRSTSTRONG_RTL = new TextDirectionHeuristicInternal(FirstStrong.INSTANCE, true);
        ANYRTL_LTR = new TextDirectionHeuristicInternal(AnyStrong.INSTANCE_RTL, false);
        LOCALE = TextDirectionHeuristicLocale.INSTANCE;
    }
    
    private TextDirectionHeuristicsCompat() {
    }
    
    static int isRtlText(final int n) {
        switch (n) {
            default: {
                return 2;
            }
            case 0: {
                return 1;
            }
            case 1:
            case 2: {
                return 0;
            }
        }
    }
    
    static int isRtlTextOrFormat(final int n) {
        switch (n) {
            default: {
                return 2;
            }
            case 0:
            case 14:
            case 15: {
                return 1;
            }
            case 1:
            case 2:
            case 16:
            case 17: {
                return 0;
            }
        }
    }
    
    private static class AnyStrong implements TextDirectionAlgorithm
    {
        public static final AnyStrong INSTANCE_LTR;
        public static final AnyStrong INSTANCE_RTL;
        private final boolean mLookForRtl;
        
        static {
            INSTANCE_RTL = new AnyStrong(true);
            INSTANCE_LTR = new AnyStrong(false);
        }
        
        private AnyStrong(final boolean mLookForRtl) {
            this.mLookForRtl = mLookForRtl;
        }
        
        @Override
        public int checkRtl(final CharSequence charSequence, int n, final int n2) {
            final int n3 = 1;
            int n4 = 0;
            int n5 = n;
            while (true) {
                int n6 = n5;
                if (n6 >= n + n2) {
                    if (n4 == 0) {
                        return 2;
                    }
                    n = n3;
                    if (!this.mLookForRtl) {
                        n = 0;
                    }
                    return n;
                }
                else {
                    int n7 = 0;
                    switch (TextDirectionHeuristicsCompat.isRtlText(Character.getDirectionality(charSequence.charAt(n6)))) {
                        default: {
                            n7 = n4;
                            break;
                        }
                        case 0: {
                            if (!this.mLookForRtl) {
                                n7 = 1;
                                break;
                            }
                            return 0;
                        }
                        case 1: {
                            if (this.mLookForRtl) {
                                n7 = 1;
                                break;
                            }
                            return 1;
                        }
                    }
                    ++n6;
                    n4 = n7;
                    n5 = n6;
                }
            }
        }
    }
    
    private static class FirstStrong implements TextDirectionAlgorithm
    {
        public static final FirstStrong INSTANCE;
        
        static {
            INSTANCE = new FirstStrong();
        }
        
        @Override
        public int checkRtl(final CharSequence charSequence, final int n, final int n2) {
            int rtlTextOrFormat = 2;
            for (int n3 = n; n3 < n + n2 && rtlTextOrFormat == 2; rtlTextOrFormat = TextDirectionHeuristicsCompat.isRtlTextOrFormat(Character.getDirectionality(charSequence.charAt(n3))), ++n3) {}
            return rtlTextOrFormat;
        }
    }
    
    private interface TextDirectionAlgorithm
    {
        int checkRtl(final CharSequence p0, final int p1, final int p2);
    }
    
    private abstract static class TextDirectionHeuristicImpl implements TextDirectionHeuristicCompat
    {
        private final TextDirectionAlgorithm mAlgorithm;
        
        public TextDirectionHeuristicImpl(final TextDirectionAlgorithm mAlgorithm) {
            this.mAlgorithm = mAlgorithm;
        }
        
        private boolean doCheck(final CharSequence charSequence, final int n, final int n2) {
            switch (this.mAlgorithm.checkRtl(charSequence, n, n2)) {
                default: {
                    return this.defaultIsRtl();
                }
                case 0: {
                    return true;
                }
                case 1: {
                    return false;
                }
            }
        }
        
        protected abstract boolean defaultIsRtl();
        
        @Override
        public boolean isRtl(final CharSequence charSequence, final int n, final int n2) {
            if (charSequence == null || n < 0 || n2 < 0 || charSequence.length() - n2 < n) {
                throw new IllegalArgumentException();
            }
            if (this.mAlgorithm != null) {
                return this.doCheck(charSequence, n, n2);
            }
            return this.defaultIsRtl();
        }
        
        @Override
        public boolean isRtl(final char[] array, final int n, final int n2) {
            return this.isRtl(CharBuffer.wrap(array), n, n2);
        }
    }
    
    private static class TextDirectionHeuristicInternal extends TextDirectionHeuristicImpl
    {
        private final boolean mDefaultIsRtl;
        
        TextDirectionHeuristicInternal(final TextDirectionAlgorithm textDirectionAlgorithm, final boolean mDefaultIsRtl) {
            super(textDirectionAlgorithm);
            this.mDefaultIsRtl = mDefaultIsRtl;
        }
        
        @Override
        protected boolean defaultIsRtl() {
            return this.mDefaultIsRtl;
        }
    }
    
    private static class TextDirectionHeuristicLocale extends TextDirectionHeuristicImpl
    {
        public static final TextDirectionHeuristicLocale INSTANCE;
        
        static {
            INSTANCE = new TextDirectionHeuristicLocale();
        }
        
        public TextDirectionHeuristicLocale() {
            super(null);
        }
        
        @Override
        protected boolean defaultIsRtl() {
            boolean b = true;
            if (TextUtilsCompat.getLayoutDirectionFromLocale(Locale.getDefault()) != 1) {
                b = false;
            }
            return b;
        }
    }
}
