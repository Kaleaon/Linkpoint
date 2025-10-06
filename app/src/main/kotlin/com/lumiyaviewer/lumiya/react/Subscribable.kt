package com.lumiyaviewer.lumiya.react

import com.lumiyaviewer.lumiya.react.Subscription.OnData
import com.lumiyaviewer.lumiya.react.Subscription.OnError
import java.util.concurrent.Executor

interface Subscribable<K, T> {
    fun subscribe(key: K, onData: OnData<T>): Subscription<K, T>
    
    fun subscribe(key: K, onData: OnData<T>, onError: OnError?): Subscription<K, T>
    
    fun subscribe(key: K, executor: Executor?, onData: OnData<T>): Subscription<K, T>
    
    fun subscribe(key: K, executor: Executor?, onData: OnData<T>, onError: OnError?): Subscription<K, T>
}
