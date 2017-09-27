package de.claudioaltamura.jee.async.servlet;

import javax.servlet.http.HttpServlet;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

public class JettyTestServer {

	private Server server;
	private ServletContextHandler context;

	public JettyTestServer(int port, String contextPath) throws Exception {
		server = new Server(port);
		context = new ServletContextHandler();
		context.setContextPath(contextPath);
		server.setHandler(context);
	}

	public void addServlet(Class<? extends HttpServlet> servletClass, boolean async, String path) {
		ServletHolder asyncHolder = context.addServlet(servletClass, path);
		asyncHolder.setAsyncSupported(async);
	}

	public void start() throws Exception
	{
		server.start();
	}

	public void stop() throws Exception
	{
		if(server!=null)
		{
			server.stop();
			server.join();
		}
	}

}
