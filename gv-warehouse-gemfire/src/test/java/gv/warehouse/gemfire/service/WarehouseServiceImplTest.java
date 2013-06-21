package gv.warehouse.gemfire.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import gv.api.Product;
import gv.api.Shipment;
import gv.api.ShipmentLine;
import gv.api.Warehouse;
import gv.stock.api.ShipmentConfirmation;
import gv.stock.api.ShipmentRequest;
import gv.stock.api.StockChangeRequest;
import gv.stock.api.StockQueryRequest;
import gv.test.UnitTest;
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
		String warehouseName = "testWarehouse";
		int stockDelta = 26;
		given(repository.findOne(new Id(warehouseName, productId))).willReturn(null);
		
		// when
		int newStockLevel = service.updateStock(new StockChangeRequest(warehouseName, productId, stockDelta));
		
		// then
		verify(repository).findOne(new Id(warehouseName, productId));
		verify(repository).save(any(StockLevel.class));
		assertEquals(stockDelta, newStockLevel);
	}
	
	@Test
	public void shouldUpdateStockLevelForExistingProduct() {
		// given
		long productId = 3L;
		String warehouseName = "testWarehouse";
		int currentStock = 3;
		int stockDelta = 26;
		StockLevel existingStock = new StockLevel(warehouseName, productId, currentStock);
		given(repository.findOne(new Id(warehouseName, productId))).willReturn(existingStock);
		
		// when
		int newStockLevel = service.updateStock(new StockChangeRequest(warehouseName, productId, stockDelta));
		
		// then
		verify(repository).save(any(StockLevel.class));
		assertEquals(currentStock + stockDelta, newStockLevel);
	}
	
	@Test 
	public void shouldSetStockLevelForNonExistentProduct() {
		// given
		long productId = 3L;
		String warehouseName = "testWarehouse";
		int qty = 26;
		given(repository.findOne(new Id(warehouseName, productId))).willReturn(null);
		
		// when
		service.setStock(new StockChangeRequest(warehouseName, productId, qty));
		
		// then
		StockLevel expectedNewStockLevel = new StockLevel(warehouseName, productId, qty);
		verify(repository).save(eq(expectedNewStockLevel));
	}

	@Test 
	public void shouldSetStockLevelForExistingProduct() {
		// given
		long productId = 3L;
		String warehouseName = "testWarehouse";
		int qty = 26;
		StockLevel existingStockLevel = new StockLevel(warehouseName, productId, 2);
		
		given(repository.findOne(new Id(warehouseName, productId))).willReturn(existingStockLevel);
		
		// when
		service.setStock(new StockChangeRequest(warehouseName, productId, qty));
		
		// then
		StockLevel expectedNewStockLevel = new StockLevel(warehouseName, productId, qty);
		verify(repository).save(eq(expectedNewStockLevel));
	}
	
	@Test
	public void shouldReturnZeroStockForNonExistentProduct() {
		// given 
		Long productId = 3L;
		String warehouseName = "testWarehouse";
		given(repository.findOne(new Id(warehouseName, productId))).willReturn(null);
		
		// when
		int stockLevel = service.getStock(new StockQueryRequest(warehouseName, productId));
		verify(repository).findOne(new Id(warehouseName, productId));
		assertEquals(0,  stockLevel);
	}

	@Test
	public void shouldReturnCurrentStockForExistingProduct() {
		// given 
		Long productId = 3L;
		String warehouseName = "testWarehouse";
		int qty = 26;
		StockLevel existingStockLevel = new StockLevel(warehouseName, productId, qty);
		
		given(repository.findOne(new Id(warehouseName, productId))).willReturn(existingStockLevel);
		
		// when
		int stockLevel = service.getStock(new StockQueryRequest(warehouseName, productId));
		verify(repository).findOne(new Id(warehouseName, productId));
		assertEquals(qty,  stockLevel);
	}
	
	@Test 
	public void shouldConfirmShipmentRequest() {
		// given
		long productId = 3L;
		String warehouseName = "testWarehouse";
		int qty = 3;
		StockLevel existingStockLevel = new StockLevel(warehouseName, productId, 15);
		
		given(repository.findOne(new Id(warehouseName, productId))).willReturn(existingStockLevel);
		
		ShipmentRequest request = new ShipmentRequest(warehouseName, productId, qty);
		
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
		String warehouseName = "testWarehouse";
		StockLevel existingStockLevel = new StockLevel(warehouseName, productId, 15);
		
		given(repository.findOne(new Id(warehouseName, productId))).willReturn(existingStockLevel);
		
		ShipmentLine line = new ShipmentLine(null, 1, new Product(productId, "", ""));
		Shipment shipment = new Shipment(
				new Warehouse(13L, warehouseName),
				Lists.newArrayList(line));
		
		// when
		service.cancelShipment(shipment);
		
		// then
		verify(repository).save(any(StockLevel.class));
	}
}
