package com.model;

public class Event {
	
	private String title;
	private String description;
	private boolean all_day;
	private String start_time;
	private String end_time;
	private boolean repeat;
	private String calendar_id;
	
	private String recurrence_type;
	private String recurrence_interval;
	private String day_of_week;
	private String date_of_month;
	private String month_of_year;
	
	public Event(String calendar_id, String title,String description,boolean all_day,String start_time,String end_time,boolean repeat) {
		this.setCalendar_id(calendar_id);
		this.title = title;
		this.description = description;
		this.all_day = all_day;
		this.start_time = start_time;
		this.end_time = end_time;
		this.repeat = repeat;
	}
	
	public Event(String calendar_id, String title,String description, boolean all_day,String start_time,String end_time,boolean repeat,String recurrence_type,String recurrence_interval,String day_of_week,String date_of_month,String month_of_year) {
		this.setCalendar_id(calendar_id);
		this.title = title;
		this.description = description;
		this.all_day = all_day;
		this.start_time = start_time;
		this.end_time = end_time;
		this.repeat = repeat;
		this.recurrence_type = recurrence_type;
		this.recurrence_interval = recurrence_interval;
		this.day_of_week = day_of_week;
		this.date_of_month = date_of_month;
		this.month_of_year = month_of_year;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public boolean isAll_day() {
		return all_day;
	}
	public void setAll_day(boolean all_day) {
		this.all_day = all_day;
	}
	public String getStart_time() {
		return start_time;
	}
	public void setStart_time(String start_time) {
		this.start_time = start_time;
	}
	public String getEnd_time() {
		return end_time;
	}
	public void setEnd_time(String end_time) {
		this.end_time = end_time;
	}
	public boolean getRepeat() {
		return repeat;
	}
	public void setRepeat(boolean repeat) {
		this.repeat = repeat;
	}
	public String getCalendar_id() {
		return calendar_id;
	}
	public void setCalendar_id(String calendar_id) {
		this.calendar_id = calendar_id;
	}
	public String getRecurrence_type() {
		return recurrence_type;
	}
	public void setRecurrence_type(String recurrence_type) {
		this.recurrence_type = recurrence_type;
	}
	public String getRecurrence_interval() {
		return recurrence_interval;
	}
	public void setRecurrence_interval(String recurrence_interval) {
		this.recurrence_interval = recurrence_interval;
	}
	public String getDay_of_week() {
		return day_of_week;
	}
	public void setDay_of_week(String day_of_week) {
		this.day_of_week = day_of_week;
	}
	public String getDate_of_month() {
		return date_of_month;
	}
	public void setDate_of_month(String date_of_month) {
		this.date_of_month = date_of_month;
	}
	public String getMonth_of_year() {
		return month_of_year;
	}
	public void setMonth_of_year(String month_of_year) {
		this.month_of_year = month_of_year;
	}
	
}
