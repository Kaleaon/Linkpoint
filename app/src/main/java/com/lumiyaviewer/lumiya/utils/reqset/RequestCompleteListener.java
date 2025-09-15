package com.lumiyaviewer.lumiya.utils.reqset;
import java.util.*;

public interface RequestCompleteListener<T> {
    void onRequestComplete(T t);
}
