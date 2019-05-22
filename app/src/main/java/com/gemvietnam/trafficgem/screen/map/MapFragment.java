package com.gemvietnam.trafficgem.screen.map;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.gemvietnam.base.viper.ViewFragment;
import com.gemvietnam.trafficgem.R;
import com.gemvietnam.trafficgem.library.MySupportMapFragment;
import com.gemvietnam.trafficgem.library.Point;
import com.gemvietnam.Constants;
import com.gemvietnam.trafficgem.library.Report;
import com.gemvietnam.trafficgem.library.User;
import com.gemvietnam.trafficgem.library.responseMessage.CurrentTrafficResponse;
import com.gemvietnam.trafficgem.library.responseMessage.GetReportResponse;
import com.gemvietnam.trafficgem.screen.leftmenu.MenuItem;
import com.gemvietnam.trafficgem.screen.leftmenu.OnMenuItemClickedListener;
import com.gemvietnam.trafficgem.screen.main.MainActivity;
import com.gemvietnam.trafficgem.service.DataExchange;
import com.gemvietnam.trafficgem.utils.AppUtils;
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
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
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
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static android.content.Context.LOCATION_SERVICE;
import static com.gemvietnam.trafficgem.library.responseMessage.Constants.Message;
import static com.gemvietnam.trafficgem.utils.Constants.IDMsg;
import static com.gemvietnam.trafficgem.utils.Constants.LAST_USER;
import static com.gemvietnam.trafficgem.utils.Constants.LOGIN_TIME_FORMAT;
import static com.gemvietnam.trafficgem.utils.Constants.Latitude;
import static com.gemvietnam.trafficgem.utils.Constants.Longitude;
import static com.gemvietnam.trafficgem.utils.Constants.Period_Time;
import static com.gemvietnam.trafficgem.utils.Constants.Range;
import static com.gemvietnam.trafficgem.utils.Constants.SUCCESS;
import static com.gemvietnam.trafficgem.utils.Constants.Time_Stamp;

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
    @BindView(R.id.ll_get_report)
    LinearLayout llGetReport;
    @BindView(R.id.spinner_range)
    Spinner sRange;
    @BindView(R.id.spinner_period)
    Spinner sPeriod;
    @BindView(R.id.get_report_img)
    ImageView ivGetReport;


    private GoogleMap mMap;
    private boolean displayCurrentMarkerPosition = true;
    private boolean holdMarker = true;
    private ProgressDialog mprogress;
    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 1;
    private static final String TAG = "TAG: ";
    private static final int LOCATION_CODE_AUTOCOMPLETE = 10;
    private static final int ORIGIN_CODE_AUTOCOMPLETE = 11;
    private static final int DESTINATION_CODE_AUTOCOMPLETE = 12;
    private static int PLACE_PICKER_REQUEST = 2;
    private List<Polyline> polylines;
    private int height, width;
    private Point[][] points;
    private String[][] colors;
    public static boolean mMapIsTouched = false;
    private int LAYER = 1;
    private String getLayer1, getLayer2, getLayer3, getLayer4;
    private boolean trafficStateFlag = false;
    private double north = 21.16;
    private double south = 20.95;
    private double east = 105.92;
    private double west = 105.46;
    //    private double delLat = 0.0089573;
//    private double delLon = 0.0088355;
    private double delLat, delLon;
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

        // Khởi tạo tham số spinner chọn range và period ở đây
        setupSpinners();

        if (Build.VERSION.SDK_INT >= 23) {
            checkPermissions();
        }
        mLocationSearchCv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openAutoCompleteActivity();
            }
        });

        // sau khi chọn range với period thì click ở đây để get
        ivGetReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                processGet();
            }
        });
    }

    private void setupSpinners() {
        sPeriod.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        sRange.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        List<String> range = new ArrayList<>();
        range.add("500 m");
        range.add("1 km");
        range.add("2 km");
        range.add("5 km");
        List<String> period = new ArrayList<>();
        period.add("10 minutes");
        period.add("20 minutes");
        period.add("30 minutes");

        ArrayAdapter<String> adapterRange = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, range);
        ArrayAdapter<String> adapterPeriod = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, period);

        // Drop down layout style - list view with radio button
        adapterRange.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapterPeriod.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        sRange.setAdapter(adapterRange);
        sPeriod.setAdapter(adapterPeriod);
    }

    /**
     * thực hiện get report và show map
     */
    private void processGet() {
        if(!AppUtils.networkOk(getViewContext())){
            Toast.makeText(getViewContext(), "NO INTERNET !!!", Toast.LENGTH_LONG).show();
            return;
        }
        String period_time = sPeriod.getSelectedItem().toString();
        String timeValue = "10";
        if(period_time.equals("10 minutes")){
            timeValue = "10";
        }   else if(period_time.equals("20 minutes")){
            timeValue = "20";
        }   else if(period_time.equals("30 minutes")){
            timeValue = "30";
        }
        User user = Hawk.get(LAST_USER);
        DataExchange dataExchange = new DataExchange();
        String response = dataExchange.getReport(user.getToken(), timeValue);
        // thêm biến để xem viền của map (giống với traffic state)
        Location location = getLastKnownLocation();
        if(location == null)    return;
        double north = location.getLatitude(), west = location.getLongitude(), east = location.getLongitude(), south = location.getLatitude();
//        response = demoGetReportResponse();
        GetReportResponse getReportResponse = new GetReportResponse(response);
        getReportResponse.analysis();
        if(getReportResponse.getSuccess()) {
            Report[] reports = getReportResponse.getReport();
            int length = reports.length;
            for(int i=0; i<length; i++) {
                if (north < reports[i].getLatitude()) {
                    north = reports[i].getLatitude();
                }
                if (south > reports[i].getLatitude()) {
                    south = reports[i].getLatitude();
                }
                if (west > reports[i].getLongitude()) {
                    west = reports[i].getLongitude();
                }
                if (east < reports[i].getLongitude()) {
                    east = reports[i].getLongitude();
                }
                MarkerOptions markerOptions = new MarkerOptions()
                        .position(new LatLng(reports[i].getLatitude(), reports[i].getLongitude()));
                // tạm thời để màu khác nhau, nếu có icon thì thay. mình cũng không biết id tương ứng với cái gì
                switch (reports[i].getIDMsg()) {
                    case 1:
                        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE));
                        break;
                    case 2:
                        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                        break;
                    case 3:
                        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
                        break;
                    case 4:
                        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN));
                        break;
                    case 5:
                        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET));
                        break;
                    case 6:
                        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
                        break;
                    case 7:
                        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));
                        break;
//                    default:
//                        markerOptions.icon(BitmapDescriptorFactory.defaultMarker());
                }
                mMap.addMarker(markerOptions);
            }
        }

        // show map thôi
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

        for (int i = 0; i < height; i++) {
            int k = 1;
            for (int j = 0; j < width; j++) {
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
                points[i][j].setDelLat(delLat);
                points[i][j].setDelLon(delLon);
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
    }

    private int transform(String s) {
        if(s == null) return getResources().getColor(R.color.gray);
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
                return getResources().getColor(R.color.gray);
        }
    }

    private void setupListCell() {
        for (int i = 0; i < height; i++) {
            for (int j = 1; j < width; j++) {
                PolygonOptions polygonOptions = new PolygonOptions();
                polygonOptions.addAll(points[i][j].getPeaks());
                polygonOptions.strokeWidth(0);
                Polygon polygon = mMap.addPolygon(polygonOptions);
                polygon.setFillColor(points[i][j].getColor());
            }
        }
    }

    private void showTrafficState() {
        llGetReport.setVisibility(View.GONE);
        mMap.clear();
        if(!AppUtils.networkOk(getViewContext())){
            Toast.makeText(getViewContext(), "NO INTERNET !!!", Toast.LENGTH_LONG).show();
            return;
        }
        User user = Hawk.get(LAST_USER);
        DataExchange trafficState = new DataExchange();
        getLayer1 = trafficState.getCurrent(user.getToken(), 1);
        getLayer2 = trafficState.getCurrent(user.getToken(), 2);
        getLayer3 = trafficState.getCurrent(user.getToken(), 3);
//        getLayer4 = trafficState.getCurrent(user.getToken(), 4);
        Log.d("test-traffic-state", getLayer1);
        Log.d("test-traffic-state", getLayer2);
        Log.d("test-traffic-state", getLayer3);
//        Log.d("test-traffic-state", getLayer4);
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

    private void setTrafficState(int layer){
        String getResponse = getLayer1;
        switch (layer){
            case 1: getResponse = getLayer1;
                break;
            case 2: getResponse = getLayer2;
                break;
            case 3: getResponse = getLayer3;
//                break;
//            case 4: getResponse = getLayer4;
        }
        CurrentTrafficResponse trafficResponse = new CurrentTrafficResponse(getResponse);
        trafficResponse.analysis();
        if(!trafficResponse.getSuccess()){
            return;
        }
        height = trafficResponse.getHeight();
        width = trafficResponse.getWidth();
        Log.d("height-width", String.valueOf(height));
        Log.d("height-width", String.valueOf(width));
        double north = trafficResponse.getNorth();
        double south = trafficResponse.getSouth();
        double west = trafficResponse.getWest();
        double east = trafficResponse.getEast();
        delLat = (east - west) / width;
        delLon = (north - south) / height;
        points = new Point[height][width];
//    colors = new String[height][width];
//    colors = setupListColor();
        colors = trafficResponse.getColorArray();
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

    }
    private void showLocation() {
        mLocationSearchCv.setVisibility(View.VISIBLE);
        mSearchCancelImg.setVisibility(View.GONE);
        llGetReport.setVisibility(View.GONE);
        mGridview.setVisibility(View.GONE);
        mMap.clear();
        getCurrentLocation();
    }

    private void showReport() {
        mLocationSearchCv.setVisibility(View.GONE);
        llGetReport.setVisibility(View.VISIBLE);
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
                    //.bearing(90)                // Sets the orientation of the camera to east
                    //.tilt(40)                   // Sets the tilt of the camera to 30 degrees
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
                offTrafficState();
                onDisplayCurrentMarkerPosition();
                offHoldMarker();
                showLocation();
                break;
            case Constants.NORMAL:
                offTrafficState();
                onDisplayCurrentMarkerPosition();
                offHoldMarker();
                break;
            case Constants.ADVANCE:
                offTrafficState();
                onDisplayCurrentMarkerPosition();
                offHoldMarker();
                break;
            case Constants.TRAFFIC:
                onTrafficState();
                offDisplayCurrentMarkerPosition();
                showTrafficState();
                break;
            case Constants.GET:
                offTrafficState();
                offDisplayCurrentMarkerPosition();
                showReport();
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
                    if(displayCurrentMarkerPosition){
                        mMap.addMarker(new MarkerOptions().position(loc));
                    }
                    if(!holdMarker){
                        mMap.clear();
                        mMap.addMarker(new MarkerOptions().position(loc));
                    }
                }
            }
        };
        mMap.setOnMyLocationChangeListener(myLocationChangeListener);
        final int[] layerTemp = {2};
        if(trafficStateFlag){
            googleMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
                @Override
                public void onCameraIdle() {
                    int zoomLevel = Math.round(mMap.getCameraPosition().zoom);
                    if(zoomLevel < 13)  LAYER = 1;
                    else if(zoomLevel > 12 && zoomLevel < 17)   LAYER = 2;
                    else if(zoomLevel > 16 )   LAYER = 3;
                    if(layerTemp[0] != LAYER){
                        layerTemp[0] = LAYER;
                        mMap.clear();
                        Log.d("ZOOM", String.valueOf(zoomLevel));
                        setTrafficState(LAYER);
                    }
                }
            });
        }
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
                    new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude()), 15));
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

    private void onHoldMarker(){ holdMarker = true;}

    private void offHoldMarker(){ holdMarker = false;}

    private void onTrafficState(){ trafficStateFlag = true; }

    private void offTrafficState(){ trafficStateFlag = false; }

    private void onDisplayCurrentMarkerPosition(){ displayCurrentMarkerPosition = true; }

    private void offDisplayCurrentMarkerPosition(){ displayCurrentMarkerPosition = false; }

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

    public String test1(){
        String test = new String("{\n" +
                "    \"success\": true,\n" +
                "    \"message\": \"success\",\n" +
                "    \"data\": {\n" +
                "        \"grid\": {\n" +
                "            \"height\": 6,\n" +
                "            \"width\": 13,\n" +
                "            \"east\": 105.92,\n" +
                "            \"west\": 105.46,\n" +
                "            \"south\": 20.95,\n" +
                "            \"north\": 21.16\n" +
                "        },\n" +
                "        \"cells\": [\n" +
                "            { \"height\":0, \"width\":0, \"color\":\"#808080\" },{ \"height\":0, \"width\":1, \"color\":\"#FF0000\" },{ \"height\":0, \"width\":2, \"color\":\"#FF0000\" },{ \"height\":0, \"width\":3, \"color\":\"#808080\" },{ \"height\":0, \"width\":4, \"color\":\"#808080\" },{ \"height\":0, \"width\":5, \"color\":\"#808080\" },{ \"height\":0, \"width\":6, \"color\":\"#00FF00\" },{ \"height\":0, \"width\":7, \"color\":\"#00FF00\" },{ \"height\":0, \"width\":8, \"color\":\"#00FF00\" },{ \"height\":0, \"width\":9, \"color\":\"#808080\" },{ \"height\":0, \"width\":10, \"color\":\"#FFFF00\" },{ \"height\":0, \"width\":11, \"color\":\"#808080\" },{ \"height\":0, \"width\":12, \"color\":\"#00FF00\" },\n" +
                "{ \"height\":1, \"width\":0, \"color\":\"#808080\" },{ \"height\":1, \"width\":1, \"color\":\"#00FF00\" },{ \"height\":1, \"width\":2, \"color\":\"#00FF00\" },{ \"height\":1, \"width\":3, \"color\":\"#808080\" },{ \"height\":1, \"width\":4, \"color\":\"#00FF00\" },{ \"height\":1, \"width\":5, \"color\":\"#00FFFF\" },{ \"height\":1, \"width\":6, \"color\":\"#808080\" },{ \"height\":1, \"width\":7, \"color\":\"#808080\" },{ \"height\":1, \"width\":8, \"color\":\"#00FF00\" },{ \"height\":1, \"width\":9, \"color\":\"#808080\" },{ \"height\":1, \"width\":10, \"color\":\"#00FFFF\" },{ \"height\":1, \"width\":11, \"color\":\"#00FFFF\" },{ \"height\":1, \"width\":12, \"color\":\"#808080\" },\n" +
                "{ \"height\":2, \"width\":0, \"color\":\"#00FFFF\" },{ \"height\":2, \"width\":1, \"color\":\"#00FF00\" },{ \"height\":2, \"width\":2, \"color\":\"#808080\" },{ \"height\":2, \"width\":3, \"color\":\"#FFFF00\" },{ \"height\":2, \"width\":4, \"color\":\"#808080\" },{ \"height\":2, \"width\":5, \"color\":\"#808080\" },{ \"height\":2, \"width\":6, \"color\":\"#808080\" },{ \"height\":2, \"width\":7, \"color\":\"#00FF00\" },{ \"height\":2, \"width\":8, \"color\":\"#FFFF00\" },{ \"height\":2, \"width\":9, \"color\":\"#00FF00\" },{ \"height\":2, \"width\":10, \"color\":\"#00FFFF\" },{ \"height\":2, \"width\":11, \"color\":\"#FFFF00\" },{ \"height\":2, \"width\":12, \"color\":\"#00FFFF\" },\n" +
                "{ \"height\":3, \"width\":0, \"color\":\"#FFFF00\" },{ \"height\":3, \"width\":1, \"color\":\"#808080\" },{ \"height\":3, \"width\":2, \"color\":\"#FFFF00\" },{ \"height\":3, \"width\":3, \"color\":\"#00FFFF\" },{ \"height\":3, \"width\":4, \"color\":\"#00FF00\" },{ \"height\":3, \"width\":5, \"color\":\"#808080\" },{ \"height\":3, \"width\":6, \"color\":\"#FFFF00\" },{ \"height\":3, \"width\":7, \"color\":\"#00FF00\" },{ \"height\":3, \"width\":8, \"color\":\"#FF0000\" },{ \"height\":3, \"width\":9, \"color\":\"#00FFFF\" },{ \"height\":3, \"width\":10, \"color\":\"#FF0000\" },{ \"height\":3, \"width\":11, \"color\":\"#00FFFF\" },{ \"height\":3, \"width\":12, \"color\":\"#808080\" },\n" +
                "{ \"height\":4, \"width\":0, \"color\":\"#808080\" },{ \"height\":4, \"width\":1, \"color\":\"#808080\" },{ \"height\":4, \"width\":2, \"color\":\"#FFFF00\" },{ \"height\":4, \"width\":3, \"color\":\"#808080\" },{ \"height\":4, \"width\":4, \"color\":\"#FFFF00\" },{ \"height\":4, \"width\":5, \"color\":\"#808080\" },{ \"height\":4, \"width\":6, \"color\":\"#00FF00\" },{ \"height\":4, \"width\":7, \"color\":\"#00FF00\" },{ \"height\":4, \"width\":8, \"color\":\"#00FFFF\" },{ \"height\":4, \"width\":9, \"color\":\"#808080\" },{ \"height\":4, \"width\":10, \"color\":\"#808080\" },{ \"height\":4, \"width\":11, \"color\":\"#808080\" },{ \"height\":4, \"width\":12, \"color\":\"#808080\" },\n" +
                "{ \"height\":5, \"width\":0, \"color\":\"#808080\" },{ \"height\":5, \"width\":1, \"color\":\"#00FFFF\" },{ \"height\":5, \"width\":2, \"color\":\"#00FF00\" },{ \"height\":5, \"width\":3, \"color\":\"#00FF00\" },{ \"height\":5, \"width\":4, \"color\":\"#00FF00\" },{ \"height\":5, \"width\":5, \"color\":\"#808080\" },{ \"height\":5, \"width\":6, \"color\":\"#808080\" },{ \"height\":5, \"width\":7, \"color\":\"#00FFFF\" },{ \"height\":5, \"width\":8, \"color\":\"#FFFF00\" },{ \"height\":5, \"width\":9, \"color\":\"#808080\" },{ \"height\":5, \"width\":10, \"color\":\"#00FFFF\" },{ \"height\":5, \"width\":11, \"color\":\"#00FF00\" },{ \"height\":5, \"width\":12, \"color\":\"#808080\" }\n" +
                "\n" +
                "        ]\n" +
                "    }\n" +
                "}");
        return test;
    }

    public String test2(){
        String test2 = new String("{\n" +
                "    \"success\": true,\n" +
                "    \"message\": \"success\",\n" +
                "    \"data\": {\n" +
                "        \"grid\": {\n" +
                "            \"height\": 12,\n" +
                "            \"width\": 27,\n" +
                "            \"east\": 105.92,\n" +
                "            \"west\": 105.46,\n" +
                "            \"south\": 20.95,\n" +
                "            \"north\": 21.16\n" +
                "        },\n" +
                "        \"cells\": [\n" +
                "            { \"height\":0, \"width\":0, \"color\":\"#808080\" },{ \"height\":0, \"width\":1, \"color\":\"#FF0000\" },{ \"height\":0, \"width\":2, \"color\":\"#FF0000\" },{ \"height\":0, \"width\":3, \"color\":\"#808080\" },{ \"height\":0, \"width\":4, \"color\":\"#808080\" },{ \"height\":0, \"width\":5, \"color\":\"#808080\" },{ \"height\":0, \"width\":6, \"color\":\"#00FF00\" },{ \"height\":0, \"width\":7, \"color\":\"#00FF00\" },{ \"height\":0, \"width\":8, \"color\":\"#00FF00\" },{ \"height\":0, \"width\":9, \"color\":\"#808080\" },{ \"height\":0, \"width\":10, \"color\":\"#FFFF00\" },{ \"height\":0, \"width\":11, \"color\":\"#808080\" },{ \"height\":0, \"width\":12, \"color\":\"#00FF00\" },{ \"height\":0, \"width\":13, \"color\":\"#808080\" },{ \"height\":0, \"width\":14, \"color\":\"#FFFF00\" },{ \"height\":0, \"width\":15, \"color\":\"#808080\" },{ \"height\":0, \"width\":16, \"color\":\"#808080\" },{ \"height\":0, \"width\":17, \"color\":\"#FFFF00\" },{ \"height\":0, \"width\":18, \"color\":\"#00FF00\" },{ \"height\":0, \"width\":19, \"color\":\"#808080\" },{ \"height\":0, \"width\":20, \"color\":\"#808080\" },{ \"height\":0, \"width\":21, \"color\":\"#808080\" },{ \"height\":0, \"width\":22, \"color\":\"#808080\" },{ \"height\":0, \"width\":23, \"color\":\"#FFFF00\" },{ \"height\":0, \"width\":24, \"color\":\"#808080\" },{ \"height\":0, \"width\":25, \"color\":\"#00FFFF\" },{ \"height\":0, \"width\":26, \"color\":\"#FFFF00\" },\n" +
                "{ \"height\":1, \"width\":0, \"color\":\"#808080\" },{ \"height\":1, \"width\":1, \"color\":\"#00FF00\" },{ \"height\":1, \"width\":2, \"color\":\"#00FF00\" },{ \"height\":1, \"width\":3, \"color\":\"#808080\" },{ \"height\":1, \"width\":4, \"color\":\"#00FF00\" },{ \"height\":1, \"width\":5, \"color\":\"#00FFFF\" },{ \"height\":1, \"width\":6, \"color\":\"#808080\" },{ \"height\":1, \"width\":7, \"color\":\"#808080\" },{ \"height\":1, \"width\":8, \"color\":\"#00FF00\" },{ \"height\":1, \"width\":9, \"color\":\"#808080\" },{ \"height\":1, \"width\":10, \"color\":\"#00FFFF\" },{ \"height\":1, \"width\":11, \"color\":\"#00FFFF\" },{ \"height\":1, \"width\":12, \"color\":\"#808080\" },{ \"height\":1, \"width\":13, \"color\":\"#FFFF00\" },{ \"height\":1, \"width\":14, \"color\":\"#00FF00\" },{ \"height\":1, \"width\":15, \"color\":\"#00FFFF\" },{ \"height\":1, \"width\":16, \"color\":\"#00FFFF\" },{ \"height\":1, \"width\":17, \"color\":\"#00FFFF\" },{ \"height\":1, \"width\":18, \"color\":\"#00FF00\" },{ \"height\":1, \"width\":19, \"color\":\"#00FF00\" },{ \"height\":1, \"width\":20, \"color\":\"#FFFF00\" },{ \"height\":1, \"width\":21, \"color\":\"#FFFF00\" },{ \"height\":1, \"width\":22, \"color\":\"#808080\" },{ \"height\":1, \"width\":23, \"color\":\"#00FFFF\" },{ \"height\":1, \"width\":24, \"color\":\"#00FF00\" },{ \"height\":1, \"width\":25, \"color\":\"#808080\" },{ \"height\":1, \"width\":26, \"color\":\"#FFFF00\" },\n" +
                "{ \"height\":2, \"width\":0, \"color\":\"#00FFFF\" },{ \"height\":2, \"width\":1, \"color\":\"#00FF00\" },{ \"height\":2, \"width\":2, \"color\":\"#808080\" },{ \"height\":2, \"width\":3, \"color\":\"#FFFF00\" },{ \"height\":2, \"width\":4, \"color\":\"#808080\" },{ \"height\":2, \"width\":5, \"color\":\"#808080\" },{ \"height\":2, \"width\":6, \"color\":\"#808080\" },{ \"height\":2, \"width\":7, \"color\":\"#00FF00\" },{ \"height\":2, \"width\":8, \"color\":\"#FFFF00\" },{ \"height\":2, \"width\":9, \"color\":\"#00FF00\" },{ \"height\":2, \"width\":10, \"color\":\"#00FFFF\" },{ \"height\":2, \"width\":11, \"color\":\"#FFFF00\" },{ \"height\":2, \"width\":12, \"color\":\"#00FFFF\" },{ \"height\":2, \"width\":13, \"color\":\"#808080\" },{ \"height\":2, \"width\":14, \"color\":\"#808080\" },{ \"height\":2, \"width\":15, \"color\":\"#00FF00\" },{ \"height\":2, \"width\":16, \"color\":\"#808080\" },{ \"height\":2, \"width\":17, \"color\":\"#00FFFF\" },{ \"height\":2, \"width\":18, \"color\":\"#808080\" },{ \"height\":2, \"width\":19, \"color\":\"#00FFFF\" },{ \"height\":2, \"width\":20, \"color\":\"#00FF00\" },{ \"height\":2, \"width\":21, \"color\":\"#00FFFF\" },{ \"height\":2, \"width\":22, \"color\":\"#808080\" },{ \"height\":2, \"width\":23, \"color\":\"#808080\" },{ \"height\":2, \"width\":24, \"color\":\"#00FF00\" },{ \"height\":2, \"width\":25, \"color\":\"#808080\" },{ \"height\":2, \"width\":26, \"color\":\"#808080\" },\n" +
                "{ \"height\":3, \"width\":0, \"color\":\"#FFFF00\" },{ \"height\":3, \"width\":1, \"color\":\"#808080\" },{ \"height\":3, \"width\":2, \"color\":\"#FFFF00\" },{ \"height\":3, \"width\":3, \"color\":\"#00FFFF\" },{ \"height\":3, \"width\":4, \"color\":\"#00FF00\" },{ \"height\":3, \"width\":5, \"color\":\"#808080\" },{ \"height\":3, \"width\":6, \"color\":\"#FFFF00\" },{ \"height\":3, \"width\":7, \"color\":\"#00FF00\" },{ \"height\":3, \"width\":8, \"color\":\"#FF0000\" },{ \"height\":3, \"width\":9, \"color\":\"#00FFFF\" },{ \"height\":3, \"width\":10, \"color\":\"#FF0000\" },{ \"height\":3, \"width\":11, \"color\":\"#00FFFF\" },{ \"height\":3, \"width\":12, \"color\":\"#808080\" },{ \"height\":3, \"width\":13, \"color\":\"#00FF00\" },{ \"height\":3, \"width\":14, \"color\":\"#808080\" },{ \"height\":3, \"width\":15, \"color\":\"#808080\" },{ \"height\":3, \"width\":16, \"color\":\"#808080\" },{ \"height\":3, \"width\":17, \"color\":\"#808080\" },{ \"height\":3, \"width\":18, \"color\":\"#808080\" },{ \"height\":3, \"width\":19, \"color\":\"#00FF00\" },{ \"height\":3, \"width\":20, \"color\":\"#00FF00\" },{ \"height\":3, \"width\":21, \"color\":\"#FFFF00\" },{ \"height\":3, \"width\":22, \"color\":\"#00FFFF\" },{ \"height\":3, \"width\":23, \"color\":\"#808080\" },{ \"height\":3, \"width\":24, \"color\":\"#808080\" },{ \"height\":3, \"width\":25, \"color\":\"#00FFFF\" },{ \"height\":3, \"width\":26, \"color\":\"#00FF00\" },\n" +
                "{ \"height\":4, \"width\":0, \"color\":\"#808080\" },{ \"height\":4, \"width\":1, \"color\":\"#808080\" },{ \"height\":4, \"width\":2, \"color\":\"#FFFF00\" },{ \"height\":4, \"width\":3, \"color\":\"#808080\" },{ \"height\":4, \"width\":4, \"color\":\"#FFFF00\" },{ \"height\":4, \"width\":5, \"color\":\"#808080\" },{ \"height\":4, \"width\":6, \"color\":\"#00FF00\" },{ \"height\":4, \"width\":7, \"color\":\"#00FF00\" },{ \"height\":4, \"width\":8, \"color\":\"#00FFFF\" },{ \"height\":4, \"width\":9, \"color\":\"#808080\" },{ \"height\":4, \"width\":10, \"color\":\"#808080\" },{ \"height\":4, \"width\":11, \"color\":\"#808080\" },{ \"height\":4, \"width\":12, \"color\":\"#808080\" },{ \"height\":4, \"width\":13, \"color\":\"#00FF00\" },{ \"height\":4, \"width\":14, \"color\":\"#808080\" },{ \"height\":4, \"width\":15, \"color\":\"#00FF00\" },{ \"height\":4, \"width\":16, \"color\":\"#00FF00\" },{ \"height\":4, \"width\":17, \"color\":\"#808080\" },{ \"height\":4, \"width\":18, \"color\":\"#808080\" },{ \"height\":4, \"width\":19, \"color\":\"#808080\" },{ \"height\":4, \"width\":20, \"color\":\"#00FFFF\" },{ \"height\":4, \"width\":21, \"color\":\"#00FF00\" },{ \"height\":4, \"width\":22, \"color\":\"#00FF00\" },{ \"height\":4, \"width\":23, \"color\":\"#00FF00\" },{ \"height\":4, \"width\":24, \"color\":\"#00FF00\" },{ \"height\":4, \"width\":25, \"color\":\"#808080\" },{ \"height\":4, \"width\":26, \"color\":\"#FFFF00\" },\n" +
                "{ \"height\":5, \"width\":0, \"color\":\"#808080\" },{ \"height\":5, \"width\":1, \"color\":\"#00FFFF\" },{ \"height\":5, \"width\":2, \"color\":\"#00FF00\" },{ \"height\":5, \"width\":3, \"color\":\"#00FF00\" },{ \"height\":5, \"width\":4, \"color\":\"#00FF00\" },{ \"height\":5, \"width\":5, \"color\":\"#808080\" },{ \"height\":5, \"width\":6, \"color\":\"#808080\" },{ \"height\":5, \"width\":7, \"color\":\"#00FFFF\" },{ \"height\":5, \"width\":8, \"color\":\"#FFFF00\" },{ \"height\":5, \"width\":9, \"color\":\"#808080\" },{ \"height\":5, \"width\":10, \"color\":\"#00FFFF\" },{ \"height\":5, \"width\":11, \"color\":\"#00FF00\" },{ \"height\":5, \"width\":12, \"color\":\"#808080\" },{ \"height\":5, \"width\":13, \"color\":\"#00FF00\" },{ \"height\":5, \"width\":14, \"color\":\"#00FF00\" },{ \"height\":5, \"width\":15, \"color\":\"#808080\" },{ \"height\":5, \"width\":16, \"color\":\"#808080\" },{ \"height\":5, \"width\":17, \"color\":\"#808080\" },{ \"height\":5, \"width\":18, \"color\":\"#00FF00\" },{ \"height\":5, \"width\":19, \"color\":\"#00FF00\" },{ \"height\":5, \"width\":20, \"color\":\"#00FF00\" },{ \"height\":5, \"width\":21, \"color\":\"#808080\" },{ \"height\":5, \"width\":22, \"color\":\"#808080\" },{ \"height\":5, \"width\":23, \"color\":\"#808080\" },{ \"height\":5, \"width\":24, \"color\":\"#FF0000\" },{ \"height\":5, \"width\":25, \"color\":\"#808080\" },{ \"height\":5, \"width\":26, \"color\":\"#00FF00\" },\n" +
                "{ \"height\":6, \"width\":0, \"color\":\"#808080\" },{ \"height\":6, \"width\":1, \"color\":\"#00FFFF\" },{ \"height\":6, \"width\":2, \"color\":\"#00FF00\" },{ \"height\":6, \"width\":3, \"color\":\"#00FF00\" },{ \"height\":6, \"width\":4, \"color\":\"#00FFFF\" },{ \"height\":6, \"width\":5, \"color\":\"#FFFF00\" },{ \"height\":6, \"width\":6, \"color\":\"#00FFFF\" },{ \"height\":6, \"width\":7, \"color\":\"#FFFF00\" },{ \"height\":6, \"width\":8, \"color\":\"#808080\" },{ \"height\":6, \"width\":9, \"color\":\"#00FFFF\" },{ \"height\":6, \"width\":10, \"color\":\"#00FFFF\" },{ \"height\":6, \"width\":11, \"color\":\"#808080\" },{ \"height\":6, \"width\":12, \"color\":\"#808080\" },{ \"height\":6, \"width\":13, \"color\":\"#808080\" },{ \"height\":6, \"width\":14, \"color\":\"#00FFFF\" },{ \"height\":6, \"width\":15, \"color\":\"#808080\" },{ \"height\":6, \"width\":16, \"color\":\"#FF0000\" },{ \"height\":6, \"width\":17, \"color\":\"#808080\" },{ \"height\":6, \"width\":18, \"color\":\"#808080\" },{ \"height\":6, \"width\":19, \"color\":\"#FFFF00\" },{ \"height\":6, \"width\":20, \"color\":\"#00FFFF\" },{ \"height\":6, \"width\":21, \"color\":\"#808080\" },{ \"height\":6, \"width\":22, \"color\":\"#808080\" },{ \"height\":6, \"width\":23, \"color\":\"#808080\" },{ \"height\":6, \"width\":24, \"color\":\"#00FFFF\" },{ \"height\":6, \"width\":25, \"color\":\"#FFFF00\" },{ \"height\":6, \"width\":26, \"color\":\"#808080\" },\n" +
                "{ \"height\":7, \"width\":0, \"color\":\"#00FF00\" },{ \"height\":7, \"width\":1, \"color\":\"#808080\" },{ \"height\":7, \"width\":2, \"color\":\"#00FF00\" },{ \"height\":7, \"width\":3, \"color\":\"#FF0000\" },{ \"height\":7, \"width\":4, \"color\":\"#00FFFF\" },{ \"height\":7, \"width\":5, \"color\":\"#00FF00\" },{ \"height\":7, \"width\":6, \"color\":\"#808080\" },{ \"height\":7, \"width\":7, \"color\":\"#808080\" },{ \"height\":7, \"width\":8, \"color\":\"#00FFFF\" },{ \"height\":7, \"width\":9, \"color\":\"#00FFFF\" },{ \"height\":7, \"width\":10, \"color\":\"#808080\" },{ \"height\":7, \"width\":11, \"color\":\"#FFFF00\" },{ \"height\":7, \"width\":12, \"color\":\"#00FF00\" },{ \"height\":7, \"width\":13, \"color\":\"#FF0000\" },{ \"height\":7, \"width\":14, \"color\":\"#808080\" },{ \"height\":7, \"width\":15, \"color\":\"#FFFF00\" },{ \"height\":7, \"width\":16, \"color\":\"#00FF00\" },{ \"height\":7, \"width\":17, \"color\":\"#808080\" },{ \"height\":7, \"width\":18, \"color\":\"#00FFFF\" },{ \"height\":7, \"width\":19, \"color\":\"#808080\" },{ \"height\":7, \"width\":20, \"color\":\"#808080\" },{ \"height\":7, \"width\":21, \"color\":\"#808080\" },{ \"height\":7, \"width\":22, \"color\":\"#808080\" },{ \"height\":7, \"width\":23, \"color\":\"#808080\" },{ \"height\":7, \"width\":24, \"color\":\"#808080\" },{ \"height\":7, \"width\":25, \"color\":\"#808080\" },{ \"height\":7, \"width\":26, \"color\":\"#FFFF00\" },\n" +
                "{ \"height\":8, \"width\":0, \"color\":\"#808080\" },{ \"height\":8, \"width\":1, \"color\":\"#FFFF00\" },{ \"height\":8, \"width\":2, \"color\":\"#00FF00\" },{ \"height\":8, \"width\":3, \"color\":\"#00FF00\" },{ \"height\":8, \"width\":4, \"color\":\"#00FFFF\" },{ \"height\":8, \"width\":5, \"color\":\"#808080\" },{ \"height\":8, \"width\":6, \"color\":\"#00FFFF\" },{ \"height\":8, \"width\":7, \"color\":\"#00FFFF\" },{ \"height\":8, \"width\":8, \"color\":\"#FFFF00\" },{ \"height\":8, \"width\":9, \"color\":\"#00FFFF\" },{ \"height\":8, \"width\":10, \"color\":\"#808080\" },{ \"height\":8, \"width\":11, \"color\":\"#808080\" },{ \"height\":8, \"width\":12, \"color\":\"#808080\" },{ \"height\":8, \"width\":13, \"color\":\"#FFFF00\" },{ \"height\":8, \"width\":14, \"color\":\"#808080\" },{ \"height\":8, \"width\":15, \"color\":\"#808080\" },{ \"height\":8, \"width\":16, \"color\":\"#FFFF00\" },{ \"height\":8, \"width\":17, \"color\":\"#808080\" },{ \"height\":8, \"width\":18, \"color\":\"#808080\" },{ \"height\":8, \"width\":19, \"color\":\"#808080\" },{ \"height\":8, \"width\":20, \"color\":\"#00FF00\" },{ \"height\":8, \"width\":21, \"color\":\"#00FFFF\" },{ \"height\":8, \"width\":22, \"color\":\"#00FF00\" },{ \"height\":8, \"width\":23, \"color\":\"#808080\" },{ \"height\":8, \"width\":24, \"color\":\"#808080\" },{ \"height\":8, \"width\":25, \"color\":\"#FFFF00\" },{ \"height\":8, \"width\":26, \"color\":\"#00FF00\" },\n" +
                "{ \"height\":9, \"width\":0, \"color\":\"#808080\" },{ \"height\":9, \"width\":1, \"color\":\"#00FF00\" },{ \"height\":9, \"width\":2, \"color\":\"#00FF00\" },{ \"height\":9, \"width\":3, \"color\":\"#808080\" },{ \"height\":9, \"width\":4, \"color\":\"#FFFF00\" },{ \"height\":9, \"width\":5, \"color\":\"#00FFFF\" },{ \"height\":9, \"width\":6, \"color\":\"#FFFF00\" },{ \"height\":9, \"width\":7, \"color\":\"#FFFF00\" },{ \"height\":9, \"width\":8, \"color\":\"#808080\" },{ \"height\":9, \"width\":9, \"color\":\"#00FFFF\" },{ \"height\":9, \"width\":10, \"color\":\"#FFFF00\" },{ \"height\":9, \"width\":11, \"color\":\"#808080\" },{ \"height\":9, \"width\":12, \"color\":\"#808080\" },{ \"height\":9, \"width\":13, \"color\":\"#FFFF00\" },{ \"height\":9, \"width\":14, \"color\":\"#808080\" },{ \"height\":9, \"width\":15, \"color\":\"#FFFF00\" },{ \"height\":9, \"width\":16, \"color\":\"#808080\" },{ \"height\":9, \"width\":17, \"color\":\"#00FF00\" },{ \"height\":9, \"width\":18, \"color\":\"#00FFFF\" },{ \"height\":9, \"width\":19, \"color\":\"#FF0000\" },{ \"height\":9, \"width\":20, \"color\":\"#00FF00\" },{ \"height\":9, \"width\":21, \"color\":\"#808080\" },{ \"height\":9, \"width\":22, \"color\":\"#00FFFF\" },{ \"height\":9, \"width\":23, \"color\":\"#FFFF00\" },{ \"height\":9, \"width\":24, \"color\":\"#808080\" },{ \"height\":9, \"width\":25, \"color\":\"#808080\" },{ \"height\":9, \"width\":26, \"color\":\"#FFFF00\" },\n" +
                "{ \"height\":10, \"width\":0, \"color\":\"#00FF00\" },{ \"height\":10, \"width\":1, \"color\":\"#808080\" },{ \"height\":10, \"width\":2, \"color\":\"#00FFFF\" },{ \"height\":10, \"width\":3, \"color\":\"#808080\" },{ \"height\":10, \"width\":4, \"color\":\"#FFFF00\" },{ \"height\":10, \"width\":5, \"color\":\"#00FF00\" },{ \"height\":10, \"width\":6, \"color\":\"#808080\" },{ \"height\":10, \"width\":7, \"color\":\"#808080\" },{ \"height\":10, \"width\":8, \"color\":\"#808080\" },{ \"height\":10, \"width\":9, \"color\":\"#808080\" },{ \"height\":10, \"width\":10, \"color\":\"#00FFFF\" },{ \"height\":10, \"width\":11, \"color\":\"#808080\" },{ \"height\":10, \"width\":12, \"color\":\"#00FFFF\" },{ \"height\":10, \"width\":13, \"color\":\"#808080\" },{ \"height\":10, \"width\":14, \"color\":\"#00FF00\" },{ \"height\":10, \"width\":15, \"color\":\"#808080\" },{ \"height\":10, \"width\":16, \"color\":\"#808080\" },{ \"height\":10, \"width\":17, \"color\":\"#00FF00\" },{ \"height\":10, \"width\":18, \"color\":\"#00FFFF\" },{ \"height\":10, \"width\":19, \"color\":\"#00FFFF\" },{ \"height\":10, \"width\":20, \"color\":\"#00FF00\" },{ \"height\":10, \"width\":21, \"color\":\"#808080\" },{ \"height\":10, \"width\":22, \"color\":\"#FFFF00\" },{ \"height\":10, \"width\":23, \"color\":\"#FFFF00\" },{ \"height\":10, \"width\":24, \"color\":\"#00FF00\" },{ \"height\":10, \"width\":25, \"color\":\"#00FF00\" },{ \"height\":10, \"width\":26, \"color\":\"#FFFF00\" },\n" +
                "{ \"height\":11, \"width\":0, \"color\":\"#FF0000\" },{ \"height\":11, \"width\":1, \"color\":\"#00FF00\" },{ \"height\":11, \"width\":2, \"color\":\"#FFFF00\" },{ \"height\":11, \"width\":3, \"color\":\"#808080\" },{ \"height\":11, \"width\":4, \"color\":\"#FFFF00\" },{ \"height\":11, \"width\":5, \"color\":\"#808080\" },{ \"height\":11, \"width\":6, \"color\":\"#FFFF00\" },{ \"height\":11, \"width\":7, \"color\":\"#808080\" },{ \"height\":11, \"width\":8, \"color\":\"#808080\" },{ \"height\":11, \"width\":9, \"color\":\"#808080\" },{ \"height\":11, \"width\":10, \"color\":\"#808080\" },{ \"height\":11, \"width\":11, \"color\":\"#808080\" },{ \"height\":11, \"width\":12, \"color\":\"#808080\" },{ \"height\":11, \"width\":13, \"color\":\"#FF0000\" },{ \"height\":11, \"width\":14, \"color\":\"#808080\" },{ \"height\":11, \"width\":15, \"color\":\"#FFFF00\" },{ \"height\":11, \"width\":16, \"color\":\"#FFFF00\" },{ \"height\":11, \"width\":17, \"color\":\"#808080\" },{ \"height\":11, \"width\":18, \"color\":\"#00FF00\" },{ \"height\":11, \"width\":19, \"color\":\"#808080\" },{ \"height\":11, \"width\":20, \"color\":\"#808080\" },{ \"height\":11, \"width\":21, \"color\":\"#FFFF00\" },{ \"height\":11, \"width\":22, \"color\":\"#808080\" },{ \"height\":11, \"width\":23, \"color\":\"#808080\" },{ \"height\":11, \"width\":24, \"color\":\"#FFFF00\" },{ \"height\":11, \"width\":25, \"color\":\"#00FF00\" },{ \"height\":11, \"width\":26, \"color\":\"#FF0000\" }\n" +
                "\n" +
                "        ]\n" +
                "    }\n" +
                "}");
        return test2;
    }

    public String[][] demoCells(){
        String[][] cells = new String[height][width];
        for(int i=0; i<height; i++){
            for(int j=0; j<width; j++){
                cells[i][j] = "#808080";
            }
        }
        return cells;
    }

    public String demoGetReportResponse(){
        JSONObject jsonObject = new JSONObject();
        JSONArray data =  new JSONArray();
        double lat = 21.0338596;
        double lng = 105.7598883;
        int id = 1;
        try {
            for(int i=0; i<10; i++){
                JSONObject report = new JSONObject();
                report.put(Latitude, lat);
                report.put(Longitude, lng);
                report.put("idmsg", id);
                data.put(report);
                lat += 0.02;
                lng += 0.02;
                id = id%7 + 1;
            }
            jsonObject.put(SUCCESS, true);
            jsonObject.put("message", "success" );
            jsonObject.put("data", data);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }
}
