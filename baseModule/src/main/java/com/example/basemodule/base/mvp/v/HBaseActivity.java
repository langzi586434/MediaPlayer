package com.example.basemodule.base.mvp.v;

import com.example.basemodule.base.BaseActivity;
import com.example.basemodule.base.mvp.p.BasePresenter;


public abstract class HBaseActivity<P extends BasePresenter> extends BaseActivity implements IView {

    protected P mPresenter;

    @Override
    protected void initData() {
        initPresenter();
    }

    protected abstract void initPresenter();

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != mPresenter) {
            mPresenter = null;
        }
    }
}

