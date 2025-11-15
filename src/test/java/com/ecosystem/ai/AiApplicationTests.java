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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;



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

	@Value("classpath:/docs/biology_test.pdf")
	private Resource document;

	@Test
	void contextLoads() {


		// запись в векторную базу данных

		var pdfReader = new PagePdfDocumentReader(document);
		TextSplitter textSplitter = new TokenTextSplitter();
		List<Document> documents = textSplitter.apply(pdfReader.get());

		vectorStore.accept(documents);



	}

	@Test
	void searchInVector(){

		BiologyTeacherAnswer answer = builder.build().prompt().advisors(QuestionAnswerAdvisor.builder(vectorStore).build())

				.user("какую болезнь изучал ивановский")
				.call().entity(BiologyTeacherAnswer.class);

		System.out.println(answer);
	}

}
