// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.ui.objpopup;

import android.content.Context;
import android.support.v4.view.ActionProvider;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

public class ObjectPopupsActionProvider extends ActionProvider
    implements android.view.View.OnClickListener
{
    public static interface ObjectPopupsClickListener
    {

        public abstract void onObjectPopupsClicked();
    }


    private int objectPopupCount;
    private ObjectPopupsClickListener objectPopupsClickListener;
    private TextView popupCountTextView;

    public ObjectPopupsActionProvider(Context context)
    {
        super(context);
        objectPopupCount = 0;
        popupCountTextView = null;
        objectPopupsClickListener = null;
    }

    public boolean isVisible()
    {
        boolean flag = false;
        if (objectPopupCount != 0)
        {
            flag = true;
        }
        return flag;
    }

    public void onClick(View view)
    {
        if (objectPopupsClickListener != null)
        {
            objectPopupsClickListener.onObjectPopupsClicked();
        }
    }

    public View onCreateActionView()
    {
        View view = LayoutInflater.from(getContext()).inflate(0x7f040079, null);
        popupCountTextView = (TextView)view.findViewById(0x7f10023e);
        if (popupCountTextView != null)
        {
            popupCountTextView.setText(Integer.toString(objectPopupCount));
        }
        view.setOnClickListener(this);
        return view;
    }

    public boolean overridesItemVisibility()
    {
        return true;
    }

    public void setObjectPopupCount(int i)
    {
        if (objectPopupCount != i)
        {
            objectPopupCount = i;
            if (popupCountTextView != null)
            {
                popupCountTextView.setText(Integer.toString(i));
            }
            refreshVisibility();
        }
    }

    public void setObjectPopupsClickListener(ObjectPopupsClickListener objectpopupsclicklistener)
    {
        objectPopupsClickListener = objectpopupsclicklistener;
    }
}
