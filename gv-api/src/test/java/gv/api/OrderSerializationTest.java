package gv.api;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.StringWriter;

import javax.xml.transform.Result;
import javax.xml.transform.stream.StreamResult;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.oxm.Marshaller;
import org.springframework.oxm.Unmarshaller;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.google.common.collect.Lists;

@ContextConfiguration("./api-test-context.xml")
@RunWith(SpringJUnit4ClassRunner.class)
public class OrderSerializationTest {
	
	@Autowired
	Marshaller marshaller;
	
	@Autowired
	Unmarshaller unmarshaller;

	@Test
	public void shouldSerialize() throws IOException {
		// given
		Long productId = 12L;
		int qty = 13;
		
		OrderLine line = new OrderLine(productId, qty);
		
		Order order = new Order(Lists.newArrayList(line));
		
		String xml = toString(order);
		
		String expected ="<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>" +
				"<ns2:order xmlns:ns2=\"http://www.gv.demo/api\"><lines><line qty=\"13\" productId=\"12\"/></lines></ns2:order>";
		
		assertEquals(expected, xml);

	}
	
	public String toString(Order order) throws IOException {
		StringWriter writer = new StringWriter();
		Result result = new StreamResult(writer);
		this.marshaller.marshal(order, result);
		
		return writer.toString();
	}
}
