package hr.franjkovic.ivan.myway.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.OnClick;
import hr.franjkovic.ivan.myway.R;
import hr.franjkovic.ivan.myway.database.Track;

import static hr.franjkovic.ivan.myway.database.MyWayApp.database;
import static hr.franjkovic.ivan.myway.tools.MyTools.Animations.*;
import static hr.franjkovic.ivan.myway.tools.MyTools.Constants.BROADCAST_ALERT.NEW_LOCATION_ALERT;
import static hr.franjkovic.ivan.myway.tools.MyTools.Keys.SHARED_PREFERENCES.DISTANCE;
import static hr.franjkovic.ivan.myway.tools.MyTools.Keys.SHARED_PREFERENCES.NAME;
import static hr.franjkovic.ivan.myway.tools.MyTools.StaticMethods.distanceToString;
import static hr.franjkovic.ivan.myway.tools.MyTools.StaticMethods.injectLayout;
import static hr.franjkovic.ivan.myway.tools.MyTools.StaticMethods.isFloat;
import static hr.franjkovic.ivan.myway.tools.MyTools.StaticMethods.playSound;
import static hr.franjkovic.ivan.myway.tools.MyTools.StaticMethods.startVibration;

public class DistanceActivity extends BaseActivity {

    @BindView(R.id.tvDistanceMin)
    TextView tvDistanceMin;

    @BindView(R.id.tvDistanceMax)
    TextView tvDistanceMax;

    @BindView(R.id.tvDistanceTotal)
    TextView tvDistanceTotal;

    @BindView(R.id.tvDistance)
    TextView tvDistance;

    @BindView(R.id.btnDistanceBack)
    Button btnDistanceBack;

    private BroadcastReceiver distanceReceiver;

    private SharedPreferences distanceSharedPreferences;

    private List<Float> length;
    private List<Track> tracks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        injectLayout(this, R.layout.activity_distance, activityContent);

        distanceSharedPreferences = getSharedPreferences(NAME, MODE_PRIVATE);

        length = new ArrayList<>();
        tracks = new ArrayList<>();
        tracks = database.trackDao().getAll();
    }

    @Override
    protected void onResume() {
        super.onResume();

        tvDistance.setText(distanceToString(distanceSharedPreferences.getString(DISTANCE, getString(R.string.no_value)), getString(R.string.no_value)));
        tvDistanceMin.setText(getMinTrackLength());
        tvDistanceMax.setText(getMaxTrackLength());
        tvDistanceTotal.setText(getTotalLength());
        regReceiver();
    }

    @Override
    protected void onPause() {
        super.onPause();

        unregReceiver();
    }

    @OnClick(R.id.btnDistanceBack)
    public void btnDistanceBack() {
        showWithAnim(btnDistanceBack);
        startVibration(this, 100);
        playSound(this, R.raw.on_click);
        finish();
    }

    private void regReceiver() {
        distanceReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                tvDistance.setText(distanceSharedPreferences.getString(DISTANCE, getString(R.string.no_value)) + getString(R.string.km));
            }
        };
        registerReceiver(distanceReceiver, new IntentFilter(NEW_LOCATION_ALERT));
    }

    private void unregReceiver() {
        unregisterReceiver(distanceReceiver);
    }

    /**
     * Calculates the total length of all saved tracks
     *
     * @return String value of total length in format - x.xx km
     */
    private String getTotalLength() {
        float totalLength = 0;
        if (tracks.size() > 0) {
            for (Track t : tracks) {
                if (isFloat(t.getTrackLength())) {
                    totalLength += Float.valueOf(t.getTrackLength());
                }
            }
        }
        return String.format(Locale.ROOT, "%.2f", totalLength) + getString(R.string.km);
    }

    /**
     * Search for all saved tracks that are the shortest
     *
     * @return String value of min length in format - x.xx m
     */
    private String getMinTrackLength() {
        if (tracks.size() > 0) {
            for (Track t : tracks) {
                if (isFloat(t.getTrackLength())) {
                    length.add(Float.valueOf(t.getTrackLength()));
                }
            }
            if (length.size() > 0) {
                return String.format(Locale.ROOT, "%.2f", Collections.min(length)) + getString(R.string.km);
            } else {
                return getString(R.string.no_value);
            }
        } else {
            return getString(R.string.no_value);
        }
    }

    /**
     * Search all saved tracks that are the longest
     *
     * @return String value of max length in format - x.xx m
     */
    private String getMaxTrackLength() {
        if (tracks.size() > 0) {
            for (Track t : tracks) {
                if (isFloat(t.getTrackLength())) {
                    length.add(Float.valueOf(t.getTrackLength()));
                }
            }
            if (length.size() > 0) {
                return String.format(Locale.ROOT, "%.2f", Collections.max(length)) + getString(R.string.km);
            } else {
                return getString(R.string.no_value);
            }
        } else {
            return getString(R.string.no_value);
        }
    }
}
