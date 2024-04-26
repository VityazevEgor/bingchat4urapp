package com.bingchat4urapp_server.bingchat4urapp_server.Filters;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.bingchat4urapp_server.bingchat4urapp_server.Context;
import com.bingchat4urapp_server.bingchat4urapp_server.Controlers.Utils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class NoChatFilter extends OncePerRequestFilter {

    @Autowired
    Context _context;

    @Autowired
    Utils _utils;

    private final Logger logger = LoggerFactory.getLogger(NoChatFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException{
        var task = _context.findCreateChatTask();
        boolean pass = task!=null && task.isFinished && !task.gotError;
        if (pass){
            filterChain.doFilter(request, response);
        }
        else{
            logger.warn("No chat. Creating new one");
            var newCreateChatTask = _utils.createNewChatTask("3");
            _context.save(newCreateChatTask);
            response.sendRedirect("/task/" + newCreateChatTask.id);
        }
    }
    
}
