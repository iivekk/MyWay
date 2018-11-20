package hr.franjkovic.ivan.myway.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.OnClick;
import hr.franjkovic.ivan.myway.R;
import hr.franjkovic.ivan.myway.database.Track;

import static hr.franjkovic.ivan.myway.tools.MyTools.Animations.showWithAnim;
import static hr.franjkovic.ivan.myway.tools.MyTools.Constants.SAVED_TRACK_INFO_ACTIVITY;
import static hr.franjkovic.ivan.myway.tools.MyTools.Keys.CLASS_NAME;
import static hr.franjkovic.ivan.myway.tools.MyTools.Keys.SELECTED_TRACK;
import static hr.franjkovic.ivan.myway.tools.MyTools.Keys.SELECTED_TRACK_LATITUDES;
import static hr.franjkovic.ivan.myway.tools.MyTools.Keys.SELECTED_TRACK_LAT_MARK_LIST;
import static hr.franjkovic.ivan.myway.tools.MyTools.Keys.SELECTED_TRACK_LNG_MARK_LIST;
import static hr.franjkovic.ivan.myway.tools.MyTools.Keys.SELECTED_TRACK_LONGITUDES;
import static hr.franjkovic.ivan.myway.tools.MyTools.StaticMethods.*;

public class SavedTrackInfoActivity extends BaseActivity {

    @BindView(R.id.tvInfoNameS)
    TextView tvInfoNameS;

    @BindView(R.id.tvInfoDateS)
    TextView tvInfoDateS;

    @BindView(R.id.tvInfoStartS)
    TextView tvInfoStartS;

    @BindView(R.id.tvInfoEndS)
    TextView tvInfoEndS;

    @BindView(R.id.tvInfoDurationS)
    TextView tvInfoDurationS;

    @BindView(R.id.tvInfoActiveTimeS)
    TextView tvInfoActiveTimeS;

    @BindView(R.id.tvInfoLengthS)
    TextView tvInfoLengthS;

    @BindView(R.id.tvInfoAverageSpeedS)
    TextView tvInfoAverageSpeedS;

    @BindView(R.id.tvInfoMaxSpeedS)
    TextView tvInfoMaxSpeedS;

    @BindView(R.id.tvInfoMinAltitudeS)
    TextView tvInfoMinAltitudeS;

    @BindView(R.id.tvInfoMaxAltitudeS)
    TextView tvInfoMaxAltitudeS;

    @BindView(R.id.btnInfoMapS)
    Button btnInfoMapS;

    @BindView(R.id.btnInfoBackS)
    Button btnInfoBackS;

    private Track mSelectedTrack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        injectLayout(this, R.layout.activity_saved_track_info, activityContent);

        // getting an object from Intent that represents the selected track
        mSelectedTrack = getIntent().getParcelableExtra(SELECTED_TRACK);

    }

    @Override
    protected void onResume() {
        super.onResume();
        setSavedTrackInfo(mSelectedTrack);
    }

    @OnClick(R.id.btnInfoMapS)
    public void btnInfoMapS() {
        showWithAnim(btnInfoMapS);
        startVibration(this, 100);
        playSound(this, R.raw.on_click);
        Intent startMapsSaved = new Intent(this, MapsActivity.class);
        startMapsSaved.putExtra(CLASS_NAME, SAVED_TRACK_INFO_ACTIVITY); // put the activity name
        startMapsSaved.putExtra(SELECTED_TRACK_LATITUDES, mSelectedTrack.getLatitudeList()); // put a String from which we will get a Latitude list, from the selected track
        startMapsSaved.putExtra(SELECTED_TRACK_LONGITUDES, mSelectedTrack.getLongitudeList()); // put a String from which we will get a Longitude list, from the selected track
        startMapsSaved.putExtra(SELECTED_TRACK_LAT_MARK_LIST, mSelectedTrack.getMarkerLatList());  // put a String from which we will get a Latitude list, from the selected track
        startMapsSaved.putExtra(SELECTED_TRACK_LNG_MARK_LIST, mSelectedTrack.getMarkerLngList());  // put a String from which we will get a Longitude list, from the selected track
        startActivity(startMapsSaved);
    }

    @OnClick(R.id.btnInfoBackS)
    public void btnInfoBackS() {
        showWithAnim(btnInfoBackS);
        startVibration(this, 100);
        playSound(this, R.raw.on_click);
        finish();
    }

    private void setSavedTrackInfo(Track track) {
        tvInfoNameS.setText(track.getTrackName());
        tvInfoDateS.setText(track.getTrackDate());
        tvInfoStartS.setText(track.getStartTime());
        tvInfoEndS.setText(track.getEndTime());
        tvInfoDurationS.setText(track.getTrackDuration());
        tvInfoActiveTimeS.setText(track.getActiveTime());
        tvInfoLengthS.setText(distanceToString(track.getTrackLength(), "/"));
        tvInfoAverageSpeedS.setText(track.getAverageSpeed());
        tvInfoMaxSpeedS.setText(track.getMaxSpeed());
        tvInfoMinAltitudeS.setText(track.getMinAltitude());
        tvInfoMaxAltitudeS.setText(track.getMaxAltitude());
    }
}
