package com.gemvietnam.trafficgem.screen.map;

import com.gemvietnam.base.viper.Presenter;
import com.gemvietnam.base.viper.interfaces.ContainerView;
import com.gemvietnam.trafficgem.screen.main.DrawerToggleListener;

/**
 * The Main Presenter
 */
public class MapPresenter extends Presenter<MapContract.View, MapContract.Interactor>
    implements MapContract.Presenter {

  private DrawerToggleListener mDrawerToggleListener;
  private int key_type = 0;

  public MapPresenter(ContainerView containerView) {
    super(containerView);
  }

  @Override
  public MapContract.View onCreateView() {
    return MapFragment.getInstance();
  }

  @Override
  public void start() {
    // Start getting data here
//    mView.showProgress();
    mView.showMap();
  }

  @Override
  public MapContract.Interactor onCreateInteractor() {
    return new MapInteractor(this);
  }

  public MapPresenter setDrawerToggleListener(DrawerToggleListener drawerToggleListener, int key) {
    mDrawerToggleListener = drawerToggleListener;
    this.key_type = key;
    return this;
  }

  @Override
  public int getKey() {
    return key_type;
  }
}
