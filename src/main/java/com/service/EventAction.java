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
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.ServletActionContext;

import com.google.gson.Gson;
import com.model.Database;
import com.model.Event;
import com.opensymphony.xwork2.ActionSupport;

public class EventAction extends ActionSupport{

	private static final long serialVersionUID = 363773494066800857L;
	private static String ORIGIN_STRING = "http://127.0.0.1:5501";
	private static Map<String,String> map = new HashMap<>();
	private static HttpServletRequest request;
	private static HttpServletResponse response;
	private static Connection conn;
	private static InputStream inputStream;
	
	{
		try {
			conn = Database.getConnection();
		}
		catch(Exception e) {
			
		}
	}
	public void helper() {
		request = ServletActionContext.getRequest();
		response = ServletActionContext.getResponse();
	}
	public void doOptions(HttpServletRequest request,HttpServletResponse response) {
		response.setHeader("Access-Control-Allow-Origin",ORIGIN_STRING);
		response.setHeader("Access-Control-Allow-Methods","GET,PUT,DELETE,POST,OPTIONS");
		response.setHeader("Access-Control-Allow-Headers","Content-Type,Authorization");
		response.setHeader("Access-Control-Allow-Credentials","true");
		response.setStatus(HttpServletResponse.SC_OK);	
	}
	
	public String doPost() throws ServletException, IOException, ClassNotFoundException, SQLException{
		
		helper();
		if("OPTIONS".equals(request.getMethod())) {
			doOptions(request,response);
			return SUCCESS;
		}
		response.setHeader("Access-Control-Allow-Origin",ORIGIN_STRING);
		response.setHeader("Access-Control-Allow-Methods","GET,PUT,DELETE,POST,OPTIONS");
		response.setHeader("Access-Control-Allow-Headers","Content-Type,Authorization");
		response.setHeader("Access-Control-Allow-Credentials","true");
		
		BufferedReader reader = new BufferedReader(new InputStreamReader(request.getInputStream()));
		StringBuilder sb = new StringBuilder();
		String line = "";
		while((line = reader.readLine())!=null) {
			sb.append(line);
		}
		Gson gson = new Gson();
		Event event = gson.fromJson(sb.toString(),Event.class);
		try {
				String query = "insert into events(calendar_id,title,description,all_day,start_time,end_time,`repeat`) values(?,?,?,?,?,?,?)";
				PreparedStatement stmt = conn.prepareStatement(query);
				stmt.setString(1,event.getCalendar_id());
				stmt.setString(2,event.getTitle());
				stmt.setString(3,event.getDescription());
				stmt.setBoolean(4,event.isAll_day());
				stmt.setString(5,event.getStart_time());
				stmt.setString(6,event.getEnd_time());
				stmt.setBoolean(7,event.getRepeat());
				
				int rows = stmt.executeUpdate();
				if(rows<1) {
					map.put("message","Unable to insert event");
					map.put("status","failed");
					inputStream = new ByteArrayInputStream(gson.toJson(map).getBytes(StandardCharsets.UTF_8));
					return ERROR;
				}
				if (event.getRepeat()) {
					rows = 0;
					query ="select event_id from events where title = ?";
					stmt = conn.prepareStatement(query);
					ResultSet rs = stmt.executeQuery();
					String event_id="";
					if(rs.next()) {
						event_id = rs.getString("event_id");
					}
					query = "insert into recurrence(event_id,recurrence_type,recurrence_interval,day_of_the_week,date_of_month,month_of_year) values(?,?,?,?,?,?)";
					stmt= conn.prepareStatement(query);
					stmt.setString(1,event_id);
					stmt.setString(2,event.getRecurrence_type());
					stmt.setString(3,event.getRecurrence_interval());
					stmt.setString(4,event.getDay_of_week());
					stmt.setString(5,event.getDate_of_month());
					stmt.setString(6,event.getMonth_of_year());
					rows= stmt.executeUpdate();
					if(rows<1) {
						map.put("message","Unable to insert repeat event");
						map.put("status","failed");
						inputStream = new ByteArrayInputStream(gson.toJson(map).getBytes(StandardCharsets.UTF_8));
						return ERROR;
					}
				}
				map.put("message","Record inserted successfully");
				map.put("status","success");
				inputStream = new ByteArrayInputStream(gson.toJson(map).getBytes(StandardCharsets.UTF_8));
				return SUCCESS;
				
		}
		catch(Exception e) {
			map.put("message",e.getMessage());
			map.put("status","failed");
			inputStream = new ByteArrayInputStream(gson.toJson(map).getBytes(StandardCharsets.UTF_8));
			return ERROR;
		}
	}
	public InputStream getInputStream() {
		return inputStream;
	}
	public void setInputStream(InputStream inputStream) {
		EventAction.inputStream = inputStream;
	}
}
