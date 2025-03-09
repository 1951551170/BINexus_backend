package com.BINexus.back.aop;


import com.BINexus.back.common.BaseContext;
import com.BINexus.back.common.ErrorCode;
import com.BINexus.back.exception.BusinessException;
import com.BINexus.back.utils.AppJwtUtil;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * jwt令牌校验的拦截器
 */
@Component
@Slf4j
public class JwtTokenUserInterceptor implements HandlerInterceptor {



    /**
     * 校验jwt
     *
     * @param request
     * @param response
     * @param handler
     * @return
     * @throws Exception
     */
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {

        log.info("进入拦截器");
        //1、从请求头中获取令牌
        String token = request.getHeader("token");
        log.info("获取到token{}",token);
        //3.判断token是否存在
        if(StringUtils.isBlank(token)){
            response.setStatus(401);
            return false;
        }

        //4.判断token是否有效
        try {
            Claims claimsBody = AppJwtUtil.getClaimsBody(token);
            //检验是否过期
            int result = AppJwtUtil.verifyToken(claimsBody);
            if(result == 1 || result == 2){
                throw new BusinessException(ErrorCode.TOKEN_EXPIRE);
            }

            //解析token获取用户信息
            Object userId = claimsBody.get("userId");
            BaseContext.setCurrentId(Long.valueOf(String.valueOf(userId)));
            //3、通过，放行
            return true;
        }  catch (Exception e){
            response.setStatus(401);
            return false;
        }

    }
}
