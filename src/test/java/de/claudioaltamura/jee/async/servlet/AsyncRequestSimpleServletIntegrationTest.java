package de.claudioaltamura.jee.async.servlet;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class AsyncRequestSimpleServletIntegrationTest {

	private JettyTestServer jettyTestServer;

	@Before
	public void setUp() throws Exception {
		jettyTestServer = new JettyTestServer(8080, "/async");
		jettyTestServer.addServlet(AsyncRequestSimpleServlet.class, true, "/requestsimple");
		jettyTestServer.start();
	}

	@After
	public void tearDown() throws Exception {
		jettyTestServer.stop();
	}
	@Test
	public void ok() throws ClientProtocolException, IOException {
		HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();
		CloseableHttpClient httpClient = httpClientBuilder.build();
		HttpGet request = new HttpGet("http://localhost:8080/async/requestsimple?ms=5");

		CloseableHttpResponse response = httpClient.execute(request);

		assertEquals(HttpServletResponse.SC_OK, response.getStatusLine().getStatusCode());
	}

	@Test
	public void timeout() throws ClientProtocolException, IOException {
		HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();
		CloseableHttpClient httpClient = httpClientBuilder.build();
		HttpGet request = new HttpGet("http://localhost:8080/async/requestsimple?ms=20");

		CloseableHttpResponse response = httpClient.execute(request);

		assertEquals(HttpServletResponse.SC_GATEWAY_TIMEOUT, response.getStatusLine().getStatusCode());
	}

}
