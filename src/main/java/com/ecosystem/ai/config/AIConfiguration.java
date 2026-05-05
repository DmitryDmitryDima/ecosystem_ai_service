package com.ecosystem.ai.config;


import org.apache.logging.log4j.LogManager;

import org.apache.logging.log4j.Logger;
import org.springframework.ai.chat.client.ChatClient;

import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.ai.openai.api.ResponseFormat;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.RestClient;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Configuration
public class AIConfiguration {





    private final String vseLLMbase = "https://api.vsellm.ru";
    private final String vseLLMModel = "deepseek/deepseek-v3.2";









    @Bean
    public ChatClient vseLLMChatClient(){
        final RestClient.Builder builder = RestClient.builder()
                .requestInterceptor(new ClientLoggerRequestInterceptor());






        String apiKey = System.getenv("VSELLM_KEY");
        System.out.println(apiKey);





        OpenAiApi openAiApi = OpenAiApi.builder()
                .apiKey(apiKey)
                .baseUrl(vseLLMbase)
                .restClientBuilder(builder)
                .build();



        OpenAiChatModel chatModel = OpenAiChatModel.builder()
                .openAiApi(openAiApi)
                .defaultOptions(OpenAiChatOptions.builder()
                        .responseFormat(ResponseFormat.builder()
                                .type(ResponseFormat.Type.JSON_OBJECT)
                                .build())

                        .model(vseLLMModel).build())
                .build();

        return ChatClient.builder(chatModel).build();
    }



    /*
    пример interceptor'а
     */
    public static class ClientLoggerRequestInterceptor implements ClientHttpRequestInterceptor
    {
        private static final Logger log = LogManager.getLogger(ClientLoggerRequestInterceptor.class);

        @Override
        public ClientHttpResponse intercept(HttpRequest request, byte[] body,
                                            ClientHttpRequestExecution execution) throws IOException
        {
            logRequest(request, body);
            var response = execution.execute(request, body);
            return logResponse(request, response);
        }

        private void logRequest(HttpRequest request, byte[] body)
        {
            log.info("Request: {} {}", request.getMethod(), request.getURI());
            log.debug(String.valueOf(request.getHeaders()));
            if (body != null && body.length > 0)
            {
                log.info("Request body: {}", new String(body, StandardCharsets.UTF_8));
            }
        }

        private ClientHttpResponse logResponse(HttpRequest request,
                                               ClientHttpResponse response) throws IOException
        {
            log.info("Response status: {}", response.getStatusCode());
            log.debug(String.valueOf(response.getHeaders()));

            byte[] responseBody = response.getBody().readAllBytes();
            if (responseBody.length > 0)
            {
                log.info("Response body: {}",
                        new String(responseBody, StandardCharsets.UTF_8));
            }

            // Return wrapped response to allow reading the body again
            return new BufferingClientHttpResponseWrapper(response, responseBody);
        }
    }
    private static class BufferingClientHttpResponseWrapper implements ClientHttpResponse
    {
        private final ClientHttpResponse response;
        private final byte[] body;

        public BufferingClientHttpResponseWrapper(ClientHttpResponse response,
                                                  byte[] body)
        {
            this.response = response;
            this.body = body;
        }

        @Override
        public InputStream getBody()
        {
            return new ByteArrayInputStream(body);
        }

        // Delegate other methods to wrapped response
        @Override
        public HttpStatusCode getStatusCode() throws IOException
        {
            return response.getStatusCode();
        }

        @Override
        public HttpHeaders getHeaders()
        {
            return response.getHeaders();
        }

        @Override
        public void close()
        {
            response.close();
        }

        @Override
        public String getStatusText() throws IOException
        {
            return response.getStatusText();
        }
    }






    /*
    пример настройки ollama embedding модели
     */

    /*
    @Bean
    public OllamaApi ollamaApi(){
        return OllamaApi.builder().build();
    }

     */


    /*
    @Bean
    public OllamaEmbeddingModel ollamaEmbeddingModel(OllamaApi ollamaApi){

        OllamaOptions ollamaOptions = OllamaOptions.builder()
                .model("qwen3-embedding:0.6b")

                .build();


        return new OllamaEmbeddingModel(ollamaApi, ollamaOptions, ObservationRegistry.NOOP, ModelManagementOptions.builder()
                .build() );
    }

     */


}
