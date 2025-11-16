package com.ecosystem.ai;

import com.ecosystem.ai.entities.BiologyTeacherAnswer;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.embedding.EmbeddingResponse;

import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.transformer.splitter.TextSplitter;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.filter.Filter;
import org.springframework.ai.vectorstore.filter.FilterExpressionBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;


import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;

@SpringBootTest
class AiApplicationTests {



	@Autowired
	private EmbeddingModel embeddingModel;

	@Autowired
	private VectorStore vectorStore;

	@Autowired
	private ChatClient.Builder builder;

	@Value("classpath:/docs/2198.pdf")
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

		List<Document> documents = vectorStore.similaritySearch("что такое стабилизирующий отбор");



		documents.forEach(doc->{
			System.out.println(doc);
			System.out.println("Document--------------");

		});

		Instant now = Instant.now();

		BiologyTeacherAnswer answer = builder.build().prompt().advisors(QuestionAnswerAdvisor.builder(vectorStore).build())

				.user("что такое стабилизирующий отбор")
				.call().entity(BiologyTeacherAnswer.class);

		System.out.println(answer);
		Instant after = Instant.now();

		Duration duration = Duration.between(now, after);

		System.out.println(duration.toSeconds());


	}

}
