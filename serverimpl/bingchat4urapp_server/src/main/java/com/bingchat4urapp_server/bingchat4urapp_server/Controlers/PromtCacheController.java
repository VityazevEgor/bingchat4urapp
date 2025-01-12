package com.bingchat4urapp_server.bingchat4urapp_server.Controlers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bingchat4urapp_server.bingchat4urapp_server.Models.PromtCache;
import com.bingchat4urapp_server.bingchat4urapp_server.Models.PromtCacheRepo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping(path = "/api/promtChache/")
public class PromtCacheController {
    @Autowired
    private PromtCacheRepo promtCacheRepo;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    private static class SaveRequest {
        public String promt;
    }

    @PostMapping("update")
    public Boolean updatePromtChache(@RequestBody SaveRequest promt) {
        if (promtCacheRepo.count() == 0) {
            promtCacheRepo.save(new PromtCache(promt.getPromt()));
            return true;
        }

        var promtCache = promtCacheRepo.findAll().get(0);
        promtCache.setPromt(promt.getPromt());
        promtCacheRepo.save(promtCache);
        return true;
    }    
}
