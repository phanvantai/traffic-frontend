package com.gemvietnam.base.viper.interfaces;

/**
 * Views can present on a {@link ContainerView}
 * Created by neo on 9/15/2016.
 */
public interface PresentView<P extends IPresenter> extends IView<P> {
  void showProgress();

  void hideProgress();

  void showAlertDialog(String message);

  void onRequestError(String errorCode, String errorMessage);

  void onNetworkError(boolean shouldShowPopup);

  void onRequestSuccess();
}
