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
		try {
			mapping.setName(getMethodName(uri));
			String method = request.getMethod().toUpperCase();
			switch(method) {
				case "OPTIONS":
					response.setHeader("Access-Control-Allow-Origin",ORIGIN_STRING);
					response.setHeader("Access-Control-Allow-Methods","GET,POST,PUT,DELETE,OPTIONS");
					response.setHeader("Access-Control-Allow-Headers","Content-Type, Authorization");
					response.setStatus(HttpServletResponse.SC_OK);
					return null;
				case "POST":
					mapping.setMethod("doPost");
					break;
				case "GET":
					mapping.setMethod("doGet");
					break;
				case "PUT":
					mapping.setMethod("doPut");
					break;
				case "DELETE":
					mapping.setMethod("doDelete");
					break;
				default:
					mapping.setMethod("doGet");
					break;
			}
			System.out.println(request.getMethod());
			return mapping;
		}
		catch(Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public String getMethodName(String uri) {
		return uri.split("CalendarAPI/")[1];
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
