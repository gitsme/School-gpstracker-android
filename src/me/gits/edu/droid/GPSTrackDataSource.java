package me.gits.edu.droid;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class GPSTrackDataSource {
	private SQLiteDatabase database;
	private MySQLiteHelper dbHelper;
	private String[] allColumns = { MySQLiteHelper.COLUMN_ID,
									MySQLiteHelper.COLUMN_GPS_LONG,
									MySQLiteHelper.COLUMN_GPS_LATI,
									MySQLiteHelper.COLUMN_TIME,
									MySQLiteHelper.COLUMN_TRACK_ID};
	
	public GPSTrackDataSource(Context context) {
		dbHelper = new MySQLiteHelper(context);
	}
	
	public void open() throws SQLException {
		database = dbHelper.getWritableDatabase();
	}
	
	public void close() {
		dbHelper.close();
	}
	
	/**
	 * Return all gps positions from the database
	 * @return
	 */
	public List<GPSTrack> getAllPositions(){
		List<GPSTrack> positions = new ArrayList<GPSTrack>();
		Cursor cursor = database.query(MySQLiteHelper.TABLE_GPSTRACKS, 
						allColumns, null, null, null, null, null);
		cursor.moveToFirst();
		
		while(!cursor.isAfterLast()) {
			GPSTrack pos = cursorToGPSTrack(cursor);
			positions.add(pos);
			cursor.moveToNext();
		}
		cursor.close();
		return positions;
	}
	
	/**
	 * Return the route from the database, with the given trackid
	 * @return
	 */
	public List<GPSTrack> getRoute(int _trackId) {
		List<GPSTrack> positions = new ArrayList<GPSTrack>();
		Cursor cursor = database.query(MySQLiteHelper.TABLE_GPSTRACKS, 
						allColumns, "track_id like " + _trackId, null, null, null, null);
		cursor.moveToFirst();
		
		while(!cursor.isAfterLast()) {
			GPSTrack pos = cursorToGPSTrack(cursor);
			positions.add(pos);
			cursor.moveToNext();
		}
		cursor.close();
		return positions;
	}
	
	/**
	 * Adds a gps position to the database
	 * @param longitude
	 * @param latitude
	 * @param time
	 * @param trackId
	 * @return
	 */
	public GPSTrack createGPSTrack(double longitude, double latitude, long time, int trackId) {
		ContentValues values = new ContentValues();
		values.put(MySQLiteHelper.COLUMN_GPS_LATI, latitude);
		values.put(MySQLiteHelper.COLUMN_GPS_LONG, longitude);
		values.put(MySQLiteHelper.COLUMN_TIME, time);
		values.put(MySQLiteHelper.COLUMN_TRACK_ID, trackId);
		
		Log.d(GPSTrackDataSource.class.getName(), "createGPSTrack called");
		long insertId = database.insert(MySQLiteHelper.TABLE_GPSTRACKS, null, values);
		
		return null;
	}
	
	/**
	 * Converts the Cursor to GPSTrack object
	 * @param cursor
	 * @return
	 */
	private GPSTrack cursorToGPSTrack(Cursor cursor) {
		GPSTrack gpstrack = new GPSTrack();
		gpstrack.setId(cursor.getLong(0));
		gpstrack.setLongitude(cursor.getString(1));
		gpstrack.setLatitude(cursor.getString(2));
		gpstrack.setTime(cursor.getLong(3));
		gpstrack.setTrackId(cursor.getInt(4));
		
		return gpstrack;
	}
	
	/**
	 * Get the largest trackid from the database
	 * @return
	 */
	public int getLargestTrackId() {
		Cursor cursor = database.rawQuery("SELECT MAX(track_id) FROM gpstracks", null);
		cursor.moveToFirst();
		try{
			return cursor.getInt(0);
		} catch (Exception e) {
			return 0;
		}
	}
}
