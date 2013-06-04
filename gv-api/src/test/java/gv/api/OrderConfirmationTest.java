package gv.api;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

import com.google.common.collect.Lists;

public class OrderConfirmationTest {
	
	@Test
	public void shouldSetProperties() {
		// given
		List<Shipment> shipments = Lists.newArrayList();
		Order order = new Order();
		
		// When
		OrderConfirmation confirmation = new OrderConfirmation(order, shipments);
		
		// then
		assertEquals(shipments, confirmation.getShipments());
		assertEquals(order, confirmation.getOrder());
	}
	
	@Test
	public void shouldIndicatePartialShipments() {
		// given
		Product p1 = new Product(13L, "Product13", "Product13D");
		Product p2 = new Product(17L, "Product17", "Product17D");
		OrderLine line1 = new OrderLine(p1.getId(), 5);
		OrderLine line2 = new OrderLine(p2.getId(), 7);
		Order order = new Order(Lists.newArrayList(line1, line2));
		
		ShipmentLine sl1 = new ShipmentLine(null, 4, p1);
		ShipmentLine sl2 = new ShipmentLine(null, 7, p2);
		
		Shipment s1 = new Shipment(null, Lists.newArrayList(sl1, sl2));
		
		OrderConfirmation confirmation = new OrderConfirmation(order, Lists.newArrayList(s1));
		
		// then
		assertTrue("Should be partial", confirmation.isPartial());
		
	}

	@Test
	public void shouldIndicateFullShipments() {
		// given
		Product p1 = new Product(13L, "Product13", "Product13D");
		Product p2 = new Product(17L, "Product17", "Product17D");
		OrderLine line1 = new OrderLine(p1.getId(), 5);
		OrderLine line2 = new OrderLine(p2.getId(), 7);
		Order order = new Order(Lists.newArrayList(line1, line2));
		
		ShipmentLine sl1 = new ShipmentLine(null, 5, p1);
		ShipmentLine sl2 = new ShipmentLine(null, 7, p2);
		
		Shipment s1 = new Shipment(null, Lists.newArrayList(sl1, sl2));
		
		OrderConfirmation confirmation = new OrderConfirmation(order, Lists.newArrayList(s1));
		
		// then
		assertFalse("Should not be partial", confirmation.isPartial());
		
	}

}
