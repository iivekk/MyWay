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

import static hr.franjkovic.ivan.myway.tools.MyTools.Animations.showWithAnim;
import static hr.franjkovic.ivan.myway.tools.MyTools.Constants.BROADCAST_ALERT.NEW_LOCATION_ALERT;
import static hr.franjkovic.ivan.myway.tools.MyTools.Keys.SHARED_PREFERENCES.ALTITUDE;
import static hr.franjkovic.ivan.myway.tools.MyTools.Keys.SHARED_PREFERENCES.MAX_ALTITUDE;
import static hr.franjkovic.ivan.myway.tools.MyTools.Keys.SHARED_PREFERENCES.MIN_ALTITUDE;
import static hr.franjkovic.ivan.myway.tools.MyTools.Keys.SHARED_PREFERENCES.NAME;
import static hr.franjkovic.ivan.myway.tools.MyTools.StaticMethods.injectLayout;
import static hr.franjkovic.ivan.myway.tools.MyTools.StaticMethods.playSound;
import static hr.franjkovic.ivan.myway.tools.MyTools.StaticMethods.startVibration;

public class AltitudeActivity extends BaseActivity {

    @BindView(R.id.tvAltitudeMin)
    TextView tvAltitudeMin;

    @BindView(R.id.tvAltitudeMax)
    TextView tvAltitudeMax;

    @BindView(R.id.tvAltitude)
    TextView tvAltitude;

    @BindView(R.id.btnAltitudeBack)
    Button btnAltitudeBack;

    private BroadcastReceiver altitudeReceiver;

    private SharedPreferences altitudeSharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        injectLayout(this, R.layout.activity_altitude, activityContent);

        altitudeSharedPreferences = getSharedPreferences(NAME, MODE_PRIVATE);
    }

    @Override
    protected void onResume() {
        super.onResume();

        tvAltitude.setText(altitudeSharedPreferences.getString(ALTITUDE, getString(R.string.no_value)));
        tvAltitudeMin.setText(altitudeSharedPreferences.getString(MIN_ALTITUDE, getString(R.string.no_value)));
        tvAltitudeMax.setText(altitudeSharedPreferences.getString(MAX_ALTITUDE, getString(R.string.no_value)));
        regReceiver();
    }

    @Override
    protected void onPause() {
        super.onPause();

        unregReceiver();
    }

    @OnClick(R.id.btnAltitudeBack)
    public void btnAltitudeBack() {
        showWithAnim(btnAltitudeBack);
        startVibration(this, 100);
        playSound(this, R.raw.on_click);
        finish();
    }

    private void regReceiver() {
        altitudeReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                tvAltitude.setText(altitudeSharedPreferences.getString(ALTITUDE, getString(R.string.no_value)));
                tvAltitudeMin.setText(altitudeSharedPreferences.getString(MIN_ALTITUDE, getString(R.string.no_value)));
                tvAltitudeMax.setText(altitudeSharedPreferences.getString(MAX_ALTITUDE, getString(R.string.no_value)));
            }
        };
        registerReceiver(altitudeReceiver, new IntentFilter(NEW_LOCATION_ALERT));
    }

    private void unregReceiver() {
        unregisterReceiver(altitudeReceiver);
    }

}
