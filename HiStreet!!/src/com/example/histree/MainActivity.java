/**package com.example.histree;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
} */
package com.example.histree;

import android.os.Bundle;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.Button;



public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		//declaring buttons
	    final Button search= (Button)  findViewById(R.id.search);
	    final Button share = (Button) findViewById(R.id.share);
	    final Button draw = (Button) findViewById(R.id.draw);
	    final Button append = (Button) findViewById(R.id.append);
		final Button loging = (Button) findViewById(R.id.loging);
		
		//assigning buttons to corresponding activities
	    search.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) 
        	{
        		Intent i = new Intent(MainActivity.this, SearchActivity.class);
        		startActivity(i);        			
        	}
	    }
        
	    );  
	    share.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) 
        	{
        		Intent i1 = new Intent(MainActivity.this, ShareActivity.class);
        		startActivity(i1);        			
          }
	    }
	    ); 
        
	    append.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) 
        	{
        		Intent i1 = new Intent(MainActivity.this, AnviewActivity.class);
        		startActivity(i1);        			
        	}
	    });
        
	    draw.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) 
        	{
        		Intent i1 = new Intent(MainActivity.this, DrawActivity.class);
        		startActivity(i1);        			
        	}
	    });
	    loging.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) 
        	{
        		Intent i1 = new Intent(MainActivity.this, LoginActivity.class);
        		startActivity(i1);        			
        	}
	    });
	    
	}
	}

