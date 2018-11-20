package hr.franjkovic.ivan.myway.activities;

import android.Manifest;
import android.animation.AnimatorInflater;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnLongClick;
import hr.franjkovic.ivan.myway.R;
import hr.franjkovic.ivan.myway.services.LocationTrackerService;

import static hr.franjkovic.ivan.myway.tools.MyTools.Animations.showWithAnim;
import static hr.franjkovic.ivan.myway.tools.MyTools.Constants.BROADCAST_ALERT.NEW_LOCATION_ALERT;
import static hr.franjkovic.ivan.myway.tools.MyTools.Constants.BROADCAST_ALERT.START_TRACKING;
import static hr.franjkovic.ivan.myway.tools.MyTools.Constants.HOME_ACTIVITY;
import static hr.franjkovic.ivan.myway.tools.MyTools.Constants.REQUEST_LOCATION_PERMISSION;
import static hr.franjkovic.ivan.myway.tools.MyTools.Keys.CLASS_NAME;
import static hr.franjkovic.ivan.myway.tools.MyTools.Keys.SHARED_PREFERENCES.*;
import static hr.franjkovic.ivan.myway.tools.MyTools.StaticMethods.*;

public class HomeActivity extends BaseActivity {

    @BindView(R.id.llHome)
    LinearLayout llHome;

    @BindView(R.id.llHomeLatLng)
    LinearLayout llHomeLatLng;

    @BindView(R.id.tvHomeLatitude)
    TextView tvHomeLatitude;

    @BindView(R.id.tvHomeLongitude)
    TextView tvHomeLongitude;

    @BindView(R.id.tvHomeDistance)
    TextView tvHomeDistance;

    @BindView(R.id.tvHomeSpeed)
    TextView tvHomeSpeed;

    @BindView(R.id.tvHomeAltitude)
    TextView tvHomeAltitude;

    @BindView(R.id.tvHours)
    TextView tvHours;

    @BindView(R.id.tvMinutes)
    TextView tvMinutes;

    @BindView(R.id.tvSeconds)
    TextView tvSeconds;

    @BindView(R.id.tvTenths)
    TextView tvTenths;

    @BindView(R.id.btnHomeStart)
    Button btnHomeStart;

    @BindView(R.id.btnHomeStop)
    Button btnHomeStop;

    private boolean trackingLocation;

    private FusedLocationProviderClient mFusedLocationProviderClient;

    private BroadcastReceiver homeReceiver;

    private SharedPreferences homeSharedPreferences;
    private SharedPreferences.Editor homeEditor;

    private Date start, stop;

    private Thread thread;

    private ObjectAnimator mObjectAnimator;
    private Animation animShake;
    private AlphaAnimation flashAnimation;

    private List<String> latMark, lngMark;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // inject activity_home into the activity_base that contains the drawer
        injectLayout(this, R.layout.activity_home, activityContent);

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        homeSharedPreferences = getSharedPreferences(NAME, MODE_PRIVATE);
        homeEditor = homeSharedPreferences.edit();

        mObjectAnimator = (ObjectAnimator) AnimatorInflater.loadAnimator(this, R.animator.blink);
        animShake = AnimationUtils.loadAnimation(this, R.anim.shake);
    }

    @Override
    protected void onResume() {
        super.onResume();
        clearText();
        setLocationInfo(homeSharedPreferences);
        regReceiver();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        removeLocationUpdates();
        stopForegroundService();
        unregReceiver();
        clearText();
        clearSharedPreferences(homeSharedPreferences);
    }

    @OnClick(R.id.llHomeLatLng)
    // If tracking is started, click to save the current location and add the marker
    public void addMarker() {
        if (trackingLocation) {
            showWithAnim(llHomeLatLng);
            playSound(this, R.raw.on_click);
            latMark.add(tvHomeLatitude.getText().toString());
            lngMark.add(tvHomeLongitude.getText().toString());
            saveToSharedPreferences(MARKER_LAT_LIST, sLatMarkList());
            saveToSharedPreferences(MARKER_LNG_LIST, sLngMarkList());
        }
    }

    @OnLongClick(R.id.llHomeLatLng) // If tracking is started, we open a map with a long click
    public boolean llHomeLatLng() {
        if (trackingLocation) {
            startVibration(this, 100);
            playSound(this, R.raw.on_click);
            Intent homeStartMapsActivity = new Intent(this, MapsActivity.class);
            homeStartMapsActivity.putExtra(CLASS_NAME, HOME_ACTIVITY);
            startActivity(homeStartMapsActivity);
            return true;
        }
        return false;
    }

    @OnLongClick(R.id.llHomeDistance)  // Open the distance screen
    public boolean llHomeDistance() {
        startVibration(this, 100);
        playSound(this, R.raw.on_click);
        startActivity(new Intent(this, DistanceActivity.class));
        return true;
    }

    @OnLongClick(R.id.llHomeSpeed)  // Open the speed screen
    public boolean llHomeSpeed() {
        startVibration(this, 100);
        playSound(this, R.raw.on_click);
        startActivity(new Intent(this, SpeedActivity.class));
        return true;
    }

    @OnLongClick(R.id.llHomeAltitude)  // Open the altitude screen
    public boolean llHomeAltitude() {
        startVibration(this, 100);
        playSound(this, R.raw.on_click);
        startActivity(new Intent(this, AltitudeActivity.class));
        return true;
    }

    @OnLongClick(R.id.llHomeTime)  // Shows 'Toast' with active time
    public boolean showActiveTime() {
        if (trackingLocation) {
            Toast.makeText(this, getString(R.string.active_time_toast) + homeSharedPreferences
                    .getString(ACTIVE_TIME, getString(R.string.no_value)), Toast.LENGTH_SHORT).show();
        }
        return true;
    }

    @OnClick(R.id.btnHomeStart) // start tracking
    public void btnHomeStart() {
        if (!trackingLocation) {
            showWithAnim(btnHomeStart);
            llHome.startAnimation(animShake);
            clearText();
            startTrackingLocation();
        }
    }

    @OnClick(R.id.btnHomeStop)  // stop tracking
    public void btnHomeStop() {
        showWithAnim(btnHomeStop);
        if (trackingLocation) {
            stopTrackingLocation();
        } else {
            clearText();
        }
    }

    @OnLongClick(R.id.btnHomeStop)  // open the tracklist
    public boolean btnHomeStopLongClick() {
        startVibration(this, 100);
        startActivity(new Intent(this, TrackListActivity.class));
        return true;
    }

    private void startTrackingLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) // check for location permission
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION_PERMISSION);
            return;
        } else {
            trackingLocation = true;
            clearSharedPreferences(homeSharedPreferences);  // clear SharedPreference for new data
            latMark = new ArrayList<>(); // initializing the 'strings' where we save the latitude of the location to add markers
            lngMark = new ArrayList<>(); // initializing the 'strings' where we save the longitudes of the location to add markers
            startVibration(this, 100);
            startAnimButton(); // start anim on button 'start'
            start = new Date(); // create start time
            startTimer(start);  // start timer
            saveToSharedPreferences(START_TIME, setTimeToString(start));  // save start time
            saveToSharedPreferences(TRACK_DATE, setDateToString(start)); // save the track date
            startForegroundService(); // start service for getting location, work in background
            startLocationUpdates(); // start location update
        }
    }

    private void stopTrackingLocation() {
        if (trackingLocation) {
            removeLocationUpdates(); // remove location udate
            stopForegroundService(); // stop service
            stopAnimButton(); // stop animation on the button
            stop = new Date(); // create end time
            saveToSharedPreferences(END_TIME, setTimeToString(stop)); // save end time
            stopTimer(); // stop timer
            saveToSharedPreferences(TRACK_DURATION, timeDifferenceToString(start.getTime(), stop.getTime())); // save track duration
            startFlashAnim();
            trackingLocation = false;
        }
    }

    /**
     * requests location updates with a callback on the specified PendingIntent
     */
    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager
                .PERMISSION_GRANTED) {
            return;
        }
        mFusedLocationProviderClient.requestLocationUpdates(getLocationRequest(), getPendingIntent());
    }

    /**
     * remove location updates
     */
    private void removeLocationUpdates() {
        mFusedLocationProviderClient.removeLocationUpdates(getPendingIntent());
    }

    /**
     * checking the android version and launching the foreground service
     */
    @TargetApi(Build.VERSION_CODES.O)
    private void startForegroundService() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            this.startForegroundService(new Intent(this, LocationTrackerService.class));
        } else {
            this.startService(new Intent(this, LocationTrackerService.class));
        }
    }

    /**
     * stop foreground service
     */
    private void stopForegroundService() {
        this.stopService(new Intent(this, LocationTrackerService.class));
    }

    /**
     * defining location updates
     *
     * @return location request for location update
     */
    private LocationRequest getLocationRequest() {
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        return locationRequest;
    }

    /**
     * @return pendingintent that uses broadcasting
     */
    private PendingIntent getPendingIntent() {
        return PendingIntent.getBroadcast(this, 0, new Intent(START_TRACKING), PendingIntent
                .FLAG_UPDATE_CURRENT);
    }

    /**
     * shows values from the shared preference
     *
     * @param sp - SharedPreference where we store temporary data
     */
    private void setLocationInfo(SharedPreferences sp) {
        tvHomeLatitude.setText(sp.getString(LATITUDE, getString(R.string.no_value)));
        tvHomeLongitude.setText(sp.getString(LONGITUDE, getString(R.string.no_value)));
        tvHomeDistance.setText(distanceToString(sp.getString(DISTANCE, getString(R.string.no_value)), getString(R.string.no_value)));
        tvHomeSpeed.setText(sp.getString(SPEED, getString(R.string.no_value)));
        tvHomeAltitude.setText(sp.getString(ALTITUDE, getString(R.string.no_value)));
    }

    /**
     * sets the values from screen to the defaults
     */
    private void clearText() {
        tvHomeLatitude.setText(R.string.no_value);
        tvHomeLongitude.setText(R.string.no_value);
        tvHomeDistance.setText(R.string.no_value);
        tvHomeSpeed.setText(R.string.no_value);
        tvHomeAltitude.setText(R.string.no_value);
        setTimerToZero();
    }

    /**
     * registering the receiver and displaying the location value
     */
    private void regReceiver() {
        homeReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                setLocationInfo(homeSharedPreferences);
            }
        };
        registerReceiver(homeReceiver, new IntentFilter(NEW_LOCATION_ALERT));
    }

    /**
     * unregister the receiver
     */
    private void unregReceiver() {
        unregisterReceiver(homeReceiver);
    }

    /**
     * save temporary data in SharedPreference
     *
     * @param key   - String value we use as a key
     * @param value - String value we save
     */
    private void saveToSharedPreferences(String key, String value) {
        homeEditor.putString(key, value);
        homeEditor.apply();
    }

    /**
     * @param date - the date from which we show the time
     * @return String value of time in format - HH:mm:ss.S
     */
    private String setTimeToString(Date date) {
        return new SimpleDateFormat("HH:mm:ss.S", Locale.getDefault()).format(date);
    }

    /**
     * @param date - the date we want to show
     * @return String value of date in format - dd.MM.yyyy.
     */
    private String setDateToString(Date date) {
        return new SimpleDateFormat("dd.MM.yyyy.", Locale.getDefault()).format(date);
    }

    /**
     * clear data from SharedPreference
     *
     * @param sp - local SharedPreference
     */
    private void clearSharedPreferences(SharedPreferences sp) {
        homeSharedPreferences.edit().clear().apply();
    }

    /**
     * animation on button 'Start' when is location tracking start
     */
    private void startAnimButton() {
        playSound(this, R.raw.start_tracking);
        btnHomeStart.setTextColor(Color.RED);
        btnHomeStart.setTypeface(btnHomeStart.getTypeface(), Typeface.BOLD);
        mObjectAnimator.setTarget(btnHomeStart);
        mObjectAnimator.start();
    }

    /**
     * stop animation on button 'Start'
     */
    private void stopAnimButton() {
        mObjectAnimator.end();
        mObjectAnimator.cancel();
        btnHomeStart.setTypeface(btnHomeStop.getTypeface(), Typeface.NORMAL);
        btnHomeStart.setTextColor(Color.BLACK);
    }

    /**
     * start timer that display tenths using Thread
     *
     * @param date - date from which we measure the difference of time
     */
    private void startTimer(final Date date) {
        setTimerToZero();
        thread = new Thread() {
            @Override
            public void run() {
                try {
                    while (!thread.isInterrupted()) {
                        Thread.sleep(100);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                setTimer(date);
                            }
                        });
                    }
                } catch (InterruptedException e) {
                }
            }
        };
        thread.start();
    }

    /**
     * stop the timer
     */
    private void stopTimer() {
        thread.interrupt();
    }

    /**
     * setting and displaying the time of location tracking
     *
     * @param date - date from which we measure the difference of time
     */
    private void setTimer(Date date) {
        long timerTime = new Date().getTime() - date.getTime();
        long milliseconds = timerTime % 1000;
        long seconds = timerTime / 1000 % 60;
        long minutes = timerTime / (60 * 1000) % 60;
        long hours = timerTime / (60 * 60 * 1000);
        tvHours.setText("" + hours);
        tvMinutes.setText(formatMinutes(minutes));
        tvSeconds.setText(formatSeconds(seconds));
        tvTenths.setText(formatTenths(milliseconds));
    }

    /**
     * setting timer to zero
     */
    private void setTimerToZero() {
        tvHours.setText("0");
        tvMinutes.setText("00");
        tvSeconds.setText("00");
        tvTenths.setText("0");
    }

    /**
     * start animation for the stop of location tracking
     */
    private void startFlashAnim() {
        flashAnimation = new AlphaAnimation(0f, 1f);
        flashAnimation.setDuration(700);
        flashAnimation.setFillAfter(true);
        flashAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                playSound(HomeActivity.this, R.raw.stop_tracking);
                startVibration(HomeActivity.this, 500);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                startActivity(new Intent(HomeActivity.this, TrackInfoActivity.class));
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        llHome.startAnimation(flashAnimation);
    }

    /**
     * converts List<String> 'latMark' to String and as divider used - xx
     *
     * @return one String value
     */
    private String sLatMarkList() {
        return TextUtils.join("xx", latMark);
    }

    /**
     * converts List<String> 'lngMark' to String and as divider used - xx
     *
     * @return one String value
     */
    private String sLngMarkList() {
        return TextUtils.join("xx", lngMark);
    }

}
