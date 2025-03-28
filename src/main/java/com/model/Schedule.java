package com.model;

public class Schedule {
	private int calendar_id;
	private String title;
	private String description;
	private boolean allDay;
	private String startTime;
	private String endTime;
	private boolean isRecurring;
	private String frequency;
	private int repeatInterval;
	private String day;
	private String date;
	private String week;
	private String month;
	private int offset;
	
	
	public Schedule(int calendar_id,String title,String description,boolean allDay,String startTime,String endTime,boolean isRecurring,String frequency,int repeatInterval,String day,String date,String week,String month,int offset) {
		this.calendar_id = calendar_id;
		this.title = title;
		this.description = description;
		this.allDay = allDay;
		this.startTime = startTime;
		this.endTime = endTime;
		this.isRecurring  = isRecurring;
		this.frequency = frequency;
		this.repeatInterval = repeatInterval;
		this.day = day;
		this.date = date;
		this.week = week;
		this.month = month;
		this.offset = offset;
	}
	
	
	
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public boolean isAllDay() {
		return allDay;
	}
	public void setAllDay(boolean all_day) {
		this.allDay = all_day;
	}
	public String getStartTime() {
		return startTime;
	}
	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}
	public boolean isRecurring() {
		return isRecurring;
	}
	public void setRecurring(boolean isRecurring) {
		this.isRecurring = isRecurring;
	}
	public String getFrequency() {
		return frequency;
	}
	public void setFrequency(String frequency) {
		this.frequency = frequency;
	}
	public int getRepeatInterval() {
		return repeatInterval;
	}
	public void setRepeatInterval(int repeatInterval) {
		this.repeatInterval = repeatInterval;
	}
	public String getDay() {
		return day;
	}
	public void setDay(String day) {
		this.day = day;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public String getWeek() {
		return week;
	}
	public void setWeek(String week) {
		this.week = week;
	}
	public String getMonth() {
		return month;
	}
	public void setMonth(String month) {
		this.month = month;
	}
	public int getOffset() {
		return offset;
	}
	public void setOffset(int offset) {
		this.offset = offset;
	}



	public String getEndTime() {
		return endTime;
	}



	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}



	public int getCalendar_id() {
		return calendar_id;
	}



	public void setCalendar_id(int calendar_id) {
		this.calendar_id = calendar_id;
	}
}
