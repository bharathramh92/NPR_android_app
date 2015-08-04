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

import org.json.JSONException;
import org.w3c.dom.ls.LSInput;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView.FindListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class ListActivity extends Activity {
	
	public static String STORYCATID="id";
	
	RequestParams request;
	customProgress progress;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list);
		if((request=initialCheck())==null)
		{	
			Log.d("list","List activity exiting");
			finish();
			return;
		}
		
		getListData listData = new getListData();
		listData.execute(request);
		
		ListView listView = (ListView) findViewById(R.id.listActivityListView);
		
		
	}
	
	private void populateListView(final ArrayList<ListDetails> result) {
		ListView listView = (ListView) findViewById(R.id.listActivityListView);
		customAdapter adapter = new customAdapter(ListActivity.this, R.layout.list_activity_container, result);
		listView.setAdapter(adapter);
		
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Log.d("list",result.get(position).toString());
				Intent intent = new Intent(ListActivity.this,StoriesActivity.class);
				intent.putExtra(ListActivity.STORYCATID, result.get(position).getId());
				startActivity(intent);
			}
		});
		
	}
	
	public static class customAdapter extends ArrayAdapter<ListDetails>{
		Context mContext;
		ArrayList<ListDetails> objects;
		int resource;
		public customAdapter(Context context, int resource,
				ArrayList<ListDetails> objects) {
			super(context, resource, objects);
			this.mContext = context;
			this.objects=objects;
			this.resource = resource;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView=inflater.inflate(resource, parent,false);
			TextView text=(TextView) convertView.findViewById(R.id.listActivityContainerText);
			text.setText(objects.get(position).getText());
			return convertView;
		}
		
		
		
	}
	
	public class getListData extends AsyncTask<RequestParams, Void, ArrayList<ListDetails> >{
		BufferedReader reader;
		StringBuilder sb;
		@Override
		protected ArrayList<ListDetails>  doInBackground(RequestParams... params) {
			try {
				HttpURLConnection con;
				sb=new StringBuilder();
				if((con = params[0].setupConnection())==null){
					Log.d("list","con was null");
					return null;
				}
				reader=new BufferedReader(new InputStreamReader(con.getInputStream()));
				String line="";
				while((line=reader.readLine())!=null){
					sb.append(line);
				}
				return listJSONUtils.JSONParser.parser(sb.toString());
				
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
			super.onPreExecute();
			progress=new customProgress(ListActivity.this);
			String listChoice = getIntent().getExtras().getString(MainActivity.MAINTOLISTCHOICE);
			progress.setMessage("Loading "+listChoice);
			progress.setCancelable(false);
			
			progress.show();
			
			/*progress=ProgressDialog.show(ListActivity.this, null, "Loading "+listChoice, true, true, new DialogInterface.OnCancelListener() {
				
				@Override
				public void onCancel(DialogInterface dialog) {
					// TODO Auto-generated method stub
					onBackPressed();
				}
			});*/
		}

		@Override
		protected void onPostExecute(ArrayList<ListDetails>  result) {
			super.onPostExecute(result);
			if(progress!=null)
				progress.dismiss();
			if(result!=null){
			Log.d("list",result.toString());
			populateListView(result);
			}
		}
	}
	
	private RequestParams initialCheck(){
		RequestParams req = new RequestParams("GET", getResources().getString(R.string.nprListUrl),"");
		req.addParams("output", "JSON");
		
		String listChoice = getIntent().getExtras().getString(MainActivity.MAINTOLISTCHOICE);

		if(listChoice.equals("programs")){
			req.addParams("id", "3004");
			Log.d("list","Pre-Check success in list activity for "+listChoice);
		}else if(listChoice.equals("topics")){
			req.addParams("id", "3002");
			Log.d("list","Pre-Check success in list activity for "+listChoice);
		}else{
			Log.d("list","List activity called incorrectly");
			return null;
		}
		return req;
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

/*	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		finish();
		super.onBackPressed();
	}*/
}
