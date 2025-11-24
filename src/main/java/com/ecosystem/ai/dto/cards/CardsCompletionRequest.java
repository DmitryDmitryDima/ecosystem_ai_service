package com.ecosystem.ai.dto.cards;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CardsCompletionRequest {
    private String question;
}
