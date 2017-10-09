package de.claudioaltamura.jee.async.servlet;

import java.io.IOException;

import javax.servlet.AsyncContext;
import javax.servlet.AsyncEvent;
import javax.servlet.AsyncListener;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AsyncRequestListener implements AsyncListener {

	private static final Logger LOG = LoggerFactory.getLogger(AsyncRequestListener.class);

	@Override
	public void onStartAsync(AsyncEvent event) throws IOException {
		LOG.info("start");
	}

	@Override
	public void onComplete(AsyncEvent event) throws IOException {
		LOG.info("complete");
	}

	@Override
	public void onTimeout(AsyncEvent event) throws IOException {
		LOG.info("timeout");
		LOG.info(event.toString());

		AsyncContext asyncContext = event.getAsyncContext();
		HttpServletResponse httpServletResponse = getHttpServletResponse(event.getAsyncContext().getResponse());
		httpServletResponse.sendError(HttpServletResponse.SC_GATEWAY_TIMEOUT, "timeout");
		asyncContext.complete();
	}

	@Override
	public void onError(AsyncEvent event) throws IOException {
		LOG.info("error");
		LOG.info(event.toString());

		AsyncContext asyncContext = event.getAsyncContext();
		HttpServletResponse httpServletResponse = getHttpServletResponse(event.getAsyncContext().getResponse());
		httpServletResponse.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "server error");
		asyncContext.complete();
	}

	private HttpServletResponse getHttpServletResponse(ServletResponse response)
	{
		return (HttpServletResponse)response;
	}
}
