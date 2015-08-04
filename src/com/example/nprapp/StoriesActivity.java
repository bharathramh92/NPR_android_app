/*
Assignment : HomeWork 4
Names:
Bharathram Hariharan
Hemchand Ramireddy
Pratiksha Badgujar
*/
package com.example.nprapp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.zip.Inflater;

import org.json.JSONException;

import com.squareup.picasso.Picasso;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.nfc.FormatException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class StoriesActivity extends Activity{
	public static String STORIESTOSTORY="stories_to_story";
	public static String STORYINDEX="index";
	public static int REQUESTFORSTORY=100;
	customProgress progress;
	Toast toast;
	
	customStoryAdapter adapter;
	ArrayList<StoryDetails> resultStories;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
	  
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_stories);
	
		postProcess();
	}
	
	public void killToast(){
		if(toast!=null){
			toast.cancel();
			toast = null;
		}
	}
	
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle presses on the action bar items
		
	    switch (item.getItemId()) {
	        case R.id.action_settings:
	        	Log.d("list","clearing history");
	        	SharedPreferences sharedPref=this.getSharedPreferences( getResources().getString(R.string.prefKey), Context.MODE_PRIVATE );
	    		SharedPreferences.Editor editor = sharedPref.edit();
	    		editor.clear().commit();
	    		Toast.makeText(StoriesActivity.this, "Cleared History", Toast.LENGTH_SHORT).show();
	    		finish();
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    // Inflate the menu items for use in the action bar
		String favCheck = getIntent().getExtras().getString(MainActivity.MAINTOSTORIES);
		if(favCheck!=null && favCheck.equals("favourites")){
			MenuInflater inflater = getMenuInflater();
		    inflater.inflate(R.layout.stories_activity_actions, menu);
		}
	    return super.onCreateOptionsMenu(menu);
	}
	
	public void postProcess(){
		
		RequestParams request = preProcess();
		if(request==null)
		{	
			Log.d("list","List activity exiting");
			killToast();
			toast = new Toast(StoriesActivity.this);
			toast.makeText(StoriesActivity.this, "No data starred", Toast.LENGTH_SHORT).show();
//			finish();
			return;
		}
		else{
		getDataLists dataListGetter = new getDataLists();
		dataListGetter.execute(request);
		}
	}
	
	
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		if(requestCode==StoriesActivity.REQUESTFORSTORY){
//			Log.d("stories",data.toString());
			if(resultCode==RESULT_CANCELED){
				String favCheck = getIntent().getExtras().getString(MainActivity.MAINTOSTORIES);
				if(favCheck!=null && favCheck.equals("favourites")){
					
//					ListView listView = (ListView)findViewById(R.id.storiesListView);
					Log.d("stories", "on activity result");
					int position=data.getExtras().getInt(StoryActivity.STORYTOINDEX);
					Log.d("stories",position+" is the position");
					
					adapter.remove(resultStories.get(position));
					
				}
			}
			else if(resultCode==RESULT_OK){
				return;
			}
	}	
}



	private void populate(final ArrayList<StoryDetails> result) {
		resultStories=result;
		ListView listView = (ListView) findViewById(R.id.storiesListView);
		adapter= new customStoryAdapter(StoriesActivity.this, R.layout.story_container_layout, resultStories);
		adapter.setNotifyOnChange(true);
		listView.setAdapter(adapter);

		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent intent = new Intent(StoriesActivity.this,StoryActivity.class);
				intent.putExtra(StoriesActivity.STORIESTOSTORY, resultStories.get(position));
				intent.putExtra(StoriesActivity.STORYINDEX, position);
				startActivityForResult(intent, StoriesActivity.REQUESTFORSTORY);
			}
		});
	}
	
	private class customStoryAdapter extends ArrayAdapter<StoryDetails>{
		
		Context mContext;
		int resource;
		ArrayList<StoryDetails> objects;
		public customStoryAdapter(Context context, int resource,
				ArrayList<StoryDetails> objects) {
			super(context, resource, objects);
			this.mContext=context;
			this.resource=resource;
			this.objects=objects;
		}
		
		
	



		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			LayoutInflater inflate = (LayoutInflater) this.mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView=inflate.inflate(resource, parent,false);
			
			ImageView imgView = (ImageView) convertView.findViewById(R.id.storyImage);
			String imgUrl=objects.get(position).getImageUrl();
			if(imgUrl==null || imgUrl==""){
				imgView.setImageResource(R.drawable.no_image);
			}else{
				Picasso.with(mContext).load(imgUrl).into(imgView);
			}
			TextView title = (TextView) convertView.findViewById(R.id.storyTitle);
			title.setText(objects.get(position).getTitle());
			TextView pubDate = (TextView) convertView.findViewById(R.id.storyPubDate);
//			int sec=objects.get(position).getDurationSeconds();
//			String time =(sec/60)%60+" min "+sec%60+" sec";
			pubDate.setText(objects.get(position).getPubDate());
			TextView teaserText = (TextView) convertView.findViewById(R.id.storyTeaser);
			teaserText.setText(objects.get(position).getTeaserText());
			return convertView;
		}
		
	}
	
	private class customProgress extends ProgressDialog{

		public customProgress(Context context) {
			super(context);
			// TODO Auto-generated constructor stub
		}

		@Override
		public void onBackPressed() {
			// TODO Auto-generated method stub
			if(progress!=null)
			{	
				progress.dismiss();
				progress=null;
			}
			
			finish();
			super.onBackPressed();
		}
		
	}
	
	public class getDataLists extends AsyncTask<RequestParams, Void, ArrayList<StoryDetails>>{
		
		@Override
		protected ArrayList<StoryDetails> doInBackground(
				RequestParams... params) {
			
			BufferedReader reader ;
			try {
				HttpURLConnection con = params[0].setupConnection();
				InputStream in = con.getInputStream();
				reader=new BufferedReader(new InputStreamReader(in));
				String line="";
				StringBuilder sb = new StringBuilder();
				while((line=reader.readLine())!=null){
					sb.append(line);
				}
				return storiesJSONUtils.JSONParser.parser(sb.toString());
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			
//			progress=new ProgressDialog(StoriesActivity.this);
			
			progress=new customProgress(StoriesActivity.this);
			progress.setMessage("Loading stories");
			progress.setCancelable(false);
			progress.show();
		
//			progress=ProgressDialog.show(StoriesActivity.this, null, "Loading stories", true, false
//					,new DialogInterface.OnCancelListener() {
//				
//				@Override
//				public void onCancel(DialogInterface dialog) {
//					// TODO Auto-generated method stub
//					
//					onBackPressed();
//				}
//			}
//			);
		}

		@Override
		protected void onPostExecute(ArrayList<StoryDetails> result) {
			super.onPostExecute(result);
			if(progress!=null)
				progress.dismiss();
			if(result!=null){
				populate(result);
	//			setResult(result);
			}
		}
	}
	

	


	public RequestParams preProcess(){
		RequestParams req = new RequestParams("GET", getResources().getString(R.string.nprQueryUrl),"");
		String apiKey = getResources().getString(R.string.key);
		
		String favCheck = getIntent().getExtras().getString(MainActivity.MAINTOSTORIES);
		String ids;
		if(favCheck!=null && favCheck.equals("favourites")){
			ids= retrieveAllids();
		}else{
			long idSP = getIntent().getExtras().getLong(ListActivity.STORYCATID);
			ids = String.valueOf(idSP);
		}		
		
		Log.d("Stories","id of program/topic is "+ ids);
		
		
		if(!ids.equals("empty")){
			req.addParams("id", ids+"");
			req.addParams("dateType", "story");
			req.addParams("output", "JSON");
			req.addParams("numResults", "25");
			req.addParams("apiKey", apiKey);
			Log.d("Stories","Requested url is "+req.getEncodedUrl());
			return req;
		}
		else{
			return null;
		}
		
		
		
	}

	private String retrieveAllids() {
		SharedPreferences sharedPref=StoriesActivity.this.getSharedPreferences( getResources().getString(R.string.prefKey), Context.MODE_PRIVATE );
//		String a = sharedPref.getString(currentStory.getId()+"", currentStory.getTitle()+" Not stored");
		Map tempMap = sharedPref.getAll();
		StringBuilder keys = new StringBuilder(tempMap.keySet().toString());
		keys.deleteCharAt(keys.length()-1);
		keys.deleteCharAt(0);
		Log.d("stories", keys.toString());
		if(keys.toString()=="")
			return "empty";
		else
			return keys.toString().replaceAll("\\s", "");
	}
	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		Log.d("back","Back button pressed");
		finish();
		super.onBackPressed();
	}
}
