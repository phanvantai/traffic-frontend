package com.gemvietnam.trafficgem.screen.waypoint;

import com.gemvietnam.base.viper.interfaces.IInteractor;
import com.gemvietnam.base.viper.interfaces.IPresenter;
import com.gemvietnam.base.viper.interfaces.PresentView;

/**
 * The WayPoint Contract
 */
interface WayPointContract {

  interface Interactor extends IInteractor<Presenter> {
  }

  interface View extends PresentView<Presenter> {
    void showMap();
  }

  interface Presenter extends IPresenter<View, Interactor> {
    int getKey();
  }
}



