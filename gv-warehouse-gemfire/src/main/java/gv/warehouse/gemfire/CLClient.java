package gv.warehouse.gemfire;

import java.io.IOException;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class CLClient {
	
	@SuppressWarnings("unchecked")
	public static void main(String[] args) throws IOException,
			InterruptedException {
		
		ApplicationContext context = new ClassPathXmlApplicationContext("gemfire/client/cl-cache-config.xml");
		
		System.out.println("CacheListener Client started");
		
		while(true) {
			Thread.sleep(1000L);
		}

	}

}
