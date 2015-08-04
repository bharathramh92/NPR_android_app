/*
Assignment : HomeWork 4
Names:
Bharathram Hariharan
Hemchand Ramireddy
Pratiksha Badgujar
*/
package com.example.nprapp;

import java.io.InputStream;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xml.sax.helpers.DefaultHandler;

public class listJSONUtils {
	public static class JSONParser extends DefaultHandler{
		public static ArrayList<ListDetails> parser(String in) throws JSONException{
			ArrayList<ListDetails> content = new ArrayList<ListDetails>();
			ListDetails details;
			JSONObject root = new JSONObject(in);
			JSONArray rootArray = root.getJSONArray("item");
			
			for(int i=0; i<rootArray.length();++i){
				details= new ListDetails();
				JSONObject elements = rootArray.getJSONObject(i);
				JSONObject temp = elements.getJSONObject("title");
				details.setText(temp.getString("$text"));
				details.setId(elements.getLong("id"));
				content.add(details);
			}
			
			return content;
		}
	}
}
