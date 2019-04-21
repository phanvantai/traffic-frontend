package com.gemvietnam.trafficgem.screen.main;


import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.widget.FrameLayout;

import com.gemvietnam.base.ContainerActivity;
import com.gemvietnam.base.viper.ViewFragment;
import com.gemvietnam.trafficgem.R;
import com.gemvietnam.trafficgem.screen.leftmenu.LeftMenuPresenter;
import com.gemvietnam.trafficgem.screen.leftmenu.MenuItem;
import com.gemvietnam.trafficgem.screen.leftmenu.OnMenuItemClickedListener;
import com.gemvietnam.trafficgem.service.LocationTracker;
import com.gemvietnam.trafficgem.utils.AppUtils;
import com.gemvietnam.trafficgem.utils.CustomToken;
import com.gemvietnam.trafficgem.utils.ViewUtils;
import com.orhanobut.hawk.Hawk;

import butterknife.BindView;

import static com.gemvietnam.trafficgem.utils.Constants.MY_TOKEN;
import static com.gemvietnam.trafficgem.utils.Constants.START_SERVICE;
import static com.gemvietnam.trafficgem.utils.Constants.STOP_SERVICE;

/**
 * Created by Quannv on 3/29/2017.
 */

public class MainActivity extends ContainerActivity implements
        OnMenuItemClickedListener,
        DrawerLayout.DrawerListener,
        DrawerToggleListener {

    @BindView(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;
    @BindView(R.id.left_drawer)
    FrameLayout mLeftDrawer;
    MenuItem current;
    private MainNavigator mMainNavigator;
    private Handler handler = new Handler();
    private Runnable mPendingRunnable;

    @Override
    public int getLayoutId() {
        return R.layout.fragment_main;
    }

    @Override
    public void initLayout() {
        AppUtils.createNotificationChanel(this);
//    super.initLayout();

        Hawk.init(getApplicationContext()).build();
        // start location tracking when start app
        startLocationTracker();

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

    /**
     * start service LocationTracker()
     */
    private void startLocationTracker() {
        Intent startIntent = new Intent(this, LocationTracker.class);
        startIntent.setAction(START_SERVICE);
        if (Build.VERSION.SDK_INT >= 26) {
            startForegroundService(startIntent);
        } else {
            startService(startIntent);
        }
    }

    /**
     * override method when click back on navigation bar
     */
    @Override
    public void onBackPressed() {
        // disable going back to the LoginActivity
        moveTaskToBack(true);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);
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

        // on click sign out
        if (menuItem.equals(MenuItem.SIGN_OUT)) {
//      PrefWrapper.clearUser(this);
//      ActivityUtils.startActivity(this, LoginActivity.class);
            CustomToken customToken = Hawk.get(MY_TOKEN);
            customToken.removeToken();
            Hawk.put(MY_TOKEN, customToken);
            finish();
        } else if (menuItem.equals(MenuItem.VIEW_EVENT)) {
            AppUtils.createDialog(this);
        } else {
            if (current == menuItem) {
                mDrawerLayout.closeDrawer(mLeftDrawer);
                return;
            } else {
                mPendingRunnable = new Runnable() {
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
        if (mPendingRunnable != null) {
            handler.post(mPendingRunnable);
            mPendingRunnable = null;
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

    @Override
    protected void onDestroy() {
        Intent stopIntent = new Intent(MainActivity.this, LocationTracker.class);
        stopIntent.setAction(STOP_SERVICE);
        startService(stopIntent);
        super.onDestroy();
    }
}
