package com.xsfdev.componentmvp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.ViewTreeObserver;

/**
 * Created by xsf on 2018/10/18.
 * Description:
 */
public abstract class BaseFragment<P extends PresenterGroup> extends Fragment
        implements IGroupView, KeyEvent.Callback {

    protected P mTopPresenter;
    private View mRootView;

    private boolean mDestroyed = false;


    @Override
    public final void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (onActivityResultHappen(requestCode, resultCode, data)) {
            return;
        }
        PresenterGroup topPresenter = mTopPresenter;
        if (topPresenter == null) {
            return;
        }
        topPresenter.onDispatchActivityResult(requestCode, resultCode, data);
    }

    /**
     * 处理Fragment的onActivityResult
     *
     * @param requestCode
     * @param resultCode
     * @param data
     * @return true 已经处理了onActivityResult
     */
    public boolean onActivityResultHappen(int requestCode, int resultCode, Intent data) {
        return false;
    }

    protected abstract P onCreateTopPresenter();

    @Nullable
    @Override
    public final View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mDestroyed = false;
        mTopPresenter = onCreateTopPresenter();
        mTopPresenter.setIView(this);
        mTopPresenter.setHostFragment(this);
        mRootView = onCreateViewImpl(inflater, container, savedInstanceState);
        mRootView.getViewTreeObserver().addOnGlobalLayoutListener(mListener);
        mTopPresenter.dispatchPageCreate();
        return mRootView;
    }

    protected final void parentNoClipChildren(View topParent, View child) {
        if (child == null || !(topParent instanceof ViewGroup)) {
            return;
        }
        ViewParent parent = child.getParent();
        if (parent instanceof ViewGroup) {
            ((ViewGroup) parent).setClipChildren(false);
            /** 到了最顶层的view,不在递归处理*/
            if (topParent != parent) {
                parentNoClipChildren(topParent, (ViewGroup) parent);
            }
        }
    }

    @Nullable
    protected View onCreateViewImpl(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return null;
    }

    @Override
    public final void onStart() {
        super.onStart();
        mTopPresenter.dispatchPageStart();
        onStartImpl();
    }

    protected void onStartImpl() {
    }

    @Override
    public final void onResume() {
        super.onResume();
        mTopPresenter.dispatchPageResume();
        onResumeImpl();
    }

    protected void onResumeImpl() {
    }

    @Override
    public final void onPause() {
        super.onPause();
        mTopPresenter.dispatchPagePause();
        onPauseImpl();
    }

    protected void onPauseImpl() {
    }

    @Override
    public final void onStop() {
        super.onStop();
        mTopPresenter.dispatchPageStop();
        onStopImpl();
    }

    protected void onStopImpl() {
    }

    @Override
    public final void onDestroyView() {
        mDestroyed = true;
        super.onDestroyView();
        dismissCurrentDialog();
        mTopPresenter.dispatchPageDestroy();

        onDestroyViewImpl();

        mTopPresenter = null;
        mRootView = null;
    }

    protected boolean isDestroyed() {
        return mDestroyed;
    }

    private void dismissCurrentDialog() {
    }

    protected void onDestroyViewImpl() {
    }


    /**
     * 展示loading信息
     *
     * @param info
     */
    @Override
    public final void showDialog(String info) {
    }

    /**
     * 取消正在展示的loading
     */
    @Override
    public final void dismissDialog(int dialogId) {
    }

    /**
     * Dialog被点击
     *
     * @param dialogId 哪个dialog被点击
     * @param action   哪个按钮被点击
     */
    @Override
    public final void onDialogClicked(int dialogId, int action) {
        if (!isDestroyed() && !onDialogAction(dialogId, action) && mTopPresenter != null) {
            mTopPresenter.dispatchDialogAction(dialogId, action);
        }
    }

    /**
     * 处理Fragment中添加的dialog的回调
     *
     * @param dialogId
     * @param action
     * @return
     */
    protected boolean onDialogAction(int dialogId, int action) {
        return false;
    }

    /**
     * 展示一个Toast
     *
     * @param info
     */
    @Override
    public final void showToast(String info) {
    }


    private ViewTreeObserver.OnGlobalLayoutListener mListener = new ViewTreeObserver.OnGlobalLayoutListener() {
        @Override
        public void onGlobalLayout() {
            View rootView = mRootView;
            if (rootView == null) {
                return;
            }
            rootView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
            onFirstLayoutDone();
        }
    };

    protected void onFirstLayoutDone() {

    }

    @Override
    public void setBackVisible(boolean visible) {
    }

    @Override
    public void setTitle(String title) {
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return false;
    }

    @Override
    public boolean onKeyLongPress(int keyCode, KeyEvent event) {
        return false;
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (isDestroyed() || event.getKeyCode() != KeyEvent.KEYCODE_BACK) {
            return false;
        } else {
            return mTopPresenter.dispatchBackPressed(IPresenter.BackType.BackKey);
        }
    }

    @Override
    public boolean onKeyMultiple(int keyCode, int count, KeyEvent event) {
        return false;
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mTopPresenter.dispatchPageLowMemory();
    }


}