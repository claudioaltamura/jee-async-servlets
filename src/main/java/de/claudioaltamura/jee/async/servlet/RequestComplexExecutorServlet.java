package de.claudioaltamura.jee.async.servlet;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebInitParam;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebServlet(value = "/requestcomplexexecutor", initParams = {
		@WebInitParam(name = "threadpoolsize", value = "3") })
public class RequestComplexExecutorServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private static final Logger LOG = LoggerFactory.getLogger(RequestComplexExecutorServlet.class);

	public static final AtomicInteger counter = new AtomicInteger(0);

	private ExecutorService executorService;

	@Override
	public void init() throws ServletException {

		int size = Integer.parseInt(getInitParameter("threadpoolsize"));
		executorService = Executors.newFixedThreadPool(size);
	}

	@Override
	public void destroy() {
		executorService.shutdown();
		try {
			executorService.awaitTermination(5, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			LOG.error(e.getMessage());
		}
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		// simulate error - this does not cause onError - causes network error
		// on client side
		if (counter.addAndGet(1) < 5) {
			throw new IndexOutOfBoundsException("Simulated error");
		} else {
			enqueTask(request, response);
		}
	}

	private void enqueTask(HttpServletRequest request, HttpServletResponse response) {
		executorService.execute(new Runnable() {
			@Override
			public void run() {
				try {
					response.getWriter().write("ok");
				} catch (Exception e) {
					LOG.error("ERROR IN Servlet", e);
				}
			}
		});
	}
}
