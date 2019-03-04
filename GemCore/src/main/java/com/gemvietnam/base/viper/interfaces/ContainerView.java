package com.gemvietnam.base.viper.interfaces;

import com.gemvietnam.base.viper.ViewFragment;


/**
 * Container view that place fragments on it
 * Created by neo on 9/15/2016.
 */
public interface ContainerView extends IView {
  ViewFragment onCreateFirstFragment();

  void addView(IView view);

  void pushView(IView view);

  void pushView(IView view, int frameId);

  void presentView(IView view);

  void back();
}
