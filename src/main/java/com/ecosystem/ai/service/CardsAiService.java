package com.ecosystem.ai.service;

import com.ecosystem.ai.dto.SecurityContext;
import com.ecosystem.ai.dto.cards.CardsCompletionAnswer;
import com.ecosystem.ai.dto.cards.CardsCompletionRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CardsAiService {

    @Autowired
    private ChatClientManager chatClientManager;


    public CardsCompletionAnswer autocompletion(CardsCompletionRequest request, SecurityContext securityContext){
        String systemMessage =
                """
                
                Ты занимаешься созданием ответа для карточки запоминания. Пользователь вводит вопрос или термин, ты должен предложить возможный ответ
                
                Пример:
                question: Митоз
                answer: Процесс деления эукариотических клеток, при котором из одной материнской клетки образуются две генетически идентичные дочерние клетки
                
                Ответ должен быть лаконичным и коротким.
                
                На основе вопроса и ответа создай 2-3 тега.
                        
                """;

        String userMessage = request.getQuestion();

        return chatClientManager.executePrompt(systemMessage, userMessage, CardsCompletionAnswer.class);
    }
}
