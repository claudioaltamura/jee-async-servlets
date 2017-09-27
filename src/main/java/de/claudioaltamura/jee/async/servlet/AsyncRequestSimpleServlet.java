package de.claudioaltamura.jee.async.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.AsyncContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(value = "/requestsimple", asyncSupported = true)
public class AsyncRequestSimpleServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private final static Logger LOG = Logger.getLogger(AsyncRequestSimpleServlet.class.getName());

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
					LOG.log(Level.WARNING, e.getMessage());
				} catch (IOException e) {
					LOG.log(Level.WARNING, e.getMessage());
				} catch (InterruptedException e) { //comes from Thread.sleep();
					LOG.log(Level.WARNING, e.getMessage());
				}
			}
		});

	}

}
