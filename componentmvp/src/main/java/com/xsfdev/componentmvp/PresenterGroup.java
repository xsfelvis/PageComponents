package com.xsfdev.componentmvp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;

import java.util.LinkedList;
import java.util.List;

import static com.xsfdev.Constants.BITS_OF_2_BYTES;

/**
 * Created by xsf on 2018/10/18.
 * Description:
 */
public abstract class PresenterGroup<V extends IGroupView> extends IPresenter<V> {
    private static final int REQUEST_CODE_MASK = 0x0000FF00;
    private static final int REQUEST_CODE_MAX = 0x000000FF;

    private static final int DIALOG_ID_MASK = 0xFFFF0000;
    private static final int DIALOG_ID_MAX = 0x0000FFFF;

    protected final Handler mUIHandler;
    protected Bundle mArguments;
    private PageState mCurrentState = PageState.NONE;
    private Fragment mHostFragment;

    public PresenterGroup(Context context, Bundle arguments) {
        super(context);
        mUIHandler = new Handler(Looper.getMainLooper());
        mArguments = arguments;
    }


    /**
     * 子的Presenter集合
     */
    private final List<IPresenter> mChildren = new LinkedList<>();
    private final IndexAllocator<IPresenter> mChildIndexes = new IndexAllocator<>();

    /** ---------------------------------Presenter层级管理功能开始----------------------------------------*/
    /**
     * 向当前Presenter中加入一个子的Presenter
     *
     * @param child
     * @param arguments 组件Presenter的初始化参数
     * @return
     */
    public final boolean addChild(IPresenter child, Bundle arguments) {
        if (!runOnUIThread()) {
            throw new RuntimeException("添加child必须在UI线程!");
        }
        if (child == null) {
            throw new IllegalArgumentException("无法添加一个null的Presenter到父Presenter中!");
        }
        if (child.getParent() != null) {
            throw new IllegalArgumentException(child + "已经添加到" + child.mParent + "中!");
        }
        if (mCurrentState == PageState.DESTROYED) {
            throw new IllegalStateException("页面已经销毁,不能够再往里边添加组件!!!");
        }
        child.setParent(this);
        mChildren.add(child);
        dispatchLifeCycleWhenAdd(child, arguments);
        return true;
    }

    /**
     * 向当前Presenter中加入一个子的Presenter, 默认使用页面的Arguments作为参数
     *
     * @param child
     * @return
     */
    public final boolean addChild(IPresenter child) {
        return addChild(child, mArguments);
    }

    /**
     * 从当前Presenter中移除一个子的Presenter
     *
     * @param child
     * @return
     */
    public final boolean removeChild(IPresenter child) {
        if (!runOnUIThread()) {
            throw new RuntimeException("移除child必须在UI线程执行!");
        }
        if (mCurrentState == PageState.DESTROYED) {
            throw new IllegalStateException("页面已经销毁,已经没有任何组件了!!!");
        }
        if (child != null && child.getParent() != null) {
            boolean success = mChildren.remove(child);
            if (success) {
                mChildIndexes.removeIndex(child);
                dispatchLiftCycleWhenRemove(child);
            }
            child.setParent(null);
            return success;
        } else {
            return false;
        }
    }

    /**
     * ---------------------------------Presenter层级管理功能开始----------------------------------------
     */
    @Override
    protected final void startActivityForResult(Intent intent, int requestCode, Bundle options) {
        if (mView == null) {
            return;
        }
        mView.startActivityForResult(intent, requestCode, options);
    }

    /**
     * 启动一个Activity并等待结果
     *
     * @param intent
     * @param requestCode
     * @param options
     */
    final void startActivityForChild(Intent intent, int requestCode, Bundle options, IPresenter child) {
        if (intent == null || child == null) {
            return;
        }
        if (mView == null) {
            return;
        }
        /** 如果Parent为空,是顶级容器,直接请求,不需要其它的信息*/
        if (requestCode == -1) {
            startActivityForResult(intent, requestCode, options);
            return;
        }
        /** 为子Presenter生成一个新的requestCode*/
        requestCode = hostRequestCodeForChild(child, requestCode);
        startActivityForResult(intent, requestCode, options);
    }

    @Override
    protected final int requestCodeForHost(int requestCode) {
        return requestCode;
    }

    /**
     * 为子Presenter创建一个新的requestCode
     *
     * @param child
     * @param requestCode
     * @return
     */
    int hostRequestCodeForChild(IPresenter child, int requestCode) {
        /** 如果requestCode是一个无效值,不做处理*/
        if (requestCode == -1) {
            return requestCode;
        }
        if ((requestCode & REQUEST_CODE_MASK) != 0) {
            throw new RuntimeException("request code 必须在0到" + REQUEST_CODE_MAX + "之间");
        }
        /** 获取自身在父容器中的索引信息*/
        int index = mChildIndexes.allocateIndex(child, 1, REQUEST_CODE_MAX);
        if (index <= 0) {
            throw new RuntimeException("子Presenter已经超过容量,请审核自己的代码!");
        }
        requestCode = (index << 8) | requestCode;
        return requestCode;
    }

    /**
     * 分发OnActivityResult
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    final void onDispatchActivityResult(int requestCode, int resultCode, Intent data) {
        /** 如果为0,表示当前Presenter*/
        if ((requestCode & REQUEST_CODE_MASK) == 0) {
            onActivityResult(requestCode, resultCode, data);
            return;
        }

        /** 获得Presenter的偏移信息,如果偏移信息不符合逻辑,不再处理*/
        int index = (requestCode & REQUEST_CODE_MASK) >> 8;
        IPresenter presenter = mChildIndexes.findByIndex(index);
        if (presenter == null) {
            return;
        }
        requestCode = requestCode & (~REQUEST_CODE_MASK);
        presenter.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * ---------------------------------启动Activity的功能结束----------------------------------------
     */

    /**
     * 分发页面创建的生命周期回调
     */
    final void dispatchPageCreate() {
        onCreatePage(mArguments);

        mCurrentState = PageState.CREATED;
    }

    /**
     * 分发页面Start的声明周期回调
     */
    final void dispatchPageStart() {
        onStartPage();

        int size = mChildren != null ? mChildren.size() : 0;
        for (int i = 0; i < size; i++) {
            mChildren.get(i).onStartPage();
        }

        mCurrentState = PageState.STARTED;
    }

    /**
     * 分发页面Resume的生命周期回调
     */
    final void dispatchPageResume() {
        onResumePage();

        int size = mChildren != null ? mChildren.size() : 0;
        for (int i = 0; i < size; i++) {
            mChildren.get(i).onResumePage();
        }

        mCurrentState = PageState.RESUMED;
    }

    /**
     * 分发页面Pause的生命周期回调
     */
    final void dispatchPagePause() {
        onPausePage();

        int size = mChildren != null ? mChildren.size() : 0;
        for (int i = 0; i < size; i++) {
            mChildren.get(i).onPausePage();
        }

        mCurrentState = PageState.PAUSED;
    }

    /**
     * 分发页面Stop的生命周期回调
     */
    final void dispatchPageStop() {
        onStopPage();

        int size = mChildren != null ? mChildren.size() : 0;
        for (int i = 0; i < size; i++) {
            mChildren.get(i).onStopPage();
        }
        mCurrentState = PageState.STOPPED;
    }

    /**
     * 分发页面销毁的生命周期回调
     */
    final void dispatchPageDestroy() {
        /** 清理掉所有没有处理的信息*/
        mUIHandler.removeCallbacksAndMessages(null);
        onDestroyPage();

        int size = mChildren != null ? mChildren.size() : 0;
        for (int i = size - 1; i >= 0; i--) {
            removeChild(mChildren.get(i));
        }

        mCurrentState = PageState.DESTROYED;
    }

    /**
     * 分发页面LowMemory的生命周期回调
     */
    final void dispatchPageLowMemory() {
        onLowMemory();
        int size = mChildren != null ? mChildren.size() : 0;
        for (int i = 0; i < size; i++) {
            mChildren.get(i).onLowMemory();
        }
    }

    protected boolean runOnUIThread() {
        return Looper.myLooper() == Looper.getMainLooper();
    }

    @Override
    protected final void showDialog(String info) {
        mView.showDialog(info);
    }

    final void showDialogForChild(String info, IPresenter child) {
//        int newDialogId = checkAndBuildNewDialogId(info.getDialogId(), child);
//        info.setDialogId(newDialogId);
//        this.showDialog(info);
    }

    @Override
    protected final void dismissDialog(int dialogId) {
        mView.dismissDialog(dialogId);
    }

    final void dismissDialogForChild(int dialogId, IPresenter child) {
        this.dismissDialog(checkAndBuildNewDialogId(dialogId, child));
    }

    /**
     * 分发dialog的点击事件
     *
     * @param dialogId
     * @param action
     */
    final void dispatchDialogAction(int dialogId, int action) {
        int index = (dialogId & DIALOG_ID_MASK) >> BITS_OF_2_BYTES;
        if (index == 0) {
            onDialogAction(dialogId, action);
        } else {
            IPresenter child = mChildIndexes.findByIndex(index);
            int newDialogId = dialogId & (~DIALOG_ID_MASK);
            if (child != null) {
                child.onDialogAction(newDialogId, action);
            }
        }
    }

    @Override
    protected final void showToast(String info) {
        mView.showToast(info);
    }

    private int checkAndBuildNewDialogId(int dialogId, IPresenter child) {
        if ((dialogId & DIALOG_ID_MASK) != 0) {
            throw new RuntimeException("Dialog id必须在0到" + DIALOG_ID_MAX + "之间");
        }
        /** 获取自身在父容器中的索引信息*/
        int index = mChildIndexes.allocateIndex(child, 1, DIALOG_ID_MAX);
        if (index <= 0) {
            throw new RuntimeException("在父容器中查找不到自身!");
        }

        return (index << BITS_OF_2_BYTES) | dialogId;
    }


    public boolean dispatchBackPressed(BackType backType) {
        int size = mChildren != null ? mChildren.size() : 0;
        for (int i = size - 1; i >= 0; i--) {
            IPresenter child = mChildren.get(i);
            if (child == null) {
                continue;
            }
            boolean handled = child.onBackPressed(backType);
            if (handled) {
                return handled;
            }
        }
        return onBackPressed(backType);
    }

    private void updateTitle(final String title) {
        if (runOnUIThread()) {
            mView.setTitle(title);
        } else {
            mUIHandler.post(new Runnable() {
                @Override
                public void run() {
                    mView.setTitle(title);
                }
            });
        }
    }

//    private OnEventListener<String> mTitleListener = new OnEventListener<String>() {
//        @Override
//        public void onEvent(String category, String event) {
//            if (!TextUtils.equals(EventKeys.Common.UPDATE_TITLE, category)) {
//                return;
//            }
//            updateTitle(event);
//        }
//    };

    /**
     * 页面跳转到指定的Fragment
     *
     * @param fragment
     * @param options
     */
    @Override
    protected void forward(Fragment fragment, Bundle options) {
    }

    /**
     * 回到上一个页面
     *
     * @param args 参数信息
     */
    @Override
    protected void goBack(Bundle args) {

    }

    /**
     * 获取宿主Fragment
     *
     * @return 宿主Fragment, 可能为空.不建议从这个Fragment中取出Activity使用
     */
    @Override
    protected Fragment getHost() {
        return mHostFragment;
    }

    public void setHostFragment(Fragment host) {
        mHostFragment = host;
    }

    @Override
    public boolean isAdded() {
        if (mHostFragment == null) {
            return false;
        }
        return mHostFragment.isAdded();
    }

    /**
     * Return the {@link FragmentActivity} this fragment is currently associated with.
     * May return {@code null} if the fragment is associated with a {@link Context}
     * instead.
     */
    final public FragmentActivity getActivity() {
        return mHostFragment == null ? null : (FragmentActivity) mHostFragment.getActivity();
    }

    private enum PageState {
        NONE, CREATED, STARTED, RESUMED, PAUSED, STOPPED, DESTROYED
    }

    private void dispatchLifeCycleWhenAdd(IPresenter child, Bundle arguments) {
        switch (mCurrentState) {
            case NONE:
            case CREATED: {
                child.onCreatePage(arguments);
                break;
            }
            case STARTED: {
                child.onCreatePage(arguments);
                child.onStartPage();
                break;
            }
            case RESUMED: {
                child.onCreatePage(arguments);
                child.onStartPage();
                child.onResumePage();
                break;
            }
            case PAUSED: {
                child.onCreatePage(arguments);
                child.onStartPage();
                child.onResumePage();
                child.onPausePage();
                break;
            }
            case STOPPED: {
                child.onCreatePage(arguments);
                child.onStartPage();
                child.onResumePage();
                child.onPausePage();
                child.onStopPage();
                break;
            }
        }
    }

    private void dispatchLiftCycleWhenRemove(IPresenter child) {
        switch (mCurrentState) {
            case NONE:
            case CREATED: {
                child.onStartPage();
                child.onResumePage();
                child.onPausePage();
                child.onStopPage();
                child.onDestroyPage();
                break;
            }
            case STARTED: {
                child.onResumePage();
                child.onPausePage();
                child.onStopPage();
                child.onDestroyPage();
                break;
            }
            case RESUMED: {
                child.onPausePage();
                child.onStopPage();
                child.onDestroyPage();
                break;
            }
            case PAUSED: {
                child.onStopPage();
                child.onDestroyPage();
                break;
            }
            case STOPPED: {
                child.onDestroyPage();
                break;
            }
        }
    }
}
