/**package com.example.histree;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class SearchActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.search, menu);
		return true;
	}

} */
package com.example.histree;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import com.streamoid.http.MyAsyncHttpClient.CompletionListener;
import com.streamoid.http.MyAsyncHttpClient.Response;
//import com.streamoid.sampleapp.R;
import com.streamoid.servermanager.ServerManager;



import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.graphics.PointF;
import android.text.format.Time;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class SearchActivity extends Activity {
	private LocationManager locationManager;
	  private String provider;
	  public Location location;

	private ExifInterface ex;
	Uri imageuri;
	private ServerManager mServerManager;
	private int mDBItemIndex;
	TextView mTextView;
	protected static final String TAG="";
	private static final String COL_X = null;
	private static final String COL_Y = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);
        final ImageButton cam = (ImageButton) findViewById(R.id.cam);
        final ImageButton find = (ImageButton) findViewById(R.id.find);
		
        
		//launching camera
	    cam.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) 
        	{
        		Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        		startActivityForResult(i,1);        			
        	}
	    
	      }
	     );
	
	
	    //matching with the database
        find.setOnClickListener(new View.OnClickListener() {
        public void onClick(View v) 
    	{ //checking whether image captured is correct or not
    		    query();
    		
    	}
        
         });
	    }
	//return buffer containing image data from its uri
		private byte[] readImage(Uri imageuri) {
			
			InputStream ins;
			try 
			{
				ins = getContentResolver().openInputStream(imageuri);
			} catch (FileNotFoundException e1) 
			{
				Toast.makeText(this, "Filenotfound", Toast.LENGTH_LONG).show();
				return null;
			}
			
			ByteArrayOutputStream outputStream=new ByteArrayOutputStream();
			int size = 0;
			// Read the entire resource into a local byte buffer.
			byte[] buffer = new byte[1024];
			
			try {
				while((size=ins.read(buffer,0,1024))>=0){
				  outputStream.write(buffer,0,size);
				}
				ins.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			return outputStream.toByteArray();
		}

	private void uploadImage(Uri uri) throws IOException
	{
		ArrayList<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
		ex = new ExifInterface(getPathFromURI(uri));
		
		// You can define any parameters of your interest here
		// use exifinterface to extract data attached to image and add it to database
		params.add(new BasicNameValuePair("title", ex.getAttribute(ex.TAG_MAKE)));
		params.add(new BasicNameValuePair("lat", ex.getAttribute(ex.TAG_GPS_LATITUDE)));
		params.add(new BasicNameValuePair("long", ex.getAttribute(ex.TAG_GPS_LONGITUDE)));
		params.add(new BasicNameValuePair("other data", ex.getAttribute(ex.TAG_MODEL)));
		
				
		// Recommended size for DB image <= 1024x768. Avoid bigger sizes
		mServerManager.addToDB(imageuri.getLastPathSegment(), readImage(imageuri), params, new CompletionListener() {
			
			
			@Override
			public void onSuccess(Response response) {
//				mTextView.setText(mTextView.getText() + " " + response.message);
				mDBItemIndex = response.root.optInt("id");
				Log.i("uploaded image",imageuri.getLastPathSegment());
			}
			
			@Override
			public void onFailure(Response response) {
//				mTextView.setText(mTextView.getText() + " " + response.message);
				Log.e("Error","Unable to upload image"+imageuri.getLastPathSegment());
			}
		});
		
	}
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data){
		
		if(resultCode != RESULT_OK) return;
		
		imageuri = data.getData();
		String imagePath = getPathFromURI(imageuri);
		try {
			
			ex = new ExifInterface(imagePath);
			
			String str = ex.getAttribute(ExifInterface.TAG_MAKE);
			if(str!=null) Log.i("initial Tag make",str);
			str = ex.getAttribute(ExifInterface.TAG_MODEL);
			if(str!=null) Log.i("initial Tag model",str);
			str = ex.getAttribute(ExifInterface.TAG_GPS_LATITUDE);
			if(str!=null) Log.i("latitude",str);
			str = ex.getAttribute(ExifInterface.TAG_GPS_LONGITUDE);
			if(str!=null) Log.i("longitude",str);
			
			
			
			Time now = new Time(); now.setToNow();
			
//			ex.setAttribute(ExifInterface.TAG_ISO, "You can put");
			ex.setAttribute(ExifInterface.TAG_MAKE, "My new image");
			ex.setAttribute(ExifInterface.TAG_MODEL, "related data here-"+String.valueOf(now.minute)+":"+String.valueOf(now.second));  	
			ex.setAttribute(ExifInterface.TAG_GPS_LATITUDE,"latitude");
			ex.setAttribute(ExifInterface.TAG_GPS_LONGITUDE,"longitude");
			//			This isn't the right way but i couldn't find any ;)
//			u can attach more data like gps location, title etc using other tags 
//			or prompt the user to insert data using a dialogbox or something
			ex.saveAttributes();
			uploadImage(imageuri);
		} catch (IOException e) {}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.search, menu);
		return true;
	}
	//This function returns actual/absolute of the media file from its uri 
	private String getPathFromURI(Uri contentURI) {
	    Cursor cursor = getContentResolver()
	               .query(contentURI, null, null, null, null); 
	    cursor.moveToFirst(); 
	    int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA); 
	    return cursor.getString(idx); 
	}
	final Context context = this;

	//Function for checking if image matches
	void query() {
		/// API 3 - Query with an image
		// Recommended size for query image <= 640x480. Avoid bigger sizes
		mServerManager.query(readImage(imageuri), new CompletionListener() {
			
			@Override
			public void onSuccess(Response response) {
				mTextView.setText(mTextView.getText() + " " + response.message);
				
				JSONArray matches = response.root.optJSONArray("matches");
				
				for(int i = 0; i < matches.length(); i++) {
					JSONObject match = (JSONObject)matches.optJSONObject(i);
					
					Log.i(TAG, "title =  " + match.optString("title"));
					Log.i(TAG, "where =  " + match.optString("where"));
					Log.i(LOCATION_SERVICE, "location =  " + match.optString("location"));
				String im=getPathFromURI(imageuri);
				try {
					ex=new ExifInterface(im);
					String lat=ex.getAttribute(ExifInterface.TAG_GPS_LATITUDE);
					String lng=ex.getAttribute(ExifInterface.TAG_GPS_LONGITUDE);
					int x=Integer.parseInt(lat);
					int y=Integer.parseInt(lng);
					
				} catch (IOException e) {
					
				}
				Criteria criteria = new Criteria();
				provider = locationManager.getBestProvider(criteria, false);
			    location = locationManager.getLastKnownLocation(provider);
                int lat = 0,lng = 0;
			    // Initialize the location fields
			    if (location != null) {
			    	 lat = (int) (location.getLatitude());
				     lng = (int) (location.getLongitude());
			    } else {
			      Toast.makeText(context,"GPS Not Available",30);
			      
			    }
			    PointF search = new PointF(x, y);
			   PointF t=new PointF();
			   if (pointIsInCircle( search,t,radius))
			    {Toast.makeText(context, "Location Found", 30);
				update();}
			   else
			   { Toast.makeText(context, "Location Not Found", 30);
			     update();
			   }
				}
				
			}
			
			@Override
			public void onFailure(Response response) {
				//mTextView.setText(mTextView.getText() + " " + response.message);
				AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
						context);
		 
					// set title
					alertDialogBuilder.setTitle("Append Map");
		 
					// set dialog message
					alertDialogBuilder
						.setMessage("Click yes to exit!")
						.setCancelable(false)
						.setPositiveButton("Yes",new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,int id) {
								// if this button is clicked, 
								Intent i = new Intent(SearchActivity.this,AnviewActivity.class);
				        		startActivityForResult(i,1);   
								
							}
						  })
						.setNegativeButton("No",new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,int id) {
								// if this button is clicked, just close
								// the dialog box and do nothing
								dialog.cancel();
							}
						});
		 
						// create alert dialog
						AlertDialog alertDialog = alertDialogBuilder.create();
		 
						// show it
						alertDialog.show();
					}
				});
			}
		
		
	
	//updating my image in Database
	void update() { 
		/// API 4 - Update DB record
		ArrayList<BasicNameValuePair> paramsUpdate = new ArrayList<BasicNameValuePair>();
		
		// You can define any parameters of your interest here
		paramsUpdate.add(new BasicNameValuePair("title", "Landmark")); // Old one will be update
		paramsUpdate.add(new BasicNameValuePair("when", "previous")); // New meta 
		mServerManager.updateDB(mDBItemIndex, paramsUpdate, new CompletionListener() {
			
			@Override
			public void onSuccess(Response response) {
				mTextView.setText(mTextView.getText() + " " + response.message);
				
				delete();
			}
			
			@Override
			public void onFailure(Response response) {
				mTextView.setText(mTextView.getText() + " " + response.message);
			}
		});
	}


     //deleting any image from database
void delete() {
	/// API 5 - Delete DB record
	mServerManager.deleteFromDB(mDBItemIndex, new CompletionListener() {
		
		@Override
		public void onSuccess(Response response) {
			mTextView.setText(mTextView.getText() + " " + response.message);
			
			logout();
		}
		
		@Override
		public void onFailure(Response response) {
			mTextView.setText(mTextView.getText() + " " + response.message);
		}
	});	
}

   ///
void logout() {
	/// API 6 - Logout
	mServerManager.logout(new CompletionListener() {
		
		@Override
		public void onSuccess(Response response) {
			mTextView.setText(mTextView.getText() + " " + response.message);
		}
		
		@Override
		public void onFailure(Response response) {
			mTextView.setText(mTextView.getText() + " " + response.message);
		}
	});
}

byte[] readImage(int resId) {
	InputStream ins = getResources().openRawResource(resId);
	ByteArrayOutputStream outputStream=new ByteArrayOutputStream();
	int size = 0;
	// Read the entire resource into a local byte buffer.
	byte[] buffer = new byte[1024];
	
	try {
		while((size=ins.read(buffer,0,1024))>=0){
		  outputStream.write(buffer,0,size);
		}
		ins.close();
	} catch (IOException e) {
		e.printStackTrace();
	}
	
	return outputStream.toByteArray();
}

public static PointF calculateDerivedPosition(PointF point,
        double range, double bearing)
{
    double EarthRadius = 6371000; // m

    double latA = Math.toRadians(point.x);
    double lonA = Math.toRadians(point.y);
    double angularDistance = range / EarthRadius;
    double trueCourse = Math.toRadians(bearing);

    double lat = Math.asin(
            Math.sin(latA) * Math.cos(angularDistance) +
                    Math.cos(latA) * Math.sin(angularDistance)
                    * Math.cos(trueCourse));

    double dlon = Math.atan2(
            Math.sin(trueCourse) * Math.sin(angularDistance)
                    * Math.cos(latA),
            Math.cos(angularDistance) - Math.sin(latA) * Math.sin(lat));

    double lon = ((lonA + dlon + Math.PI) % (Math.PI * 2)) - Math.PI;

    lat = Math.toDegrees(lat);
    lon = Math.toDegrees(lon);

    PointF newPoint = new PointF((float) lat, (float) lon);

    return newPoint;

}
public int x,y;
public int radius=1000;//making a circle of radius 1000m(searching in this locality)
PointF center = new PointF(x, y);
final double mult = 1; // mult = 1.1; is more reliable
PointF p1 = calculateDerivedPosition(center, mult * radius, 0);
PointF p2 = calculateDerivedPosition(center, mult * radius, 90);
PointF p3 = calculateDerivedPosition(center, mult * radius, 180);
PointF p4 = calculateDerivedPosition(center, mult * radius, 270);
String strWhere =  " Where "
    + COL_X + " > " + String.valueOf(p3.x) + " And "
    + COL_X + " < " + String.valueOf(p1.x) + " And "
    + COL_Y + " < " + String.valueOf(p2.y) + " And "
    + COL_Y + " > " + String.valueOf(p4.y);
public static boolean pointIsInCircle(PointF pointForCheck, PointF center,
    double radius) {
if (getDistanceBetweenTwoPoints(pointForCheck, center) <= radius)
    return true;
else
    return false;
}

public static double getDistanceBetweenTwoPoints(PointF p1, PointF p2) {
double R = 6371000; // m
double dLat = Math.toRadians(p2.x - p1.x);
double dLon = Math.toRadians(p2.y - p1.y);
double lat1 = Math.toRadians(p1.x);
double lat2 = Math.toRadians(p2.x);

double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) + Math.sin(dLon / 2)
        * Math.sin(dLon / 2) * Math.cos(lat1) * Math.cos(lat2);
double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
double d = R * c;

return d;
}
}
