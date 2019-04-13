package com.gemvietnam.trafficgem.screen.main;


import android.content.Intent;
import android.location.Location;
import android.os.Build;
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
import com.gemvietnam.trafficgem.service.LocationTracker;
import com.gemvietnam.trafficgem.utils.AppUtils;
import com.gemvietnam.trafficgem.utils.ViewUtils;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;

import static com.gemvietnam.trafficgem.utils.AppUtils.START_SERVICE;
import static com.gemvietnam.trafficgem.utils.AppUtils.STOP_SERVICE;

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

        // creat chanel for notification (android O and above)
        AppUtils.createNotificationChanel(this);
//    super.initLayout();

        //checkPermission();

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
        // set more user's information
        //Bundle bundle = startIntent.getExtras();
        //bundle.putString("key", "value");
        startIntent.setAction(START_SERVICE);
        if (Build.VERSION.SDK_INT >= 26) {
            startForegroundService(startIntent);
        } else {
            startService(startIntent);
        }
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
            finish();
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

    public void sendReport(String myurl, int idMsg, Location location, String picturePath, Date date){
        DefaultHttpClient localDefaultHttpClient = new DefaultHttpClient();
        FileBody localFileBody = new FileBody(new File(picturePath));
        HttpPost localHttpPost = new HttpPost(myurl);
        DateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
        MultipartEntity localMultiPartEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);

        try {
            localMultiPartEntity.addPart("ID", new StringBody(Integer.toString(idMsg)));
            localMultiPartEntity.addPart("Latitude", new StringBody(Double.toString(location.getLatitude())));
            localMultiPartEntity.addPart("Longitude", new StringBody(Double.toString(location.getLongitude())));
            File file = new File(picturePath);
            localMultiPartEntity.addPart("Picture", new FileBody(file));
            localMultiPartEntity.addPart("Date", new StringBody(dateFormat.format(date)));

        localHttpPost.setEntity(localMultiPartEntity);
        HttpResponse response = localDefaultHttpClient.execute(localHttpPost);
        System.out.println("response code "+response.getStatusLine());

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (Exception e){
            Log.d("Exception", e.toString());
        }
    }
}
