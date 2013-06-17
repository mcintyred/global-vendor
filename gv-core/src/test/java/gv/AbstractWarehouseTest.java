package gv;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.reset;
import gv.api.Product;
import gv.api.Warehouse;
import gv.core.service.DistributedWarehouseServiceImpl;
import gv.core.service.WarehouseServiceList;
import gv.core.service.WarehouseServiceLocatorImpl;
import gv.core.service.entity.WarehouseServiceBinding;
import gv.core.service.repository.StockAlertEntityRepository;
import gv.core.service.repository.WarehouseRepository;
import gv.products.api.ProductService;
import gv.warehouse.api.WarehouseService;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.collect.Lists;

@RunWith(MockitoJUnitRunner.class)
abstract public class AbstractWarehouseTest {

	public static final long TOKYO = 21L;

	public static final long PARIS = 17L;

	public static final long LONDON = 3L;

	protected WarehouseServiceLocatorImpl locator;

	@Mock
	protected WarehouseService londonService;

	@Mock
	protected WarehouseService parisService;

	@Mock
	protected WarehouseService tokyoService;

	protected Warehouse london;
	protected Warehouse paris;
	protected Warehouse tokyo;

	protected DistributedWarehouseServiceImpl warehouseService;

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
		List<WarehouseService> services = new ArrayList<WarehouseService>();
		london = new Warehouse(LONDON, "London");
		paris = new Warehouse(PARIS, "Paris");
		tokyo = new Warehouse(TOKYO, "Tokyo");

		WarehouseServiceBinding londonEntity = new WarehouseServiceBinding(
				LONDON, "London", "londonService");
		WarehouseServiceBinding parisEntity = new WarehouseServiceBinding(
				PARIS, "Paris", "parisService");
		WarehouseServiceBinding tokyoEntity = new WarehouseServiceBinding(
				TOKYO, "Tokyo", "tokyoService");

		given(londonService.getName()).willReturn("londonService");
		given(parisService.getName()).willReturn("parisService");
		given(tokyoService.getName()).willReturn("tokyoService");

		services.add(londonService);
		services.add(parisService);
		services.add(tokyoService);

		locator = new WarehouseServiceLocatorImpl();
		locator.setServiceList(new WarehouseServiceList(services));
		locator.setRepository(warehouseRepository);
		locator.getServiceNameMap();
		
		given(warehouseRepository.findOne(LONDON)).willReturn(londonEntity);
		given(warehouseRepository.findOne(PARIS)).willReturn(parisEntity);
		given(warehouseRepository.findOne(TOKYO)).willReturn(tokyoEntity);
		given(warehouseRepository.findAll()).willReturn(
				Lists.newArrayList(londonEntity, parisEntity, tokyoEntity));

		warehouseService = new DistributedWarehouseServiceImpl();
		warehouseService.setWarehouseRepository(warehouseRepository);
		warehouseService.setWarehouseServiceLocator(locator);
		
		warehouseService.setProductService(productService);
		warehouseService.setStockAlertEntityRepository(stockAlertRepository);
		
		ham = new Product(1L, "Ham", "A nice bit of ham");
		cheese = new Product(2L, "Cheese", "A mature cheddar");
		
		given(productService.getProductById(1L)).willReturn(ham);
		given(productService.getProductById(2L)).willReturn(cheese);
		
		reset(londonService, parisService, tokyoService);

	}
}