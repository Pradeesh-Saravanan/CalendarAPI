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

import com.auth.CORS;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.google.gson.Gson;
import com.model.User;
import com.opensymphony.xwork2.ActionSupport;
import com.utils.Database;

public class LoginJWT extends ActionSupport{
	private Map<String,String> map =new HashMap<>();
	private InputStream responseData = new ByteArrayInputStream("NULL".getBytes());	

	private static final long serialVersionUID = 7730398943265126114L;


	public String doPost() throws IOException {
		System.out.println("running login");
		HttpServletRequest request = ServletActionContext.getRequest();
		HttpServletResponse response= ServletActionContext.getResponse();
				
		response = CORS.resolve(response);

	    	BufferedReader reader = new BufferedReader(new InputStreamReader(request.getInputStream()));
	    	StringBuilder builder = new StringBuilder();
	    	String line = "";
	    	while((line=reader.readLine())!=null) {
	    		builder.append(line);
	    	}
	    	System.out.println(builder.toString());
	    	Gson gson = new Gson();
	    User user = gson.fromJson(builder.toString(),User.class);	
	    try (Connection connection = Database.getConnection()){
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
		
		return JWT.create().withSubject(username).withExpiresAt(new Date(System.currentTimeMillis()+3600*24*1000)).sign(algorithm);
	}

	public InputStream getResponseData() {
		return this.responseData;
	}
}
