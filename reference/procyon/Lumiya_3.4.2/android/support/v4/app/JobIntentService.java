// 
// Decompiled by Procyon v0.6.0
// 

package android.support.v4.app;

import android.app.job.JobInfo$Builder;
import android.app.job.JobScheduler;
import android.app.job.JobInfo;
import android.app.job.JobWorkItem;
import android.app.job.JobParameters;
import android.support.annotation.RequiresApi;
import android.app.job.JobServiceEngine;
import android.os.PowerManager;
import android.os.PowerManager$WakeLock;
import android.support.annotation.Nullable;
import android.os.IBinder;
import android.os.AsyncTask;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.content.Context;
import android.os.Build$VERSION;
import java.util.ArrayList;
import android.content.ComponentName;
import java.util.HashMap;
import android.app.Service;

public abstract class JobIntentService extends Service
{
    static final boolean DEBUG = false;
    static final String TAG = "JobIntentService";
    static final HashMap<ComponentName, WorkEnqueuer> sClassWorkEnqueuer;
    static final Object sLock;
    final ArrayList<CompatWorkItem> mCompatQueue;
    WorkEnqueuer mCompatWorkEnqueuer;
    CommandProcessor mCurProcessor;
    boolean mDestroyed;
    boolean mInterruptIfStopped;
    CompatJobEngine mJobImpl;
    boolean mStopped;
    
    static {
        sLock = new Object();
        sClassWorkEnqueuer = new HashMap<ComponentName, WorkEnqueuer>();
    }
    
    public JobIntentService() {
        this.mInterruptIfStopped = false;
        this.mStopped = false;
        this.mDestroyed = false;
        if (Build$VERSION.SDK_INT < 26) {
            this.mCompatQueue = new ArrayList<CompatWorkItem>();
        }
        else {
            this.mCompatQueue = null;
        }
    }
    
    public static void enqueueWork(@NonNull final Context context, @NonNull final ComponentName componentName, final int n, @NonNull final Intent intent) {
        Label_0034: {
            if (intent == null) {
                break Label_0034;
            }
            synchronized (JobIntentService.sLock) {
                final WorkEnqueuer workEnqueuer = getWorkEnqueuer(context, componentName, true, n);
                workEnqueuer.ensureJobId(n);
                workEnqueuer.enqueueWork(intent);
                return;
                throw new IllegalArgumentException("work must not be null");
            }
        }
    }
    
    public static void enqueueWork(@NonNull final Context context, @NonNull final Class clazz, final int n, @NonNull final Intent intent) {
        enqueueWork(context, new ComponentName(context, clazz), n, intent);
    }
    
    static WorkEnqueuer getWorkEnqueuer(final Context context, final ComponentName componentName, final boolean b, final int n) {
        final WorkEnqueuer workEnqueuer = JobIntentService.sClassWorkEnqueuer.get(componentName);
        Object value;
        if (workEnqueuer != null) {
            value = workEnqueuer;
        }
        else {
            if (Build$VERSION.SDK_INT < 26) {
                value = new CompatWorkEnqueuer(context, componentName);
            }
            else {
                if (!b) {
                    throw new IllegalArgumentException("Can't be here without a job id");
                }
                value = new JobWorkEnqueuer(context, componentName, n);
            }
            JobIntentService.sClassWorkEnqueuer.put(componentName, (WorkEnqueuer)value);
        }
        return (WorkEnqueuer)value;
    }
    
    GenericWorkItem dequeueWork() {
        Label_0028: {
            if (this.mJobImpl != null) {
                break Label_0028;
            }
            synchronized (this.mCompatQueue) {
                if (this.mCompatQueue.size() <= 0) {
                    return null;
                }
                return (GenericWorkItem)this.mCompatQueue.remove(0);
                return this.mJobImpl.dequeueWork();
            }
        }
    }
    
    boolean doStopCurrentWork() {
        if (this.mCurProcessor != null) {
            this.mCurProcessor.cancel(this.mInterruptIfStopped);
        }
        this.mStopped = true;
        return this.onStopCurrentWork();
    }
    
    void ensureProcessorRunningLocked(final boolean b) {
        if (this.mCurProcessor == null) {
            this.mCurProcessor = new CommandProcessor();
            if (this.mCompatWorkEnqueuer != null && b) {
                this.mCompatWorkEnqueuer.serviceProcessingStarted();
            }
            this.mCurProcessor.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (Object[])new Void[0]);
        }
    }
    
    public boolean isStopped() {
        return this.mStopped;
    }
    
    public IBinder onBind(@NonNull final Intent intent) {
        if (this.mJobImpl == null) {
            return null;
        }
        return this.mJobImpl.compatGetBinder();
    }
    
    public void onCreate() {
        super.onCreate();
        if (Build$VERSION.SDK_INT < 26) {
            this.mJobImpl = null;
            this.mCompatWorkEnqueuer = getWorkEnqueuer((Context)this, new ComponentName((Context)this, (Class)this.getClass()), false, 0);
        }
        else {
            this.mJobImpl = (CompatJobEngine)new JobServiceEngineImpl(this);
            this.mCompatWorkEnqueuer = null;
        }
    }
    
    public void onDestroy() {
        super.onDestroy();
        if (this.mCompatQueue != null) {
            synchronized (this.mCompatQueue) {
                this.mDestroyed = true;
                this.mCompatWorkEnqueuer.serviceProcessingFinished();
            }
        }
    }
    
    protected abstract void onHandleWork(@NonNull final Intent p0);
    
    public int onStartCommand(@Nullable final Intent intent, final int n, final int n2) {
        if (this.mCompatQueue == null) {
            return 2;
        }
        this.mCompatWorkEnqueuer.serviceStartReceived();
        synchronized (this.mCompatQueue) {
            final ArrayList<CompatWorkItem> mCompatQueue = this.mCompatQueue;
            Intent intent2 = intent;
            if (intent == null) {
                intent2 = new Intent();
            }
            mCompatQueue.add(new CompatWorkItem(intent2, n2));
            this.ensureProcessorRunningLocked(true);
            return 3;
        }
    }
    
    public boolean onStopCurrentWork() {
        return true;
    }
    
    void processorFinished() {
        if (this.mCompatQueue != null) {
        Label_0034_Outer:
            while (true) {
            Label_0034:
                while (true) {
                Label_0062:
                    while (true) {
                        synchronized (this.mCompatQueue) {
                            this.mCurProcessor = null;
                            if (this.mCompatQueue == null) {
                                if (this.mDestroyed) {
                                    break;
                                }
                                break Label_0062;
                            }
                        }
                        if (this.mCompatQueue.size() > 0) {
                            this.ensureProcessorRunningLocked(false);
                            continue Label_0034;
                        }
                        continue Label_0034_Outer;
                    }
                    this.mCompatWorkEnqueuer.serviceProcessingFinished();
                    continue Label_0034;
                }
            }
        }
    }
    
    public void setInterruptIfStopped(final boolean mInterruptIfStopped) {
        this.mInterruptIfStopped = mInterruptIfStopped;
    }
    
    final class CommandProcessor extends AsyncTask<Void, Void, Void>
    {
        protected Void doInBackground(final Void... array) {
            while (true) {
                final GenericWorkItem dequeueWork = JobIntentService.this.dequeueWork();
                if (dequeueWork == null) {
                    break;
                }
                JobIntentService.this.onHandleWork(dequeueWork.getIntent());
                dequeueWork.complete();
            }
            return null;
        }
        
        protected void onCancelled(final Void void1) {
            JobIntentService.this.processorFinished();
        }
        
        protected void onPostExecute(final Void void1) {
            JobIntentService.this.processorFinished();
        }
    }
    
    interface CompatJobEngine
    {
        IBinder compatGetBinder();
        
        GenericWorkItem dequeueWork();
    }
    
    static final class CompatWorkEnqueuer extends WorkEnqueuer
    {
        private final Context mContext;
        private final PowerManager$WakeLock mLaunchWakeLock;
        boolean mLaunchingService;
        private final PowerManager$WakeLock mRunWakeLock;
        boolean mServiceProcessing;
        
        CompatWorkEnqueuer(final Context context, final ComponentName componentName) {
            super(context, componentName);
            this.mContext = context.getApplicationContext();
            final PowerManager powerManager = (PowerManager)context.getSystemService("power");
            (this.mLaunchWakeLock = powerManager.newWakeLock(1, componentName.getClassName() + ":launch")).setReferenceCounted(false);
            (this.mRunWakeLock = powerManager.newWakeLock(1, componentName.getClassName() + ":run")).setReferenceCounted(false);
        }
        
        @Override
        void enqueueWork(Intent intent) {
            intent = new Intent(intent);
            intent.setComponent(this.mComponentName);
            if (this.mContext.startService(intent) != null) {
                while (true) {
                    while (true) {
                        synchronized (this) {
                            if (this.mLaunchingService) {
                                break;
                            }
                        }
                        this.mLaunchingService = true;
                        if (!this.mServiceProcessing) {
                            this.mLaunchWakeLock.acquire(60000L);
                            continue;
                        }
                        continue;
                    }
                }
            }
        }
        
        @Override
        public void serviceProcessingFinished() {
            while (true) {
                while (true) {
                    Label_0039: {
                        synchronized (this) {
                            if (this.mServiceProcessing) {
                                if (this.mLaunchingService) {
                                    break Label_0039;
                                }
                                this.mServiceProcessing = false;
                                this.mRunWakeLock.release();
                            }
                            return;
                        }
                    }
                    this.mLaunchWakeLock.acquire(60000L);
                    continue;
                }
            }
        }
        
        @Override
        public void serviceProcessingStarted() {
            synchronized (this) {
                if (!this.mServiceProcessing) {
                    this.mServiceProcessing = true;
                    this.mRunWakeLock.acquire(600000L);
                    this.mLaunchWakeLock.release();
                }
            }
        }
        
        @Override
        public void serviceStartReceived() {
            synchronized (this) {
                this.mLaunchingService = false;
            }
        }
    }
    
    final class CompatWorkItem implements GenericWorkItem
    {
        final Intent mIntent;
        final int mStartId;
        
        CompatWorkItem(final Intent mIntent, final int mStartId) {
            this.mIntent = mIntent;
            this.mStartId = mStartId;
        }
        
        @Override
        public void complete() {
            JobIntentService.this.stopSelf(this.mStartId);
        }
        
        @Override
        public Intent getIntent() {
            return this.mIntent;
        }
    }
    
    interface GenericWorkItem
    {
        void complete();
        
        Intent getIntent();
    }
    
    @RequiresApi(26)
    static final class JobServiceEngineImpl extends JobServiceEngine implements CompatJobEngine
    {
        static final boolean DEBUG = false;
        static final String TAG = "JobServiceEngineImpl";
        final Object mLock;
        JobParameters mParams;
        final JobIntentService mService;
        
        JobServiceEngineImpl(final JobIntentService mService) {
            super((Service)mService);
            this.mLock = new Object();
            this.mService = mService;
        }
        
        public IBinder compatGetBinder() {
            return this.getBinder();
        }
        
        public GenericWorkItem dequeueWork() {
            synchronized (this.mLock) {
                if (this.mParams == null) {
                    return null;
                }
                final JobWorkItem dequeueWork = this.mParams.dequeueWork();
                monitorexit(this.mLock);
                if (dequeueWork == null) {
                    return null;
                }
            }
            final JobWorkItem jobWorkItem;
            jobWorkItem.getIntent().setExtrasClassLoader(this.mService.getClassLoader());
            return new WrapperWorkItem(jobWorkItem);
        }
        
        public boolean onStartJob(final JobParameters mParams) {
            this.mParams = mParams;
            this.mService.ensureProcessorRunningLocked(false);
            return true;
        }
        
        public boolean onStopJob(final JobParameters jobParameters) {
            final boolean doStopCurrentWork = this.mService.doStopCurrentWork();
            synchronized (this.mLock) {
                this.mParams = null;
                return doStopCurrentWork;
            }
        }
        
        final class WrapperWorkItem implements GenericWorkItem
        {
            final JobWorkItem mJobWork;
            
            WrapperWorkItem(final JobWorkItem mJobWork) {
                this.mJobWork = mJobWork;
            }
            
            @Override
            public void complete() {
                synchronized (JobServiceEngineImpl.this.mLock) {
                    if (JobServiceEngineImpl.this.mParams != null) {
                        JobServiceEngineImpl.this.mParams.completeWork(this.mJobWork);
                    }
                }
            }
            
            @Override
            public Intent getIntent() {
                return this.mJobWork.getIntent();
            }
        }
    }
    
    @RequiresApi(26)
    static final class JobWorkEnqueuer extends WorkEnqueuer
    {
        private final JobInfo mJobInfo;
        private final JobScheduler mJobScheduler;
        
        JobWorkEnqueuer(final Context context, final ComponentName componentName, final int n) {
            super(context, componentName);
            ((WorkEnqueuer)this).ensureJobId(n);
            this.mJobInfo = new JobInfo$Builder(n, this.mComponentName).setOverrideDeadline(0L).build();
            this.mJobScheduler = (JobScheduler)context.getApplicationContext().getSystemService("jobscheduler");
        }
        
        @Override
        void enqueueWork(final Intent intent) {
            this.mJobScheduler.enqueue(this.mJobInfo, new JobWorkItem(intent));
        }
    }
    
    abstract static class WorkEnqueuer
    {
        final ComponentName mComponentName;
        boolean mHasJobId;
        int mJobId;
        
        WorkEnqueuer(final Context context, final ComponentName mComponentName) {
            this.mComponentName = mComponentName;
        }
        
        abstract void enqueueWork(final Intent p0);
        
        void ensureJobId(final int n) {
            if (this.mHasJobId) {
                if (this.mJobId != n) {
                    throw new IllegalArgumentException("Given job ID " + n + " is different than previous " + this.mJobId);
                }
            }
            else {
                this.mHasJobId = true;
                this.mJobId = n;
            }
        }
        
        public void serviceProcessingFinished() {
        }
        
        public void serviceProcessingStarted() {
        }
        
        public void serviceStartReceived() {
        }
    }
}
