package com.gemvietnam.trafficgem.screen.main;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.widget.FrameLayout;

import com.gemvietnam.base.ContainerActivity;
import com.gemvietnam.base.viper.ViewFragment;
import com.gemvietnam.trafficgem.R;
import com.gemvietnam.trafficgem.screen.leftmenu.LeftMenuPresenter;
import com.gemvietnam.trafficgem.screen.leftmenu.MenuItem;
import com.gemvietnam.trafficgem.screen.leftmenu.OnMenuItemClickedListener;
import com.gemvietnam.trafficgem.service.GPSTracker;
import com.gemvietnam.trafficgem.user.LoginActivity;
import com.gemvietnam.trafficgem.utils.ViewUtils;

import butterknife.Bind;

/**
 * Created by Quannv on 3/29/2017.
 */

public class MainActivity extends ContainerActivity implements
    OnMenuItemClickedListener, DrawerLayout.DrawerListener, DrawerToggleListener {
    public static boolean isLoginSuccess = false;

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

        //checkLoginStatus();

        checkPermission();
        // start location tracking when start app
        startGPSTracker();

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

    private void checkPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }
    }

    private void startGPSTracker() {
        Intent intent = new Intent(this, GPSTracker.class);
        startService(intent);
    }

    public void checkLoginStatus() {
        if (isLoginSuccess) {
            // do nothing
        } else {
            // open login activity
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
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
            isLoginSuccess = false;
            checkLoginStatus();
//      PrefWrapper.clearUser(this);
//      ActivityUtils.startActivity(this, LoginActivity.class);
            //finish();
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
