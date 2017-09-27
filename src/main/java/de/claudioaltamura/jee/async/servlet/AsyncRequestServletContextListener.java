package de.claudioaltamura.jee.async.servlet;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@WebListener
public class AsyncRequestServletContextListener implements ServletContextListener {

	static final String EXECUTOR_SERVICE = "executorService";

	public void contextInitialized(ServletContextEvent servletContextEvent) {
		ThreadPoolExecutor executor = new ThreadPoolExecutor(10, 100, 0L, TimeUnit.MILLISECONDS,
				new LinkedBlockingQueue<Runnable>(100));
		servletContextEvent.getServletContext().setAttribute(EXECUTOR_SERVICE, executor);
	}

	public void contextDestroyed(ServletContextEvent servletContextEvent) {
		ThreadPoolExecutor executor = (ThreadPoolExecutor) servletContextEvent.getServletContext()
				.getAttribute(EXECUTOR_SERVICE);
		executor.shutdown();
	}

}