package gv.core.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.reset;
import gv.api.Warehouse;
import gv.core.service.entity.WarehouseServiceBinding;
import gv.core.service.repository.WarehouseRepository;
import gv.warehouse.api.WarehouseService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class WarehouseServiceLocatorImplTest {
	
	public static final long TOKYO = 21L;

	public static final long PARIS = 17L;

	public static final long LONDON = 3L;
	
	public static final String LOCAL = "local";
	public static final String REMOTE = "remote";

	private WarehouseServiceLocatorImpl locator;
	
	@Mock
	private WarehouseService localService;
	
	@Mock
	private WarehouseService remoteService;
	
	private Warehouse london;
	private Warehouse paris;
	private Warehouse tokyo;
	
	@Mock
	WarehouseRepository warehouseRepository;
	
	
	@Before
	public void setUp() {
		// Create the Warehouse to StockDAO map and inject it into the service
		Map<String, WarehouseService> services = new HashMap<String, WarehouseService>();
		london = new Warehouse(LONDON, "London");
		paris = new Warehouse(PARIS, "Paris");
		tokyo = new Warehouse(TOKYO, "Tokyo");
		
		WarehouseServiceBinding londonEntity = new WarehouseServiceBinding(LONDON, "London", LOCAL);
		WarehouseServiceBinding parisEntity = new WarehouseServiceBinding(PARIS, "Paris", LOCAL);
		WarehouseServiceBinding tokyoEntity = new WarehouseServiceBinding(TOKYO, "Tokyo", REMOTE);
		
		services.put(LOCAL, localService);
		services.put(REMOTE, remoteService);
		
		given(warehouseRepository.findOneByName(london.getName())).willReturn(londonEntity);
		given(warehouseRepository.findOneByName(paris.getName())).willReturn(parisEntity);
		given(warehouseRepository.findOneByName(tokyo.getName())).willReturn(tokyoEntity);
		
		locator = new WarehouseServiceLocatorImpl();
		locator.setServiceMap(new WarehouseServiceMap(services));
		locator.setRepository(warehouseRepository);
		
		locator.getServiceNameMap();
		
		given(warehouseRepository.findOne(LONDON)).willReturn(londonEntity);
		given(warehouseRepository.findOne(PARIS)).willReturn(parisEntity);
		given(warehouseRepository.findOne(TOKYO)).willReturn(tokyoEntity);
	}
	
	@Test
	public void shouldLocateRepositoryById() {
		WarehouseService repo = locator.locateService(london.getId());
		assertEquals(localService, repo);
		repo = locator.locateService(tokyo.getId());
		assertEquals(remoteService, repo);
	}
	
	@Test
	public void shouldLocateRepositoryByName() {
		WarehouseService repo = locator.locateService(london.getName());
		assertEquals(localService, repo);
		repo = locator.locateService(tokyo.getName());
		assertEquals(remoteService, repo);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void shouldThrowExceptionForUnknownWarehouse() {
		// given
		Warehouse unknown = new Warehouse(999L, "Unknown");
		// when
		WarehouseService repo = locator.locateService(unknown.getId());
		// then
		// exception expected
		assertFalse("Exception expected", true);
		
	}

}
