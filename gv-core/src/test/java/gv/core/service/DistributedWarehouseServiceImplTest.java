package gv.core.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import gv.AbstractWarehouseTest;
import gv.api.Warehouse;
import gv.core.service.entity.StockAlertEntity;
import gv.warehouse.api.StockAlert;
import gv.warehouse.api.StockAlertDetails;
import gv.warehouse.api.StockChangeRequest;
import gv.warehouse.api.StockQueryRequest;

import java.util.List;
import java.util.Set;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.runners.MockitoJUnitRunner;

import com.google.common.collect.Lists;

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
	
	@Test
	public void shouldHandleDepletedStockAlert() {
		// given
		StockAlert alert = new StockAlert(ham.getId(), LONDON, 5, 10);
		
		ArgumentCaptor<StockAlertEntity> captor = ArgumentCaptor.forClass(StockAlertEntity.class);
		
		// when
		warehouseService.handleStockAlert(alert);
		
		// then
		verify(stockAlertRepository).save(captor.capture());
		StockAlertEntity ent = captor.getValue();
		assertEquals(ham.getId(), (Long)ent.getProductId());
		assertEquals(LONDON, ent.getWarehouseId());
		assertEquals(5, ent.getStockLevel());
		assertEquals(10, ent.getThreshold());
	}

	@Test
	public void shouldHandleReplenishedStockAlert() {
		// given
		StockAlert alert = new StockAlert(ham.getId(), LONDON, 15, 10);
		StockAlertEntity ent = new StockAlertEntity();
		ent.setId(3L);
		ent.setProductId(ham.getId());
		ent.setWarehouseId(LONDON);
		ent.setStockLevel(5);
		ent.setThreshold(7);
		List<StockAlertEntity> alerts = Lists.newArrayList();
		alerts.add(ent);
		given(stockAlertRepository.findByWarehouseIdAndProductId(LONDON, ham.getId())).willReturn(alerts);
		
		ArgumentCaptor<StockAlertEntity> captor = ArgumentCaptor.forClass(StockAlertEntity.class);
		
		// when
		warehouseService.handleStockAlert(alert);
		
		// then
		verify(stockAlertRepository).delete(captor.capture());
		StockAlertEntity del = captor.getValue();
		assertEquals(ent, del);
	}

	@Test
	public void shouldListStockAlerts() {
		// given
		StockAlertEntity ent = new StockAlertEntity();
		ent.setId(3L);
		ent.setProductId(ham.getId());
		ent.setWarehouseId(LONDON);
		ent.setStockLevel(5);
		ent.setThreshold(7);
		List<StockAlertEntity> alerts = Lists.newArrayList();
		alerts.add(ent);
		given(stockAlertRepository.findAll()).willReturn(alerts);
		
		// when
		List<StockAlertDetails> details = warehouseService.getStockAlerts();
		
		// then
		assertNotNull(details);
		assertEquals(1, details.size());
		StockAlertDetails d = details.get(0);
		assertEquals(ham, d.getProduct());
		assertEquals(london, d.getWarehouse());
		assertEquals(5, d.getStockLevel());
		assertEquals(7, d.getThreshold());
	}

}
