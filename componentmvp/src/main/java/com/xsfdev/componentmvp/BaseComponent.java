package com.xsfdev.componentmvp;

import android.content.Context;
import android.view.ViewGroup;

/**
 * Created by xsf on 2018/10/18.
 * Description:
 */
public abstract class BaseComponent<V extends IView, P extends IPresenter> implements
        IComponent {

    private V mView;
    private P mPresenter;

    /**
     * 初始化组件的各个部分(View 和 Presenter)，在使用组件时必须先调用此方法
     *
     * @param context
     * @param container
     * @param productId
     */
    @Override
    public void init(Context context, ViewGroup container, String productId) {
        mView = onCreateView(context, container, productId);
        mPresenter = onCreatePresenter(context, productId);
        bind(mView, mPresenter);
        // 将View绑定到Presenter
        if (mPresenter != null && mView != null) {
            mPresenter.setIView(mView);
        }
    }

    /**
     * 为view设置各种监听,涉及到业务逻辑的调用Presenter的方法处理
     *
     * @param view
     * @param presenter
     */
    protected abstract void bind(V view, P presenter);

    protected abstract V onCreateView(Context context, ViewGroup container, String sid);

    protected abstract P onCreatePresenter(Context context, String sid);

    @Override
    public V getView() {
        return mView;
    }

    @Override
    public P getPresenter() {
        return mPresenter;
    }

}
