// 
// Decompiled by Procyon v0.6.0
// 

package android.support.v4.text.util;

import android.support.annotation.RestrictTo;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Retention;
import java.util.List;
import java.util.Collections;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import android.webkit.WebView;
import java.util.regex.Matcher;
import java.util.Locale;
import java.util.Iterator;
import android.support.v4.util.PatternsCompat;
import java.util.ArrayList;
import android.text.style.URLSpan;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.util.Linkify;
import android.text.util.Linkify$TransformFilter;
import android.text.util.Linkify$MatchFilter;
import android.os.Build$VERSION;
import android.support.annotation.Nullable;
import java.util.regex.Pattern;
import android.text.method.MovementMethod;
import android.text.method.LinkMovementMethod;
import android.support.annotation.NonNull;
import android.widget.TextView;
import java.util.Comparator;

public final class LinkifyCompat
{
    private static final Comparator<LinkSpec> COMPARATOR;
    private static final String[] EMPTY_STRING;
    
    static {
        EMPTY_STRING = new String[0];
        COMPARATOR = new Comparator<LinkSpec>() {
            @Override
            public final int compare(final LinkSpec linkSpec, final LinkSpec linkSpec2) {
                if (linkSpec.start < linkSpec2.start) {
                    return -1;
                }
                if (linkSpec.start > linkSpec2.start) {
                    return 1;
                }
                if (linkSpec.end < linkSpec2.end) {
                    return 1;
                }
                if (linkSpec.end <= linkSpec2.end) {
                    return 0;
                }
                return -1;
            }
        };
    }
    
    private LinkifyCompat() {
    }
    
    private static void addLinkMovementMethod(@NonNull final TextView textView) {
        final MovementMethod movementMethod = textView.getMovementMethod();
        if (movementMethod == null || !(movementMethod instanceof LinkMovementMethod)) {
            if (textView.getLinksClickable()) {
                textView.setMovementMethod(LinkMovementMethod.getInstance());
            }
        }
    }
    
    public static final void addLinks(@NonNull final TextView textView, @NonNull final Pattern pattern, @Nullable final String s) {
        if (Build$VERSION.SDK_INT < 26) {
            addLinks(textView, pattern, s, null, null, null);
            return;
        }
        Linkify.addLinks(textView, pattern, s);
    }
    
    public static final void addLinks(@NonNull final TextView textView, @NonNull final Pattern pattern, @Nullable final String s, @Nullable final Linkify$MatchFilter linkify$MatchFilter, @Nullable final Linkify$TransformFilter linkify$TransformFilter) {
        if (Build$VERSION.SDK_INT < 26) {
            addLinks(textView, pattern, s, null, linkify$MatchFilter, linkify$TransformFilter);
            return;
        }
        Linkify.addLinks(textView, pattern, s, linkify$MatchFilter, linkify$TransformFilter);
    }
    
    public static final void addLinks(@NonNull final TextView textView, @NonNull final Pattern pattern, @Nullable final String s, @Nullable final String[] array, @Nullable final Linkify$MatchFilter linkify$MatchFilter, @Nullable final Linkify$TransformFilter linkify$TransformFilter) {
        if (Build$VERSION.SDK_INT < 26) {
            final SpannableString value = SpannableString.valueOf(textView.getText());
            if (addLinks((Spannable)value, pattern, s, array, linkify$MatchFilter, linkify$TransformFilter)) {
                textView.setText((CharSequence)value);
                addLinkMovementMethod(textView);
            }
            return;
        }
        Linkify.addLinks(textView, pattern, s, array, linkify$MatchFilter, linkify$TransformFilter);
    }
    
    public static final boolean addLinks(@NonNull final Spannable spannable, final int n) {
        if (Build$VERSION.SDK_INT >= 26) {
            return Linkify.addLinks(spannable, n);
        }
        if (n == 0) {
            return false;
        }
        final URLSpan[] array = (URLSpan[])spannable.getSpans(0, spannable.length(), (Class)URLSpan.class);
        int length = array.length;
        while (--length >= 0) {
            spannable.removeSpan((Object)array[length]);
        }
        if ((n & 0x4) != 0x0) {
            Linkify.addLinks(spannable, 4);
        }
        final ArrayList list = new ArrayList();
        if ((n & 0x1) != 0x0) {
            gatherLinks(list, spannable, PatternsCompat.AUTOLINK_WEB_URL, new String[] { "http://", "https://", "rtsp://" }, Linkify.sUrlMatchFilter, null);
        }
        if ((n & 0x2) != 0x0) {
            gatherLinks(list, spannable, PatternsCompat.AUTOLINK_EMAIL_ADDRESS, new String[] { "mailto:" }, null, null);
        }
        if ((n & 0x8) != 0x0) {
            gatherMapLinks(list, spannable);
        }
        pruneOverlaps(list, spannable);
        if (list.size() != 0) {
            for (final LinkSpec linkSpec : list) {
                if (linkSpec.frameworkAddedSpan == null) {
                    applyLink(linkSpec.url, linkSpec.start, linkSpec.end, spannable);
                }
            }
            return true;
        }
        return false;
    }
    
    public static final boolean addLinks(@NonNull final Spannable spannable, @NonNull final Pattern pattern, @Nullable final String s) {
        if (Build$VERSION.SDK_INT < 26) {
            return addLinks(spannable, pattern, s, null, null, null);
        }
        return Linkify.addLinks(spannable, pattern, s);
    }
    
    public static final boolean addLinks(@NonNull final Spannable spannable, @NonNull final Pattern pattern, @Nullable final String s, @Nullable final Linkify$MatchFilter linkify$MatchFilter, @Nullable final Linkify$TransformFilter linkify$TransformFilter) {
        if (Build$VERSION.SDK_INT < 26) {
            return addLinks(spannable, pattern, s, null, linkify$MatchFilter, linkify$TransformFilter);
        }
        return Linkify.addLinks(spannable, pattern, s, linkify$MatchFilter, linkify$TransformFilter);
    }
    
    public static final boolean addLinks(@NonNull final Spannable input, @NonNull final Pattern pattern, @Nullable String lowerCase, @Nullable String[] empty_STRING, @Nullable final Linkify$MatchFilter linkify$MatchFilter, @Nullable final Linkify$TransformFilter linkify$TransformFilter) {
        if (Build$VERSION.SDK_INT < 26) {
            if (lowerCase == null) {
                lowerCase = "";
            }
            if (empty_STRING == null || empty_STRING.length < 1) {
                empty_STRING = LinkifyCompat.EMPTY_STRING;
            }
            final String[] array = new String[empty_STRING.length + 1];
            array[0] = lowerCase.toLowerCase(Locale.ROOT);
            for (int i = 0; i < empty_STRING.length; ++i) {
                lowerCase = empty_STRING[i];
                if (lowerCase != null) {
                    lowerCase = lowerCase.toLowerCase(Locale.ROOT);
                }
                else {
                    lowerCase = "";
                }
                array[i + 1] = lowerCase;
            }
            final Matcher matcher = pattern.matcher((CharSequence)input);
            boolean b = false;
            while (matcher.find()) {
                final int start = matcher.start();
                final int end = matcher.end();
                if (linkify$MatchFilter == null || linkify$MatchFilter.acceptMatch((CharSequence)input, start, end)) {
                    applyLink(makeUrl(matcher.group(0), array, matcher, linkify$TransformFilter), start, end, input);
                    b = true;
                }
            }
            return b;
        }
        return Linkify.addLinks(input, pattern, lowerCase, empty_STRING, linkify$MatchFilter, linkify$TransformFilter);
    }
    
    public static final boolean addLinks(@NonNull final TextView textView, final int n) {
        if (Build$VERSION.SDK_INT >= 26) {
            return Linkify.addLinks(textView, n);
        }
        if (n == 0) {
            return false;
        }
        final CharSequence text = textView.getText();
        if (!(text instanceof Spannable)) {
            final SpannableString value = SpannableString.valueOf(text);
            if (!addLinks((Spannable)value, n)) {
                return false;
            }
            addLinkMovementMethod(textView);
            textView.setText((CharSequence)value);
            return true;
        }
        else {
            if (!addLinks((Spannable)text, n)) {
                return false;
            }
            addLinkMovementMethod(textView);
            return true;
        }
    }
    
    private static void applyLink(final String s, final int n, final int n2, final Spannable spannable) {
        spannable.setSpan((Object)new URLSpan(s), n, n2, 33);
    }
    
    private static void gatherLinks(final ArrayList<LinkSpec> list, final Spannable input, final Pattern pattern, final String[] array, final Linkify$MatchFilter linkify$MatchFilter, final Linkify$TransformFilter linkify$TransformFilter) {
        final Matcher matcher = pattern.matcher((CharSequence)input);
        while (matcher.find()) {
            final int start = matcher.start();
            final int end = matcher.end();
            if (linkify$MatchFilter != null && !linkify$MatchFilter.acceptMatch((CharSequence)input, start, end)) {
                continue;
            }
            final LinkSpec e = new LinkSpec();
            e.url = makeUrl(matcher.group(0), array, matcher, linkify$TransformFilter);
            e.start = start;
            e.end = end;
            list.add(e);
        }
    }
    
    private static final void gatherMapLinks(final ArrayList<LinkSpec> ex, Spannable spannable) {
        int n = 0;
        spannable = (Spannable)spannable.toString();
        while (true) {
            String address;
            LinkSpec linkSpec;
            try {
                while (true) {
                    address = WebView.findAddress((String)spannable);
                    if (address == null) {
                        break;
                    }
                    final int index = ((String)spannable).indexOf(address);
                    if (index < 0) {
                        break;
                    }
                    linkSpec = new LinkSpec();
                    final int beginIndex = address.length() + index;
                    linkSpec.start = index + n;
                    linkSpec.end = n + beginIndex;
                    spannable = (Spannable)((String)spannable).substring(beginIndex);
                    n += beginIndex;
                    final String s = address;
                    final String s2 = "UTF-8";
                    final String s3 = URLEncoder.encode(s, s2);
                    final StringBuilder sb = new(java.lang.StringBuilder.class)();
                    final StringBuilder sb3;
                    final StringBuilder sb2 = sb3 = sb;
                    new StringBuilder();
                    final LinkSpec linkSpec2 = linkSpec;
                    final StringBuilder sb4 = sb2;
                    final String s4 = "geo:0,0?q=";
                    final StringBuilder sb5 = sb4.append(s4);
                    final String s5 = s3;
                    final StringBuilder sb6 = sb5.append(s5);
                    final String s6 = sb6.toString();
                    linkSpec2.url = s6;
                    final ArrayList<LinkSpec> list = (ArrayList<LinkSpec>)ex;
                    final LinkSpec linkSpec3 = linkSpec;
                    list.add(linkSpec3);
                }
                return;
            }
            catch (final UnsupportedOperationException ex) {
                return;
            }
            try {
                final String s = address;
                final String s2 = "UTF-8";
                final String s3 = URLEncoder.encode(s, s2);
                final StringBuilder sb = new(java.lang.StringBuilder.class)();
                final StringBuilder sb3;
                final StringBuilder sb2 = sb3 = sb;
                new StringBuilder();
                final LinkSpec linkSpec2 = linkSpec;
                final StringBuilder sb4 = sb2;
                final String s4 = "geo:0,0?q=";
                final StringBuilder sb5 = sb4.append(s4);
                final String s5 = s3;
                final StringBuilder sb6 = sb5.append(s5);
                final String s6 = sb6.toString();
                linkSpec2.url = s6;
                final ArrayList<LinkSpec> list = (ArrayList<LinkSpec>)ex;
                final LinkSpec linkSpec3 = linkSpec;
                list.add(linkSpec3);
                continue;
            }
            catch (final UnsupportedEncodingException ex2) {
                continue;
            }
            break;
        }
    }
    
    private static String makeUrl(@NonNull String string, @NonNull final String[] array, final Matcher matcher, @Nullable final Linkify$TransformFilter linkify$TransformFilter) {
        final boolean b = true;
        String transformUrl;
        if (linkify$TransformFilter == null) {
            transformUrl = string;
        }
        else {
            transformUrl = linkify$TransformFilter.transformUrl(matcher, string);
        }
        while (true) {
            for (int i = 0; i < array.length; ++i) {
                if (transformUrl.regionMatches(true, 0, array[i], 0, array[i].length())) {
                    string = transformUrl;
                    int n = b ? 1 : 0;
                    if (!transformUrl.regionMatches(false, 0, array[i], 0, array[i].length())) {
                        string = array[i] + transformUrl.substring(array[i].length());
                        n = (b ? 1 : 0);
                    }
                    String string2;
                    if (n != 0) {
                        string2 = string;
                    }
                    else {
                        string2 = string;
                        if (array.length > 0) {
                            string2 = array[0] + string;
                        }
                    }
                    return string2;
                }
            }
            int n = 0;
            string = transformUrl;
            continue;
        }
    }
    
    private static final void pruneOverlaps(final ArrayList<LinkSpec> list, final Spannable spannable) {
        int i = 0;
        final URLSpan[] array = (URLSpan[])spannable.getSpans(0, spannable.length(), (Class)URLSpan.class);
        for (int j = 0; j < array.length; ++j) {
            final LinkSpec e = new LinkSpec();
            e.frameworkAddedSpan = array[j];
            e.start = spannable.getSpanStart((Object)array[j]);
            e.end = spannable.getSpanEnd((Object)array[j]);
            list.add(e);
        }
        Collections.sort((List<Object>)list, (Comparator<? super Object>)LinkifyCompat.COMPARATOR);
        int size = list.size();
        while (i < size - 1) {
            final LinkSpec linkSpec = list.get(i);
            final LinkSpec linkSpec2 = list.get(i + 1);
            if (linkSpec.start <= linkSpec2.start && linkSpec.end > linkSpec2.start) {
                int n;
                if (linkSpec2.end > linkSpec.end) {
                    if (linkSpec.end - linkSpec.start <= linkSpec2.end - linkSpec2.start) {
                        if (linkSpec.end - linkSpec.start >= linkSpec2.end - linkSpec2.start) {
                            n = -1;
                        }
                        else {
                            n = i;
                        }
                    }
                    else {
                        n = i + 1;
                    }
                }
                else {
                    n = i + 1;
                }
                if (n != -1) {
                    final URLSpan frameworkAddedSpan = list.get(n).frameworkAddedSpan;
                    if (frameworkAddedSpan != null) {
                        spannable.removeSpan((Object)frameworkAddedSpan);
                    }
                    list.remove(n);
                    --size;
                    continue;
                }
            }
            ++i;
        }
    }
    
    private static class LinkSpec
    {
        int end;
        URLSpan frameworkAddedSpan;
        int start;
        String url;
        
        LinkSpec() {
        }
    }
    
    @Retention(RetentionPolicy.SOURCE)
    @RestrictTo({ RestrictTo.Scope.LIBRARY_GROUP })
    public @interface LinkifyMask {
    }
}
