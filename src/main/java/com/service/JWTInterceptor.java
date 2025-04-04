package com.service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts2.ServletActionContext;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.Interceptor;

public class JWTInterceptor implements Interceptor{
	private static String ORIGIN_STRING = "http://127.0.0.1:5501";
	private static final long serialVersionUID = -3593487341246306868L;

	
	@Override
	public void destroy() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void init() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String intercept(ActionInvocation arg0) throws Exception {
		HttpServletRequest request = ServletActionContext.getRequest();
		HttpServletResponse response = ServletActionContext.getResponse();
		if("OPTIONS".equals(request.getMethod())) {
			response.setHeader("Access-Control-Allow-Origin",ORIGIN_STRING);
			response.setHeader("Access-Control-Allow-Methods","GET,POST,PUT,DELETE,OPTIONS");
			response.setHeader("Access-Control-Allow-Headers","Content-Type, Authorization");
			response.setStatus(HttpServletResponse.SC_OK);
			return null;
		}
		System.out.println("Interceptor is active");
		response.setHeader("Access-Control-Allow-Origin",ORIGIN_STRING);
		response.setHeader("Access-Control-Allow-Methods","GET,POST,PUT,DELETE,OPTIONS");
		response.setHeader("Access-Control-Allow-Headers","Content-Type, Authorization");
		
		String authHeader = request.getHeader("Authorization");
		if(authHeader!=null && authHeader.startsWith("Bearer ")) {
			String token = authHeader.substring(7);
			try {
				Algorithm algorithm = Algorithm.HMAC256("secretKey");
				DecodedJWT decoded  = JWT.require(algorithm).build().verify(token);
				HttpSession session = request.getSession();
				session.setAttribute("username",decoded.getSubject());
				System.out.println("interceptor verification complete");
				return arg0.invoke();
			}
			catch(Exception e)
			{
				e.printStackTrace();
				response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
				return null;
			}
		}
		return null;
	}

}
