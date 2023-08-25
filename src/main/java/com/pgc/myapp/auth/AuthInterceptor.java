package com.pgc.myapp.auth;

import com.pgc.myapp.auth.util.JwtUtil;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.lang.reflect.Method;

@Component
public class AuthInterceptor implements HandlerInterceptor {

    @Autowired
    JwtUtil jwtUtil;

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler)
            throws Exception {
        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            Method method = handlerMethod.getMethod();

            Auth auth = method.getAnnotation(Auth.class);

            if (auth == null) {
                return true;
            }

            String token = request.getHeader("Authorization");
            System.out.println(token);
            if(token == null || token.isEmpty()){
                response.setStatus(401);
                return false;
            }

            AuthProfile profile = jwtUtil.validateToken(token.replace("Bearer",""));
            System.out.println(jwtUtil.validateToken(token.replace("Bearer","")));
            if(profile == null){
                System.out.println("profile null");
                response.setStatus(401);
                return false;
            }

            request.setAttribute("authProfile",profile);
            return true;
        }
        return true;
    }
}

