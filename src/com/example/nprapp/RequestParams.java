/*
Assignment : HomeWork 4
Names:
Bharathram Hariharan
Hemchand Ramireddy
Pratiksha Badgujar
*/
package com.example.nprapp;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;

import android.util.Log;

public class RequestParams {
	String baseUrl,method;
	StringBuilder insideStruct = new StringBuilder();
	HashMap<String, String> values= new HashMap<String, String>();
	
	

	public RequestParams( String method,String baseUrl,String insideStruct){
		this.baseUrl= baseUrl;
		this.method=method;
		this.insideStruct.append(insideStruct);
		if(this.insideStruct!=null && this.insideStruct.equals("")){
			this.insideStruct.append("/");
		}
		//method check
		if(this.method.equals("GET") || this.method.equals("POST")){
			Log.d("Request Param Method", this.method);
		}
		else{
			Log.d("Request Param Method", "Not defined Properly");
		}
	}
	
	public void addParams(String key, String value){
		this.values.put(key, value);
	}
	
	public String getEncodedParams(){
		
		StringBuilder sb = new StringBuilder();
		String tempValue;
		for(String CurrentKey:this.values.keySet()){
			try {
				tempValue=URLEncoder.encode(values.get(CurrentKey), "UTF-8") ;
				sb.append(CurrentKey+"="+tempValue+"&");
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		if(this.values.keySet().size()>0){
			sb.deleteCharAt(sb.length()-1);
		}
		
		
		return sb.toString();
	}
	
	
	public String getEncodedUrl(){
		return baseUrl+this.insideStruct.toString()+"?"+getEncodedParams();
	}
	
	public HttpURLConnection setupConnection() throws IOException{
		Log.d("Request Param method","values in hashmap are "+values.toString()+" and note that it will be LIFO");
		Log.d("Request Params Set Up Connection","Requesting the following url "+ getEncodedUrl());
		if(this.method.equals("GET")){
			URL url = new URL(getEncodedUrl());
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setRequestMethod("GET");
			con.connect();
			if(con.getResponseCode()==HttpURLConnection.HTTP_OK){
				Log.d("Request Params Set Up Connection","Connection established");
				return con;
			}
			else{
				Log.d("Request Params Set Up Connection","Connection not established");
			}
		}else if(this.method.equals("POST")){
			URL url = new URL(getEncodedUrl());
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setRequestMethod("POST");
			con.setDoOutput(true);
			OutputStreamWriter writer = new OutputStreamWriter(con.getOutputStream());
			writer.write(getEncodedParams());
			writer.close();
			return con;
		}
		return null;
	}
	
	@Override
	public String toString() {
		return "RequestParams [baseUrl=" + baseUrl + ", method=" + method
				+ ", values=" + values + "]";
	}
}
