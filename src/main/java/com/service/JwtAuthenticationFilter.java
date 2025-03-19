package com.service;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.model.JwtUtil;

public class JwtAuthenticationFilter implements Filter{

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		// TODO Auto-generated method stub
		System.out.println("Jwt authentication");
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		String authHeader = httpRequest.getHeader("Authorization");
		if(authHeader!=null && authHeader.startsWith("Bearer ")) {
			String token = authHeader.substring(7);
			if(JwtUtil.validateToken(token)) {
				String username = JwtUtil.parseToken(token).getSubject();
				httpRequest.setAttribute("username",username);
			}
			else{
				((HttpServletResponse) response).sendError(HttpServletResponse.SC_UNAUTHORIZED,"Invalid token");
				return;
			}
		}
		else {
			((HttpServletResponse) response).sendError(HttpServletResponse.SC_UNAUTHORIZED,"Authorization Header Missing");
			return;
		}
		chain.doFilter(request,response);
	}
}
