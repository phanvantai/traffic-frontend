package com.gemvietnam.trafficgem.screen.map;

import com.gemvietnam.base.viper.interfaces.IInteractor;
import com.gemvietnam.base.viper.interfaces.IPresenter;
import com.gemvietnam.base.viper.interfaces.PresentView;

/**
 * The Main Contract
 */
interface MapContract {

  interface Interactor extends IInteractor<Presenter> {
  }

  interface View extends PresentView<Presenter> {
    void showMap();
  }

  interface Presenter extends IPresenter<View, Interactor> {
    int getKey();
  }
}



