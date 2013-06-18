package gv.warehouse.gemfire.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import gv.api.Product;
import gv.api.Shipment;
import gv.api.ShipmentLine;
import gv.api.Warehouse;
import gv.test.UnitTest;
import gv.warehouse.api.ShipmentConfirmation;
import gv.warehouse.api.ShipmentRequest;
import gv.warehouse.api.StockAlert;
import gv.warehouse.api.StockAlertListener;
import gv.warehouse.api.StockChangeRequest;
import gv.warehouse.api.StockQueryRequest;
import gv.warehouse.gemfire.entity.StockLevel;
import gv.warehouse.gemfire.entity.StockLevel.Id;
import gv.warehouse.gemfire.service.repository.StockLevelRepository;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.google.common.collect.Lists;

@RunWith(MockitoJUnitRunner.class)
@Category(UnitTest.class)
public class WarehouseServiceImplTest {
	
	private WarehouseServiceImpl service;
	
	@Mock
	private StockLevelRepository repository;
	
	@Before
	public void setUp() {
		service = new WarehouseServiceImpl(repository);
	}
	
	@Test
	public void shouldUpdateStockLevelForNonExistentProduct() {
		// given
		long productId = 3L;
		long warehouseId = 5L;
		int stockDelta = 26;
		given(repository.findOne(new Id(warehouseId, productId))).willReturn(null);
		
		// when
		int newStockLevel = service.updateStock(new StockChangeRequest(warehouseId, productId, stockDelta));
		
		// then
		verify(repository).findOne(new Id(warehouseId, productId));
		verify(repository).save(any(StockLevel.class));
		assertEquals(stockDelta, newStockLevel);
	}
	
	@Test
	public void shouldUpdateStockLevelForExistingProduct() {
		// given
		long productId = 3L;
		long warehouseId = 5L;
		int currentStock = 3;
		int stockDelta = 26;
		StockLevel existingStock = new StockLevel(warehouseId, productId, currentStock);
		given(repository.findOne(new Id(warehouseId, productId))).willReturn(existingStock);
		
		// when
		int newStockLevel = service.updateStock(new StockChangeRequest(warehouseId, productId, stockDelta));
		
		// then
		verify(repository).save(any(StockLevel.class));
		assertEquals(currentStock + stockDelta, newStockLevel);
	}
	
	@Test 
	public void shouldSetStockLevelForNonExistentProduct() {
		// given
		long productId = 3L;
		long warehouseId = 5L;
		int qty = 26;
		given(repository.findOne(new Id(warehouseId, productId))).willReturn(null);
		
		// when
		service.setStock(new StockChangeRequest(warehouseId, productId, qty));
		
		// then
		StockLevel expectedNewStockLevel = new StockLevel(warehouseId, productId, qty);
		verify(repository).save(eq(expectedNewStockLevel));
	}

	@Test 
	public void shouldSetStockLevelForExistingProduct() {
		// given
		long productId = 3L;
		long warehouseId = 5L;
		int qty = 26;
		StockLevel existingStockLevel = new StockLevel(warehouseId, productId, 2);
		
		given(repository.findOne(new Id(warehouseId, productId))).willReturn(existingStockLevel);
		
		// when
		service.setStock(new StockChangeRequest(warehouseId, productId, qty));
		
		// then
		StockLevel expectedNewStockLevel = new StockLevel(warehouseId, productId, qty);
		verify(repository).save(eq(expectedNewStockLevel));
	}
	
	@Test
	public void shouldReturnZeroStockForNonExistentProduct() {
		// given 
		Long productId = 3L;
		Long warehouseId = 5L;
		given(repository.findOne(new Id(warehouseId, productId))).willReturn(null);
		
		// when
		int stockLevel = service.getStock(new StockQueryRequest(warehouseId, productId));
		verify(repository).findOne(new Id(warehouseId, productId));
		assertEquals(0,  stockLevel);
	}

	@Test
	public void shouldReturnCurrentStockForExistingProduct() {
		// given 
		Long productId = 3L;
		Long warehouseId = 5L;
		int qty = 26;
		StockLevel existingStockLevel = new StockLevel(warehouseId, productId, qty);
		
		given(repository.findOne(new Id(warehouseId, productId))).willReturn(existingStockLevel);
		
		// when
		int stockLevel = service.getStock(new StockQueryRequest(warehouseId, productId));
		verify(repository).findOne(new Id(warehouseId, productId));
		assertEquals(qty,  stockLevel);
	}
	
	@Test 
	public void shouldConfirmShipmentRequest() {
		// given
		long productId = 3L;
		long warehouseId = 5L;
		int qty = 3;
		StockLevel existingStockLevel = new StockLevel(warehouseId, productId, 15);
		
		given(repository.findOne(new Id(warehouseId, productId))).willReturn(existingStockLevel);
		
		ShipmentRequest request = new ShipmentRequest(warehouseId, productId, qty);
		
		// when
		ShipmentConfirmation confirmation = service.requestShipment(request);
		
		// then
		assertNotNull(confirmation);
		assertEquals(qty, confirmation.getQty());
	}
	
	@Test 
	public void shouldCancelShipmentRequest() {
		// given
		long productId = 3L;
		long warehouseId = 5L;
		StockLevel existingStockLevel = new StockLevel(warehouseId, productId, 15);
		
		given(repository.findOne(new Id(warehouseId, productId))).willReturn(existingStockLevel);
		
		ShipmentLine line = new ShipmentLine(null, 1, new Product(productId, "", ""));
		Shipment shipment = new Shipment(
				new Warehouse(warehouseId, ""),
				Lists.newArrayList(line));
		
		// when
		service.cancelShipment(shipment);
		
		// then
		verify(repository).save(any(StockLevel.class));
	}
}
