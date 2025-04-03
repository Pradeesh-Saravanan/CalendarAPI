//package com.utils;
//
//import java.nio.charset.StandardCharsets;
//import java.util.Date;
//
//import javax.crypto.SecretKey;
//
//import io.jsonwebtoken.*;
//import io.jsonwebtoken.security.Keys;
//
//public class JwtUtil {
//	private static final String key = "11a6b207-b5e9-4650-9c79-d4cd666548af";
//	private static final long EXPIRATION_TIME = 3600000;
//	
//	public static String createToken(String username) {
//		SecretKey SecretKey = Keys.hmacShaKeyFor(key.getBytes(StandardCharsets.UTF_8));
//		System.out.println("key created");
//		String result = Jwts.builder().subject(username).expiration(new Date(System.currentTimeMillis()+EXPIRATION_TIME)).signWith(SecretKey).compact();
//		System.out.println("Token created");
//		return result;
//	}
//	
//	public static Claims parseToken(String token) {
//		SecretKey SecretKey = Keys.hmacShaKeyFor(key.getBytes(StandardCharsets.UTF_8));
//		return Jwts.parser().verifyWith(SecretKey).build().parseSignedClaims(token).getPayload();
//	}
//	
//	public static boolean validateToken(String token) {
//		try {
//			parseToken(token);
//			return true;
//		}
//		catch(JwtException | IllegalArgumentException e) {
//			return false;
//		}
//	}
//}
