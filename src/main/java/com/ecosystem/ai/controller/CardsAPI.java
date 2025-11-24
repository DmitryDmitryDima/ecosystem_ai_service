package com.ecosystem.ai.controller;

import com.ecosystem.ai.dto.SecurityContext;
import com.ecosystem.ai.dto.cards.CardsCompletionAnswer;
import com.ecosystem.ai.dto.cards.CardsCompletionRequest;
import com.ecosystem.ai.service.CardsAiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/cards")
public class CardsAPI {

    @Autowired
    private CardsAiService cardsAiService;





    // обращаемся к нейросети с целью автозаполнения ответа к введенному пользователь вопросу
    // используется либо vector based ответ, либо мнение самой llm (если vector based недоступен)
    @PostMapping("/autocomplete")
    public CardsCompletionAnswer cardsCompletionAnswer(@RequestHeader Map<String, String> headers,
                                                       @RequestBody CardsCompletionRequest request){


        return cardsAiService.autocompletion(request, SecurityContext.generateContext(headers));








    }





}
