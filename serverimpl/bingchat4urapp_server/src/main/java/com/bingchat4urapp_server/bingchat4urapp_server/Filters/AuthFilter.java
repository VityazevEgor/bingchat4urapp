package com.bingchat4urapp_server.bingchat4urapp_server.Filters;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import com.bingchat4urapp_server.bingchat4urapp_server.Context;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class AuthFilter extends OncePerRequestFilter {

    @Autowired
    private Context _context;

    private final Logger logger = LoggerFactory.getLogger(AuthFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)throws ServletException, IOException {
        var lastAuthTask = _context.findLastAuthTask();
        boolean pass = lastAuthTask != null && !lastAuthTask.gotError && lastAuthTask.isFinished;

        if (pass){
            filterChain.doFilter(request, response);
            logger.info("Passed request");
        }
        else{
            logger.info("Request filtered cuz there is no auth task");
            response.sendRedirect("/authgui");
        }
    }
    
}
