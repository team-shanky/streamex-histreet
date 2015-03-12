/**package com.example.histree;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class AnviewActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_anview);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.anview, menu);
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

import com.streamoid.http.MyAsyncHttpClient.CompletionListener;
import com.streamoid.http.MyAsyncHttpClient.Response;
import com.streamoid.servermanager.ServerManager;


import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.text.format.Time;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;




public class AnviewActivity extends Activity {
	private ExifInterface ex;
	Uri imageuri;
	private ServerManager mServerManager;
	private int mDBItemIndex;
	EditText mEdit;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_anview);
	
	final ImageButton cam1 = (ImageButton) findViewById(R.id.cam1);
	
	cam1.setOnClickListener(new View.OnClickListener() {
        public void onClick(View v) 
    	{
    		Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
    		startActivity(i);        			
    	}
    
      }
     );
	
	
    final ImageButton add = (ImageButton) findViewById(R.id.add);
    mEdit   = (EditText)findViewById(R.id.editText1);
	
	add.setOnClickListener(new View.OnClickListener() {
        public void onClick(View v)
        {   
        	Log.v("EditText", mEdit.getText().toString());
    	    try {
				uploadImage(imageuri);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	      			
    	}
    
      }
     );
	
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
			
			Time now = new Time(); now.setToNow();
			
//			ex.setAttribute(ExifInterface.TAG_ISO, "You can put");
			ex.setAttribute(ExifInterface.TAG_MAKE, "image.jpg");
			ex.setAttribute(ExifInterface.TAG_MODEL, "related data here-"+String.valueOf(now.minute)+":"+String.valueOf(now.second));  	
//			need to be checked image.jpg
			ex.saveAttributes();
			uploadImage(imageuri);
		} catch (IOException e) {}
	}
	
	private void uploadImage(Uri uri) throws IOException
	{
		ArrayList<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
		ex = new ExifInterface(getPathFromURI(uri));
		
		// You can define any parameters of your interest here
		// use exifinterface to extract data attached to image and add it to database
		params.add(new BasicNameValuePair("title", ex.getAttribute(ex.TAG_MAKE)));
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
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.anview, menu);
		return true;
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

	private String getPathFromURI(Uri contentURI) {
	    Cursor cursor = getContentResolver()
	               .query(contentURI, null, null, null, null); 
	    cursor.moveToFirst(); 
	    int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA); 
	    return cursor.getString(idx); 
	}

} 
