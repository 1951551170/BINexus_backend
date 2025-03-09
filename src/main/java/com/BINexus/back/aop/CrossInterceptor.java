package com.BINexus.back.aop;

/*
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

*/
/*
 * 跨域请求拦截器
 *//*

@Log4j2
@Configuration
public class CrossInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String origin  = request.getHeader(HttpHeaders.ORIGIN);
        if (origin != null) {
            response.setHeader("Access-Control-Allow-Origin", "http://localhost:8081");
            response.setHeader("Access-Control-Allow-Credentials", "true");
            response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE, PUT, HEAD");
            response.setHeader("Access-Control-Allow-Headers", "Content-Type, Token");
            response.setHeader("Access-Control-Max-Age", "3600");
        }
        return true;
    }
}
*/
