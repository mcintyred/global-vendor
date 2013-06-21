package gv.warehouse.jpa.service;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.google.common.collect.Lists;

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
import gv.warehouse.jpa.service.WarehouseServiceImpl;
import gv.warehouse.jpa.service.entity.StockLevel;
import gv.warehouse.jpa.service.repository.StockLevelRepository;

@RunWith(MockitoJUnitRunner.class)
public class WarehouseServiceImplTest {
	
	private WarehouseServiceImpl service;
	
	@Mock
	private StockLevelRepository repository;
	
	class Captor implements StockAlertListener {
		
		private StockAlert alert;

		@Override
		public void handleStockAlert(StockAlert alert) {
			this.alert = alert;
		}
		
		public StockAlert getAlert() {
			return alert;
		}
		
		public void clear() {
			alert = null;
		}
	}
	
	private Captor listener = new Captor();
	
	@Before
	public void setUp() {
		service = new WarehouseServiceImpl(repository);
		service.setStockAlertListener(listener);
		service.setStockAlertThreshold(5);
	}
	
	@Test
	public void shouldUpdateStockLevelForNonExistentProduct() {
		// given
		long productId = 3L;
		String warehouseName = "testWarehouse";
		int stockDelta = 26;
		given(repository.findByWarehouseNameAndProductId(warehouseName, productId)).willReturn(null);
		
		// when
		int newStockLevel = service.updateStock(new StockChangeRequest(warehouseName, productId, stockDelta));
		
		// then
		verify(repository).findByWarehouseNameAndProductId(warehouseName, productId);
		verify(repository).save(any(StockLevel.class));
		assertEquals(stockDelta, newStockLevel);
		
		assertNotNull(listener.getAlert());
		StockAlert alert = listener.getAlert();
		assertEquals(productId, alert.getProductId());
		assertEquals(warehouseName, alert.getWarehouseName());
		assertEquals(stockDelta, alert.getStockLevel());
		assertEquals(5, alert.getThreshold());
	}
	
	@Test
	public void shouldUpdateStockLevelForExistingProduct() {
		// given
		long productId = 3L;
		long warehouseId = 5L;
		String warehouseName = "testWarehouse";
		int currentStock = 3;
		int stockDelta = 26;
		StockLevel existingStock = new StockLevel(warehouseName, productId, currentStock);
		given(repository.findByWarehouseNameAndProductId(warehouseName, productId)).willReturn(existingStock);
		
		// when
		int newStockLevel = service.updateStock(new StockChangeRequest(warehouseName, productId, stockDelta));
		
		// then
		verify(repository).findByWarehouseNameAndProductId(warehouseName, productId);
		verify(repository).save(any(StockLevel.class));
		assertEquals(currentStock + stockDelta, newStockLevel);
		
		assertNotNull(listener.getAlert());
		StockAlert alert = listener.getAlert();
		assertEquals(productId, alert.getProductId());
		assertEquals(warehouseName, alert.getWarehouseName());
		assertEquals(newStockLevel, alert.getStockLevel());
		assertEquals(5, alert.getThreshold());
	}
	
	@Test 
	public void shouldSetStockLevelForNonExistentProduct() {
		// given
		long productId = 3L;
		long warehouseId = 5L;
		String warehouseName = "testWarehouse";
		int qty = 26;
		given(repository.findByWarehouseNameAndProductId(warehouseName, productId)).willReturn(null);
		
		// when
		service.setStock(new StockChangeRequest(warehouseName, productId, qty));
		
		// then
		StockLevel expectedNewStockLevel = new StockLevel(warehouseName, productId, qty);
		verify(repository).findByWarehouseNameAndProductId(warehouseName, productId);
		verify(repository).save(eq(expectedNewStockLevel));
		
		assertNotNull(listener.getAlert());
		StockAlert alert = listener.getAlert();
		assertEquals(productId, alert.getProductId());
		assertEquals(warehouseName, alert.getWarehouseName());
		assertEquals(qty, alert.getStockLevel());
		assertEquals(5, alert.getThreshold());
	}

	@Test 
	public void shouldSetStockLevelForExistingProduct() {
		// given
		long productId = 3L;
		long warehouseId = 5L;
		String warehouseName = "testWarehouse";
		int qty = 26;
		StockLevel existingStockLevel = new StockLevel(warehouseName, productId, 2);
		
		given(repository.findByWarehouseNameAndProductId(warehouseName, productId)).willReturn(existingStockLevel);
		
		// when
		service.setStock(new StockChangeRequest(warehouseName, productId, qty));
		
		// then
		StockLevel expectedNewStockLevel = new StockLevel(warehouseName, productId, qty);
		verify(repository).findByWarehouseNameAndProductId(warehouseName, productId);
		verify(repository).save(eq(expectedNewStockLevel));
		
		
		assertNotNull(listener.getAlert());
		StockAlert alert = listener.getAlert();
		assertEquals(productId, alert.getProductId());
		assertEquals(warehouseName, alert.getWarehouseName());
		assertEquals(qty, alert.getStockLevel());
		assertEquals(5, alert.getThreshold());
	}
	
	@Test
	public void shouldReturnZeroStockForNonExistentProduct() {
		// given 
		Long productId = 3L;
		Long warehouseId = 5L;
		String warehouseName = "testWarehouse";
		given(repository.findByWarehouseNameAndProductId(warehouseName, productId)).willReturn(null);
		
		// when
		int stockLevel = service.getStock(new StockQueryRequest(warehouseName, productId));
		verify(repository).findByWarehouseNameAndProductId(warehouseName, productId);
		assertEquals(0,  stockLevel);
	}

	@Test
	public void shouldReturnCurrentStockForExistingProduct() {
		// given 
		Long productId = 3L;
		Long warehouseId = 5L;
		String warehouseName = "testWarehouse";
		int qty = 26;
		StockLevel existingStockLevel = new StockLevel(warehouseName, productId, qty);
		
		given(repository.findByWarehouseNameAndProductId(warehouseName, productId)).willReturn(existingStockLevel);
		
		// when
		int stockLevel = service.getStock(new StockQueryRequest(warehouseName, productId));
		verify(repository).findByWarehouseNameAndProductId(warehouseName, productId);
		assertEquals(qty,  stockLevel);
	}
	
	@Test 
	public void shouldConfirmShipmentRequestAndNotTriggerAnAlert() {
		// given
		long productId = 3L;
		long warehouseId = 5L;
		String warehouseName = "testWarehouse";
		int qty = 3;
		StockLevel existingStockLevel = new StockLevel(warehouseName, productId, 15);
		
		given(repository.findByWarehouseNameAndProductId(warehouseName, productId)).willReturn(existingStockLevel);
		
		ShipmentRequest request = new ShipmentRequest(warehouseName, productId, qty);
		
		// when
		ShipmentConfirmation confirmation = service.requestShipment(request);
		
		// then
		assertNotNull(confirmation);
		assertEquals(qty, confirmation.getQty());
		assertNull(listener.getAlert());
	}
	

	@Test 
	public void shouldConfirmShipmentRequestAndTriggerAnAlert() {
		// given
		long productId = 3L;
		long warehouseId = 5L;
		String warehouseName = "testWarehouse";
		int qty = 12;
		StockLevel existingStockLevel = new StockLevel(warehouseName, productId, 15);
		
		given(repository.findByWarehouseNameAndProductId(warehouseName, productId)).willReturn(existingStockLevel);
		
		ShipmentRequest request = new ShipmentRequest(warehouseName, productId, qty);
		
		// when
		ShipmentConfirmation confirmation = service.requestShipment(request);
		
		// then
		assertNotNull(confirmation);
		assertEquals(qty, confirmation.getQty());
		assertNotNull(listener.getAlert());
		StockAlert alert = listener.getAlert();
		assertEquals(productId, alert.getProductId());
		assertEquals(warehouseName, alert.getWarehouseName());
		assertEquals(3, alert.getStockLevel());
		assertEquals(5, alert.getThreshold());
	}
	
	@Test 
	public void shouldCancelShipmentRequestAndNotTriggerAnAlert() {
		// given
		long productId = 3L;
		long warehouseId = 5L;
		String warehouseName = "testWarehouse";
		StockLevel existingStockLevel = new StockLevel(warehouseName, productId, 15);
		
		given(repository.findByWarehouseNameAndProductId(warehouseName, productId)).willReturn(existingStockLevel);
		
		ShipmentLine line = new ShipmentLine(null, 1, new Product(productId, "", ""));
		Shipment shipment = new Shipment(
				new Warehouse(warehouseId, warehouseName),
				Lists.newArrayList(line));
		
		// when
		service.cancelShipment(shipment);
		
		// then
		verify(repository).save(any(StockLevel.class));
		assertNull(listener.getAlert());
	}

	@Test 
	public void shouldCancelShipmentRequestAndTriggerAnAlert() {
		// given
		long productId = 3L;
		long warehouseId = 5L;
		String warehouseName = "testWarehouse";
		StockLevel existingStockLevel = new StockLevel(warehouseName, productId, 3);
		
		given(repository.findByWarehouseNameAndProductId(warehouseName, productId)).willReturn(existingStockLevel);
		
		ShipmentLine line = new ShipmentLine(null, 13, new Product(productId, "", ""));
		Shipment shipment = new Shipment(
				new Warehouse(warehouseId, warehouseName),
				Lists.newArrayList(line));
		
		// when
		service.cancelShipment(shipment);
		
		// then
		verify(repository).save(any(StockLevel.class));
		assertNotNull(listener.getAlert());
		StockAlert alert = listener.getAlert();
		assertEquals(productId, alert.getProductId());
		assertEquals(warehouseName, alert.getWarehouseName());
		assertEquals(16, alert.getStockLevel());
		assertEquals(5, alert.getThreshold());
	}
}
