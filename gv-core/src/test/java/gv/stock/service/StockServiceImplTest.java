package gv.stock.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import gv.AbstractWarehouseTest;
import gv.api.Warehouse;
import gv.stock.api.StockAlert;
import gv.stock.api.StockAlertDetails;
import gv.stock.api.StockChangeRequest;
import gv.stock.api.StockQueryRequest;
import gv.stock.service.entity.StockAlertEntity;

import java.util.List;
import java.util.Set;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.runners.MockitoJUnitRunner;

import com.google.common.collect.Lists;

@RunWith(MockitoJUnitRunner.class)
public class StockServiceImplTest extends AbstractWarehouseTest {
	
	@Test
	public void shouldListWarehouses() {
		// when
		Set<Warehouse> warehouses = stockService.listWarehouses();
		// then
		assertEquals(warehouses.size(), 2);
	}
	
	@Test
	public void shouldGetWarehouseById() {
		// when
		Warehouse found = stockService.getWarehouseById(LONDON);
		// then
		assertEquals(found, london);
		// when
		found = stockService.getWarehouseById(TOKYO);
		// then
		assertEquals(found, tokyo);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void shouldNotGetWarehouseByInvalidId() {
		// when
		stockService.getWarehouseById(13L);
		// then
		// exception expected
	}
	
	@Test
	public void shouldUpdateStockInTheCorrectWarehouse() {
		// given
		Long productId = 127L;
		int stockDelta = 55;
		StockChangeRequest request = new StockChangeRequest(london.getName(), productId, stockDelta);
		given(localService.updateStock(request)).willReturn(stockDelta + 5);
		
		// when
		stockService.updateStock(request);
		
		// then
		verify(localService).updateStock(request);
		verifyZeroInteractions(remoteService);
	}

	@Test
	public void shouldSetStockInTheCorrectWarehouse() {
		// given
		Long productId = 127L;
		int stockLevel = 55;		
		StockChangeRequest request = new StockChangeRequest(london.getName(), productId, stockLevel);
		
		// when
		stockService.setStock(request);
		
		// then
		verify(localService).setStock(request);
		verifyZeroInteractions(remoteService);
	}

	@Test
	public void shouldGetStockFromTheCorrectWarehouse() {
		// given
		Long productId = 127L;
		int stockLevel = 55;	
		StockQueryRequest request = new StockQueryRequest(london.getName(), productId);
		given(localService.getStock(request)).willReturn(stockLevel);
		// when
		int foundStockLevel = stockService.getStockInWarehouse(request);
		
		// then
		verify(localService).getStock(request);
		verifyZeroInteractions(remoteService);
		assertEquals(stockLevel, foundStockLevel);
	}
	
	@Test
	public void shouldHandleDepletedStockAlert() {
		// given
		StockAlert alert = new StockAlert(ham.getId(), london.getName(), 5, 10);
		
		ArgumentCaptor<StockAlertEntity> captor = ArgumentCaptor.forClass(StockAlertEntity.class);
		
		// when
		stockService.handleStockAlert(alert);
		
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
		StockAlert alert = new StockAlert(ham.getId(), london.getName(), 15, 10);
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
		stockService.handleStockAlert(alert);
		
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
		List<StockAlertDetails> details = stockService.getStockAlerts();
		
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
