package de.claudioaltamura.jee.async.servlet;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.AsyncEvent;
import javax.servlet.AsyncListener;
import javax.servlet.http.HttpServletResponse;

public class AsyncRequestListener implements AsyncListener {

	private final static Logger LOG = Logger.getLogger(AsyncRequestListener.class.getName());

	@Override
	public void onStartAsync(AsyncEvent event) throws IOException {
	}

	@Override
	public void onComplete(AsyncEvent event) throws IOException {
	}

	@Override
	public void onTimeout(AsyncEvent event) throws IOException {
		LOG.info("timeout");
		((HttpServletResponse)event.getAsyncContext().getResponse()).sendError(HttpServletResponse.SC_GATEWAY_TIMEOUT, "timeout");
		event.getAsyncContext().complete();
	}

	@Override
	public void onError(AsyncEvent event) throws IOException {
		LOG.info("error");
		((HttpServletResponse)event.getAsyncContext().getResponse()).sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "server error");
		event.getAsyncContext().complete();
	}

}
