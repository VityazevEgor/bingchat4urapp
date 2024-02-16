package com.bingchat4urapp_server.bingchat4urapp_server.Models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class TaskModel {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Integer Id;

    // 1 - auth Bing. 2 - send promt to bing
    public Integer Type;

    public String Data = null;

    public Boolean IsFinished = false;

    public Boolean GotError = false;

    public String Result = null;

}
