package com.ecosystem.ai;

import com.ecosystem.ai.dto.Answer;
import com.ecosystem.ai.service.ChatClientManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class VseLLMTest {

    @Autowired
    private ChatClientManager manager;



    @Test
    public void basicTest(){
        Answer answer = manager.executePrompt("I'm using you for testing purposes in Spring AI. See user prompt and output format",
                "send me hello as answer", Answer.class);

        System.out.println(answer);


    }
}
