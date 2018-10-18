package com.xsfdev.componentmvp;

import android.content.Intent;
import android.os.Bundle;

/**
 * Created by xsf on 2018/10/18.
 * Description:
 */
public interface IGroupView extends IView {
    /**
     * 显示一个dialog
     *
     * @param info
     */
    void showDialog(String info);

    /**
     * dismiss一个dialog
     *
     * @param dialogId
     */
    void dismissDialog(int dialogId);

    /**
     * dialog按钮被点击后回调
     *
     * @param dialogId
     * @param action
     */
    void onDialogClicked(int dialogId, int action);

    /**
     * 显示一个Toast
     *
     * @param info
     */
    void showToast(String info);

    /**
     * 设置back按钮的可见性
     *
     * @param visible
     */
    void setBackVisible(boolean visible);

    /**
     * 设置title信息
     *
     * @param title
     */
    void setTitle(String title);

    /**
     * 派发activity
     *
     * @param intent
     * @param requestCode
     * @param options
     */
    void startActivityForResult(Intent intent, int requestCode, Bundle options);

}
