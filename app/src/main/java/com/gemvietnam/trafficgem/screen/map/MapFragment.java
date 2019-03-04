package com.gemvietnam.trafficgem.screen.map;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.gemvietnam.Constants;
import com.gemvietnam.base.viper.ViewFragment;
import com.gemvietnam.trafficgem.R;
import com.gemvietnam.trafficgem.library.MySupportMapFragment;
import com.gemvietnam.trafficgem.library.Point;
import com.gemvietnam.trafficgem.screen.leftmenu.MenuItem;
import com.gemvietnam.trafficgem.screen.leftmenu.OnMenuItemClickedListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.Polyline;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import butterknife.Bind;
import butterknife.OnClick;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static android.content.Context.LOCATION_SERVICE;

/**
 * The Main Fragment
 */
public class MapFragment extends ViewFragment<MapContract.Presenter> implements MapContract.View,
    OnMapReadyCallback, GoogleApiClient.OnConnectionFailedListener,
    GoogleApiClient.ConnectionCallbacks, OnMenuItemClickedListener {

  @Bind(R.id.activity_main_location_search_tv)
  TextView mLocationSearchTv;
  @Bind(R.id.activity_main_location_cancel_img)
  ImageView mSearchCancelImg;
  @Bind(R.id.activity_main_location_search_cv)
  CardView mLocationSearchCv;
  @Bind(R.id.activity_main_gridview)
  GridView mGridview;

  private GoogleMap mMap;
  private ProgressDialog mprogress;
  private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 1;
  private static final String TAG = "TAG: ";
  private static final int LOCATION_CODE_AUTOCOMPLETE = 10;
  private static final int ORIGIN_CODE_AUTOCOMPLETE = 11;
  private static final int DESTINATION_CODE_AUTOCOMPLETE = 12;
  private static int PLACE_PICKER_REQUEST = 2;
  private List<Polyline> polylines;

  private Point[][] points = new Point[23][56];
  private String[][] colors = new String[23][56];
  public static boolean mMapIsTouched = false;
  private double delLat = 0.0089573;
  private double delLon = 0.0088355;

  public static MapFragment getInstance() {
    return new MapFragment();
  }

  @Override
  protected int getLayoutId() {
    return R.layout.fragment_map;
  }

  @Override
  public void initLayout() {
    super.initLayout();

    if (Build.VERSION.SDK_INT >= 23) {
      checkPermissions();
    }
    mLocationSearchCv.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        openAutoCompleteActivity();
      }
    });
  }

  private void signout() {

  }

  private void setupListColor() {
    String list1 =  new String("\"#808080\",\"#FF0000\",\"#FF0000\",\"#808080\",\"#808080\",\"#808080\",\"#00FF00\",\"#00FF00\",\"#00FF00\",\"#808080\",\"#FFFF00\",\"#808080\",\"#00FF00\",\"#808080\",\"#FFFF00\",\"#808080\",\"#808080\",\"#FFFF00\",\"#00FF00\",\"#808080\",\"#808080\",\"#808080\",\"#808080\",\"#FFFF00\",\"#808080\",\"#00FFFF\",\"#FFFF00\",\"#00FF00\",\"#808080\",\"#808080\",\"#00FF00\",\"#808080\",\"#00FFFF\",\"#FFFF00\",\"#00FF00\",\"#808080\",\"#808080\",\"#808080\",\"#FFFF00\",\"#808080\",\"#808080\",\"#00FFFF\",\"#808080\",\"#00FFFF\",\"#00FF00\",\"#808080\",\"#FFFF00\",\"#FF0000\",\"#808080\",\"#00FF00\",\"#808080\",\"#808080\",\"#808080\",\"#808080\",\"#808080\",\"#FF0000\"");
    String list2 =  new String("\"#808080\",\"#00FF00\",\"#00FF00\",\"#808080\",\"#00FF00\",\"#00FFFF\",\"#808080\",\"#808080\",\"#00FF00\",\"#808080\",\"#00FFFF\",\"#00FFFF\",\"#808080\",\"#FFFF00\",\"#00FF00\",\"#00FFFF\",\"#00FFFF\",\"#00FFFF\",\"#00FF00\",\"#00FF00\",\"#FFFF00\",\"#FFFF00\",\"#808080\",\"#00FFFF\",\"#00FF00\",\"#808080\",\"#FFFF00\",\"#00FF00\",\"#00FFFF\",\"#808080\",\"#FFFF00\",\"#808080\",\"#808080\",\"#00FFFF\",\"#00FFFF\",\"#00FF00\",\"#808080\",\"#808080\",\"#808080\",\"#00FF00\",\"#00FF00\",\"#FFFF00\",\"#00FF00\",\"#00FF00\",\"#00FF00\",\"#00FF00\",\"#808080\",\"#00FF00\",\"#00FF00\",\"#808080\",\"#00FF00\",\"#808080\",\"#00FF00\",\"#00FF00\",\"#808080\",\"#808080\"");
    String list3 =  new String("\"#00FFFF\",\"#00FF00\",\"#808080\",\"#FFFF00\",\"#808080\",\"#808080\",\"#808080\",\"#00FF00\",\"#FFFF00\",\"#00FF00\",\"#00FFFF\",\"#FFFF00\",\"#00FFFF\",\"#808080\",\"#808080\",\"#00FF00\",\"#808080\",\"#00FFFF\",\"#808080\",\"#00FFFF\",\"#00FF00\",\"#00FFFF\",\"#808080\",\"#808080\",\"#00FF00\",\"#808080\",\"#808080\",\"#FFFF00\",\"#808080\",\"#00FFFF\",\"#808080\",\"#808080\",\"#00FF00\",\"#00FF00\",\"#FF0000\",\"#FFFF00\",\"#808080\",\"#808080\",\"#00FF00\",\"#FF0000\",\"#FFFF00\",\"#FF0000\",\"#00FFFF\",\"#808080\",\"#808080\",\"#00FF00\",\"#808080\",\"#808080\",\"#00FFFF\",\"#808080\",\"#FFFF00\",\"#FFFF00\",\"#808080\",\"#00FF00\",\"#FF0000\",\"#FF0000\"");
    String list4 =  new String("\"#FFFF00\",\"#808080\",\"#FFFF00\",\"#00FFFF\",\"#00FF00\",\"#808080\",\"#FFFF00\",\"#00FF00\",\"#FF0000\",\"#00FFFF\",\"#FF0000\",\"#00FFFF\",\"#808080\",\"#00FF00\",\"#808080\",\"#808080\",\"#808080\",\"#808080\",\"#808080\",\"#00FF00\",\"#00FF00\",\"#FFFF00\",\"#00FFFF\",\"#808080\",\"#808080\",\"#00FFFF\",\"#00FF00\",\"#808080\",\"#00FFFF\",\"#808080\",\"#808080\",\"#808080\",\"#808080\",\"#00FFFF\",\"#FFFF00\",\"#808080\",\"#808080\",\"#00FF00\",\"#808080\",\"#808080\",\"#FFFF00\",\"#808080\",\"#00FFFF\",\"#FF0000\",\"#808080\",\"#FFFF00\",\"#00FF00\",\"#FF0000\",\"#808080\",\"#808080\",\"#FFFF00\",\"#808080\",\"#FF0000\",\"#00FF00\",\"#FFFF00\",\"#00FF00\"");
    String list5 =  new String("\"#808080\",\"#808080\",\"#FFFF00\",\"#808080\",\"#FFFF00\",\"#808080\",\"#00FF00\",\"#00FF00\",\"#00FFFF\",\"#808080\",\"#808080\",\"#808080\",\"#808080\",\"#00FF00\",\"#808080\",\"#00FF00\",\"#00FF00\",\"#808080\",\"#808080\",\"#808080\",\"#00FFFF\",\"#00FF00\",\"#00FF00\",\"#00FF00\",\"#00FF00\",\"#808080\",\"#FFFF00\",\"#00FFFF\",\"#808080\",\"#808080\",\"#808080\",\"#00FFFF\",\"#808080\",\"#00FF00\",\"#FFFF00\",\"#808080\",\"#00FF00\",\"#808080\",\"#00FFFF\",\"#808080\",\"#808080\",\"#FFFF00\",\"#808080\",\"#808080\",\"#808080\",\"#00FFFF\",\"#00FFFF\",\"#00FF00\",\"#FFFF00\",\"#808080\",\"#00FFFF\",\"#808080\",\"#808080\",\"#808080\",\"#00FFFF\",\"#808080\"");
    String list6 =  new String("\"#808080\",\"#00FFFF\",\"#00FF00\",\"#00FF00\",\"#00FF00\",\"#808080\",\"#808080\",\"#00FFFF\",\"#FFFF00\",\"#808080\",\"#00FFFF\",\"#00FF00\",\"#808080\",\"#00FF00\",\"#00FF00\",\"#808080\",\"#808080\",\"#808080\",\"#00FF00\",\"#00FF00\",\"#00FF00\",\"#808080\",\"#808080\",\"#808080\",\"#FF0000\",\"#808080\",\"#00FF00\",\"#00FF00\",\"#808080\",\"#808080\",\"#00FF00\",\"#808080\",\"#808080\",\"#808080\",\"#808080\",\"#808080\",\"#00FFFF\",\"#00FF00\",\"#00FFFF\",\"#808080\",\"#FFFF00\",\"#808080\",\"#808080\",\"#00FFFF\",\"#808080\",\"#808080\",\"#808080\",\"#FFFF00\",\"#00FF00\",\"#00FF00\",\"#808080\",\"#808080\",\"#808080\",\"#00FF00\",\"#FFFF00\",\"#808080\"");
    String list7 =  new String("\"#808080\",\"#00FFFF\",\"#00FF00\",\"#00FF00\",\"#00FFFF\",\"#FFFF00\",\"#00FFFF\",\"#FFFF00\",\"#808080\",\"#00FFFF\",\"#00FFFF\",\"#808080\",\"#808080\",\"#808080\",\"#00FFFF\",\"#808080\",\"#FF0000\",\"#808080\",\"#808080\",\"#FFFF00\",\"#00FFFF\",\"#808080\",\"#808080\",\"#808080\",\"#00FFFF\",\"#FFFF00\",\"#808080\",\"#808080\",\"#00FF00\",\"#808080\",\"#808080\",\"#FFFF00\",\"#00FF00\",\"#808080\",\"#808080\",\"#808080\",\"#00FF00\",\"#808080\",\"#808080\",\"#00FF00\",\"#00FFFF\",\"#FFFF00\",\"#808080\",\"#FFFF00\",\"#808080\",\"#808080\",\"#808080\",\"#00FFFF\",\"#808080\",\"#808080\",\"#00FF00\",\"#FF0000\",\"#808080\",\"#FF0000\",\"#FFFF00\",\"#808080\"");
    String list8 =  new String("\"#00FF00\",\"#808080\",\"#00FF00\",\"#FF0000\",\"#00FFFF\",\"#00FF00\",\"#808080\",\"#808080\",\"#00FFFF\",\"#00FFFF\",\"#808080\",\"#FFFF00\",\"#00FF00\",\"#FF0000\",\"#808080\",\"#FFFF00\",\"#00FF00\",\"#808080\",\"#00FFFF\",\"#808080\",\"#808080\",\"#808080\",\"#808080\",\"#808080\",\"#808080\",\"#808080\",\"#FFFF00\",\"#808080\",\"#00FFFF\",\"#00FFFF\",\"#808080\",\"#808080\",\"#FFFF00\",\"#00FF00\",\"#FF0000\",\"#FFFF00\",\"#808080\",\"#00FF00\",\"#00FFFF\",\"#808080\",\"#808080\",\"#808080\",\"#00FFFF\",\"#00FF00\",\"#808080\",\"#808080\",\"#00FF00\",\"#00FF00\",\"#808080\",\"#808080\",\"#00FF00\",\"#FF0000\",\"#FFFF00\",\"#00FF00\",\"#FFFF00\",\"#00FFFF\"");
    String list9 =  new String("\"#808080\",\"#FFFF00\",\"#00FF00\",\"#00FF00\",\"#00FFFF\",\"#808080\",\"#00FFFF\",\"#00FFFF\",\"#FFFF00\",\"#00FFFF\",\"#808080\",\"#808080\",\"#808080\",\"#FFFF00\",\"#808080\",\"#808080\",\"#FFFF00\",\"#808080\",\"#808080\",\"#808080\",\"#00FF00\",\"#00FFFF\",\"#00FF00\",\"#808080\",\"#808080\",\"#FFFF00\",\"#00FF00\",\"#00FF00\",\"#FFFF00\",\"#00FFFF\",\"#808080\",\"#808080\",\"#00FFFF\",\"#808080\",\"#FFFF00\",\"#808080\",\"#00FFFF\",\"#808080\",\"#00FF00\",\"#00FFFF\",\"#00FFFF\",\"#00FF00\",\"#00FF00\",\"#FFFF00\",\"#808080\",\"#808080\",\"#808080\",\"#808080\",\"#808080\",\"#00FFFF\",\"#808080\",\"#FFFF00\",\"#808080\",\"#00FF00\",\"#00FFFF\",\"#00FFFF\"");
    String list10 = new String("\"#808080\",\"#00FF00\",\"#00FF00\",\"#808080\",\"#FFFF00\",\"#00FFFF\",\"#FFFF00\",\"#FFFF00\",\"#808080\",\"#00FFFF\",\"#FFFF00\",\"#808080\",\"#808080\",\"#FFFF00\",\"#808080\",\"#FFFF00\",\"#808080\",\"#00FF00\",\"#00FFFF\",\"#FF0000\",\"#00FF00\",\"#808080\",\"#00FFFF\",\"#FFFF00\",\"#808080\",\"#808080\",\"#FFFF00\",\"#808080\",\"#FF0000\",\"#00FF00\",\"#00FF00\",\"#808080\",\"#808080\",\"#808080\",\"#FFFF00\",\"#FFFF00\",\"#00FFFF\",\"#808080\",\"#00FFFF\",\"#00FF00\",\"#00FFFF\",\"#808080\",\"#808080\",\"#808080\",\"#808080\",\"#00FFFF\",\"#808080\",\"#00FFFF\",\"#00FF00\",\"#808080\",\"#00FFFF\",\"#00FF00\",\"#00FFFF\",\"#808080\",\"#FFFF00\",\"#808080\"");
    String list11 = new String("\"#00FF00\",\"#808080\",\"#00FFFF\",\"#808080\",\"#FFFF00\",\"#00FF00\",\"#808080\",\"#808080\",\"#808080\",\"#808080\",\"#00FFFF\",\"#808080\",\"#00FFFF\",\"#808080\",\"#00FF00\",\"#808080\",\"#808080\",\"#00FF00\",\"#00FFFF\",\"#00FFFF\",\"#00FF00\",\"#808080\",\"#FFFF00\",\"#FFFF00\",\"#00FF00\",\"#00FF00\",\"#FFFF00\",\"#808080\",\"#00FFFF\",\"#808080\",\"#00FF00\",\"#808080\",\"#808080\",\"#00FF00\",\"#FFFF00\",\"#808080\",\"#00FFFF\",\"#00FF00\",\"#FFFF00\",\"#808080\",\"#FFFF00\",\"#808080\",\"#808080\",\"#00FFFF\",\"#00FFFF\",\"#808080\",\"#00FFFF\",\"#808080\",\"#00FFFF\",\"#00FF00\",\"#00FF00\",\"#808080\",\"#FFFF00\",\"#FF0000\",\"#00FF00\",\"#FFFF00\"");
    String list12 = new String("\"#FF0000\",\"#00FF00\",\"#FFFF00\",\"#808080\",\"#FFFF00\",\"#808080\",\"#FFFF00\",\"#808080\",\"#808080\",\"#808080\",\"#808080\",\"#808080\",\"#808080\",\"#FF0000\",\"#808080\",\"#FFFF00\",\"#FFFF00\",\"#808080\",\"#00FF00\",\"#808080\",\"#808080\",\"#FFFF00\",\"#808080\",\"#808080\",\"#FFFF00\",\"#00FF00\",\"#FF0000\",\"#808080\",\"#FFFF00\",\"#808080\",\"#FFFF00\",\"#808080\",\"#00FFFF\",\"#00FFFF\",\"#00FF00\",\"#00FF00\",\"#808080\",\"#808080\",\"#808080\",\"#FFFF00\",\"#FFFF00\",\"#808080\",\"#FFFF00\",\"#808080\",\"#00FFFF\",\"#00FFFF\",\"#FF0000\",\"#808080\",\"#00FF00\",\"#808080\",\"#808080\",\"#00FF00\",\"#FFFF00\",\"#808080\",\"#808080\",\"#808080\"");
    String list13 = new String("\"#00FFFF\",\"#FF0000\",\"#808080\",\"#808080\",\"#FFFF00\",\"#FFFF00\",\"#808080\",\"#FF0000\",\"#FFFF00\",\"#808080\",\"#00FFFF\",\"#808080\",\"#FFFF00\",\"#808080\",\"#00FFFF\",\"#FFFF00\",\"#00FF00\",\"#FFFF00\",\"#808080\",\"#00FFFF\",\"#00FF00\",\"#00FFFF\",\"#00FF00\",\"#FFFF00\",\"#00FFFF\",\"#808080\",\"#808080\",\"#FFFF00\",\"#00FF00\",\"#00FFFF\",\"#808080\",\"#808080\",\"#00FFFF\",\"#00FFFF\",\"#00FFFF\",\"#808080\",\"#808080\",\"#00FF00\",\"#808080\",\"#808080\",\"#808080\",\"#00FFFF\",\"#00FF00\",\"#808080\",\"#00FFFF\",\"#00FFFF\",\"#00FF00\",\"#808080\",\"#808080\",\"#808080\",\"#00FFFF\",\"#00FFFF\",\"#808080\",\"#808080\",\"#808080\",\"#00FFFF\"");
    String list14 = new String("\"#808080\",\"#808080\",\"#808080\",\"#00FFFF\",\"#808080\",\"#808080\",\"#808080\",\"#FFFF00\",\"#FF0000\",\"#808080\",\"#00FFFF\",\"#00FFFF\",\"#FFFF00\",\"#FFFF00\",\"#808080\",\"#00FFFF\",\"#00FF00\",\"#808080\",\"#00FF00\",\"#808080\",\"#808080\",\"#00FFFF\",\"#00FFFF\",\"#00FF00\",\"#FFFF00\",\"#00FF00\",\"#808080\",\"#FF0000\",\"#00FFFF\",\"#808080\",\"#808080\",\"#808080\",\"#808080\",\"#00FF00\",\"#808080\",\"#FF0000\",\"#00FFFF\",\"#808080\",\"#808080\",\"#808080\",\"#00FF00\",\"#808080\",\"#808080\",\"#808080\",\"#00FF00\",\"#00FFFF\",\"#808080\",\"#808080\",\"#FFFF00\",\"#808080\",\"#808080\",\"#808080\",\"#00FFFF\",\"#808080\",\"#808080\",\"#FFFF00\"");
    String list15 = new String("\"#808080\",\"#00FFFF\",\"#808080\",\"#00FFFF\",\"#00FFFF\",\"#FFFF00\",\"#00FF00\",\"#00FF00\",\"#808080\",\"#00FF00\",\"#00FFFF\",\"#00FFFF\",\"#FFFF00\",\"#808080\",\"#00FF00\",\"#FF0000\",\"#00FF00\",\"#00FFFF\",\"#808080\",\"#808080\",\"#808080\",\"#808080\",\"#FFFF00\",\"#808080\",\"#808080\",\"#00FF00\",\"#808080\",\"#00FF00\",\"#00FF00\",\"#00FF00\",\"#808080\",\"#808080\",\"#808080\",\"#FFFF00\",\"#808080\",\"#808080\",\"#808080\",\"#00FFFF\",\"#FF0000\",\"#FFFF00\",\"#00FF00\",\"#808080\",\"#00FF00\",\"#00FFFF\",\"#00FFFF\",\"#808080\",\"#808080\",\"#00FFFF\",\"#00FF00\",\"#00FF00\",\"#00FFFF\",\"#808080\",\"#00FF00\",\"#FFFF00\",\"#808080\",\"#00FF00\"");
    String list16 = new String("\"#00FFFF\",\"#00FFFF\",\"#FFFF00\",\"#00FF00\",\"#FF0000\",\"#00FF00\",\"#00FFFF\",\"#00FFFF\",\"#00FFFF\",\"#808080\",\"#FFFF00\",\"#00FFFF\",\"#808080\",\"#808080\",\"#00FF00\",\"#FFFF00\",\"#00FFFF\",\"#FFFF00\",\"#FFFF00\",\"#00FFFF\",\"#808080\",\"#808080\",\"#00FFFF\",\"#00FFFF\",\"#00FFFF\",\"#808080\",\"#00FFFF\",\"#00FF00\",\"#00FFFF\",\"#808080\",\"#FFFF00\",\"#808080\",\"#00FF00\",\"#00FFFF\",\"#00FF00\",\"#FF0000\",\"#808080\",\"#808080\",\"#808080\",\"#00FF00\",\"#00FF00\",\"#808080\",\"#FFFF00\",\"#00FF00\",\"#FFFF00\",\"#00FF00\",\"#808080\",\"#808080\",\"#00FF00\",\"#00FF00\",\"#808080\",\"#808080\",\"#808080\",\"#808080\",\"#808080\",\"#808080\"");
    String list17 = new String("\"#808080\",\"#808080\",\"#808080\",\"#00FF00\",\"#808080\",\"#00FF00\",\"#808080\",\"#00FFFF\",\"#00FF00\",\"#808080\",\"#808080\",\"#808080\",\"#FF0000\",\"#00FF00\",\"#00FF00\",\"#00FFFF\",\"#00FFFF\",\"#00FFFF\",\"#808080\",\"#00FFFF\",\"#808080\",\"#00FF00\",\"#00FFFF\",\"#FFFF00\",\"#808080\",\"#808080\",\"#00FF00\",\"#808080\",\"#808080\",\"#808080\",\"#808080\",\"#808080\",\"#808080\",\"#808080\",\"#00FFFF\",\"#808080\",\"#808080\",\"#00FF00\",\"#808080\",\"#808080\",\"#00FF00\",\"#808080\",\"#808080\",\"#808080\",\"#00FF00\",\"#808080\",\"#808080\",\"#FFFF00\",\"#00FFFF\",\"#FFFF00\",\"#808080\",\"#00FF00\",\"#00FF00\",\"#808080\",\"#FFFF00\",\"#808080\"");
    String list18 = new String("\"#808080\",\"#FF0000\",\"#00FFFF\",\"#00FFFF\",\"#00FF00\",\"#808080\",\"#00FFFF\",\"#00FF00\",\"#FFFF00\",\"#FFFF00\",\"#808080\",\"#00FF00\",\"#00FF00\",\"#00FF00\",\"#808080\",\"#FFFF00\",\"#00FFFF\",\"#808080\",\"#808080\",\"#00FFFF\",\"#FF0000\",\"#00FF00\",\"#808080\",\"#808080\",\"#FF0000\",\"#808080\",\"#00FFFF\",\"#808080\",\"#00FF00\",\"#808080\",\"#808080\",\"#FF0000\",\"#00FF00\",\"#808080\",\"#00FFFF\",\"#808080\",\"#FF0000\",\"#00FFFF\",\"#808080\",\"#808080\",\"#00FF00\",\"#808080\",\"#FFFF00\",\"#808080\",\"#808080\",\"#00FFFF\",\"#00FF00\",\"#00FF00\",\"#808080\",\"#00FF00\",\"#FFFF00\",\"#808080\",\"#808080\",\"#00FF00\",\"#808080\",\"#00FF00\"");
    String list19 = new String("\"#808080\",\"#00FF00\",\"#FFFF00\",\"#808080\",\"#808080\",\"#00FFFF\",\"#808080\",\"#FF0000\",\"#00FF00\",\"#808080\",\"#808080\",\"#00FF00\",\"#808080\",\"#808080\",\"#808080\",\"#00FF00\",\"#00FFFF\",\"#00FF00\",\"#808080\",\"#00FF00\",\"#808080\",\"#808080\",\"#FFFF00\",\"#808080\",\"#FFFF00\",\"#808080\",\"#00FFFF\",\"#00FF00\",\"#00FFFF\",\"#808080\",\"#00FF00\",\"#00FF00\",\"#00FF00\",\"#808080\",\"#FFFF00\",\"#00FF00\",\"#FF0000\",\"#808080\",\"#808080\",\"#808080\",\"#808080\",\"#808080\",\"#808080\",\"#808080\",\"#808080\",\"#00FF00\",\"#808080\",\"#FFFF00\",\"#00FFFF\",\"#FFFF00\",\"#00FF00\",\"#808080\",\"#00FF00\",\"#00FFFF\",\"#808080\",\"#808080\"");
    String list20 = new String("\"#808080\",\"#00FFFF\",\"#808080\",\"#00FFFF\",\"#808080\",\"#FFFF00\",\"#808080\",\"#808080\",\"#00FFFF\",\"#FFFF00\",\"#00FF00\",\"#FF0000\",\"#FFFF00\",\"#FFFF00\",\"#808080\",\"#00FF00\",\"#00FF00\",\"#808080\",\"#808080\",\"#FF0000\",\"#00FFFF\",\"#FF0000\",\"#00FF00\",\"#808080\",\"#808080\",\"#808080\",\"#00FF00\",\"#808080\",\"#808080\",\"#FFFF00\",\"#808080\",\"#808080\",\"#FFFF00\",\"#808080\",\"#808080\",\"#FFFF00\",\"#808080\",\"#808080\",\"#808080\",\"#808080\",\"#FFFF00\",\"#808080\",\"#FFFF00\",\"#808080\",\"#808080\",\"#00FFFF\",\"#808080\",\"#808080\",\"#00FFFF\",\"#00FF00\",\"#FFFF00\",\"#00FFFF\",\"#00FF00\",\"#808080\",\"#00FFFF\",\"#FFFF00\"");
    String list21 = new String("\"#00FF00\",\"#00FF00\",\"#00FF00\",\"#00FF00\",\"#808080\",\"#00FF00\",\"#00FFFF\",\"#FFFF00\",\"#FFFF00\",\"#808080\",\"#808080\",\"#808080\",\"#808080\",\"#FFFF00\",\"#808080\",\"#FFFF00\",\"#FFFF00\",\"#808080\",\"#00FFFF\",\"#808080\",\"#00FF00\",\"#00FF00\",\"#FF0000\",\"#808080\",\"#808080\",\"#00FF00\",\"#00FFFF\",\"#808080\",\"#808080\",\"#00FF00\",\"#808080\",\"#808080\",\"#00FFFF\",\"#808080\",\"#00FF00\",\"#00FF00\",\"#808080\",\"#808080\",\"#00FFFF\",\"#808080\",\"#00FFFF\",\"#808080\",\"#00FFFF\",\"#00FF00\",\"#FFFF00\",\"#00FFFF\",\"#808080\",\"#00FF00\",\"#808080\",\"#808080\",\"#00FFFF\",\"#FFFF00\",\"#FFFF00\",\"#808080\",\"#FFFF00\",\"#FFFF00\"");
    String list22 = new String("\"#FFFF00\",\"#FFFF00\",\"#FFFF00\",\"#00FFFF\",\"#808080\",\"#00FF00\",\"#FFFF00\",\"#00FFFF\",\"#FFFF00\",\"#808080\",\"#00FF00\",\"#808080\",\"#00FFFF\",\"#808080\",\"#00FFFF\",\"#00FFFF\",\"#808080\",\"#00FF00\",\"#00FFFF\",\"#00FF00\",\"#00FFFF\",\"#808080\",\"#FF0000\",\"#00FF00\",\"#808080\",\"#00FF00\",\"#808080\",\"#00FF00\",\"#808080\",\"#00FFFF\",\"#808080\",\"#808080\",\"#00FFFF\",\"#FFFF00\",\"#00FFFF\",\"#00FF00\",\"#808080\",\"#FFFF00\",\"#808080\",\"#808080\",\"#FFFF00\",\"#808080\",\"#808080\",\"#FF0000\",\"#00FF00\",\"#808080\",\"#808080\",\"#808080\",\"#00FF00\",\"#808080\",\"#00FF00\",\"#00FF00\",\"#00FF00\",\"#808080\",\"#FFFF00\",\"#808080\"");
    String list23 = new String("\"#FF0000\",\"#808080\",\"#00FFFF\",\"#808080\",\"#00FF00\",\"#808080\",\"#00FF00\",\"#808080\",\"#00FF00\",\"#808080\",\"#808080\",\"#FFFF00\",\"#808080\",\"#808080\",\"#FFFF00\",\"#808080\",\"#00FF00\",\"#FF0000\",\"#808080\",\"#00FFFF\",\"#808080\",\"#808080\",\"#808080\",\"#808080\",\"#00FF00\",\"#00FFFF\",\"#808080\",\"#00FF00\",\"#00FFFF\",\"#808080\",\"#808080\",\"#808080\",\"#808080\",\"#808080\",\"#808080\",\"#00FFFF\",\"#808080\",\"#00FF00\",\"#FF0000\",\"#FFFF00\",\"#808080\",\"#00FFFF\",\"#00FF00\",\"#808080\",\"#00FFFF\",\"#00FFFF\",\"#808080\",\"#808080\",\"#00FF00\",\"#808080\",\"#00FFFF\",\"#00FFFF\",\"#808080\",\"#FFFF00\",\"#FFFF00\",\"#808080\"");

    List<String> list = new ArrayList<>();
    list.add(list1);
    list.add(list2);
    list.add(list3);
    list.add(list4);
    list.add(list5);
    list.add(list6);
    list.add(list7);
    list.add(list8);
    list.add(list9);
    list.add(list10);
    list.add(list11);
    list.add(list12);
    list.add(list13);
    list.add(list14);
    list.add(list15);
    list.add(list16);
    list.add(list17);
    list.add(list18);
    list.add(list19);
    list.add(list20);
    list.add(list21);
    list.add(list22);
    list.add(list23);

    for (int i = 0; i < 23; i++) {
      int k = 1;
      for (int j = 0; j < 56; j++) {
        colors[i][j] = list.get(i).substring(k, k + 7);
        k = k + 10;
      }
    }

  }

  private void setupListPoint() {
    for (int i = 0; i < 23; i++) {
      for (int j = 0; j < 56; j++) {
        points[i][j] = new Point();
      }
    }

    points[0][0].setLat(21.157200);
    points[0][0].setLon(105.456390);
    for (int i = 1; i < 23; i++) {
      points[i][0].setLat(points[i - 1][0].getLat() - delLat);
      points[i][0].setLon(points[i - 1][0].getLon());
    }
    for (int i = 0; i < 23; i++) {
      for (int j = 1; j < 56; j++) {
        points[i][j].setLat(points[i][j - 1].getLat());
        points[i][j].setLon(points[i][j - 1].getLon() + delLon);
      }
    }
    for (int i = 0; i < 23; i++)
      for (int j = 0; j < 56; j++)
        points[i][j].setColor(transform(colors[i][j]));
  }

  private int transform(String s) {
    switch (s) {
      case "#FF0000":
        return getResources().getColor(R.color.red);
      case "#FFFF00":
        return getResources().getColor(R.color.yellow);
      case "#00FF00":
        return getResources().getColor(R.color.green);
      case "#0000FF":
        return getResources().getColor(R.color.blue);
      case "#808080":
        return getResources().getColor(R.color.gray);
      default:
//        return getResources().getColor(R.color.gray);
        return Color.RED;
    }
  }

  private void setupListCell() {
    for (int i = 0; i < 23; i++) {
      for (int j = 1; j < 56; j++) {
        PolygonOptions polygonOptions = new PolygonOptions();
        polygonOptions.addAll(points[i][j].getPeaks());
        polygonOptions.strokeWidth(0);
        Polygon polygon = mMap.addPolygon(polygonOptions);
        polygon.setFillColor(points[i][j].getColor());
      }
    }
  }

  private void showTrafficState() {
    mMap.clear();
    setupListColor();
    setupListPoint();
    setupListCell();
    mGridview.setVisibility(View.GONE);

    MarkerOptions marker1 = new MarkerOptions().position(new LatLng(21.157200, 105.456390));
    MarkerOptions marker2 = new MarkerOptions().position(new LatLng(21.157200, 105.951180));
    MarkerOptions marker3 = new MarkerOptions().position(new LatLng(20.951180, 105.456390));
    MarkerOptions marker4 = new MarkerOptions().position(new LatLng(20.951180, 105.951180));
    LatLngBounds.Builder builder = new LatLngBounds.Builder();
    builder.include(marker1.getPosition());
    builder.include(marker2.getPosition());
    builder.include(marker3.getPosition());
    builder.include(marker4.getPosition());
    LatLngBounds bounds = builder.build();
    int padding = 0;
    CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
//    mMap.getUiSettings().setScrollGesturesEnabled(false);
    mMap.animateCamera(cu);
  }

  private void showLocation() {
    mLocationSearchCv.setVisibility(View.VISIBLE);
    mSearchCancelImg.setVisibility(View.GONE);
    mGridview.setVisibility(View.GONE);
    mMap.clear();
    getCurrentLocation();
  }

  private void checkPermissions() {
    if (ContextCompat.checkSelfPermission(getActivity(),
        android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
        != PackageManager.PERMISSION_GRANTED) {

      // Should we show an explanation?
      if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
          android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {


      } else {
        ActivityCompat.requestPermissions(getActivity(),
            new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
            MY_PERMISSIONS_REQUEST_READ_CONTACTS);
      }
    }
    if (ContextCompat.checkSelfPermission(getActivity(),
        android.Manifest.permission.ACCESS_FINE_LOCATION)
        != PackageManager.PERMISSION_GRANTED) {

      // Should we show an explanation?
      if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
          android.Manifest.permission.ACCESS_FINE_LOCATION)) {


      } else {
        ActivityCompat.requestPermissions(getActivity(),
            new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
            MY_PERMISSIONS_REQUEST_READ_CONTACTS);
      }
    }
  }

  /**
   * get current location
   */
  public void getCurrentLocation() {
    LocationManager locationManager = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);
    Criteria criteria = new Criteria();
    if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
      if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
          android.Manifest.permission.ACCESS_FINE_LOCATION)) {


      } else {
        ActivityCompat.requestPermissions(getActivity(),
            new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
            MY_PERMISSIONS_REQUEST_READ_CONTACTS);
      }
    }
//    Location lastLocation = locationManager.getLastKnownLocation(locationManager.getBestProvider(criteria, false));
    Location lastLocation = getLastKnownLocation();
    if (lastLocation != null) {
      mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
          new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude()), 16));
      CameraPosition cameraPosition = new CameraPosition.Builder()
          .target(new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude()))      // Sets the center of the map to location user
          .zoom(15)                   // Sets the zoom
          .bearing(90)                // Sets the orientation of the camera to east
          .tilt(40)                   // Sets the tilt of the camera to 30 degrees
          .build();                   // Creates a CameraPosition from the builder
      mMap.getUiSettings().setScrollGesturesEnabled(true);
      mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }
  }

  /**
   * on show map
   */
  @Override
  public void showMap() {
    final SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
        .findFragmentById(R.id.map_fm);
    MySupportMapFragment fr = (MySupportMapFragment) mapFragment;
    fr.getMapAsync(this);
    initLayout();
//    mGridview.setAdapter(new GridViewAdapter(getActivity()));
    mGridview.setOnTouchListener(new View.OnTouchListener() {
      @Override
      public boolean onTouch(View v, MotionEvent event) {
        return mapFragment.getView().dispatchTouchEvent(event);
      }
    });
  }

  @Override
  public void onConnected(@Nullable Bundle bundle) {

  }

  @Override
  public void onConnectionSuspended(int i) {

  }

  @Override
  public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

  }

  @Override
  public void onMapReady(GoogleMap googleMap) {
//    hideProgress();
    mMap = googleMap;
    /**
     * check permission
     */
    if (ActivityCompat.checkSelfPermission(getActivity(),
        android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
        && ActivityCompat.checkSelfPermission(getActivity(),
        android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
      if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
          android.Manifest.permission.ACCESS_FINE_LOCATION)) {


      } else {
        ActivityCompat.requestPermissions(getActivity(),
            new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
            MY_PERMISSIONS_REQUEST_READ_CONTACTS);
      }
      return;
    }

    /**
     * location changed listener
     */
    mMap.setMyLocationEnabled(true);
    mMap.setPadding(0, 300, 0, 0);

    switch (mPresenter.getKey()) {
      case Constants.LOCATION:
        showLocation();
        break;
      case Constants.NORMAL:
        break;
      case Constants.ADVANCE:
        break;
      case Constants.TRAFFIC:
        showTrafficState();
        break;
      case Constants.SIGNOUT:
        signout();
        break;
      default:
        break;
    }

    GoogleMap.OnMyLocationChangeListener myLocationChangeListener = new GoogleMap.OnMyLocationChangeListener() {
      @Override
      public void onMyLocationChange(Location location) {
        LatLng loc = new LatLng(location.getLatitude(), location.getLongitude());
        if (mMap != null) {
//          mMap.clear();
          mMap.addMarker(new MarkerOptions().position(loc));
        }
      }
    };
    mMap.setOnMyLocationChangeListener(myLocationChangeListener);
  }

  public void getLocation() {
    LocationManager locationManager = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);
    Criteria criteria = new Criteria();
    if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
      if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
          android.Manifest.permission.ACCESS_FINE_LOCATION)) {


      } else {
        ActivityCompat.requestPermissions(getActivity(),
            new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
            MY_PERMISSIONS_REQUEST_READ_CONTACTS);
      }
    }
    Location lastLocation = locationManager.getLastKnownLocation(locationManager.getBestProvider(criteria, false));
    if (lastLocation != null) {
      mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
          new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude()), 16));
      CameraPosition cameraPosition = new CameraPosition.Builder()
          .target(new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude()))      // Sets the center of the map to location user
          .zoom(15)                   // Sets the zoom
          .bearing(90)                // Sets the orientation of the camera to east
          .tilt(40)                   // Sets the tilt of the camera to 30 degrees
          .build();                   // Creates a CameraPosition from the builder
      mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }
  }

  private Location getLastKnownLocation() {
    LocationManager mLocationManager = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);
    if (ActivityCompat.checkSelfPermission(getActivity(),
        android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
        && ActivityCompat.checkSelfPermission(getActivity(),
        android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
      if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
          android.Manifest.permission.ACCESS_FINE_LOCATION)) {


      } else {
        ActivityCompat.requestPermissions(getActivity(),
            new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
            MY_PERMISSIONS_REQUEST_READ_CONTACTS);
      }
    }

    List<String> providers = mLocationManager.getProviders(true);
    Location bestLocation = null;
    for (String provider : providers) {
      Location l = mLocationManager.getLastKnownLocation(provider);
      if (l == null) {
        continue;
      }
      if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
        // Found best last known location: %s", l);
        bestLocation = l;
      }
    }
    if (bestLocation == null) {
      return null;
    }
    return bestLocation;
  }

  private void openAutoCompleteActivity() {
    try {
      Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY).build(getActivity());
      startActivityForResult(intent, LOCATION_CODE_AUTOCOMPLETE);
    } catch (GooglePlayServicesRepairableException e) {
      GoogleApiAvailability.getInstance().getErrorDialog(getActivity(), e.getConnectionStatusCode(),
          0).show();
    } catch (GooglePlayServicesNotAvailableException e) {
      String message = "Google Play Services is not available: " +
          GoogleApiAvailability.getInstance().getErrorString(e.errorCode);
      Log.e(TAG, message);
      Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }
  }

  @OnClick(R.id.activity_main_location_cancel_img)
  public void cancelLocation() {
    mLocationSearchTv.setText("Search location here...");
    mSearchCancelImg.setVisibility(View.GONE);
    getCurrentLocation();
  }


  @Override
  public void onItemSelected(MenuItem menuItem) {

  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (requestCode == LOCATION_CODE_AUTOCOMPLETE) {
      if (resultCode == RESULT_OK) {
        Place place = PlaceAutocomplete.getPlace(getActivity(), data);
        LatLng latln = place.getLatLng();
        mLocationSearchTv.setText(place.getName());
        mSearchCancelImg.setVisibility(View.VISIBLE);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latln, 16));
        mMap.addMarker(new MarkerOptions().position(latln));

        Log.i(TAG, "Place: " + place.getName());
      } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
        Status status = PlaceAutocomplete.getStatus(getActivity(), data);
        // TODO: Handle the error.
        Log.i(TAG, status.getStatusMessage());

      } else if (resultCode == RESULT_CANCELED) {
      }
    }
  }

  private String readFromFile(Context context) {

    String ret = "";

    try {
      InputStream inputStream = context.openFileInput("data.txt");

      if (inputStream != null) {
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        String receiveString = "";
        StringBuilder stringBuilder = new StringBuilder();

        while ((receiveString = bufferedReader.readLine()) != null) {
          stringBuilder.append(receiveString);
        }

        inputStream.close();
        ret = stringBuilder.toString();
      }
    } catch (FileNotFoundException e) {
      Log.e("login activity", "File not found: " + e.toString());
    } catch (IOException e) {
      Log.e("login activity", "Can not read file: " + e.toString());
    }

    return ret;
  }
}
