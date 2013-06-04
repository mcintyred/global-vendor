package gv.api;

import static org.junit.Assert.assertEquals;

import org.junit.Test;


public class OrderLineTest {
	
	@Test
	public void shouldSetProperties() {
		// given
		OrderLine line = new OrderLine(13L, 23);
		
		// then
		assertEquals(13L, line.getProductId());
		assertEquals(23, line.getQty());
		
	}

}
