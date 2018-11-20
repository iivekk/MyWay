package hr.franjkovic.ivan.myway.tools;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.LinearLayout;

import butterknife.ButterKnife;
import hr.franjkovic.ivan.myway.R;

public class MyTools {

    public static class Animations {

        /**
         * Shows animation over a particular view.
         *
         * @param v - target view
         */
        public static void showWithAnim(View v) {
            v.setAlpha(1f);
            AlphaAnimation animation = new AlphaAnimation(0.1f, 1.0f);
            animation.setDuration(300);
            animation.setFillAfter(true);
            animation.hasEnded();
            v.startAnimation(animation);
        }
    }

    public static class Constants {

        public static final int REQUEST_LOCATION_PERMISSION = 1;
        public static final String HOME_ACTIVITY = "home_activity";
        public static final String TRACK_INFO_ACTIVITY = "track_info_activity";
        public static final String SAVED_TRACK_INFO_ACTIVITY = "saved_track_info_activity";

        public interface NOTIFICATION_ID {
            int FOREGROUND_SERVICE = 1;
        }

        public interface BROADCAST_ALERT {
            String NEW_LOCATION_ALERT = "new_loc_alert";
            String START_TRACKING = "start_tracking";
            String TRACK_SAVED = "track_saved";
        }
    }

    public static class Keys {

        public static final String CLASS_NAME = "class_name";
        public static final String SELECTED_TRACK = "selected_track";
        public static final String SELECTED_TRACK_LATITUDES = "selected_track_latitudes";
        public static final String SELECTED_TRACK_LONGITUDES = "selected_track_longitudes";
        public static final String SELECTED_TRACK_LAT_MARK_LIST = "selected_track_lat_mark_list";
        public static final String SELECTED_TRACK_LNG_MARK_LIST = "selected_track_lng_mark_list";
        public static final String TRACK_NAME = "track_name";

        public interface SHARED_PREFERENCES {
            String NAME = "shared_preferences";
            String LATITUDE = "latitude";
            String LATITUDES_LIST = "latitudes_list";
            String LONGITUDE = "longitude";
            String LONGITUDES_LIST = "longitudes_list";
            String DISTANCE = "distance";
            String SPEED = "speed";
            String MAX_SPEED = "max_speed";
            String AVERAGE_SPEED = "average_speed";
            String ALTITUDE = "altitude";
            String MIN_ALTITUDE = "min_altitude";
            String MAX_ALTITUDE = "max_altitude";
            String START_TIME = "start_time";
            String END_TIME = "end_time";
            String TRACK_DATE = "track_date";
            String TRACK_DURATION = "track_duration";
            String ACTIVE_TIME = "active_tme";
            String MARKER_LAT_LIST = "marker_lat_list";
            String MARKER_LNG_LIST = "marker_lng_list";
        }
    }

    public static class StaticMethods {

        /**
         * checks if there is a value and therefore returns a specific String
         *
         * @param verifiedValue - verified value
         * @param defValue      - default value
         * @return String value of distance
         */
        public static String distanceToString(String verifiedValue, String defValue) {
            if (verifiedValue.equals(defValue)) {
                return defValue;
            } else {
                return verifiedValue + " km";
            }
        }

        /**
         * checks if the String value is a Double value
         *
         * @param verifiedValue - verified value
         * @return true if is double, or false is not a double
         */
        public static boolean isDouble(String verifiedValue) {
            try {
                double d = Double.parseDouble(verifiedValue);
                return true;
            } catch (Exception e) {
                return false;
            }
        }

        /**
         * checks if the String value is a Float value
         *
         * @param verifiedValue - verified value
         * @return true if is float, or false is not a float
         */
        public static boolean isFloat(String verifiedValue) {
            try {
                float f = Float.parseFloat(verifiedValue);
                return true;
            } catch (Exception e) {
                return false;
            }
        }

        /**
         * Inject certain layout in a default layout
         *
         * @param context  - current context
         * @param resource - certain layout
         * @param layout   - default layout
         */
        public static void injectLayout(Context context, int resource, LinearLayout layout) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View contentView = inflater.inflate(resource, null, false);
            ButterKnife.bind(context, contentView);
            layout.addView(contentView, 0);
        }

        /**
         * Checks in the settings if the sound is on, and play start
         *
         * @param context - context
         * @param resId   - resource Id for sound
         */
        public static void playSound(Context context, int resId) {
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
            if (sp.getBoolean(context.getString(R.string.sound_on_off), true)) {
                MediaPlayer player = MediaPlayer.create(context, resId);
                player.start();
            }
        }

        /**
         * Checks in the settings if the vibration is on, and vibrate start
         *
         * @param context      - context
         * @param milliseconds - vibration duration in milliseconds
         */
        public static void startVibration(Context context, long milliseconds) {
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
            if (sp.getBoolean(context.getString(R.string.vibration_on_off), true)) {
                Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
                vibrator.vibrate(milliseconds);
            }
        }

        /**
         * if the value of a minute is less than 10, it puts zero in front
         *
         * @param minutes - minute value
         * @return String value of minute
         */
        public static String formatMinutes(Long minutes) {
            if (minutes < 10) {
                return "0" + minutes;
            }
            return "" + minutes;
        }

        /**
         * if the value of a seconds is less than 10, it puts zero in front
         *
         * @param seconds - seconds value
         * @return String value of seconds
         */
        public static String formatSeconds(Long seconds) {
            if (seconds < 10) {
                return "0" + seconds;
            }
            return "" + seconds;
        }

        /**
         * @param milliseconds - milliseconds value
         * @return String value of tenths
         */
        public static String formatTenths(long milliseconds) {
            char tenths = String.valueOf(milliseconds).charAt(0);
            return "" + tenths;
        }

        /**
         * @param time - the value of time in milliseconds
         * @return String value of time in format - h:mm:ss.S
         */
        public static String formatTime(long time) {
            long milliseconds = time % 1000;
            long seconds = time / 1000 % 60;
            long minutes = time / (60 * 1000) % 60;
            long hours = time / (60 * 60 * 1000);
            return hours + ":" + formatMinutes(minutes) + ":" + formatSeconds(seconds) + "." + formatTenths(milliseconds);
        }

        /**
         * @param startTime - long value of start time
         * @param stopTime  - long value of end time
         * @return String value of time difference
         */
        public static String timeDifferenceToString(long startTime, long stopTime) {
            long timeDifference = stopTime - startTime;
            return formatTime(timeDifference);
        }
    }
}
