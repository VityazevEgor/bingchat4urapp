package com.vityazev_egor.Models;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ChatAnswer {
    private String cleanAnswer;
    private String htmlAnswer;
}
