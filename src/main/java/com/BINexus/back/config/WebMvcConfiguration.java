/*
package com.BINexus.back.config;


import com.BINexus.back.aop.JwtTokenUserInterceptor;
import com.BINexus.back.aop.OptionsInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

*/
/**
 * 配置类，注册web层相关组件
 *//*

@Configuration
@Slf4j
public class WebMvcConfiguration implements WebMvcConfigurer {


    @Autowired
    private JwtTokenUserInterceptor jwtTokenUserInterceptor;


    */
/**
     * 注册自定义拦截器
     *
     * @param
     *//*

    @Override
    public  void addInterceptors(InterceptorRegistry registry) {
        log.info("开始注册自定义拦截器...");
*/
/*//*
/        跨域拦截器
        registry.addInterceptor(new CrossInterceptor()).addPathPatterns("/**");
        log.info("CrossInterceptor拦截器注册成功！");*//*


        registry.addInterceptor(new OptionsInterceptor()).addPathPatterns("/**");
        log.info("Options请求拦截器注册成功！");

        registry.addInterceptor(jwtTokenUserInterceptor)
                .addPathPatterns("/asfas**")
                .excludePathPatterns("/**");
    }

}
*/
