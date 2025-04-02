package com.service;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.ServletActionContext;

import com.google.gson.Gson;
import com.model.Database;
import com.model.Schedule;
import com.opensymphony.xwork2.ActionSupport;

public class ScheduleAction extends ActionSupport{

	private static final long serialVersionUID = 7156203297401441841L;
	private static String ORIGIN_STRING = "http://127.0.0.1:5501";
	private static Connection connection;
	private static Map<String,String> map = new HashMap<>();
	private InputStream responseData = new ByteArrayInputStream("Null".getBytes());
	public  InputStream getResponseData() {
		return responseData;
	}
	public void doOptions(HttpServletRequest request,HttpServletResponse response) {
		response.setHeader("Access-Control-Allow-Origin",ORIGIN_STRING);
		response.setHeader("Access-Control-Allow-Methods","GET,POST,PUT,DELETE,OPTIONS");
		response.setHeader("Access-Control-Allow-Headers","Content-Type, Authorization");
		response.setStatus(HttpServletResponse.SC_OK);
	}
	
	{
		try {
			connection = Database.getConnection();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	private static Map<String,String> encodeDay = Map.of("Monday","1","Tuesday","2","Wednesday","3","Thursday","4","Friday","5","Saturday","6","Sunday","7","Day","8");
	private static Map<String,String> decodeDay = Map.of("1","MONDAY","2","TUESDAY","3","WEDNESDAY","4","THURSDAY","5","FRIDAY","6","SATURDAY","7","SUNDAY");
	
	private static Map<String,String> encodeWeek = Map.of("First","1","Second","2","Third","3","Fourth","4","Fifth","5","Last","6");
	
	public Schedule encodeSchedule(Schedule schedule) {
		schedule.setTitle(schedule.getTitle().trim());
		schedule.setDescription(schedule.getDescription().trim());
		if(schedule.getDay()!=null) {
			String[] dayArray = schedule.getDay().split(",");
			StringBuilder builder = new StringBuilder();
			for(String day:dayArray) {
				System.out.println(day+encodeDay.get(day));
				builder.append(encodeDay.get(day)+",");
			}
			System.out.println(builder.toString());
			schedule.setDay(builder.toString());
		}
		if(schedule.getWeek()!=null) {
			schedule.setWeek(encodeWeek.get(schedule.getWeek()));
		}
		return schedule;
	}
	public String doPost() throws IOException {
		
		HttpServletRequest request = ServletActionContext.getRequest();
		HttpServletResponse response = ServletActionContext.getResponse();
		
		if("OPTIONS".equals(request.getMethod())) {
			doOptions(request,response);
			return NONE;
		}
		
		response.setHeader("Access-Control-Allow-Origin",ORIGIN_STRING);
		response.setHeader("Access-Control-Allow-Methods","POST,GET,PUT,DELETE,OPTIONS");
		response.setHeader("Access-Control-Allow-Headers","Content-Type, Authorization");
		
		BufferedReader reader = new BufferedReader(new InputStreamReader(request.getInputStream()));
		StringBuilder builder = new StringBuilder();
		String line = "";
		while((line=reader.readLine())!=null) {
			builder.append(line);
		}
		Gson gson = new Gson();
		 if (builder.toString().isEmpty()) {
	            map.put("message", "No data received");
	            map.put("status", "error");
	            responseData = new ByteArrayInputStream(gson.toJson(map).getBytes("UTF-8"));
	            return ERROR;
	    }
//		System.out.println(builder.toString());
		Schedule schedule = gson.fromJson(builder.toString(),Schedule.class);
		
//		System.out.println("Is Recurring: "+schedule.isRecurring());
		
		schedule = encodeSchedule(schedule);
		
		try {
			String query = "insert into schedule(calendar_id,title,description,all_day,start_time,end_time,is_recurring,frequency,repeat_interval,day,date,month,week,offset) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
			PreparedStatement stmt = connection.prepareStatement(query);
			stmt.setInt(1,schedule.getCalendarId());
			stmt.setString(2,schedule.getTitle());
			stmt.setString(3,schedule.getDescription());
			stmt.setBoolean(4,schedule.isAllDay());
			stmt.setString(5,schedule.getStartTime());
			stmt.setString(6,schedule.getEndTime());
			stmt.setBoolean(7,schedule.isRecurring());
			stmt.setString(8,schedule.getFrequency());
			stmt.setDouble(9,schedule.getRepeatInterval());
			stmt.setString(10,schedule.getDay());
			stmt.setString(11,schedule.getDate());
			stmt.setString(12,schedule.getMonth());
			stmt.setString(13,schedule.getWeek());
			stmt.setInt(14,schedule.getOffset());
			
			int rows = stmt.executeUpdate();
			
			if(rows<1) {
				map.put("message","Unable to insert schedule");
				map.put("status","error");
				responseData = new ByteArrayInputStream(gson.toJson(map).getBytes());
				return ERROR;
			}
			
			map.put("message","Schedule inserted successfully");
			map.put("status","success");
			responseData = new ByteArrayInputStream(gson.toJson(map).getBytes(StandardCharsets.UTF_8));
			return SUCCESS;
		}
		catch(Exception e) {
			e.printStackTrace();
			map.put("message",e.toString());
			map.put("status","error");
			responseData = new ByteArrayInputStream(gson.toJson(map).getBytes(StandardCharsets.UTF_8));
			return ERROR;
		}
	}
	
	public String doGet() throws IOException {
		HttpServletRequest request = ServletActionContext.getRequest();
		HttpServletResponse response = ServletActionContext.getResponse();
		
		if("OPTIONS".equals(request.getMethod())) {
			doOptions(request,response);
			return NONE;
		}
		response.setHeader("Access-Control-Allow-Origin",ORIGIN_STRING);
		response.setHeader("Access-Control-Allow-Methods","POST,GET,PUT,DELETE,OPTIONS");
		response.setHeader("Access-Control-Allow-Headers","Content-Type, Authorization");
		
		List<Schedule> schedules = new ArrayList<>();
		Gson gson = new Gson();
		BufferedReader reader = new BufferedReader(new InputStreamReader(request.getInputStream()));
		StringBuilder builder = new StringBuilder();
		String line = "";
		while((line=reader.readLine())!=null) {
			builder.append(line);
		}
		 if (builder.toString().isEmpty()) {
	            map.put("message", "No data received");
	            map.put("status", "error");
	            responseData = new ByteArrayInputStream(gson.toJson(map).getBytes("UTF-8"));
	            return ERROR;
	    }
		System.out.println(builder.toString());
		Schedule idSchedule = gson.fromJson(builder.toString(),Schedule.class);
		int calendarId = 0;
		if(idSchedule!=null) {
			calendarId = idSchedule.getCalendarId();
		}else {
			map.put("message","Calendar id not found");
			map.put("status","error");
			responseData = new ByteArrayInputStream(gson.toJson(map).getBytes("UTF-8"));
			return ERROR;
		}
//		System.out.println(calendarId);
		String query = "select * from schedule where calendar_id = ?";
		try(PreparedStatement stmt = connection.prepareStatement(query)){
			stmt.setInt(1,calendarId);
			ResultSet rs = stmt.executeQuery();
			if(rs==null){
				map.put("message","No schedules found");
				map.put("status","error");
				responseData = new ByteArrayInputStream(gson.toJson(map).getBytes("UTF-8"));
				return ERROR;
			}
			while(rs.next()) {
				Schedule newSchedule = new Schedule(rs.getInt("calendar_id"),
										rs.getString("title"),
										rs.getString("description"),
										rs.getBoolean("all_day"),
										rs.getString("start_time"),
										rs.getString("end_time"),
										rs.getBoolean("is_recurring"),
										rs.getString("frequency"),
										rs.getDouble("repeat_interval"),
										rs.getString("day"),
										rs.getString("date"),
										rs.getString("week"),
										rs.getString("month"),
										rs.getInt("offset"),
										rs.getInt("schedule_id")
						);
//				System.out.println("Rs "+newSchedule.getWeek());
//				schedules.add(newSchedule);
				schedules.addAll(populateSchedules(newSchedule));
			}
			if(schedules.size()>0) {
				responseData = new ByteArrayInputStream(gson.toJson(schedules).getBytes("UTF-8"));
				return SUCCESS;
			}
			else {
				map.put("message","No schedules found");
				map.put("status","error");
				responseData = new ByteArrayInputStream(gson.toJson(map).getBytes("UTF-8"));
				return ERROR;
			}
		}
		catch(Exception e) {
			e.printStackTrace();
			map.put("message",e.toString());
			map.put("status","error");
			responseData = new ByteArrayInputStream(gson.toJson(map).getBytes("UTF-8"));
			return ERROR;
		}
//		return ERROR;
	}
	
	
	public String doPut() throws IOException {
		HttpServletRequest request = ServletActionContext.getRequest();
		HttpServletResponse response = ServletActionContext.getResponse();
		
		if("OPTIONS".equals(request.getMethod())) {
			doOptions(request,response);
			return NONE;
		}
		response.setHeader("Access-Control-Allow-Origin",ORIGIN_STRING);
		response.setHeader("Access-Control-Allow-Methods","POST,GET,PUT,DELETE,OPTIONS");
		response.setHeader("Access-Control-Allow-Headers","Content-Type, Authorization");
		
		BufferedReader reader = new BufferedReader(new InputStreamReader(request.getInputStream()));
		StringBuilder builder = new StringBuilder();
		String line = "";
		while((line=reader.readLine())!=null) {
			builder.append(line);
		}
		Gson gson = new Gson();
		Schedule schedule = gson.fromJson(builder.toString(),Schedule.class);
		try {
			String query = "update schedule set title=?, description=?,all_day =?,start_time=?,end_time=?,is_recurring=?,frequency=?,repeat_interval=?,day=?,date=?,month=?,week=?,offset=? where schedule_id = ?";
			PreparedStatement stmt = connection.prepareStatement(query);
			stmt.setString(1,schedule.getTitle());
			stmt.setString(2,schedule.getDescription());
			stmt.setBoolean(3,schedule.isAllDay());
			stmt.setString(4,schedule.getStartTime());
			stmt.setString(5,schedule.getEndTime());
			stmt.setBoolean(6,schedule.isRecurring());
			stmt.setString(7,schedule.getFrequency());
			stmt.setDouble(8,schedule.getRepeatInterval());
			stmt.setString(9,schedule.getDay());
			stmt.setString(10,schedule.getDate());
			stmt.setString(11,schedule.getMonth());
			stmt.setString(12,schedule.getWeek());
			stmt.setInt(13,schedule.getOffset());
			stmt.setInt(14,schedule.getScheduleId());
			
			int rows = stmt.executeUpdate();
			if(rows<1) {
				map.put("message","Unable to update record");
				map.put("status","error");
				responseData = new ByteArrayInputStream(gson.toJson(map).getBytes());
				return ERROR;
			}
			map.put("message","Updated record successfully");
			map.put("status","success");
			responseData = new ByteArrayInputStream(gson.toJson(map).getBytes());
			return SUCCESS;
		}
		catch(Exception e) {
			map.put("message",e.toString());
			map.put("status","error");
			responseData = new ByteArrayInputStream(gson.toJson(map).getBytes());
			return ERROR;
		}
		
	}
	public String doDelete() throws IOException {
		
		HttpServletRequest request = ServletActionContext.getRequest();
		HttpServletResponse response = ServletActionContext.getResponse();
		
		if("OPTIONS".equals(request.getMethod())) {
			doOptions(request,response);
			return NONE;
		}
		response.setHeader("Access-Control-Allow-Origin",ORIGIN_STRING);
		response.setHeader("Access-Control-Allow-Methods","POST,GET,PUT,DELETE,OPTIONS");
		response.setHeader("Access-Control-Allow-Headers","Content-Type, Authorization");
		
		BufferedReader reader = new BufferedReader(new InputStreamReader(request.getInputStream()));
		StringBuilder builder = new StringBuilder();
		String line = "";
		while((line=reader.readLine())!=null) {
			builder.append(line);
		}
		Gson gson = new Gson();
		Schedule schedule = gson.fromJson(builder.toString(),Schedule.class);
		
		try {
			String query = "delete from schedule where calendar_id =? and schedule_id = ?";
			PreparedStatement stmt = connection.prepareStatement(query);
			stmt.setInt(1,schedule.getCalendarId());
			stmt.setInt(2,schedule.getScheduleId());
			System.out.println(builder.toString());
			int rows = stmt.executeUpdate();
			if(rows<1) {
				map.put("message","Unable to delete record");
				map.put("status","error");
				responseData = new ByteArrayInputStream(gson.toJson(map).getBytes());
				return ERROR;
			}
			map.put("message","Deleted record successfully");
			map.put("status","success");
			responseData = new ByteArrayInputStream(gson.toJson(map).getBytes());
			return SUCCESS;
		}
		catch(Exception e) {
			map.put("message",e.toString());
			map.put("status","error");
			responseData = new ByteArrayInputStream(gson.toJson(map).getBytes());
			return ERROR;
		}
	}
	
	public List<Schedule> populateSchedules(Schedule schedule){
		System.out.println("Incubator running");
		List<Schedule> recurringSchedules = new ArrayList<>();
		int interval = (int)schedule.getRepeatInterval();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		
		LocalDateTime startDate = LocalDateTime.parse(schedule.getStartTime(),formatter);
		LocalDateTime endDate = LocalDateTime.parse(schedule.getEndTime(),formatter);
		long duration = Duration.between(startDate,endDate).toMillis();
		if(startDate==null||endDate==null) {
			throw new IllegalArgumentException("Invalid start or end time format");
		}
		int i=0;			
		LocalDateTime yearEnd = LocalDate.of(startDate.getYear(),12,31).atTime(23,59,59);
		LocalDateTime yearStart = LocalDate.of(startDate.getYear(),01,01).atTime(00,00,00);
		
		if("daily".equals(schedule.getFrequency())) {
			while(true) {
				LocalDateTime newStart = startDate.plusDays(i++*interval);
				LocalDateTime newEnd  = startDate.plus(duration,ChronoUnit.MILLIS);
				if(newStart.isAfter(yearEnd)) {
					break;
				}
				recurringSchedules.add(
						new Schedule(schedule.getCalendarId(),
								schedule.getTitle(),
								schedule.getDescription(),
								schedule.isAllDay(),
								newStart.toString().
								replace("T"," "),
								newEnd.toString().replace("T"," "),
								schedule.isRecurring(),
								schedule.getFrequency()
								,schedule.getRepeatInterval(),
								schedule.getDay(),
								schedule.getDate(),
								schedule.getWeek(),
								schedule.getMonth(),
								schedule.getOffset(),
								schedule.getScheduleId()
								));
			}
			System.out.println("populated daily...");
			return recurringSchedules;			
		}
		
		if("weekly".equals(schedule.getFrequency())) {
			
			String days = schedule.getDay();
			LocalDateTime newStart = startDate;
			LocalDateTime newEnd= endDate;
			recurringSchedules.add(
					new Schedule(schedule.getCalendarId(),
							schedule.getTitle()
							,schedule.getDescription(),
							schedule.isAllDay(),
							newStart.toString().replace("T"," "),
							newEnd.toString().replace("T"," "),
							schedule.isRecurring(),
							schedule.getFrequency(),
							schedule.getRepeatInterval(),
							schedule.getDay(),
							schedule.getDate(),
							schedule.getWeek(),
							schedule.getMonth(),
							schedule.getOffset(),
							schedule.getScheduleId()));
			
			while(!newStart.isAfter(yearEnd)) {
				for(String day:days.split(",")) {
					LocalDate newDate = newStart.toLocalDate().with(DayOfWeek.of(Integer.parseInt(day)));
					if(newDate.atTime(startDate.toLocalTime()).isAfter(yearEnd)) {
						break;
					}
					if(newDate.atTime(startDate.toLocalTime()).isBefore(yearStart)){
						continue;
					}
					
					System.out.println("New Date:"+newDate);
					recurringSchedules.add(new Schedule(schedule.getCalendarId(),
							schedule.getTitle(),
							schedule.getDescription(),
							schedule.isAllDay(),
							newDate.atTime(startDate.toLocalTime()).toString().replace("T"," "),
							newDate.atTime(startDate.toLocalTime()).plus(duration,ChronoUnit.MILLIS).toString().replace("T"," "),
							schedule.isRecurring(),
							schedule.getFrequency(),
							schedule.getRepeatInterval(),
							schedule.getDay(),
							schedule.getDate(),
							schedule.getWeek(),
							schedule.getMonth(),
							schedule.getOffset(),
							schedule.getScheduleId()
							));
				}
				newStart = newStart.plusWeeks(interval);
			}
			
			System.out.println("populated weekly....");
			return recurringSchedules;
		}
		
		if("monthly".equals(schedule.getFrequency())) {
		
			System.out.println("Type at first: "+schedule.getWeek());
			LocalDateTime newStart = startDate;
			
			recurringSchedules.add(new Schedule(
					schedule.getCalendarId(),
					schedule.getTitle(),
					schedule.getDescription(),
					schedule.isAllDay(),
					newStart.toString().replace("T"," "),
					endDate.toString().replace("T"," "),
					schedule.isRecurring(),
					schedule.getFrequency(),
					schedule.getRepeatInterval(),
					schedule.getDay(),
					schedule.getDate(),
					schedule.getWeek(),
					schedule.getMonth(),
					schedule.getOffset(),
					schedule.getScheduleId()));
			
			if(schedule.getDate()!=null && schedule.getWeek()==null) {
				String[] dateArray = schedule.getDate().split(",");
				while(!newStart.isAfter(yearEnd)) {
					for(String date:dateArray) {
						LocalDateTime newDate = newStart.withDayOfMonth(Integer.parseInt(date));
//						System.out.println(Integer.parseInt(date)+" "+newDate);
						if(newDate.isBefore(yearStart)) {
							continue;
						}
						if(newDate.isAfter(yearEnd)) {
							break;
						}
						System.out.println("New Monthly Date:"+newDate);
						recurringSchedules.add(new Schedule(schedule.getCalendarId(),
								schedule.getTitle(),
								schedule.getDescription(),
								schedule.isAllDay(),
								newDate.toString().replace("T"," "),
								newDate.plus(duration,ChronoUnit.MILLIS).toString().replace("T"," "),
								schedule.isRecurring(),
								schedule.getFrequency(),
								schedule.getRepeatInterval(),
								schedule.getDay(),
								schedule.getDate(),
								schedule.getWeek(),
								schedule.getMonth(),
								schedule.getOffset(),
								schedule.getScheduleId()
								));
					}
					newStart = newStart.plusMonths(interval);
					
				}
				System.out.println("populated monthly....");
				return recurringSchedules;
			}
			if(schedule.getWeek()!=null) {
				if(schedule.getDay().split(",")[0].equals("8")) {
					while(!newStart.isAfter(yearEnd)) {
//						System.out.println("Day:"+schedule.getDay().split(",")[0].equals("8"));
						LocalDateTime newDate = newStart;
						String type = schedule.getWeek();
						System.out.println("New date"+newDate +" "+type);
						if(type.equals("1")) {
							newDate = newStart.withDayOfMonth(1);
						}
						else if(type.equals("2")) {
							newDate = newStart.withDayOfMonth(2);
						}
						else if(type.equals("3")) {
							newDate = newStart.withDayOfMonth(3);
						}
						else if(type.equals("4")) {
							newDate = newStart.withDayOfMonth(4);
						}
						else if(type.equals("5")) {
							newDate = newStart.withDayOfMonth(5);
						}
						else if(type.equals("6")) {
							YearMonth yearMonth = YearMonth.from(newStart);
							newStart = yearMonth.atEndOfMonth().atTime(newStart.toLocalTime());
						}
						if(newDate.isAfter(yearEnd)) {
							break;
						}

						recurringSchedules.add(new Schedule(schedule.getCalendarId(),
								schedule.getTitle(),
								schedule.getDescription(),
								schedule.isAllDay(),
								newDate.toString().replace("T"," "),
								newDate.plus(duration,ChronoUnit.MILLIS).toString().replace("T"," "),
								schedule.isRecurring(),
								schedule.getFrequency(),
								schedule.getRepeatInterval(),
								schedule.getDay(),
								schedule.getDate(),
								schedule.getWeek(),
								schedule.getMonth(),
								schedule.getOffset(),
								schedule.getScheduleId()));
						System.out.println(newDate);
						newStart = newStart.plusMonths(interval);
					}
					System.out.println("populated monthly...");
					return recurringSchedules;
				}
				else {
					while(!newStart.isAfter(yearEnd)) {
						LocalDate newDate = newStart.toLocalDate();
					    while (newDate.getDayOfWeek() != DayOfWeek.of(Integer.parseInt(schedule.getDay().split(",")[0]))) {
					        newDate = newDate.plusDays(1);
					    }
						LocalDateTime newDateTime = newDate.atTime(newStart.toLocalTime());
						if(newDateTime.isAfter(yearEnd)) {
							break;
						}
						String type = schedule.getWeek();
						if(type.equals("1")) {
							
						}
						else if(type.equals("2")) {
							newDateTime = newDateTime.plusWeeks(1);
						}
						else if(type.equals("3")) {
							newDateTime = newDateTime.plusWeeks(2);
						}
						else if(type.equals("4")) {
							 newDateTime =newDateTime.plusWeeks(3);
						}
						else if(type.equals("5")) {
							 newDateTime = newDateTime.plusWeeks(4);
						}
						
						if (type.equals("6")) {
								YearMonth yearMonth = YearMonth.from(newStart);
								LocalDate lastDayOfMonth = yearMonth.atEndOfMonth();
								while (lastDayOfMonth.getDayOfWeek() != DayOfWeek.of(Integer.parseInt(schedule.getDay().split(",")[0]))) {
									lastDayOfMonth = lastDayOfMonth.minusDays(1);
								}
								newDateTime = lastDayOfMonth.atTime(newStart.toLocalTime());
						}
						if(newDateTime.getMonthValue()>newStart.getMonthValue()) {
							newStart = newStart.plusMonths(interval);
							continue;
						}
						recurringSchedules.add(new Schedule(schedule.getCalendarId(),
								schedule.getTitle(),
								schedule.getDescription(),
								schedule.isAllDay(),
								newDateTime.toString().replace("T"," "),
								newDateTime.plus(duration,ChronoUnit.MILLIS).toString().replace("T"," "),
								schedule.isRecurring(),
								schedule.getFrequency(),
								schedule.getRepeatInterval(),
								schedule.getDay(),
								schedule.getDate(),
								schedule.getWeek(),
								schedule.getMonth(),
								schedule.getOffset(),
								schedule.getScheduleId()));
						System.out.println(newDateTime);
						newStart = newStart.plusMonths(interval);
					}
					System.out.println("populated monthly...");
					return recurringSchedules;
				}
//				return recurringSchedules;
			}
		}
		
		if("yearly".equals(schedule.getFrequency())) {
//			System.out.println("Type at first: "+schedule.getMonth());
			LocalDateTime newStart = startDate.toLocalDate().with(Month.valueOf(schedule.getMonth().toUpperCase())).atTime(startDate.toLocalTime());

			
//			recurringSchedules.add(new Schedule(schedule.getCalendarId(),schedule.getTitle(),schedule.getDescription(),schedule.isAllDay(),newStart.toString().replace("T"," "),endDate.toString().replace("T"," "),schedule.isRecurring(),schedule.getFrequency(),schedule.getRepeatInterval(),schedule.getDay(),schedule.getDate(),schedule.getWeek(),schedule.getMonth(),schedule.getOffset()));
//			System.out.println(schedule.getMonth() +" "+ schedule.getDate());
			yearEnd = yearEnd.plusYears(5);
			if(schedule.getMonth()!=null && schedule.getDate()!=null) {
				String[] dateArray = schedule.getDate().split(",");
				while(!newStart.isAfter(yearEnd)) {
					for(String date:dateArray) {
						LocalDateTime newDate = newStart.withDayOfMonth(Integer.parseInt(date));
//						System.out.println(newDate);
						if(newDate.isBefore(yearStart)) {
							continue;
						}
						if(newDate.isAfter(yearEnd)) {
							break;
						}
						recurringSchedules.add(new Schedule(schedule.getCalendarId(),
								schedule.getTitle(),
								schedule.getDescription(),
								schedule.isAllDay(),
								newDate.toString().replace("T"," "),
								newDate.plus(duration,ChronoUnit.MILLIS).toString().replace("T"," "),
								schedule.isRecurring(),
								schedule.getFrequency(),
								schedule.getRepeatInterval(),
								schedule.getDay(),
								schedule.getDate(),
								schedule.getWeek(),
								schedule.getMonth(),
								schedule.getOffset(),
								schedule.getScheduleId()));
					}
					newStart = getYearlyInterval(newStart,schedule.getRepeatInterval());

//					newStart = newStart.plusMonths(3);
					System.out.println(newStart);
				}
				System.out.println("populated yearly....");
				return recurringSchedules;
			}
			if(schedule.getWeek()!=null) {
				if(schedule.getDay().split(",")[0].equals("8")) {
					newStart = newStart.toLocalDate().with(Month.valueOf(schedule.getMonth().toUpperCase())).atTime(newStart.toLocalTime());
					while(!newStart.isAfter(yearEnd)) {
//						System.out.println("Day:"+schedule.getDay().split(",")[0].equals("8"));
						LocalDateTime newDate = newStart;
						String type = schedule.getWeek();
						System.out.println("New date"+newDate +" "+type);
						if(type.equals("1")) {
							newDate = newStart.withDayOfMonth(1);
						}
						else if(type.equals("2")) {
							newDate = newStart.withDayOfMonth(2);
						}
						else if(type.equals("3")) {
							newDate = newStart.withDayOfMonth(3);
						}
						else if(type.equals("4")) {
							newDate = newStart.withDayOfMonth(4);
						}
						else if(type.equals("5")) {
							newDate = newStart.withDayOfMonth(5);
						}
						else if(type.equals("6")) {
							YearMonth yearMonth = YearMonth.from(newStart);
							newStart = yearMonth.atEndOfMonth().atTime(newStart.toLocalTime());
						}
						if(newDate.isAfter(yearEnd)) {
							break;
						}

						recurringSchedules.add(new Schedule(schedule.getCalendarId(),
								schedule.getTitle(),
								schedule.getDescription(),
								schedule.isAllDay(),
								newDate.toString().replace("T"," "),
								newDate.plus(duration,ChronoUnit.MILLIS).toString().replace("T"," "),
								schedule.isRecurring(),
								schedule.getFrequency(),
								schedule.getRepeatInterval(),
								schedule.getDay(),
								schedule.getDate(),
								schedule.getWeek(),
								schedule.getMonth(),
								schedule.getOffset(),
								schedule.getScheduleId()));
						System.out.println(newDate);
						newStart = getYearlyInterval(newStart,schedule.getRepeatInterval());
					}
					System.out.println("populated monthly...");
					return recurringSchedules;
				}
				else {
					while(!newStart.isAfter(yearEnd)) {
						LocalDate newDate = newStart.toLocalDate().with(Month.valueOf(schedule.getMonth().toUpperCase()));
					    while (newDate.getDayOfWeek() != DayOfWeek.of(Integer.parseInt(schedule.getDay().split(",")[0]))) {
					        newDate = newDate.plusDays(1);
					    }
						LocalDateTime newDateTime = newDate.atTime(newStart.toLocalTime());
						if(newDateTime.isAfter(yearEnd)) {
							break;
						}
						String type = schedule.getWeek();
						if(type.equals("1")) {
							
						}
						else if(type.equals("2")) {
							newDateTime = newDateTime.plusWeeks(1);
						}
						else if(type.equals("3")) {
							newDateTime = newDateTime.plusWeeks(2);
						}
						else if(type.equals("4")) {
							 newDateTime =newDateTime.plusWeeks(3);
						}
						else if(type.equals("5")) {
							 newDateTime = newDateTime.plusWeeks(4);
						}
						
						if (type.equals("6")) {
	
								YearMonth yearMonth = YearMonth.from(newStart);
								LocalDate lastDayOfMonth = yearMonth.atEndOfMonth();
								while (lastDayOfMonth.getDayOfWeek() != DayOfWeek.of(Integer.parseInt(schedule.getDay().split(",")[0]))) {
									lastDayOfMonth = lastDayOfMonth.minusDays(1);
								}
								newDateTime = lastDayOfMonth.atTime(newStart.toLocalTime());
						}
						if(newDateTime.getMonthValue()>newStart.getMonthValue()) {
							newStart = getYearlyInterval(newStart,interval);
							continue;
						}
						recurringSchedules.add(new Schedule(schedule.getCalendarId(),
								schedule.getTitle(),
								schedule.getDescription(),
								schedule.isAllDay(),
								newDateTime.toString().replace("T"," "),
								newDateTime.plus(duration,ChronoUnit.MILLIS).toString().replace("T"," "),
								schedule.isRecurring(),
								schedule.getFrequency(),
								schedule.getRepeatInterval(),
								schedule.getDay(),
								schedule.getDate(),
								schedule.getWeek(),
								schedule.getMonth(),
								schedule.getOffset(),
								schedule.getScheduleId()));
						System.out.println(newDateTime);
						newStart = getYearlyInterval(newStart,interval);
					}
					System.out.println("populated yearly...");
					return recurringSchedules;
				}
			}
		}
		
		return recurringSchedules;
	}
	
	public LocalDateTime getYearlyInterval(LocalDateTime newStart,double interval) {
		
		if(Double.compare(interval,1)==0) {
			newStart = newStart.plusYears(1);
		}
		else if(Double.compare(interval,2)==0) {
			newStart =  newStart.plusYears(2);
		}
		else if(Double.compare(interval,0.5)==0) {
			newStart =  newStart.plusMonths(6);	
		}
		else if(interval==0.25) {
			newStart = newStart.plusMonths(3);
		}
		
		return newStart;
	}
	
}
