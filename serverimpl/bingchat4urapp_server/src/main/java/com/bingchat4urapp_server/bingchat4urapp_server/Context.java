package com.bingchat4urapp_server.bingchat4urapp_server;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.bingchat4urapp_server.bingchat4urapp_server.Models.TaskModel;

public interface Context extends JpaRepository<TaskModel, Integer> {
    @Query("SELECT t FROM TaskModel t WHERE t.isFinished = false ORDER BY t.id ASC LIMIT 1")
    TaskModel findFirstUnfinishedTask();

    // get last TaskModel where field 'type' == 1
    @Query("SELECT t FROM TaskModel t WHERE t.type = 1 ORDER BY t.id DESC LIMIT 1")
    TaskModel findLastAuthTask();
}
