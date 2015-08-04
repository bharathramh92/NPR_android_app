/*
Assignment : HomeWork 4
FileName: MainActivity.java
Names:
Bharathram Hariharan
Hemchand Ramireddy
Pratiksha Badgujar
*/
package com.example.nprapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends Activity {

	public static String MAINTOLISTCHOICE = "main_to_list";
	public static String MAINTOSTORIES = "main_to_stories";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		findViewById(R.id.mainExit).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		
		findViewById(R.id.mainPrograms).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				callListActivity("programs");
			}
		});
		
		findViewById(R.id.mainTopics).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				callListActivity("topics");
			}
		});
		
		
		
		
		findViewById(R.id.mainFavourite).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MainActivity.this , StoriesActivity.class);
				intent.putExtra(MainActivity.MAINTOSTORIES, "favourites");
				Log.d("main","calling stories for favourite");
				startActivity(intent);
			}
		});;
		
	}
	

	public void callListActivity(String listChoiceString){
		Intent intent = new Intent(MainActivity.this,ListActivity.class);
		intent.putExtra(MainActivity.MAINTOLISTCHOICE, listChoiceString);
		Log.d("main","calling list for "+listChoiceString);
		startActivity(intent);
	}
}
