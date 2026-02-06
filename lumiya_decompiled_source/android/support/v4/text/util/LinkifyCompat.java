package android.support.v4.text.util;

import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RestrictTo;
import android.support.v4.util.PatternsCompat;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.method.MovementMethod;
import android.text.style.URLSpan;
import android.text.util.Linkify;
import android.webkit.WebView;
import android.widget.TextView;
import java.io.UnsupportedEncodingException;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
/* loaded from: classes.dex */
public final class LinkifyCompat {
    private static final String[] EMPTY_STRING = new String[0];
    private static final Comparator<LinkSpec> COMPARATOR = new Comparator<LinkSpec>() { // from class: android.support.v4.text.util.LinkifyCompat.1
        @Override // java.util.Comparator
        public final int compare(LinkSpec linkSpec, LinkSpec linkSpec2) {
            if (linkSpec.start >= linkSpec2.start) {
                if (linkSpec.start > linkSpec2.start || linkSpec.end < linkSpec2.end) {
                    return 1;
                }
                return linkSpec.end <= linkSpec2.end ? 0 : -1;
            }
            return -1;
        }
    };

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class LinkSpec {
        int end;
        URLSpan frameworkAddedSpan;
        int start;
        String url;

        LinkSpec() {
        }
    }

    @Retention(RetentionPolicy.SOURCE)
    @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
    /* loaded from: classes.dex */
    public @interface LinkifyMask {
    }

    private LinkifyCompat() {
    }

    private static void addLinkMovementMethod(@NonNull TextView textView) {
        MovementMethod movementMethod = textView.getMovementMethod();
        if ((movementMethod != null && (movementMethod instanceof LinkMovementMethod)) || !textView.getLinksClickable()) {
            return;
        }
        textView.setMovementMethod(LinkMovementMethod.getInstance());
    }

    public static final void addLinks(@NonNull TextView textView, @NonNull Pattern pattern, @Nullable String str) {
        if (Build.VERSION.SDK_INT < 26) {
            addLinks(textView, pattern, str, (String[]) null, (Linkify.MatchFilter) null, (Linkify.TransformFilter) null);
        } else {
            Linkify.addLinks(textView, pattern, str);
        }
    }

    public static final void addLinks(@NonNull TextView textView, @NonNull Pattern pattern, @Nullable String str, @Nullable Linkify.MatchFilter matchFilter, @Nullable Linkify.TransformFilter transformFilter) {
        if (Build.VERSION.SDK_INT < 26) {
            addLinks(textView, pattern, str, (String[]) null, matchFilter, transformFilter);
        } else {
            Linkify.addLinks(textView, pattern, str, matchFilter, transformFilter);
        }
    }

    public static final void addLinks(@NonNull TextView textView, @NonNull Pattern pattern, @Nullable String str, @Nullable String[] strArr, @Nullable Linkify.MatchFilter matchFilter, @Nullable Linkify.TransformFilter transformFilter) {
        if (Build.VERSION.SDK_INT >= 26) {
            Linkify.addLinks(textView, pattern, str, strArr, matchFilter, transformFilter);
            return;
        }
        SpannableString valueOf = SpannableString.valueOf(textView.getText());
        if (addLinks(valueOf, pattern, str, strArr, matchFilter, transformFilter)) {
            textView.setText(valueOf);
            addLinkMovementMethod(textView);
        }
    }

    public static final boolean addLinks(@NonNull Spannable spannable, int i) {
        if (Build.VERSION.SDK_INT < 26) {
            if (i != 0) {
                URLSpan[] uRLSpanArr = (URLSpan[]) spannable.getSpans(0, spannable.length(), URLSpan.class);
                int length = uRLSpanArr.length;
                while (true) {
                    length--;
                    if (length < 0) {
                        break;
                    }
                    spannable.removeSpan(uRLSpanArr[length]);
                }
                if ((i & 4) != 0) {
                    Linkify.addLinks(spannable, 4);
                }
                ArrayList arrayList = new ArrayList();
                if ((i & 1) != 0) {
                    gatherLinks(arrayList, spannable, PatternsCompat.AUTOLINK_WEB_URL, new String[]{"http://", "https://", "rtsp://"}, Linkify.sUrlMatchFilter, null);
                }
                if ((i & 2) != 0) {
                    gatherLinks(arrayList, spannable, PatternsCompat.AUTOLINK_EMAIL_ADDRESS, new String[]{"mailto:"}, null, null);
                }
                if ((i & 8) != 0) {
                    gatherMapLinks(arrayList, spannable);
                }
                pruneOverlaps(arrayList, spannable);
                if (arrayList.size() != 0) {
                    Iterator it = arrayList.iterator();
                    while (it.hasNext()) {
                        LinkSpec linkSpec = (LinkSpec) it.next();
                        if (linkSpec.frameworkAddedSpan == null) {
                            applyLink(linkSpec.url, linkSpec.start, linkSpec.end, spannable);
                        }
                    }
                    return true;
                }
                return false;
            }
            return false;
        }
        return Linkify.addLinks(spannable, i);
    }

    public static final boolean addLinks(@NonNull Spannable spannable, @NonNull Pattern pattern, @Nullable String str) {
        return Build.VERSION.SDK_INT < 26 ? addLinks(spannable, pattern, str, (String[]) null, (Linkify.MatchFilter) null, (Linkify.TransformFilter) null) : Linkify.addLinks(spannable, pattern, str);
    }

    public static final boolean addLinks(@NonNull Spannable spannable, @NonNull Pattern pattern, @Nullable String str, @Nullable Linkify.MatchFilter matchFilter, @Nullable Linkify.TransformFilter transformFilter) {
        return Build.VERSION.SDK_INT < 26 ? addLinks(spannable, pattern, str, (String[]) null, matchFilter, transformFilter) : Linkify.addLinks(spannable, pattern, str, matchFilter, transformFilter);
    }

    public static final boolean addLinks(@NonNull Spannable spannable, @NonNull Pattern pattern, @Nullable String str, @Nullable String[] strArr, @Nullable Linkify.MatchFilter matchFilter, @Nullable Linkify.TransformFilter transformFilter) {
        if (Build.VERSION.SDK_INT < 26) {
            if (str == null) {
                str = "";
            }
            if (strArr == null || strArr.length < 1) {
                strArr = EMPTY_STRING;
            }
            String[] strArr2 = new String[strArr.length + 1];
            strArr2[0] = str.toLowerCase(Locale.ROOT);
            for (int i = 0; i < strArr.length; i++) {
                String str2 = strArr[i];
                strArr2[i + 1] = str2 != null ? str2.toLowerCase(Locale.ROOT) : "";
            }
            Matcher matcher = pattern.matcher(spannable);
            boolean z = false;
            while (matcher.find()) {
                int start = matcher.start();
                int end = matcher.end();
                if (matchFilter == null ? true : matchFilter.acceptMatch(spannable, start, end)) {
                    applyLink(makeUrl(matcher.group(0), strArr2, matcher, transformFilter), start, end, spannable);
                    z = true;
                }
            }
            return z;
        }
        return Linkify.addLinks(spannable, pattern, str, strArr, matchFilter, transformFilter);
    }

    public static final boolean addLinks(@NonNull TextView textView, int i) {
        if (Build.VERSION.SDK_INT < 26) {
            if (i != 0) {
                CharSequence text = textView.getText();
                if (text instanceof Spannable) {
                    if (addLinks((Spannable) text, i)) {
                        addLinkMovementMethod(textView);
                        return true;
                    }
                    return false;
                }
                SpannableString valueOf = SpannableString.valueOf(text);
                if (addLinks(valueOf, i)) {
                    addLinkMovementMethod(textView);
                    textView.setText(valueOf);
                    return true;
                }
                return false;
            }
            return false;
        }
        return Linkify.addLinks(textView, i);
    }

    private static void applyLink(String str, int i, int i2, Spannable spannable) {
        spannable.setSpan(new URLSpan(str), i, i2, 33);
    }

    private static void gatherLinks(ArrayList<LinkSpec> arrayList, Spannable spannable, Pattern pattern, String[] strArr, Linkify.MatchFilter matchFilter, Linkify.TransformFilter transformFilter) {
        Matcher matcher = pattern.matcher(spannable);
        while (matcher.find()) {
            int start = matcher.start();
            int end = matcher.end();
            if (matchFilter == null || matchFilter.acceptMatch(spannable, start, end)) {
                LinkSpec linkSpec = new LinkSpec();
                linkSpec.url = makeUrl(matcher.group(0), strArr, matcher, transformFilter);
                linkSpec.start = start;
                linkSpec.end = end;
                arrayList.add(linkSpec);
            }
        }
    }

    private static final void gatherMapLinks(ArrayList<LinkSpec> arrayList, Spannable spannable) {
        int indexOf;
        int i = 0;
        String obj = spannable.toString();
        while (true) {
            try {
                String findAddress = WebView.findAddress(obj);
                if (findAddress == null || (indexOf = obj.indexOf(findAddress)) < 0) {
                    return;
                }
                LinkSpec linkSpec = new LinkSpec();
                int length = findAddress.length() + indexOf;
                linkSpec.start = indexOf + i;
                linkSpec.end = i + length;
                obj = obj.substring(length);
                i += length;
                try {
                    linkSpec.url = "geo:0,0?q=" + URLEncoder.encode(findAddress, "UTF-8");
                    arrayList.add(linkSpec);
                } catch (UnsupportedEncodingException e) {
                }
            } catch (UnsupportedOperationException e2) {
                return;
            }
        }
    }

    private static String makeUrl(@NonNull String str, @NonNull String[] strArr, Matcher matcher, @Nullable Linkify.TransformFilter transformFilter) {
        boolean z = true;
        String transformUrl = transformFilter == null ? str : transformFilter.transformUrl(matcher, str);
        int i = 0;
        while (true) {
            if (i >= strArr.length) {
                z = false;
                break;
            } else if (transformUrl.regionMatches(true, 0, strArr[i], 0, strArr[i].length())) {
                if (!transformUrl.regionMatches(false, 0, strArr[i], 0, strArr[i].length())) {
                    transformUrl = strArr[i] + transformUrl.substring(strArr[i].length());
                }
            } else {
                i++;
            }
        }
        return (!z && strArr.length > 0) ? strArr[0] + transformUrl : transformUrl;
    }

    private static final void pruneOverlaps(ArrayList<LinkSpec> arrayList, Spannable spannable) {
        int i = 0;
        Object[] objArr = (URLSpan[]) spannable.getSpans(0, spannable.length(), URLSpan.class);
        for (int i2 = 0; i2 < objArr.length; i2++) {
            LinkSpec linkSpec = new LinkSpec();
            linkSpec.frameworkAddedSpan = objArr[i2];
            linkSpec.start = spannable.getSpanStart(objArr[i2]);
            linkSpec.end = spannable.getSpanEnd(objArr[i2]);
            arrayList.add(linkSpec);
        }
        Collections.sort(arrayList, COMPARATOR);
        int size = arrayList.size();
        while (i < size - 1) {
            LinkSpec linkSpec2 = arrayList.get(i);
            LinkSpec linkSpec3 = arrayList.get(i + 1);
            if (linkSpec2.start <= linkSpec3.start && linkSpec2.end > linkSpec3.start) {
                int i3 = linkSpec3.end > linkSpec2.end ? linkSpec2.end - linkSpec2.start <= linkSpec3.end - linkSpec3.start ? linkSpec2.end - linkSpec2.start >= linkSpec3.end - linkSpec3.start ? -1 : i : i + 1 : i + 1;
                if (i3 != -1) {
                    Object obj = arrayList.get(i3).frameworkAddedSpan;
                    if (obj != null) {
                        spannable.removeSpan(obj);
                    }
                    arrayList.remove(i3);
                    size--;
                }
            }
            i++;
        }
    }
}
