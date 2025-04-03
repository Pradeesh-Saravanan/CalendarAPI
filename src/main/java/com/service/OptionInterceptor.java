package com.service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.ServletActionContext;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.Interceptor;

public class OptionInterceptor implements Interceptor{

	private static final long serialVersionUID = -7106508288074154220L;
	private static String ORIGIN_STRING = "http://127.0.0.1:5501";
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
		System.out.println("Options interceptor is running...."+request.getMethod());
		if("OPTIONS".equals(request.getMethod())) {
			response.setHeader("Access-Control-Allow-Origin",ORIGIN_STRING);
			response.setHeader("Access-Control-Allow-Methods","GET,POST,PUT,DELETE,OPTIONS");
			response.setHeader("Access-Control-Allow-Headers","Content-Type, Authorization");
			response.setStatus(HttpServletResponse.SC_OK);
			return null;
		}
		return arg0.invoke();
	}

}
