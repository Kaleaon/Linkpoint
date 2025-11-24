package com.lumiyaviewer.lumiya.react

class Subscription {
    companion object {
        fun <T> OnData(context: Any?, callback: (T) -> Unit): OnDataCallback<T> {
            return object : OnDataCallback<T> {
                override fun onData(data: T) {
                    callback(data)
                }
            }
        }
    }
    
    interface OnDataCallback<T> {
        fun onData(data: T)
    }
}
