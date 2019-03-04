package com.gemvietnam.trafficgem.screen.leftmenu;

import com.gemvietnam.base.viper.Presenter;
import com.gemvietnam.base.viper.interfaces.ContainerView;

/**
 * The LeftMenu Presenter
 */
public class LeftMenuPresenter extends Presenter<LeftMenuContract.View, LeftMenuContract.Interactor>
    implements LeftMenuContract.Presenter {

  private OnMenuItemClickedListener mItemClickedListener;

  public LeftMenuPresenter(ContainerView containerView) {
    super(containerView);
  }

  @Override
  public LeftMenuContract.View onCreateView() {
    return LeftMenuFragment.getInstance();
  }

  @Override
  public void start() {
    // Start getting data here
  }

  @Override
  public LeftMenuContract.Interactor onCreateInteractor() {
    return new LeftMenuInteractor(this);
  }

  @Override
  public void onMenuItemClicked(MenuItem value) {
    if (mItemClickedListener != null) {
      mItemClickedListener.onItemSelected(value);
    }
  }

  public LeftMenuPresenter setItemClickedListener(OnMenuItemClickedListener itemClickedListener) {
    mItemClickedListener = itemClickedListener;
    return this;
  }
}
