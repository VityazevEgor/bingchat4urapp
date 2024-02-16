package com.bingchat4urapp_server.bingchat4urapp_server;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bingchat4urapp_server.bingchat4urapp_server.Models.TaskModel;

public interface Context extends JpaRepository<TaskModel, Integer> {
}
