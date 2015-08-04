/*
Assignment : HomeWork 4
Names:
Bharathram Hariharan
Hemchand Ramireddy
Pratiksha Badgujar
*/
package com.example.nprapp;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xml.sax.helpers.DefaultHandler;

import android.util.Log;

public class storiesJSONUtils {

	public static class JSONParser{

		public static ArrayList<StoryDetails> parser(String st) throws JSONException{
			ArrayList<StoryDetails> storyLists = new ArrayList<StoryDetails>();
				
			JSONObject root = new JSONObject(st);
			JSONObject rootSub=root.getJSONObject("list");
//			Log.d("story json utils",rootSub.toString());
			JSONArray rootArray = rootSub.getJSONArray("story");
			
			
			StoryDetails story;
			
			for(int i=0;i<rootArray.length();++i){
				story = new StoryDetails();
				try{
				JSONObject storyObject = (JSONObject) rootArray.get(i);
				
				story.setId(storyObject.getLong("id"));
				story.setPubDate(storyObject.getJSONObject("pubDate").getString("$text"));
				story.setTeaserText(storyObject.getJSONObject("teaser").getString("$text"));
				story.setTitle(storyObject.getJSONObject("title").getString("$text"));

				JSONaddName(story,storyObject);
				
				JSONaddImageUrl(story,storyObject);
				
				JSONaddAudioUrl(story,storyObject);
				
				JSONaddWeblink(story,storyObject);
				
				JSONaddAudioDuration(story,storyObject);
				
				}catch(Exception e){
					e.printStackTrace();
				}
				finally{
					storyLists.add(story);
					Log.d("story json utils","passing the follwing objects from json utils " +story.toString()+"\n");
				}
			}
//			Log.d("story json utils",rootArray.length()+"");
		
			return storyLists;
			
			
		}
		
	}
	
	public static void JSONaddAudioDuration(StoryDetails storyH,JSONObject storyObjectH){
		try{
			storyH.setDurationSeconds(storyObjectH.getJSONArray("audio").getJSONObject(0).getJSONObject("duration").getString("$text"));
		}catch(Exception e){
			Log.d("json story","weblink for"+storyH.getTitle()+" not present");
			e.printStackTrace();			
		}
	}
	
	public static void JSONaddWeblink(StoryDetails storyH,JSONObject storyObjectH){
		try{
			storyH.setWebLink(storyObjectH.getJSONArray("link").getJSONObject(0).getString("$text"));
		}catch(Exception e){
			Log.d("json story","weblink for"+storyH.getTitle()+" not present");
			e.printStackTrace();		}
	}
	
	public static void JSONaddName(StoryDetails storyH,JSONObject storyObjectH){
		try{
			storyH.setName(storyObjectH.getJSONArray("byline").getJSONObject(0).getJSONObject("name").getString("$text"));
		}catch(Exception e){
			Log.d("json story","weblink for"+storyH.getTitle()+" not present");
			e.printStackTrace();		}
	}
	public static void JSONaddImageUrl(StoryDetails storyH,JSONObject storyObjectH){
		try{
			storyH.setImageUrl(storyObjectH.getJSONArray("image").getJSONObject(0).getString("src"));
		}catch(Exception e){
			Log.d("json story","weblink for"+storyH.getTitle()+" not present");
			e.printStackTrace();		}
	}
	public static void JSONaddAudioUrl(StoryDetails storyH,JSONObject storyObjectH){
		try{
			storyH.setAudioUrl(storyObjectH.getJSONArray("audio").getJSONObject(0).getJSONObject("format").getJSONArray("mp3").getJSONObject(0).getString("$text"));
		}catch(Exception e){
			Log.d("json story","weblink for"+storyH.getTitle()+" not present");
			e.printStackTrace();		}
	}
}
