package gv.warehouse.gemfire.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import gv.api.Product;
import gv.api.Shipment;
import gv.api.ShipmentLine;
import gv.api.Warehouse;
import gv.test.IntegrationTest;
import gv.warehouse.api.DiscontinueProductRequest;
import gv.warehouse.api.ShipmentConfirmation;
import gv.warehouse.api.ShipmentRequest;
import gv.warehouse.api.StockAlert;
import gv.warehouse.api.StockAlertEventSource;
import gv.warehouse.api.StockAlertListener;
import gv.warehouse.api.StockChangeRequest;
import gv.warehouse.api.StockQueryRequest;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.google.common.collect.Lists;

@RunWith(SpringJUnit4ClassRunner.class)

abstract public class ClientServerTest {

	@Autowired
	private WarehouseServiceImpl service;
	
	@Autowired
	private StockAlertEventSource eventSource;
	
	
	class Captor implements StockAlertListener {
		
		private StockAlert alert;
		
		private CountDownLatch latch;
		
		public void expect() {
			latch = new CountDownLatch(1);
		}

		@Override
		public void handleStockAlert(StockAlert alert) {
			this.alert = alert;
			latch.countDown();
		}
		
		public StockAlert getAlert() throws Exception {
			latch.await(1000L, TimeUnit.MILLISECONDS);
			return alert;
		}
		
		public void clear() throws Exception {
			getAlert();
			alert = null;
		}
	}
	
	private Captor captor = new Captor();
	
	public static final long WAREHOUSE_ID = 3;
	public static final long PRODUCT_ID = 5;
	
	
	@Before
	public void setUp() throws Exception {
		
		captor.expect();
		
		DiscontinueProductRequest request = new DiscontinueProductRequest(WAREHOUSE_ID, PRODUCT_ID);
		service.discontinueProduct(request);
		
		eventSource.setStockAlertListener(captor);
		eventSource.setStockAlertThreshold(5);
		captor.clear();
	}

	@Test
	public void shouldUpdateStockLevelForNonExistentProduct() throws Exception {
		// given
		int stockDelta = 26;
		captor.expect();
		
		// when
		int newStockLevel = service.updateStock(new StockChangeRequest(WAREHOUSE_ID, PRODUCT_ID, stockDelta));
		
		// then
		assertEquals(stockDelta, newStockLevel);
		
		assertNotNull(captor.getAlert());
		StockAlert alert = captor.getAlert();
		assertEquals(PRODUCT_ID, alert.getProductId());
		assertEquals(WAREHOUSE_ID, alert.getWarehouseId());
		assertEquals(stockDelta, alert.getStockLevel());
		assertEquals(5, alert.getThreshold());
	}
	
	@Test
	public void shouldUpdateStockLevelForExistingProduct()  throws Exception {
		// given
		int currentStock = 3;
		int stockDelta = 26;
		
		captor.expect();
		StockChangeRequest existingStock = new StockChangeRequest(WAREHOUSE_ID, PRODUCT_ID, currentStock);
		service.setStock(existingStock);
		captor.clear();
		captor.expect();

		// when
		int newStockLevel = service.updateStock(new StockChangeRequest(WAREHOUSE_ID, PRODUCT_ID, stockDelta));
		
		// then
		assertEquals(currentStock + stockDelta, newStockLevel);
		
		assertNotNull(captor.getAlert());
		StockAlert alert = captor.getAlert();
		assertEquals(PRODUCT_ID, alert.getProductId());
		assertEquals(WAREHOUSE_ID, alert.getWarehouseId());
		assertEquals(newStockLevel, alert.getStockLevel());
		assertEquals(5, alert.getThreshold());
	}
	
	@Test 
	public void shouldSetStockLevelForNonExistentProduct() throws Exception {
		// given
		int qty = 26;
		captor.expect();

		// when
		int newLevel = service.setStock(new StockChangeRequest(WAREHOUSE_ID, PRODUCT_ID, qty));
		
		// then
		assertEquals(qty, newLevel);
		
		assertNotNull(captor.getAlert());
		StockAlert alert = captor.getAlert();
		assertEquals(PRODUCT_ID, alert.getProductId());
		assertEquals(WAREHOUSE_ID, alert.getWarehouseId());
		assertEquals(qty, alert.getStockLevel());
		assertEquals(5, alert.getThreshold());
	}
	
	@Test 
	public void shouldSetStockLevelForExistingProduct()  throws Exception {
		// given
		int qty = 26;
		
		captor.expect();
		StockChangeRequest existingStock = new StockChangeRequest(WAREHOUSE_ID, PRODUCT_ID, 3);
		service.setStock(existingStock);
		captor.clear();
		captor.expect();
		
		// when
		service.setStock(new StockChangeRequest(WAREHOUSE_ID, PRODUCT_ID, qty));
		
		// then
		
		assertNotNull(captor.getAlert());
		StockAlert alert = captor.getAlert();
		assertEquals(PRODUCT_ID, alert.getProductId());
		assertEquals(WAREHOUSE_ID, alert.getWarehouseId());
		assertEquals(qty, alert.getStockLevel());
		assertEquals(5, alert.getThreshold());
	}
	
	@Test
	public void shouldReturnZeroStockForNonExistentProduct() {
		// given 
		StockQueryRequest request = new StockQueryRequest(WAREHOUSE_ID, PRODUCT_ID);

		// when
		int stockLevel = service.getStock(request);
		assertEquals(0,  stockLevel);
	}

	@Test
	public void shouldReturnCurrentStockForExistingProduct() throws Exception {
		// given 
		int qty = 26;
		
		captor.expect();
		StockChangeRequest existingStock = new StockChangeRequest(WAREHOUSE_ID, PRODUCT_ID, qty);
		service.setStock(existingStock);
		captor.clear();
		
		// when
		int stockLevel = service.getStock(new StockQueryRequest(WAREHOUSE_ID, PRODUCT_ID));
		assertEquals(qty,  stockLevel);
	}
	
	@Test 
	public void shouldConfirmShipmentRequestAndNotTriggerAnAlert()  throws Exception {
		// given
		int qty = 3;
		
		captor.expect();
		StockChangeRequest existingStock = new StockChangeRequest(WAREHOUSE_ID, PRODUCT_ID, 4);
		service.setStock(existingStock);
		captor.clear();
		
		ShipmentRequest request = new ShipmentRequest(WAREHOUSE_ID, PRODUCT_ID, qty);
		captor.expect();
		
		// when
		ShipmentConfirmation confirmation = service.requestShipment(request);
		
		// then
		assertNotNull(confirmation);
		assertEquals(qty, confirmation.getQty());
		assertNull(captor.getAlert());
	}
	

	@Test 
	public void shouldConfirmShipmentRequestAndTriggerAnAlert()  throws Exception {
		// given
		int stockLevel = 15;
		int qty = 12;
		
		captor.expect();
		StockChangeRequest existingStock = new StockChangeRequest(WAREHOUSE_ID, PRODUCT_ID, stockLevel);
		service.setStock(existingStock);
		captor.clear();
		
		ShipmentRequest request = new ShipmentRequest(WAREHOUSE_ID, PRODUCT_ID, qty);
		captor.expect();
		
		// when
		ShipmentConfirmation confirmation = service.requestShipment(request);
		
		// then
		assertNotNull(confirmation);
		assertEquals(qty, confirmation.getQty());
		
		assertNotNull(captor.getAlert());
		StockAlert alert = captor.getAlert();
		
		assertEquals(PRODUCT_ID, alert.getProductId());
		assertEquals(WAREHOUSE_ID, alert.getWarehouseId());
		assertEquals(stockLevel - qty, alert.getStockLevel());
		assertEquals(5, alert.getThreshold());
	}
	
	@Test 
	public void shouldCancelShipmentRequestAndNotTriggerAnAlert()  throws Exception {
		// given
		int qty = 15;
		
		captor.expect();
		StockChangeRequest existingStock = new StockChangeRequest(WAREHOUSE_ID, PRODUCT_ID, qty);
		service.setStock(existingStock);
		captor.clear();
		
		
		ShipmentLine line = new ShipmentLine(null, 1, new Product(PRODUCT_ID, "", ""));
		Shipment shipment = new Shipment(
				new Warehouse(WAREHOUSE_ID, ""),
				Lists.newArrayList(line));
		captor.expect();
		// when
		service.cancelShipment(shipment);
		
		// then
		assertNull(captor.getAlert());
	}

	@Test 
	public void shouldCancelShipmentRequestAndTriggerAnAlert()  throws Exception {
		// given
		int qty = 3;
		
		captor.expect();
		StockChangeRequest existingStock = new StockChangeRequest(WAREHOUSE_ID, PRODUCT_ID, qty);
		service.setStock(existingStock);
		captor.clear();
		
		
		ShipmentLine line = new ShipmentLine(null, 13, new Product(PRODUCT_ID, "", ""));
		Shipment shipment = new Shipment(
				new Warehouse(WAREHOUSE_ID, ""),
				Lists.newArrayList(line));
		captor.expect();
		// when
		service.cancelShipment(shipment);
		
		// then
		assertNotNull(captor.getAlert());
		StockAlert alert = captor.getAlert();
		assertEquals(PRODUCT_ID, alert.getProductId());
		assertEquals(WAREHOUSE_ID, alert.getWarehouseId());
		assertEquals(16, alert.getStockLevel());
		assertEquals(5, alert.getThreshold());
	}
}