package com.bingchat4urapp_server.bingchat4urapp_server.Models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class TaskModel {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Integer id;

    // 1 - auth Bing. 2 - send promt to bing
    public Integer type;

    // Json of taks parametrs
    public String data = null;

    public Boolean isFinished = false;

    public Boolean gotError = false;

    public String result = null;

    // name of image
    public String imageResult = null;

}
