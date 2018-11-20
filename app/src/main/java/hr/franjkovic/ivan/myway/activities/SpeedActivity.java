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
import static hr.franjkovic.ivan.myway.tools.MyTools.Keys.SHARED_PREFERENCES.AVERAGE_SPEED;
import static hr.franjkovic.ivan.myway.tools.MyTools.Keys.SHARED_PREFERENCES.MAX_SPEED;
import static hr.franjkovic.ivan.myway.tools.MyTools.Keys.SHARED_PREFERENCES.NAME;
import static hr.franjkovic.ivan.myway.tools.MyTools.Keys.SHARED_PREFERENCES.SPEED;
import static hr.franjkovic.ivan.myway.tools.MyTools.StaticMethods.injectLayout;
import static hr.franjkovic.ivan.myway.tools.MyTools.StaticMethods.playSound;
import static hr.franjkovic.ivan.myway.tools.MyTools.StaticMethods.startVibration;

public class SpeedActivity extends BaseActivity {

    @BindView(R.id.tvSpeedMax)
    TextView tvSpeedMax;

    @BindView(R.id.tvSpeedAverage)
    TextView tvSpeedAverage;

    @BindView(R.id.tvSpeed)
    TextView tvSpeed;

    @BindView(R.id.btnSpeedBack)
    Button btnSpeedBack;

    private BroadcastReceiver speedReceiver;

    private SharedPreferences speedSharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        injectLayout(this, R.layout.activity_speed, activityContent);

        speedSharedPreferences = getSharedPreferences(NAME, MODE_PRIVATE);
    }

    @Override
    protected void onResume() {
        super.onResume();

        tvSpeed.setText(speedSharedPreferences.getString(SPEED, getString(R.string.no_value)));
        tvSpeedMax.setText(speedSharedPreferences.getString(MAX_SPEED, getString(R.string.no_value)));
        tvSpeedAverage.setText(speedSharedPreferences.getString(AVERAGE_SPEED, getString(R.string.no_value)));
        regReceiver();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregReceiver();
    }

    @OnClick(R.id.btnSpeedBack)
    public void btnSpeedBack() {
        showWithAnim(btnSpeedBack);
        startVibration(this, 100);
        playSound(this, R.raw.on_click);
        finish();
    }

    private void regReceiver() {
        speedReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                tvSpeed.setText(speedSharedPreferences.getString(SPEED, getString(R.string.no_value)));
                tvSpeedMax.setText(speedSharedPreferences.getString(MAX_SPEED, getString(R.string.no_value)));
                tvSpeedAverage.setText(speedSharedPreferences.getString(AVERAGE_SPEED, getString(R.string.no_value)));
            }
        };
        registerReceiver(speedReceiver, new IntentFilter(NEW_LOCATION_ALERT));
    }

    private void unregReceiver() {
        unregisterReceiver(speedReceiver);
    }

}
