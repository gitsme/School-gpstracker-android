package me.gits.edu.droid;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MySQLiteHelper extends SQLiteOpenHelper{
	private static final String DATABASE_NAME = "gpstracks.db";
	private static final int DATABASE_VERSION = 1;
	
	public static final String TABLE_GPSTRACKS = "gpstracks";
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_GPS_LONG = "longitude";
	public static final String COLUMN_GPS_LATI = "latitude";
	public static final String COLUMN_TIME = "time";
	public static final String COLUMN_TRACK_ID = "track_id";
	
	// DB creation sql statement
	private static final String DATABASE_CREATE = "create table "
					+ TABLE_GPSTRACKS + "(" 
					+ COLUMN_ID + " integer primary key autoincrement, " 
					+ COLUMN_GPS_LONG + " text not null, "
					+ COLUMN_GPS_LATI + " text not null, "
					+ COLUMN_TIME + " long not null, "
					+ COLUMN_TRACK_ID + " integer not null);";

	public MySQLiteHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		Log.d(MySQLiteHelper.class.getName(), "onCreate called");
		db.execSQL(DATABASE_CREATE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.d(MySQLiteHelper.class.getName(), "onUpgrade called");
		Log.w(MySQLiteHelper.class.getName(),
				"Upgrading database from version " 
						+ oldVersion + " to "
						+ newVersion + ", which will destroy all old data");
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_GPSTRACKS);
		onCreate(db);
	}
}
