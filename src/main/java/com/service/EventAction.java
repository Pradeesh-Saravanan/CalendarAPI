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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
	public String doGet() throws ServletException, IOException, ClassNotFoundException, SQLException{
		helper();
		if("OPTIONS".equals(request.getMethod())) {
			doOptions(request,response);
			return NONE;
		}
		response.setHeader("Access-Control-Allow-Origin",ORIGIN_STRING);
		response.setHeader("Access-Control-Allow-Methods","GET,PUT,DELETE,POST,OPTIONS");
		response.setHeader("Access-Control-Allow-Headers","Content-Type,Authorization");
		response.setHeader("Access-Control-Allow-Credentials","true");
		
		try {
			String user_query = "select * from events where calendar_id = ?";
			PreparedStatement stmt = conn.prepareStatement(user_query);
			stmt.setString(1,request.getParameter("calendar_id"));
			ResultSet rs = stmt.executeQuery();
			List<Event> events = new ArrayList<>();
			if(rs==null) {
				map.put("status","failed");
				map.put("message","No records found");					
				Gson gson = new Gson();
				inputStream = new ByteArrayInputStream(gson.toJson(map).getBytes(StandardCharsets.UTF_8));
				return ERROR;
			}
			while(rs.next()) {
				String title = rs.getString("title");
				String desc = rs.getString("description");
				boolean all_day = rs.getBoolean("all_day");
				String start_time = rs.getString("start_time");
				String end_time = rs.getString("end_time");
				boolean repeat = rs.getBoolean("repeat");
				if(repeat==true) {
//					System.out.println("repeat true");
					List<Event> recurringEvents = Recurrence.generateRecurringEvents(request.getParameter("calendar_id"),rs.getString("event_id"),title,desc,all_day,start_time,end_time,repeat);
					events.addAll(recurringEvents); 
				}
				else {
					events.add(new Event(request.getParameter("calendar_id"),title,desc,all_day,start_time,end_time,repeat));
				}
			}
			Gson gson = new Gson();
			inputStream = new ByteArrayInputStream(gson.toJson(events).getBytes(StandardCharsets.UTF_8));
			return SUCCESS;
		}
		catch(Exception e) {
			map.put("status","failed");
			map.put("message",e.getMessage());
			
			Gson gson = new Gson();
			inputStream = new ByteArrayInputStream(gson.toJson(map).getBytes(StandardCharsets.UTF_8));
			return ERROR;
		}
		
	}
	
	@SuppressWarnings("resource")
	public String doPost() throws ServletException, IOException, ClassNotFoundException, SQLException{
		
		helper();
		if("OPTIONS".equals(request.getMethod())) {
			doOptions(request,response);
			return NONE;
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
					stmt.setString(1,event.getTitle());
					ResultSet rs = stmt.executeQuery();

					String event_id="";
					if(rs.next()) {
						event_id = rs.getString("event_id");
					}
//					event.setRecurrence_type(event.getRecurrence_type().split(" ")[1]);
//					if(event.getRecurrence_type().contains("custom")) {
//						String event_type = event.getRecurrence_type().split(" ")[1];
//						String mod = "",mod_val = "",mod_query="";
//						switch(event_type) {
//						case "daily":
//							break;
//						case "weekly":
//							mod = ",day_of_week";
//							mod_val = event.getDay_of_week();
//							mod_query=",?";
//							break;
//						case "monthly":
//							mod = ",date_of_month";
//							mod_query=",?";
//							mod_val = event.getDate_of_month();
//							break;
//						case "yearly":
//							mod = ",month_of_year";
//							mod_query=",?";
//							mod_val = event.getMonth_of_year();
//							break;
//						default:
//							break;
//						}
//						if(event.getRecurrence_interval()=="") {
//							event.setRecurrence_interval("1");
//						}
//						System.out.println(mod+" "+mod_val);
//						query = "insert into recurrence(event_id,recurrence_type,recurrence_interval"+mod+") values(?,?,?"+mod_query+")";
//						stmt= conn.prepareStatement(query);
//						
//						stmt.setString(1,event_id);
//						stmt.setString(2,event.getRecurrence_type());
//						stmt.setString(3,event.getRecurrence_interval());
//						if(mod!="") {
//							stmt.setString(4,mod_val);
//						}
//						
//						rows= stmt.executeUpdate();
//						if(rows<1) {
//							map.put("message","Unable to insert repeat event");
//							map.put("status","failed");
//							inputStream = new ByteArrayInputStream(gson.toJson(map).getBytes(StandardCharsets.UTF_8));
//							return ERROR;	
//						}
//
//					}
//					else {
						
						query = "insert into recurrence(event_id,recurrence_type,recurrence_interval) values(?,?,?)";
						stmt= conn.prepareStatement(query);
						if(event.getRecurrence_interval().length()==0) {
							event.setRecurrence_interval("1");
						}
						System.out.println(event.getRecurrence_interval());
						stmt.setString(1,event_id);
						stmt.setString(2,event.getRecurrence_type());
						stmt.setString(3,event.getRecurrence_interval());
						
						rows= stmt.executeUpdate();
						if(rows<1) {
							map.put("message","Unable to insert repeat event");
							map.put("status","failed");
							inputStream = new ByteArrayInputStream(gson.toJson(map).getBytes(StandardCharsets.UTF_8));
							return ERROR;
						}

//					}
					
				}
				map.put("message","Record inserted successfully");
				map.put("status","success");
				inputStream = new ByteArrayInputStream(gson.toJson(map).getBytes(StandardCharsets.UTF_8));
				return SUCCESS;
				
		}
		catch(Exception e) {
			e.printStackTrace();
			map.put("message",e.getMessage());
			map.put("status","failed");
			inputStream = new ByteArrayInputStream(gson.toJson(map).getBytes(StandardCharsets.UTF_8));
			return ERROR;
		}
	}
	@SuppressWarnings("resource")
	public String doPut() throws ServletException, IOException, ClassNotFoundException, SQLException{
		
		helper();
		if("OPTIONS".equals(request.getMethod())) {
			doOptions(request,response);
			return NONE;
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
				String query = "update events set calendar_id=?,title=?,description=?,all_day=?,start_time=?,end_time=?,`repeat`=? where title = ?";
				PreparedStatement stmt = conn.prepareStatement(query);
				stmt.setString(1,event.getCalendar_id());
				stmt.setString(2,event.getTitle());
				stmt.setString(3,event.getDescription());
				stmt.setBoolean(4,event.isAll_day());
				stmt.setString(5,event.getStart_time());
				stmt.setString(6,event.getEnd_time());
				stmt.setBoolean(7,event.getRepeat());
				stmt.setString(8,event.getTitle());
				
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
					stmt.setString(1,event.getTitle());
					ResultSet rs = stmt.executeQuery();

					String event_id="";
					if(rs.next()) {
						event_id = rs.getString("event_id");
					}
//					event.setRecurrence_type(event.getRecurrence_type().split(" ")[1]);
//					if(event.getRecurrence_type().contains("custom")) {
//						String event_type = event.getRecurrence_type().split(" ")[1];
//						String mod = "",mod_val = "",mod_query="";
//						switch(event_type) {
//						case "daily":
//							break;
//						case "weekly":
//							mod = ",day_of_week";
//							mod_val = event.getDay_of_week();
//							mod_query=",?";
//							break;
//						case "monthly":
//							mod = ",date_of_month";
//							mod_query=",?";
//							mod_val = event.getDate_of_month();
//							break;
//						case "yearly":
//							mod = ",month_of_year";
//							mod_query=",?";
//							mod_val = event.getMonth_of_year();
//							break;
//						default:
//							break;
//						}
//						if(event.getRecurrence_interval()=="") {
//							event.setRecurrence_interval("1");
//						}
//						System.out.println(mod+" "+mod_val);
//						query = "insert into recurrence(event_id,recurrence_type,recurrence_interval"+mod+") values(?,?,?"+mod_query+")";
//						stmt= conn.prepareStatement(query);
//						
//						stmt.setString(1,event_id);
//						stmt.setString(2,event.getRecurrence_type());
//						stmt.setString(3,event.getRecurrence_interval());
//						if(mod!="") {
//							stmt.setString(4,mod_val);
//						}
//						
//						rows= stmt.executeUpdate();
//						if(rows<1) {
//							map.put("message","Unable to insert repeat event");
//							map.put("status","failed");
//							inputStream = new ByteArrayInputStream(gson.toJson(map).getBytes(StandardCharsets.UTF_8));
//							return ERROR;	
//						}
//
//					}
//					else {
						
						query = "insert into recurrence(event_id,recurrence_type,recurrence_interval) values(?,?,?)";
						stmt= conn.prepareStatement(query);
						if(event.getRecurrence_interval().length()==0) {
							event.setRecurrence_interval("1");
						}
						System.out.println(event.getRecurrence_interval());
						stmt.setString(1,event_id);
						stmt.setString(2,event.getRecurrence_type());
						stmt.setString(3,event.getRecurrence_interval());
						
						rows= stmt.executeUpdate();
						if(rows<1) {
							map.put("message","Unable to insert repeat event");
							map.put("status","failed");
							inputStream = new ByteArrayInputStream(gson.toJson(map).getBytes(StandardCharsets.UTF_8));
							return ERROR;
						}

//					}
					
				}
				map.put("message","Record inserted successfully");
				map.put("status","success");
				inputStream = new ByteArrayInputStream(gson.toJson(map).getBytes(StandardCharsets.UTF_8));
				return SUCCESS;
				
		}
		catch(Exception e) {
			e.printStackTrace();
			map.put("message",e.getMessage());
			map.put("status","failed");
			inputStream = new ByteArrayInputStream(gson.toJson(map).getBytes(StandardCharsets.UTF_8));
			return ERROR;
		}
	}
	
	public String doDelete() throws IOException {
		helper();
		if("OPTIONS".equals(request.getMethod())) {
			doOptions(request,response);
			return NONE;
		}
		response.setHeader("Access-Control-Allow-Origin",ORIGIN_STRING);
		response.setHeader("Access-Control-Allow-Methods","GET,PUT,DELETE,POST,OPTIONS");
		response.setHeader("Access-Control-Allow-Headers","Content-Type,Authorization");
		response.setHeader("Access-Control-Allow-Credentials","true");
		System.out.println("delete is running");
		String query = "delete from events where title = ?";
		Gson gson  = new Gson();
		try(PreparedStatement stmt = conn.prepareStatement(query)){
			stmt.setString(1,request.getParameter("name"));
			System.out.println(request.getParameter("name"));
			int rows = stmt.executeUpdate();
			if(rows<1) {
				map.put("message","Unable to delete event");
				map.put("status","failed");
				inputStream = new ByteArrayInputStream(gson.toJson(map).getBytes(StandardCharsets.UTF_8));
				return ERROR;
			}
			map.put("message","Event deleted successfully");
			map.put("status","success");
			inputStream = new ByteArrayInputStream(gson.toJson(map).getBytes(StandardCharsets.UTF_8));
			return SUCCESS;
		}
		catch(Exception e) {
			e.printStackTrace();
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
