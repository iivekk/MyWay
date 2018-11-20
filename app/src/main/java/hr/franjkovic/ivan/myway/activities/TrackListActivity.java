package hr.franjkovic.ivan.myway.activities;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.ArrayList;

import butterknife.BindView;
import hr.franjkovic.ivan.myway.R;
import hr.franjkovic.ivan.myway.adapter.OnClickItemListener;
import hr.franjkovic.ivan.myway.adapter.TrackListAdapter;
import hr.franjkovic.ivan.myway.database.Track;

import static hr.franjkovic.ivan.myway.database.MyWayApp.database;
import static hr.franjkovic.ivan.myway.tools.MyTools.Constants.BROADCAST_ALERT.TRACK_SAVED;
import static hr.franjkovic.ivan.myway.tools.MyTools.Keys.SELECTED_TRACK;
import static hr.franjkovic.ivan.myway.tools.MyTools.StaticMethods.injectLayout;
import static hr.franjkovic.ivan.myway.tools.MyTools.StaticMethods.playSound;
import static hr.franjkovic.ivan.myway.tools.MyTools.StaticMethods.startVibration;

public class TrackListActivity extends BaseActivity implements OnClickItemListener {

    @BindView(R.id.rvTrackList)
    RecyclerView rvTrackList;

    private TrackListAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    BroadcastReceiver tlReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        injectLayout(this, R.layout.activity_track_list, activityContent);

        mLayoutManager = new LinearLayoutManager(this);
        mAdapter = new TrackListAdapter(this, new ArrayList<Track>());
        mAdapter.setClickListener(this);
        rvTrackList.setLayoutManager(mLayoutManager);
        rvTrackList.setItemAnimator(new DefaultItemAnimator());
        rvTrackList.setAdapter(mAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateTrackList();
        regReceiver();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregReceiver();
    }

    private void updateTrackList() {
        mAdapter.updateList(database.trackDao().getAll());
    }

    @Override
    public void onItemClick(View view, int position) {
        startVibration(this, 100);
        playSound(this, R.raw.on_click);
        Track selectedTrack = mAdapter.getTrack(position); // getting selected track
        Intent openTrackInfo = new Intent(this, SavedTrackInfoActivity.class);
        openTrackInfo.putExtra(SELECTED_TRACK, selectedTrack); // put a selected track
        startActivity(openTrackInfo);
    }

    // opens dialog where we choose whether we want to delete track
    @Override
    public void onItemLongClick(final View view, final int position) {
        startVibration(this, 100);
        playSound(this, R.raw.on_click);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIconAttribute(android.R.attr.alertDialogIcon);
        builder.setTitle(R.string.delete_track_title_dialog);
        builder.setMessage(R.string.delete_track_warning_message);

        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                database.trackDao().delete(mAdapter.getTrack(position));
                updateTrackList();
            }
        });

        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void regReceiver() {
        tlReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                updateTrackList();
            }
        };
        registerReceiver(tlReceiver, new IntentFilter(TRACK_SAVED));
    }

    private void unregReceiver() {
        unregisterReceiver(tlReceiver);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i = new Intent(this, HomeActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(i);
    }

}
