package com.gemvietnam.base.viper.interfaces;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.Fragment;

/**
 * Base Presenter
 * Created by neo on 2/5/2016.
 */
public interface IPresenter<V extends IView, I extends IInteractor> {

  void start();

  V getView();

  I onCreateInteractor();

  V onCreateView();

  Fragment getFragment();

  void presentView();

  void pushView();

  void pushView(int frameId);

  Activity getViewContext();

  void registerEventBus();

  void unregisterEventBus();

  void back();
}
