package com.service;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.utils.JwtUtil;

public class JwtAuthenticationFilter implements Filter{
	private static String ORIGIN_STRING = "http://127.0.0.1:5501";
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
			HttpServletRequest httpRequest = (HttpServletRequest) request;
			HttpServletResponse httpResponse = (HttpServletResponse) response;
			
			if("OPTIONS".equals(httpRequest.getMethod())) {
				httpResponse.setHeader("Access-Control-Allow-Origin",ORIGIN_STRING);
				httpResponse.setHeader("Access-Control-Allow-Methods","GET,POST,PUT,DELETE,OPTIONS");
				httpResponse.setHeader("Access-Control-Allow-Headers","Content-Type, Authorization");
				httpResponse.setStatus(HttpServletResponse.SC_OK);
				return;
			}
			String authHeader = httpRequest.getHeader("Authorization");
			System.out.println("Authentication filter in active "+authHeader);
			if(authHeader!=null && authHeader.startsWith("Bearer ")) {
				String token = authHeader.substring(7);
				try {
					Algorithm algorithm = Algorithm.HMAC256("secretKey");
					JWT.require(algorithm).build().verify(token);
					chain.doFilter(request,response);
				}
				catch(Exception e) {
					e.printStackTrace();
					httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
				}
			}
			else {
				httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
				throw new ServletException("Invalid Authorization header");
			}
		
	}
}
