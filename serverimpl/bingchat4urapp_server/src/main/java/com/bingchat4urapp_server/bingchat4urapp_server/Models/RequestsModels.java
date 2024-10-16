package com.bingchat4urapp_server.bingchat4urapp_server.Models;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class RequestsModels {
    public static class AuthRequest {

        @NotBlank(message = "Login is mandatory")
        @Size(min = 6, max = 50, message = "Login must be less than 50 characters")
        private String login;

        @NotBlank(message = "Password is mandatory")
        @Size(min = 8, max = 50, message = "Password must be at least 8 characters")
        private String password;

        // getters
        public String getLogin() {
            return login;
        }
        public String getPassword()  {
            return password;
        
        }

        // setters
        public void setLogin(String login) {
            this.login = login;
        }
        public void setPassword(String password)  {
            this.password = password;
        }
    }

    public static class PromtRequest {

        @NotBlank(message = "Prompt is mandatory")
        @Size(min = 2, max = 4000, message = "Prompt must be less than 4000 characters")
        private String promt;

        @NotNull(message = "Timeout for answer is mandatory")
        @Min(value = 30, message = "Timeout for answer must be at least 30 seconds")
        private Integer timeOutForAnswer;

        // getters
        public String getPromt() {
            return promt;
        }
        public Integer getTimeOutForAnswer() {
            return timeOutForAnswer;
        }

        // setters
        public void setPromt(String prompt) {
            this.promt = prompt;
        }
        public void setTimeOutForAnswer(Integer timeOutForAnswer) {
            this.timeOutForAnswer = timeOutForAnswer;
        }
    }

    public static class ChatRequest {

        @NotNull(message = "Type is mandatory")
        @Min(value = 1, message = "Type must be a positive integer")
        @Max(value = 3, message =  "Type must be less than 3")
        private Integer type;
    
        // getters
        public Integer getType() {
            return type;
        }
    
        // setters
        public void setType(Integer type) {
            this.type = type;
        }
    }

}
