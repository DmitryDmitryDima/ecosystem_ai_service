package com.ecosystem.ai.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;



@Service
public class ChatClientManager {



    @Autowired
    @Qualifier("vseLLMChatClient")
    private ChatClient vseLLMChatClient;


    public <T> T executePrompt(String systemMessage, String userMessage, Class<T> answerFormat){
        // временно
        return vseLLMChatClient.prompt()
                .system(systemMessage)
                .user(userMessage)
                .call()
                .entity(answerFormat);
    }


}
