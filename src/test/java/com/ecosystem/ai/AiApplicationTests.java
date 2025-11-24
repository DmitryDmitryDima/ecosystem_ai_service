package com.ecosystem.ai;

import com.ecosystem.ai.dto.BiologyTeacherAnswer;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;

import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.transformer.splitter.TextSplitter;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.filter.FilterExpressionBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;


import java.time.Duration;
import java.time.Instant;
import java.util.List;

@SpringBootTest
class AiApplicationTests {



	@Autowired
	@Qualifier("ollamaEmbeddingModel")
	private EmbeddingModel embeddingModel;

	@Autowired
	private VectorStore vectorStore;

	@Autowired
	@Qualifier("openAiChatClient")
	private ChatClient chatClient;

	@Value("classpath:/docs/biology_test.pdf")
	private Resource document;

	@Test
	void contextLoads() {


		// запись в векторную базу данных

		var pdfReader = new PagePdfDocumentReader(document);
		TextSplitter textSplitter = new TokenTextSplitter();

		List<Document> documents = textSplitter.apply(pdfReader.get());


		// пример добавления некоего id как обрабатываемому документу
		// в будущей системе наиболее грамотным будет поиск по disk_file_id
		/*

		то есть некоторая сущность под названием "база знаний"
		пользователь выбирает те файлы, которые он хочет включить в эту базу знаний
		то есть база знаний - по сути список file id
		при добавлении файла система проверяет, был ли файл ранее загружен в vector store
		Если нет - при подготовке базы знаний будет происходить его анализ и загрузка

		Количество баз знаний, а также максимальный размер хранилища будут ограничены, чтобы не допускать абьюза мощностей

		 */
		documents.forEach(el->{
			el.getMetadata().put("file_id", "some_id");

		});

		vectorStore.accept(documents);




	}

	@Test
	void searchInVectorStoreById(){
		String id = "some_id";
		List<Document> documents = vectorStore.similaritySearch(SearchRequest.builder()
						.query("что такое стабилизирующий отбор")
								.filterExpression(new FilterExpressionBuilder().eq("file_id", id).build())
										.build());
		documents.forEach(System.out::println);


	}

	@Test
	void searchInVector(){

		List<Document> documents = vectorStore.similaritySearch("Какую болезнь изучал Ивановский?");



		documents.forEach(doc->{
			System.out.println(doc);
			System.out.println("Document--------------");

		});

		Instant now = Instant.now();

		BiologyTeacherAnswer answer = chatClient.prompt().advisors(QuestionAnswerAdvisor.builder(vectorStore).build())

				.user("какую болезнь изучал ивановский")
				.call().entity(BiologyTeacherAnswer.class);

		System.out.println(answer);
		Instant after = Instant.now();

		Duration duration = Duration.between(now, after);

		System.out.println(duration.toSeconds());


	}

	@Test
	public void customChatClient(){

	}



	// если мы работаем с open api compatible endpoint'ом

	@Test
	public void fullyManualConfig(){


		OpenAiApi openAiApiOpenRouter = OpenAiApi.builder()
				.apiKey("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")
				.baseUrl("https://openrouter.ai/api").build();

		OpenAiChatModel chatModelOpenRouter = OpenAiChatModel.builder()
				.openAiApi(openAiApiOpenRouter)
				.defaultOptions(OpenAiChatOptions.builder()

						.model("x-ai/grok-4.1-fast:free").build())

				.build();

		ChatClient preparedChatOpenRouter = ChatClient.builder(chatModelOpenRouter).build();



		System.out.println(preparedChatOpenRouter.prompt().user("hello").call().content());




		OpenAiApi openAiApiGithubModels = OpenAiApi.builder()
				.apiKey("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")
				.baseUrl("https://models.github.ai/inference").build();

		OpenAiChatModel chatModelGitHubModels = OpenAiChatModel.builder()
				.openAiApi(openAiApiGithubModels)
				.defaultOptions(
						OpenAiChatOptions.builder()

								.model("openai/gpt-4.1-mini")
								.build()
				).build();

		ChatClient preparedChatGithubModels = ChatClient.builder(chatModelGitHubModels).build();

		System.out.println(preparedChatGithubModels.prompt().user("hello").call().content());





	}

}
