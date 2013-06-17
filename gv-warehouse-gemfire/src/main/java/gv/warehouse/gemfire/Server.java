package gv.warehouse.gemfire;

import java.io.IOException;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Server {
	
	@SuppressWarnings("unchecked")
	public static void main(String[] args) throws IOException,
			InterruptedException {
		
		ApplicationContext context = new ClassPathXmlApplicationContext("META-INF/gemfire/server/cache-config.xml");
		
		System.out.println("CacheListener Server started");
		
		while(true) {
			Thread.sleep(1000L);
		}

	}

}
