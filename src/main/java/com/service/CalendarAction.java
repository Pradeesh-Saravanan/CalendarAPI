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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.ServletActionContext;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.google.gson.Gson;
import com.opensymphony.xwork2.ActionSupport;
import com.utils.Calendar;
import com.utils.Database;

public class CalendarAction extends ActionSupport {

	private static final long serialVersionUID = 2371873185954435378L;
	private final String ORIGIN_STRING = "http://127.0.0.1:5501";
	private Map<String,String> map = new HashMap<>();
	private String user_id = "";
	private String user;
	private InputStream inputStream;
	public void doOptions(HttpServletRequest request,HttpServletResponse response) {
		response.setHeader("Access-Control-Allow-Origin",ORIGIN_STRING);
		response.setHeader("Access-Control-Allow-Methods","GET,POST,DELETE,PUT,OPTIONS");
		response.setHeader("Access-Control-Allow-Credentials","true");
		response.setHeader("Access-Control-Allow-Headers","Content-Type,Authorization");
		response.setStatus(HttpServletResponse.SC_OK);
	}
	
	public String doDelete() throws ServletException,IOException,ClassNotFoundException{
		HttpServletRequest request = ServletActionContext.getRequest();
		HttpServletResponse response = ServletActionContext.getResponse();
		
		if("OPTIONS".equals(request.getMethod())) {
			doOptions(request,response);
			return NONE;
		}
		
		response.setHeader("Access-Control-Allow-Origin",ORIGIN_STRING);
		response.setHeader("Access-Control-Allow-Methods","GET,POST,DELETE,PUT,OPTIONS");
		response.setHeader("Access-Control-Allow-Credentials","true");
		response.setHeader("Access-Control-Allow-Header","Content-Type,Authorization");
		String authHeader = request.getHeader("Authorization");
		if(authHeader!=null && authHeader.startsWith("Bearer ")) {
			String token = authHeader.substring(7);
			Algorithm algorithm = Algorithm.HMAC256("secretKey");
			DecodedJWT decoded = JWT.require(algorithm).build().verify(token);
			setUser(decoded.getSubject());
		}
		else {
			map.put("status","failed");
			map.put("message","Auth token error");
			Gson gson = new Gson();
			inputStream = new ByteArrayInputStream(gson.toJson(map).getBytes(StandardCharsets.UTF_8));
			return ERROR;
		}
		
		try(Connection conn = Database.getConnection()){
			String user_query = "select user_id from users where username = ?";
			PreparedStatement stmt = conn.prepareStatement(user_query);
			stmt.setString(1,getUser());
			ResultSet rs = stmt.executeQuery();
			if(rs.next()) {
				user_id = rs.getString("user_id");
				String query = "delete from calendars where calendar_id = ?";
				stmt = conn.prepareStatement(query);
				stmt.setString(1,request.getParameter("calendar_id"));
				int rows = stmt.executeUpdate();
				if(rows<1) {
					map.put("status","failed");
					map.put("message","No records found");					
					Gson gson = new Gson();
					inputStream = new ByteArrayInputStream(gson.toJson(map).getBytes(StandardCharsets.UTF_8));
					return ERROR;
				}
				map.put("status","success");
				map.put("message","Calendar deleted successfully");
				Gson gson = new Gson();
				inputStream = new ByteArrayInputStream(gson.toJson(map).getBytes(StandardCharsets.UTF_8));
				return SUCCESS;
				
			}
		}
		catch(Exception e) {
			map.put("status","failed");
			map.put("message",e.getMessage());
			
			Gson gson = new Gson();
			inputStream = new ByteArrayInputStream(gson.toJson(map).getBytes(StandardCharsets.UTF_8));
			return ERROR;
		}
		
		return ERROR;
	}
	
	public String doPut() throws ServletException, ClassNotFoundException, IOException{
		HttpServletRequest request = ServletActionContext.getRequest();
		HttpServletResponse response = ServletActionContext.getResponse();
		
		if("OPTIONS".equals(request.getMethod()))
		{
			doOptions(request,response);
			return NONE;
		}
		response.setHeader("Access-Control-Allow-Origin",ORIGIN_STRING);
		response.setHeader("Access-Control-Allow-Methods","GET,POST,DELETE,PUT,OPTIONS");
		response.setHeader("Access-Control-Allow-Credentials","true");
		response.setHeader("Access-Control-Allow-Headers","Content-Type,Authorization");
		
		String authHeader = request.getHeader("Authorization");
		if(authHeader!=null && authHeader.startsWith("Bearer ")) {
			String token = authHeader.substring(7);
			Algorithm algorithm = Algorithm.HMAC256("secretKey");
			DecodedJWT decoded = JWT.require(algorithm).build().verify(token);
			setUser(decoded.getSubject());
		}
		else {
			map.put("status","failed");
			map.put("message","Auth token error");
			Gson gson = new Gson();
			inputStream = new ByteArrayInputStream(gson.toJson(map).getBytes(StandardCharsets.UTF_8));
			return ERROR;
		}
		BufferedReader reader = new BufferedReader(new InputStreamReader(request.getInputStream()));
		StringBuilder sb = new StringBuilder();
		String line = "";
		while((line=reader.readLine())!=null) {
			sb.append(line);
		}
		Gson gson = new Gson();
		Calendar calendar = gson.fromJson(sb.toString(),Calendar.class);
		
		try(Connection conn = Database.getConnection()){
			String user_query = "select user_id from users where username = ?";
			PreparedStatement stmt = conn.prepareStatement(user_query);
			stmt.setString(1,getUser());
			System.out.println(getUser());
			ResultSet rs = stmt.executeQuery();
			if(rs.next()) {
				user_id = rs.getString("user_id");
			
				
				String query = "update calendars set description = ? where calendar_id = ? and user_id = ?";
				stmt = conn.prepareStatement(query);
				stmt.setString(1,calendar.getDescription());
				stmt.setString(2,calendar.getCalendar_id());
				stmt.setString(3,user_id);
				int rows = stmt.executeUpdate();
				if(rows<1) {
					map.put("status","failed");
					map.put("message","No records found");					
					inputStream = new ByteArrayInputStream(gson.toJson(map).getBytes(StandardCharsets.UTF_8));
					return ERROR;
				}
				map.put("status","success");
				map.put("message","Calendar updated successfully");
				inputStream = new ByteArrayInputStream(gson.toJson(map).getBytes(StandardCharsets.UTF_8));
				return SUCCESS;
			}
			
		}
		catch(Exception e) {
			map.put("status","failed");
			map.put("message",e.getMessage());
			
			inputStream = new ByteArrayInputStream(gson.toJson(map).getBytes(StandardCharsets.UTF_8));
			return ERROR;
		}
		return ERROR;
	}
	
	public String doGet() throws ServletException, ClassNotFoundException, IOException{
		HttpServletRequest request = ServletActionContext.getRequest();
		HttpServletResponse response = ServletActionContext.getResponse();
		
		if("OPTIONS".equals(request.getMethod())) {
			doOptions(request,response);
			return NONE;
		}
		
		response.setHeader("Access-Control-Allow-Origin",ORIGIN_STRING);
		response.setHeader("Access-Control-Allow-Methods","GET,POST,DELETE,PUT,OPTIONS");
		response.setHeader("Access-Control-Allow-Headers","Content-Type, Authorization");
		response.setHeader("Access-Control-Allow-Credentials","true");
		System.out.println("Calendar API is running....");
		
		String authHeader = request.getHeader("Authorization");
		if(authHeader!=null && authHeader.startsWith("Bearer ")) {
			String token = authHeader.substring(7);
			Algorithm algorithm = Algorithm.HMAC256("secretKey");
			DecodedJWT decoded = JWT.require(algorithm).build().verify(token);
			setUser(decoded.getSubject());
		}
		else {
			map.put("status","failed");
			map.put("message","Auth token error");
			Gson gson = new Gson();
			inputStream = new ByteArrayInputStream(gson.toJson(map).getBytes(StandardCharsets.UTF_8));
			return ERROR;
		}
		
		try(Connection conn = Database.getConnection()){
			String user_query = "select user_id from users where username = ?";
			PreparedStatement stmt = conn.prepareStatement(user_query);
			stmt.setString(1,getUser());
			ResultSet rs = stmt.executeQuery();
			if(rs.next()) {
				String user_id = rs.getString("user_id");
				String query = "select * from calendars where user_id = ?";
				stmt = conn.prepareStatement(query);
				stmt.setString(1,user_id);
				rs = stmt.executeQuery();
				List<Calendar> calendars = new ArrayList<>();
				if(rs==null) {
						map.put("status","failed");
						map.put("message","No records found");					
						Gson gson = new Gson();
						inputStream = new ByteArrayInputStream(gson.toJson(map).getBytes(StandardCharsets.UTF_8));
						return ERROR;
				}
				while(rs.next()) {
					String name = rs.getString("calendar_name");
					String desc = rs.getString("description");
					String id = rs.getString("calendar_id");
					calendars.add(new Calendar(name,desc,id));
				}
				Gson gson = new Gson();
				inputStream = new ByteArrayInputStream(gson.toJson(calendars).getBytes(StandardCharsets.UTF_8));
				return SUCCESS;
			}
			
		}
		catch(Exception e) {
			map.put("status","failed");
			map.put("message",e.getMessage());
			
			Gson gson = new Gson();
			inputStream = new ByteArrayInputStream(gson.toJson(map).getBytes(StandardCharsets.UTF_8));
			return ERROR;
		}
		return SUCCESS;
	}
	
	public String doPost() throws ServletException,ClassNotFoundException, IOException{
		
		HttpServletRequest request = ServletActionContext.getRequest();
		HttpServletResponse response = ServletActionContext.getResponse();
		
		if("OPTIONS".equals(request.getMethod())) {
			doOptions(request,response);
			setInputStream(new ByteArrayInputStream("".getBytes(StandardCharsets.UTF_8)));
			return NONE;
		}
		response.setHeader("Access-Control-Allow-Origin",ORIGIN_STRING);
		response.setHeader("Access-Control-Allow-Credentials","true");
		response.setHeader("Access-Control-Allow-Headers","Content-Type, Authorization");
		response.setHeader("Access-Control-Allow-Methods","GET,POST,PUT,DELETE,OPTIONS");
		System.out.println("Calendar API running.....");
		
		String authHeader = request.getHeader("Authorization");
		if(authHeader!=null && authHeader.startsWith("Bearer ")) {
			String token = authHeader.substring(7);
			Algorithm algorithm = Algorithm.HMAC256("secretKey");
			DecodedJWT decoded = JWT.require(algorithm).build().verify(token);
			setUser(decoded.getSubject());
		}
		else {
			map.put("status","failed");
			map.put("message","Auth token error");
			Gson gson = new Gson();
			inputStream = new ByteArrayInputStream(gson.toJson(map).getBytes(StandardCharsets.UTF_8));
			return ERROR;
		}
		BufferedReader reader = new BufferedReader(new InputStreamReader(request.getInputStream()));
		StringBuilder sb = new StringBuilder();
		String line = "";
		
		while((line=reader.readLine())!=null) {
			sb.append(line);
		}
		
		Gson gson = new Gson();
		Calendar calendar = gson.fromJson(sb.toString(),Calendar.class);
		
		try(Connection connection = Database.getConnection()){
			
			String user_query = "select * from users where username = ?";
			PreparedStatement stmt = connection.prepareStatement(user_query);
			stmt.setString(1,getUser());
			
			ResultSet rs = stmt.executeQuery();
			
			if(rs.next()) {
				
				user_id = rs.getString("user_id");
				System.out.println(user_id);
				String query = "insert into calendars(user_id,calendar_name,description,created_at) values(?,?,?,now());";
				stmt = connection.prepareStatement(query);
				stmt.setString(1,user_id);
				stmt.setString(2,calendar.getCalendar_name());
				stmt.setString(3,calendar.getDescription());
				stmt.executeUpdate();
				map.put("status","success");
				map.put("message","Calendar added");
				inputStream = new ByteArrayInputStream(gson.toJson(map).getBytes(StandardCharsets.UTF_8));
				
				return SUCCESS;
			}
		}
		catch(Exception e) {
			map.put("status","failed");
			map.put("message",e.getMessage());
			
			inputStream = new ByteArrayInputStream(gson.toJson(map).getBytes(StandardCharsets.UTF_8));
			return ERROR;
		}
		return SUCCESS;
	}
	
	public InputStream getInputStream() {
		return inputStream;
	}
	public void setInputStream(InputStream inputStream) {
		this.inputStream = inputStream;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}
	
}
