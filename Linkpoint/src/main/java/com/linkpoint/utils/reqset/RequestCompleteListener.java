package com.linkpoint.utils.reqset;
import java.util.*;

public interface RequestCompleteListener<T> {
    void onRequestComplete(T t);
}
