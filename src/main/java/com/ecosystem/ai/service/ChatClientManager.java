package com.ecosystem.ai.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;


/*

генерируем ответ в зависимости от доступности того или иного клиента (можно также фиксировать лимиты)

если имет место историю - она должна быть доступна всем клиентам
 */
@Service
public class ChatClientManager {


    @Autowired
    @Qualifier("openRouterChatClient")
    private ChatClient openRouterClient;

    @Autowired
    @Qualifier("githubModelsChatClient")
    private ChatClient githubModelsClient;

    /*
    нужно разработать механизм подбора нужного клиента
     */
    public <T> T executePrompt(String systemMessage, String userMessage, Class<T> answerFormat){
        // временно
        return openRouterClient.prompt()
                .system(systemMessage)
                .user(userMessage)
                .call()
                .entity(answerFormat);
    }


}
