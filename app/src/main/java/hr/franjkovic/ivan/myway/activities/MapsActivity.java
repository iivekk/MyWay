package hr.franjkovic.ivan.myway.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;

import hr.franjkovic.ivan.myway.R;

import static hr.franjkovic.ivan.myway.tools.MyTools.Constants.HOME_ACTIVITY;
import static hr.franjkovic.ivan.myway.tools.MyTools.Constants.SAVED_TRACK_INFO_ACTIVITY;
import static hr.franjkovic.ivan.myway.tools.MyTools.Constants.TRACK_INFO_ACTIVITY;
import static hr.franjkovic.ivan.myway.tools.MyTools.Keys.CLASS_NAME;
import static hr.franjkovic.ivan.myway.tools.MyTools.Keys.SELECTED_TRACK_LATITUDES;
import static hr.franjkovic.ivan.myway.tools.MyTools.Keys.SELECTED_TRACK_LAT_MARK_LIST;
import static hr.franjkovic.ivan.myway.tools.MyTools.Keys.SELECTED_TRACK_LNG_MARK_LIST;
import static hr.franjkovic.ivan.myway.tools.MyTools.Keys.SELECTED_TRACK_LONGITUDES;
import static hr.franjkovic.ivan.myway.tools.MyTools.Keys.SHARED_PREFERENCES.LATITUDES_LIST;
import static hr.franjkovic.ivan.myway.tools.MyTools.Keys.SHARED_PREFERENCES.LONGITUDES_LIST;
import static hr.franjkovic.ivan.myway.tools.MyTools.Keys.SHARED_PREFERENCES.MARKER_LAT_LIST;
import static hr.franjkovic.ivan.myway.tools.MyTools.Keys.SHARED_PREFERENCES.MARKER_LNG_LIST;
import static hr.franjkovic.ivan.myway.tools.MyTools.Keys.SHARED_PREFERENCES.NAME;
import static hr.franjkovic.ivan.myway.tools.MyTools.StaticMethods.isDouble;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    private SharedPreferences mapsSharedPreferences;

    private Polyline mPolyline;

    private List<LatLng> mLocationPoints, markerList;
    private String sListLatitudes, sListLongitudes, sLatMarkList, sLngMarkList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mapsSharedPreferences = getSharedPreferences(NAME, MODE_PRIVATE);

        mLocationPoints = new ArrayList<>();
        markerList = new ArrayList<>();

        // setting the value
        sListLatitudes = mapsSharedPreferences.getString(LATITUDES_LIST, "0");
        sListLongitudes = mapsSharedPreferences.getString(LONGITUDES_LIST, "0");
        sLatMarkList = mapsSharedPreferences.getString(MARKER_LAT_LIST, "0");
        sLngMarkList = mapsSharedPreferences.getString(MARKER_LNG_LIST, "0");

        // check where the request comes from and set location points
        Intent activityRequest = getIntent();
        if (activityRequest != null) {
            if (activityRequest.getStringExtra(CLASS_NAME).equals(HOME_ACTIVITY)) {
                setLocationPoints(sListLatitudes, sListLongitudes, mLocationPoints);
                setLocationPoints(sLatMarkList, sLngMarkList, markerList);
            }
            if (activityRequest.getStringExtra(CLASS_NAME).equals(TRACK_INFO_ACTIVITY)) {
                setLocationPoints(sListLatitudes, sListLongitudes, mLocationPoints);
                setLocationPoints(sLatMarkList, sLngMarkList, markerList);
            }
            if (activityRequest.getStringExtra(CLASS_NAME).equals(SAVED_TRACK_INFO_ACTIVITY)) {
                setLocationPoints(getIntent().getStringExtra(SELECTED_TRACK_LATITUDES)
                        , getIntent().getStringExtra(SELECTED_TRACK_LONGITUDES), mLocationPoints);
                setLocationPoints(getIntent().getStringExtra(SELECTED_TRACK_LAT_MARK_LIST)
                        , getIntent().getStringExtra(SELECTED_TRACK_LNG_MARK_LIST), markerList);
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mAddMarker(mMap);
        setPolyline(mMap);
    }

    /**
     * create Polyline from the set location points
     *
     * @param gMap
     */
    private void setPolyline(GoogleMap gMap) {
        if (mLocationPoints.size() > 0) {
            mPolyline = gMap.addPolyline(new PolylineOptions());
            mPolyline.setPoints(mLocationPoints);
            mPolyline.setColor(Color.BLUE);
            mPolyline.setWidth(5);
        }
    }

    /**
     * add markers to the map
     *
     * @param gMap - local Map
     */
    private void mAddMarker(GoogleMap gMap) {
        int counter = 1;
        if (mLocationPoints.size() > 0) {
            gMap.addMarker(new MarkerOptions().position(mLocationPoints.get(0)).title(getString(R.string.start_track_map)));
            gMap.addMarker(new MarkerOptions().position(mLocationPoints.get(mLocationPoints.size() - 1))
                    .title(getString(R.string.end_track_map)));
            for (LatLng l : markerList) {
                gMap.addMarker(new MarkerOptions().position(l).title(counter + "."));
                counter++;
            }
            setCameraView(gMap, 128);
        }
    }

    /**
     * creating a list of LatLng objects to display on the map
     *
     * @param latitudes  String value created from the latitude list
     * @param longitudes String value created from the longitude list
     * @param list       - List<LatLng> we want set up
     */
    private void setLocationPoints(String latitudes, String longitudes, List<LatLng> list) {
        if (latitudes != null && longitudes != null) {
            String[] lat = latitudes.split("xx");
            String[] lng = longitudes.split("xx");
            for (int i = 0; i < lat.length; i++) {
                if (isDouble(lat[i]) && isDouble(lng[i])) {
                    double x = Double.valueOf(lat[i]);
                    double y = Double.valueOf(lng[i]);
                    list.add(new LatLng(x, y));
                }
            }
        }
    }

    /**
     * setting CameraView, includes all location points
     *
     * @param gMap    - local Map
     * @param padding - space (in px) to leave between the bounding box edges and the view edges
     */
    private void setCameraView(GoogleMap gMap, int padding) {
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (LatLng latLng : mLocationPoints) {
            builder.include(latLng);
        }
        LatLngBounds bounds = builder.build();
        gMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, padding));
    }


}
