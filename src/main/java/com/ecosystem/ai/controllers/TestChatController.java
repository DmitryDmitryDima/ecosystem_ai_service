package com.ecosystem.ai.controllers;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/test")
public class TestChatController {

    private final ChatClient chatClient;

    public TestChatController(@Autowired ChatClient.Builder builder) {
        this.chatClient = builder.build();
    }


    // ответ посылается, как только будет готов, без стрима (довольно долго ждать)
    @GetMapping("/chat")
    public String chat(){
        return chatClient.prompt()
                .user("Создай карточку для запоминания (к примеру для колоды anki) с переводом и примером для английского слова consistency")
                .call()
                .content();
    }

    // стримим результат дял лучшего пользовательского опыта
    @GetMapping("/stream")
    public Flux<String> stream(){
        return chatClient.prompt()
                .user("дай самое краткое определение комплекса гольджи. Ответ пришли в json формате {suggestion:your_answer}")
                .stream()
                .content();

    }
}
