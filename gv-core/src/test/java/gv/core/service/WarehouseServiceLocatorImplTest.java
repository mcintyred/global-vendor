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
import java.util.List;

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

	private WarehouseServiceLocatorImpl locator;
	
	@Mock
	private WarehouseService londonService;
	
	@Mock
	private WarehouseService parisService;
	
	@Mock
	private WarehouseService tokyoService;
	
	private Warehouse london;
	private Warehouse paris;
	private Warehouse tokyo;
	
	@Mock
	WarehouseRepository warehouseRepository;
	
	
	@Before
	public void setUp() {
		// Create the Warehouse to StockDAO map and inject it into the service
		List<WarehouseService> services = new ArrayList<WarehouseService>();
		london = new Warehouse(LONDON, "London");
		paris = new Warehouse(PARIS, "Paris");
		tokyo = new Warehouse(TOKYO, "Tokyo");
		
		WarehouseServiceBinding londonEntity = new WarehouseServiceBinding(LONDON, "London", "londonService");
		WarehouseServiceBinding parisEntity = new WarehouseServiceBinding(PARIS, "Paris", "parisService");
		WarehouseServiceBinding tokyoEntity = new WarehouseServiceBinding(TOKYO, "Tokyo", "tokyoService");
		
		services.add(londonService);
		services.add(parisService);
		services.add(tokyoService);
		
		given(londonService.getName()).willReturn("londonService");
		given(parisService.getName()).willReturn("parisService");
		given(tokyoService.getName()).willReturn("tokyoService");
		
		locator = new WarehouseServiceLocatorImpl();
		locator.setServiceList(new WarehouseServiceList(services));
		locator.setRepository(warehouseRepository);
		
		locator.getServiceNameMap();
		
		given(warehouseRepository.findOne(LONDON)).willReturn(londonEntity);
		given(warehouseRepository.findOne(PARIS)).willReturn(parisEntity);
		given(warehouseRepository.findOne(TOKYO)).willReturn(tokyoEntity);
		reset(londonService, parisService, tokyoService);
	}
	
	@Test
	public void shouldLocateRepository() {
		WarehouseService repo = locator.locateService(london.getId());
		assertEquals(londonService, repo);
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
