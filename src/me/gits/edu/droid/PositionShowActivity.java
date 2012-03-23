/**
 * Activity used to show a list of all gps positions that have been tracked
 */

package me.gits.edu.droid;

import java.util.List;

import android.app.ListActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class PositionShowActivity extends ListActivity{
	
	public void onCreate(Bundle savedInstanceState) {
	  	super.onCreate(savedInstanceState);
	  	
	  	GPSTrackDataSource datasource = new GPSTrackDataSource(this);
        datasource.open();
        
		List<GPSTrack> values = datasource.getAllPositions();
		datasource.close();
		ArrayAdapter<GPSTrack> adapter = new ArrayAdapter<GPSTrack>(this,
	        			android.R.layout.simple_list_item_1, values);
		
		setListAdapter(new ArrayAdapter<GPSTrack>(this, android.R.layout.simple_expandable_list_item_1, values));

		ListView lv = getListView();
		lv.setTextFilterEnabled(true);
		
		lv.setOnItemClickListener(new OnItemClickListener() {
	    public void onItemClick(AdapterView<?> parent, View view,
	        int position, long id) {
	      // When clicked, show a toast with the TextView text
	    		Toast.makeText(getApplicationContext(), ((TextView) view).getText(),
	    				Toast.LENGTH_SHORT).show();
	    	}
		});
	}
}
