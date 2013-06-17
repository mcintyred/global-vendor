package gv.warehouse.gemfire;

import java.io.IOException;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Standalone {
	
	@SuppressWarnings("unchecked")
	public static void main(String[] args) throws IOException,
			InterruptedException {
		
		ApplicationContext context = new ClassPathXmlApplicationContext("gemfire/standalone/cache-config.xml");
		
		System.out.println("Standalone cache started");
		
		while(true) {
			Thread.sleep(1000L);
		}

	}

}
