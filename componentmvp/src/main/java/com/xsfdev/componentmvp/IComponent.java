package com.xsfdev.componentmvp;

import android.content.Context;
import android.view.ViewGroup;

/**
 * Created by xsf on 2018/10/18.
 * Description:
 * 组件统一接口
 * 这是抽象工厂的接口
 * 每个具体的组件会有自己的一个IComponent实现
 * 每个具体的实现能够生产一个IView和一个IPresenter组成的产品族
 */
public interface IComponent<V extends IView, P extends IPresenter> {
    /**
     * 对IComponent进行初始化
     * IComponent内部对应的IView和IPresenter在这里创建
     * IView和IPresenter之间的关联也在这里实现
     *
     * @param context
     * @param container
     * @param sid
     */
    void init(Context context, ViewGroup container, String sid);

    /**
     * 生产一个IView对象,用于外部使用
     *
     * @return
     */
    V getView();

    /**
     * 生产一个IPresenter对象,用户操作IView和Model之间的交互
     *
     * @return
     */
    P getPresenter();
}
