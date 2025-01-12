package com.bingchat4urapp_server.bingchat4urapp_server.Models;

import java.util.HashMap;
import java.util.Map;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapKeyColumn;

@Entity
public class TaskModel {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Integer id;

    // 1 - auth Bing. 2 - send promt to bing
    public Integer type;

    // Json of taks parametrs
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "task_data", joinColumns = @JoinColumn(name = "task_id"))
    @MapKeyColumn(name = "data_key")
    @Column(name = "data_value")
    public Map<String, String> data = new HashMap<>();

    public Boolean isFinished = false;

    public Boolean gotError = false;

    public String result = null;
    public String htmlResult = null;

    // name of image
    public String imageResult = null;

}
