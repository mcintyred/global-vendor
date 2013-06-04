package gv.api;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class OrderTest {
	
	@Test
	public void shouldSetProperties() {
		// given
		Order order = new Order();
		
		// then
		assertNotNull(order.getLines());
		assertEquals(0, order.getLines().size());
		
		// given
		List<OrderLine> lines = new ArrayList<OrderLine>();
		
		// when
		order.setLines(lines);
		
		// then
		assertEquals(lines, order.getLines());
		
		// given
		Order newOrder = new Order(lines);
		
		// then
		assertEquals(lines, newOrder.getLines());
	}

}
