package com.ecosystem.ai.controllers;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


/*
conversation memory
 */
@RestController
@RequestMapping("/test/memory")
public class TestMemoryController {



    private final ChatClient chatClient;



    /*
    по дефолту используется in-memory память для чата. Помним, что llm не имеет собственной памяти
     */
    @Autowired
    public TestMemoryController(ChatClient.Builder builder, ChatMemory chatMemory){
        this.chatClient = builder.defaultAdvisors(MessageChatMemoryAdvisor
                .builder(chatMemory).build()).build();


    }


    @GetMapping("/say")
    public String say(@RequestParam String message){

        return chatClient.prompt().user(message).call().content();
    }







}
