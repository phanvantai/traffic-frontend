package com.gemvietnam.trafficgem.screen.main;

import android.os.Handler;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

import com.gemvietnam.base.ContainerActivity;
import com.gemvietnam.base.viper.ViewFragment;
import com.gemvietnam.trafficgem.R;
import com.gemvietnam.trafficgem.screen.leftmenu.LeftMenuPresenter;
import com.gemvietnam.trafficgem.screen.leftmenu.MenuItem;
import com.gemvietnam.trafficgem.screen.leftmenu.OnMenuItemClickedListener;
import com.gemvietnam.trafficgem.utils.ViewUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.logging.Logger;

import butterknife.Bind;

/**
 * Created by Quannv on 3/29/2017.
 */

public class MainActivity extends ContainerActivity implements
    OnMenuItemClickedListener, DrawerLayout.DrawerListener, DrawerToggleListener {

  @Bind(R.id.drawer_layout)
  DrawerLayout mDrawerLayout;
  @Bind(R.id.left_drawer)
  FrameLayout mLeftDrawer;
  MenuItem current;
  private MainNavigator mMainNavigator;
  private Handler handler = new Handler();
  private Runnable mPendingRunable;


  @Override
  public int getLayoutId() {
    return R.layout.fragment_main;
  }

  @Override
  public void initLayout() {
//    super.initLayout();

    mDrawerLayout.addDrawerListener(this);

    // Add menu
    getSupportFragmentManager().beginTransaction()
        .add(R.id.left_drawer,
            new LeftMenuPresenter(null)
                .setItemClickedListener(this)
                .getFragment())
        .commit();

    // Add first screen in main
    mMainNavigator = new MainNavigator(this);
    mMainNavigator.showFragment(MenuItem.YOUR_LOCATION);
  }

  @Override
  public ViewFragment onCreateFirstFragment() {
    return null;
  }

  @Override
  public void onItemSelected(final MenuItem menuItem) {
    if (mDrawerLayout.isDrawerOpen(mLeftDrawer)) {
      mDrawerLayout.closeDrawer(mLeftDrawer);
    }
    /**
     * on sign out
     */
    if (menuItem.equals(MenuItem.SIGN_OUT)) {
//      PrefWrapper.clearUser(this);
//      ActivityUtils.startActivity(this, LoginActivity.class);
      finish();
    } else {
      if (current == menuItem) {
        mDrawerLayout.closeDrawer(mLeftDrawer);
        return;
      } else {
        mPendingRunable = new Runnable() {
          @Override
          public void run() {
            current = menuItem;
            mMainNavigator.showFragment(menuItem);
          }
        };
      }
    }
  }

  @Override
  public void onDrawerSlide(View drawerView, float slideOffset) {
    ViewUtils.hideKeyBoard(MainActivity.this);
  }

  @Override
  public void onDrawerOpened(View drawerView) {

  }

  @Override
  public void onDrawerClosed(View drawerView) {
    if (mPendingRunable != null) {
      handler.post(mPendingRunable);
      mPendingRunable = null;
    }
  }

  @Override
  public void onDrawerStateChanged(int newState) {

  }

  @Override
  public void onToggleDrawer() {
    if (mDrawerLayout.isDrawerOpen(mLeftDrawer)) {
      mDrawerLayout.closeDrawer(mLeftDrawer);
    } else {
      mDrawerLayout.openDrawer(mLeftDrawer);
    }
  }

}
