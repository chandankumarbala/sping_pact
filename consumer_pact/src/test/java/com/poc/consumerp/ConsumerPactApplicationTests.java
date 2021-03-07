package com.poc.consumerp;


import au.com.dius.pact.consumer.MockServer;
import au.com.dius.pact.consumer.dsl.PactDslJsonBody;
import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.consumer.junit5.PactConsumerTestExt;
import au.com.dius.pact.consumer.junit5.PactTestFor;
import au.com.dius.pact.core.model.RequestResponsePact;
import au.com.dius.pact.core.model.annotations.Pact;
import au.com.dius.pact.core.model.annotations.PactFolder;
import org.apache.commons.io.FileUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.fluent.Request;
import org.json.JSONArray;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;


@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ExtendWith(PactConsumerTestExt.class)
@PactTestFor(providerName = "mock_provider", port = "8086")
@PactFolder("/home/chandan/code/POC/pact_contract/contracts")
class ConsumerPactApplicationTests {


	@Pact(consumer = "consumer-app",provider = "mock_provider")
	public RequestResponsePact producerResponds(PactDslWithProvider builder) throws IOException {
		Map<String, String> headers = new HashMap<>();
		headers.put("Content-Type", "application/json");

		String expectedPOutputFromProducer=
				FileUtils.readFileToString(new File("/home/chandan/code/POC/pact_contract/contracts/mock_responces/producer-reponse.json"), StandardCharsets.US_ASCII);
		return builder.given("Producer reponds as expected 200")//the state is picked from here in producer side for check
				.uponReceiving("A request to /producer/books")
				.path("/producer/books")
				.method("GET")
				.willRespondWith()
				.status(200)
				.body(expectedPOutputFromProducer)
				.toPact();
	}


	@Pact(consumer = "consumer-app",provider = "mock_provider")
	public RequestResponsePact producerRespondsWithHarryBook(PactDslWithProvider builder) throws IOException {
		Map<String, String> headers = new HashMap<>();
		headers.put("Content-Type", "application/json");


		PactDslJsonBody responseWrittenWithDsl = new PactDslJsonBody()
				.stringType("name", "Harry Porter 1")
				.stringType("author","Chandan Kumar Bala")
				.integerType("pages",300)
				.stringType("edition","1.0")
				.close()
				.asBody();
		return builder.given("Producer reponds with a Harry Potter book")//the state is picked from here in producer side for check
				.uponReceiving("A request to /producer/book")
				.path("/producer/book")
				.method("GET")
				.willRespondWith()
				.status(200)
				.body(responseWrittenWithDsl)
				.toPact();
	}


	@Test
	@PactTestFor(pactMethod = "producerResponds")
	public void validateProviderContract(MockServer mockServer) throws Exception {
		/*final RestTemplate restCaller = new RestTemplate();
		String json = restCaller.getForEntity("http://localhost:8086/producer/books", String.class).getBody().toString();
		Assert.assertNotNull(json);*/
		HttpResponse httpResponse = Request.Get(mockServer.getUrl() + "/producer/books")
				.execute().returnResponse();
		Assert.assertEquals(httpResponse.getStatusLine().getStatusCode(),200);

	}


	@Autowired
	private ConsumerService service;

	@Test
	@PactTestFor(pactMethod = "producerResponds")
	public void validateServiceLayer() throws Exception {
		String jsonFromMockedBackend=service.getBooksFromProducer();
		//System.out.println(jsonFromMockedBackend);
		JSONArray parsedJson=new JSONArray(jsonFromMockedBackend);
		Assert.assertEquals(parsedJson.length(),30);
	}

	@Test
	@PactTestFor(pactMethod = "producerRespondsWithHarryBook")
	public void validateProducersHarryBook() throws Exception {
		BookBeanFromConsumer processedData=service.getHarrysBookFromProducer();
		Assert.assertEquals(processedData.getAuthor(),"Chandan Kumar Bala");
		Assert.assertEquals(processedData.getPages(),300);
	}

}
