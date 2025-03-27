package com.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import com.model.Database;
import com.model.Event;

public class Recurrence {
	

	static List<Event> generateRecurringEvents(String calendar_id,String eventId,String title,String desc,boolean allDay, String startTime, String endTime,boolean repeat) throws ClassNotFoundException, SQLException{
		
		System.out.println("Generator running");
		Connection conn = Database.getConnection();
		List<Event> recurringEvents = new ArrayList<>();
		String recurrenceQuery = "select * from recurrence where event_id = ?";
		try(PreparedStatement stmt = conn.prepareStatement(recurrenceQuery)){
			System.out.println(eventId);
			stmt.setString(1,eventId);
			ResultSet rs = stmt.executeQuery();
			if(rs.next()) {
				System.out.println("recurrence data fetched");
				String recurrenceType = null;
				if(rs.getString("recurrence_type").contains("custom")) {
					 recurrenceType = rs.getString("recurrence_type").split(" ")[1];
				}
				else {
					 recurrenceType = rs.getString("recurrence_type");
				}
				
				String recurrenceInterval = rs.getString("recurrence_interval");
				if(recurrenceInterval=="") {
					recurrenceInterval = "1";
				}
				switch(recurrenceType) {
				
					case "daily":
						recurringEvents = generateDailyRecurringEvents(calendar_id,title,desc,allDay,startTime,endTime,recurrenceInterval,repeat);
						break;
					case "weekly":
						recurringEvents = generateWeeklyRecurringEvents(calendar_id,title,desc,allDay,startTime,endTime,recurrenceInterval,repeat);
						break;
					case "monthly":
						recurringEvents = generateMonthlyRecurringEvents(calendar_id,title,desc,allDay,startTime,endTime,recurrenceInterval,repeat);
						break;
					case "yearly":
						recurringEvents = generateYearlyRecurringEvents(calendar_id,title,desc,allDay,startTime,endTime,recurrenceInterval,repeat);
						break;
					default:
						break;

				}
			}
		}
		
		catch(Exception e) {
			e.printStackTrace();
		}
			
		return recurringEvents;
	}
	
	private static List<Event> generateDailyRecurringEvents(String calendar_id,String title,String desc,boolean allDay, String startTime,String endTime,String recurrenceInterval,boolean repeat){
		List<Event> recurringEvents = new ArrayList<>();
		System.out.println("daily generator");
		
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyy-MM-dd HH:mm:ss");
		LocalDateTime startLocalDateTime = LocalDateTime.parse(startTime,formatter);
		LocalDateTime endLocalDateTime = LocalDateTime.parse(endTime,formatter);
		if (startLocalDateTime == null || endLocalDateTime == null) {
	        throw new IllegalArgumentException("Invalid start or end time format");
	    }
		LocalDateTime startDate = startLocalDateTime;
		
		int interval = Integer.parseInt(recurrenceInterval);
		
		int currentYear = startLocalDateTime.getYear();
		LocalDate lastDay = LocalDate.of(currentYear,12,31);
		LocalDateTime lastDateTime  = lastDay.atTime(23,59,59);
		int i=0;
		while(true) {
			LocalDateTime eventStartDateTime = startDate.plusDays(i*interval);
			
			if(eventStartDateTime.isAfter(lastDateTime)) {
				break;
			}
			
			LocalDateTime eventEndDateTime = eventStartDateTime.plusHours(endLocalDateTime.getHour()-startLocalDateTime.getHour()).plusMinutes(endLocalDateTime.getMinute()-startLocalDateTime.getMinute()).plusSeconds(endLocalDateTime.getSecond()-startLocalDateTime.getSecond());
			Event event = new Event(calendar_id,title,desc,allDay,eventStartDateTime.toString().replace("T"," "),eventEndDateTime.toString().replace("T"," "),repeat);
			recurringEvents.add(event);
			i++;
		}
		
		return recurringEvents;
	}
	
	private static List<Event> generateWeeklyRecurringEvents(String calendar_id,String title,String desc,boolean allDay,String startTime,String endTime,String recurrenceInterval,boolean repeat){
		List<Event> recurringEvents = new ArrayList<>();
		System.out.println("weekly generator");

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		
		LocalDateTime startLocalDateTime = LocalDateTime.parse(startTime,formatter);
		LocalDateTime endLocalDateTime = LocalDateTime.parse(endTime,formatter);
		
		long duration = Duration.between(startLocalDateTime,endLocalDateTime).toMillis();
		
		LocalDateTime startDate = startLocalDateTime;
		int interval = Integer.parseInt(recurrenceInterval);
		
		int currentYear = startLocalDateTime.getYear();
		LocalDate lastDay = LocalDate.of(currentYear,12,31);
		LocalDateTime lastDateTime = lastDay.atTime(23,59,59);
		int i=0;
		
		while(true) {
			LocalDateTime eventStartDateTime = startDate.plusWeeks(i*interval);
			
			if(eventStartDateTime.isAfter(lastDateTime)) {
				break;
			}
			LocalDateTime eventEndDateTime = eventStartDateTime.plus(duration,ChronoUnit.MILLIS);
			
			Event event = new Event(calendar_id,title,desc,allDay,eventStartDateTime.toString().replace("T"," "),eventEndDateTime.toString().replace("T"," "),repeat);
			recurringEvents.add(event);
			i++;
		}
		
			
		return recurringEvents;
	}
	
	
	private static List<Event> generateMonthlyRecurringEvents(String calendar_id,String title,String desc,boolean allDay,String startTime,String endTime,String recurrenceInterval,boolean repeat){
		
		List<Event> recurringEvents = new ArrayList<>();
		System.out.println("Monthly generator");

		
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		
		LocalDateTime startLocalDateTime = LocalDateTime.parse(startTime,formatter);
		LocalDateTime endLocalDateTime = LocalDateTime.parse(endTime,formatter);
		
		
		long duration = Duration.between(startLocalDateTime,endLocalDateTime).toMillis();
		
		int interval = Integer.parseInt(recurrenceInterval);
		
		for(int i=0;i<10;i++) {
			LocalDateTime eventStartDateTime = startLocalDateTime.plusMonths(i*interval);
			
			if(eventStartDateTime.getDayOfMonth() != startLocalDateTime.getDayOfMonth()) {
				eventStartDateTime = eventStartDateTime.withDayOfMonth(eventStartDateTime.toLocalDate().lengthOfMonth());
			}
			
			LocalDateTime eventEndDateTime = eventStartDateTime.plus(duration,ChronoUnit.MILLIS);
			
			Event event = new Event(calendar_id,title,desc,allDay,eventStartDateTime.toString().replace("T"," "),eventEndDateTime.toString().replace("T"," "),repeat);
			recurringEvents.add(event);
		}
		
		return recurringEvents;
	}
	
	private static List<Event> generateYearlyRecurringEvents(String calendar_id,String title,String desc,boolean allDay,String startTime,String endTime,String recurrenceInterval, boolean repeat){
		
		List<Event> recurringEvents = new ArrayList<>();
		System.out.println("Yearly generator");

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		
		LocalDateTime startLocalDateTime = LocalDateTime.parse(startTime,formatter);
		LocalDateTime endLocalDateTime = LocalDateTime.parse(endTime,formatter);
		
		int interval = Integer.parseInt(recurrenceInterval);
		long duration = Duration.between(startLocalDateTime,endLocalDateTime).toMillis();
		
		for(int i=0;i<10;i++) {
			LocalDateTime eventStartDateTime = startLocalDateTime.plusYears(i*interval);
			if(eventStartDateTime.getMonthValue()==2 && eventStartDateTime.getDayOfMonth()==29) {
				eventStartDateTime = eventStartDateTime.withDayOfMonth(28);
			}
			
			LocalDateTime eventEndDateTime = eventStartDateTime.plus(duration,ChronoUnit.MILLIS);
			
			Event event = new Event(calendar_id,title,desc,allDay,eventStartDateTime.toString().replace("T"," "),eventEndDateTime.toString().replace("T"," "),repeat);
			recurringEvents.add(event);
		}
		
		return recurringEvents;
	}
	
}
