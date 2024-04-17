package com.example.community.util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
public class CookieUtill {

   public static void addCookie(HttpServletResponse response,String name, String value,int maxAge){
       Cookie cookie = new Cookie(name, value);
       cookie.setMaxAge(maxAge);
       cookie.setPath("/");
       response.addCookie(cookie);
   }
   public static String getCookieValue(HttpServletRequest request,String name){
       Cookie[] cookies = request.getCookies();
       if(cookies != null){
           for (Cookie cookie: cookies){
               if(cookie.getName().equals(name)){
                   return cookie.getValue();

               }

           }

       }

       return null;
   }
}
