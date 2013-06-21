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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

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

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
@Transactional
public class WarehouseServiceImplRepositoryTest {
	
	private WarehouseServiceImpl service;
	
	@Autowired
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
		StockChangeRequest stockChangeRequest = new StockChangeRequest(warehouseName, productId, stockDelta);
		StockQueryRequest stockQueryRequest = new StockQueryRequest(warehouseName, productId);
		
		// when
		int newStockLevel = service.updateStock(stockChangeRequest);
		
		// then
		assertEquals(stockDelta, newStockLevel);
		
		assertNotNull(listener.getAlert());
		StockAlert alert = listener.getAlert();
		assertEquals(productId, alert.getProductId());
		assertEquals(warehouseName, alert.getWarehouseName());
		assertEquals(stockDelta, alert.getStockLevel());
		assertEquals(5, alert.getThreshold());
		
		// when
		int persistedStockLevel = service.getStock(stockQueryRequest);
		
		// then
		assertEquals(stockDelta, persistedStockLevel);
	}
	
	@Test
	public void shouldUpdateStockLevelForExistingProduct() {
		// given
		long productId = 3L;
		String warehouseName = "testWarehouse";
		int currentStock = 3;
		int stockDelta = 13;
		StockChangeRequest stockChangeRequest = new StockChangeRequest(warehouseName, productId, currentStock);		
		service.updateStock(stockChangeRequest);
		listener.clear();
		
		// when
		int newStockLevel = service.updateStock(new StockChangeRequest(warehouseName, productId, stockDelta));
		
		// then
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
		String warehouseName = "testWarehouse";
		int stockDelta = 26;
		StockChangeRequest stockChangeRequest = new StockChangeRequest(warehouseName, productId, stockDelta);
		StockQueryRequest stockQueryRequest = new StockQueryRequest(warehouseName, productId);
		
		// when
		int newStockLevel = service.setStock(stockChangeRequest);
		
		// then
		assertEquals(stockDelta, newStockLevel);
		
		assertNotNull(listener.getAlert());
		StockAlert alert = listener.getAlert();
		assertEquals(productId, alert.getProductId());
		assertEquals(warehouseName, alert.getWarehouseName());
		assertEquals(stockDelta, alert.getStockLevel());
		assertEquals(5, alert.getThreshold());
		
		// when
		int persistedStockLevel = service.getStock(stockQueryRequest);
		
		// then
		assertEquals(stockDelta, persistedStockLevel);
	}

	@Test 
	public void shouldSetStockLevelForExistingProduct() {
		// given
		long productId = 3L;
		String warehouseName = "testWarehouse";
		int currentStock = 13;
		int stockDelta = 13;
		StockChangeRequest stockChangeRequest = new StockChangeRequest(warehouseName, productId, currentStock);		
		service.setStock(stockChangeRequest);
		
		// when
		int newStockLevel = service.setStock(new StockChangeRequest(warehouseName, productId, stockDelta));
		
		// then
		assertEquals(stockDelta, newStockLevel);
		
		assertNotNull(listener.getAlert());
		StockAlert alert = listener.getAlert();
		assertEquals(productId, alert.getProductId());
		assertEquals(warehouseName, alert.getWarehouseName());
		assertEquals(newStockLevel, alert.getStockLevel());
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
	public void shouldConfirmShipmentRequestAndNotTriggerAnAlert() {
		// given
		long productId = 3L;
		String warehouseName = "testWarehouse";
		int qty = 3;
		int stockLevel = 15;
		
		service.setStock(new StockChangeRequest(warehouseName, productId, stockLevel));
		listener.clear();
		
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
		String warehouseName = "testWarehouse";
		int qty = 12;
		int stockLevel = 15;
		
		service.setStock(new StockChangeRequest(warehouseName, productId, stockLevel));
		
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
		String warehouseName = "testWarehouse";
		int stockLevel = 15;
		
		service.setStock(new StockChangeRequest(warehouseName, productId, stockLevel));
		listener.clear();
		
		ShipmentLine line = new ShipmentLine(null, 1, new Product(productId, "", ""));
		Shipment shipment = new Shipment(
				new Warehouse(13L, warehouseName),
				Lists.newArrayList(line));
		
		// when
		service.cancelShipment(shipment);
		
		// then
		assertNull(listener.getAlert());
	}

	@Test 
	public void shouldCancelShipmentRequestAndTriggerAnAlert() {
		// given
		long productId = 3L;
		String warehouseName = "testWarehouse";
		int stockLevel = 3;
		
		service.setStock(new StockChangeRequest(warehouseName, productId, stockLevel));
		listener.clear();
		
		ShipmentLine line = new ShipmentLine(null, 13, new Product(productId, "", ""));
		Shipment shipment = new Shipment(
				new Warehouse(13L, warehouseName),
				Lists.newArrayList(line));
		
		// when
		service.cancelShipment(shipment);
		
		// then
		assertNotNull(listener.getAlert());
		StockAlert alert = listener.getAlert();
		assertEquals(productId, alert.getProductId());
		assertEquals(warehouseName, alert.getWarehouseName());
		assertEquals(16, alert.getStockLevel());
		assertEquals(5, alert.getThreshold());
	}
}
