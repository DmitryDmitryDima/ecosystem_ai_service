package com.ecosystem.ai.controllers;


import com.ecosystem.ai.entities.BiologyTeacherAnswer;
import com.ecosystem.ai.entities.FilterAnswer;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.List;


@RestController
@RequestMapping("/test/structured")
public class TestStructuredOutput {


     private final ChatClient client;

     private final String biologyAssistantSystemPrompt = """
                
                You are a biology instructor that can answer questions about biology and biology science terms. You know nothing but biology science!
                
                
                You can ONLY discuss using SHORT answers:
                - All about biology science
                - All about terminology and classifications in biology
                
                You can't discuss something that not directly related to biology!If asked about anything else, respond "I can only help with biology-related questions"
                
                Each answer must be supported by appropriate tags related to the meaning of the question and answer.
                
                Positive example: what is Mitosis?
                Your answer: "Mitosis is the process of cell division that results in two genetically identical daughter cells from a single parent cell"
                Your tags: ["cells", "replication"]
                
                Positive example: what is DNA?
                Your answer: "DNA, or deoxyribonucleic acid, is the molecule that carries the genetic instructions for the development and function of all known living organisms and many viruses"
                Your tags: ["genetics", "nucleus"]
                
                
                Negative example: what is Java OOP?
                Your answer: "I can only help with biology-related questions"
                Your tags: "[]"
                
                Negative example: what is dollar?
                Your answer: "I can only help with biology-related questions"
                Your tags: "[]"
                
                
                
                
  
                """;

     public TestStructuredOutput(@Autowired ChatClient.Builder builder){
         this.client = builder.build();
     }


     @GetMapping("/hybridApproach")
     public BiologyTeacherAnswer testDoubleFiltration(@RequestParam String term){

         String filterMessage = "you are a filter. if user question is directly about biology and biology science terms - return true, else - return false";

         FilterAnswer filterAnswer = client.prompt().system(filterMessage).user(u->{
             u.text("what is {term}").param("term", term);}).call().entity(FilterAnswer.class);

         System.out.println(filterAnswer);

         if (filterAnswer!=null && !filterAnswer.result()){
             return new BiologyTeacherAnswer("I can only help with biology-related questions", List.of());

         }
         else {
             return client.prompt().user(u->{
                 u.text("what is {term}").param("term", term);
             }).system(biologyAssistantSystemPrompt).call().entity(BiologyTeacherAnswer.class);
         }

     }



     @GetMapping("/isRelated")
     public String relatedTest(@RequestParam String term){
         String systemMessage = "you are a filter. if user question is directly about biology and biology science terms - return json {answer:true}, else - return json {answer:false}";

         return client.prompt().user(u->{
             u.text("what is {term}").param("term", term);
         }).system(systemMessage).call().content();
     }


     @GetMapping("")
     public BiologyTeacherAnswer respond(@RequestParam String term){



         return client.prompt().user(u->{
             u.text("what is {term}").param("term", term);
         }).system(biologyAssistantSystemPrompt).call().entity(BiologyTeacherAnswer.class);
     }




}
