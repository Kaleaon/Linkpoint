package android.support.v7.preference;

import android.content.Context;
import android.content.Intent;
import android.content.res.XmlResourceParser;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.InflateException;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

class PreferenceInflater {
    private static final HashMap<String, Constructor> CONSTRUCTOR_MAP = new HashMap<>();
    private static final Class<?>[] CONSTRUCTOR_SIGNATURE = {Context.class, AttributeSet.class};
    private static final String EXTRA_TAG_NAME = "extra";
    private static final String INTENT_TAG_NAME = "intent";
    private static final String TAG = "PreferenceInflater";
    private final Object[] mConstructorArgs = new Object[2];
    private final Context mContext;
    private String[] mDefaultPackages;
    private PreferenceManager mPreferenceManager;

    public PreferenceInflater(Context context, PreferenceManager preferenceManager) {
        this.mContext = context;
        init(preferenceManager);
    }

    private Preference createItem(@NonNull String str, @Nullable String[] strArr, AttributeSet attributeSet) throws ClassNotFoundException, InflateException {
        Class<?> cls;
        int i = 0;
        Constructor<?> constructor = CONSTRUCTOR_MAP.get(str);
        if (constructor == null) {
            ClassLoader classLoader = this.mContext.getClassLoader();
            if (strArr != null) {
                if (strArr.length != 0) {
                    int length = strArr.length;
                    ClassNotFoundException classNotFoundException = null;
                    while (true) {
                        if (i >= length) {
                            cls = null;
                            break;
                        }
                        try {
                            cls = classLoader.loadClass(strArr[i] + str);
                            break;
                        } catch (ClassNotFoundException e) {
                            i++;
                            classNotFoundException = e;
                        }
                    }
                    if (cls == null) {
                        if (classNotFoundException != null) {
                            throw classNotFoundException;
                        }
                        throw new InflateException(attributeSet.getPositionDescription() + ": Error inflating class " + str);
                    }
                    constructor = cls.getConstructor(CONSTRUCTOR_SIGNATURE);
                    constructor.setAccessible(true);
                    CONSTRUCTOR_MAP.put(str, constructor);
                }
            }
            cls = classLoader.loadClass(str);
            constructor = cls.getConstructor(CONSTRUCTOR_SIGNATURE);
            constructor.setAccessible(true);
            CONSTRUCTOR_MAP.put(str, constructor);
        }
        try {
            Object[] objArr = this.mConstructorArgs;
            objArr[1] = attributeSet;
            return (Preference) constructor.newInstance(objArr);
        } catch (ClassNotFoundException e2) {
            throw e2;
        } catch (Exception e3) {
            InflateException inflateException = new InflateException(attributeSet.getPositionDescription() + ": Error inflating class " + str);
            inflateException.initCause(e3);
            throw inflateException;
        }
    }

    private Preference createItemFromTag(String str, AttributeSet attributeSet) {
        try {
            return -1 != str.indexOf(46) ? createItem(str, (String[]) null, attributeSet) : onCreateItem(str, attributeSet);
        } catch (InflateException e) {
            throw e;
        } catch (ClassNotFoundException e2) {
            InflateException inflateException = new InflateException(attributeSet.getPositionDescription() + ": Error inflating class (not found)" + str);
            inflateException.initCause(e2);
            throw inflateException;
        } catch (Exception e3) {
            InflateException inflateException2 = new InflateException(attributeSet.getPositionDescription() + ": Error inflating class " + str);
            inflateException2.initCause(e3);
            throw inflateException2;
        }
    }

    private void init(PreferenceManager preferenceManager) {
        this.mPreferenceManager = preferenceManager;
        setDefaultPackages(new String[]{"android.support.v14.preference.", "android.support.v7.preference."});
    }

    @NonNull
    private PreferenceGroup onMergeRoots(PreferenceGroup preferenceGroup, @NonNull PreferenceGroup preferenceGroup2) {
        if (preferenceGroup != null) {
            return preferenceGroup;
        }
        preferenceGroup2.onAttachedToHierarchy(this.mPreferenceManager);
        return preferenceGroup2;
    }

    private void rInflate(XmlPullParser xmlPullParser, Preference preference, AttributeSet attributeSet) throws XmlPullParserException, IOException {
        int depth = xmlPullParser.getDepth();
        while (true) {
            int next = xmlPullParser.next();
            if ((next != 3 || xmlPullParser.getDepth() > depth) && next != 1) {
                if (next == 2) {
                    String name = xmlPullParser.getName();
                    if (INTENT_TAG_NAME.equals(name)) {
                        try {
                            preference.setIntent(Intent.parseIntent(getContext().getResources(), xmlPullParser, attributeSet));
                        } catch (IOException e) {
                            XmlPullParserException xmlPullParserException = new XmlPullParserException("Error parsing preference");
                            xmlPullParserException.initCause(e);
                            throw xmlPullParserException;
                        }
                    } else if (!EXTRA_TAG_NAME.equals(name)) {
                        Preference createItemFromTag = createItemFromTag(name, attributeSet);
                        ((PreferenceGroup) preference).addItemFromInflater(createItemFromTag);
                        rInflate(xmlPullParser, createItemFromTag, attributeSet);
                    } else {
                        getContext().getResources().parseBundleExtra(EXTRA_TAG_NAME, attributeSet, preference.getExtras());
                        try {
                            skipCurrentTag(xmlPullParser);
                        } catch (IOException e2) {
                            XmlPullParserException xmlPullParserException2 = new XmlPullParserException("Error parsing preference");
                            xmlPullParserException2.initCause(e2);
                            throw xmlPullParserException2;
                        }
                    }
                }
            } else {
                return;
            }
        }
    }

    private static void skipCurrentTag(XmlPullParser xmlPullParser) throws XmlPullParserException, IOException {
        int depth = xmlPullParser.getDepth();
        while (true) {
            int next = xmlPullParser.next();
            if (next != 1) {
                if (next == 3 && xmlPullParser.getDepth() <= depth) {
                    return;
                }
            } else {
                return;
            }
        }
    }

    public Context getContext() {
        return this.mContext;
    }

    public String[] getDefaultPackages() {
        return this.mDefaultPackages;
    }

    public Preference inflate(int i, @Nullable PreferenceGroup preferenceGroup) {
        XmlResourceParser xml = getContext().getResources().getXml(i);
        try {
            return inflate((XmlPullParser) xml, preferenceGroup);
        } finally {
            xml.close();
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:14:0x002e A[SYNTHETIC, Splitter:B:14:0x002e] */
    /* JADX WARNING: Removed duplicated region for block: B:8:0x0017 A[Catch:{ InflateException -> 0x004c, XmlPullParserException -> 0x0051, IOException -> 0x005f }] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public android.support.v7.preference.Preference inflate(org.xmlpull.v1.XmlPullParser r7, @android.support.annotation.Nullable android.support.v7.preference.PreferenceGroup r8) {
        /*
            r6 = this;
            r5 = 2
            java.lang.Object[] r1 = r6.mConstructorArgs
            monitor-enter(r1)
            android.util.AttributeSet r2 = android.util.Xml.asAttributeSet(r7)     // Catch:{ all -> 0x004e }
            java.lang.Object[] r0 = r6.mConstructorArgs     // Catch:{ all -> 0x004e }
            android.content.Context r3 = r6.mContext     // Catch:{ all -> 0x004e }
            r4 = 0
            r0[r4] = r3     // Catch:{ all -> 0x004e }
        L_0x000f:
            int r0 = r7.next()     // Catch:{ InflateException -> 0x004c, XmlPullParserException -> 0x0051, IOException -> 0x005f }
            if (r0 != r5) goto L_0x002a
        L_0x0015:
            if (r0 != r5) goto L_0x002e
            java.lang.String r0 = r7.getName()     // Catch:{ InflateException -> 0x004c, XmlPullParserException -> 0x0051, IOException -> 0x005f }
            android.support.v7.preference.Preference r0 = r6.createItemFromTag(r0, r2)     // Catch:{ InflateException -> 0x004c, XmlPullParserException -> 0x0051, IOException -> 0x005f }
            android.support.v7.preference.PreferenceGroup r0 = (android.support.v7.preference.PreferenceGroup) r0     // Catch:{ InflateException -> 0x004c, XmlPullParserException -> 0x0051, IOException -> 0x005f }
            android.support.v7.preference.PreferenceGroup r0 = r6.onMergeRoots(r8, r0)     // Catch:{ InflateException -> 0x004c, XmlPullParserException -> 0x0051, IOException -> 0x005f }
            r6.rInflate(r7, r0, r2)     // Catch:{ InflateException -> 0x004c, XmlPullParserException -> 0x0051, IOException -> 0x005f }
            monitor-exit(r1)     // Catch:{ all -> 0x004e }
            return r0
        L_0x002a:
            r3 = 1
            if (r0 == r3) goto L_0x0015
            goto L_0x000f
        L_0x002e:
            android.view.InflateException r0 = new android.view.InflateException     // Catch:{ InflateException -> 0x004c, XmlPullParserException -> 0x0051, IOException -> 0x005f }
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ InflateException -> 0x004c, XmlPullParserException -> 0x0051, IOException -> 0x005f }
            r2.<init>()     // Catch:{ InflateException -> 0x004c, XmlPullParserException -> 0x0051, IOException -> 0x005f }
            java.lang.String r3 = r7.getPositionDescription()     // Catch:{ InflateException -> 0x004c, XmlPullParserException -> 0x0051, IOException -> 0x005f }
            java.lang.StringBuilder r2 = r2.append(r3)     // Catch:{ InflateException -> 0x004c, XmlPullParserException -> 0x0051, IOException -> 0x005f }
            java.lang.String r3 = ": No start tag found!"
            java.lang.StringBuilder r2 = r2.append(r3)     // Catch:{ InflateException -> 0x004c, XmlPullParserException -> 0x0051, IOException -> 0x005f }
            java.lang.String r2 = r2.toString()     // Catch:{ InflateException -> 0x004c, XmlPullParserException -> 0x0051, IOException -> 0x005f }
            r0.<init>(r2)     // Catch:{ InflateException -> 0x004c, XmlPullParserException -> 0x0051, IOException -> 0x005f }
            throw r0     // Catch:{ InflateException -> 0x004c, XmlPullParserException -> 0x0051, IOException -> 0x005f }
        L_0x004c:
            r0 = move-exception
            throw r0     // Catch:{ all -> 0x004e }
        L_0x004e:
            r0 = move-exception
            monitor-exit(r1)     // Catch:{ all -> 0x004e }
            throw r0
        L_0x0051:
            r0 = move-exception
            android.view.InflateException r2 = new android.view.InflateException     // Catch:{ all -> 0x004e }
            java.lang.String r3 = r0.getMessage()     // Catch:{ all -> 0x004e }
            r2.<init>(r3)     // Catch:{ all -> 0x004e }
            r2.initCause(r0)     // Catch:{ all -> 0x004e }
            throw r2     // Catch:{ all -> 0x004e }
        L_0x005f:
            r0 = move-exception
            android.view.InflateException r2 = new android.view.InflateException     // Catch:{ all -> 0x004e }
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ all -> 0x004e }
            r3.<init>()     // Catch:{ all -> 0x004e }
            java.lang.String r4 = r7.getPositionDescription()     // Catch:{ all -> 0x004e }
            java.lang.StringBuilder r3 = r3.append(r4)     // Catch:{ all -> 0x004e }
            java.lang.String r4 = ": "
            java.lang.StringBuilder r3 = r3.append(r4)     // Catch:{ all -> 0x004e }
            java.lang.String r4 = r0.getMessage()     // Catch:{ all -> 0x004e }
            java.lang.StringBuilder r3 = r3.append(r4)     // Catch:{ all -> 0x004e }
            java.lang.String r3 = r3.toString()     // Catch:{ all -> 0x004e }
            r2.<init>(r3)     // Catch:{ all -> 0x004e }
            r2.initCause(r0)     // Catch:{ all -> 0x004e }
            throw r2     // Catch:{ all -> 0x004e }
        */
        throw new UnsupportedOperationException("Method not decompiled: android.support.v7.preference.PreferenceInflater.inflate(org.xmlpull.v1.XmlPullParser, android.support.v7.preference.PreferenceGroup):android.support.v7.preference.Preference");
    }

    /* access modifiers changed from: protected */
    public Preference onCreateItem(String str, AttributeSet attributeSet) throws ClassNotFoundException {
        return createItem(str, this.mDefaultPackages, attributeSet);
    }

    public void setDefaultPackages(String[] strArr) {
        this.mDefaultPackages = strArr;
    }
}
