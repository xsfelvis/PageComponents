package com.xsfdev.componentmvp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;

/**
 * Created by xsf on 2018/10/18.
 * Description:
 */
public abstract class IPresenter<V extends IView> {
    protected Context mContext;
    protected PresenterGroup mParent;
    protected V mView;

    public IPresenter(Context context) {
        mContext = context;
    }

    protected void setParent(PresenterGroup parent) {
        mParent = parent;
    }

    protected PresenterGroup getParent() {
        return mParent;
    }

    /**
     * 页面跳转到指定的Fragment
     *
     * @param fragment
     * @param options
     */
    protected void forward(Fragment fragment, Bundle options) {
        if (null != mParent) {
            mParent.forward(fragment, options);
        }
    }

    /**
     * 页面跳转到指定的Fragment
     *
     * @param clazz
     * @param options
     */
    protected void forward(Class<? extends Fragment> clazz, Bundle options) {
        if (null != mParent) {
            mParent.forward(clazz, options);
        }
    }

    /**
     * 回到上一个页面
     */
    protected void goBack() {
        goBack(null);
    }

    /**
     * 回到上一个页面
     *
     * @param args 参数信息
     */
    protected void goBack(Bundle args) {
        if (null != mParent) {
            mParent.goBack(args);
        }
    }


    /**
     * 获取宿主Fragment
     *
     * @return 宿主Fragment, 可能为空.不建议从这个Fragment中取出Activity使用
     */
    protected Fragment getHost() {
        if (null != mParent) {
            return mParent.getHost();
        }

        return null;
    }


    public boolean isAdded() {
        if (mParent == null) {
            return false;
        }
        return mParent.isAdded();
    }

    /** ---------------------------------启动Activity的功能开始----------------------------------------*/
    /**
     * 启动一个Activity
     *
     * @param intent
     */
    protected void startActivity(Intent intent) {
        startActivityForResult(intent, -1, null);
    }

    /**
     * 启动一个Activity
     *
     * @param intent
     * @param options
     */
    protected void startActivity(Intent intent, Bundle options) {
        startActivityForResult(intent, -1, options);
    }

    /**
     * 启动一个Activity并等待结果
     *
     * @param intent
     * @param requestCode
     */
    protected void startActivityForResult(Intent intent, int requestCode) {
        startActivityForResult(intent, requestCode, null);
    }

    /**
     * 启动一个Activity并等待结果
     *
     * @param intent
     * @param requestCode
     * @param options
     */
    protected void startActivityForResult(Intent intent, int requestCode, Bundle options) {
//        if (mParent != null) {
//            mParent.startActivityForChild(intent, requestCode, options, this);
//        }
    }

    /**
     * 处理onActivityResult
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    }
    /** ------------------------------Presenter与IView的绑定功能开始-----------------------------------*/
    /**
     * 将View绑定到Presenter中, 通常这个操作需要在Component初始化的时候完成。
     *
     * @param iView
     */
    public void setIView(V iView) {
        mView = iView;
    }

    /** ------------------------------Presenter与IView的绑定功能结束-----------------------------------*/

    /**
     * ---------------------------------页面生命周期回调功能开始---------------------------------------
     */
    protected void onCreatePage(Bundle arguments) {
    }

    protected void onStartPage() {
    }

    protected void onResumePage() {
    }

    protected void onPausePage() {
    }

    protected void onStopPage() {
    }

    protected void onDestroyPage() {
    }

    protected void onLowMemory() {
    }

    /**
     * ---------------------------------页面生命周期回调功能结束---------------------------------------
     */

    /**
     * 显示一个Dialog, DialogInfo用于填充dialog中的内容
     *
     * @param info
     */
    protected void showDialog(String info) {
        if (mParent != null) {
            mParent.showDialogForChild(info, this);
        }
    }

    /**
     * dismiss一个Dialog
     *
     * @param dialogId
     */
    protected void dismissDialog(int dialogId) {
        if (mParent != null) {
            mParent.dismissDialogForChild(dialogId, this);
        }
    }

    /**
     * dialog按钮被点击或者被取消之后回调
     *
     * @param dialogId
     * @param action
     */
    protected void onDialogAction(int dialogId, int action) {

    }

    protected void showToast(String info) {
        if (mParent != null) {
            mParent.showToast(info);
        }
    }

    /**
     * 在Presenter中直接使用Fragment启动Activity时
     * 先用这个接口重新生成requestCode
     *
     * @param requestCode
     * @return
     */
    protected int requestCodeForHost(int requestCode) {
        if (mParent != null) {
            return mParent.hostRequestCodeForChild(this, requestCode);
        } else {
            return requestCode;
        }
    }

    /**
     * 点击左上角返回按钮和Back键时回调
     */
    protected boolean onBackPressed(BackType backType) {
        return false;
    }

    /**
     * ------------------------------------事件分发功能开始-------------------------------------------
     */

    public enum BackType {
        TopLeft, BackKey
    }
}
