package com.gemvietnam.trafficgem.screen.waypoint;

import com.gemvietnam.base.viper.Presenter;
import com.gemvietnam.base.viper.interfaces.ContainerView;
import com.gemvietnam.trafficgem.screen.main.DrawerToggleListener;
import com.gemvietnam.trafficgem.screen.main.MainActivity;

/**
 * The WayPoint Presenter
 */
public class WayPointPresenter extends Presenter<WayPointContract.View, WayPointContract.Interactor>
    implements WayPointContract.Presenter {

  private DrawerToggleListener mDrawerToggleListener;
  private int key_type;

  public WayPointPresenter(ContainerView containerView) {
    super(containerView);
  }

  @Override
  public WayPointContract.View onCreateView() {
    return WayPointFragment.getInstance();
  }

  @Override
  public void start() {
    // Start getting data here
    mView.showMap();
  }

  @Override
  public WayPointContract.Interactor onCreateInteractor() {
    return new WayPointInteractor(this);
  }

  public WayPointPresenter setDrawerToggleListener(DrawerToggleListener drawerToggleListener, int key) {
    mDrawerToggleListener = drawerToggleListener;
    this.key_type = key;
    return this;
  }

  @Override
  public int getKey() {
    return key_type;
  }
}
