/*
Assignment : HomeWork 4
Names:
Bharathram Hariharan
Hemchand Ramireddy
Pratiksha Badgujar
*/
package com.example.nprapp;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class StoryDetails implements Serializable{
	long id;
	String title,audioUrl,imageUrl,teaserText,name,webLink;
	String pubDate;
	int durationSeconds;
	
	public void setDurationSeconds(String sec){
		this.durationSeconds = Integer.valueOf(sec);
	}
	public int getDurationSeconds(){
		return this.durationSeconds;
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getAudioUrl() {
		return audioUrl;
	}
	public void setAudioUrl(String audioUrl) {
		this.audioUrl = audioUrl;
	}
	public String getImageUrl() {
		return imageUrl;
	}
	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}
	public String getTeaserText() {
		return teaserText;
	}
	public void setTeaserText(String teaserText) {
		this.teaserText = teaserText;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getWebLink() {
		return webLink;
	}
	public void setWebLink(String webLink) {
		this.webLink = webLink;
	}
	public String getPubDate() {
		return pubDate;
	}
	public void setPubDate(String dateTemp) {
		this.pubDate = dateTemp;
//		this.pubDate = pubDate;
	}
	
	
	
	@Override
	public String toString() {
		return "StoryDetails [id=" + id + ", title=" + title + ", audioUrl="
				+ audioUrl + ", imageUrl=" + imageUrl + ", teaserText="
				+ teaserText + ", name=" + name + ", webLink=" + webLink
				+ ", pubDate=" + pubDate +"Duration =" + durationSeconds +"]";
	}
	
	
}
