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
}
