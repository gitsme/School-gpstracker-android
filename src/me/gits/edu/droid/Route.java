/**
 * Class used for later expansion of the app
 */
package me.gits.edu.droid;

import java.util.List;

public class Route {
	private long id;
	private List<GPSTrack> positions;
	private long endTime;
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public List<GPSTrack> getPositions() {
		return positions;
	}
	public void setPositions(List<GPSTrack> positions) {
		this.positions = positions;
	}
	public long getEndTime() {
		return endTime;
	}
	public void setEndTime(long endTime) {
		this.endTime = endTime;
	}
}
