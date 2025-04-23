package com.vityazev_egor.Models;

import java.util.Optional;

import lombok.AllArgsConstructor;
import lombok.Getter;
import java.awt.image.BufferedImage;

@Getter
@AllArgsConstructor
public class ChatAnswer {
    private Optional<String> cleanAnswer;
    private Optional<String> htmlAnswer;
    private Optional<BufferedImage> answerImage;

    public ChatAnswer(){
        cleanAnswer = Optional.empty();
        htmlAnswer = Optional.empty();
        answerImage = Optional.empty();
    }

    public void setCleanAnswer(String cleanAnswer) {
        this.cleanAnswer = Optional.ofNullable(cleanAnswer);
    }
    public void setHtmlAnswer(String htmlAnswer) {
        this.htmlAnswer = Optional.ofNullable(htmlAnswer);
    }
    public void setAnswerImage(BufferedImage answerImage) {
        this.answerImage = Optional.ofNullable(answerImage);
    }

    public void addPrefixToCleanAnswer(String prefix){
        cleanAnswer.ifPresentOrElse(answer ->{
            cleanAnswer = Optional.of(prefix + answer);
        }, () -> System.err.println("Clean answer is empty"));
    }
}
