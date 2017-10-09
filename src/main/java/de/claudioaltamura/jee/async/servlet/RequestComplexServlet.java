package de.claudioaltamura.jee.async.servlet;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebServlet(value = "/requestcomplex")
public class RequestComplexServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private static final Logger LOG = LoggerFactory.getLogger(RequestComplexServlet.class);

	public static final AtomicInteger counter = new AtomicInteger(0);

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

	private void enqueTask(HttpServletRequest request, HttpServletResponse response) throws IOException {
		try {
			response.getWriter().write("ok");
		} catch (IOException e) {
			LOG.error(e.getMessage());
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "server error");
		}
	}
}
