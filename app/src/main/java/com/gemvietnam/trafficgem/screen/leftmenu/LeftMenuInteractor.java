package com.gemvietnam.trafficgem.screen.leftmenu;

import com.gemvietnam.base.viper.Interactor;

/**
 * The LeftMenu interactor
 */
class LeftMenuInteractor extends Interactor<LeftMenuContract.Presenter>
    implements LeftMenuContract.Interactor {

  LeftMenuInteractor(LeftMenuContract.Presenter presenter) {
    super(presenter);
  }
}
