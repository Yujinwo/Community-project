package com.example.community.util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
public class CookieUtill {

   public static void addCookie(HttpServletResponse response,String name, String value,int maxAge){
       // 새로운 쿠키 객체 생성
       Cookie cookie = new Cookie(name, value);
       // 쿠키의 만료 시간 설정
       cookie.setMaxAge(maxAge);
       // 쿠키의 경로 설정
       cookie.setPath("/");
       // HttpServletResponse에 쿠키 추가
       response.addCookie(cookie);
   }
    public static void removeCookie(HttpServletResponse response,String name){
        // 새로운 쿠키 객체 생성
        Cookie cookie = new Cookie(name, null);
        // 쿠키의 만료 시간 설정
        cookie.setMaxAge(0);
        // 쿠키의 경로 설정
        cookie.setPath("/");
        // HttpServletResponse에 쿠키 추가
        response.addCookie(cookie);
    }
   public static String getCookieValue(HttpServletRequest request,String name){
       // HttpServletRequest에서 모든 쿠키를 가져옵니다.
       Cookie[] cookies = request.getCookies();
       // 쿠키 배열이 비어있지 않은 경우
       if(cookies != null){
           // 모든 쿠키를 반복하여 지정된 이름의 쿠키를 찾습니다.
           for (Cookie cookie: cookies){
               // 현재 쿠키의 이름이 지정된 이름과 일치하는 경우
               if(cookie.getName().equals(name)){
                   // 해당 쿠키의 값을 반환합니다.
                   return cookie.getValue();
               }
           }
       }
       // 지정된 이름의 쿠키를 찾지 못한 경우 null을 반환합니다.
       return null;
   }
}
