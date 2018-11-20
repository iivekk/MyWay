package hr.franjkovic.ivan.myway.services;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import hr.franjkovic.ivan.myway.R;
import hr.franjkovic.ivan.myway.database.Track;

import static hr.franjkovic.ivan.myway.database.MyWayApp.database;
import static hr.franjkovic.ivan.myway.tools.MyTools.Constants.BROADCAST_ALERT.TRACK_SAVED;
import static hr.franjkovic.ivan.myway.tools.MyTools.Keys.SHARED_PREFERENCES.ACTIVE_TIME;
import static hr.franjkovic.ivan.myway.tools.MyTools.Keys.SHARED_PREFERENCES.AVERAGE_SPEED;
import static hr.franjkovic.ivan.myway.tools.MyTools.Keys.SHARED_PREFERENCES.DISTANCE;
import static hr.franjkovic.ivan.myway.tools.MyTools.Keys.SHARED_PREFERENCES.END_TIME;
import static hr.franjkovic.ivan.myway.tools.MyTools.Keys.SHARED_PREFERENCES.LATITUDES_LIST;
import static hr.franjkovic.ivan.myway.tools.MyTools.Keys.SHARED_PREFERENCES.LONGITUDES_LIST;
import static hr.franjkovic.ivan.myway.tools.MyTools.Keys.SHARED_PREFERENCES.MARKER_LAT_LIST;
import static hr.franjkovic.ivan.myway.tools.MyTools.Keys.SHARED_PREFERENCES.MARKER_LNG_LIST;
import static hr.franjkovic.ivan.myway.tools.MyTools.Keys.SHARED_PREFERENCES.MAX_ALTITUDE;
import static hr.franjkovic.ivan.myway.tools.MyTools.Keys.SHARED_PREFERENCES.MAX_SPEED;
import static hr.franjkovic.ivan.myway.tools.MyTools.Keys.SHARED_PREFERENCES.MIN_ALTITUDE;
import static hr.franjkovic.ivan.myway.tools.MyTools.Keys.SHARED_PREFERENCES.NAME;
import static hr.franjkovic.ivan.myway.tools.MyTools.Keys.SHARED_PREFERENCES.START_TIME;
import static hr.franjkovic.ivan.myway.tools.MyTools.Keys.SHARED_PREFERENCES.TRACK_DATE;
import static hr.franjkovic.ivan.myway.tools.MyTools.Keys.SHARED_PREFERENCES.TRACK_DURATION;
import static hr.franjkovic.ivan.myway.tools.MyTools.Keys.TRACK_NAME;

public class SaveTrack extends IntentService {

    private SharedPreferences stSharedPreferences;
    private List<Track> tracks;

    public SaveTrack() {
        super("SaveTrack");
    }

    @Override
    public void onCreate() {
        super.onCreate();

        stSharedPreferences = getSharedPreferences(NAME, MODE_PRIVATE);
        tracks = new ArrayList<>();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            saveTrackAction(intent.getStringExtra(TRACK_NAME));
        }
    }

    /**
     * save track to database action
     *
     * @param trackName - track name
     */
    private void saveTrackAction(String trackName) {
        saveTrackToDatabase(stSharedPreferences, trackName);
        showToast();
        trackSavedAlert();

    }

    /**
     * checks if track is saved and shows Toast
     */
    private void showToast() {
        Handler handler = new Handler(Looper.getMainLooper());
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (checkTrackSaved()) {
                    Toast.makeText(SaveTrack.this, getString(R.string.track_is_saved), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(SaveTrack.this, getString(R.string.track_not_saved), Toast.LENGTH_SHORT).show();
                }
            }
        };
        handler.post(runnable);
    }

    private void saveTrackToDatabase(SharedPreferences sp, String trackName) {
        database.trackDao().insert(new Track(trackName
                , sp.getString(TRACK_DATE, "/")
                , sp.getString(START_TIME, "/")
                , sp.getString(END_TIME, "/")
                , sp.getString(TRACK_DURATION, "/")
                , sp.getString(ACTIVE_TIME, "/")
                , sp.getString(DISTANCE, "/")
                , sp.getString(AVERAGE_SPEED, "/")
                , sp.getString(MAX_SPEED, "/")
                , sp.getString(MIN_ALTITUDE, "/")
                , sp.getString(MAX_ALTITUDE, "/")
                , sp.getString(LATITUDES_LIST, "/")
                , sp.getString(LONGITUDES_LIST, "/")
                , sp.getString(MARKER_LAT_LIST, "/")
                , sp.getString(MARKER_LNG_LIST, "/")));
    }

    /**
     * check if track is saved
     *
     * @return true if the track is saved or false if the track has not been saved
     */
    private boolean checkTrackSaved() {
        boolean check = false;
        tracks = database.trackDao().getAll();
        for (Track t : tracks) {
            if (t.getStartTime().equals(stSharedPreferences.getString(START_TIME, "null"))) {
                check = true;
            }
        }
        return check;
    }

    /**
     * sending the broadcast to the track being saved
     */
    private void trackSavedAlert() {
        if (checkTrackSaved()) {
            sendBroadcast(new Intent(TRACK_SAVED));
            stSharedPreferences.edit().clear().apply();
        }
    }
}
