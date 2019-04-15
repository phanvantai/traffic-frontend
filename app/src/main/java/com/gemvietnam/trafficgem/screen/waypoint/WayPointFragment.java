package com.gemvietnam.trafficgem.screen.waypoint;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.gemvietnam.Constants;
import com.gemvietnam.base.viper.ViewFragment;
import com.gemvietnam.trafficgem.R;
import com.gemvietnam.trafficgem.library.AbstractRouting;
import com.gemvietnam.trafficgem.library.MySupportMapFragment;
import com.gemvietnam.trafficgem.library.Route;
import com.gemvietnam.trafficgem.library.RouteException;
import com.gemvietnam.trafficgem.library.Routing;
import com.gemvietnam.trafficgem.library.RoutingListener;
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
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static android.content.Context.LOCATION_SERVICE;

/**
 * The WayPoint Fragment
 */
public class WayPointFragment extends ViewFragment<WayPointContract.Presenter> implements
    WayPointContract.View, OnMapReadyCallback, RoutingListener,
    GoogleApiClient.OnConnectionFailedListener,
    GoogleApiClient.ConnectionCallbacks {
    @BindView(R.id.waypoint_search_img)
    ImageView mSearchImg;
    @BindView(R.id.waypoint_location_tv)
    TextView mLocationTv;
    @BindView(R.id.waypoint_destination_tv)
    TextView mDestinationTv;

    private static final String YOUR_LOCATION = "Your Location";
    private static final String CHOOSE_DESTINATION = "Choose Destination";
    private static final int ORIGIN_CODE_AUTOCOMPLETE = 11;
    private static final int DESTINATION_CODE_AUTOCOMPLETE = 12;
    private static final String TAG = "TAG: ";
    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 1;


    private LatLng mOrigin;
    private LatLng mDestination;
    private ProgressDialog mprogress;
    private GoogleMap mMap;
    private List<Polyline> polylines;
    private static boolean mMapIsTouched = false;
    int shortestPath = 0;

    public static WayPointFragment getInstance() {
        return new WayPointFragment();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_way_point;
    }

    @Override
    public void initLayout() {
        super.initLayout();
        mLocationTv.setText(YOUR_LOCATION);
        mDestinationTv.setText(CHOOSE_DESTINATION);
        //getCurrentLocation();
        mOrigin = getLocation();


        collectData();

        mLocationTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openAutoCompleteActivity(ORIGIN_CODE_AUTOCOMPLETE);
            }
        });
        mDestinationTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openAutoCompleteActivity(DESTINATION_CODE_AUTOCOMPLETE);
            }
        });
        mSearchImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mDestinationTv.getText() != CHOOSE_DESTINATION) {
//          showDialog();
                    route();
                } else {
                    Toast.makeText(getViewContext(), "Choose your destination first", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void collectData() {

    }


    private LatLng getLocation() {
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
        Location lastLocation = getLastKnownLocation();
        return new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());
    }


    public Location getLastKnownLocation() {
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
        return bestLocation;
    }

    /**
     * get current location
     */
    public void getCurrentLocation() {
        LocationManager locationManager = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);
        Criteria criteria = new Criteria();
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

            mOrigin = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());

        }
    }

    private void route() {
        mprogress = ProgressDialog.show(getActivity(), "Please wait.",
                "Fetching route information.", true);
        Routing routing = new Routing.Builder()
                .travelMode(AbstractRouting.TravelMode.DRIVING)
                .withListener(this)
                .alternativeRoutes(true)
                .waypoints(mOrigin, mDestination)
                .build();
        routing.execute(true);
    }

    @Override
    public void showMap() {
        final SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.waypoint_map);
        MySupportMapFragment fr = (MySupportMapFragment) mapFragment;
        fr.getMapAsync(this);
    }

    private void showDialog() {
        mprogress = new ProgressDialog(getViewContext());
        mprogress.setMessage("Loading...");
        mprogress.setCanceledOnTouchOutside(false);
        mprogress.show();
    }

    private void openAutoCompleteActivity(int code) {
        try {
            Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY).build(getActivity());
            startActivityForResult(intent, code);
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == ORIGIN_CODE_AUTOCOMPLETE) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(getActivity(), data);
                mOrigin = place.getLatLng();
                mLocationTv.setText(place.getName());
                Log.i(TAG, "Place: " + place.getName());
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(getActivity(), data);
                // TODO: Handle the error.
                Log.i(TAG, status.getStatusMessage());

            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }

        if (requestCode == DESTINATION_CODE_AUTOCOMPLETE) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(getActivity(), data);
                mDestination = place.getLatLng();
                mDestinationTv.setText(place.getName());
                Log.i(TAG, "Place: " + place.getName());
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(getActivity(), data);
                // TODO: Handle the error.
                Log.i(TAG, status.getStatusMessage());

            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
    }

    @Override
    public void onRoutingFailure(RouteException e) {

    }

    @Override
    public void onRoutingStart() {

    }

    @Override
    public void onRoutingSuccess(List<Route> route, int shortestRouteIndex) {
        mprogress.dismiss();
        mMap.clear();
        polylines = new ArrayList<>();
        for (int i = 1; i < route.size(); i++) {
            if (route.get(i).getTravelTime() < route.get(i - 1).getTravelTime())
                shortestPath = i;
        }
        if (mPresenter.getKey() == Constants.NORMAL) {
            PolylineOptions polyOptions = new PolylineOptions();
            polyOptions.color(getResources().getColor(R.color.colorAccent));
            polyOptions.width(15);
            polyOptions.addAll(route.get(0).getPoints());
            Polyline polyline = mMap.addPolyline(polyOptions);
            polylines.add(polyline);
        } else if (mPresenter.getKey() == Constants.ADVANCE) {
            PolylineOptions polyOptions = new PolylineOptions();
            polyOptions.color(getResources().getColor(R.color.colorAccent));
            polyOptions.width(15);
            polyOptions.addAll(route.get(0).getPoints());
            Polyline polyline = mMap.addPolyline(polyOptions);
            polylines.add(polyline);
        }

        mMap.addMarker(new MarkerOptions().position(mOrigin));
        mMap.addMarker(new MarkerOptions().position(mDestination));

        int padding = 100;
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(route.get(0).getLatLgnBounds(), padding);
        mMap.moveCamera(cu);
    }

    @Override
    public void onRoutingCancelled() {

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
        mMap = googleMap;
        if (mprogress != null && mprogress.isShowing())
            mprogress.dismiss();
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
//        mMap.addMarker(new MarkerOptions().position(mOrigin));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(mOrigin));
        mMap.setMyLocationEnabled(true);
        mMap.setPadding(0, 300, 0, 0);
    }

}
