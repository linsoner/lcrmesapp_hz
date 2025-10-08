package com.dyg.siginprint.base.presenter;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;

/**
 * 作者：DC-DingYG on 2018-04-27 9:03
 * 邮箱：dingyg012655@126.com
 */
public abstract class BasePresenter<T> {
    protected Reference<T> viewRef;  //弱引用,防止内存泄漏

    public void attachView(T view) {
        viewRef = new WeakReference<T>(view);
    }

    public void detachView() {
        if (viewRef != null) {
            viewRef.clear();
            viewRef = null;
        }
    }
}