package gv.warehouse.gemfire.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import gv.api.Product;
import gv.api.Shipment;
import gv.api.ShipmentLine;
import gv.api.Warehouse;
import gv.stock.api.ShipmentConfirmation;
import gv.stock.api.ShipmentRequest;
import gv.stock.api.StockAlert;
import gv.stock.api.StockAlertListener;
import gv.stock.api.StockChangeRequest;
import gv.stock.api.StockQueryRequest;
import gv.test.IntegrationTest;
import gv.warehouse.gemfire.listener.StockChangeListener;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.google.common.collect.Lists;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
@Category(IntegrationTest.class)
public class WarehouseServiceImplIntegrationTest {
	
	
	@Autowired
	private WarehouseServiceImpl service;
	
	@Autowired
	private StockChangeListener listener;
	
	@Autowired
	private RegionWrapper wrapper;

	
	@Autowired 
	private StockAlertCaptor captor;
	
	@Before
	public void setUp() throws Exception {
		listener.setStockAlertListener(captor);
		listener.setStockAlertThreshold(5);
		wrapper.getRegion().clear();
		captor.clear();
	}

	@Test
	public void shouldUpdateStockLevelForNonExistentProduct() throws Exception {
		// given
		long productId = 3L;
		String warehouseName = "testWarehouse";
		int stockDelta = 26;
		
		captor.expect();
		
		// when
		int newStockLevel = service.updateStock(new StockChangeRequest(warehouseName, productId, stockDelta));
		
		// then
		assertEquals(stockDelta, newStockLevel);
		
		assertNotNull(captor.getAlert());
		StockAlert alert = captor.getAlert();
		assertEquals(productId, alert.getProductId());
		assertEquals(warehouseName, alert.getWarehouseName());
		assertEquals(stockDelta, alert.getStockLevel());
		assertEquals(5, alert.getThreshold());
	}
	
	@Test
	public void shouldUpdateStockLevelForExistingProduct() throws Exception {
		// given
		long productId = 3L;
		String warehouseName = "testWarehouse";
		int currentStock = 3;
		int stockDelta = 26;
		StockChangeRequest existingStock = new StockChangeRequest(warehouseName, productId, currentStock);
		service.setStock(existingStock);
		captor.clear();
		captor.expect();
		
		// when
		int newStockLevel = service.updateStock(new StockChangeRequest(warehouseName, productId, stockDelta));
		
		// then
		assertEquals(currentStock + stockDelta, newStockLevel);
		
		assertNotNull(captor.getAlert());
		StockAlert alert = captor.getAlert();
		assertEquals(productId, alert.getProductId());
		assertEquals(warehouseName, alert.getWarehouseName());
		assertEquals(newStockLevel, alert.getStockLevel());
		assertEquals(5, alert.getThreshold());
	}
	
	@Test 
	public void shouldSetStockLevelForNonExistentProduct() throws Exception {
		// given
		long productId = 3L;
		String warehouseName = "testWarehouse";
		int qty = 26;
		captor.expect();

		// when
		int newLevel = service.setStock(new StockChangeRequest(warehouseName, productId, qty));
		
		// then
		assertEquals(qty, newLevel);
		
		assertNotNull(captor.getAlert());
		StockAlert alert = captor.getAlert();
		assertEquals(productId, alert.getProductId());
		assertEquals(warehouseName, alert.getWarehouseName());
		assertEquals(qty, alert.getStockLevel());
		assertEquals(5, alert.getThreshold());
	}
	
	@Test 
	public void shouldSetStockLevelForExistingProduct() throws Exception {
		// given
		long productId = 3L;
		String warehouseName = "testWarehouse";
		int qty = 26;
		StockChangeRequest existingStock = new StockChangeRequest(warehouseName, productId, 3);
		service.setStock(existingStock);
		captor.clear();
		captor.expect();

		// when
		service.setStock(new StockChangeRequest(warehouseName, productId, qty));
		
		// then
		
		assertNotNull(captor.getAlert());
		StockAlert alert = captor.getAlert();
		assertEquals(productId, alert.getProductId());
		assertEquals(warehouseName, alert.getWarehouseName());
		assertEquals(qty, alert.getStockLevel());
		assertEquals(5, alert.getThreshold());
	}
	
	@Test
	public void shouldReturnZeroStockForNonExistentProduct() {
		// given 
		Long productId = 3L;
		String warehouseName = "testWarehouse";
		
		// when
		int stockLevel = service.getStock(new StockQueryRequest(warehouseName, productId));
		assertEquals(0,  stockLevel);
	}

	@Test
	public void shouldReturnCurrentStockForExistingProduct() throws Exception {
		// given 
		Long productId = 3L;
		String warehouseName = "testWarehouse";
		int qty = 26;
		StockChangeRequest existingStock = new StockChangeRequest(warehouseName, productId, qty);
		service.setStock(existingStock);
		captor.clear();
		captor.expect();

		// when
		int stockLevel = service.getStock(new StockQueryRequest(warehouseName, productId));
		assertEquals(qty,  stockLevel);
	}
	
	@Test 
	public void shouldConfirmShipmentRequestAndNotTriggerAnAlert() throws Exception {
		// given
		long productId = 3L;
		String warehouseName = "testWarehouse";
		int qty = 3;
		StockChangeRequest existingStock = new StockChangeRequest(warehouseName, productId, 4);
		service.setStock(existingStock);
		captor.clear();
		captor.expect();

		ShipmentRequest request = new ShipmentRequest(warehouseName, productId, qty);
		
		// when
		ShipmentConfirmation confirmation = service.requestShipment(request);
		
		// then
		assertNotNull(confirmation);
		assertEquals(qty, confirmation.getQty());
		assertNull(captor.getAlert());
	}
	

	@Test 
	public void shouldConfirmShipmentRequestAndTriggerAnAlert() throws Exception {
		// given
		long productId = 3L;
		String warehouseName = "testWarehouse";
		int qty = 12;
		StockChangeRequest existingStock = new StockChangeRequest(warehouseName, productId, 15);
		service.setStock(existingStock);
		captor.clear();
		captor.expect();

		ShipmentRequest request = new ShipmentRequest(warehouseName, productId, qty);
		
		// when
		ShipmentConfirmation confirmation = service.requestShipment(request);
		
		// then
		assertNotNull(confirmation);
		assertEquals(qty, confirmation.getQty());
		assertNotNull(captor.getAlert());
		StockAlert alert = captor.getAlert();
		assertEquals(productId, alert.getProductId());
		assertEquals(warehouseName, alert.getWarehouseName());
		assertEquals(3, alert.getStockLevel());
		assertEquals(5, alert.getThreshold());
	}
	
	@Test 
	public void shouldCancelShipmentRequestAndNotTriggerAnAlert() throws Exception {
		// given
		long productId = 3L;
		String warehouseName = "testWarehouse";
		int qty = 15;
		
		StockChangeRequest existingStock = new StockChangeRequest(warehouseName, productId, qty);
		service.setStock(existingStock);
		captor.clear();
		captor.expect();
		
		ShipmentLine line = new ShipmentLine(null, 1, new Product(productId, "", ""));
		Shipment shipment = new Shipment(
				new Warehouse(13L, warehouseName),
				Lists.newArrayList(line));
		
		// when
		service.cancelShipment(shipment);
		
		// then
		assertNull(captor.getAlert());
	}

	@Test 
	public void shouldCancelShipmentRequestAndTriggerAnAlert() throws Exception {
		// given
		long productId = 3L;
		String warehouseName = "testWarehouse";
		int qty = 3;
		
		StockChangeRequest existingStock = new StockChangeRequest(warehouseName, productId, qty);
		service.setStock(existingStock);
		captor.clear();
		captor.expect();
		
		ShipmentLine line = new ShipmentLine(null, 13, new Product(productId, "", ""));
		Shipment shipment = new Shipment(
				new Warehouse(13L, warehouseName),
				Lists.newArrayList(line));
		
		// when
		service.cancelShipment(shipment);
		
		// then
		assertNotNull(captor.getAlert());
		StockAlert alert = captor.getAlert();
		assertEquals(productId, alert.getProductId());
		assertEquals(warehouseName, alert.getWarehouseName());
		assertEquals(16, alert.getStockLevel());
		assertEquals(5, alert.getThreshold());
	}
}
