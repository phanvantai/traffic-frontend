package com.gemvietnam.trafficgem.screen.main;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.gemvietnam.Constants;
import com.gemvietnam.base.viper.Presenter;
import com.gemvietnam.trafficgem.R;
import com.gemvietnam.trafficgem.screen.leftmenu.MenuItem;
import com.gemvietnam.trafficgem.screen.map.MapPresenter;
import com.gemvietnam.trafficgem.screen.waypoint.WayPointPresenter;

import java.util.HashMap;
import java.util.Map;

/**
 * Main screen Navigator
 * Created by Quannv on 31/3/2017
 */

public class MainNavigator {
    private static final int FRAME_CONTAINER_ID = R.id.container_frame;

    private Map<MenuItem, Presenter> mMap = new HashMap<>();

    private MainActivity mMainActivity;
    private FragmentManager mFragmentManager;

    public MainNavigator(MainActivity mainActivity) {
        mMainActivity = mainActivity;
        mFragmentManager = mMainActivity.getSupportFragmentManager();
    }

    /**
     * Show fragment by id in list of this navigator ids
     */
    public void showFragment(MenuItem menuItem) {
        FragmentTransaction transaction = mFragmentManager.beginTransaction();

        transaction.replace(FRAME_CONTAINER_ID, createFragment(menuItem));
        transaction.commit();
    }

    /**
     * Create main fragment
     */
    private Fragment createFragment(MenuItem menuItem) {
        Presenter presenter;

        switch (menuItem) {
            case YOUR_LOCATION:
                presenter = new MapPresenter(mMainActivity).
                        setDrawerToggleListener(mMainActivity, Constants.LOCATION);
                break;
            case NORMAL_SEARCH:
                presenter = new WayPointPresenter(mMainActivity)
                        .setDrawerToggleListener(mMainActivity, Constants.NORMAL);
                break;
            case ADVANCE_SEARCH:
                presenter = new WayPointPresenter(mMainActivity).
                        setDrawerToggleListener(mMainActivity, Constants.ADVANCE);
                break;
            case TRAFFIC_STATE:
                presenter = new MapPresenter(mMainActivity).
                        setDrawerToggleListener(mMainActivity, Constants.TRAFFIC);
                break;
            case VIEW_STATE:
                presenter = new MapPresenter(mMainActivity).
                        setDrawerToggleListener(mMainActivity, Constants.TRAFFIC);
                break;
            case SIGN_OUT:
                presenter = new MapPresenter(mMainActivity).
                        setDrawerToggleListener(mMainActivity, Constants.SIGNOUT);
                break;
            default:
                presenter = new MapPresenter(mMainActivity).
                        setDrawerToggleListener(mMainActivity, Constants.LOCATION);
                break;
        }

        mMap.put(menuItem, presenter);
        return presenter.getFragment();
    }

    public Presenter getPresenter(MenuItem menuItem) {
        return mMap.get(menuItem);
    }
}
