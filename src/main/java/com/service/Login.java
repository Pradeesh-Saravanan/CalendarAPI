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
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.ServletActionContext;
import org.mindrot.jbcrypt.BCrypt;

import com.google.gson.Gson;
import com.model.Database;
import com.model.JwtUtil;
import com.model.User;
import com.opensymphony.xwork2.ActionSupport;

public class Login extends ActionSupport{
	private final String ORIGIN_STRING = "http://127.0.0.1:5500";
	private Map<String,String> map = new HashMap<>();
	private String jsonString = "";
	private InputStream inputStream;
	private String jwtToken;
	private static final long serialVersionUID = 5322981128270902702L;
	public String doPost() throws ServletException,IOException{
		HttpServletRequest request = ServletActionContext.getRequest();
		HttpServletResponse response = ServletActionContext.getResponse();
		
		if("OPTIONS".equals(request.getMethod())) {
	    	response.setHeader("Access-Control-Allow-Origin",ORIGIN_STRING);
	        response.setHeader("Access-Control-Allow-Credentials", "true");
	    	response.setHeader("Access-Control-Allow-Methods","GET,OPTIONS,POST");
	    	response.setHeader("Access-Control-Allow-Headers","Content-Type, Authorization");
	    	response.setStatus(HttpServletResponse.SC_OK);
	    	inputStream = new ByteArrayInputStream(jsonString.getBytes(StandardCharsets.UTF_8));
	    	return SUCCESS;
		}
    	response.setHeader("Access-Control-Allow-Credentials", "true");
    	response.setHeader("Access-Control-Allow-Origin",ORIGIN_STRING);
    	response.setHeader("Access-Control-Allow-Methods","GET,OPTIONS,POST");
    	response.setHeader("Access-Control-Allow-Headers","Content-Type, Authorization");
    	BufferedReader  stream = new BufferedReader(new InputStreamReader(request.getInputStream()));
    	StringBuilder sb = new StringBuilder();
    	String line = "";
    	while((line = stream.readLine())!=null) {
    		sb.append(line);
    	}
    	
    	Gson gson = new Gson();
    	User user = gson.fromJson(sb.toString(),User.class);
		try(Connection conn = Database.getConnection()){
			String query = "select * from users where username = ?";
			try(PreparedStatement stmt = conn.prepareStatement(query)){
				stmt.setString(1,user.getUsername());
				ResultSet rs = stmt.executeQuery();
				if(rs.next()) {
					System.out.println(user.getUsername());
					if(BCrypt.checkpw(user.getPassword(),rs.getString("password"))) {
						jwtToken = JwtUtil.createToken(user.getUsername());
						map.put("status","success");
						map.put("token",jwtToken);
						jsonString = gson.toJson(map);
    					inputStream = new ByteArrayInputStream(jsonString.getBytes(StandardCharsets.UTF_8));
						return SUCCESS;
					}
					else {
						System.out.println("Failed");
						response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
						map.put("status","failed");
						jsonString = gson.toJson(map);
    					inputStream = new ByteArrayInputStream(jsonString.getBytes(StandardCharsets.UTF_8));
						return ERROR;
					}
				}
				else {
					System.out.println("Failed");
					response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
					map.put("status","failed");
					jsonString = gson.toJson(map);
					inputStream = new ByteArrayInputStream(jsonString.getBytes(StandardCharsets.UTF_8));
					return ERROR;
				}
			}
		}
		catch(Exception e) {
			
		}
		return ERROR;
	}
	public InputStream getInputStream() {
		return inputStream;
	}
	public void setInputStream(InputStream inputStream) {
		this.inputStream = inputStream;
	}
//	public String getJwtToken() {
//		return jwtToken;
//	}
}
