package com.innolux.service;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.servlets.CrossOriginFilter;
import com.innolux.receiver.WebApiController;

import java.util.EnumSet;

import javax.servlet.DispatcherType;

import org.apache.log4j.Logger;

public class WebApiService extends Thread {
	public static Logger logger = Logger.getLogger(WebApiService.class);

	public void run() {
		ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
		context.setContextPath("/");

		 Server jettyServer = new Server(8080);
		//Server jettyServer = new Server(new QueuedThreadPool(128, 15));
//		ServerConnector connector = new ServerConnector(jettyServer, new HttpConnectionFactory());
//
//		connector.setPort(8080);
//		jettyServer.addConnector(connector);

		jettyServer.setHandler(context);

		ServletHolder jerseyServlet = context.addServlet(org.glassfish.jersey.servlet.ServletContainer.class, "/*");
		jerseyServlet.setInitOrder(0);

		// Tells the Jersey Servlet which REST service/class to load.
		jerseyServlet.setInitParameter("jersey.config.server.provider.classnames",
				WebApiController.class.getCanonicalName());

		// Add the filter, and then use the provided FilterHolder to configure
		// it
		FilterHolder cors = context.addFilter(CrossOriginFilter.class, "/*", EnumSet.of(DispatcherType.REQUEST));
		cors.setInitParameter(CrossOriginFilter.ALLOWED_ORIGINS_PARAM, "*");
		cors.setInitParameter(CrossOriginFilter.ACCESS_CONTROL_ALLOW_ORIGIN_HEADER, "*");
		cors.setInitParameter(CrossOriginFilter.ALLOWED_METHODS_PARAM, "GET, POST, PUT, DELETE, OPTIONS ,HEAD");
		cors.setInitParameter(CrossOriginFilter.ALLOWED_HEADERS_PARAM, "X-Requested-With,Content-Type,Accept,Origin");

		try {
			jettyServer.start();
			logger.info("jettyServer Started...");
			jettyServer.join();
		} catch (Exception e) {
			logger.info(e.toString());
		} finally {

			jettyServer.destroy();
			logger.info("jettyServer End...");
		}

	}

}
