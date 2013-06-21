package gv;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.reset;
import gv.api.Product;
import gv.api.Warehouse;
import gv.core.service.WarehouseServiceMap;
import gv.core.service.WarehouseServiceLocatorImpl;
import gv.core.service.entity.WarehouseServiceBinding;
import gv.core.service.repository.WarehouseRepository;
import gv.products.api.ProductService;
import gv.stock.service.StockServiceImpl;
import gv.stock.service.StockServiceImplTest;
import gv.stock.service.repository.StockAlertEntityRepository;
import gv.warehouse.api.WarehouseService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.collect.Lists;

@RunWith(MockitoJUnitRunner.class)
abstract public class AbstractWarehouseTest {

	public static final long TOKYO = 21L;

	public static final long LONDON = 3L;

	public static final String LOCAL = "local";
	public static final String REMOTE = "remote";

	protected WarehouseServiceLocatorImpl locator;

	@Mock
	protected WarehouseService localService;

	@Mock
	protected WarehouseService remoteService;

	protected Warehouse london;
	protected Warehouse tokyo;

	protected StockServiceImpl stockService;

	@Mock
	protected WarehouseRepository warehouseRepository;
	
	@Mock
	protected StockAlertEntityRepository stockAlertRepository;
	
	@Mock
	protected ProductService productService;
	
	
	protected Product ham;
	protected Product cheese;

	@Before
	public void setUp() {
		// Create the Warehouse to StockDAO map and inject it into the service
		Map<String, WarehouseService> services = new HashMap<String, WarehouseService>();
		london = new Warehouse(LONDON, "London");
		tokyo = new Warehouse(TOKYO, "Tokyo");

		WarehouseServiceBinding londonEntity = new WarehouseServiceBinding(
				LONDON, "London", LOCAL);
		WarehouseServiceBinding tokyoEntity = new WarehouseServiceBinding(
				TOKYO, "Tokyo", REMOTE);

		services.put(LOCAL, localService);
		services.put(REMOTE, remoteService);

		locator = new WarehouseServiceLocatorImpl();
		locator.setServiceMap(new WarehouseServiceMap(services));
		locator.setRepository(warehouseRepository);
		locator.getServiceNameMap();
		
		given(warehouseRepository.findOne(LONDON)).willReturn(londonEntity);
		given(warehouseRepository.findOne(TOKYO)).willReturn(tokyoEntity);

		given(warehouseRepository.findOneByName(london.getName())).willReturn(londonEntity);
		given(warehouseRepository.findOneByName(tokyo.getName())).willReturn(tokyoEntity);
		
		given(warehouseRepository.findAll()).willReturn(
				Lists.newArrayList(londonEntity, tokyoEntity));

		stockService = new StockServiceImpl();
		stockService.setWarehouseServiceLocator(locator);
		
		stockService.setProductService(productService);
		stockService.setStockAlertEntityRepository(stockAlertRepository);
		
		ham = new Product(1L, "Ham", "A nice bit of ham");
		cheese = new Product(2L, "Cheese", "A mature cheddar");
		
		given(productService.getProductById(1L)).willReturn(ham);
		given(productService.getProductById(2L)).willReturn(cheese);

	}
}
