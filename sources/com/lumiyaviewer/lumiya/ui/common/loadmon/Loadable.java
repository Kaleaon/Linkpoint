// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.ui.common.loadmon;


public interface Loadable
{
    public static interface LoadableStatusListener
    {

        public abstract void onLoadableStatusChange(Loadable loadable, Status status);
    }

    public static final class Status extends Enum
    {

        private static final Status $VALUES[];
        public static final Status Error;
        public static final Status Idle;
        public static final Status Loaded;
        public static final Status Loading;

        public static Status valueOf(String s)
        {
            return (Status)Enum.valueOf(com/lumiyaviewer/lumiya/ui/common/loadmon/Loadable$Status, s);
        }

        public static Status[] values()
        {
            return $VALUES;
        }

        static 
        {
            Idle = new Status("Idle", 0);
            Loading = new Status("Loading", 1);
            Loaded = new Status("Loaded", 2);
            Error = new Status("Error", 3);
            $VALUES = (new Status[] {
                Idle, Loading, Loaded, Error
            });
        }

        private Status(String s, int i)
        {
            super(s, i);
        }
    }


    public abstract void addLoadableStatusListener(LoadableStatusListener loadablestatuslistener);

    public abstract Status getLoadableStatus();
}
