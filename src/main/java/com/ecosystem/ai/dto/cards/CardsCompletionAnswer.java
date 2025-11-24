package com.ecosystem.ai.dto.cards;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CardsCompletionAnswer {

    private String answer;

    private List<String> tags;
}
