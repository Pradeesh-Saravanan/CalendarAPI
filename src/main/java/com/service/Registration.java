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
import org.mindrot.jbcrypt.*;

import com.auth.CORS;
import com.google.gson.*;
import com.model.User;
import com.opensymphony.xwork2.ActionSupport;
import com.utils.Database;	

public class Registration extends ActionSupport {
	private static final long serialVersionUID = -5926468460905343227L;
	private static String jsonString;
	private static InputStream responseData;
//	protected void doOptions(HttpServletRequest request, HttpServletResponse response) throws ServletException,IOException{
//		response.setHeader("Access-Control-Allow-Origin", "*");
//		response.setHeader("Access-Control-Allow-Methods","POST,OPTIONS");
//		response.setHeader("Access-Control-Allow-Headers", "Content-Type, access-control-allow-methods");
//		response.setStatus(HttpServletResponse.SC_OK);
//	}
    public String doPost() throws ServletException, IOException, ClassNotFoundException {
       	HttpServletRequest request = ServletActionContext.getRequest();
        HttpServletResponse response = ServletActionContext.getResponse();
//		response.setHeader("Access-Control-Allow-Origin","*");
//		response.setHeader("Access-Control-Allow-Headers", "Content-Type,Authorization,access-control-allow-methods");
//		response.setHeader("Access-Control-Allow-Methods", "POST,OPTIONS");
//		if("OPTIONS".equals(request.getMethod()))
//		{
//			response.setStatus(HttpServletResponse.SC_OK);
//			return null;
//		}
		
        response = CORS.resolve(response);
		if("OPTIONS".equals(request.getMethod())) {
			response.setStatus(HttpServletResponse.SC_OK);
			return NONE;
		}
		
		BufferedReader reader = new BufferedReader(new InputStreamReader(request.getInputStream()));
		StringBuilder sb = new StringBuilder();
		String line ;
		while((line=reader.readLine())!=null) {
			sb.append(line);
		}
		Gson gson = new Gson();
		User user = gson.fromJson(sb.toString(),User.class);
		Map<String,String> map = new HashMap<>();
		try(Connection conn = Database.getConnection()){
			String query = "select username from users where username = ?";
			try(PreparedStatement stmt = conn.prepareStatement(query)){
				stmt.setString(1, user.getUsername());
				ResultSet rs = stmt.executeQuery();
				if(rs.next()) {
					map.put("status", "failed");
					map.put("message", "Username already exists");
					jsonString = gson.toJson(map);
					responseData = new ByteArrayInputStream(jsonString.getBytes(StandardCharsets.UTF_8));
					return "json";
				}
				else {
					String hashed = BCrypt.hashpw(user.getPassword(), BCrypt.gensalt());
//					String idString = UUID.randomUUID().toString();
					query = "insert into users(username,password) values(?,?)";
					try(PreparedStatement insert =conn.prepareStatement(query)){
						insert.setString(1, user.getUsername());
						insert.setString(2, hashed);
//						insert.setString(3, idString);
						insert.executeUpdate();
						map.put("status", "success");
						map.put("message","User added to database");
						jsonString = gson.toJson(map);
    					responseData = new ByteArrayInputStream(jsonString.getBytes(StandardCharsets.UTF_8));
    					return "json";
    				}
					catch(SQLException e) {
						e.printStackTrace();
					}
				}
			}
		}
		catch(SQLException | ClassNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}
    public InputStream getResponseData() {
        return responseData;
    }
}
