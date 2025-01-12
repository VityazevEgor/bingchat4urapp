package com.bingchat4urapp_server.bingchat4urapp_server.Models;

import com.vityazev_egor.Wrapper.LLMproviders;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class RequestsModels {
    @Getter
    @Setter
    @NoArgsConstructor
    public static class AuthRequest {

        @NotBlank(message = "Login is mandatory")
        @Size(min = 6, max = 50, message = "Login must be less than 50 characters")
        private String login;

        @NotBlank(message = "Password is mandatory")
        @Size(min = 8, max = 50, message = "Password must be at least 8 characters")
        private String password;

        @NotNull(message = "Provider is mandatory")
        private LLMproviders provider;
    }

    @Getter
    @Setter
    public static class PromtRequest {

        @NotBlank(message = "Prompt is mandatory")
        @Size(min = 4, max = 4000, message = "Prompt must be less than 4000 characters")
        private String promt;

        @NotNull(message = "Timeout for answer is mandatory")
        @Min(value = 30, message = "Timeout for answer must be at least 30 seconds")
        @Max(value = 300, message = "Timeout for answer must be smaller than 5 minutes")
        private Integer timeOutForAnswer;
    }

    @Getter
    @Setter
    public static class SetPreferedRequest {
        @NotNull(message = "You must provide value for field 'provider'")
        private LLMproviders provider;
    }
}
