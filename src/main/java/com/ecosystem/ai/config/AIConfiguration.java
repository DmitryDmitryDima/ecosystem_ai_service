package com.ecosystem.ai.config;

import io.micrometer.observation.ObservationRegistry;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.MetadataMode;
import org.springframework.ai.model.ollama.autoconfigure.OllamaEmbeddingProperties;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.ollama.OllamaEmbeddingModel;
import org.springframework.ai.ollama.api.OllamaApi;
import org.springframework.ai.ollama.api.OllamaOptions;
import org.springframework.ai.ollama.management.ModelManagementOptions;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AIConfiguration {


    private final String OPEN_ROUTER_API_KEY = "open_router";
    private final String OPEN_ROUTER_MODEL_NAME = "x-ai/grok-4.1-fast:free";


    private final String GITHUB_MODELS_API_KEY = "github";
    private final String GITHUB_MODELS_MODEL_NAME = "openai/gpt-4.1-mini";




    @Bean
    public ChatClient openRouterChatClient(){
        OpenAiApi openAiApiOpenRouter = OpenAiApi.builder()
                .apiKey(OPEN_ROUTER_API_KEY)
                .baseUrl("https://openrouter.ai/api").build();

        OpenAiChatModel chatModelOpenRouter = OpenAiChatModel.builder()
                .openAiApi(openAiApiOpenRouter)
                .defaultOptions(OpenAiChatOptions.builder()

                        .model(OPEN_ROUTER_MODEL_NAME).build())

                .build();

        return ChatClient.builder(chatModelOpenRouter).build();
    }

    @Bean
    public ChatClient githubModelsChatClient(){
        OpenAiApi openAiApiGithubModels = OpenAiApi.builder()
                .apiKey(GITHUB_MODELS_API_KEY)
                .baseUrl("https://models.github.ai/inference").build();

        OpenAiChatModel chatModelGitHubModels = OpenAiChatModel.builder()
                .openAiApi(openAiApiGithubModels)
                .defaultOptions(
                        OpenAiChatOptions.builder()

                                .model(GITHUB_MODELS_MODEL_NAME)
                                .build()
                ).build();

        return ChatClient.builder(chatModelGitHubModels).build();
    }






    @Bean
    public OllamaApi ollamaApi(){
        return OllamaApi.builder().build();
    }



    @Bean
    public OllamaEmbeddingModel ollamaEmbeddingModel(OllamaApi ollamaApi){

        OllamaOptions ollamaOptions = OllamaOptions.builder()
                .model("qwen3-embedding:0.6b")

                .build();


        return new OllamaEmbeddingModel(ollamaApi, ollamaOptions, ObservationRegistry.NOOP, ModelManagementOptions.builder()
                .build() );
    }


}
