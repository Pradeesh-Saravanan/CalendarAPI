package com.auth;

import javax.servlet.http.HttpServletResponse;

public class CORS {
		public static HttpServletResponse resolve(HttpServletResponse response) {
			response.setHeader("Access-Control-Allow-Origin","http://127.0.0.1:5501");
			response.setHeader("Access-Control-Allow-Methods","GET,POST,DELETE,PUT,OPTIONS");
			response.setHeader("Access-Control-Allow-Headers","Content-Type,Authorization");
			return response;
		}
}
