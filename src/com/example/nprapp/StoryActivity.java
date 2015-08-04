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

import android.app.Activity;
import android.app.Notification.Action;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnInfoListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.MediaController.MediaPlayerControl;

public class StoryActivity extends Activity implements OnPreparedListener, MediaPlayerControl,MediaPlayer.OnCompletionListener{
	
	public static String STORYTOINDEX="story_position";
//	int p =0;
	TextView storyActTitle,storyActName,storyActPubDate,storyTeaserText;
	String webLink;
	boolean starred;
	ImageView st_star;
	Context context;
	StoryDetails currentStory ;
	boolean notForceQuit=false;
	MediaPlayer player;
	MediaController controller;
	Toast toast;
	Boolean completedOnce=false;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_story);
		
		currentStory = (StoryDetails) getIntent().getExtras().getSerializable(StoriesActivity.STORIESTOSTORY);
		Log.d("final_story",currentStory.toString());
		
		st_star=(ImageView) findViewById(R.id.st_star);
		webLink=currentStory.getWebLink();
		
		storyActTitle=(TextView) findViewById(R.id.storyActTitle);
		storyActName = (TextView) findViewById(R.id.storyActName);
		storyActPubDate = (TextView) findViewById(R.id.storyActPubDate);
		storyTeaserText = (TextView) findViewById(R.id.storyTeaserText);
		
		int sec=currentStory.getDurationSeconds();
		String time =(sec/60)%60+" min "+sec%60+" sec";
		storyActPubDate.setText(currentStory.getPubDate()+" | "+time);
		
		storyActTitle.setText(currentStory.getTitle());
		if(currentStory.getName()!=null && !currentStory.getName().isEmpty())
		{
			storyActName.setText("by "+ currentStory.getName());
		}
		storyTeaserText.setText(currentStory.getTeaserText());
		controller = new MediaController(StoryActivity.this);
		context = this;
	
		if(currentStory.getAudioUrl()!=null){
			findViewById(R.id.st_speaker).setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if(player==null && !completedOnce){					//when loading for the first time
						
						RequestParams request = new RequestParams("GET", currentStory.getAudioUrl(), "");
						audioAsyncTask audioPlay  = new audioAsyncTask();
						audioPlay.execute(request);
					}else if(completedOnce){							//when loading the second time and afterwards
						player.start();
					}
					else{												//during loading for the first time
						killToast();
						String msg;
						if(!player.isPlaying())
							msg = "Buffering the audio now. Please wait. "
//							+getBufferPercentage()
//							+"% Completed"
							;
						else
							msg="Audio already playing. Please use the media controller";
							toast=Toast.makeText(StoryActivity.this, msg, Toast.LENGTH_SHORT);
						toast.show();
					}
				}
			});
		}else{
			killToast();
			toast=Toast.makeText(StoryActivity.this, "Audio not available for this story", Toast.LENGTH_SHORT);
			toast.show();
		}
		
		
			
		
		
		findViewById(R.id.st_back).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onBackPressed();
			}
		});
		
		if(webLink!=null){
			findViewById(R.id.st_globe).setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent=new Intent(Intent.ACTION_VIEW, Uri.parse(webLink));
					startActivity(intent);
				}
			});
		}
		else{
			killToast();
//			toast = new Toast(context);
			toast=Toast.makeText(StoryActivity.this, "Web link not Available", Toast.LENGTH_SHORT);
			toast.show();
			findViewById(R.id.st_globe).setFocusable(true);
			findViewById(R.id.st_globe).setFocusableInTouchMode(true);
		}
		
		
		
		SharedPreferences sharedPreference = context.getSharedPreferences(getResources().getString(R.string.prefKey), Context.MODE_PRIVATE);
		String presenceIdCheck=sharedPreference.getString(currentStory.getId()+"", "");
		Log.d("final_story",sharedPreference.getAll().toString());
		Log.d("final_story",presenceIdCheck + " is the result after checking");
		if(presenceIdCheck!=""){
			starred=true;
			st_star.setImageResource(R.drawable.star_select);
		}else{
			starred=false;
		}
		
		
		
		
		st_star.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				starred=!starred;
				if(starred){
					
					SharedPreferences sharedPref=context.getSharedPreferences( getResources().getString(R.string.prefKey), Context.MODE_PRIVATE );
					SharedPreferences.Editor editor = sharedPref.edit();
//					if(sharedPref.getString(currentStory.getId()+"", "")==""){
					editor.putString(currentStory.getId()+"", currentStory.getId()+"");
					editor.commit();
//					}
					
					st_star.setImageResource(R.drawable.star_select);
				}else{
					
					SharedPreferences sharedPref=context.getSharedPreferences( getResources().getString(R.string.prefKey), Context.MODE_PRIVATE );
					SharedPreferences.Editor editor = sharedPref.edit();
					editor.remove(currentStory.getId()+"");
					editor.commit();
					
					st_star.setImageResource(R.drawable.star_not_selected);
				}
			}
		});
		
		if(player!=null){

		}

	}
	public class audioAsyncTask extends AsyncTask<RequestParams, Void, Void>{
		BufferedReader reader;
		@Override
		protected Void doInBackground(RequestParams... params) {
			
			try {
				HttpURLConnection con = params[0].setupConnection();
				InputStream in = con.getInputStream();
				reader = new BufferedReader(new InputStreamReader(in));
				String streamUrl = reader.readLine();
				Log.d("final_story",streamUrl+" is the stream url");
				player = new MediaPlayer();
				
				player.setAudioStreamType(AudioManager.STREAM_MUSIC);
				player.setDataSource(StoryActivity.this, Uri.parse(streamUrl));
//				controller.setVisibility(View.VISIBLE);
				player.setOnPreparedListener(StoryActivity.this);
				player.setAudioStreamType(AudioManager.STREAM_MUSIC);
				
				Log.d("story","about to play");
//				player.prepare();
				player.prepareAsync();
				publishProgress();
//				player.start();
				
				player.setOnCompletionListener(new OnCompletionListener() {
					
					@Override
					public void onCompletion(MediaPlayer mp) {
						// TODO Auto-generated method stub
						completedOnce=true;
					}
				});
				
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}
		@Override
		protected void onProgressUpdate(Void... values) {
			// TODO Auto-generated method stub
			Log.d("story","on progress");
			
			killToast();
//			toast = new Toast(StoryActivity.this);
			toast=Toast.makeText(StoryActivity.this, " Started streaming audio. Please wait", Toast.LENGTH_SHORT);
			toast.show();
			super.onProgressUpdate(values);
		}
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			findViewById(R.id.st_speaker).setFocusable(true);
			findViewById(R.id.st_speaker).setFocusableInTouchMode(true);
			super.onPreExecute();
		}
		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			findViewById(R.id.st_speaker).setFocusable(false);
			findViewById(R.id.st_speaker).setFocusableInTouchMode(false);
			super.onPostExecute(result);
		}
		
	}
	
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		
		if(controller!=null){
			controller.show();
		}
			
		return super.onTouchEvent(event);
	}


	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		if(!notForceQuit){
		killToast();
		if(player!=null)
		{	if(player.isPlaying()){
				player.stop();
		}
			player.release();
			player = null;
		}
		}
		super.onDestroy();
//		controller.setVisibility(View.INVISIBLE);
	}
	
	public void killToast(){
		if(toast!=null){
			toast.cancel();
			toast = null;
		}
	}

 
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
//		controller.setVisibility(View.INVISIBLE);
		if(player!=null)
			{player.stop();
			player.release();
			player = null;
			}
		notForceQuit = true;
		SharedPreferences sharedPref=context.getSharedPreferences( getResources().getString(R.string.prefKey), Context.MODE_PRIVATE );
		String a = sharedPref.getString(currentStory.getId()+"", "not_stored");
		Log.d("final_story", a);
		if(a.equals("not_stored")){
			Intent intent = new Intent();
			Log.d("final_story","before sending");
			int position=getIntent().getExtras().getInt(StoriesActivity.STORYINDEX);
			intent.putExtra(StoryActivity.STORYTOINDEX, position);
			Log.d("final_story","sending "+intent);
			setResult(RESULT_CANCELED,intent);
		}else{
			Log.d("final_story","no change");
			setResult(RESULT_OK);
		}
		super.onBackPressed();
	}


	@Override
	public void onPrepared(MediaPlayer mp) {
		// TODO Auto-generated method stub
		mp.start();
		controller.setMediaPlayer(StoryActivity.this);
		controller.setEnabled(true);
		controller.setAnchorView(findViewById(R.id.mediaControllerStory));
		controller.show();
		
	}


	@Override
	public void start() {
		// TODO Auto-generated method stub
		if(player!=null)
			player.start();
	}


	@Override
	public void pause() {
		// TODO Auto-generated method stub
		if(player!=null)
			player.pause();
	}


	@Override
	public int getDuration() {
		// TODO Auto-generated method stub
		if(player!=null)
			return player.getDuration();
		else
			return 0;
	}


	@Override
	public int getCurrentPosition() {
		// TODO Auto-generated method stub
		if(player!=null)
			return player.getCurrentPosition();
		else
			return 0;
	}


	@Override
	public void seekTo(int pos) {
		// TODO Auto-generated method stub
		if(player!=null)
		{
			Log.d("seekTo",pos+"");
			player.seekTo(pos);
		}
	}


	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		
		super.onStop();
	}


	@Override
	public boolean isPlaying() {
		// TODO Auto-generated method stub
		if(player!=null)
			return player.isPlaying();
		else
			return false;
	}


	@Override
	public int getBufferPercentage() {
		// TODO Auto-generated method stub
		return 0;
	}


	@Override
	public boolean canPause() {
		// TODO Auto-generated method stub
		return true;
	}
	
	

	@Override
	public boolean canSeekBackward() {
		// TODO Auto-generated method stub
		return true;
	}


	@Override
	public boolean canSeekForward() {
		// TODO Auto-generated method stub
		return true;
	}


	@Override
	public int getAudioSessionId() {
		// TODO Auto-generated method stub
		return player.getAudioSessionId();
	}


	@Override
	public void onCompletion(MediaPlayer mp) {
		// TODO Auto-generated method stub
		Log.d("buffer","Completed");
		if(mp!=null){
			mp=null;
		}
	}
}
