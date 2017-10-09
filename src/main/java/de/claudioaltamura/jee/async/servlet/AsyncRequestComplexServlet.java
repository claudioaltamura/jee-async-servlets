package de.claudioaltamura.jee.async.servlet;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import javax.servlet.AsyncContext;
import javax.servlet.ServletException;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebInitParam;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebServlet(value = "/asyncrequestcomplex", asyncSupported = true, initParams = {
		@WebInitParam(name = "threadpoolsize", value = "3") })
public class AsyncRequestComplexServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private static final Logger LOG = LoggerFactory.getLogger(AsyncRequestComplexServlet.class);

	public static final AtomicInteger counter = new AtomicInteger(0);
	public static final int CALLBACK_TIMEOUT_MS = 10000;
	public static final int MAX_SIMULATED_TASK_LENGTH_MS = 5000;

	private ExecutorService executorService;

	public void init() throws ServletException {

		int size = Integer.parseInt(getInitParameter("threadpoolsize"));
		executorService = Executors.newFixedThreadPool(size);
	}

	public void destroy() {
		executorService.shutdown();
		try {
			executorService.awaitTermination(5, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			LOG.error(e.getMessage());
		}
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		final AsyncContext ctx = request.startAsync();
		ctx.setTimeout(CALLBACK_TIMEOUT_MS);
		ctx.addListener(new AsyncRequestListener());

		// simulate error - this does not cause onError - causes network error
		// on client side
		if (counter.addAndGet(1) < 5) {
			throw new IndexOutOfBoundsException("Simulated error");
		} else {
			enqueTask(ctx);
		}
	}

	private void enqueTask(final AsyncContext ctx) {
		executorService.execute(new Runnable() {
			@Override
			public void run() {
				try {
					ServletResponse response = ctx.getResponse();
					if (response != null) {
						response.getWriter().write("ok");
						ctx.complete();
					} else {
						throw new IllegalStateException(); // this is caught below
					}
				} catch (IllegalStateException ex) {
					// just means the context was already timeout, timeout listener already called.
					LOG.error("Request object from context is null! (nothing to worry about.)");
				} catch (Exception e) {
					LOG.error("ERROR IN AsyncServlet", e);
				}
			}
		});
	}
}
