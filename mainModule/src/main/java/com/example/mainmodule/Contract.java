package com.example.mainmodule;

import com.example.mylibrary.base.mvp.v.IView;


public class Contract {
    interface View extends IView {
        void onSuccess();

    }

    interface Presenter {
        void getData();
    }

}
