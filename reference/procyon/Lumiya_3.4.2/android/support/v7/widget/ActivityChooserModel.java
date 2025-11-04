// 
// Decompiled by Procyon v0.6.0
// 

package android.support.v7.widget;

import java.math.BigDecimal;
import android.content.ComponentName;
import java.util.Collections;
import java.util.Collection;
import android.os.AsyncTask;
import android.content.pm.ResolveInfo;
import android.text.TextUtils;
import java.util.ArrayList;
import java.util.HashMap;
import android.content.Intent;
import android.content.Context;
import java.util.List;
import java.util.Map;
import android.database.DataSetObservable;

class ActivityChooserModel extends DataSetObservable
{
    static final String ATTRIBUTE_ACTIVITY = "activity";
    static final String ATTRIBUTE_TIME = "time";
    static final String ATTRIBUTE_WEIGHT = "weight";
    static final boolean DEBUG = false;
    private static final int DEFAULT_ACTIVITY_INFLATION = 5;
    private static final float DEFAULT_HISTORICAL_RECORD_WEIGHT = 1.0f;
    public static final String DEFAULT_HISTORY_FILE_NAME = "activity_choser_model_history.xml";
    public static final int DEFAULT_HISTORY_MAX_LENGTH = 50;
    private static final String HISTORY_FILE_EXTENSION = ".xml";
    private static final int INVALID_INDEX = -1;
    static final String LOG_TAG;
    static final String TAG_HISTORICAL_RECORD = "historical-record";
    static final String TAG_HISTORICAL_RECORDS = "historical-records";
    private static final Map<String, ActivityChooserModel> sDataModelRegistry;
    private static final Object sRegistryLock;
    private final List<ActivityResolveInfo> mActivities;
    private OnChooseActivityListener mActivityChoserModelPolicy;
    private ActivitySorter mActivitySorter;
    boolean mCanReadHistoricalData;
    final Context mContext;
    private final List<HistoricalRecord> mHistoricalRecords;
    private boolean mHistoricalRecordsChanged;
    final String mHistoryFileName;
    private int mHistoryMaxSize;
    private final Object mInstanceLock;
    private Intent mIntent;
    private boolean mReadShareHistoryCalled;
    private boolean mReloadActivities;
    
    static {
        LOG_TAG = ActivityChooserModel.class.getSimpleName();
        sRegistryLock = new Object();
        sDataModelRegistry = new HashMap<String, ActivityChooserModel>();
    }
    
    private ActivityChooserModel(final Context context, final String s) {
        this.mInstanceLock = new Object();
        this.mActivities = new ArrayList<ActivityResolveInfo>();
        this.mHistoricalRecords = new ArrayList<HistoricalRecord>();
        this.mActivitySorter = (ActivitySorter)new DefaultSorter();
        this.mHistoryMaxSize = 50;
        this.mCanReadHistoricalData = true;
        this.mReadShareHistoryCalled = false;
        this.mHistoricalRecordsChanged = true;
        this.mReloadActivities = false;
        this.mContext = context.getApplicationContext();
        if (!TextUtils.isEmpty((CharSequence)s) && !s.endsWith(".xml")) {
            this.mHistoryFileName = s + ".xml";
        }
        else {
            this.mHistoryFileName = s;
        }
    }
    
    private boolean addHistoricalRecord(final HistoricalRecord historicalRecord) {
        final boolean add = this.mHistoricalRecords.add(historicalRecord);
        if (add) {
            this.mHistoricalRecordsChanged = true;
            this.pruneExcessiveHistoricalRecordsIfNeeded();
            this.persistHistoricalDataIfNeeded();
            this.sortActivitiesIfNeeded();
            this.notifyChanged();
        }
        return add;
    }
    
    private void ensureConsistentState() {
        final boolean loadActivitiesIfNeeded = this.loadActivitiesIfNeeded();
        final boolean historicalDataIfNeeded = this.readHistoricalDataIfNeeded();
        this.pruneExcessiveHistoricalRecordsIfNeeded();
        if (loadActivitiesIfNeeded | historicalDataIfNeeded) {
            this.sortActivitiesIfNeeded();
            this.notifyChanged();
        }
    }
    
    public static ActivityChooserModel get(final Context context, final String s) {
        synchronized (ActivityChooserModel.sRegistryLock) {
            final ActivityChooserModel activityChooserModel = ActivityChooserModel.sDataModelRegistry.get(s);
            ActivityChooserModel activityChooserModel2;
            if (activityChooserModel != null) {
                activityChooserModel2 = activityChooserModel;
            }
            else {
                final ActivityChooserModel activityChooserModel3 = new ActivityChooserModel(context, s);
                ActivityChooserModel.sDataModelRegistry.put(s, activityChooserModel3);
                activityChooserModel2 = activityChooserModel3;
            }
            return activityChooserModel2;
        }
    }
    
    private boolean loadActivitiesIfNeeded() {
        if (this.mReloadActivities && this.mIntent != null) {
            this.mReloadActivities = false;
            this.mActivities.clear();
            final List queryIntentActivities = this.mContext.getPackageManager().queryIntentActivities(this.mIntent, 0);
            for (int size = queryIntentActivities.size(), i = 0; i < size; ++i) {
                this.mActivities.add(new ActivityResolveInfo((ResolveInfo)queryIntentActivities.get(i)));
            }
            return true;
        }
        return false;
    }
    
    private void persistHistoricalDataIfNeeded() {
        if (!this.mReadShareHistoryCalled) {
            throw new IllegalStateException("No preceding call to #readHistoricalData");
        }
        if (this.mHistoricalRecordsChanged) {
            this.mHistoricalRecordsChanged = false;
            if (!TextUtils.isEmpty((CharSequence)this.mHistoryFileName)) {
                new PersistHistoryAsyncTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Object[] { new ArrayList(this.mHistoricalRecords), this.mHistoryFileName });
            }
        }
    }
    
    private void pruneExcessiveHistoricalRecordsIfNeeded() {
        final int n = this.mHistoricalRecords.size() - this.mHistoryMaxSize;
        if (n > 0) {
            this.mHistoricalRecordsChanged = true;
            for (int i = 0; i < n; ++i) {
                final HistoricalRecord historicalRecord = this.mHistoricalRecords.remove(0);
            }
        }
    }
    
    private boolean readHistoricalDataIfNeeded() {
        if (this.mCanReadHistoricalData && this.mHistoricalRecordsChanged && !TextUtils.isEmpty((CharSequence)this.mHistoryFileName)) {
            this.mCanReadHistoricalData = false;
            this.mReadShareHistoryCalled = true;
            this.readHistoricalDataImpl();
            return true;
        }
        return false;
    }
    
    private void readHistoricalDataImpl() {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     1: istore_1       
        //     2: aload_0        
        //     3: getfield        android/support/v7/widget/ActivityChooserModel.mContext:Landroid/content/Context;
        //     6: aload_0        
        //     7: getfield        android/support/v7/widget/ActivityChooserModel.mHistoryFileName:Ljava/lang/String;
        //    10: invokevirtual   android/content/Context.openFileInput:(Ljava/lang/String;)Ljava/io/FileInputStream;
        //    13: astore_2       
        //    14: invokestatic    android/util/Xml.newPullParser:()Lorg/xmlpull/v1/XmlPullParser;
        //    17: astore_3       
        //    18: aload_3        
        //    19: aload_2        
        //    20: ldc_w           "UTF-8"
        //    23: invokeinterface org/xmlpull/v1/XmlPullParser.setInput:(Ljava/io/InputStream;Ljava/lang/String;)V
        //    28: iload_1        
        //    29: iconst_1       
        //    30: if_icmpne       212
        //    33: ldc             "historical-records"
        //    35: aload_3        
        //    36: invokeinterface org/xmlpull/v1/XmlPullParser.getName:()Ljava/lang/String;
        //    41: invokevirtual   java/lang/String.equals:(Ljava/lang/Object;)Z
        //    44: ifeq            227
        //    47: aload_0        
        //    48: getfield        android/support/v7/widget/ActivityChooserModel.mHistoricalRecords:Ljava/util/List;
        //    51: astore          4
        //    53: aload           4
        //    55: invokeinterface java/util/List.clear:()V
        //    60: aload_3        
        //    61: invokeinterface org/xmlpull/v1/XmlPullParser.next:()I
        //    66: istore_1       
        //    67: iload_1        
        //    68: iconst_1       
        //    69: if_icmpeq       324
        //    72: iload_1        
        //    73: iconst_3       
        //    74: if_icmpeq       60
        //    77: iload_1        
        //    78: iconst_4       
        //    79: if_icmpeq       60
        //    82: ldc             "historical-record"
        //    84: aload_3        
        //    85: invokeinterface org/xmlpull/v1/XmlPullParser.getName:()Ljava/lang/String;
        //    90: invokevirtual   java/lang/String.equals:(Ljava/lang/Object;)Z
        //    93: ifeq            299
        //    96: aload_3        
        //    97: aconst_null    
        //    98: ldc             "activity"
        //   100: invokeinterface org/xmlpull/v1/XmlPullParser.getAttributeValue:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;
        //   105: astore          5
        //   107: aload_3        
        //   108: aconst_null    
        //   109: ldc             "time"
        //   111: invokeinterface org/xmlpull/v1/XmlPullParser.getAttributeValue:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;
        //   116: invokestatic    java/lang/Long.parseLong:(Ljava/lang/String;)J
        //   119: lstore          6
        //   121: aload_3        
        //   122: aconst_null    
        //   123: ldc             "weight"
        //   125: invokeinterface org/xmlpull/v1/XmlPullParser.getAttributeValue:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;
        //   130: invokestatic    java/lang/Float.parseFloat:(Ljava/lang/String;)F
        //   133: fstore          8
        //   135: new             Landroid/support/v7/widget/ActivityChooserModel$HistoricalRecord;
        //   138: astore          9
        //   140: aload           9
        //   142: aload           5
        //   144: lload           6
        //   146: fload           8
        //   148: invokespecial   android/support/v7/widget/ActivityChooserModel$HistoricalRecord.<init>:(Ljava/lang/String;JF)V
        //   151: aload           4
        //   153: aload           9
        //   155: invokeinterface java/util/List.add:(Ljava/lang/Object;)Z
        //   160: pop            
        //   161: goto            60
        //   164: astore          9
        //   166: getstatic       android/support/v7/widget/ActivityChooserModel.LOG_TAG:Ljava/lang/String;
        //   169: astore_3       
        //   170: new             Ljava/lang/StringBuilder;
        //   173: astore          4
        //   175: aload           4
        //   177: invokespecial   java/lang/StringBuilder.<init>:()V
        //   180: aload_3        
        //   181: aload           4
        //   183: ldc_w           "Error reading historical recrod file: "
        //   186: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   189: aload_0        
        //   190: getfield        android/support/v7/widget/ActivityChooserModel.mHistoryFileName:Ljava/lang/String;
        //   193: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   196: invokevirtual   java/lang/StringBuilder.toString:()Ljava/lang/String;
        //   199: aload           9
        //   201: invokestatic    android/util/Log.e:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
        //   204: pop            
        //   205: aload_2        
        //   206: ifnonnull       339
        //   209: return         
        //   210: astore_2       
        //   211: return         
        //   212: iload_1        
        //   213: iconst_2       
        //   214: if_icmpeq       33
        //   217: aload_3        
        //   218: invokeinterface org/xmlpull/v1/XmlPullParser.next:()I
        //   223: istore_1       
        //   224: goto            28
        //   227: new             Lorg/xmlpull/v1/XmlPullParserException;
        //   230: astore          9
        //   232: aload           9
        //   234: ldc_w           "Share records file does not start with historical-records tag."
        //   237: invokespecial   org/xmlpull/v1/XmlPullParserException.<init>:(Ljava/lang/String;)V
        //   240: aload           9
        //   242: athrow         
        //   243: astore_3       
        //   244: getstatic       android/support/v7/widget/ActivityChooserModel.LOG_TAG:Ljava/lang/String;
        //   247: astore          9
        //   249: new             Ljava/lang/StringBuilder;
        //   252: astore          4
        //   254: aload           4
        //   256: invokespecial   java/lang/StringBuilder.<init>:()V
        //   259: aload           9
        //   261: aload           4
        //   263: ldc_w           "Error reading historical recrod file: "
        //   266: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   269: aload_0        
        //   270: getfield        android/support/v7/widget/ActivityChooserModel.mHistoryFileName:Ljava/lang/String;
        //   273: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   276: invokevirtual   java/lang/StringBuilder.toString:()Ljava/lang/String;
        //   279: aload_3        
        //   280: invokestatic    android/util/Log.e:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
        //   283: pop            
        //   284: aload_2        
        //   285: ifnull          209
        //   288: aload_2        
        //   289: invokevirtual   java/io/FileInputStream.close:()V
        //   292: goto            209
        //   295: astore_2       
        //   296: goto            209
        //   299: new             Lorg/xmlpull/v1/XmlPullParserException;
        //   302: astore          9
        //   304: aload           9
        //   306: ldc_w           "Share records file not well-formed."
        //   309: invokespecial   org/xmlpull/v1/XmlPullParserException.<init>:(Ljava/lang/String;)V
        //   312: aload           9
        //   314: athrow         
        //   315: astore          9
        //   317: aload_2        
        //   318: ifnonnull       350
        //   321: aload           9
        //   323: athrow         
        //   324: aload_2        
        //   325: ifnull          209
        //   328: aload_2        
        //   329: invokevirtual   java/io/FileInputStream.close:()V
        //   332: goto            209
        //   335: astore_2       
        //   336: goto            209
        //   339: aload_2        
        //   340: invokevirtual   java/io/FileInputStream.close:()V
        //   343: goto            209
        //   346: astore_2       
        //   347: goto            209
        //   350: aload_2        
        //   351: invokevirtual   java/io/FileInputStream.close:()V
        //   354: goto            321
        //   357: astore_2       
        //   358: goto            321
        //    Exceptions:
        //  Try           Handler
        //  Start  End    Start  End    Type                                   
        //  -----  -----  -----  -----  ---------------------------------------
        //  2      14     210    212    Ljava/io/FileNotFoundException;
        //  14     28     164    350    Lorg/xmlpull/v1/XmlPullParserException;
        //  14     28     243    299    Ljava/io/IOException;
        //  14     28     315    361    Any
        //  33     60     164    350    Lorg/xmlpull/v1/XmlPullParserException;
        //  33     60     243    299    Ljava/io/IOException;
        //  33     60     315    361    Any
        //  60     67     164    350    Lorg/xmlpull/v1/XmlPullParserException;
        //  60     67     243    299    Ljava/io/IOException;
        //  60     67     315    361    Any
        //  82     161    164    350    Lorg/xmlpull/v1/XmlPullParserException;
        //  82     161    243    299    Ljava/io/IOException;
        //  82     161    315    361    Any
        //  166    205    315    361    Any
        //  217    224    164    350    Lorg/xmlpull/v1/XmlPullParserException;
        //  217    224    243    299    Ljava/io/IOException;
        //  217    224    315    361    Any
        //  227    243    164    350    Lorg/xmlpull/v1/XmlPullParserException;
        //  227    243    243    299    Ljava/io/IOException;
        //  227    243    315    361    Any
        //  244    284    315    361    Any
        //  288    292    295    299    Ljava/io/IOException;
        //  299    315    164    350    Lorg/xmlpull/v1/XmlPullParserException;
        //  299    315    243    299    Ljava/io/IOException;
        //  299    315    315    361    Any
        //  328    332    335    339    Ljava/io/IOException;
        //  339    343    346    350    Ljava/io/IOException;
        //  350    354    357    361    Ljava/io/IOException;
        // 
        // The error that occurred was:
        // 
        // java.lang.IllegalStateException: Expression is linked from several locations: Label_0028:
        //     at com.strobel.decompiler.ast.Error.expressionLinkedFromMultipleLocations(Error.java:27)
        //     at com.strobel.decompiler.ast.AstOptimizer.mergeDisparateObjectInitializations(AstOptimizer.java:2604)
        //     at com.strobel.decompiler.ast.AstOptimizer.optimize(AstOptimizer.java:235)
        //     at com.strobel.decompiler.ast.AstOptimizer.optimize(AstOptimizer.java:42)
        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:206)
        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:93)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethodBody(AstBuilder.java:868)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethod(AstBuilder.java:761)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addTypeMembers(AstBuilder.java:638)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeCore(AstBuilder.java:605)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeNoCache(AstBuilder.java:195)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createType(AstBuilder.java:162)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addType(AstBuilder.java:137)
        //     at com.strobel.decompiler.languages.java.JavaLanguage.buildAst(JavaLanguage.java:71)
        //     at com.strobel.decompiler.languages.java.JavaLanguage.decompileType(JavaLanguage.java:59)
        //     at com.strobel.decompiler.DecompilerDriver.decompileType(DecompilerDriver.java:333)
        //     at com.strobel.decompiler.DecompilerDriver.decompileJar(DecompilerDriver.java:254)
        //     at com.strobel.decompiler.DecompilerDriver.main(DecompilerDriver.java:144)
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    private boolean sortActivitiesIfNeeded() {
        if (this.mActivitySorter != null && this.mIntent != null && !this.mActivities.isEmpty() && !this.mHistoricalRecords.isEmpty()) {
            this.mActivitySorter.sort(this.mIntent, this.mActivities, Collections.unmodifiableList((List<? extends HistoricalRecord>)this.mHistoricalRecords));
            return true;
        }
        return false;
    }
    
    public Intent chooseActivity(final int n) {
        synchronized (this.mInstanceLock) {
            if (this.mIntent == null) {
                return null;
            }
            this.ensureConsistentState();
            final ActivityResolveInfo activityResolveInfo = this.mActivities.get(n);
            final ComponentName component = new ComponentName(activityResolveInfo.resolveInfo.activityInfo.packageName, activityResolveInfo.resolveInfo.activityInfo.name);
            final Intent intent = new Intent(this.mIntent);
            intent.setComponent(component);
            if (this.mActivityChoserModelPolicy != null && this.mActivityChoserModelPolicy.onChooseActivity(this, new Intent(intent))) {
                return null;
            }
            this.addHistoricalRecord(new HistoricalRecord(component, System.currentTimeMillis(), 1.0f));
            return intent;
        }
    }
    
    public ResolveInfo getActivity(final int n) {
        synchronized (this.mInstanceLock) {
            this.ensureConsistentState();
            return this.mActivities.get(n).resolveInfo;
        }
    }
    
    public int getActivityCount() {
        synchronized (this.mInstanceLock) {
            this.ensureConsistentState();
            return this.mActivities.size();
        }
    }
    
    public int getActivityIndex(final ResolveInfo resolveInfo) {
        synchronized (this.mInstanceLock) {
            this.ensureConsistentState();
            final List<ActivityResolveInfo> mActivities = this.mActivities;
            for (int size = mActivities.size(), i = 0; i < size; ++i) {
                if (((ActivityResolveInfo)mActivities.get(i)).resolveInfo == resolveInfo) {
                    return i;
                }
            }
            return -1;
        }
    }
    
    public ResolveInfo getDefaultActivity() {
        synchronized (this.mInstanceLock) {
            this.ensureConsistentState();
            if (this.mActivities.isEmpty()) {
                return null;
            }
            return this.mActivities.get(0).resolveInfo;
        }
    }
    
    public int getHistoryMaxSize() {
        synchronized (this.mInstanceLock) {
            return this.mHistoryMaxSize;
        }
    }
    
    public int getHistorySize() {
        synchronized (this.mInstanceLock) {
            this.ensureConsistentState();
            return this.mHistoricalRecords.size();
        }
    }
    
    public Intent getIntent() {
        synchronized (this.mInstanceLock) {
            return this.mIntent;
        }
    }
    
    public void setActivitySorter(final ActivitySorter mActivitySorter) {
        synchronized (this.mInstanceLock) {
            if (this.mActivitySorter != mActivitySorter) {
                this.mActivitySorter = mActivitySorter;
                if (this.sortActivitiesIfNeeded()) {
                    this.notifyChanged();
                }
            }
        }
    }
    
    public void setDefaultActivity(final int n) {
        synchronized (this.mInstanceLock) {
            this.ensureConsistentState();
            final ActivityResolveInfo activityResolveInfo = this.mActivities.get(n);
            final ActivityResolveInfo activityResolveInfo2 = this.mActivities.get(0);
            float n2;
            if (activityResolveInfo2 == null) {
                n2 = 1.0f;
            }
            else {
                n2 = activityResolveInfo2.weight - activityResolveInfo.weight + 5.0f;
            }
            this.addHistoricalRecord(new HistoricalRecord(new ComponentName(activityResolveInfo.resolveInfo.activityInfo.packageName, activityResolveInfo.resolveInfo.activityInfo.name), System.currentTimeMillis(), n2));
        }
    }
    
    public void setHistoryMaxSize(final int mHistoryMaxSize) {
        synchronized (this.mInstanceLock) {
            if (this.mHistoryMaxSize != mHistoryMaxSize) {
                this.mHistoryMaxSize = mHistoryMaxSize;
                this.pruneExcessiveHistoricalRecordsIfNeeded();
                if (this.sortActivitiesIfNeeded()) {
                    this.notifyChanged();
                }
            }
        }
    }
    
    public void setIntent(final Intent mIntent) {
        synchronized (this.mInstanceLock) {
            if (this.mIntent != mIntent) {
                this.mIntent = mIntent;
                this.mReloadActivities = true;
                this.ensureConsistentState();
            }
        }
    }
    
    public void setOnChooseActivityListener(final OnChooseActivityListener mActivityChoserModelPolicy) {
        synchronized (this.mInstanceLock) {
            this.mActivityChoserModelPolicy = mActivityChoserModelPolicy;
        }
    }
    
    public interface ActivityChooserModelClient
    {
        void setActivityChooserModel(final ActivityChooserModel p0);
    }
    
    public static final class ActivityResolveInfo implements Comparable<ActivityResolveInfo>
    {
        public final ResolveInfo resolveInfo;
        public float weight;
        
        public ActivityResolveInfo(final ResolveInfo resolveInfo) {
            this.resolveInfo = resolveInfo;
        }
        
        @Override
        public int compareTo(final ActivityResolveInfo activityResolveInfo) {
            return Float.floatToIntBits(activityResolveInfo.weight) - Float.floatToIntBits(this.weight);
        }
        
        @Override
        public boolean equals(final Object o) {
            return this == o || (o != null && this.getClass() == o.getClass() && Float.floatToIntBits(this.weight) == Float.floatToIntBits(((ActivityResolveInfo)o).weight));
        }
        
        @Override
        public int hashCode() {
            return Float.floatToIntBits(this.weight) + 31;
        }
        
        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder();
            sb.append("[");
            sb.append("resolveInfo:").append(this.resolveInfo.toString());
            sb.append("; weight:").append(new BigDecimal(this.weight));
            sb.append("]");
            return sb.toString();
        }
    }
    
    public interface ActivitySorter
    {
        void sort(final Intent p0, final List<ActivityResolveInfo> p1, final List<HistoricalRecord> p2);
    }
    
    private static final class DefaultSorter implements ActivitySorter
    {
        private static final float WEIGHT_DECAY_COEFFICIENT = 0.95f;
        private final Map<ComponentName, ActivityResolveInfo> mPackageNameToActivityMap;
        
        DefaultSorter() {
            this.mPackageNameToActivityMap = new HashMap<ComponentName, ActivityResolveInfo>();
        }
        
        @Override
        public void sort(final Intent intent, final List<ActivityResolveInfo> list, final List<HistoricalRecord> list2) {
            final Map<ComponentName, ActivityResolveInfo> mPackageNameToActivityMap = this.mPackageNameToActivityMap;
            mPackageNameToActivityMap.clear();
            for (int size = list.size(), i = 0; i < size; ++i) {
                final ActivityResolveInfo activityResolveInfo = list.get(i);
                activityResolveInfo.weight = 0.0f;
                mPackageNameToActivityMap.put(new ComponentName(activityResolveInfo.resolveInfo.activityInfo.packageName, activityResolveInfo.resolveInfo.activityInfo.name), activityResolveInfo);
            }
            int j = list2.size();
            float n = 1.0f;
            --j;
            while (j >= 0) {
                final HistoricalRecord historicalRecord = list2.get(j);
                final ActivityResolveInfo activityResolveInfo2 = mPackageNameToActivityMap.get(historicalRecord.activity);
                if (activityResolveInfo2 != null) {
                    activityResolveInfo2.weight += historicalRecord.weight * n;
                    n *= 0.95f;
                }
                --j;
            }
            Collections.sort((List<Comparable>)list);
        }
    }
    
    public static final class HistoricalRecord
    {
        public final ComponentName activity;
        public final long time;
        public final float weight;
        
        public HistoricalRecord(final ComponentName activity, final long time, final float weight) {
            this.activity = activity;
            this.time = time;
            this.weight = weight;
        }
        
        public HistoricalRecord(final String s, final long n, final float n2) {
            this(ComponentName.unflattenFromString(s), n, n2);
        }
        
        @Override
        public boolean equals(final Object o) {
            if (this == o) {
                return true;
            }
            if (o == null) {
                return false;
            }
            if (this.getClass() == o.getClass()) {
                final HistoricalRecord historicalRecord = (HistoricalRecord)o;
                if (this.activity != null) {
                    if (!this.activity.equals((Object)historicalRecord.activity)) {
                        return false;
                    }
                }
                else if (historicalRecord.activity != null) {
                    return false;
                }
                return this.time == historicalRecord.time && Float.floatToIntBits(this.weight) == Float.floatToIntBits(historicalRecord.weight);
            }
            return false;
        }
        
        @Override
        public int hashCode() {
            int hashCode;
            if (this.activity != null) {
                hashCode = this.activity.hashCode();
            }
            else {
                hashCode = 0;
            }
            return ((hashCode + 31) * 31 + (int)(this.time ^ this.time >>> 32)) * 31 + Float.floatToIntBits(this.weight);
        }
        
        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder();
            sb.append("[");
            sb.append("; activity:").append(this.activity);
            sb.append("; time:").append(this.time);
            sb.append("; weight:").append(new BigDecimal(this.weight));
            sb.append("]");
            return sb.toString();
        }
    }
    
    public interface OnChooseActivityListener
    {
        boolean onChooseActivity(final ActivityChooserModel p0, final Intent p1);
    }
    
    private final class PersistHistoryAsyncTask extends AsyncTask<Object, Void, Void>
    {
        PersistHistoryAsyncTask() {
        }
        
        public Void doInBackground(final Object... p0) {
            // 
            // This method could not be decompiled.
            // 
            // Original Bytecode:
            // 
            //     1: istore_2       
            //     2: aload_1        
            //     3: iconst_0       
            //     4: aaload         
            //     5: checkcast       Ljava/util/List;
            //     8: astore_3       
            //     9: aload_1        
            //    10: iconst_1       
            //    11: aaload         
            //    12: checkcast       Ljava/lang/String;
            //    15: astore          4
            //    17: aload_0        
            //    18: getfield        android/support/v7/widget/ActivityChooserModel$PersistHistoryAsyncTask.this$0:Landroid/support/v7/widget/ActivityChooserModel;
            //    21: getfield        android/support/v7/widget/ActivityChooserModel.mContext:Landroid/content/Context;
            //    24: aload           4
            //    26: iconst_0       
            //    27: invokevirtual   android/content/Context.openFileOutput:(Ljava/lang/String;I)Ljava/io/FileOutputStream;
            //    30: astore_1       
            //    31: invokestatic    android/util/Xml.newSerializer:()Lorg/xmlpull/v1/XmlSerializer;
            //    34: astore          5
            //    36: aload           5
            //    38: aload_1        
            //    39: aconst_null    
            //    40: invokeinterface org/xmlpull/v1/XmlSerializer.setOutput:(Ljava/io/OutputStream;Ljava/lang/String;)V
            //    45: aload           5
            //    47: ldc             "UTF-8"
            //    49: iconst_1       
            //    50: invokestatic    java/lang/Boolean.valueOf:(Z)Ljava/lang/Boolean;
            //    53: invokeinterface org/xmlpull/v1/XmlSerializer.startDocument:(Ljava/lang/String;Ljava/lang/Boolean;)V
            //    58: aload           5
            //    60: aconst_null    
            //    61: ldc             "historical-records"
            //    63: invokeinterface org/xmlpull/v1/XmlSerializer.startTag:(Ljava/lang/String;Ljava/lang/String;)Lorg/xmlpull/v1/XmlSerializer;
            //    68: pop            
            //    69: aload_3        
            //    70: invokeinterface java/util/List.size:()I
            //    75: istore          6
            //    77: iload_2        
            //    78: iload           6
            //    80: if_icmplt       146
            //    83: aload           5
            //    85: aconst_null    
            //    86: ldc             "historical-records"
            //    88: invokeinterface org/xmlpull/v1/XmlSerializer.endTag:(Ljava/lang/String;Ljava/lang/String;)Lorg/xmlpull/v1/XmlSerializer;
            //    93: pop            
            //    94: aload           5
            //    96: invokeinterface org/xmlpull/v1/XmlSerializer.endDocument:()V
            //   101: aload_0        
            //   102: getfield        android/support/v7/widget/ActivityChooserModel$PersistHistoryAsyncTask.this$0:Landroid/support/v7/widget/ActivityChooserModel;
            //   105: iconst_1       
            //   106: putfield        android/support/v7/widget/ActivityChooserModel.mCanReadHistoricalData:Z
            //   109: aload_1        
            //   110: ifnonnull       243
            //   113: aconst_null    
            //   114: areturn        
            //   115: astore_1       
            //   116: getstatic       android/support/v7/widget/ActivityChooserModel.LOG_TAG:Ljava/lang/String;
            //   119: new             Ljava/lang/StringBuilder;
            //   122: dup            
            //   123: invokespecial   java/lang/StringBuilder.<init>:()V
            //   126: ldc             "Error writing historical record file: "
            //   128: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
            //   131: aload           4
            //   133: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
            //   136: invokevirtual   java/lang/StringBuilder.toString:()Ljava/lang/String;
            //   139: aload_1        
            //   140: invokestatic    android/util/Log.e:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
            //   143: pop            
            //   144: aconst_null    
            //   145: areturn        
            //   146: aload_3        
            //   147: iconst_0       
            //   148: invokeinterface java/util/List.remove:(I)Ljava/lang/Object;
            //   153: checkcast       Landroid/support/v7/widget/ActivityChooserModel$HistoricalRecord;
            //   156: astore          4
            //   158: aload           5
            //   160: aconst_null    
            //   161: ldc             "historical-record"
            //   163: invokeinterface org/xmlpull/v1/XmlSerializer.startTag:(Ljava/lang/String;Ljava/lang/String;)Lorg/xmlpull/v1/XmlSerializer;
            //   168: pop            
            //   169: aload           5
            //   171: aconst_null    
            //   172: ldc             "activity"
            //   174: aload           4
            //   176: getfield        android/support/v7/widget/ActivityChooserModel$HistoricalRecord.activity:Landroid/content/ComponentName;
            //   179: invokevirtual   android/content/ComponentName.flattenToString:()Ljava/lang/String;
            //   182: invokeinterface org/xmlpull/v1/XmlSerializer.attribute:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Lorg/xmlpull/v1/XmlSerializer;
            //   187: pop            
            //   188: aload           5
            //   190: aconst_null    
            //   191: ldc             "time"
            //   193: aload           4
            //   195: getfield        android/support/v7/widget/ActivityChooserModel$HistoricalRecord.time:J
            //   198: invokestatic    java/lang/String.valueOf:(J)Ljava/lang/String;
            //   201: invokeinterface org/xmlpull/v1/XmlSerializer.attribute:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Lorg/xmlpull/v1/XmlSerializer;
            //   206: pop            
            //   207: aload           5
            //   209: aconst_null    
            //   210: ldc             "weight"
            //   212: aload           4
            //   214: getfield        android/support/v7/widget/ActivityChooserModel$HistoricalRecord.weight:F
            //   217: invokestatic    java/lang/String.valueOf:(F)Ljava/lang/String;
            //   220: invokeinterface org/xmlpull/v1/XmlSerializer.attribute:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Lorg/xmlpull/v1/XmlSerializer;
            //   225: pop            
            //   226: aload           5
            //   228: aconst_null    
            //   229: ldc             "historical-record"
            //   231: invokeinterface org/xmlpull/v1/XmlSerializer.endTag:(Ljava/lang/String;Ljava/lang/String;)Lorg/xmlpull/v1/XmlSerializer;
            //   236: pop            
            //   237: iinc            2, 1
            //   240: goto            77
            //   243: aload_1        
            //   244: invokevirtual   java/io/FileOutputStream.close:()V
            //   247: goto            113
            //   250: astore_1       
            //   251: goto            113
            //   254: astore          4
            //   256: getstatic       android/support/v7/widget/ActivityChooserModel.LOG_TAG:Ljava/lang/String;
            //   259: astore          5
            //   261: new             Ljava/lang/StringBuilder;
            //   264: astore_3       
            //   265: aload_3        
            //   266: invokespecial   java/lang/StringBuilder.<init>:()V
            //   269: aload           5
            //   271: aload_3        
            //   272: ldc             "Error writing historical record file: "
            //   274: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
            //   277: aload_0        
            //   278: getfield        android/support/v7/widget/ActivityChooserModel$PersistHistoryAsyncTask.this$0:Landroid/support/v7/widget/ActivityChooserModel;
            //   281: getfield        android/support/v7/widget/ActivityChooserModel.mHistoryFileName:Ljava/lang/String;
            //   284: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
            //   287: invokevirtual   java/lang/StringBuilder.toString:()Ljava/lang/String;
            //   290: aload           4
            //   292: invokestatic    android/util/Log.e:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
            //   295: pop            
            //   296: aload_0        
            //   297: getfield        android/support/v7/widget/ActivityChooserModel$PersistHistoryAsyncTask.this$0:Landroid/support/v7/widget/ActivityChooserModel;
            //   300: iconst_1       
            //   301: putfield        android/support/v7/widget/ActivityChooserModel.mCanReadHistoricalData:Z
            //   304: aload_1        
            //   305: ifnull          113
            //   308: aload_1        
            //   309: invokevirtual   java/io/FileOutputStream.close:()V
            //   312: goto            113
            //   315: astore_1       
            //   316: goto            113
            //   319: astore          4
            //   321: getstatic       android/support/v7/widget/ActivityChooserModel.LOG_TAG:Ljava/lang/String;
            //   324: astore_3       
            //   325: new             Ljava/lang/StringBuilder;
            //   328: astore          5
            //   330: aload           5
            //   332: invokespecial   java/lang/StringBuilder.<init>:()V
            //   335: aload_3        
            //   336: aload           5
            //   338: ldc             "Error writing historical record file: "
            //   340: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
            //   343: aload_0        
            //   344: getfield        android/support/v7/widget/ActivityChooserModel$PersistHistoryAsyncTask.this$0:Landroid/support/v7/widget/ActivityChooserModel;
            //   347: getfield        android/support/v7/widget/ActivityChooserModel.mHistoryFileName:Ljava/lang/String;
            //   350: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
            //   353: invokevirtual   java/lang/StringBuilder.toString:()Ljava/lang/String;
            //   356: aload           4
            //   358: invokestatic    android/util/Log.e:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
            //   361: pop            
            //   362: aload_0        
            //   363: getfield        android/support/v7/widget/ActivityChooserModel$PersistHistoryAsyncTask.this$0:Landroid/support/v7/widget/ActivityChooserModel;
            //   366: iconst_1       
            //   367: putfield        android/support/v7/widget/ActivityChooserModel.mCanReadHistoricalData:Z
            //   370: aload_1        
            //   371: ifnull          113
            //   374: aload_1        
            //   375: invokevirtual   java/io/FileOutputStream.close:()V
            //   378: goto            113
            //   381: astore_1       
            //   382: goto            113
            //   385: astore          4
            //   387: getstatic       android/support/v7/widget/ActivityChooserModel.LOG_TAG:Ljava/lang/String;
            //   390: astore_3       
            //   391: new             Ljava/lang/StringBuilder;
            //   394: astore          5
            //   396: aload           5
            //   398: invokespecial   java/lang/StringBuilder.<init>:()V
            //   401: aload_3        
            //   402: aload           5
            //   404: ldc             "Error writing historical record file: "
            //   406: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
            //   409: aload_0        
            //   410: getfield        android/support/v7/widget/ActivityChooserModel$PersistHistoryAsyncTask.this$0:Landroid/support/v7/widget/ActivityChooserModel;
            //   413: getfield        android/support/v7/widget/ActivityChooserModel.mHistoryFileName:Ljava/lang/String;
            //   416: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
            //   419: invokevirtual   java/lang/StringBuilder.toString:()Ljava/lang/String;
            //   422: aload           4
            //   424: invokestatic    android/util/Log.e:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
            //   427: pop            
            //   428: aload_0        
            //   429: getfield        android/support/v7/widget/ActivityChooserModel$PersistHistoryAsyncTask.this$0:Landroid/support/v7/widget/ActivityChooserModel;
            //   432: iconst_1       
            //   433: putfield        android/support/v7/widget/ActivityChooserModel.mCanReadHistoricalData:Z
            //   436: aload_1        
            //   437: ifnull          113
            //   440: aload_1        
            //   441: invokevirtual   java/io/FileOutputStream.close:()V
            //   444: goto            113
            //   447: astore_1       
            //   448: goto            113
            //   451: astore_3       
            //   452: aload_0        
            //   453: getfield        android/support/v7/widget/ActivityChooserModel$PersistHistoryAsyncTask.this$0:Landroid/support/v7/widget/ActivityChooserModel;
            //   456: iconst_1       
            //   457: putfield        android/support/v7/widget/ActivityChooserModel.mCanReadHistoricalData:Z
            //   460: aload_1        
            //   461: ifnonnull       466
            //   464: aload_3        
            //   465: athrow         
            //   466: aload_1        
            //   467: invokevirtual   java/io/FileOutputStream.close:()V
            //   470: goto            464
            //   473: astore_1       
            //   474: goto            464
            //    Exceptions:
            //  Try           Handler
            //  Start  End    Start  End    Type                                
            //  -----  -----  -----  -----  ------------------------------------
            //  17     31     115    146    Ljava/io/FileNotFoundException;
            //  36     77     254    319    Ljava/lang/IllegalArgumentException;
            //  36     77     319    385    Ljava/lang/IllegalStateException;
            //  36     77     385    451    Ljava/io/IOException;
            //  36     77     451    477    Any
            //  83     101    254    319    Ljava/lang/IllegalArgumentException;
            //  83     101    319    385    Ljava/lang/IllegalStateException;
            //  83     101    385    451    Ljava/io/IOException;
            //  83     101    451    477    Any
            //  146    237    254    319    Ljava/lang/IllegalArgumentException;
            //  146    237    319    385    Ljava/lang/IllegalStateException;
            //  146    237    385    451    Ljava/io/IOException;
            //  146    237    451    477    Any
            //  243    247    250    254    Ljava/io/IOException;
            //  256    296    451    477    Any
            //  308    312    315    319    Ljava/io/IOException;
            //  321    362    451    477    Any
            //  374    378    381    385    Ljava/io/IOException;
            //  387    428    451    477    Any
            //  440    444    447    451    Ljava/io/IOException;
            //  466    470    473    477    Ljava/io/IOException;
            // 
            // The error that occurred was:
            // 
            // java.lang.IllegalStateException: Expression is linked from several locations: Label_0077:
            //     at com.strobel.decompiler.ast.Error.expressionLinkedFromMultipleLocations(Error.java:27)
            //     at com.strobel.decompiler.ast.AstOptimizer.mergeDisparateObjectInitializations(AstOptimizer.java:2604)
            //     at com.strobel.decompiler.ast.AstOptimizer.optimize(AstOptimizer.java:235)
            //     at com.strobel.decompiler.ast.AstOptimizer.optimize(AstOptimizer.java:42)
            //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:206)
            //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:93)
            //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethodBody(AstBuilder.java:868)
            //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethod(AstBuilder.java:761)
            //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addTypeMembers(AstBuilder.java:638)
            //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeCore(AstBuilder.java:605)
            //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeNoCache(AstBuilder.java:195)
            //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addTypeMembers(AstBuilder.java:662)
            //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeCore(AstBuilder.java:605)
            //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeNoCache(AstBuilder.java:195)
            //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createType(AstBuilder.java:162)
            //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addType(AstBuilder.java:137)
            //     at com.strobel.decompiler.languages.java.JavaLanguage.buildAst(JavaLanguage.java:71)
            //     at com.strobel.decompiler.languages.java.JavaLanguage.decompileType(JavaLanguage.java:59)
            //     at com.strobel.decompiler.DecompilerDriver.decompileType(DecompilerDriver.java:333)
            //     at com.strobel.decompiler.DecompilerDriver.decompileJar(DecompilerDriver.java:254)
            //     at com.strobel.decompiler.DecompilerDriver.main(DecompilerDriver.java:144)
            // 
            throw new IllegalStateException("An error occurred while decompiling this method.");
        }
    }
}
