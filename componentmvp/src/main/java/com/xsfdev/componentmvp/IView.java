package com.xsfdev.componentmvp;

import android.view.View;

/**
 * Created by xsf on 2018/10/18.
 * Description:
 */
public interface IView {
    /**
     * IView是组件的组成部分,用于提供视图的功能接口
     * 在实际的使用中需要将IView对应的实际视图添加到布局结构中
     * 通过这个接口可以获取到视图,返回类型为android.view.View
     *
     * @return
     */
    View getView();
}
