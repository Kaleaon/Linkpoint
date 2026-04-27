package android.support.v7.view.menu;

import android.support.v7.view.menu.MenuPresenter;

interface MenuHelper {
    void dismiss();

    void setPresenterCallback(MenuPresenter.Callback callback);
}
