/**
 * Main activity for the app
 */
package me.gits.edu.droid;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.Overlay;
import com.google.android.maps.Projection;

public class GPSTrackActivity extends MapActivity {
	int trackId = 0;
	long interval = 5000;
	long preTime = 0;
	long newTime = 0;
	long diffTime = 0;
	long minTime = 5000; // minimum time in milliseconds for how often to update location
	float minDistance = 0; // minimum distance traveled since last location
	private MapView mapView = null;
	private MapController mapController = null;
	private MyLocationOverlay whereAmI = null;
	private GPSTrackDataSource datasource = null;
	private ToggleButton trackBtn = null;
	private List<Overlay> mapOverlays = null;
	private Projection projection = null;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mapview);
        
        datasource = new GPSTrackDataSource(this);
        datasource.open();
        
        mapView = (MapView)findViewById(R.id.mapview);
        mapView.setBuiltInZoomControls(true);
        
        mapController = mapView.getController();
        mapController.setZoom(15);
        
        whereAmI = new MyLocationOverlay(this, mapView);
        
        mapOverlays = mapView.getOverlays();
        projection = mapView.getProjection();
        mapOverlays.add(whereAmI);
        mapOverlays.add(new RouteOverlay(this, mapView));
        mapView.postInvalidate();
        
        LocationManager locManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        LocationListener locListener = new MyLocationListener();
        
        trackBtn = (ToggleButton)findViewById(R.id.toggleButton1);
        trackBtn.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                	// When the togglebutton is turned on again, a new trackID is set by retrieving the largest one from the DB, and +1
                	int lastTrackId = datasource.getLargestTrackId();
                	trackId = lastTrackId + 1;
                }
            }
        });
        
    	locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, minTime, minDistance, locListener);
    }
    
    @Override
    public void onResume() {
    	super.onResume();
    	whereAmI.enableMyLocation();
    	whereAmI.runOnFirstFix(new Runnable() {
    		public void run() {
    			mapController.setCenter(whereAmI.getMyLocation());
    		}
    	});
    }
    
    @Override
    public void onPause() {
    	super.onPause();
    	whereAmI.disableMyLocation();
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.listPosForLatestTrack:
                Intent poslist = new Intent(GPSTrackActivity.this, PositionShowActivity.class);
                GPSTrackActivity.this.startActivityForResult(poslist, 1);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    
	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}
	
	
    /**
     * My location Listener
     */
    public class MyLocationListener implements LocationListener {

		@Override
		public void onLocationChanged(Location location) {
			double latitude = location.getLatitude();
			double longitude = location.getLongitude();
			long time = location.getTime();
			
			if(trackBtn.isChecked())
				datasource.createGPSTrack(longitude, latitude, time, trackId);
			
			setTime(location);
			diffTime = location.getTime() - preTime;
			
			String text = "My current location is: " +
					"\nLatitud = " + latitude +
					"\nLongitud = " + longitude + 
					"\nTime: " + time + 
					"\nTimeDiff: " + diffTime;
			if(trackBtn.isChecked())
				Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
			
		}
		
		@Override
		public void onProviderDisabled(String provider) {
			Toast.makeText(getApplicationContext(), "GPS Disabled", Toast.LENGTH_SHORT).show();			
		}

		@Override
		public void onProviderEnabled(String provider) {
			Toast.makeText(getApplicationContext(), "GPS Enabled", Toast.LENGTH_SHORT).show();
		}
		
		/**
		 * Update preTime and newTime variables
		 * @param location
		 */
		private void setTime(Location location) {
			if(preTime == 0)
				preTime = location.getTime();
			else
				preTime = newTime;
			
			newTime = location.getTime();
			
			if(preTime != 0 && newTime != 0)
				diffTime = newTime - preTime;
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) { }
    	
    } // End MyLocationListener

    /**
     * Route overlay class
     */
    public class RouteOverlay extends Overlay{
    	Context context;
    	MapView mapview;
    	
    	public RouteOverlay(Context _context, MapView _mapView) {
    		this.context = _context;
    		this.mapview = _mapView;
    	}
    	
    	public void draw(Canvas canvas, MapView mapv, boolean shadow) {
    		
    		super.draw(canvas, mapv, shadow);
    		Path path = new Path();
    		List<GeoPoint> gpl = new ArrayList<GeoPoint>();
    		
    		Paint mPaint = new Paint();
    		mPaint.setDither(true);
            mPaint.setColor(Color.RED);
            mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
            mPaint.setStrokeJoin(Paint.Join.ROUND);
            mPaint.setStrokeCap(Paint.Cap.ROUND);
            mPaint.setStrokeWidth(2);
    		
    	  	GPSTrackDataSource datasource = new GPSTrackDataSource(context);
            datasource.open();
            
    		List<GPSTrack> values = datasource.getRoute(trackId);
    		datasource.close();
    		ArrayAdapter<GPSTrack> adapter = new ArrayAdapter<GPSTrack>(context,
    	        			android.R.layout.simple_list_item_1, values);
            
            if(adapter.getCount() != 0 && adapter.getItem(0) != null) {
	    		for(int i=0; i<adapter.getCount()-1; i++){
	    			if(adapter.getItem(i).getLatitude() != null) {
	    				String latiStr = adapter.getItem(i).getLatitude();
	    				String longiStr = adapter.getItem(i).getLongitude();
		    			float lat = Float.valueOf(latiStr).floatValue();
		    			float lng = Float.valueOf(longiStr).floatValue();
	    				gpl.add(new GeoPoint((int)(lat * 1E6), (int)(lng * 1E6)));
	    			}
	    		}
            }

            for(int i=0; i<gpl.size()-1; i++) {
    			Point from = new Point();
    			Point to = new Point();
    			
    			projection.toPixels(gpl.get(i), from);
    			projection.toPixels(gpl.get(i+1), to);
    			
    			path.moveTo(from.x, from.y);
    			path.lineTo(to.x, to.y);
    		}

    		canvas.drawPath(path, mPaint);
    	} // End draw()

    } // End routeoverlay class
}