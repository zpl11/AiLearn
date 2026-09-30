package org.example.componeyoa.common;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class JwtInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 放行浏览器的跨域预检请求 (OPTIONS)
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        // 1. 提取 Authorization 头部
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401
            response.setContentType("application/json;charset=utf-8");
            response.getWriter().write("{\"code\":401,\"message\":\"未提供有效的认证凭据，请重新登录\"}");
            return false;
        }

        // 2. 剥离 "Bearer " 前缀拿到真实的 Token
        String token = authHeader.substring(7);

        try {
            // 3. 验签与过期时间核验
            Long userId = JwtUtils.getUserId(token);
            String userName = JwtUtils.getUserName(token);

            // 4. 存入当前线程上下文
            UserContext.setUserId(userId);
            UserContext.setUserName(userName);
            return true;
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=utf-8");
            response.getWriter().write("{\"code\":401,\"message\":\"登录凭证已失效或被篡改，请重新登录\"}");
            return false;
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        // 请求完毕，必定清理 ThreadLocal
        UserContext.clear();
    }
}
