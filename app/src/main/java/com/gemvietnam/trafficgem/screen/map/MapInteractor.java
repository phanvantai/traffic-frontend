package com.gemvietnam.trafficgem.screen.map;

import com.gemvietnam.base.viper.Interactor;

/**
 * The Main interactor
 */
class MapInteractor extends Interactor<MapContract.Presenter>
    implements MapContract.Interactor {

  MapInteractor(MapContract.Presenter presenter) {
    super(presenter);
  }
}
