package com.pedro.spring.interceptors;

import com.pedro.spring.service.CookieService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class RequestInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        try {
            if (CookieService.getCookie(request, "userAuthenticated") != null) {
                return true;
            }
        } catch (Exception e) {
            response.sendRedirect("/");
            return false;
        }
        return false;
    }
}
