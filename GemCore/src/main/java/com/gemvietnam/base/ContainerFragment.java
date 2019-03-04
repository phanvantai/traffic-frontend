package com.gemvietnam.base;

import com.gemvietnam.base.viper.interfaces.ContainerView;
import com.gemvietnam.base.viper.interfaces.IPresenter;
import com.gemvietnam.base.viper.interfaces.IView;
import com.gemvietnam.common.R;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

/**
 * Base Fragment
 * Created by neo on 3/22/2016.
 */
public abstract class ContainerFragment extends BaseFragment implements ContainerView {
  /**
   * Return layout resource id for activity
   * Override if you using other layout
   */
  @Override
  public int getLayoutId() {
    return R.layout.container;
  }

  @Override
  public void initLayout() {
  }

  public void addChildFragment(Fragment fragment) {
    addChildFragment(fragment, true);
  }

  public void addChildFragment(Fragment fragment, boolean addToBackStack) {
    addChildFragment(fragment, addToBackStack, fragment.getClass().getSimpleName());
  }

  public void addChildFragment(Fragment fragment, boolean addToBackStack, String tag) {
    addChildFragment(fragment, CoreDefault.FRAGMENT_CONTAINER_ID, addToBackStack, tag);
  }

  @Override
  protected void startPresent() {
    addChildFragment(onCreateFirstFragment(), false);
    mIsStarted = true;
  }

  @Override
  public void addView(IView view) {
    if (view instanceof BaseFragment) {
      addChildFragment(((BaseFragment) view)
//              .setAnimIn(R.anim.slide_none)
//              .setAnimOut(R.anim.slide_none)
          , true);
    }
  }

  @Override
  public void pushView(IView view) {
    if (view instanceof BaseFragment) {
      addChildFragment(((BaseFragment) view)
              .setAnimIn(R.anim.slide_right_in)
              .setAnimOut(R.anim.slide_right_out),
          true);
    }
  }

  @Override
  public void presentView(IView view) {
    if (view instanceof BaseFragment) {
      addChildFragment(((BaseFragment) view)
              .setAnimIn(R.anim.slide_bottom_in)
              .setAnimOut(R.anim.slide_bottom_out),
          true);
    }
  }

  @Override
  public void setPresenter(IPresenter presenter) {
    // Do nothing
  }

  @Override
  public void onActivityCreated(@Nullable Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    if (!mStartOnAnimationEnded && !mIsStarted) {
      startPresent();
    }
  }

  @Override
  protected boolean needTranslationAnimation() {
    return true;
  }

  @Override
  public BaseActivity getBaseActivity() {
    if (getActivity() instanceof BaseActivity) {
      return (BaseActivity) getActivity();
    }

    return null;
  }

  @Override
  public Activity getViewContext() {
    return getActivity();
  }

  @Override
  public void back() {
    FragmentManager manager = getFragmentManager();
    if (manager.getBackStackEntryCount() > 0) {
      manager.popBackStack();
    } else {
      getActivity().finish();
    }
  }
}
