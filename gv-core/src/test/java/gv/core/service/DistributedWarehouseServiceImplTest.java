package gv.core.service;

import static org.junit.Assert.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import gv.AbstractWarehouseTest;
import gv.api.Warehouse;
import gv.warehouse.api.StockChangeRequest;
import gv.warehouse.api.StockQueryRequest;

import java.util.Set;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class DistributedWarehouseServiceImplTest extends AbstractWarehouseTest {
	
	@Test
	public void shouldListWarehouses() {
		// when
		Set<Warehouse> warehouses = warehouseService.listWarehouses();
		// then
		assertEquals(warehouses.size(), 3);
	}
	
	@Test
	public void shouldGetWarehouseById() {
		// when
		Warehouse found = warehouseService.getWarehouseById(LONDON);
		// then
		assertEquals(found, london);
		// when
		found = warehouseService.getWarehouseById(PARIS);
		// then
		assertEquals(found, paris);
		// when
		found = warehouseService.getWarehouseById(TOKYO);
		// then
		assertEquals(found, tokyo);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void shouldNotGetWarehouseByInvalidId() {
		// when
		warehouseService.getWarehouseById(13L);
		// then
		// exception expected
	}
	
	@Test
	public void shouldUpdateStockInTheCorrectWarehouse() {
		// given
		Long warehouseId = LONDON;
		Long productId = 127L;
		int stockDelta = 55;
		StockChangeRequest request = new StockChangeRequest(warehouseId, productId, stockDelta);
		given(londonService.updateStock(request)).willReturn(stockDelta + 5);
		
		// when
		warehouseService.updateStock(request);
		
		// then
		verify(londonService).updateStock(request);
		verifyZeroInteractions(parisService);
	}

	@Test
	public void shouldSetStockInTheCorrectWarehouse() {
		// given
		Long warehouseId = LONDON;
		Long productId = 127L;
		int stockLevel = 55;		
		StockChangeRequest request = new StockChangeRequest(warehouseId, productId, stockLevel);
		
		// when
		warehouseService.setStock(request);
		
		// then
		verify(londonService).setStock(request);
		verifyZeroInteractions(parisService);
	}

	@Test
	public void shouldGetStockFromTheCorrectWarehouse() {
		// given
		Long warehouseId = LONDON;
		Long productId = 127L;
		int stockLevel = 55;	
		StockQueryRequest request = new StockQueryRequest(warehouseId, productId);
		given(londonService.getStock(request)).willReturn(stockLevel);
		// when
		int foundStockLevel = warehouseService.getStockInWarehouse(request);
		
		// then
		verify(londonService).getStock(request);
		verifyZeroInteractions(parisService);
		assertEquals(stockLevel, foundStockLevel);
	}

}
