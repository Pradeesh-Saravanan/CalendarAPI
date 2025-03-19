package com.model;

public class Calendar {
	private String user_id;
	private String calendar_name;
	private String description;
	public Calendar(String calendar_name,String description){
		this.calendar_name = calendar_name;
		this.description = description;
	}
	public String getUser_id() {
		return user_id;
	}
	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}
	public String getCalendar_name() {
		return calendar_name;
	}
	public void setCalendar_name(String calendar_name) {
		this.calendar_name = calendar_name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
}
