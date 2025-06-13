package com.bingchat4urapp_server.bingchat4urapp_server.Controlers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bingchat4urapp_server.bingchat4urapp_server.Models.PromptCache;
import com.bingchat4urapp_server.bingchat4urapp_server.Models.PromptCacheRepo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping(path = "/api/promptCache/")
public class PromptCacheController {
    @Autowired
    private PromptCacheRepo promptCacheRepo;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    private static class SaveRequest {
        public String prompt;
    }

    @PostMapping("update")
    public Boolean updatePromptCache(@RequestBody SaveRequest prompt) {
        if (promptCacheRepo.count() == 0) {
            promptCacheRepo.save(new PromptCache(prompt.getPrompt()));
            return true;
        }

        var promptCache = promptCacheRepo.findAll().get(0);
        promptCache.setPrompt(prompt.getPrompt());
        promptCacheRepo.save(promptCache);
        return true;
    }    
}
