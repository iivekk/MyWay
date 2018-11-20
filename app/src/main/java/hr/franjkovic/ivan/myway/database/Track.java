package hr.franjkovic.ivan.myway.database;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;

@Entity(tableName = "tracks")
public class Track implements Parcelable {

    @PrimaryKey(autoGenerate = true)
    private int trackId;

    @ColumnInfo(name = "name")
    private String trackName;

    @ColumnInfo(name = "date")
    private String trackDate;

    @ColumnInfo(name = "start_time")
    private String startTime;

    @ColumnInfo(name = "end_time")
    private String endTime;

    @ColumnInfo(name = "duration")
    private String trackDuration;

    @ColumnInfo(name = "active_time")
    private String activeTime;

    @ColumnInfo(name = "length")
    private String trackLength;

    @ColumnInfo(name = "average_speed")
    private String averageSpeed;

    @ColumnInfo(name = "max_speed")
    private String maxSpeed;

    @ColumnInfo(name = "min_altitude")
    private String minAltitude;

    @ColumnInfo(name = "max_altitude")
    private String maxAltitude;

    @ColumnInfo(name = "latitude_list")
    private String latitudeList;

    @ColumnInfo(name = "longitude_list")
    private String longitudeList;

    @ColumnInfo(name = "marker_lat")
    private String markerLatList;

    @ColumnInfo(name = "marker_lng")
    private String markerLngList;

    public Track(String trackName, String trackDate, String startTime, String endTime
            , String trackDuration, String activeTime, String trackLength, String averageSpeed
            , String maxSpeed, String minAltitude, String maxAltitude, String latitudeList
            , String longitudeList, String markerLatList, String markerLngList) {
        this.trackName = trackName;
        this.trackDate = trackDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.trackDuration = trackDuration;
        this.activeTime = activeTime;
        this.trackLength = trackLength;
        this.averageSpeed = averageSpeed;
        this.maxSpeed = maxSpeed;
        this.minAltitude = minAltitude;
        this.maxAltitude = maxAltitude;
        this.latitudeList = latitudeList;
        this.longitudeList = longitudeList;
        this.markerLatList = markerLatList;
        this.markerLngList = markerLngList;
    }

    public int getTrackId() {
        return trackId;
    }

    public String getTrackName() {
        return trackName;
    }

    public String getTrackDate() {
        return trackDate;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public String getTrackDuration() {
        return trackDuration;
    }

    public String getActiveTime() {
        return activeTime;
    }

    public String getTrackLength() {
        return trackLength;
    }

    public String getAverageSpeed() {
        return averageSpeed;
    }

    public String getMaxSpeed() {
        return maxSpeed;
    }

    public String getMinAltitude() {
        return minAltitude;
    }

    public String getMaxAltitude() {
        return maxAltitude;
    }

    public String getLatitudeList() {
        return latitudeList;
    }

    public String getLongitudeList() {
        return longitudeList;
    }

    public String getMarkerLatList() {
        return markerLatList;
    }

    public String getMarkerLngList() {
        return markerLngList;
    }

    public void setTrackId(int trackId) {
        this.trackId = trackId;
    }

    public void setTrackName(String trackName) {
        this.trackName = trackName;
    }

    public void setTrackDate(String trackDate) {
        this.trackDate = trackDate;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public void setTrackDuration(String trackDuration) {
        this.trackDuration = trackDuration;
    }

    public void setActiveTime(String activeTime) {
        this.activeTime = activeTime;
    }

    public void setTrackLength(String trackLength) {
        this.trackLength = trackLength;
    }

    public void setAverageSpeed(String averageSpeed) {
        this.averageSpeed = averageSpeed;
    }

    public void setMaxSpeed(String maxSpeed) {
        this.maxSpeed = maxSpeed;
    }

    public void setMinAltitude(String minAltitude) {
        this.minAltitude = minAltitude;
    }

    public void setMaxAltitude(String maxAltitude) {
        this.maxAltitude = maxAltitude;
    }

    public void setLatitudeList(String latitudeList) {
        this.latitudeList = latitudeList;
    }

    public void setLongitudeList(String longitudeList) {
        this.longitudeList = longitudeList;
    }

    public void setMarkerLatList(String markerLatList) {
        this.markerLatList = markerLatList;
    }

    public void setMarkerLngList(String markerLngList) {
        this.markerLngList = markerLngList;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.trackId);
        dest.writeString(this.trackName);
        dest.writeString(this.trackDate);
        dest.writeString(this.startTime);
        dest.writeString(this.endTime);
        dest.writeString(this.trackDuration);
        dest.writeString(this.activeTime);
        dest.writeString(this.trackLength);
        dest.writeString(this.averageSpeed);
        dest.writeString(this.maxSpeed);
        dest.writeString(this.minAltitude);
        dest.writeString(this.maxAltitude);
        dest.writeString(this.latitudeList);
        dest.writeString(this.longitudeList);
        dest.writeString(this.markerLatList);
        dest.writeString(this.markerLngList);
    }

    public static final Parcelable.Creator<Track> CREATOR = new Parcelable.Creator<Track>() {
        public Track createFromParcel(Parcel in) {
            return new Track(in);
        }

        @Override
        public Track[] newArray(int size) {
            return new Track[size];
        }
    };

    private Track(Parcel in) {
        trackId = in.readInt();
        trackName = in.readString();
        trackDate = in.readString();
        startTime = in.readString();
        endTime = in.readString();
        trackDuration = in.readString();
        activeTime = in.readString();
        trackLength = in.readString();
        averageSpeed = in.readString();
        maxSpeed = in.readString();
        minAltitude = in.readString();
        maxAltitude = in.readString();
        latitudeList = in.readString();
        longitudeList = in.readString();
        markerLatList = in.readString();
        markerLngList = in.readString();
    }
}
