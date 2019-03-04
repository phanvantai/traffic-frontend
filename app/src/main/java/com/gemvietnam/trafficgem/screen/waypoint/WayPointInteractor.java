package com.gemvietnam.trafficgem.screen.waypoint;

import com.gemvietnam.base.viper.Interactor;

/**
 * The WayPoint interactor
 */
class WayPointInteractor extends Interactor<WayPointContract.Presenter>
    implements WayPointContract.Interactor {

  WayPointInteractor(WayPointContract.Presenter presenter) {
    super(presenter);
  }
}
