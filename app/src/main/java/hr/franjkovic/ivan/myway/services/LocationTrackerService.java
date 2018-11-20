package hr.franjkovic.ivan.myway.services;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;

import com.google.android.gms.location.LocationResult;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import hr.franjkovic.ivan.myway.R;
import hr.franjkovic.ivan.myway.activities.HomeActivity;
import hr.franjkovic.ivan.myway.tools.MyTools.Keys.SHARED_PREFERENCES;

import static hr.franjkovic.ivan.myway.tools.MyTools.Constants.BROADCAST_ALERT.*;
import static hr.franjkovic.ivan.myway.tools.MyTools.Constants.NOTIFICATION_ID.*;
import static hr.franjkovic.ivan.myway.tools.MyTools.Keys.SHARED_PREFERENCES.*;
import static hr.franjkovic.ivan.myway.tools.MyTools.StaticMethods.formatTime;

public class LocationTrackerService extends Service {

    private BroadcastReceiver serviceReceiver;

    private SharedPreferences serviceSharedPreferences;
    private SharedPreferences.Editor serviceEditor;

    private List<Location> locations;
    private List<String> latitudes, longitudes;
    private List<Float> speed;
    private List<Double> altitudes;
    private List<Long> activeTime;

    private long startTime, endTime;
    private boolean isActive;


    public LocationTrackerService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();

        locations = new ArrayList<>();
        latitudes = new ArrayList<>();
        longitudes = new ArrayList<>();
        speed = new ArrayList<>();
        altitudes = new ArrayList<>();
        activeTime = new ArrayList<>();

        serviceSharedPreferences = getSharedPreferences(SHARED_PREFERENCES.NAME, MODE_PRIVATE);
        serviceEditor = serviceSharedPreferences.edit();

        // start foreground service
        startForeground(FOREGROUND_SERVICE, getNotification());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        getReceieve();
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unRegReceiver();
    }

    // create notification
    @TargetApi(Build.VERSION_CODES.O)
    private Notification getNotification() {
        Intent notificationIntent = new Intent(this, HomeActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pNotificationIntent = PendingIntent.getActivity(this, 0,
                notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context
                .NOTIFICATION_SERVICE);

        String CHANNEL_ID = "CHANNEL_ID";
        CharSequence CHANNEL_NAME = "CHANNEL_NAME";
        int importance = NotificationManager.IMPORTANCE_HIGH;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID,
                    CHANNEL_NAME, importance);
            notificationChannel.setSound(null, null);

            notificationManager.createNotificationChannel(notificationChannel);

            Notification notification = new Notification.Builder(this)
                    .setContentIntent(pNotificationIntent)
                    .setSmallIcon(R.drawable.ic_notification)
                    .setChannelId(CHANNEL_ID)
                    .build();
            return notification;
        } else {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                    .setContentTitle(getString(R.string.app_name))
                    .setContentIntent(pNotificationIntent)
                    .setSmallIcon(R.drawable.ic_notification);
            Notification notification = builder.build();
            return notification;
        }
    }

    // getting location
    private void getLocation(Intent intent) {
        LocationResult locationResult = LocationResult.extractResult(intent);
        if (locationResult != null) {
            Location newLocation = locationResult.getLastLocation();
            if (newLocation.hasAccuracy()) {
                if (newLocation.getAccuracy() < 5) { // only locations where accuracy is less than 5 meters
                    locations.add(newLocation); // add current location to list of location
                    latitudes.add(String.valueOf(newLocation.getLatitude())); // add location latitude to list
                    longitudes.add(String.valueOf(newLocation.getLongitude())); // add location longitude to list
                    speed.add(newLocation.getSpeed()); // add current speed to list
                    setActiveTime(newLocation); // setting active time

                    if (newLocation.hasAltitude()) {
                        altitudes.add(newLocation.getAltitude()); // add altitude to list
                    }

                    saveToSharedPreferences(newLocation);
                    newLocationAlert();
                }
            }
        }
    }

    /**
     * sends a broadcast informing me that a new location has been set up
     */
    private void newLocationAlert() {
        sendBroadcast(new Intent(NEW_LOCATION_ALERT));
    }

    private void getReceieve() {
        serviceReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                getLocation(intent);
            }
        };
        registerReceiver(serviceReceiver, new IntentFilter(START_TRACKING));
    }

    private void unRegReceiver() {
        unregisterReceiver(serviceReceiver);
    }

    /**
     * @param location - current location we use
     * @return String value of location latitude
     */
    private String sLatitude(Location location) {
        if (location != null) {
            return String.valueOf(location.getLatitude());
        } else {
            return getString(R.string.no_value);
        }
    }

    /**
     * @param location - current location we use
     * @return String value of location longitude
     */
    private String sLongitude(Location location) {
        if (location != null) {
            return String.valueOf(location.getLongitude());
        } else {
            return getString(R.string.no_value);
        }
    }

    /**
     * @return String value of distance traveled
     */
    private String sDistance() {
        float distance = 0f;
        if (locations == null || locations.size() < 2) {
            return String.valueOf(0f);
        } else {
            for (int i = 0; i < locations.size() - 1; i++) {
                distance += locations.get(i).distanceTo(locations.get(i + 1));
            }
            if (distance > 10) {
                return String.format(Locale.ROOT, "%.2f", (distance / 1000));
            } else {
                return String.valueOf(0f);
            }
        }
    }

    /**
     * @param location - current location we use
     * @return String value of current speed
     */
    private String sSpeed(Location location) {
        if (location != null && locations.size() > 1) {
            return String.format(Locale.ROOT, "%.1f", (location.getSpeed() * 3.6f)) + getString(R.string.km_h);
        } else {
            return getString(R.string.no_value);
        }
    }

    /**
     * @return String value of max speed
     */
    private String sMaxSpeed() {
        if (speed.size() > 0) {
            return String.format(Locale.ROOT, "%.1f", (Collections.max(speed) * 3.6f)) + getString(R.string.km_h);
        } else {
            return getString(R.string.no_value);
        }
    }

    /**
     * @return String value of average speed
     */
    private String sAverageSpeed() {
        float speedAverage = 0;
        if (speed.size() > 0) {
            float speedSum = 0;
            for (int i = 0; i < speed.size(); i++) {
                speedSum += speed.get(i);
            }
            speedAverage = speedSum / speed.size();
            return String.format(Locale.ROOT, "%.1f", (speedAverage * 3.6f)) + getString(R.string.km_h);
        } else {
            return getString(R.string.no_value);
        }
    }

    /**
     * @param location - current location we use
     * @return String value of current altitude
     */
    private String sAltitude(Location location) {
        if (location != null && location.hasAltitude()) {
            return String.valueOf(Math.round(location.getAltitude())) + getString(R.string.meters);
        } else {
            return getString(R.string.no_value);
        }
    }

    /**
     * @return String value of min altitude
     */
    private String sMinAltitude() {
        if (altitudes.size() > 0) {
            return String.valueOf(Math.round(Collections.min(altitudes))) + getString(R.string.meters);
        } else {
            return getString(R.string.no_value);
        }
    }

    /**
     * @return String value of max altitude
     */
    private String sMaxAltitude() {
        if (altitudes.size() > 0) {
            return String.valueOf(Math.round(Collections.max(altitudes))) + getString(R.string.meters);
        } else {
            return getString(R.string.no_value);
        }
    }

    /**
     * @return String value converted from list of location latitudes
     */
    private String sLatitudesList() {
        return TextUtils.join("xx", latitudes);
    }

    /**
     * @return String value converted from list of location longitudes
     */
    private String sLongitudeList() {
        return TextUtils.join("xx", longitudes);
    }

    /**
     * save values in SharedPreference
     *
     * @param location - current location we use
     */
    private void saveToSharedPreferences(Location location) {
        serviceEditor.putString(LATITUDE, sLatitude(location));
        serviceEditor.putString(LONGITUDE, sLongitude(location));
        serviceEditor.putString(DISTANCE, sDistance());
        serviceEditor.putString(SPEED, sSpeed(location));
        serviceEditor.putString(MAX_SPEED, sMaxSpeed());
        serviceEditor.putString(AVERAGE_SPEED, sAverageSpeed());
        serviceEditor.putString(ALTITUDE, sAltitude(location));
        serviceEditor.putString(MIN_ALTITUDE, sMinAltitude());
        serviceEditor.putString(MAX_ALTITUDE, sMaxAltitude());
        serviceEditor.putString(LATITUDES_LIST, sLatitudesList());
        serviceEditor.putString(LONGITUDES_LIST, sLongitudeList());
        serviceEditor.putString(ACTIVE_TIME, sActiveTime());
        serviceEditor.apply();
    }

    /**
     * setting active time
     *
     * @param location - current location we use
     */
    private void setActiveTime(Location location) {
        if (location.hasSpeed()) {
            if (location.getSpeed() > 0) { // if speed is higher of zero, it's active time, start timing
                if (!isActive) {
                    startTime = System.currentTimeMillis();
                }
                isActive = true;
            } else if (isActive) { // if speed is zero, end timing, time add to active time
                endTime = System.currentTimeMillis();
                activeTime.add(endTime - startTime);
                isActive = false;
            }
        }
    }

    /**
     * calculate the active time of the track
     *
     * @return String value of active time
     */
    private String sActiveTime() {
        long time = 0;
        if (activeTime.size() > 0) {
            for (long t : activeTime) {
                time += t;
            }
        }
        return formatTime(time);
    }
}
