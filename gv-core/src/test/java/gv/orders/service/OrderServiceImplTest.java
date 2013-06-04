package gv.orders.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import gv.AbstractWarehouseTest;
import gv.api.Order;
import gv.api.OrderConfirmation;
import gv.api.OrderLine;
import gv.api.Product;
import gv.api.Shipment;
import gv.api.ShipmentLine;
import gv.products.api.ProductService;
import gv.warehouse.api.ShipmentConfirmation;
import gv.warehouse.api.ShipmentRequest;
import gv.warehouse.api.StockQueryRequest;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import com.google.common.collect.Lists;

public class OrderServiceImplTest extends AbstractWarehouseTest {
	
	private OrderServiceImpl orderService;
	
	@Mock
	protected ProductService productService;
	
	@Before
	public void setUp() {
		super.setUp();
		orderService = new OrderServiceImpl();
		orderService.setDistributedWarehouseService(warehouseService);
		orderService.setProductService(productService);
	}
	
	
	@Test
	public void shouldPlaceAnOrderWithWarehouseWithTheMostStock() {
		// given
		Long productId = 127L;
		Product product = new Product(productId, "pa", "pad");
		int qty = 7;
		OrderLine orderLine = new OrderLine(productId, qty);
		
		Order order = new Order(Lists.newArrayList(orderLine));
		int londonStock = 23;
		int parisStock = 22;
		int tokyoStock = 3;
		
		given(londonService.getStock(any(StockQueryRequest.class))).willReturn(londonStock);
		given(parisService.getStock(any(StockQueryRequest.class))).willReturn(parisStock);
		given(tokyoService.getStock(any(StockQueryRequest.class))).willReturn(tokyoStock);
		
		given(productService.getProductById(productId)).willReturn(product);
		
		given(londonService.requestShipment(any(ShipmentRequest.class))).willReturn(new ShipmentConfirmation(productId, new LocalDate(), qty));
		
		// when
		OrderConfirmation confirmation = orderService.placeOrder(order);
		
		// then
		assertNotNull(confirmation);
		assertFalse(confirmation.isPartial());
		assertEquals(confirmation.getShipments().size(), 1);

		Shipment shipment = confirmation.getShipments().get(0);
		assertEquals(shipment.getLines().size(), 1);
		
		ShipmentLine line = shipment.getLines().get(0);
		assertEquals(qty, line.getQty());
		assertEquals(productId, line.getProduct().getId());
		assertEquals((Long)LONDON, shipment.getWarehouse().getId());
	}

	@Test
	public void shouldPlaceAnOrderWithAllWarehousesIfNeeded() {
		// given
		Long productId = 127L;
		int qty = 18;
		OrderLine orderLine = new OrderLine(productId, qty);
		
		Order order = new Order(Lists.newArrayList(orderLine));
		int londonStock = 5;
		int parisStock = 7;
		int tokyoStock = 8;
		
		given(londonService.getStock(any(StockQueryRequest.class))).willReturn(londonStock);
		given(parisService.getStock(any(StockQueryRequest.class))).willReturn(parisStock);
		given(tokyoService.getStock(any(StockQueryRequest.class))).willReturn(tokyoStock);
		
		given(tokyoService.requestShipment(any(ShipmentRequest.class))).willReturn(new ShipmentConfirmation(productId, new LocalDate(), 8));
		given(parisService.requestShipment(any(ShipmentRequest.class))).willReturn(new ShipmentConfirmation(productId, new LocalDate(), 7));
		given(londonService.requestShipment(any(ShipmentRequest.class))).willReturn(new ShipmentConfirmation(productId, new LocalDate(), 3));
		
		// when
		OrderConfirmation confirmation = orderService.placeOrder(order);
		
		// then
		assertNotNull(confirmation);
		assertEquals(confirmation.getShipments().size(), 3);
		
	}
}
