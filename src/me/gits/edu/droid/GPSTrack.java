package me.gits.edu.droid;

public class GPSTrack {
	private long id;
	private String latitude;
	private String longitude;
	private long time;
	private int trackId;
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getLatitude() {
		return latitude;
	}
	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}
	public String getLongitude() {
		return longitude;
	}
	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}
	public long getTime() {
		return time;
	}
	public void setTime(long time) {
		this.time = time;
	}
	public String toString() {
		return "Long: " + longitude +
				"\nLati: " + latitude +
				"\nTime: " + time +
				"\nTrack: " + trackId;
	}
	public int getTrackId() {
		return trackId;
	}
	public void setTrackId(int trackId) {
		this.trackId = trackId;
	}
}
