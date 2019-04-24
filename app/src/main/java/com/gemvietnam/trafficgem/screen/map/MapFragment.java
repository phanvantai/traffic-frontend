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

import com.gemvietnam.base.viper.ViewFragment;
import com.gemvietnam.trafficgem.R;
import com.gemvietnam.trafficgem.library.MySupportMapFragment;
import com.gemvietnam.trafficgem.library.Point;
import com.gemvietnam.Constants;
import com.gemvietnam.trafficgem.library.responseMessage.CurrentTrafficResponse;
import com.gemvietnam.trafficgem.screen.leftmenu.MenuItem;
import com.gemvietnam.trafficgem.screen.leftmenu.OnMenuItemClickedListener;
import com.gemvietnam.trafficgem.service.DataExchange;
import com.gemvietnam.trafficgem.utils.CustomToken;
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
import com.orhanobut.hawk.Hawk;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import butterknife.BindView;
import butterknife.OnClick;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static android.content.Context.LOCATION_SERVICE;
import static com.gemvietnam.trafficgem.utils.Constants.MY_TOKEN;
import static com.gemvietnam.trafficgem.utils.Constants.URL_CURRENT;

/**
 * The Main Fragment
 */
public class MapFragment extends ViewFragment<MapContract.Presenter> implements MapContract.View,
    OnMapReadyCallback, GoogleApiClient.OnConnectionFailedListener,
    GoogleApiClient.ConnectionCallbacks, OnMenuItemClickedListener {

  @BindView(R.id.activity_main_location_search_tv)
  TextView mLocationSearchTv;
  @BindView(R.id.activity_main_location_cancel_img)
  ImageView mSearchCancelImg;
  @BindView(R.id.activity_main_location_search_cv)
  CardView mLocationSearchCv;
  @BindView(R.id.activity_main_gridview)
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

  private Point[][] points;
  private String[][] colors;
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

  private String[][] setupListColor() {
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
    return colors;
  }

  private void setupListPoint(CurrentTrafficResponse trafficResponse) {
    for (int i = 0; i < trafficResponse.getHeight(); i++) {
      for (int j = 0; j < trafficResponse.getWidth(); j++) {
        points[i][j] = new Point();
      }
    }
    points[0][0].setLat(trafficResponse.getNorth());
    points[0][0].setLon(trafficResponse.getWest());
    for (int i = 1; i < trafficResponse.getHeight(); i++) {
      points[i][0].setLat(points[i - 1][0].getLat() - delLat);
      points[i][0].setLon(points[i - 1][0].getLon());
    }
    for (int i = 0; i < trafficResponse.getHeight(); i++) {
      for (int j = 1; j < trafficResponse.getWidth(); j++) {
        points[i][j].setLat(points[i][j - 1].getLat());
        points[i][j].setLon(points[i][j - 1].getLon() + delLon);
      }
    }
    for (int i = 0; i < trafficResponse.getHeight(); i++)
      for (int j = 0; j < trafficResponse.getWidth(); j++)
        points[i][j].setColor(transform(colors[i][j]));

//    for (int i = 0; i < 23; i++) {
//      for (int j = 0; j < 56; j++) {
//        points[i][j] = new Point();
//      }
//    }
//
//    points[0][0].setLat(21.157200);
//    points[0][0].setLon(105.456390);
//    for (int i = 1; i < 23; i++) {
//      points[i][0].setLat(points[i - 1][0].getLat() - delLat);
//      points[i][0].setLon(points[i - 1][0].getLon());
//    }
//    for (int i = 0; i < 23; i++) {
//      for (int j = 1; j < 56; j++) {
//        points[i][j].setLat(points[i][j - 1].getLat());
//        points[i][j].setLon(points[i][j - 1].getLon() + delLon);
//      }
//    }
//    for (int i = 0; i < 23; i++)
//      for (int j = 0; j < 56; j++)
//        points[i][j].setColor(transform(colors[i][j]));
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
    int layer = 1;
    CustomToken customToken = Hawk.get(MY_TOKEN);
    DataExchange dataExchange = new DataExchange(URL_CURRENT);
    // layer = ???
//    dataExchange.getCurrent(customToken.getToken(), layer);
//    String response = dataExchange.getResponse();
    String response = testResponse();
    //      START DEMO
//    JSONObject jsonObject = new JSONObject();
//    JSONObject jsonData = new JSONObject();
//    JSONObject jsonGrid = new JSONObject();
//    JSONArray jsonCells = new JSONArray();
//    JSONObject jsonCell = new JSONObject();
//    try {
//      jS
//    } catch (JSONException e){
//      e.printStackTrace();
//    }

    //      END DEMO
    CurrentTrafficResponse trafficResponse = new CurrentTrafficResponse(response);
    trafficResponse.analysis();
//


    int height = trafficResponse.getHeight();
    Log.d("test-height", String.valueOf(height));
    int width = trafficResponse.getWidth();
    double north = trafficResponse.getNorth();
    double south = trafficResponse.getSouth();
    double west = trafficResponse.getWest();
    double east = trafficResponse.getEast();
    points = new Point[height][width];
//    colors = new String[height][width];
//    colors = setupListColor();
    colors = trafficResponse.getColorArray();
    Log.d("test-color",colors[0][0]);
    Log.d("test-color1",colors[22][55]);
    // setup list color
//    for (int i = 0; i < height; i++) {
//      for (int j = 0; j < width; j++) {
//        colors[i][j] = trafficResponse.getColorArray()[i][j];
//      }
//    }
    setupListPoint(trafficResponse);
    // setup List Cell;
    for (int i = 0; i < height; i++) {
      for (int j = 1; j < width; j++) {
        PolygonOptions polygonOptions = new PolygonOptions();
        polygonOptions.addAll(points[i][j].getPeaks());
        polygonOptions.strokeWidth(0);
        Polygon polygon = mMap.addPolygon(polygonOptions);
        polygon.setFillColor(points[i][j].getColor());
      }
    }
    mGridview.setVisibility(View.GONE);

    MarkerOptions marker1 = new MarkerOptions().position(new LatLng(north, west));
    MarkerOptions marker2 = new MarkerOptions().position(new LatLng(north, east));
    MarkerOptions marker3 = new MarkerOptions().position(new LatLng(south, west));
    MarkerOptions marker4 = new MarkerOptions().position(new LatLng(south, east));
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
      Log.e("doLogin activity", "File not found: " + e.toString());
    } catch (IOException e) {
      Log.e("doLogin activity", "Can not read file: " + e.toString());
    }

    return ret;
  }

  public String testResponse(){
    String test = new String("{\n" +
            "    \"success\": true,\n" +
            "    \"message\": \"success\",\n" +
            "    \"data\": {\n" +
            "        \"grid\": {\n" +
            "            \"height\": 23,\n" +
            "            \"width\": 56,\n" +
            "            \"east\": 105.92,\n" +
            "            \"west\": 105.46,\n" +
            "            \"south\": 20.95,\n" +
            "            \"north\": 21.16\n" +
            "        },\n" +
            "        \"cells\": [\n" +
            "            { \"height\":0, \"width\":0, \"color\":\"#808080\" },{ \"height\":0, \"width\":1, \"color\":\"#FF0000\" },{ \"height\":0, \"width\":2, \"color\":\"#FF0000\" },{ \"height\":0, \"width\":3, \"color\":\"#808080\" },{ \"height\":0, \"width\":4, \"color\":\"#808080\" },{ \"height\":0, \"width\":5, \"color\":\"#808080\" },{ \"height\":0, \"width\":6, \"color\":\"#00FF00\" },{ \"height\":0, \"width\":7, \"color\":\"#00FF00\" },{ \"height\":0, \"width\":8, \"color\":\"#00FF00\" },{ \"height\":0, \"width\":9, \"color\":\"#808080\" },{ \"height\":0, \"width\":10, \"color\":\"#FFFF00\" },{ \"height\":0, \"width\":11, \"color\":\"#808080\" },{ \"height\":0, \"width\":12, \"color\":\"#00FF00\" },{ \"height\":0, \"width\":13, \"color\":\"#808080\" },{ \"height\":0, \"width\":14, \"color\":\"#FFFF00\" },{ \"height\":0, \"width\":15, \"color\":\"#808080\" },{ \"height\":0, \"width\":16, \"color\":\"#808080\" },{ \"height\":0, \"width\":17, \"color\":\"#FFFF00\" },{ \"height\":0, \"width\":18, \"color\":\"#00FF00\" },{ \"height\":0, \"width\":19, \"color\":\"#808080\" },{ \"height\":0, \"width\":20, \"color\":\"#808080\" },{ \"height\":0, \"width\":21, \"color\":\"#808080\" },{ \"height\":0, \"width\":22, \"color\":\"#808080\" },{ \"height\":0, \"width\":23, \"color\":\"#FFFF00\" },{ \"height\":0, \"width\":24, \"color\":\"#808080\" },{ \"height\":0, \"width\":25, \"color\":\"#00FFFF\" },{ \"height\":0, \"width\":26, \"color\":\"#FFFF00\" },{ \"height\":0, \"width\":27, \"color\":\"#00FF00\" },{ \"height\":0, \"width\":28, \"color\":\"#808080\" },{ \"height\":0, \"width\":29, \"color\":\"#808080\" },{ \"height\":0, \"width\":30, \"color\":\"#00FF00\" },{ \"height\":0, \"width\":31, \"color\":\"#808080\" },{ \"height\":0, \"width\":32, \"color\":\"#00FFFF\" },{ \"height\":0, \"width\":33, \"color\":\"#FFFF00\" },{ \"height\":0, \"width\":34, \"color\":\"#00FF00\" },{ \"height\":0, \"width\":35, \"color\":\"#808080\" },{ \"height\":0, \"width\":36, \"color\":\"#808080\" },{ \"height\":0, \"width\":37, \"color\":\"#808080\" },{ \"height\":0, \"width\":38, \"color\":\"#FFFF00\" },{ \"height\":0, \"width\":39, \"color\":\"#808080\" },{ \"height\":0, \"width\":40, \"color\":\"#808080\" },{ \"height\":0, \"width\":41, \"color\":\"#00FFFF\" },{ \"height\":0, \"width\":42, \"color\":\"#808080\" },{ \"height\":0, \"width\":43, \"color\":\"#00FFFF\" },{ \"height\":0, \"width\":44, \"color\":\"#00FF00\" },{ \"height\":0, \"width\":45, \"color\":\"#808080\" },{ \"height\":0, \"width\":46, \"color\":\"#FFFF00\" },{ \"height\":0, \"width\":47, \"color\":\"#FF0000\" },{ \"height\":0, \"width\":48, \"color\":\"#808080\" },{ \"height\":0, \"width\":49, \"color\":\"#00FF00\" },{ \"height\":0, \"width\":50, \"color\":\"#808080\" },{ \"height\":0, \"width\":51, \"color\":\"#808080\" },{ \"height\":0, \"width\":52, \"color\":\"#808080\" },{ \"height\":0, \"width\":53, \"color\":\"#808080\" },{ \"height\":0, \"width\":54, \"color\":\"#808080\" },{ \"height\":0, \"width\":55, \"color\":\"#FF0000\" },\n" +
            "{ \"height\":1, \"width\":0, \"color\":\"#808080\" },{ \"height\":1, \"width\":1, \"color\":\"#00FF00\" },{ \"height\":1, \"width\":2, \"color\":\"#00FF00\" },{ \"height\":1, \"width\":3, \"color\":\"#808080\" },{ \"height\":1, \"width\":4, \"color\":\"#00FF00\" },{ \"height\":1, \"width\":5, \"color\":\"#00FFFF\" },{ \"height\":1, \"width\":6, \"color\":\"#808080\" },{ \"height\":1, \"width\":7, \"color\":\"#808080\" },{ \"height\":1, \"width\":8, \"color\":\"#00FF00\" },{ \"height\":1, \"width\":9, \"color\":\"#808080\" },{ \"height\":1, \"width\":10, \"color\":\"#00FFFF\" },{ \"height\":1, \"width\":11, \"color\":\"#00FFFF\" },{ \"height\":1, \"width\":12, \"color\":\"#808080\" },{ \"height\":1, \"width\":13, \"color\":\"#FFFF00\" },{ \"height\":1, \"width\":14, \"color\":\"#00FF00\" },{ \"height\":1, \"width\":15, \"color\":\"#00FFFF\" },{ \"height\":1, \"width\":16, \"color\":\"#00FFFF\" },{ \"height\":1, \"width\":17, \"color\":\"#00FFFF\" },{ \"height\":1, \"width\":18, \"color\":\"#00FF00\" },{ \"height\":1, \"width\":19, \"color\":\"#00FF00\" },{ \"height\":1, \"width\":20, \"color\":\"#FFFF00\" },{ \"height\":1, \"width\":21, \"color\":\"#FFFF00\" },{ \"height\":1, \"width\":22, \"color\":\"#808080\" },{ \"height\":1, \"width\":23, \"color\":\"#00FFFF\" },{ \"height\":1, \"width\":24, \"color\":\"#00FF00\" },{ \"height\":1, \"width\":25, \"color\":\"#808080\" },{ \"height\":1, \"width\":26, \"color\":\"#FFFF00\" },{ \"height\":1, \"width\":27, \"color\":\"#00FF00\" },{ \"height\":1, \"width\":28, \"color\":\"#00FFFF\" },{ \"height\":1, \"width\":29, \"color\":\"#808080\" },{ \"height\":1, \"width\":30, \"color\":\"#FFFF00\" },{ \"height\":1, \"width\":31, \"color\":\"#808080\" },{ \"height\":1, \"width\":32, \"color\":\"#808080\" },{ \"height\":1, \"width\":33, \"color\":\"#00FFFF\" },{ \"height\":1, \"width\":34, \"color\":\"#00FFFF\" },{ \"height\":1, \"width\":35, \"color\":\"#00FF00\" },{ \"height\":1, \"width\":36, \"color\":\"#808080\" },{ \"height\":1, \"width\":37, \"color\":\"#808080\" },{ \"height\":1, \"width\":38, \"color\":\"#808080\" },{ \"height\":1, \"width\":39, \"color\":\"#00FF00\" },{ \"height\":1, \"width\":40, \"color\":\"#00FF00\" },{ \"height\":1, \"width\":41, \"color\":\"#FFFF00\" },{ \"height\":1, \"width\":42, \"color\":\"#00FF00\" },{ \"height\":1, \"width\":43, \"color\":\"#00FF00\" },{ \"height\":1, \"width\":44, \"color\":\"#00FF00\" },{ \"height\":1, \"width\":45, \"color\":\"#00FF00\" },{ \"height\":1, \"width\":46, \"color\":\"#808080\" },{ \"height\":1, \"width\":47, \"color\":\"#00FF00\" },{ \"height\":1, \"width\":48, \"color\":\"#00FF00\" },{ \"height\":1, \"width\":49, \"color\":\"#808080\" },{ \"height\":1, \"width\":50, \"color\":\"#00FF00\" },{ \"height\":1, \"width\":51, \"color\":\"#808080\" },{ \"height\":1, \"width\":52, \"color\":\"#00FF00\" },{ \"height\":1, \"width\":53, \"color\":\"#00FF00\" },{ \"height\":1, \"width\":54, \"color\":\"#808080\" },{ \"height\":1, \"width\":55, \"color\":\"#808080\" },\n" +
            "{ \"height\":2, \"width\":0, \"color\":\"#00FFFF\" },{ \"height\":2, \"width\":1, \"color\":\"#00FF00\" },{ \"height\":2, \"width\":2, \"color\":\"#808080\" },{ \"height\":2, \"width\":3, \"color\":\"#FFFF00\" },{ \"height\":2, \"width\":4, \"color\":\"#808080\" },{ \"height\":2, \"width\":5, \"color\":\"#808080\" },{ \"height\":2, \"width\":6, \"color\":\"#808080\" },{ \"height\":2, \"width\":7, \"color\":\"#00FF00\" },{ \"height\":2, \"width\":8, \"color\":\"#FFFF00\" },{ \"height\":2, \"width\":9, \"color\":\"#00FF00\" },{ \"height\":2, \"width\":10, \"color\":\"#00FFFF\" },{ \"height\":2, \"width\":11, \"color\":\"#FFFF00\" },{ \"height\":2, \"width\":12, \"color\":\"#00FFFF\" },{ \"height\":2, \"width\":13, \"color\":\"#808080\" },{ \"height\":2, \"width\":14, \"color\":\"#808080\" },{ \"height\":2, \"width\":15, \"color\":\"#00FF00\" },{ \"height\":2, \"width\":16, \"color\":\"#808080\" },{ \"height\":2, \"width\":17, \"color\":\"#00FFFF\" },{ \"height\":2, \"width\":18, \"color\":\"#808080\" },{ \"height\":2, \"width\":19, \"color\":\"#00FFFF\" },{ \"height\":2, \"width\":20, \"color\":\"#00FF00\" },{ \"height\":2, \"width\":21, \"color\":\"#00FFFF\" },{ \"height\":2, \"width\":22, \"color\":\"#808080\" },{ \"height\":2, \"width\":23, \"color\":\"#808080\" },{ \"height\":2, \"width\":24, \"color\":\"#00FF00\" },{ \"height\":2, \"width\":25, \"color\":\"#808080\" },{ \"height\":2, \"width\":26, \"color\":\"#808080\" },{ \"height\":2, \"width\":27, \"color\":\"#FFFF00\" },{ \"height\":2, \"width\":28, \"color\":\"#808080\" },{ \"height\":2, \"width\":29, \"color\":\"#00FFFF\" },{ \"height\":2, \"width\":30, \"color\":\"#808080\" },{ \"height\":2, \"width\":31, \"color\":\"#808080\" },{ \"height\":2, \"width\":32, \"color\":\"#00FF00\" },{ \"height\":2, \"width\":33, \"color\":\"#00FF00\" },{ \"height\":2, \"width\":34, \"color\":\"#FF0000\" },{ \"height\":2, \"width\":35, \"color\":\"#FFFF00\" },{ \"height\":2, \"width\":36, \"color\":\"#808080\" },{ \"height\":2, \"width\":37, \"color\":\"#808080\" },{ \"height\":2, \"width\":38, \"color\":\"#00FF00\" },{ \"height\":2, \"width\":39, \"color\":\"#FF0000\" },{ \"height\":2, \"width\":40, \"color\":\"#FFFF00\" },{ \"height\":2, \"width\":41, \"color\":\"#FF0000\" },{ \"height\":2, \"width\":42, \"color\":\"#00FFFF\" },{ \"height\":2, \"width\":43, \"color\":\"#808080\" },{ \"height\":2, \"width\":44, \"color\":\"#808080\" },{ \"height\":2, \"width\":45, \"color\":\"#00FF00\" },{ \"height\":2, \"width\":46, \"color\":\"#808080\" },{ \"height\":2, \"width\":47, \"color\":\"#808080\" },{ \"height\":2, \"width\":48, \"color\":\"#00FFFF\" },{ \"height\":2, \"width\":49, \"color\":\"#808080\" },{ \"height\":2, \"width\":50, \"color\":\"#FFFF00\" },{ \"height\":2, \"width\":51, \"color\":\"#FFFF00\" },{ \"height\":2, \"width\":52, \"color\":\"#808080\" },{ \"height\":2, \"width\":53, \"color\":\"#00FF00\" },{ \"height\":2, \"width\":54, \"color\":\"#FF0000\" },{ \"height\":2, \"width\":55, \"color\":\"#FF0000\" },\n" +
            "{ \"height\":3, \"width\":0, \"color\":\"#FFFF00\" },{ \"height\":3, \"width\":1, \"color\":\"#808080\" },{ \"height\":3, \"width\":2, \"color\":\"#FFFF00\" },{ \"height\":3, \"width\":3, \"color\":\"#00FFFF\" },{ \"height\":3, \"width\":4, \"color\":\"#00FF00\" },{ \"height\":3, \"width\":5, \"color\":\"#808080\" },{ \"height\":3, \"width\":6, \"color\":\"#FFFF00\" },{ \"height\":3, \"width\":7, \"color\":\"#00FF00\" },{ \"height\":3, \"width\":8, \"color\":\"#FF0000\" },{ \"height\":3, \"width\":9, \"color\":\"#00FFFF\" },{ \"height\":3, \"width\":10, \"color\":\"#FF0000\" },{ \"height\":3, \"width\":11, \"color\":\"#00FFFF\" },{ \"height\":3, \"width\":12, \"color\":\"#808080\" },{ \"height\":3, \"width\":13, \"color\":\"#00FF00\" },{ \"height\":3, \"width\":14, \"color\":\"#808080\" },{ \"height\":3, \"width\":15, \"color\":\"#808080\" },{ \"height\":3, \"width\":16, \"color\":\"#808080\" },{ \"height\":3, \"width\":17, \"color\":\"#808080\" },{ \"height\":3, \"width\":18, \"color\":\"#808080\" },{ \"height\":3, \"width\":19, \"color\":\"#00FF00\" },{ \"height\":3, \"width\":20, \"color\":\"#00FF00\" },{ \"height\":3, \"width\":21, \"color\":\"#FFFF00\" },{ \"height\":3, \"width\":22, \"color\":\"#00FFFF\" },{ \"height\":3, \"width\":23, \"color\":\"#808080\" },{ \"height\":3, \"width\":24, \"color\":\"#808080\" },{ \"height\":3, \"width\":25, \"color\":\"#00FFFF\" },{ \"height\":3, \"width\":26, \"color\":\"#00FF00\" },{ \"height\":3, \"width\":27, \"color\":\"#808080\" },{ \"height\":3, \"width\":28, \"color\":\"#00FFFF\" },{ \"height\":3, \"width\":29, \"color\":\"#808080\" },{ \"height\":3, \"width\":30, \"color\":\"#808080\" },{ \"height\":3, \"width\":31, \"color\":\"#808080\" },{ \"height\":3, \"width\":32, \"color\":\"#808080\" },{ \"height\":3, \"width\":33, \"color\":\"#00FFFF\" },{ \"height\":3, \"width\":34, \"color\":\"#FFFF00\" },{ \"height\":3, \"width\":35, \"color\":\"#808080\" },{ \"height\":3, \"width\":36, \"color\":\"#808080\" },{ \"height\":3, \"width\":37, \"color\":\"#00FF00\" },{ \"height\":3, \"width\":38, \"color\":\"#808080\" },{ \"height\":3, \"width\":39, \"color\":\"#808080\" },{ \"height\":3, \"width\":40, \"color\":\"#FFFF00\" },{ \"height\":3, \"width\":41, \"color\":\"#808080\" },{ \"height\":3, \"width\":42, \"color\":\"#00FFFF\" },{ \"height\":3, \"width\":43, \"color\":\"#FF0000\" },{ \"height\":3, \"width\":44, \"color\":\"#808080\" },{ \"height\":3, \"width\":45, \"color\":\"#FFFF00\" },{ \"height\":3, \"width\":46, \"color\":\"#00FF00\" },{ \"height\":3, \"width\":47, \"color\":\"#FF0000\" },{ \"height\":3, \"width\":48, \"color\":\"#808080\" },{ \"height\":3, \"width\":49, \"color\":\"#808080\" },{ \"height\":3, \"width\":50, \"color\":\"#FFFF00\" },{ \"height\":3, \"width\":51, \"color\":\"#808080\" },{ \"height\":3, \"width\":52, \"color\":\"#FF0000\" },{ \"height\":3, \"width\":53, \"color\":\"#00FF00\" },{ \"height\":3, \"width\":54, \"color\":\"#FFFF00\" },{ \"height\":3, \"width\":55, \"color\":\"#00FF00\" },\n" +
            "{ \"height\":4, \"width\":0, \"color\":\"#808080\" },{ \"height\":4, \"width\":1, \"color\":\"#808080\" },{ \"height\":4, \"width\":2, \"color\":\"#FFFF00\" },{ \"height\":4, \"width\":3, \"color\":\"#808080\" },{ \"height\":4, \"width\":4, \"color\":\"#FFFF00\" },{ \"height\":4, \"width\":5, \"color\":\"#808080\" },{ \"height\":4, \"width\":6, \"color\":\"#00FF00\" },{ \"height\":4, \"width\":7, \"color\":\"#00FF00\" },{ \"height\":4, \"width\":8, \"color\":\"#00FFFF\" },{ \"height\":4, \"width\":9, \"color\":\"#808080\" },{ \"height\":4, \"width\":10, \"color\":\"#808080\" },{ \"height\":4, \"width\":11, \"color\":\"#808080\" },{ \"height\":4, \"width\":12, \"color\":\"#808080\" },{ \"height\":4, \"width\":13, \"color\":\"#00FF00\" },{ \"height\":4, \"width\":14, \"color\":\"#808080\" },{ \"height\":4, \"width\":15, \"color\":\"#00FF00\" },{ \"height\":4, \"width\":16, \"color\":\"#00FF00\" },{ \"height\":4, \"width\":17, \"color\":\"#808080\" },{ \"height\":4, \"width\":18, \"color\":\"#808080\" },{ \"height\":4, \"width\":19, \"color\":\"#808080\" },{ \"height\":4, \"width\":20, \"color\":\"#00FFFF\" },{ \"height\":4, \"width\":21, \"color\":\"#00FF00\" },{ \"height\":4, \"width\":22, \"color\":\"#00FF00\" },{ \"height\":4, \"width\":23, \"color\":\"#00FF00\" },{ \"height\":4, \"width\":24, \"color\":\"#00FF00\" },{ \"height\":4, \"width\":25, \"color\":\"#808080\" },{ \"height\":4, \"width\":26, \"color\":\"#FFFF00\" },{ \"height\":4, \"width\":27, \"color\":\"#00FFFF\" },{ \"height\":4, \"width\":28, \"color\":\"#808080\" },{ \"height\":4, \"width\":29, \"color\":\"#808080\" },{ \"height\":4, \"width\":30, \"color\":\"#808080\" },{ \"height\":4, \"width\":31, \"color\":\"#00FFFF\" },{ \"height\":4, \"width\":32, \"color\":\"#808080\" },{ \"height\":4, \"width\":33, \"color\":\"#00FF00\" },{ \"height\":4, \"width\":34, \"color\":\"#FFFF00\" },{ \"height\":4, \"width\":35, \"color\":\"#808080\" },{ \"height\":4, \"width\":36, \"color\":\"#00FF00\" },{ \"height\":4, \"width\":37, \"color\":\"#808080\" },{ \"height\":4, \"width\":38, \"color\":\"#00FFFF\" },{ \"height\":4, \"width\":39, \"color\":\"#808080\" },{ \"height\":4, \"width\":40, \"color\":\"#808080\" },{ \"height\":4, \"width\":41, \"color\":\"#FFFF00\" },{ \"height\":4, \"width\":42, \"color\":\"#808080\" },{ \"height\":4, \"width\":43, \"color\":\"#808080\" },{ \"height\":4, \"width\":44, \"color\":\"#808080\" },{ \"height\":4, \"width\":45, \"color\":\"#00FFFF\" },{ \"height\":4, \"width\":46, \"color\":\"#00FFFF\" },{ \"height\":4, \"width\":47, \"color\":\"#00FF00\" },{ \"height\":4, \"width\":48, \"color\":\"#FFFF00\" },{ \"height\":4, \"width\":49, \"color\":\"#808080\" },{ \"height\":4, \"width\":50, \"color\":\"#00FFFF\" },{ \"height\":4, \"width\":51, \"color\":\"#808080\" },{ \"height\":4, \"width\":52, \"color\":\"#808080\" },{ \"height\":4, \"width\":53, \"color\":\"#808080\" },{ \"height\":4, \"width\":54, \"color\":\"#00FFFF\" },{ \"height\":4, \"width\":55, \"color\":\"#808080\" },\n" +
            "{ \"height\":5, \"width\":0, \"color\":\"#808080\" },{ \"height\":5, \"width\":1, \"color\":\"#00FFFF\" },{ \"height\":5, \"width\":2, \"color\":\"#00FF00\" },{ \"height\":5, \"width\":3, \"color\":\"#00FF00\" },{ \"height\":5, \"width\":4, \"color\":\"#00FF00\" },{ \"height\":5, \"width\":5, \"color\":\"#808080\" },{ \"height\":5, \"width\":6, \"color\":\"#808080\" },{ \"height\":5, \"width\":7, \"color\":\"#00FFFF\" },{ \"height\":5, \"width\":8, \"color\":\"#FFFF00\" },{ \"height\":5, \"width\":9, \"color\":\"#808080\" },{ \"height\":5, \"width\":10, \"color\":\"#00FFFF\" },{ \"height\":5, \"width\":11, \"color\":\"#00FF00\" },{ \"height\":5, \"width\":12, \"color\":\"#808080\" },{ \"height\":5, \"width\":13, \"color\":\"#00FF00\" },{ \"height\":5, \"width\":14, \"color\":\"#00FF00\" },{ \"height\":5, \"width\":15, \"color\":\"#808080\" },{ \"height\":5, \"width\":16, \"color\":\"#808080\" },{ \"height\":5, \"width\":17, \"color\":\"#808080\" },{ \"height\":5, \"width\":18, \"color\":\"#00FF00\" },{ \"height\":5, \"width\":19, \"color\":\"#00FF00\" },{ \"height\":5, \"width\":20, \"color\":\"#00FF00\" },{ \"height\":5, \"width\":21, \"color\":\"#808080\" },{ \"height\":5, \"width\":22, \"color\":\"#808080\" },{ \"height\":5, \"width\":23, \"color\":\"#808080\" },{ \"height\":5, \"width\":24, \"color\":\"#FF0000\" },{ \"height\":5, \"width\":25, \"color\":\"#808080\" },{ \"height\":5, \"width\":26, \"color\":\"#00FF00\" },{ \"height\":5, \"width\":27, \"color\":\"#00FF00\" },{ \"height\":5, \"width\":28, \"color\":\"#808080\" },{ \"height\":5, \"width\":29, \"color\":\"#808080\" },{ \"height\":5, \"width\":30, \"color\":\"#00FF00\" },{ \"height\":5, \"width\":31, \"color\":\"#808080\" },{ \"height\":5, \"width\":32, \"color\":\"#808080\" },{ \"height\":5, \"width\":33, \"color\":\"#808080\" },{ \"height\":5, \"width\":34, \"color\":\"#808080\" },{ \"height\":5, \"width\":35, \"color\":\"#808080\" },{ \"height\":5, \"width\":36, \"color\":\"#00FFFF\" },{ \"height\":5, \"width\":37, \"color\":\"#00FF00\" },{ \"height\":5, \"width\":38, \"color\":\"#00FFFF\" },{ \"height\":5, \"width\":39, \"color\":\"#808080\" },{ \"height\":5, \"width\":40, \"color\":\"#FFFF00\" },{ \"height\":5, \"width\":41, \"color\":\"#808080\" },{ \"height\":5, \"width\":42, \"color\":\"#808080\" },{ \"height\":5, \"width\":43, \"color\":\"#00FFFF\" },{ \"height\":5, \"width\":44, \"color\":\"#808080\" },{ \"height\":5, \"width\":45, \"color\":\"#808080\" },{ \"height\":5, \"width\":46, \"color\":\"#808080\" },{ \"height\":5, \"width\":47, \"color\":\"#FFFF00\" },{ \"height\":5, \"width\":48, \"color\":\"#00FF00\" },{ \"height\":5, \"width\":49, \"color\":\"#00FF00\" },{ \"height\":5, \"width\":50, \"color\":\"#808080\" },{ \"height\":5, \"width\":51, \"color\":\"#808080\" },{ \"height\":5, \"width\":52, \"color\":\"#808080\" },{ \"height\":5, \"width\":53, \"color\":\"#00FF00\" },{ \"height\":5, \"width\":54, \"color\":\"#FFFF00\" },{ \"height\":5, \"width\":55, \"color\":\"#808080\" },\n" +
            "{ \"height\":6, \"width\":0, \"color\":\"#808080\" },{ \"height\":6, \"width\":1, \"color\":\"#00FFFF\" },{ \"height\":6, \"width\":2, \"color\":\"#00FF00\" },{ \"height\":6, \"width\":3, \"color\":\"#00FF00\" },{ \"height\":6, \"width\":4, \"color\":\"#00FFFF\" },{ \"height\":6, \"width\":5, \"color\":\"#FFFF00\" },{ \"height\":6, \"width\":6, \"color\":\"#00FFFF\" },{ \"height\":6, \"width\":7, \"color\":\"#FFFF00\" },{ \"height\":6, \"width\":8, \"color\":\"#808080\" },{ \"height\":6, \"width\":9, \"color\":\"#00FFFF\" },{ \"height\":6, \"width\":10, \"color\":\"#00FFFF\" },{ \"height\":6, \"width\":11, \"color\":\"#808080\" },{ \"height\":6, \"width\":12, \"color\":\"#808080\" },{ \"height\":6, \"width\":13, \"color\":\"#808080\" },{ \"height\":6, \"width\":14, \"color\":\"#00FFFF\" },{ \"height\":6, \"width\":15, \"color\":\"#808080\" },{ \"height\":6, \"width\":16, \"color\":\"#FF0000\" },{ \"height\":6, \"width\":17, \"color\":\"#808080\" },{ \"height\":6, \"width\":18, \"color\":\"#808080\" },{ \"height\":6, \"width\":19, \"color\":\"#FFFF00\" },{ \"height\":6, \"width\":20, \"color\":\"#00FFFF\" },{ \"height\":6, \"width\":21, \"color\":\"#808080\" },{ \"height\":6, \"width\":22, \"color\":\"#808080\" },{ \"height\":6, \"width\":23, \"color\":\"#808080\" },{ \"height\":6, \"width\":24, \"color\":\"#00FFFF\" },{ \"height\":6, \"width\":25, \"color\":\"#FFFF00\" },{ \"height\":6, \"width\":26, \"color\":\"#808080\" },{ \"height\":6, \"width\":27, \"color\":\"#808080\" },{ \"height\":6, \"width\":28, \"color\":\"#00FF00\" },{ \"height\":6, \"width\":29, \"color\":\"#808080\" },{ \"height\":6, \"width\":30, \"color\":\"#808080\" },{ \"height\":6, \"width\":31, \"color\":\"#FFFF00\" },{ \"height\":6, \"width\":32, \"color\":\"#00FF00\" },{ \"height\":6, \"width\":33, \"color\":\"#808080\" },{ \"height\":6, \"width\":34, \"color\":\"#808080\" },{ \"height\":6, \"width\":35, \"color\":\"#808080\" },{ \"height\":6, \"width\":36, \"color\":\"#00FF00\" },{ \"height\":6, \"width\":37, \"color\":\"#808080\" },{ \"height\":6, \"width\":38, \"color\":\"#808080\" },{ \"height\":6, \"width\":39, \"color\":\"#00FF00\" },{ \"height\":6, \"width\":40, \"color\":\"#00FFFF\" },{ \"height\":6, \"width\":41, \"color\":\"#FFFF00\" },{ \"height\":6, \"width\":42, \"color\":\"#808080\" },{ \"height\":6, \"width\":43, \"color\":\"#FFFF00\" },{ \"height\":6, \"width\":44, \"color\":\"#808080\" },{ \"height\":6, \"width\":45, \"color\":\"#808080\" },{ \"height\":6, \"width\":46, \"color\":\"#808080\" },{ \"height\":6, \"width\":47, \"color\":\"#00FFFF\" },{ \"height\":6, \"width\":48, \"color\":\"#808080\" },{ \"height\":6, \"width\":49, \"color\":\"#808080\" },{ \"height\":6, \"width\":50, \"color\":\"#00FF00\" },{ \"height\":6, \"width\":51, \"color\":\"#FF0000\" },{ \"height\":6, \"width\":52, \"color\":\"#808080\" },{ \"height\":6, \"width\":53, \"color\":\"#FF0000\" },{ \"height\":6, \"width\":54, \"color\":\"#FFFF00\" },{ \"height\":6, \"width\":55, \"color\":\"#808080\" },\n" +
            "{ \"height\":7, \"width\":0, \"color\":\"#00FF00\" },{ \"height\":7, \"width\":1, \"color\":\"#808080\" },{ \"height\":7, \"width\":2, \"color\":\"#00FF00\" },{ \"height\":7, \"width\":3, \"color\":\"#FF0000\" },{ \"height\":7, \"width\":4, \"color\":\"#00FFFF\" },{ \"height\":7, \"width\":5, \"color\":\"#00FF00\" },{ \"height\":7, \"width\":6, \"color\":\"#808080\" },{ \"height\":7, \"width\":7, \"color\":\"#808080\" },{ \"height\":7, \"width\":8, \"color\":\"#00FFFF\" },{ \"height\":7, \"width\":9, \"color\":\"#00FFFF\" },{ \"height\":7, \"width\":10, \"color\":\"#808080\" },{ \"height\":7, \"width\":11, \"color\":\"#FFFF00\" },{ \"height\":7, \"width\":12, \"color\":\"#00FF00\" },{ \"height\":7, \"width\":13, \"color\":\"#FF0000\" },{ \"height\":7, \"width\":14, \"color\":\"#808080\" },{ \"height\":7, \"width\":15, \"color\":\"#FFFF00\" },{ \"height\":7, \"width\":16, \"color\":\"#00FF00\" },{ \"height\":7, \"width\":17, \"color\":\"#808080\" },{ \"height\":7, \"width\":18, \"color\":\"#00FFFF\" },{ \"height\":7, \"width\":19, \"color\":\"#808080\" },{ \"height\":7, \"width\":20, \"color\":\"#808080\" },{ \"height\":7, \"width\":21, \"color\":\"#808080\" },{ \"height\":7, \"width\":22, \"color\":\"#808080\" },{ \"height\":7, \"width\":23, \"color\":\"#808080\" },{ \"height\":7, \"width\":24, \"color\":\"#808080\" },{ \"height\":7, \"width\":25, \"color\":\"#808080\" },{ \"height\":7, \"width\":26, \"color\":\"#FFFF00\" },{ \"height\":7, \"width\":27, \"color\":\"#808080\" },{ \"height\":7, \"width\":28, \"color\":\"#00FFFF\" },{ \"height\":7, \"width\":29, \"color\":\"#00FFFF\" },{ \"height\":7, \"width\":30, \"color\":\"#808080\" },{ \"height\":7, \"width\":31, \"color\":\"#808080\" },{ \"height\":7, \"width\":32, \"color\":\"#FFFF00\" },{ \"height\":7, \"width\":33, \"color\":\"#00FF00\" },{ \"height\":7, \"width\":34, \"color\":\"#FF0000\" },{ \"height\":7, \"width\":35, \"color\":\"#FFFF00\" },{ \"height\":7, \"width\":36, \"color\":\"#808080\" },{ \"height\":7, \"width\":37, \"color\":\"#00FF00\" },{ \"height\":7, \"width\":38, \"color\":\"#00FFFF\" },{ \"height\":7, \"width\":39, \"color\":\"#808080\" },{ \"height\":7, \"width\":40, \"color\":\"#808080\" },{ \"height\":7, \"width\":41, \"color\":\"#808080\" },{ \"height\":7, \"width\":42, \"color\":\"#00FFFF\" },{ \"height\":7, \"width\":43, \"color\":\"#00FF00\" },{ \"height\":7, \"width\":44, \"color\":\"#808080\" },{ \"height\":7, \"width\":45, \"color\":\"#808080\" },{ \"height\":7, \"width\":46, \"color\":\"#00FF00\" },{ \"height\":7, \"width\":47, \"color\":\"#00FF00\" },{ \"height\":7, \"width\":48, \"color\":\"#808080\" },{ \"height\":7, \"width\":49, \"color\":\"#808080\" },{ \"height\":7, \"width\":50, \"color\":\"#00FF00\" },{ \"height\":7, \"width\":51, \"color\":\"#FF0000\" },{ \"height\":7, \"width\":52, \"color\":\"#FFFF00\" },{ \"height\":7, \"width\":53, \"color\":\"#00FF00\" },{ \"height\":7, \"width\":54, \"color\":\"#FFFF00\" },{ \"height\":7, \"width\":55, \"color\":\"#00FFFF\" },\n" +
            "{ \"height\":8, \"width\":0, \"color\":\"#808080\" },{ \"height\":8, \"width\":1, \"color\":\"#FFFF00\" },{ \"height\":8, \"width\":2, \"color\":\"#00FF00\" },{ \"height\":8, \"width\":3, \"color\":\"#00FF00\" },{ \"height\":8, \"width\":4, \"color\":\"#00FFFF\" },{ \"height\":8, \"width\":5, \"color\":\"#808080\" },{ \"height\":8, \"width\":6, \"color\":\"#00FFFF\" },{ \"height\":8, \"width\":7, \"color\":\"#00FFFF\" },{ \"height\":8, \"width\":8, \"color\":\"#FFFF00\" },{ \"height\":8, \"width\":9, \"color\":\"#00FFFF\" },{ \"height\":8, \"width\":10, \"color\":\"#808080\" },{ \"height\":8, \"width\":11, \"color\":\"#808080\" },{ \"height\":8, \"width\":12, \"color\":\"#808080\" },{ \"height\":8, \"width\":13, \"color\":\"#FFFF00\" },{ \"height\":8, \"width\":14, \"color\":\"#808080\" },{ \"height\":8, \"width\":15, \"color\":\"#808080\" },{ \"height\":8, \"width\":16, \"color\":\"#FFFF00\" },{ \"height\":8, \"width\":17, \"color\":\"#808080\" },{ \"height\":8, \"width\":18, \"color\":\"#808080\" },{ \"height\":8, \"width\":19, \"color\":\"#808080\" },{ \"height\":8, \"width\":20, \"color\":\"#00FF00\" },{ \"height\":8, \"width\":21, \"color\":\"#00FFFF\" },{ \"height\":8, \"width\":22, \"color\":\"#00FF00\" },{ \"height\":8, \"width\":23, \"color\":\"#808080\" },{ \"height\":8, \"width\":24, \"color\":\"#808080\" },{ \"height\":8, \"width\":25, \"color\":\"#FFFF00\" },{ \"height\":8, \"width\":26, \"color\":\"#00FF00\" },{ \"height\":8, \"width\":27, \"color\":\"#00FF00\" },{ \"height\":8, \"width\":28, \"color\":\"#FFFF00\" },{ \"height\":8, \"width\":29, \"color\":\"#00FFFF\" },{ \"height\":8, \"width\":30, \"color\":\"#808080\" },{ \"height\":8, \"width\":31, \"color\":\"#808080\" },{ \"height\":8, \"width\":32, \"color\":\"#00FFFF\" },{ \"height\":8, \"width\":33, \"color\":\"#808080\" },{ \"height\":8, \"width\":34, \"color\":\"#FFFF00\" },{ \"height\":8, \"width\":35, \"color\":\"#808080\" },{ \"height\":8, \"width\":36, \"color\":\"#00FFFF\" },{ \"height\":8, \"width\":37, \"color\":\"#808080\" },{ \"height\":8, \"width\":38, \"color\":\"#00FF00\" },{ \"height\":8, \"width\":39, \"color\":\"#00FFFF\" },{ \"height\":8, \"width\":40, \"color\":\"#00FFFF\" },{ \"height\":8, \"width\":41, \"color\":\"#00FF00\" },{ \"height\":8, \"width\":42, \"color\":\"#00FF00\" },{ \"height\":8, \"width\":43, \"color\":\"#FFFF00\" },{ \"height\":8, \"width\":44, \"color\":\"#808080\" },{ \"height\":8, \"width\":45, \"color\":\"#808080\" },{ \"height\":8, \"width\":46, \"color\":\"#808080\" },{ \"height\":8, \"width\":47, \"color\":\"#808080\" },{ \"height\":8, \"width\":48, \"color\":\"#808080\" },{ \"height\":8, \"width\":49, \"color\":\"#00FFFF\" },{ \"height\":8, \"width\":50, \"color\":\"#808080\" },{ \"height\":8, \"width\":51, \"color\":\"#FFFF00\" },{ \"height\":8, \"width\":52, \"color\":\"#808080\" },{ \"height\":8, \"width\":53, \"color\":\"#00FF00\" },{ \"height\":8, \"width\":54, \"color\":\"#00FFFF\" },{ \"height\":8, \"width\":55, \"color\":\"#00FFFF\" },\n" +
            "{ \"height\":9, \"width\":0, \"color\":\"#808080\" },{ \"height\":9, \"width\":1, \"color\":\"#00FF00\" },{ \"height\":9, \"width\":2, \"color\":\"#00FF00\" },{ \"height\":9, \"width\":3, \"color\":\"#808080\" },{ \"height\":9, \"width\":4, \"color\":\"#FFFF00\" },{ \"height\":9, \"width\":5, \"color\":\"#00FFFF\" },{ \"height\":9, \"width\":6, \"color\":\"#FFFF00\" },{ \"height\":9, \"width\":7, \"color\":\"#FFFF00\" },{ \"height\":9, \"width\":8, \"color\":\"#808080\" },{ \"height\":9, \"width\":9, \"color\":\"#00FFFF\" },{ \"height\":9, \"width\":10, \"color\":\"#FFFF00\" },{ \"height\":9, \"width\":11, \"color\":\"#808080\" },{ \"height\":9, \"width\":12, \"color\":\"#808080\" },{ \"height\":9, \"width\":13, \"color\":\"#FFFF00\" },{ \"height\":9, \"width\":14, \"color\":\"#808080\" },{ \"height\":9, \"width\":15, \"color\":\"#FFFF00\" },{ \"height\":9, \"width\":16, \"color\":\"#808080\" },{ \"height\":9, \"width\":17, \"color\":\"#00FF00\" },{ \"height\":9, \"width\":18, \"color\":\"#00FFFF\" },{ \"height\":9, \"width\":19, \"color\":\"#FF0000\" },{ \"height\":9, \"width\":20, \"color\":\"#00FF00\" },{ \"height\":9, \"width\":21, \"color\":\"#808080\" },{ \"height\":9, \"width\":22, \"color\":\"#00FFFF\" },{ \"height\":9, \"width\":23, \"color\":\"#FFFF00\" },{ \"height\":9, \"width\":24, \"color\":\"#808080\" },{ \"height\":9, \"width\":25, \"color\":\"#808080\" },{ \"height\":9, \"width\":26, \"color\":\"#FFFF00\" },{ \"height\":9, \"width\":27, \"color\":\"#808080\" },{ \"height\":9, \"width\":28, \"color\":\"#FF0000\" },{ \"height\":9, \"width\":29, \"color\":\"#00FF00\" },{ \"height\":9, \"width\":30, \"color\":\"#00FF00\" },{ \"height\":9, \"width\":31, \"color\":\"#808080\" },{ \"height\":9, \"width\":32, \"color\":\"#808080\" },{ \"height\":9, \"width\":33, \"color\":\"#808080\" },{ \"height\":9, \"width\":34, \"color\":\"#FFFF00\" },{ \"height\":9, \"width\":35, \"color\":\"#FFFF00\" },{ \"height\":9, \"width\":36, \"color\":\"#00FFFF\" },{ \"height\":9, \"width\":37, \"color\":\"#808080\" },{ \"height\":9, \"width\":38, \"color\":\"#00FFFF\" },{ \"height\":9, \"width\":39, \"color\":\"#00FF00\" },{ \"height\":9, \"width\":40, \"color\":\"#00FFFF\" },{ \"height\":9, \"width\":41, \"color\":\"#808080\" },{ \"height\":9, \"width\":42, \"color\":\"#808080\" },{ \"height\":9, \"width\":43, \"color\":\"#808080\" },{ \"height\":9, \"width\":44, \"color\":\"#808080\" },{ \"height\":9, \"width\":45, \"color\":\"#00FFFF\" },{ \"height\":9, \"width\":46, \"color\":\"#808080\" },{ \"height\":9, \"width\":47, \"color\":\"#00FFFF\" },{ \"height\":9, \"width\":48, \"color\":\"#00FF00\" },{ \"height\":9, \"width\":49, \"color\":\"#808080\" },{ \"height\":9, \"width\":50, \"color\":\"#00FFFF\" },{ \"height\":9, \"width\":51, \"color\":\"#00FF00\" },{ \"height\":9, \"width\":52, \"color\":\"#00FFFF\" },{ \"height\":9, \"width\":53, \"color\":\"#808080\" },{ \"height\":9, \"width\":54, \"color\":\"#FFFF00\" },{ \"height\":9, \"width\":55, \"color\":\"#808080\" },\n" +
            "{ \"height\":10, \"width\":0, \"color\":\"#00FF00\" },{ \"height\":10, \"width\":1, \"color\":\"#808080\" },{ \"height\":10, \"width\":2, \"color\":\"#00FFFF\" },{ \"height\":10, \"width\":3, \"color\":\"#808080\" },{ \"height\":10, \"width\":4, \"color\":\"#FFFF00\" },{ \"height\":10, \"width\":5, \"color\":\"#00FF00\" },{ \"height\":10, \"width\":6, \"color\":\"#808080\" },{ \"height\":10, \"width\":7, \"color\":\"#808080\" },{ \"height\":10, \"width\":8, \"color\":\"#808080\" },{ \"height\":10, \"width\":9, \"color\":\"#808080\" },{ \"height\":10, \"width\":10, \"color\":\"#00FFFF\" },{ \"height\":10, \"width\":11, \"color\":\"#808080\" },{ \"height\":10, \"width\":12, \"color\":\"#00FFFF\" },{ \"height\":10, \"width\":13, \"color\":\"#808080\" },{ \"height\":10, \"width\":14, \"color\":\"#00FF00\" },{ \"height\":10, \"width\":15, \"color\":\"#808080\" },{ \"height\":10, \"width\":16, \"color\":\"#808080\" },{ \"height\":10, \"width\":17, \"color\":\"#00FF00\" },{ \"height\":10, \"width\":18, \"color\":\"#00FFFF\" },{ \"height\":10, \"width\":19, \"color\":\"#00FFFF\" },{ \"height\":10, \"width\":20, \"color\":\"#00FF00\" },{ \"height\":10, \"width\":21, \"color\":\"#808080\" },{ \"height\":10, \"width\":22, \"color\":\"#FFFF00\" },{ \"height\":10, \"width\":23, \"color\":\"#FFFF00\" },{ \"height\":10, \"width\":24, \"color\":\"#00FF00\" },{ \"height\":10, \"width\":25, \"color\":\"#00FF00\" },{ \"height\":10, \"width\":26, \"color\":\"#FFFF00\" },{ \"height\":10, \"width\":27, \"color\":\"#808080\" },{ \"height\":10, \"width\":28, \"color\":\"#00FFFF\" },{ \"height\":10, \"width\":29, \"color\":\"#808080\" },{ \"height\":10, \"width\":30, \"color\":\"#00FF00\" },{ \"height\":10, \"width\":31, \"color\":\"#808080\" },{ \"height\":10, \"width\":32, \"color\":\"#808080\" },{ \"height\":10, \"width\":33, \"color\":\"#00FF00\" },{ \"height\":10, \"width\":34, \"color\":\"#FFFF00\" },{ \"height\":10, \"width\":35, \"color\":\"#808080\" },{ \"height\":10, \"width\":36, \"color\":\"#00FFFF\" },{ \"height\":10, \"width\":37, \"color\":\"#00FF00\" },{ \"height\":10, \"width\":38, \"color\":\"#FFFF00\" },{ \"height\":10, \"width\":39, \"color\":\"#808080\" },{ \"height\":10, \"width\":40, \"color\":\"#FFFF00\" },{ \"height\":10, \"width\":41, \"color\":\"#808080\" },{ \"height\":10, \"width\":42, \"color\":\"#808080\" },{ \"height\":10, \"width\":43, \"color\":\"#00FFFF\" },{ \"height\":10, \"width\":44, \"color\":\"#00FFFF\" },{ \"height\":10, \"width\":45, \"color\":\"#808080\" },{ \"height\":10, \"width\":46, \"color\":\"#00FFFF\" },{ \"height\":10, \"width\":47, \"color\":\"#808080\" },{ \"height\":10, \"width\":48, \"color\":\"#00FFFF\" },{ \"height\":10, \"width\":49, \"color\":\"#00FF00\" },{ \"height\":10, \"width\":50, \"color\":\"#00FF00\" },{ \"height\":10, \"width\":51, \"color\":\"#808080\" },{ \"height\":10, \"width\":52, \"color\":\"#FFFF00\" },{ \"height\":10, \"width\":53, \"color\":\"#FF0000\" },{ \"height\":10, \"width\":54, \"color\":\"#00FF00\" },{ \"height\":10, \"width\":55, \"color\":\"#FFFF00\" },\n" +
            "{ \"height\":11, \"width\":0, \"color\":\"#FF0000\" },{ \"height\":11, \"width\":1, \"color\":\"#00FF00\" },{ \"height\":11, \"width\":2, \"color\":\"#FFFF00\" },{ \"height\":11, \"width\":3, \"color\":\"#808080\" },{ \"height\":11, \"width\":4, \"color\":\"#FFFF00\" },{ \"height\":11, \"width\":5, \"color\":\"#808080\" },{ \"height\":11, \"width\":6, \"color\":\"#FFFF00\" },{ \"height\":11, \"width\":7, \"color\":\"#808080\" },{ \"height\":11, \"width\":8, \"color\":\"#808080\" },{ \"height\":11, \"width\":9, \"color\":\"#808080\" },{ \"height\":11, \"width\":10, \"color\":\"#808080\" },{ \"height\":11, \"width\":11, \"color\":\"#808080\" },{ \"height\":11, \"width\":12, \"color\":\"#808080\" },{ \"height\":11, \"width\":13, \"color\":\"#FF0000\" },{ \"height\":11, \"width\":14, \"color\":\"#808080\" },{ \"height\":11, \"width\":15, \"color\":\"#FFFF00\" },{ \"height\":11, \"width\":16, \"color\":\"#FFFF00\" },{ \"height\":11, \"width\":17, \"color\":\"#808080\" },{ \"height\":11, \"width\":18, \"color\":\"#00FF00\" },{ \"height\":11, \"width\":19, \"color\":\"#808080\" },{ \"height\":11, \"width\":20, \"color\":\"#808080\" },{ \"height\":11, \"width\":21, \"color\":\"#FFFF00\" },{ \"height\":11, \"width\":22, \"color\":\"#808080\" },{ \"height\":11, \"width\":23, \"color\":\"#808080\" },{ \"height\":11, \"width\":24, \"color\":\"#FFFF00\" },{ \"height\":11, \"width\":25, \"color\":\"#00FF00\" },{ \"height\":11, \"width\":26, \"color\":\"#FF0000\" },{ \"height\":11, \"width\":27, \"color\":\"#808080\" },{ \"height\":11, \"width\":28, \"color\":\"#FFFF00\" },{ \"height\":11, \"width\":29, \"color\":\"#808080\" },{ \"height\":11, \"width\":30, \"color\":\"#FFFF00\" },{ \"height\":11, \"width\":31, \"color\":\"#808080\" },{ \"height\":11, \"width\":32, \"color\":\"#00FFFF\" },{ \"height\":11, \"width\":33, \"color\":\"#00FFFF\" },{ \"height\":11, \"width\":34, \"color\":\"#00FF00\" },{ \"height\":11, \"width\":35, \"color\":\"#00FF00\" },{ \"height\":11, \"width\":36, \"color\":\"#808080\" },{ \"height\":11, \"width\":37, \"color\":\"#808080\" },{ \"height\":11, \"width\":38, \"color\":\"#808080\" },{ \"height\":11, \"width\":39, \"color\":\"#FFFF00\" },{ \"height\":11, \"width\":40, \"color\":\"#FFFF00\" },{ \"height\":11, \"width\":41, \"color\":\"#808080\" },{ \"height\":11, \"width\":42, \"color\":\"#FFFF00\" },{ \"height\":11, \"width\":43, \"color\":\"#808080\" },{ \"height\":11, \"width\":44, \"color\":\"#00FFFF\" },{ \"height\":11, \"width\":45, \"color\":\"#00FFFF\" },{ \"height\":11, \"width\":46, \"color\":\"#FF0000\" },{ \"height\":11, \"width\":47, \"color\":\"#808080\" },{ \"height\":11, \"width\":48, \"color\":\"#00FF00\" },{ \"height\":11, \"width\":49, \"color\":\"#808080\" },{ \"height\":11, \"width\":50, \"color\":\"#808080\" },{ \"height\":11, \"width\":51, \"color\":\"#00FF00\" },{ \"height\":11, \"width\":52, \"color\":\"#FFFF00\" },{ \"height\":11, \"width\":53, \"color\":\"#808080\" },{ \"height\":11, \"width\":54, \"color\":\"#808080\" },{ \"height\":11, \"width\":55, \"color\":\"#808080\" },\n" +
            "{ \"height\":12, \"width\":0, \"color\":\"#00FFFF\" },{ \"height\":12, \"width\":1, \"color\":\"#FF0000\" },{ \"height\":12, \"width\":2, \"color\":\"#808080\" },{ \"height\":12, \"width\":3, \"color\":\"#808080\" },{ \"height\":12, \"width\":4, \"color\":\"#FFFF00\" },{ \"height\":12, \"width\":5, \"color\":\"#FFFF00\" },{ \"height\":12, \"width\":6, \"color\":\"#808080\" },{ \"height\":12, \"width\":7, \"color\":\"#FF0000\" },{ \"height\":12, \"width\":8, \"color\":\"#FFFF00\" },{ \"height\":12, \"width\":9, \"color\":\"#808080\" },{ \"height\":12, \"width\":10, \"color\":\"#00FFFF\" },{ \"height\":12, \"width\":11, \"color\":\"#808080\" },{ \"height\":12, \"width\":12, \"color\":\"#FFFF00\" },{ \"height\":12, \"width\":13, \"color\":\"#808080\" },{ \"height\":12, \"width\":14, \"color\":\"#00FFFF\" },{ \"height\":12, \"width\":15, \"color\":\"#FFFF00\" },{ \"height\":12, \"width\":16, \"color\":\"#00FF00\" },{ \"height\":12, \"width\":17, \"color\":\"#FFFF00\" },{ \"height\":12, \"width\":18, \"color\":\"#808080\" },{ \"height\":12, \"width\":19, \"color\":\"#00FFFF\" },{ \"height\":12, \"width\":20, \"color\":\"#00FF00\" },{ \"height\":12, \"width\":21, \"color\":\"#00FFFF\" },{ \"height\":12, \"width\":22, \"color\":\"#00FF00\" },{ \"height\":12, \"width\":23, \"color\":\"#FFFF00\" },{ \"height\":12, \"width\":24, \"color\":\"#00FFFF\" },{ \"height\":12, \"width\":25, \"color\":\"#808080\" },{ \"height\":12, \"width\":26, \"color\":\"#808080\" },{ \"height\":12, \"width\":27, \"color\":\"#FFFF00\" },{ \"height\":12, \"width\":28, \"color\":\"#00FF00\" },{ \"height\":12, \"width\":29, \"color\":\"#00FFFF\" },{ \"height\":12, \"width\":30, \"color\":\"#808080\" },{ \"height\":12, \"width\":31, \"color\":\"#808080\" },{ \"height\":12, \"width\":32, \"color\":\"#00FFFF\" },{ \"height\":12, \"width\":33, \"color\":\"#00FFFF\" },{ \"height\":12, \"width\":34, \"color\":\"#00FFFF\" },{ \"height\":12, \"width\":35, \"color\":\"#808080\" },{ \"height\":12, \"width\":36, \"color\":\"#808080\" },{ \"height\":12, \"width\":37, \"color\":\"#00FF00\" },{ \"height\":12, \"width\":38, \"color\":\"#808080\" },{ \"height\":12, \"width\":39, \"color\":\"#808080\" },{ \"height\":12, \"width\":40, \"color\":\"#808080\" },{ \"height\":12, \"width\":41, \"color\":\"#00FFFF\" },{ \"height\":12, \"width\":42, \"color\":\"#00FF00\" },{ \"height\":12, \"width\":43, \"color\":\"#808080\" },{ \"height\":12, \"width\":44, \"color\":\"#00FFFF\" },{ \"height\":12, \"width\":45, \"color\":\"#00FFFF\" },{ \"height\":12, \"width\":46, \"color\":\"#00FF00\" },{ \"height\":12, \"width\":47, \"color\":\"#808080\" },{ \"height\":12, \"width\":48, \"color\":\"#808080\" },{ \"height\":12, \"width\":49, \"color\":\"#808080\" },{ \"height\":12, \"width\":50, \"color\":\"#00FFFF\" },{ \"height\":12, \"width\":51, \"color\":\"#00FFFF\" },{ \"height\":12, \"width\":52, \"color\":\"#808080\" },{ \"height\":12, \"width\":53, \"color\":\"#808080\" },{ \"height\":12, \"width\":54, \"color\":\"#808080\" },{ \"height\":12, \"width\":55, \"color\":\"#00FFFF\" },\n" +
            "{ \"height\":13, \"width\":0, \"color\":\"#808080\" },{ \"height\":13, \"width\":1, \"color\":\"#808080\" },{ \"height\":13, \"width\":2, \"color\":\"#808080\" },{ \"height\":13, \"width\":3, \"color\":\"#00FFFF\" },{ \"height\":13, \"width\":4, \"color\":\"#808080\" },{ \"height\":13, \"width\":5, \"color\":\"#808080\" },{ \"height\":13, \"width\":6, \"color\":\"#808080\" },{ \"height\":13, \"width\":7, \"color\":\"#FFFF00\" },{ \"height\":13, \"width\":8, \"color\":\"#FF0000\" },{ \"height\":13, \"width\":9, \"color\":\"#808080\" },{ \"height\":13, \"width\":10, \"color\":\"#00FFFF\" },{ \"height\":13, \"width\":11, \"color\":\"#00FFFF\" },{ \"height\":13, \"width\":12, \"color\":\"#FFFF00\" },{ \"height\":13, \"width\":13, \"color\":\"#FFFF00\" },{ \"height\":13, \"width\":14, \"color\":\"#808080\" },{ \"height\":13, \"width\":15, \"color\":\"#00FFFF\" },{ \"height\":13, \"width\":16, \"color\":\"#00FF00\" },{ \"height\":13, \"width\":17, \"color\":\"#808080\" },{ \"height\":13, \"width\":18, \"color\":\"#00FF00\" },{ \"height\":13, \"width\":19, \"color\":\"#808080\" },{ \"height\":13, \"width\":20, \"color\":\"#808080\" },{ \"height\":13, \"width\":21, \"color\":\"#00FFFF\" },{ \"height\":13, \"width\":22, \"color\":\"#00FFFF\" },{ \"height\":13, \"width\":23, \"color\":\"#00FF00\" },{ \"height\":13, \"width\":24, \"color\":\"#FFFF00\" },{ \"height\":13, \"width\":25, \"color\":\"#00FF00\" },{ \"height\":13, \"width\":26, \"color\":\"#808080\" },{ \"height\":13, \"width\":27, \"color\":\"#FF0000\" },{ \"height\":13, \"width\":28, \"color\":\"#00FFFF\" },{ \"height\":13, \"width\":29, \"color\":\"#808080\" },{ \"height\":13, \"width\":30, \"color\":\"#808080\" },{ \"height\":13, \"width\":31, \"color\":\"#808080\" },{ \"height\":13, \"width\":32, \"color\":\"#808080\" },{ \"height\":13, \"width\":33, \"color\":\"#00FF00\" },{ \"height\":13, \"width\":34, \"color\":\"#808080\" },{ \"height\":13, \"width\":35, \"color\":\"#FF0000\" },{ \"height\":13, \"width\":36, \"color\":\"#00FFFF\" },{ \"height\":13, \"width\":37, \"color\":\"#808080\" },{ \"height\":13, \"width\":38, \"color\":\"#808080\" },{ \"height\":13, \"width\":39, \"color\":\"#808080\" },{ \"height\":13, \"width\":40, \"color\":\"#00FF00\" },{ \"height\":13, \"width\":41, \"color\":\"#808080\" },{ \"height\":13, \"width\":42, \"color\":\"#808080\" },{ \"height\":13, \"width\":43, \"color\":\"#808080\" },{ \"height\":13, \"width\":44, \"color\":\"#00FF00\" },{ \"height\":13, \"width\":45, \"color\":\"#00FFFF\" },{ \"height\":13, \"width\":46, \"color\":\"#808080\" },{ \"height\":13, \"width\":47, \"color\":\"#808080\" },{ \"height\":13, \"width\":48, \"color\":\"#FFFF00\" },{ \"height\":13, \"width\":49, \"color\":\"#808080\" },{ \"height\":13, \"width\":50, \"color\":\"#808080\" },{ \"height\":13, \"width\":51, \"color\":\"#808080\" },{ \"height\":13, \"width\":52, \"color\":\"#00FFFF\" },{ \"height\":13, \"width\":53, \"color\":\"#808080\" },{ \"height\":13, \"width\":54, \"color\":\"#808080\" },{ \"height\":13, \"width\":55, \"color\":\"#FFFF00\" },\n" +
            "{ \"height\":14, \"width\":0, \"color\":\"#808080\" },{ \"height\":14, \"width\":1, \"color\":\"#00FFFF\" },{ \"height\":14, \"width\":2, \"color\":\"#808080\" },{ \"height\":14, \"width\":3, \"color\":\"#00FFFF\" },{ \"height\":14, \"width\":4, \"color\":\"#00FFFF\" },{ \"height\":14, \"width\":5, \"color\":\"#FFFF00\" },{ \"height\":14, \"width\":6, \"color\":\"#00FF00\" },{ \"height\":14, \"width\":7, \"color\":\"#00FF00\" },{ \"height\":14, \"width\":8, \"color\":\"#808080\" },{ \"height\":14, \"width\":9, \"color\":\"#00FF00\" },{ \"height\":14, \"width\":10, \"color\":\"#00FFFF\" },{ \"height\":14, \"width\":11, \"color\":\"#00FFFF\" },{ \"height\":14, \"width\":12, \"color\":\"#FFFF00\" },{ \"height\":14, \"width\":13, \"color\":\"#808080\" },{ \"height\":14, \"width\":14, \"color\":\"#00FF00\" },{ \"height\":14, \"width\":15, \"color\":\"#FF0000\" },{ \"height\":14, \"width\":16, \"color\":\"#00FF00\" },{ \"height\":14, \"width\":17, \"color\":\"#00FFFF\" },{ \"height\":14, \"width\":18, \"color\":\"#808080\" },{ \"height\":14, \"width\":19, \"color\":\"#808080\" },{ \"height\":14, \"width\":20, \"color\":\"#808080\" },{ \"height\":14, \"width\":21, \"color\":\"#808080\" },{ \"height\":14, \"width\":22, \"color\":\"#FFFF00\" },{ \"height\":14, \"width\":23, \"color\":\"#808080\" },{ \"height\":14, \"width\":24, \"color\":\"#808080\" },{ \"height\":14, \"width\":25, \"color\":\"#00FF00\" },{ \"height\":14, \"width\":26, \"color\":\"#808080\" },{ \"height\":14, \"width\":27, \"color\":\"#00FF00\" },{ \"height\":14, \"width\":28, \"color\":\"#00FF00\" },{ \"height\":14, \"width\":29, \"color\":\"#00FF00\" },{ \"height\":14, \"width\":30, \"color\":\"#808080\" },{ \"height\":14, \"width\":31, \"color\":\"#808080\" },{ \"height\":14, \"width\":32, \"color\":\"#808080\" },{ \"height\":14, \"width\":33, \"color\":\"#FFFF00\" },{ \"height\":14, \"width\":34, \"color\":\"#808080\" },{ \"height\":14, \"width\":35, \"color\":\"#808080\" },{ \"height\":14, \"width\":36, \"color\":\"#808080\" },{ \"height\":14, \"width\":37, \"color\":\"#00FFFF\" },{ \"height\":14, \"width\":38, \"color\":\"#FF0000\" },{ \"height\":14, \"width\":39, \"color\":\"#FFFF00\" },{ \"height\":14, \"width\":40, \"color\":\"#00FF00\" },{ \"height\":14, \"width\":41, \"color\":\"#808080\" },{ \"height\":14, \"width\":42, \"color\":\"#00FF00\" },{ \"height\":14, \"width\":43, \"color\":\"#00FFFF\" },{ \"height\":14, \"width\":44, \"color\":\"#00FFFF\" },{ \"height\":14, \"width\":45, \"color\":\"#808080\" },{ \"height\":14, \"width\":46, \"color\":\"#808080\" },{ \"height\":14, \"width\":47, \"color\":\"#00FFFF\" },{ \"height\":14, \"width\":48, \"color\":\"#00FF00\" },{ \"height\":14, \"width\":49, \"color\":\"#00FF00\" },{ \"height\":14, \"width\":50, \"color\":\"#00FFFF\" },{ \"height\":14, \"width\":51, \"color\":\"#808080\" },{ \"height\":14, \"width\":52, \"color\":\"#00FF00\" },{ \"height\":14, \"width\":53, \"color\":\"#FFFF00\" },{ \"height\":14, \"width\":54, \"color\":\"#808080\" },{ \"height\":14, \"width\":55, \"color\":\"#00FF00\" },\n" +
            "{ \"height\":15, \"width\":0, \"color\":\"#00FFFF\" },{ \"height\":15, \"width\":1, \"color\":\"#00FFFF\" },{ \"height\":15, \"width\":2, \"color\":\"#FFFF00\" },{ \"height\":15, \"width\":3, \"color\":\"#00FF00\" },{ \"height\":15, \"width\":4, \"color\":\"#FF0000\" },{ \"height\":15, \"width\":5, \"color\":\"#00FF00\" },{ \"height\":15, \"width\":6, \"color\":\"#00FFFF\" },{ \"height\":15, \"width\":7, \"color\":\"#00FFFF\" },{ \"height\":15, \"width\":8, \"color\":\"#00FFFF\" },{ \"height\":15, \"width\":9, \"color\":\"#808080\" },{ \"height\":15, \"width\":10, \"color\":\"#FFFF00\" },{ \"height\":15, \"width\":11, \"color\":\"#00FFFF\" },{ \"height\":15, \"width\":12, \"color\":\"#808080\" },{ \"height\":15, \"width\":13, \"color\":\"#808080\" },{ \"height\":15, \"width\":14, \"color\":\"#00FF00\" },{ \"height\":15, \"width\":15, \"color\":\"#FFFF00\" },{ \"height\":15, \"width\":16, \"color\":\"#00FFFF\" },{ \"height\":15, \"width\":17, \"color\":\"#FFFF00\" },{ \"height\":15, \"width\":18, \"color\":\"#FFFF00\" },{ \"height\":15, \"width\":19, \"color\":\"#00FFFF\" },{ \"height\":15, \"width\":20, \"color\":\"#808080\" },{ \"height\":15, \"width\":21, \"color\":\"#808080\" },{ \"height\":15, \"width\":22, \"color\":\"#00FFFF\" },{ \"height\":15, \"width\":23, \"color\":\"#00FFFF\" },{ \"height\":15, \"width\":24, \"color\":\"#00FFFF\" },{ \"height\":15, \"width\":25, \"color\":\"#808080\" },{ \"height\":15, \"width\":26, \"color\":\"#00FFFF\" },{ \"height\":15, \"width\":27, \"color\":\"#00FF00\" },{ \"height\":15, \"width\":28, \"color\":\"#00FFFF\" },{ \"height\":15, \"width\":29, \"color\":\"#808080\" },{ \"height\":15, \"width\":30, \"color\":\"#FFFF00\" },{ \"height\":15, \"width\":31, \"color\":\"#808080\" },{ \"height\":15, \"width\":32, \"color\":\"#00FF00\" },{ \"height\":15, \"width\":33, \"color\":\"#00FFFF\" },{ \"height\":15, \"width\":34, \"color\":\"#00FF00\" },{ \"height\":15, \"width\":35, \"color\":\"#FF0000\" },{ \"height\":15, \"width\":36, \"color\":\"#808080\" },{ \"height\":15, \"width\":37, \"color\":\"#808080\" },{ \"height\":15, \"width\":38, \"color\":\"#808080\" },{ \"height\":15, \"width\":39, \"color\":\"#00FF00\" },{ \"height\":15, \"width\":40, \"color\":\"#00FF00\" },{ \"height\":15, \"width\":41, \"color\":\"#808080\" },{ \"height\":15, \"width\":42, \"color\":\"#FFFF00\" },{ \"height\":15, \"width\":43, \"color\":\"#00FF00\" },{ \"height\":15, \"width\":44, \"color\":\"#FFFF00\" },{ \"height\":15, \"width\":45, \"color\":\"#00FF00\" },{ \"height\":15, \"width\":46, \"color\":\"#808080\" },{ \"height\":15, \"width\":47, \"color\":\"#808080\" },{ \"height\":15, \"width\":48, \"color\":\"#00FF00\" },{ \"height\":15, \"width\":49, \"color\":\"#00FF00\" },{ \"height\":15, \"width\":50, \"color\":\"#808080\" },{ \"height\":15, \"width\":51, \"color\":\"#808080\" },{ \"height\":15, \"width\":52, \"color\":\"#808080\" },{ \"height\":15, \"width\":53, \"color\":\"#808080\" },{ \"height\":15, \"width\":54, \"color\":\"#808080\" },{ \"height\":15, \"width\":55, \"color\":\"#808080\" },\n" +
            "{ \"height\":16, \"width\":0, \"color\":\"#808080\" },{ \"height\":16, \"width\":1, \"color\":\"#808080\" },{ \"height\":16, \"width\":2, \"color\":\"#808080\" },{ \"height\":16, \"width\":3, \"color\":\"#00FF00\" },{ \"height\":16, \"width\":4, \"color\":\"#808080\" },{ \"height\":16, \"width\":5, \"color\":\"#00FF00\" },{ \"height\":16, \"width\":6, \"color\":\"#808080\" },{ \"height\":16, \"width\":7, \"color\":\"#00FFFF\" },{ \"height\":16, \"width\":8, \"color\":\"#00FF00\" },{ \"height\":16, \"width\":9, \"color\":\"#808080\" },{ \"height\":16, \"width\":10, \"color\":\"#808080\" },{ \"height\":16, \"width\":11, \"color\":\"#808080\" },{ \"height\":16, \"width\":12, \"color\":\"#FF0000\" },{ \"height\":16, \"width\":13, \"color\":\"#00FF00\" },{ \"height\":16, \"width\":14, \"color\":\"#00FF00\" },{ \"height\":16, \"width\":15, \"color\":\"#00FFFF\" },{ \"height\":16, \"width\":16, \"color\":\"#00FFFF\" },{ \"height\":16, \"width\":17, \"color\":\"#00FFFF\" },{ \"height\":16, \"width\":18, \"color\":\"#808080\" },{ \"height\":16, \"width\":19, \"color\":\"#00FFFF\" },{ \"height\":16, \"width\":20, \"color\":\"#808080\" },{ \"height\":16, \"width\":21, \"color\":\"#00FF00\" },{ \"height\":16, \"width\":22, \"color\":\"#00FFFF\" },{ \"height\":16, \"width\":23, \"color\":\"#FFFF00\" },{ \"height\":16, \"width\":24, \"color\":\"#808080\" },{ \"height\":16, \"width\":25, \"color\":\"#808080\" },{ \"height\":16, \"width\":26, \"color\":\"#00FF00\" },{ \"height\":16, \"width\":27, \"color\":\"#808080\" },{ \"height\":16, \"width\":28, \"color\":\"#808080\" },{ \"height\":16, \"width\":29, \"color\":\"#808080\" },{ \"height\":16, \"width\":30, \"color\":\"#808080\" },{ \"height\":16, \"width\":31, \"color\":\"#808080\" },{ \"height\":16, \"width\":32, \"color\":\"#808080\" },{ \"height\":16, \"width\":33, \"color\":\"#808080\" },{ \"height\":16, \"width\":34, \"color\":\"#00FFFF\" },{ \"height\":16, \"width\":35, \"color\":\"#808080\" },{ \"height\":16, \"width\":36, \"color\":\"#808080\" },{ \"height\":16, \"width\":37, \"color\":\"#00FF00\" },{ \"height\":16, \"width\":38, \"color\":\"#808080\" },{ \"height\":16, \"width\":39, \"color\":\"#808080\" },{ \"height\":16, \"width\":40, \"color\":\"#00FF00\" },{ \"height\":16, \"width\":41, \"color\":\"#808080\" },{ \"height\":16, \"width\":42, \"color\":\"#808080\" },{ \"height\":16, \"width\":43, \"color\":\"#808080\" },{ \"height\":16, \"width\":44, \"color\":\"#00FF00\" },{ \"height\":16, \"width\":45, \"color\":\"#808080\" },{ \"height\":16, \"width\":46, \"color\":\"#808080\" },{ \"height\":16, \"width\":47, \"color\":\"#FFFF00\" },{ \"height\":16, \"width\":48, \"color\":\"#00FFFF\" },{ \"height\":16, \"width\":49, \"color\":\"#FFFF00\" },{ \"height\":16, \"width\":50, \"color\":\"#808080\" },{ \"height\":16, \"width\":51, \"color\":\"#00FF00\" },{ \"height\":16, \"width\":52, \"color\":\"#00FF00\" },{ \"height\":16, \"width\":53, \"color\":\"#808080\" },{ \"height\":16, \"width\":54, \"color\":\"#FFFF00\" },{ \"height\":16, \"width\":55, \"color\":\"#808080\" },\n" +
            "{ \"height\":17, \"width\":0, \"color\":\"#808080\" },{ \"height\":17, \"width\":1, \"color\":\"#FF0000\" },{ \"height\":17, \"width\":2, \"color\":\"#00FFFF\" },{ \"height\":17, \"width\":3, \"color\":\"#00FFFF\" },{ \"height\":17, \"width\":4, \"color\":\"#00FF00\" },{ \"height\":17, \"width\":5, \"color\":\"#808080\" },{ \"height\":17, \"width\":6, \"color\":\"#00FFFF\" },{ \"height\":17, \"width\":7, \"color\":\"#00FF00\" },{ \"height\":17, \"width\":8, \"color\":\"#FFFF00\" },{ \"height\":17, \"width\":9, \"color\":\"#FFFF00\" },{ \"height\":17, \"width\":10, \"color\":\"#808080\" },{ \"height\":17, \"width\":11, \"color\":\"#00FF00\" },{ \"height\":17, \"width\":12, \"color\":\"#00FF00\" },{ \"height\":17, \"width\":13, \"color\":\"#00FF00\" },{ \"height\":17, \"width\":14, \"color\":\"#808080\" },{ \"height\":17, \"width\":15, \"color\":\"#FFFF00\" },{ \"height\":17, \"width\":16, \"color\":\"#00FFFF\" },{ \"height\":17, \"width\":17, \"color\":\"#808080\" },{ \"height\":17, \"width\":18, \"color\":\"#808080\" },{ \"height\":17, \"width\":19, \"color\":\"#00FFFF\" },{ \"height\":17, \"width\":20, \"color\":\"#FF0000\" },{ \"height\":17, \"width\":21, \"color\":\"#00FF00\" },{ \"height\":17, \"width\":22, \"color\":\"#808080\" },{ \"height\":17, \"width\":23, \"color\":\"#808080\" },{ \"height\":17, \"width\":24, \"color\":\"#FF0000\" },{ \"height\":17, \"width\":25, \"color\":\"#808080\" },{ \"height\":17, \"width\":26, \"color\":\"#00FFFF\" },{ \"height\":17, \"width\":27, \"color\":\"#808080\" },{ \"height\":17, \"width\":28, \"color\":\"#00FF00\" },{ \"height\":17, \"width\":29, \"color\":\"#808080\" },{ \"height\":17, \"width\":30, \"color\":\"#808080\" },{ \"height\":17, \"width\":31, \"color\":\"#FF0000\" },{ \"height\":17, \"width\":32, \"color\":\"#00FF00\" },{ \"height\":17, \"width\":33, \"color\":\"#808080\" },{ \"height\":17, \"width\":34, \"color\":\"#00FFFF\" },{ \"height\":17, \"width\":35, \"color\":\"#808080\" },{ \"height\":17, \"width\":36, \"color\":\"#FF0000\" },{ \"height\":17, \"width\":37, \"color\":\"#00FFFF\" },{ \"height\":17, \"width\":38, \"color\":\"#808080\" },{ \"height\":17, \"width\":39, \"color\":\"#808080\" },{ \"height\":17, \"width\":40, \"color\":\"#00FF00\" },{ \"height\":17, \"width\":41, \"color\":\"#808080\" },{ \"height\":17, \"width\":42, \"color\":\"#FFFF00\" },{ \"height\":17, \"width\":43, \"color\":\"#808080\" },{ \"height\":17, \"width\":44, \"color\":\"#808080\" },{ \"height\":17, \"width\":45, \"color\":\"#00FFFF\" },{ \"height\":17, \"width\":46, \"color\":\"#00FF00\" },{ \"height\":17, \"width\":47, \"color\":\"#00FF00\" },{ \"height\":17, \"width\":48, \"color\":\"#808080\" },{ \"height\":17, \"width\":49, \"color\":\"#00FF00\" },{ \"height\":17, \"width\":50, \"color\":\"#FFFF00\" },{ \"height\":17, \"width\":51, \"color\":\"#808080\" },{ \"height\":17, \"width\":52, \"color\":\"#808080\" },{ \"height\":17, \"width\":53, \"color\":\"#00FF00\" },{ \"height\":17, \"width\":54, \"color\":\"#808080\" },{ \"height\":17, \"width\":55, \"color\":\"#00FF00\" },\n" +
            "{ \"height\":18, \"width\":0, \"color\":\"#808080\" },{ \"height\":18, \"width\":1, \"color\":\"#00FF00\" },{ \"height\":18, \"width\":2, \"color\":\"#FFFF00\" },{ \"height\":18, \"width\":3, \"color\":\"#808080\" },{ \"height\":18, \"width\":4, \"color\":\"#808080\" },{ \"height\":18, \"width\":5, \"color\":\"#00FFFF\" },{ \"height\":18, \"width\":6, \"color\":\"#808080\" },{ \"height\":18, \"width\":7, \"color\":\"#FF0000\" },{ \"height\":18, \"width\":8, \"color\":\"#00FF00\" },{ \"height\":18, \"width\":9, \"color\":\"#808080\" },{ \"height\":18, \"width\":10, \"color\":\"#808080\" },{ \"height\":18, \"width\":11, \"color\":\"#00FF00\" },{ \"height\":18, \"width\":12, \"color\":\"#808080\" },{ \"height\":18, \"width\":13, \"color\":\"#808080\" },{ \"height\":18, \"width\":14, \"color\":\"#808080\" },{ \"height\":18, \"width\":15, \"color\":\"#00FF00\" },{ \"height\":18, \"width\":16, \"color\":\"#00FFFF\" },{ \"height\":18, \"width\":17, \"color\":\"#00FF00\" },{ \"height\":18, \"width\":18, \"color\":\"#808080\" },{ \"height\":18, \"width\":19, \"color\":\"#00FF00\" },{ \"height\":18, \"width\":20, \"color\":\"#808080\" },{ \"height\":18, \"width\":21, \"color\":\"#808080\" },{ \"height\":18, \"width\":22, \"color\":\"#FFFF00\" },{ \"height\":18, \"width\":23, \"color\":\"#808080\" },{ \"height\":18, \"width\":24, \"color\":\"#FFFF00\" },{ \"height\":18, \"width\":25, \"color\":\"#808080\" },{ \"height\":18, \"width\":26, \"color\":\"#00FFFF\" },{ \"height\":18, \"width\":27, \"color\":\"#00FF00\" },{ \"height\":18, \"width\":28, \"color\":\"#00FFFF\" },{ \"height\":18, \"width\":29, \"color\":\"#808080\" },{ \"height\":18, \"width\":30, \"color\":\"#00FF00\" },{ \"height\":18, \"width\":31, \"color\":\"#00FF00\" },{ \"height\":18, \"width\":32, \"color\":\"#00FF00\" },{ \"height\":18, \"width\":33, \"color\":\"#808080\" },{ \"height\":18, \"width\":34, \"color\":\"#FFFF00\" },{ \"height\":18, \"width\":35, \"color\":\"#00FF00\" },{ \"height\":18, \"width\":36, \"color\":\"#FF0000\" },{ \"height\":18, \"width\":37, \"color\":\"#808080\" },{ \"height\":18, \"width\":38, \"color\":\"#808080\" },{ \"height\":18, \"width\":39, \"color\":\"#808080\" },{ \"height\":18, \"width\":40, \"color\":\"#808080\" },{ \"height\":18, \"width\":41, \"color\":\"#808080\" },{ \"height\":18, \"width\":42, \"color\":\"#808080\" },{ \"height\":18, \"width\":43, \"color\":\"#808080\" },{ \"height\":18, \"width\":44, \"color\":\"#808080\" },{ \"height\":18, \"width\":45, \"color\":\"#00FF00\" },{ \"height\":18, \"width\":46, \"color\":\"#808080\" },{ \"height\":18, \"width\":47, \"color\":\"#FFFF00\" },{ \"height\":18, \"width\":48, \"color\":\"#00FFFF\" },{ \"height\":18, \"width\":49, \"color\":\"#FFFF00\" },{ \"height\":18, \"width\":50, \"color\":\"#00FF00\" },{ \"height\":18, \"width\":51, \"color\":\"#808080\" },{ \"height\":18, \"width\":52, \"color\":\"#00FF00\" },{ \"height\":18, \"width\":53, \"color\":\"#00FFFF\" },{ \"height\":18, \"width\":54, \"color\":\"#808080\" },{ \"height\":18, \"width\":55, \"color\":\"#808080\" },\n" +
            "{ \"height\":19, \"width\":0, \"color\":\"#808080\" },{ \"height\":19, \"width\":1, \"color\":\"#00FFFF\" },{ \"height\":19, \"width\":2, \"color\":\"#808080\" },{ \"height\":19, \"width\":3, \"color\":\"#00FFFF\" },{ \"height\":19, \"width\":4, \"color\":\"#808080\" },{ \"height\":19, \"width\":5, \"color\":\"#FFFF00\" },{ \"height\":19, \"width\":6, \"color\":\"#808080\" },{ \"height\":19, \"width\":7, \"color\":\"#808080\" },{ \"height\":19, \"width\":8, \"color\":\"#00FFFF\" },{ \"height\":19, \"width\":9, \"color\":\"#FFFF00\" },{ \"height\":19, \"width\":10, \"color\":\"#00FF00\" },{ \"height\":19, \"width\":11, \"color\":\"#FF0000\" },{ \"height\":19, \"width\":12, \"color\":\"#FFFF00\" },{ \"height\":19, \"width\":13, \"color\":\"#FFFF00\" },{ \"height\":19, \"width\":14, \"color\":\"#808080\" },{ \"height\":19, \"width\":15, \"color\":\"#00FF00\" },{ \"height\":19, \"width\":16, \"color\":\"#00FF00\" },{ \"height\":19, \"width\":17, \"color\":\"#808080\" },{ \"height\":19, \"width\":18, \"color\":\"#808080\" },{ \"height\":19, \"width\":19, \"color\":\"#FF0000\" },{ \"height\":19, \"width\":20, \"color\":\"#00FFFF\" },{ \"height\":19, \"width\":21, \"color\":\"#FF0000\" },{ \"height\":19, \"width\":22, \"color\":\"#00FF00\" },{ \"height\":19, \"width\":23, \"color\":\"#808080\" },{ \"height\":19, \"width\":24, \"color\":\"#808080\" },{ \"height\":19, \"width\":25, \"color\":\"#808080\" },{ \"height\":19, \"width\":26, \"color\":\"#00FF00\" },{ \"height\":19, \"width\":27, \"color\":\"#808080\" },{ \"height\":19, \"width\":28, \"color\":\"#808080\" },{ \"height\":19, \"width\":29, \"color\":\"#FFFF00\" },{ \"height\":19, \"width\":30, \"color\":\"#808080\" },{ \"height\":19, \"width\":31, \"color\":\"#808080\" },{ \"height\":19, \"width\":32, \"color\":\"#FFFF00\" },{ \"height\":19, \"width\":33, \"color\":\"#808080\" },{ \"height\":19, \"width\":34, \"color\":\"#808080\" },{ \"height\":19, \"width\":35, \"color\":\"#FFFF00\" },{ \"height\":19, \"width\":36, \"color\":\"#808080\" },{ \"height\":19, \"width\":37, \"color\":\"#808080\" },{ \"height\":19, \"width\":38, \"color\":\"#808080\" },{ \"height\":19, \"width\":39, \"color\":\"#808080\" },{ \"height\":19, \"width\":40, \"color\":\"#FFFF00\" },{ \"height\":19, \"width\":41, \"color\":\"#808080\" },{ \"height\":19, \"width\":42, \"color\":\"#FFFF00\" },{ \"height\":19, \"width\":43, \"color\":\"#808080\" },{ \"height\":19, \"width\":44, \"color\":\"#808080\" },{ \"height\":19, \"width\":45, \"color\":\"#00FFFF\" },{ \"height\":19, \"width\":46, \"color\":\"#808080\" },{ \"height\":19, \"width\":47, \"color\":\"#808080\" },{ \"height\":19, \"width\":48, \"color\":\"#00FFFF\" },{ \"height\":19, \"width\":49, \"color\":\"#00FF00\" },{ \"height\":19, \"width\":50, \"color\":\"#FFFF00\" },{ \"height\":19, \"width\":51, \"color\":\"#00FFFF\" },{ \"height\":19, \"width\":52, \"color\":\"#00FF00\" },{ \"height\":19, \"width\":53, \"color\":\"#808080\" },{ \"height\":19, \"width\":54, \"color\":\"#00FFFF\" },{ \"height\":19, \"width\":55, \"color\":\"#FFFF00\" },\n" +
            "{ \"height\":20, \"width\":0, \"color\":\"#00FF00\" },{ \"height\":20, \"width\":1, \"color\":\"#00FF00\" },{ \"height\":20, \"width\":2, \"color\":\"#00FF00\" },{ \"height\":20, \"width\":3, \"color\":\"#00FF00\" },{ \"height\":20, \"width\":4, \"color\":\"#808080\" },{ \"height\":20, \"width\":5, \"color\":\"#00FF00\" },{ \"height\":20, \"width\":6, \"color\":\"#00FFFF\" },{ \"height\":20, \"width\":7, \"color\":\"#FFFF00\" },{ \"height\":20, \"width\":8, \"color\":\"#FFFF00\" },{ \"height\":20, \"width\":9, \"color\":\"#808080\" },{ \"height\":20, \"width\":10, \"color\":\"#808080\" },{ \"height\":20, \"width\":11, \"color\":\"#808080\" },{ \"height\":20, \"width\":12, \"color\":\"#808080\" },{ \"height\":20, \"width\":13, \"color\":\"#FFFF00\" },{ \"height\":20, \"width\":14, \"color\":\"#808080\" },{ \"height\":20, \"width\":15, \"color\":\"#FFFF00\" },{ \"height\":20, \"width\":16, \"color\":\"#FFFF00\" },{ \"height\":20, \"width\":17, \"color\":\"#808080\" },{ \"height\":20, \"width\":18, \"color\":\"#00FFFF\" },{ \"height\":20, \"width\":19, \"color\":\"#808080\" },{ \"height\":20, \"width\":20, \"color\":\"#00FF00\" },{ \"height\":20, \"width\":21, \"color\":\"#00FF00\" },{ \"height\":20, \"width\":22, \"color\":\"#FF0000\" },{ \"height\":20, \"width\":23, \"color\":\"#808080\" },{ \"height\":20, \"width\":24, \"color\":\"#808080\" },{ \"height\":20, \"width\":25, \"color\":\"#00FF00\" },{ \"height\":20, \"width\":26, \"color\":\"#00FFFF\" },{ \"height\":20, \"width\":27, \"color\":\"#808080\" },{ \"height\":20, \"width\":28, \"color\":\"#808080\" },{ \"height\":20, \"width\":29, \"color\":\"#00FF00\" },{ \"height\":20, \"width\":30, \"color\":\"#808080\" },{ \"height\":20, \"width\":31, \"color\":\"#808080\" },{ \"height\":20, \"width\":32, \"color\":\"#00FFFF\" },{ \"height\":20, \"width\":33, \"color\":\"#808080\" },{ \"height\":20, \"width\":34, \"color\":\"#00FF00\" },{ \"height\":20, \"width\":35, \"color\":\"#00FF00\" },{ \"height\":20, \"width\":36, \"color\":\"#808080\" },{ \"height\":20, \"width\":37, \"color\":\"#808080\" },{ \"height\":20, \"width\":38, \"color\":\"#00FFFF\" },{ \"height\":20, \"width\":39, \"color\":\"#808080\" },{ \"height\":20, \"width\":40, \"color\":\"#00FFFF\" },{ \"height\":20, \"width\":41, \"color\":\"#808080\" },{ \"height\":20, \"width\":42, \"color\":\"#00FFFF\" },{ \"height\":20, \"width\":43, \"color\":\"#00FF00\" },{ \"height\":20, \"width\":44, \"color\":\"#FFFF00\" },{ \"height\":20, \"width\":45, \"color\":\"#00FFFF\" },{ \"height\":20, \"width\":46, \"color\":\"#808080\" },{ \"height\":20, \"width\":47, \"color\":\"#00FF00\" },{ \"height\":20, \"width\":48, \"color\":\"#808080\" },{ \"height\":20, \"width\":49, \"color\":\"#808080\" },{ \"height\":20, \"width\":50, \"color\":\"#00FFFF\" },{ \"height\":20, \"width\":51, \"color\":\"#FFFF00\" },{ \"height\":20, \"width\":52, \"color\":\"#FFFF00\" },{ \"height\":20, \"width\":53, \"color\":\"#808080\" },{ \"height\":20, \"width\":54, \"color\":\"#FFFF00\" },{ \"height\":20, \"width\":55, \"color\":\"#FFFF00\" },\n" +
            "{ \"height\":21, \"width\":0, \"color\":\"#FFFF00\" },{ \"height\":21, \"width\":1, \"color\":\"#FFFF00\" },{ \"height\":21, \"width\":2, \"color\":\"#FFFF00\" },{ \"height\":21, \"width\":3, \"color\":\"#00FFFF\" },{ \"height\":21, \"width\":4, \"color\":\"#808080\" },{ \"height\":21, \"width\":5, \"color\":\"#00FF00\" },{ \"height\":21, \"width\":6, \"color\":\"#FFFF00\" },{ \"height\":21, \"width\":7, \"color\":\"#00FFFF\" },{ \"height\":21, \"width\":8, \"color\":\"#FFFF00\" },{ \"height\":21, \"width\":9, \"color\":\"#808080\" },{ \"height\":21, \"width\":10, \"color\":\"#00FF00\" },{ \"height\":21, \"width\":11, \"color\":\"#808080\" },{ \"height\":21, \"width\":12, \"color\":\"#00FFFF\" },{ \"height\":21, \"width\":13, \"color\":\"#808080\" },{ \"height\":21, \"width\":14, \"color\":\"#00FFFF\" },{ \"height\":21, \"width\":15, \"color\":\"#00FFFF\" },{ \"height\":21, \"width\":16, \"color\":\"#808080\" },{ \"height\":21, \"width\":17, \"color\":\"#00FF00\" },{ \"height\":21, \"width\":18, \"color\":\"#00FFFF\" },{ \"height\":21, \"width\":19, \"color\":\"#00FF00\" },{ \"height\":21, \"width\":20, \"color\":\"#00FFFF\" },{ \"height\":21, \"width\":21, \"color\":\"#808080\" },{ \"height\":21, \"width\":22, \"color\":\"#FF0000\" },{ \"height\":21, \"width\":23, \"color\":\"#00FF00\" },{ \"height\":21, \"width\":24, \"color\":\"#808080\" },{ \"height\":21, \"width\":25, \"color\":\"#00FF00\" },{ \"height\":21, \"width\":26, \"color\":\"#808080\" },{ \"height\":21, \"width\":27, \"color\":\"#00FF00\" },{ \"height\":21, \"width\":28, \"color\":\"#808080\" },{ \"height\":21, \"width\":29, \"color\":\"#00FFFF\" },{ \"height\":21, \"width\":30, \"color\":\"#808080\" },{ \"height\":21, \"width\":31, \"color\":\"#808080\" },{ \"height\":21, \"width\":32, \"color\":\"#00FFFF\" },{ \"height\":21, \"width\":33, \"color\":\"#FFFF00\" },{ \"height\":21, \"width\":34, \"color\":\"#00FFFF\" },{ \"height\":21, \"width\":35, \"color\":\"#00FF00\" },{ \"height\":21, \"width\":36, \"color\":\"#808080\" },{ \"height\":21, \"width\":37, \"color\":\"#FFFF00\" },{ \"height\":21, \"width\":38, \"color\":\"#808080\" },{ \"height\":21, \"width\":39, \"color\":\"#808080\" },{ \"height\":21, \"width\":40, \"color\":\"#FFFF00\" },{ \"height\":21, \"width\":41, \"color\":\"#808080\" },{ \"height\":21, \"width\":42, \"color\":\"#808080\" },{ \"height\":21, \"width\":43, \"color\":\"#FF0000\" },{ \"height\":21, \"width\":44, \"color\":\"#00FF00\" },{ \"height\":21, \"width\":45, \"color\":\"#808080\" },{ \"height\":21, \"width\":46, \"color\":\"#808080\" },{ \"height\":21, \"width\":47, \"color\":\"#808080\" },{ \"height\":21, \"width\":48, \"color\":\"#00FF00\" },{ \"height\":21, \"width\":49, \"color\":\"#808080\" },{ \"height\":21, \"width\":50, \"color\":\"#00FF00\" },{ \"height\":21, \"width\":51, \"color\":\"#00FF00\" },{ \"height\":21, \"width\":52, \"color\":\"#00FF00\" },{ \"height\":21, \"width\":53, \"color\":\"#808080\" },{ \"height\":21, \"width\":54, \"color\":\"#FFFF00\" },{ \"height\":21, \"width\":55, \"color\":\"#808080\" },\n" +
            "{ \"height\":22, \"width\":0, \"color\":\"#FF0000\" },{ \"height\":22, \"width\":1, \"color\":\"#808080\" },{ \"height\":22, \"width\":2, \"color\":\"#00FFFF\" },{ \"height\":22, \"width\":3, \"color\":\"#808080\" },{ \"height\":22, \"width\":4, \"color\":\"#00FF00\" },{ \"height\":22, \"width\":5, \"color\":\"#808080\" },{ \"height\":22, \"width\":6, \"color\":\"#00FF00\" },{ \"height\":22, \"width\":7, \"color\":\"#808080\" },{ \"height\":22, \"width\":8, \"color\":\"#00FF00\" },{ \"height\":22, \"width\":9, \"color\":\"#808080\" },{ \"height\":22, \"width\":10, \"color\":\"#808080\" },{ \"height\":22, \"width\":11, \"color\":\"#FFFF00\" },{ \"height\":22, \"width\":12, \"color\":\"#808080\" },{ \"height\":22, \"width\":13, \"color\":\"#808080\" },{ \"height\":22, \"width\":14, \"color\":\"#FFFF00\" },{ \"height\":22, \"width\":15, \"color\":\"#808080\" },{ \"height\":22, \"width\":16, \"color\":\"#00FF00\" },{ \"height\":22, \"width\":17, \"color\":\"#FF0000\" },{ \"height\":22, \"width\":18, \"color\":\"#808080\" },{ \"height\":22, \"width\":19, \"color\":\"#00FFFF\" },{ \"height\":22, \"width\":20, \"color\":\"#808080\" },{ \"height\":22, \"width\":21, \"color\":\"#808080\" },{ \"height\":22, \"width\":22, \"color\":\"#808080\" },{ \"height\":22, \"width\":23, \"color\":\"#808080\" },{ \"height\":22, \"width\":24, \"color\":\"#00FF00\" },{ \"height\":22, \"width\":25, \"color\":\"#00FFFF\" },{ \"height\":22, \"width\":26, \"color\":\"#808080\" },{ \"height\":22, \"width\":27, \"color\":\"#00FF00\" },{ \"height\":22, \"width\":28, \"color\":\"#00FFFF\" },{ \"height\":22, \"width\":29, \"color\":\"#808080\" },{ \"height\":22, \"width\":30, \"color\":\"#808080\" },{ \"height\":22, \"width\":31, \"color\":\"#808080\" },{ \"height\":22, \"width\":32, \"color\":\"#808080\" },{ \"height\":22, \"width\":33, \"color\":\"#808080\" },{ \"height\":22, \"width\":34, \"color\":\"#808080\" },{ \"height\":22, \"width\":35, \"color\":\"#00FFFF\" },{ \"height\":22, \"width\":36, \"color\":\"#808080\" },{ \"height\":22, \"width\":37, \"color\":\"#00FF00\" },{ \"height\":22, \"width\":38, \"color\":\"#FF0000\" },{ \"height\":22, \"width\":39, \"color\":\"#FFFF00\" },{ \"height\":22, \"width\":40, \"color\":\"#808080\" },{ \"height\":22, \"width\":41, \"color\":\"#00FFFF\" },{ \"height\":22, \"width\":42, \"color\":\"#00FF00\" },{ \"height\":22, \"width\":43, \"color\":\"#808080\" },{ \"height\":22, \"width\":44, \"color\":\"#00FFFF\" },{ \"height\":22, \"width\":45, \"color\":\"#00FFFF\" },{ \"height\":22, \"width\":46, \"color\":\"#808080\" },{ \"height\":22, \"width\":47, \"color\":\"#808080\" },{ \"height\":22, \"width\":48, \"color\":\"#00FF00\" },{ \"height\":22, \"width\":49, \"color\":\"#808080\" },{ \"height\":22, \"width\":50, \"color\":\"#00FFFF\" },{ \"height\":22, \"width\":51, \"color\":\"#00FFFF\" },{ \"height\":22, \"width\":52, \"color\":\"#808080\" },{ \"height\":22, \"width\":53, \"color\":\"#FFFF00\" },{ \"height\":22, \"width\":54, \"color\":\"#FFFF00\" },{ \"height\":22, \"width\":55, \"color\":\"#808080\" }\n" +
            "\n" +
            "        ]\n" +
            "    }\n" +
            "}");
    return test;
  }
}
