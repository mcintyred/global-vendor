package gv;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.amqp.core.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import gv.test.IntegrationTest;
import gv.warehouse.api.ShipmentConfirmation;
import gv.warehouse.api.ShipmentRequest;
import gv.warehouse.api.StockChangeRequest;
import gv.warehouse.api.StockQueryRequest;
import gv.warehouse.api.WarehouseService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration()
@DirtiesContext(classMode = ClassMode.AFTER_CLASS)
@Category(IntegrationTest.class)
public class IntegrationTests {
	
	@Autowired
	@Qualifier("outboundGateway")
	private WarehouseService service;
	
	@Before
	public void setup() {
		setStock();
	}
	
	@Test
	public void shouldReturnName() {
		// when
		String name = service.getName();
		
		// then
		assertEquals("Remote Warehouse", name);
	}
	

	@Test
	public void shouldSetAndGetStock() throws InterruptedException {
		
		// given
		StockQueryRequest request = new StockQueryRequest(1L, 3L);
				
		// When
		Integer response = service.getStock(request);
		
		// Then
		assertEquals(new Integer(5), response);
		
	}
	
	@Test
	public void shouldUpdateStock() throws InterruptedException {
		
		// given
		StockChangeRequest request = new StockChangeRequest(1L, 3L, 19);
				
		// When
		Integer response = service.updateStock(request);
		
		// Then
		assertEquals(new Integer(24), response);
		
	}
	
	@Test
	public void shouldRequestShipment() throws InterruptedException, JsonParseException, JsonMappingException, IOException {
		
		// given
		ShipmentRequest request = new ShipmentRequest(1L, 3L, 19);
				
		// When
		ShipmentConfirmation confirmation = service.requestShipment(request);
		
		// Then
		assertEquals(new Long(3), confirmation.getProductId());
		assertEquals(5, confirmation.getQty());
	}
	
	protected void setStock() {
		StockChangeRequest request = new StockChangeRequest(1L, 3L, 5);
		service.setStock(request);
	}
}
