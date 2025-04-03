package com.utils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.ServletActionContext;
import org.apache.struts2.dispatcher.mapper.ActionMapper;
import org.apache.struts2.dispatcher.mapper.ActionMapping;

import com.opensymphony.xwork2.config.ConfigurationManager;

public class CustomMapper implements ActionMapper {
	
	private static String ORIGIN_STRING = "http://127.0.0.1:5501";

	@Override
	public ActionMapping getMapping(HttpServletRequest request, ConfigurationManager configManager) {
		String uri = request.getRequestURI();
		ActionMapping mapping = new ActionMapping();
		HttpServletResponse response = ServletActionContext.getResponse();
//		System.out.println(uri.equals("/CalendarAPI/login") & request.getMethod()=="POST");
//		System.out.println(uri.equals("/CalendarAPI/login"));
//		System.out.println(request.getMethod().equals("POST"));
		try {
			if(uri.equals("/CalendarAPI/login") && request.getMethod().equals("OPTIONS")) {
				response.setHeader("Access-Control-Allow-Origin",ORIGIN_STRING);
				response.setHeader("Access-Control-Allow-Methods","GET,POST,PUT,DELETE,OPTIONS");
				response.setHeader("Access-Control-Allow-Headers","Content-Type, Authorization");
				response.setStatus(HttpServletResponse.SC_OK);
				return null;
			}
			else if(uri.equals("/CalendarAPI/login") && request.getMethod().equals("POST")) {
				System.out.println(uri);
				System.out.println(mapping.getName()+" "+mapping.getMethod());
				mapping.setName("login");
				mapping.setMethod("post_login");
			}
			System.out.println(request.getMethod());
			return mapping;
		}
		catch(Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public ActionMapping getMappingFromActionName(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getUriFromActionMapping(ActionMapping arg0) {
		// TODO Auto-generated method stub
		return null;
	}

}
