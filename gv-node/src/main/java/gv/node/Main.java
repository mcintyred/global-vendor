package gv.node;

import java.util.Scanner;

import org.apache.log4j.Logger;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;


public final class Main {

	private static final Logger LOGGER = Logger.getLogger(Main.class);

	private Main() { }

	/**
	 * Load the Spring Integration Application Context
	 *
	 * @param args - command line arguments
	 */
	public static void main(final String... args) {

		if (LOGGER.isInfoEnabled()) {
			LOGGER.info("\n========================================================="
					  + "\n                                                         "
					  + "\n          Starting a Warehouse Node                      "
					  + "\n                                                         "
					  + "\n=========================================================" );
		}

		final AbstractApplicationContext context =
				new ClassPathXmlApplicationContext("classpath:META-INF/spring/root-context.xml");

		context.registerShutdownHook();
		
		while(true) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				System.exit(0);
			}
		}

	}
}
