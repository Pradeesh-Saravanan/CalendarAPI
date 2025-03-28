package com.service;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.HashMap;
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
	private static InputStream responseData;
	public void doOptions(HttpServletRequest request,HttpServletResponse response) {
		response.setHeader("Access-Control-Allow-Origin",ORIGIN_STRING);
		response.setHeader("Access-Control-Allow-Methods","GET,POST,PUT,DELETE,OPTIONS");
		response.setHeader("Access-Control-Allow-Headers","Content-Type");
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
	public String doPost() throws IOException {
		
		HttpServletRequest request = ServletActionContext.getRequest();
		HttpServletResponse response = ServletActionContext.getResponse();
		
		if("OPTIONS".equals(request.getMethod())) {
			doOptions(request,response);
			return NONE;
		}
		
		response.setHeader("Access-Control-Allow-Origin",ORIGIN_STRING);
		response.setHeader("Access-Control-Allow-Methods","POST,GET,PUT,DELETE,OPTIONS");
		response.setHeader("Access-Control-Allow-Headers","Content-Type");
		
		BufferedReader reader = new BufferedReader(new InputStreamReader(request.getInputStream()));
		StringBuilder builder = new StringBuilder();
		String line = "";
		while((line=reader.readLine())!=null) {
			builder.append(line);
		}
		Gson gson = new Gson();
		System.out.println(builder.toString());
		Schedule schedule = gson.fromJson(builder.toString(),Schedule.class);
		try {
			String query = "insert into schedule(calendar_id,title,description,all_day,start_time,end_time,is_recurring,frequency,repeat_interval,day,date,month,week,offset) values(?,?,?,?,?,?,?,?,?,?,?,?,?)";
			PreparedStatement stmt = connection.prepareStatement(query);
			stmt.setInt(1,schedule.getCalendar_id());
			stmt.setString(1,schedule.getTitle());
			stmt.setString(2,schedule.getDescription());
			stmt.setBoolean(3,schedule.isAllDay());
			stmt.setString(4,schedule.getStartTime());
			stmt.setString(5,schedule.getEndTime());
			stmt.setBoolean(6,schedule.isRecurring());
			stmt.setString(7,schedule.getFrequency());
			stmt.setInt(8,schedule.getRepeatInterval());
			stmt.setString(9,schedule.getDay());
			stmt.setString(10,schedule.getDate());
			stmt.setString(11,schedule.getMonth());
			stmt.setString(12,schedule.getWeek());
			stmt.setInt(13,schedule.getOffset());
			
			int rows = stmt.executeUpdate();
			
			if(rows<1) {
				map.put("message","Unable to insert schedule");
				map.put("status","error");
				responseData = new ByteArrayInputStream(gson.toJson(map).getBytes());
				return ERROR;
			}
			
			map.put("message","Schedule inserted successfully");
			map.put("status","success");
			setResponseData(new ByteArrayInputStream(gson.toJson(map).getBytes(StandardCharsets.UTF_8)));
			return SUCCESS;
		}
		catch(Exception e) {
			e.printStackTrace();
			map.put("message",e.toString());
			map.put("status","error");
			setResponseData(new ByteArrayInputStream(gson.toJson(map).getBytes(StandardCharsets.UTF_8)));
			return ERROR;
		}
	}
	
	public static InputStream getResponseData() {
		return responseData;
	}


	public static void setResponseData(InputStream responseData) {
		ScheduleAction.responseData = responseData;
	}
}
