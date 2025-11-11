package com.ecosystem.ai.controllers;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.template.st.StTemplateRenderer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.Map;



/*
https://github.com/danvega/multiple-llms/tree/main/src/main/java/dev пример проекта с поддержкой multiple clients
 */

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
    // метод doOnNext позволяет перехватывать ответ. Сюда можно вставить event listener
    @GetMapping("/stream")
    public Flux<String> stream(){
        return chatClient.prompt()
                .user("У меня было 5 яблок, 9 груш, 10 огурцов. Назови общее количество фруктов и овощей. Ответ дай в json {all_fruits:{int}, all_vegetables {int}}")
                .stream()
                .content().doOnNext(System.out::println);

    }


    @GetMapping("/chatResponse")
    public ChatResponse chatResponse(){
        return chatClient.prompt("дай самое краткое определение комплекса гольджи." +
                        " Ответ пришли в json формате {suggestion:your_answer}")
                .call()
                .chatResponse();
    }

    @GetMapping("/prompt")
    public Flux<String> promptTemplateTest(@RequestParam("composer") String composer){

        PromptTemplate template = PromptTemplate.builder()
                .renderer(StTemplateRenderer.builder().startDelimiterToken('<').endDelimiterToken('>').build())
                .template("назови самые известные произведения композитора <composer>")
                .build();




        return chatClient.prompt(template.create(Map.of("composer",
                composer)))
                .stream()
                .content(); // создаем prompt из template.create(), читаем


    }
}
