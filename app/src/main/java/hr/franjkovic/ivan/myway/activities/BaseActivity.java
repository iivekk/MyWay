package hr.franjkovic.ivan.myway.activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;

import hr.franjkovic.ivan.myway.R;
import hr.franjkovic.ivan.myway.settings.CustomPreferenceActivity;

public class BaseActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout mDrawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;
    protected LinearLayout activityContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_base);

        initWidgets();
        navigationView.setNavigationItemSelectedListener(this);

        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.settings_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;

            case R.id.action_settings:
                startActivity(new Intent(getApplicationContext(), CustomPreferenceActivity.class));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        item.setChecked(true);
        mDrawerLayout.closeDrawers();

        switch (item.getItemId()) {
            case R.id.home_screen:
                Intent i = new Intent(this, HomeActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(i);
                break;

            case R.id.distance_screen:
                startActivity(new Intent(this, DistanceActivity.class));
                break;

            case R.id.speed_screen:
                startActivity(new Intent(this, SpeedActivity.class));
                break;

            case R.id.altitude_screen:
                startActivity(new Intent(this, AltitudeActivity.class));
                break;

            case R.id.track_list_screen:
                startActivity(new Intent(this, TrackListActivity.class));
                break;

            case R.id.exit_from_app:
                finishAffinity();
                break;

        }
        return true;
    }

    private void initWidgets() {
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        activityContent = (LinearLayout) findViewById(R.id.activity_content);
    }

}
