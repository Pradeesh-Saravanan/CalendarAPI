package com.service;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.ServletActionContext;
import org.mindrot.jbcrypt.BCrypt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.google.gson.Gson;
import com.model.Database;
import com.model.User;
import com.opensymphony.xwork2.ActionSupport;

public class LoginJWT extends ActionSupport{
	private final String ORIGIN_STRING = "http://127.0.0.1:5501";
	private Map<String,String> map =new HashMap<>();
	private InputStream responseData;
	private Connection connection;

	private static final long serialVersionUID = 7730398943265126114L;
	public void doOptions(HttpServletRequest request,HttpServletResponse response) {
		response.setHeader("Access-Control-Allow-Origin",ORIGIN_STRING);
        response.setHeader("Access-Control-Allow-Credentials", "true");
	    	response.setHeader("Access-Control-Allow-Methods","GET,OPTIONS,POST");
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
	public String doGet() throws IOException {
		HttpServletRequest request = ServletActionContext.getRequest();
		HttpServletResponse response= ServletActionContext.getResponse();
		
		if("OPTIONS".equals(request.getMethod())) {
			doOptions(request,response);
			return NONE;
		}
		response.setHeader("Access-Control-Allow-Origin",ORIGIN_STRING);
        response.setHeader("Access-Control-Allow-Credentials", "true");
	    	response.setHeader("Access-Control-Allow-Methods","GET,OPTIONS,POST");
	    	response.setHeader("Access-Control-Allow-Headers","Content-Type, Authorization");
	    	
	    	Gson gson = new Gson();
	    	
	    	String authHeader = request.getHeader("Authorization");
		System.out.println("Authentication get method is active "+authHeader);
		if(authHeader!=null && authHeader.startsWith("Bearer ")) {
			String token = authHeader.substring(7);
			try {
				Algorithm algorithm = Algorithm.HMAC256("secretKey");
				DecodedJWT decodedJWT = JWT.require(algorithm).build().verify(token);
		        
		        String user = decodedJWT.getSubject();
			    	
			    	map.put("status","success");
			    	map.put("message","Lazy login success");
			    	map.put("user",user);
			    	responseData = new ByteArrayInputStream(gson.toJson(map).getBytes());
			    	return SUCCESS;
			}
			catch(Exception e) {
				e.printStackTrace();
				response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
				map.put("status","error");
			    	map.put("message","Unable to verify token");
			    	responseData = new ByteArrayInputStream(gson.toJson(map).getBytes());
			    	return SUCCESS;
			}
		}
		else {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			map.put("status","error");
		    	map.put("message","No token found");
		    	responseData = new ByteArrayInputStream(gson.toJson(map).getBytes());
		    	return SUCCESS;
		}
	}
	public String doPost() throws IOException {
		HttpServletRequest request = ServletActionContext.getRequest();
		HttpServletResponse response= ServletActionContext.getResponse();
		
		if("OPTIONS".equals(request.getMethod())) {
			doOptions(request,response);
			return NONE;
		}
		
		response.setHeader("Access-Control-Allow-Origin",ORIGIN_STRING);
        response.setHeader("Access-Control-Allow-Credentials", "true");
	    	response.setHeader("Access-Control-Allow-Methods","GET,OPTIONS,POST");
	    	response.setHeader("Access-Control-Allow-Headers","Content-Type, Authorization");
	    	
	    	BufferedReader reader = new BufferedReader(new InputStreamReader(request.getInputStream()));
	    	StringBuilder builder = new StringBuilder();
	    	String line = "";
	    	while((line=reader.readLine())!=null) {
	    		builder.append(line);
	    	}
	    	Gson gson = new Gson();
	    User user = gson.fromJson(builder.toString(),User.class);	
	    try {
	    		String query ="select * from users where username = ?";
	    		PreparedStatement stmt = connection.prepareStatement(query);
	    		stmt.setString(1,user.getUsername());
	    		ResultSet rs = stmt.executeQuery();
	    		if(rs.next()) {
	    			if(BCrypt.checkpw(user.getPassword(),rs.getString("password"))) {
	    				String token = generateToken(user.getUsername());
	    				map.put("token",token);
	    				map.put("message","Login successful");
	    				map.put("status","success");
	    				responseData = new ByteArrayInputStream(gson.toJson(map).getBytes());
	    				return SUCCESS;
	    			}
	    			else {
	    				response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
	    				map.put("message","Wrong password");
	    				map.put("status","error");
	    				responseData = new ByteArrayInputStream(gson.toJson(map).getBytes());
	    				return ERROR;
	    			}
	    		}
	    		else {
	    			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    				map.put("message","Wrong username");
    				map.put("status","error");
    				responseData = new ByteArrayInputStream(gson.toJson(map).getBytes());
    				return ERROR;
	    		}
	    }
	    catch(Exception e) {
	    		map.put("message",e.toString());
	    		map.put("status","error");
	    		responseData = new ByteArrayInputStream(gson.toJson(map).getBytes());
	    		return ERROR;
	    }
	}
	
	public String generateToken(String username) {
		
		Algorithm algorithm = Algorithm.HMAC256("secretKey");
		
		return JWT.create().withSubject(username).withExpiresAt(new Date(System.currentTimeMillis()+3600*1000)).sign(algorithm);
	}

	public InputStream getResponseData() {
		return responseData;
	}
}
