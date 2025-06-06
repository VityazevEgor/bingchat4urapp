package com.bingchat4urapp_server.bingchat4urapp_server;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.bingchat4urapp_server.bingchat4urapp_server.Filters.AuthFilter;

@Configuration
public class FiltersCfg {
    @Bean
    @Autowired
    public FilterRegistrationBean<AuthFilter> registerAuthFilter(AuthFilter filter) {
        FilterRegistrationBean<AuthFilter> registrationBean = new FilterRegistrationBean<AuthFilter>();
        registrationBean.setFilter(filter);
        registrationBean.addUrlPatterns("/");
        registrationBean.setOrder(0);
        return registrationBean;
    }
}