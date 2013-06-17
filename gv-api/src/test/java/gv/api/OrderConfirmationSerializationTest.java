package gv.api;

import static com.google.common.collect.Lists.newArrayList;
import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.joda.time.LocalDate;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.oxm.Marshaller;
import org.springframework.oxm.Unmarshaller;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@ContextConfiguration("./api-test-context.xml")
@RunWith(SpringJUnit4ClassRunner.class)
public class OrderConfirmationSerializationTest {
	
	@Autowired
	Marshaller marshaller;
	
	@Autowired
	Unmarshaller unmarshaller;
	
	String orderConfirmation = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>" +
			"<orderConfirmation xmlns=\"http://www.gv.demo/api\"><order><lines><line qty=\"13\" productId=\"12\"/>" +
			"</lines></order>" +
			"<shipments><shipment><warehouse name=\"Test Warehouse\" id=\"13\"/>" +
			"<lines><line qty=\"13\" shipDate=\"2012-05-03\">" +
			"<product name=\"ProductA\" id=\"12\">" +
			"<description>Description</description>" +
			"</product>" +
			"</line></lines></shipment></shipments></orderConfirmation>";

	@Test
	public void shouldSerialize() throws IOException {
		// given
		Long productId = 12L;
		Product product = new Product(productId, "ProductA", "Description");
		int qty = 13;
		
		OrderLine line = new OrderLine(productId, qty);
		
		Order order = new Order(newArrayList(line));
		
		Warehouse warehouse = new Warehouse(13L, "Test Warehouse");
		ShipmentLine shipmentLine = new ShipmentLine(new LocalDate("2012-05-03"), qty, product);
		Shipment shipment = new Shipment(warehouse, newArrayList(shipmentLine));
		
		OrderConfirmation confirmation = new OrderConfirmation(order, newArrayList(shipment));
		
		// when
		String xml = toString(confirmation);
		
		// then
		assertEquals(orderConfirmation, xml);

	}
	
	@Test
	public void shouldRoundtrip() throws IOException {
		// given
		String serialized =orderConfirmation;
	
		// when
		OrderConfirmation deserialized = fromString(serialized);
		String roundtripped = toString(deserialized);
		
		// then
		assertEquals(serialized, roundtripped);

	}
	
	public String toString(OrderConfirmation confirmation) throws IOException {
		StringWriter writer = new StringWriter();
		Result result = new StreamResult(writer);
		this.marshaller.marshal(confirmation, result);
		
		return writer.toString();
	}
	
	public OrderConfirmation fromString(String xml) throws IOException {
		Source source = new StreamSource(new StringReader(xml));
		return (OrderConfirmation) this.unmarshaller.unmarshal(source);
	}
}
