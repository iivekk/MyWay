package hr.franjkovic.ivan.myway.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.OnClick;
import hr.franjkovic.ivan.myway.R;
import hr.franjkovic.ivan.myway.dialog.InsertTrackNameDialog;
import hr.franjkovic.ivan.myway.dialog.SaveTrackWarningDialog;

import static hr.franjkovic.ivan.myway.tools.MyTools.Animations.showWithAnim;
import static hr.franjkovic.ivan.myway.tools.MyTools.Constants.BROADCAST_ALERT.TRACK_SAVED;
import static hr.franjkovic.ivan.myway.tools.MyTools.Constants.TRACK_INFO_ACTIVITY;
import static hr.franjkovic.ivan.myway.tools.MyTools.Keys.CLASS_NAME;
import static hr.franjkovic.ivan.myway.tools.MyTools.Keys.SHARED_PREFERENCES.ACTIVE_TIME;
import static hr.franjkovic.ivan.myway.tools.MyTools.Keys.SHARED_PREFERENCES.AVERAGE_SPEED;
import static hr.franjkovic.ivan.myway.tools.MyTools.Keys.SHARED_PREFERENCES.DISTANCE;
import static hr.franjkovic.ivan.myway.tools.MyTools.Keys.SHARED_PREFERENCES.END_TIME;
import static hr.franjkovic.ivan.myway.tools.MyTools.Keys.SHARED_PREFERENCES.MAX_ALTITUDE;
import static hr.franjkovic.ivan.myway.tools.MyTools.Keys.SHARED_PREFERENCES.MAX_SPEED;
import static hr.franjkovic.ivan.myway.tools.MyTools.Keys.SHARED_PREFERENCES.MIN_ALTITUDE;
import static hr.franjkovic.ivan.myway.tools.MyTools.Keys.SHARED_PREFERENCES.NAME;
import static hr.franjkovic.ivan.myway.tools.MyTools.Keys.SHARED_PREFERENCES.START_TIME;
import static hr.franjkovic.ivan.myway.tools.MyTools.Keys.SHARED_PREFERENCES.TRACK_DATE;
import static hr.franjkovic.ivan.myway.tools.MyTools.Keys.SHARED_PREFERENCES.TRACK_DURATION;
import static hr.franjkovic.ivan.myway.tools.MyTools.StaticMethods.distanceToString;
import static hr.franjkovic.ivan.myway.tools.MyTools.StaticMethods.injectLayout;
import static hr.franjkovic.ivan.myway.tools.MyTools.StaticMethods.playSound;
import static hr.franjkovic.ivan.myway.tools.MyTools.StaticMethods.startVibration;

public class TrackInfoActivity extends BaseActivity {

    @BindView(R.id.tvInfoDate)
    TextView tvInfoDate;

    @BindView(R.id.tvInfoStart)
    TextView tvInfoStart;

    @BindView(R.id.tvInfoEnd)
    TextView tvInfoEnd;

    @BindView(R.id.tvInfoDuration)
    TextView tvInfoDuration;

    @BindView(R.id.tvInfoActiveTime)
    TextView tvInfoActiveTime;

    @BindView(R.id.tvInfoLength)
    TextView tvInfoLength;

    @BindView(R.id.tvInfoAverageSpeed)
    TextView tvInfoAverageSpeed;

    @BindView(R.id.tvInfoMaxSpeed)
    TextView tvInfoMaxSpeed;

    @BindView(R.id.tvInfoMinAltitude)
    TextView tvInfoMinAltitude;

    @BindView(R.id.tvInfoMaxAltitude)
    TextView tvInfoMaxAltitude;

    @BindView(R.id.btnInfoSave)
    Button btnInfoSave;

    @BindView(R.id.btnInfoMap)
    Button btnInfoMap;

    @BindView(R.id.btnInfoCancel)
    Button btnInfoCancel;

    private SharedPreferences infoSharedPreferences;

    private boolean isSaved;

    private BroadcastReceiver tiReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        injectLayout(this, R.layout.activity_track_info, activityContent);

        infoSharedPreferences = getSharedPreferences(NAME, MODE_PRIVATE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setTrackInfo(infoSharedPreferences);
        regReceiver();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregReceiver();
    }

    @OnClick(R.id.btnInfoSave)
    public void btnInfoSave() {
        if (!isSaved) {
            showWithAnim(btnInfoSave);
            startVibration(this, 100);
            playSound(this, R.raw.on_click);
            saveTrack();
        }
    }

    @OnClick(R.id.btnInfoMap)
    public void btnInfoMap() {
        showWithAnim(btnInfoMap);
        startVibration(this, 100);
        playSound(this, R.raw.on_click);
        Intent infoStartMapsActivity = new Intent(this, MapsActivity.class);
        infoStartMapsActivity.putExtra(CLASS_NAME, TRACK_INFO_ACTIVITY); // put the activity name
        startActivity(infoStartMapsActivity);
    }

    @OnClick(R.id.btnInfoCancel)
    public void btnInfoCancel() {
        showWithAnim(btnInfoCancel);
        startVibration(this, 100);
        playSound(this, R.raw.on_click);
        if (!isSaved) {
            SaveTrackWarningDialog dialog = new SaveTrackWarningDialog();
            dialog.show(getFragmentManager(), "SaveTrackWarningDialog");
        } else {
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        if (!isSaved) {
            SaveTrackWarningDialog dialog = new SaveTrackWarningDialog();
            dialog.show(getFragmentManager(), "SaveTrackWarningDialog");
        } else {
            finish();
        }
    }

    private void setTrackInfo(SharedPreferences sp) {
        tvInfoDate.setText(sp.getString(TRACK_DATE, getString(R.string.no_value)));
        tvInfoStart.setText(sp.getString(START_TIME, getString(R.string.no_value)));
        tvInfoEnd.setText(sp.getString(END_TIME, getString(R.string.no_value)));
        tvInfoDuration.setText(sp.getString(TRACK_DURATION, getString(R.string.no_value)));
        tvInfoActiveTime.setText(sp.getString(ACTIVE_TIME, getString(R.string.no_value)));
        tvInfoLength.setText(distanceToString(sp.getString(DISTANCE, getString(R.string.no_value)), getString(R.string.no_value)));
        tvInfoAverageSpeed.setText(sp.getString(AVERAGE_SPEED, getString(R.string.no_value)));
        tvInfoMaxSpeed.setText(sp.getString(MAX_SPEED, getString(R.string.no_value)));
        tvInfoMinAltitude.setText(sp.getString(MIN_ALTITUDE, getString(R.string.no_value)));
        tvInfoMaxAltitude.setText(sp.getString(MAX_ALTITUDE, getString(R.string.no_value)));
    }

    /**
     * open AlertDialog where we put name of track
     */
    private void saveTrack() {
        InsertTrackNameDialog dialog = new InsertTrackNameDialog();
        dialog.show(getFragmentManager(), "InsertTrackNameDialog");
    }

    private void regReceiver() {
        tiReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                isSaved = true;
                startActivity(new Intent(TrackInfoActivity.this, TrackListActivity.class));
            }
        };
        registerReceiver(tiReceiver, new IntentFilter(TRACK_SAVED));
    }

    private void unregReceiver() {
        unregisterReceiver(tiReceiver);
    }

}
