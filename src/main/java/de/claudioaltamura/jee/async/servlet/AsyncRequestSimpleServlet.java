package de.claudioaltamura.jee.async.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Random;

import javax.servlet.AsyncContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebServlet(value = "/asyncrequestsimple", asyncSupported = true)
public class AsyncRequestSimpleServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private final static Logger LOG = LoggerFactory.getLogger(AsyncRequestSimpleServlet.class);

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String time = request.getParameter("ms");
		final int ms = time != null ? Integer.valueOf(time) : new Random().nextInt(15);

		AsyncContext asyncContext = request.startAsync();
		asyncContext.setTimeout(10);
		asyncContext.addListener(new AsyncRequestListener());
		//fire and forget
		asyncContext.start(new Runnable() {
			@Override
			public void run() {
				try {
					Thread.sleep(ms);
					PrintWriter out = asyncContext.getResponse().getWriter();
					out.write(ms + "ms waited");
					asyncContext.complete();
				} catch (IllegalStateException e) {
					LOG.warn(e.getMessage());
				} catch (IOException e) {
					LOG.warn(e.getMessage());
				} catch (InterruptedException e) { //comes from Thread.sleep();
					LOG.warn(e.getMessage());
				}
			}
		});

	}

}
